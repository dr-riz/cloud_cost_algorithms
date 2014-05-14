import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class CostModel {
	final static DecimalFormat df = new DecimalFormat("#.###");

	// Time converter globals
	final static public double conver_to_hour = 60 * 60;
	final static public double conver_to_month = conver_to_hour * 24 * 30;
	final static public double month_hours = 24.0 /* hours */* 30.0 /*
																 * days in a
																 * month
																 */;
	final static public double overloaded = 0.80;

	/**************
	 * Costs
	 */
	// Misc cost
	final static public double constant = 0;

	/* network cost */
	// no $cost for replication
	final static public double $cost_replication_communication = 0;

	// say 10s
	final static public double time_cost_replication_communication = 10;

	final static public double ebs_io_cost = 0.01; // $0.01 per 100,000 I/O
	// request to ebs

	/* storage costs */
	final static public double hourly_partition_cost = Partition.price
			/ month_hours;
	final static public double hourly_snapshop_cost = 0.125 / month_hours;

	/* network accesses */
	// static private HashMap<Integer, Long> m_accesses = new HashMap<Integer,
	// Long>(); // map: wl_type -> count
	static private int[][] m_accesses = new int[LRPredictor.wl_a_b_c][5]; // VM.typesOfVM()

	public static double calculateCost(Config cnf) {
		double cost = -1;
		// cost = calculateCostUsingQNM(cnf);
		cost = calculateCostUsingLR(cnf);
		return cost;
	}

	/**************************************
	 * Cost model using single class queueing networks
	 **************************************/

	public static double calculateCostUsingQNM(Config cnf) {
		boolean verbose = false;

		double vm_cost = 0.0;
		double storage_cost = 0.0;
		double storage_size = 0.0;
		double penalty_cost = 0.0;
		double throughput = 0.0;
		double utilization = 0.0;
		double responseTime = 0.0;

		boolean openModel = TransactionClass.transWL();

		cnf.unstable(false);

		HashMap<Integer, VM> mapvm = cnf.getVMmap();
		Set vms = mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();

			// multi-class workload

			boolean delayCenter = false; // its a processing center

			QueuingModel mc = null;
			if (openModel) {
				mc = new MultiClass(vm, cnf, delayCenter, verbose);
			} else {
				mc = new MultiClassClosed(vm, cnf, delayCenter, verbose);
			}

			vm.utilization(mc.utilization());
			vm.responseTime(mc.responseTime());
			vm.throughput(mc.throughput());
			vm.busiestTC(mc.highestUtilTC());

			throughput += mc.throughput();
			utilization += mc.utilization();

			// responseTime += mc.responseTime() * mc.throughput();
			responseTime += mc.responseTime();

			if (mc.utilization() > overloaded) {
				System.out.print("vm (id=" + vm.id() + ", rank="
						+ vm.costRank() + ", rate=" + vm.$cost()
						+ ") has utilization(" + df.format(mc.utilization())
						+ ") > " + overloaded + " with " + vm.numOfClasses()
						+ " classes --- SYSTEM UNSTABLE!\n");
				cnf.unstable(true);
				cnf.cost(-1);
				cnf.responseTime(-1);
				continue;
			}

			/*
			 * not including replication time at this stage "if
			 * (vm.getNumOfReplica() > 0) { responeTime +=
			 * time_cost_replication_communication; } "
			 */

			System.out.print("mlc: ResponseTime of vm (id " + vm.id()
					+ ", type " + vm.type() + ",rank " + vm.costRank()
					+ ", util=" + df.format(mc.utilization()) + ", thruput= "
					+ df.format(mc.throughput()) + ") with" + " TC(");

			Vector<Integer> tcIDs = vm.getAlltc();
			Iterator<Integer> tcid_iter = tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				System.out.print(tid + ",");
			}

			System.out.println(") = " + df.format(mc.responseTime()) + "s, "
					+ "queueLength=" + df.format(mc.queueLength()) + "t");

			// VM Cost
			vm_cost += vm.$cost();

			System.out.println("ideal service_rate (E) = "
					+ df.format(mc.serviceRate() * 60) + "/h");

			// use if calculating storage cost per vm
			storage_cost += calPartitionCostPerVM(vm, cnf);

			// storage sizes: if calculating cost across aggregagated volumes
			// storage_size += calPartitionSizesForVM(vm, cnf);

			// penalties
			penalty_cost += calPenaltyCost(tcIDs, cnf);

		}

		/*
		 * not including communication cost at this stage cost +=
		 * calCommunicationCost(cnf);
		 */

		// if calculating cost across aggregagated volumes
		// storage_cost = calAggregatePartitionCost(storage_size);
		double config_cost = vm_cost + storage_cost + penalty_cost + constant; // add
		// constant;
		if (cnf.unstable()) {
			return -1;
		} else {
			System.out.println("$cost/h=$" + df.format(config_cost) + "/h");
			// System.out.println("$cost/transaction=$" + df.format(cost) +
			// "/h");

			int numberOfSC = mapvm.size();
			utilization = utilization / ((double) numberOfSC);
			responseTime = responseTime / ((double) numberOfSC);

			cnf.cost(config_cost);
			cnf.vm_cost(vm_cost);
			cnf.storage_cost(storage_cost);
			cnf.penalty_cost(penalty_cost);
			cnf.constant_cost(constant);
			cnf.utilization(utilization);
			cnf.throughput(throughput);
			cnf.responseTime(responseTime);
		}
		return config_cost;
	}

	/**************************************
	 * Cost model using LR
	 **************************************/

	public static double calculateCostUsingLR(Config cnf) {
		boolean verbose = false;

		double vm_cost = 0.0;
		double storage_cost = 0.0;
		double network_cost = 0.0;
		double penalty_cost = 0.0;
		int numOfDP = 0;
		double storage_size = 0;

		HashMap<Integer, VM> mapvm = cnf.getVMmap();
		Set vms = mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			numOfDP += vm.sizeOfPartitionTable();

			// throughput not available
			vm.throughput(-1);

			int aggregateWlType = 0;
			int heaviestWL = 0;
			int heaviestWLid = -1;
			double mpl_used = vm.mpl();

			Vector<Integer> tcIDs = vm.getAlltc();
			Iterator<Integer> tcid_iter = tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				TransactionClass tc = cnf.gettc(tid);
				int wl = tc.wl_type();
				aggregateWlType += wl;
				if (LRPredictor.weight(wl) > heaviestWL) {
					heaviestWL = wl;
					heaviestWLid = tc.id();
				}
				// System.out.println("TrasactionClass tcid=" + tc.id() +
				// ", wl_type=" + tc.wl_type());
			}

			System.out.println("aggregateWlType="
					+ LRPredictor.wl_type_str(aggregateWlType));
			System.out.println("heaviestWL="
					+ LRPredictor.wl_type_str(heaviestWL));
			System.out.println("heaviestWLid=" + heaviestWLid);
			System.out.println("mpl_used=" + mpl_used);

			tcIDs = vm.getAlltc();
			tcid_iter = tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				TransactionClass tc = cnf.gettc(tid);

				double prediction = 0; // default assuming request type not
				// present in the workload
				double[] wltype = LRPredictor.wl2wl_type(tc.wl_type());
				if ((wltype[SpecificModel.Q1] != 0)
						&& (tc.wl_type() == LRPredictor.wl_a)) {
					prediction = LRPredictor.getInstance().prediction(
							vm.type(), aggregateWlType, SpecificModel.Q1,
							mpl_used);
					tc.actualRespTime(prediction);
				}
				if ((wltype[SpecificModel.trade_update] != 0)
						&& (tc.wl_type() == LRPredictor.wl_b)) {
					prediction = LRPredictor.getInstance().prediction(
							vm.type(), aggregateWlType,
							SpecificModel.trade_update, mpl_used);
					tc.actualTPS(prediction);
				}
				if ((wltype[SpecificModel.payment] != 0)
						&& (tc.wl_type() == LRPredictor.wl_c)) {
					prediction = LRPredictor.getInstance().prediction(
							vm.type(), aggregateWlType, SpecificModel.payment,
							mpl_used);
					tc.actualTPS(prediction);
				}

			}

			vm.busiestTC(heaviestWLid);

			/*
			 * not including replication time at this stage "if
			 * (vm.getNumOfReplica() > 0) { responeTime +=
			 * time_cost_replication_communication; } "
			 */

			// VM Cost
			vm_cost += vm.$cost();

			// // calculates cost for provisioned storage
			// storage_cost += calPartitionCostPerVM(vm, cnf);

			// storage sizes: if calculating provisioning cost across
			// aggregagated volumes
			storage_size += calPartitionSizesForVM(vm, cnf);

			// network cost
			network_cost += calNetworkCostPerVM(vm, aggregateWlType);

			// penalties
			penalty_cost += calPenaltyCost(tcIDs, cnf);

			// calculate aggregate degrees of SLA met
			vm.degrees(caldegreesOfSLASatisfied(tcIDs, cnf));
		}

		// calculates cost for snapshot storage
		double snapshot_cost = storage_size * hourly_snapshop_cost;
		BigDecimal snapshot_bd = new BigDecimal(snapshot_cost).setScale(2,
				RoundingMode.UP);
		snapshot_cost = snapshot_bd.doubleValue();
		System.out.println("snapshot_cost=" + snapshot_cost);

		storage_cost += snapshot_cost;

		double provisioned_cost = storage_size * hourly_partition_cost;
		BigDecimal provisioned_bd = new BigDecimal(provisioned_cost).setScale(
				2, RoundingMode.UP);
		provisioned_cost = provisioned_bd.doubleValue();
		System.out.println("storage_size=" + storage_size);
		System.out.println("provisioned_cost=" + provisioned_cost);

		storage_cost += provisioned_cost;

		/*
		 * not including communication cost at this stage cost +=
		 * calCommunicationCost(cnf);
		 */

		// if calculating cost across aggregagated volumes
		// storage_cost = calAggregatePartitionCost(storage_size);

		double config_cost = vm_cost + storage_cost + network_cost
				+ penalty_cost + constant;
		if (cnf.unstable()) {
			return -1;
		} else {
			System.out.println("$cost/h=$" + df.format(config_cost) + "/h");

			cnf.numOfDP(numOfDP);
			cnf.cost(config_cost);
			cnf.vm_cost(vm_cost);
			cnf.storage_cost(storage_cost);
			cnf.network_cost(network_cost);
			cnf.penalty_cost(penalty_cost);
			cnf.constant_cost(constant);

			// not available anymore
			cnf.utilization(-1);
			cnf.throughput(-1);
			cnf.responseTime(-1);
		}
		return config_cost;
	}

	/**************************************
	 * helper methods for calculating cost
	 **************************************/

	public static double calVMCost(double responseTime, VM vm) {
		long vm_usage_time = roundup(responseTime / conver_to_hour);

		double cost = vm_usage_time * vm.price();
		return cost;
	}

	public static double calPartitionSizesForVM(VM vm, Config conf) {
		HashMap<Integer, Integer> mapdp = vm.getAlldp();
		double aggregated_partition_size = 0;
		if (mapdp != null && mapdp.size() != 0) {
			Set dps = mapdp.entrySet();
			Iterator dp_iter = dps.iterator();

			// copy all ids of all partitions
			while (dp_iter.hasNext()) {
				Map.Entry dpentry = (Map.Entry) dp_iter.next();
				int dp_id = (Integer) dpentry.getKey();
				Partition dp = conf.getdp(dp_id);
				aggregated_partition_size += dp.size();
			}
		}
		aggregated_partition_size = roundup(aggregated_partition_size);
		return aggregated_partition_size;
	}

	public static double calAggregatePartitionCost(
			double aggregated_partition_size) {
		aggregated_partition_size = roundup(aggregated_partition_size);
		double raw_cost = aggregated_partition_size * hourly_partition_cost;
		BigDecimal bd = new BigDecimal(raw_cost).setScale(2, RoundingMode.UP);
		double cent_cost = bd.doubleValue();
		return cent_cost; // calculating cost over aggregated partition size
	}

	public static double calPartitionCostPerVM(VM vm, Config conf) {
		HashMap<Integer, Integer> mapdp = vm.getAlldp();
		double cost = 0;
		if (mapdp != null && mapdp.size() != 0) {
			Set dps = mapdp.entrySet();
			Iterator dp_iter = dps.iterator();

			double storage = 0;
			// provisioned storage cost
			while (dp_iter.hasNext()) {
				Map.Entry dpentry = (Map.Entry) dp_iter.next();
				int dp_id = (Integer) dpentry.getKey();
				Partition dp = conf.getdp(dp_id);
				storage += dp.size();
			}

			cost += calPartitionCost(storage);
		}
		return cost;
	}

	public static double calNetworkCostPerVM(VM vm, int wl_type) {
		double cost = 0;
		
		int nw_accesses = accesses(wl_type, vm.type());
		cost = (double)(nw_accesses)/100000 * ebs_io_cost;

		return cost;
	}

	public static double calPenaltyCost(Vector<Integer> tcIDs, Config conf) {
		double cost = 0;
		Iterator<Integer> tcid_iter = tcIDs.iterator();
		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			TransactionClass tc = conf.gettc(tid);
			double respPenalty = binaryPenaltyResponseTime(tc);
			if (respPenalty > 0) {
				System.out.println("ResponseTime SLA violated for TC="
						+ tc.id() + ", desiredRespTime=" + tc.desiredRespTime()
						+ ", actualRespTime=" + tc.actualRespTime()
						+ ", penalty=$" + df.format(respPenalty));
			}
			double tpsPenalty = binaryPenaltyThroughput(tc);
			if (tpsPenalty > 0) {
				System.out.println("Throughput SLA violated for TC=" + tc.id()
						+ ", desiredThroughput=" + tc.desiredTPS()
						+ ", actualThrooughput=" + tc.actualTPS()
						+ ", penalty=$" + df.format(tpsPenalty));
			}
			cost += respPenalty + tpsPenalty;
		}
		return cost;
	}

	public static double binaryPenaltyResponseTime(TransactionClass tc) {
		double penalty = 0;
		if (tc.basepenalty() != -1) {
			double actualResTime = tc.actualRespTime();
			double desiredResTime = tc.desiredRespTime();
			double diff = actualResTime - desiredResTime;
			if (actualResTime > desiredResTime) {
				// commenting below gives us penalty per unit time
				// double tps = tc.getArrivalRate(); // throughput
				// double tph = tps * conver_to_hour;
				// penalty = tc.basepenalty() * tph;
				penalty = tc.basepenalty();
			}
		}
		return penalty;
	}

	public static double binaryPenaltyThroughput(TransactionClass tc) {
		double penalty = 0;
		if (tc.basepenalty() != -1) {
			double actualTPS = tc.actualTPS();
			double desiredTPS = tc.desiredTPS();
			double diff = actualTPS - desiredTPS;
			if (actualTPS < desiredTPS) {
				// commenting below gives us penalty per unit time
				// double tps = tc.getArrivalRate(); // throughput
				// double tph = tps * conver_to_hour;
				// penalty = tc.basepenalty() * tph;
				penalty = tc.basepenalty();
			}
		}
		return penalty;
	}

	public static double caldegreesOfSLASatisfied(Vector<Integer> tcIDs,
			Config conf) {
		double aggregate_degree = 0;
		Iterator<Integer> tcid_iter = tcIDs.iterator();
		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			TransactionClass tc = conf.gettc(tid);
			double degree = binaryDegree(tc);
			if (degree > 0) {
				System.out.println("degree of SLA TC(" + tc.id() + ") met="
						+ df.format(degree));
			}
			tc.degreeOfSLAmet(degree);
			aggregate_degree += degree;
		}
		return aggregate_degree;
	}

	public static double binaryDegree(TransactionClass tc) {
		double degree = 1; // assume SLA met by default
		if (tc.basepenalty() != -1) {
			double actualResTime = tc.actualRespTime();
			double desiredResTime = tc.desiredRespTime();
			double diff = actualResTime - desiredResTime;
			if (diff > 0) {
				degree = 0;
			}
		}
		return degree;
	}

	public static double calPartitionCost(double storage_used) {
		long roundup_size = roundup(storage_used);
		// System.out.println("roundup_size=" + roundup_size);
		double cent_cost, raw_cost = roundup_size * hourly_partition_cost;
		// System.out.println("raw_cost=" + raw_cost);
		BigDecimal bd = new BigDecimal(raw_cost).setScale(2, RoundingMode.UP);
		cent_cost = bd.doubleValue();
		// System.out.println("cent_cost=" + cent_cost);
		return cent_cost;
	}

	static void accesses(int wl_type, int vm_type, int count) {
		m_accesses[wl_type][vm_type] = count;
	}

	static void unitAccesses(int wl_type, int vm_type, int count) {
		switch (wl_type) {
		case (LRPredictor.wl_a):
		case (LRPredictor.wl_b):
		case (LRPredictor.wl_c): {
			m_accesses[wl_type][vm_type] = count;
			break;
		}
		default: {
			System.out.println("Unknown unit workload=" + wl_type);
		}
		}
	}

	static int unitAccesses(int wl_type, int vm_type) {
		int accesses = 0;
		switch (wl_type) {
		case (LRPredictor.wl_a):
		case (LRPredictor.wl_b):
		case (LRPredictor.wl_c): {
			accesses = m_accesses[wl_type][vm_type];
			break;
		}
		default: {
			System.out.println("Unknown unit workload=" + wl_type);
		}
		}
		return accesses;
	}

	static int accesses(int wl_type, int vm_type) {
		int accesses = 0;
		switch (wl_type) {
		case (LRPredictor.wl_a):
		case (LRPredictor.wl_b):
		case (LRPredictor.wl_c): {
			accesses = unitAccesses(wl_type, vm_type);
			break;
		}
		case (LRPredictor.wl_a_b): {
			int a_accesses = unitAccesses(LRPredictor.wl_a, vm_type);
			int b_accesses = unitAccesses(LRPredictor.wl_b, vm_type);

			accesses = (a_accesses + b_accesses) / 2;
			break;
		}
		case (LRPredictor.wl_a_c): {
			int a_accesses = unitAccesses(LRPredictor.wl_a, vm_type);
			int c_accesses = unitAccesses(LRPredictor.wl_c, vm_type);

			accesses = (a_accesses + c_accesses) / 2;
			break;
		}
		case (LRPredictor.wl_b_c): {
			int b_accesses = unitAccesses(LRPredictor.wl_b, vm_type);
			int c_accesses = unitAccesses(LRPredictor.wl_c, vm_type);

			accesses = (b_accesses + c_accesses) / 2;
			break;
		}
		case (LRPredictor.wl_a_b_c): {
			int a_accesses = unitAccesses(LRPredictor.wl_a, vm_type);
			int b_accesses = unitAccesses(LRPredictor.wl_b, vm_type);
			int c_accesses = unitAccesses(LRPredictor.wl_c, vm_type);

			accesses = (a_accesses + b_accesses + c_accesses) / 3;
			break;
		}
		default:
			System.out.println("Unknown unit wl type =" + wl_type
					+ ", or vm type=" + vm_type);
		}
		return accesses;
	}

	/**************************************
	 * helper methods e.g. printing etc.
	 **************************************/

	// Round up to next integer
	public static long roundup(double num) {
		if (num == 0.0) {
			return 0;
		} else {
			double res = Math.ceil(num);
			return (long) res;
		}
	}

}
