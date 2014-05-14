import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Config {
	private HashMap<Integer, TransactionClass> m_maptc; // tcid to TC mapping
	private HashMap<Integer, Partition> m_mapdp; // dpid to DP mapping

	private HashMap<Integer, VM> m_mapvm; // vid to VM mapping

	private int m_num_of_dp;
	private double m_$cost;
	private double m_vm_cost;
	private double m_storage_cost;
	private double m_network_cost;
	private double m_penalty_cost;
	private double m_constant_cost;
	private long m_cores;
	private long m_iteration;
	private double m_T_;
	private double m_R;
	private double m_X;
	private double m_U;
	private boolean m_unstable;
	private boolean m_minimum = false;
	private double m_vmcost;

	private int m_cheapest_vid;
	private double m_cheapest_vm_cost;
	private int m_action_generated;
	// meta data
	private int m_id;
	private static int m_id_counter;

	protected static DecimalFormat df = new DecimalFormat("#.######");
	private static boolean DEBUG = false;

	/**************************************
	 * constructors
	 **************************************/

	public static Config createConfig() {
		Config c = new Config();
		c.id(m_id_counter);
		m_id_counter++;
		return c;
	}

	private Config() {
		// m_vm = new Vector<VM>();
		m_mapvm = new HashMap<Integer, VM>();
		init();
	}

	private Config(HashMap<Integer, VM> vms) {
		m_mapvm = vms;
		init();
	}

	private void init() {
		// m_tc = new Vector<TransactionClass>();
		m_maptc = new HashMap<Integer, TransactionClass>();
		m_mapdp = new HashMap<Integer, Partition>();

		m_num_of_dp = 0;
		m_$cost = -1;
		m_vm_cost = -1;
		m_storage_cost = -1;
		m_network_cost = -1;
		m_penalty_cost = -1;
		m_vmcost = 0;
		m_cores = 0;
		m_iteration = -1;
		m_T_ = -1;
		m_R = -1;
		m_X = -1;
		m_U = -1;
		m_cheapest_vid = -1;
		m_cheapest_vm_cost = -1;
		m_action_generated = -1;
	}

	/**************************************
	 * accessor methods
	 **************************************/

	public void cost(double _cost) {
		m_$cost = _cost;
	}

	public double cost() {
		return m_$cost;
	}

	public void vm_cost(double _cost) {
		m_vm_cost = _cost;
	}

	public double vm_cost() {
		return m_vm_cost;
	}

	public void storage_cost(double _cost) {
		m_storage_cost = _cost;
	}

	public double storage_cost() {
		return m_storage_cost;
	}

	public void network_cost(double _cost) {
		m_network_cost = _cost;
	}

	public double network_cost() {
		return m_network_cost;
	}

	public void penalty_cost(double _cost) {
		m_penalty_cost = _cost;
	}

	public double penalty_cost() {
		return m_penalty_cost;
	}

	public void constant_cost(double _cost) {
		m_constant_cost = _cost;
	}

	public double constant_cost() {
		return m_constant_cost;
	}

	public void addtc(TransactionClass tc) {
		// m_tc.add(tc);
		m_maptc.put(tc.id(), tc);
	}

	public TransactionClass gettc(int id) {
		return (TransactionClass) m_maptc.get(id);
	}

	public int numOfTC() {
		return m_maptc.size();
	}

	public void adddp(Partition dp) {
		// m_tc.add(tc);
		m_mapdp.put(dp.id(), dp);
	}

	public Partition getdp(int id) {
		return (Partition) m_mapdp.get(id);
	}

	public void addvm(VM vm) {
		m_cores += vm.cores();
		m_mapvm.put(vm.id(), vm);
		m_vmcost += vm.$cost();
		if ((m_cheapest_vid == -1) || vm.$cost() < m_cheapest_vm_cost) {
			m_cheapest_vid = vm.id();
			m_cheapest_vm_cost = vm.$cost();
		}
	}

	public void removevm(VM vm) {
		m_cores -= vm.cores();
		m_mapvm.remove(vm.id());
		m_vmcost -= vm.$cost();
		/*
		 * if ((m_cheapest_vid == -1) || vm.$cost() < m_cheapest_vm_cost) {
		 * m_cheapest_vid = vm.id(); m_cheapest_vm_cost = vm.$cost(); }
		 */
	}

	public HashMap<Integer, VM> getVMmap() {
		return m_mapvm;
	}

	public VM getVM(int vid) {
		return m_mapvm.get(vid);
	}

	public int numOfVMs() {
		return m_mapvm.size();
	}

	public long getNumberOfCores() {
		return m_cores;
	}

	public int numOfDP() {
		return m_num_of_dp;
	}

	public void numOfDP(int dp) {
		m_num_of_dp = dp;
	}

	private void id(int _id) {
		m_id = _id;
	}

	int id() {
		return m_id;
	}

	void iteration(long _iteration) {
		m_iteration = _iteration;
	}

	long iteration() {
		return m_iteration;
	}

	void action(int action) {
		m_action_generated = action;
	}

	int action() {
		return m_action_generated;
	}

	void roundedUPExeT(double _T) {
		m_T_ = _T;
	}

	double roundedUPExeT() {
		return m_T_;
	}

	void responseTime(double _T) {
		m_R = _T;
	}

	double responseTime() {
		return m_R;
	}

	void throughput(double _T) {
		m_X = _T;
	}

	double throughput() {
		return m_X;
	}

	void utilization(double _T) {
		m_U = _T;
	}

	double utilization() {
		return m_U;
	}

	public static void resetCounter() {
		m_id_counter = 0;
	}

	public static int counter() {
		return m_id_counter;
	}

	public boolean unstable() {
		return m_unstable;
	}

	public void unstable(boolean b) {
		m_unstable = b;
	}

	public boolean minimumFound() {
		return m_minimum;
	}

	public void minimumFound(boolean b) {
		m_minimum = b;
	}

	public double getVMCost() {
		return m_vmcost;
	}

	/**************************************
	 * logic methods
	 **************************************/

	public void resetVMs() {
		m_$cost = -1;
		m_vm_cost = -1;
		m_storage_cost = -1;
		m_network_cost = -1;
		m_penalty_cost = -1;
		m_vmcost = 0;
		m_cores = 0;
		m_T_ = -1;
		m_R = -1;
		m_X = -1;
		m_U = -1;
		m_cheapest_vid = -1;
		m_cheapest_vm_cost = -1;

		m_mapvm = new HashMap<Integer, VM>();
	}

	public VM getCheapestVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM cheapestVM = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			cheapestVM = (VM) vmentry.getValue();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.costRank() < cheapestVM.costRank()) {
				cheapestVM = vm;
			}
		}

		return cheapestVM;
	}

	public VM getCostiestVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM pricyVM = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			pricyVM = (VM) vmentry.getValue();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.costRank() > pricyVM.costRank()) {
				pricyVM = vm;
			}
		}

		return pricyVM;
	}

	public VM getMostUtilizedVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM overloaded = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			overloaded = (VM) vmentry.getValue();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.utilization() > overloaded.utilization()) {
				overloaded = vm;
			}
		}

		return overloaded;
	}

	public VM getLeastUtilizedVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM underloaded = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			underloaded = (VM) vmentry.getValue();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.utilization() < underloaded.utilization()) {
				underloaded = vm;
			}
		}

		return underloaded;
	}

	public VM getSmallestCostRank_VM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM smallest_cost_rank = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			smallest_cost_rank = (VM) vmentry.getValue();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.costRank() < smallest_cost_rank.costRank()) {
				smallest_cost_rank = vm;
			}
		}

		return smallest_cost_rank;
	}

	public VM getHighgestCostRank_VM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM highest_cost_rank = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			highest_cost_rank = (VM) vmentry.getValue();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.costRank() > highest_cost_rank.costRank()) {
				highest_cost_rank = vm;
			}
		}
		return highest_cost_rank;
	}

	public VM getLowestUtilityVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}
		VM candidate = null;
		double candidate_usefulness = 0;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			candidate = (VM) vmentry.getValue();
			candidate_usefulness = candidate.utility();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();

			VM vm = (VM) vmentry.getValue();
			if (vm.utility() < candidate.utility()) {
				candidate = vm;
			}
		}
		return candidate;
	}

	public VM getHighestUtilityVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			System.out.println("no VMs in the config");
			return null;
		}
		VM candidate = null;

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			candidate = (VM) vmentry.getValue();
			candidate.utility();
		}

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			VM vm = (VM) vmentry.getValue();

			if (vm.utility() > candidate.utility()) {
				candidate = vm;
			}
		}

		return candidate;
	}

	public VM getBusietVM() {
		int numOfVMS = m_mapvm.size();

		if (numOfVMS == 0) {
			return null;
		}

		VM busiestVM = null;
		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		// get 1st entry
		if (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			busiestVM = (VM) vmentry.getValue();
		}

		vm_iter = vms.iterator();
		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			VM vm = (VM) vmentry.getValue();
			if (vm.numOfClasses() > 1) {
				busiestVM = vm;
				break;
			}
		}

		if (busiestVM.numOfClasses() == 1) {
			// there is no vm that has more than one class
			return null;
		} else {

			vm_iter = vms.iterator();
			while (vm_iter.hasNext()) {
				// get vm
				Map.Entry vmentry = (Map.Entry) vm_iter.next();
				VM vm = (VM) vmentry.getValue();

				if (vm.numOfClasses() == 1) {
					continue;
				}

				if (vm.busy() > busiestVM.busy()) {
					busiestVM = vm;
				}
			}
		}
		return busiestVM;
	}

	/**************************************
	 * config generation (most interesting bit)
	 **************************************/

	public static Config generateConfig(double arrivalrate, Partition dp,
			int vm_type, TransactionClass tc) {
		// create required configuration
		System.out.println("generateConfig");

		Config conf = Config.createConfig();

		// create copy and TransactionClass
		// but leave original untouched
		TransactionClass c1 = TransactionClass.copy(tc);

		VM vm1 = VM.createVM(vm_type);
		c1.addOnlyVM(vm1); // assign vm1 to class c1
		conf.addtc(c1); // add class c1 to config
		if (dp != null)
			conf.adddp(dp);
		conf.addvm(vm1); // add vm1 to config

		return conf;
	}

	public static Config generateConfig(Vector<TransactionClass> tcs,
			Vector<Partition> dps, int vm_type) {
		// create required configuration
		System.out.println("generateConfig");

		Config conf = Config.createConfig();

		VM vm1 = VM.createVM(vm_type);
		conf.addvm(vm1); // add vm1 to config

		// get an Iterator object for Vector using iterator() method.
		Iterator tc_itr = tcs.iterator();

		// use hasNext() and next() methods of Iterator to iterate through the
		// elements
		while (tc_itr.hasNext()) {
			TransactionClass tc = (TransactionClass) tc_itr.next();
			tc.addOnlyVM(vm1);
			conf.addtc(tc); // add class tc to config
		}

		if (dps != null) {
			// get an Iterator object for Vector using iterator() method.
			Iterator dp_itr = dps.iterator();

			// use hasNext() and next() methods of Iterator to iterate through
			// the
			// elements
			while (dp_itr.hasNext()) {
				Partition dp = (Partition) dp_itr.next();
				conf.adddp(dp); // add class tc to config
			}
		}
		return conf;
	}

	public static Config generateConfig(Config _conf, int vm_type) {
		// create required configuration
		System.out.println("generateConfig");

		Config conf = Config.createConfig();

		VM vm1 = VM.createVM(vm_type);

		Set tcs = _conf.m_maptc.entrySet();
		Iterator tc_iter = tcs.iterator();

		// copy all ids of all transaction classes and vms associated with them
		while (tc_iter.hasNext()) {
			Map.Entry tcentry = (Map.Entry) tc_iter.next();
			TransactionClass oc = (TransactionClass) tcentry.getValue();
			TransactionClass tc = null;
			tc.copy(oc); // <-------- THIS IS A BUG SINCE TC IS NULL
			tc.addOnlyVM(vm1);
			conf.addtc(tc); // add class tc to config
		}

		Set dps = _conf.m_mapdp.entrySet();
		Iterator dp_iter = tcs.iterator();

		// copy all ids of all partitions
		while (dp_iter.hasNext()) {
			Map.Entry dpentry = (Map.Entry) dp_iter.next();
			Partition op = (Partition) dpentry.getValue();
			conf.adddp(op); // add class tc to config
		}

		conf.addvm(vm1); // add vm1 to config
		return conf;
	}

	public static Config generateConfig(Config _conf, VM oldvm, int vm_type) {
		// create required configuration
		System.out.println("generateConfig");

		Config conf = Config.createConfig();
		conf.copy(_conf);
		conf.resetVMs();

		// _conf.print();
		TransactionClass c1 = conf.gettc(0);
		c1.resetVMs();

		VM vm1 = VM.createVM(vm_type, oldvm);
		c1.addOnlyVM(vm1); // assign vm1 to class c1
		conf.addvm(vm1); // add vm1 to config
		return conf;
	}

	public static Config generateConfig(double workload, double arrivalrate,
			int vm_type, int numOfVMs, TransactionClass tc) {
		// create required configuration

		Config conf = Config.createConfig();

		// create copies of TransactionClass
		// but leave original untouched
		TransactionClass c1 = TransactionClass.copy(tc);

		for (int i = 0; i < numOfVMs; i++) {
			VM vm = VM.createVM(vm_type);
			c1.addvm(vm); // assign vm to class c1
			conf.addvm(vm); // add vm1 to config
		}
		conf.addtc(c1); // add class c1 to config

		return conf;
	}

	public static Config addVM(Config conf, TransactionClass tc, int vm_type) {
		// NOT CREATING A NEW CONFIG, INSTEAD JUST ADDING THE NEW VM TO EXISTING
		// POLLUTING TC AND CONF????

		VM vm = VM.createVM(vm_type);
		tc.addvm(vm); // assign vm to class c1
		conf.addvm(vm); // add vm1 to config

		return conf;
	}

	public static Config replaceVM(Config conf, TransactionClass tc, int vm_type) {
		// NOT CREATING A NEW CONFIG, INSTEAD JUST ADDING THE NEW VM TO EXISTING
		// TC AND CONF POLLUTING????

		VM vm = VM.createVM(vm_type);
		tc.addvm(vm); // assign vm to class c1
		conf.addvm(vm); // add vm1 to config

		return conf;
	}

	public static Config generateConfigbyAddingVM(Config _conf, int vm_type) {
		System.out.println("generateConfigbyAddingVM");
		Config conf = Config.createConfig();
		conf.copy(_conf);

		conf.print();

		// retrieve the first TC
		VM busyvm = conf.getBusietVM();
		if (DEBUG)
			busyvm.print();
		int busy_tid = busyvm.busiestTC();
		TransactionClass c1 = conf.gettc(busy_tid);
		if (DEBUG)
			c1.print();
		int oldvid = c1.getOnlyvid();

		VM oldvm = conf.getVM(oldvid);

		oldvm.deltc(c1);
		c1.removeOnlyVM();

		if (DEBUG)
			oldvm.print();

		VM vm = VM.createVM(vm_type);
		c1.addOnlyVM(vm); // add vm to class c1

		if (DEBUG)
			vm.print();

		// note, we did not remove oldvm from the config since we are adding
		conf.addvm(vm); // add vm1 to config

		return conf;
	}

	public static Config generateConfigbyLoadBalancing(Config _conf,
			VM _beneficiary, VM _helper) {
		System.out.println("generateConfigbyLoadBalancing");
		Config conf = Config.createConfig();
		conf.copy(_conf);

		if (DEBUG)
			conf.print();

		VM beneficiary = conf.getVM(_beneficiary.id());
		if (DEBUG)
			beneficiary.print();
		VM helper = conf.getVM(_helper.id());
		if (DEBUG)
			helper.print();

		// retrieve the busiest TC
		int busy_tid = beneficiary.busiestTC();
		TransactionClass c1 = conf.gettc(busy_tid);
		if (DEBUG) {
			System.out.println("busy_tid=" + busy_tid);
			c1.print();
			beneficiary.print();
		}

		beneficiary.deltc(c1);
		c1.removeOnlyVM();
		c1.addOnlyVM(helper); // add vm to class c1
		if (DEBUG)
			c1.print();
		return conf;
	}

	public static Config generateConfigReplacingVM(Config _conf, VM oldvm,
			int vm_type) {

		System.out.println("generateConfigReplacingVM");
		_conf.print();
		if (DEBUG) {
			System.out.println("********** old config **********");
			_conf.print();
		}

		if (DEBUG) {
			System.out.println("********** oldvm **********");
			oldvm.print();
		}

		Config conf = createConfig();
		conf.copy(_conf);
		VM newvm = VM.createVM(vm_type, oldvm);

		Vector<Integer> tcs = newvm.getAlltc();
		Iterator<Integer> tc_iter = tcs.iterator();

		// use hasNext() and next() methods of Iterator to iterate through the
		// elements update the vid of the newvm in the TCs
		while (tc_iter.hasNext()) {
			Integer tid = (Integer) tc_iter.next();
			TransactionClass tc = conf.gettc(tid.intValue());
			tc.removeOnlyVM();
			tc.addOnlyVMid(newvm.id());
		}

		if (DEBUG) {
			System.out.println("********** new config **********");
			conf.print();
		}

		if (DEBUG) {
			System.out.println("********** newvm **********");
			newvm.print();
		}

		conf.removevm(oldvm);
		conf.addvm(newvm);
		return conf;
	}

	public static Config generateConfigbyDownsizing(Config _conf, VM remove,
			VM offloader) {
		System.out.println("generateConfigbyDownsizing");
		_conf.print();
		Config conf = Config.createConfig();
		conf.copy(_conf);

		if (remove.id() == offloader.id()) {
			// no changes
			return conf;
		}

		VM useless = conf.getVM(remove.id());
		if (DEBUG)
			useless.print();
		VM takeover = conf.getVM(offloader.id());
		if (DEBUG)
			takeover.print();

		Vector<Integer> tcs = useless.getAlltc();
		Iterator<Integer> tc_iter = tcs.iterator();

		// update TCs with the vid of takeover
		// add TCs to takeover
		while (tc_iter.hasNext()) {
			Integer tid = (Integer) tc_iter.next();
			TransactionClass tc = conf.gettc(tid.intValue());
			tc.removeOnlyVM();
			tc.addOnlyVMid(takeover.id());
			takeover.addtc(tc);
		}

		conf.removevm(useless);
		conf.action(Action.Downsize.type());
		conf.cost(-1);
		if (DEBUG)
			takeover.print();
		if (DEBUG)
			conf.print();

		return conf;
	}

	public void copy(Config conf) {
		m_num_of_dp = conf.m_num_of_dp;
		m_$cost = conf.m_$cost;
		m_vm_cost = conf.m_vm_cost;
		m_storage_cost = conf.m_storage_cost;
		m_network_cost = conf.m_network_cost;
		m_penalty_cost = conf.m_penalty_cost;
		m_cores = conf.m_cores;
		m_iteration = conf.m_iteration;
		m_action_generated = conf.m_action_generated;
		m_T_ = conf.m_T_;
		m_R = conf.m_R;
		m_X = conf.m_X;
		m_U = conf.m_U;

		m_unstable = conf.m_unstable;
		m_minimum = conf.m_minimum;
		m_vmcost = conf.m_vmcost;

		m_cheapest_vid = conf.m_cheapest_vid;
		m_cheapest_vm_cost = conf.m_cheapest_vm_cost;

		// meta data
		// overwrite everything but id
		// m_id = conf.m_id;

		m_mapvm = new HashMap<Integer, VM>(); // vid to VM

		Set vms = conf.m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			VM vm = (VM) vmentry.getValue();
			VM copy = VM.copy(vm);
			m_mapvm.put(copy.id(), copy);
		}

		m_maptc = new HashMap<Integer, TransactionClass>(); // partition
		Set tcs = conf.m_maptc.entrySet();
		Iterator tc_iter = tcs.iterator();

		// copy all ids of all transaction classes, vms and dps associated with
		// them
		while (tc_iter.hasNext()) {
			Map.Entry tcentry = (Map.Entry) tc_iter.next();
			TransactionClass tc = (TransactionClass) tcentry.getValue();
			TransactionClass copy = TransactionClass.copy(tc);
			m_maptc.put(copy.id(), copy);
		}

		m_mapdp = new HashMap<Integer, Partition>(); // partition
		Set dps = conf.m_mapdp.entrySet();
		Iterator dp_iter = dps.iterator();

		// copy all ids of all partitions
		while (dp_iter.hasNext()) {
			Map.Entry dpentry = (Map.Entry) dp_iter.next();
			Partition dp = (Partition) dpentry.getValue();
			Partition copy = Partition.copy(dp);
			m_mapdp.put(copy.id(), copy);
		}
	}

	/**************************************
	 * helper methods
	 **************************************/

	void print() {
		System.out.println("----- config id=" + m_id + " with $cost=$"
				+ df.format(m_$cost) + " generated by action = "
				+ m_action_generated + "-----");

		System.out.println("cost breakdown: vm_cost=$" + df.format(m_vm_cost)
				+ ", storage_cost=$" + df.format(m_storage_cost)
				+ ", network_cost=$" + df.format(m_network_cost)
				+ ", penalty_cost=$" + df.format(m_penalty_cost)
				+ ", constant=$" + df.format(CostModel.constant));
		System.out.println("#(classes)=" + m_maptc.size() + ", #(partitions)="
				+ m_num_of_dp // + ", #(replicas)=" + m_num_of_replicas
				+ ", #(VMs)=" + numOfVMs() + ", #(cores)=" + m_cores);
		System.out.println("utilization=" + df.format(m_U) + ", throughput="
				+ df.format(m_X) + "t/s, ResponseTime=" + df.format(m_R) + "s");

		Set tcs = m_maptc.entrySet();
		Iterator tc_iter = tcs.iterator();

		// print all ids of all transaction classes and vms associated with them
		while (tc_iter.hasNext()) {
			Map.Entry tcentry = (Map.Entry) tc_iter.next();
			TransactionClass tc = (TransactionClass) tcentry.getValue();

			System.out.print("TC(" + tcentry.getKey() + ")'s VMs(ids): ");
			System.out.println(tc.getOnlyvid());
		}

		// print all ids of all VMs and transaction classes associated with them

		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			VM vm = (VM) vmentry.getValue();

			System.out.print("vm(" + vmentry.getKey() + ") of type="
					+ vm.type() + " ==> TCs(id): ");

			Vector<Integer> tcids = vm.getAlltc();
			Iterator<Integer> iter = tcids.iterator();
			while (iter.hasNext()) {
				Integer tcid = (Integer) iter.next();
				System.out.print(tcid + ",");
			}
			System.out.println();
		}
	}

	void printSLA() {
		System.out.println("SLA for config(id=" + id() + ") with #TCs = "
				+ m_maptc.size() + ":");

		System.out
				.println("class,#(partitions),arrival_rates,desiredRespTime,actualRespTime,desiredTPS,actualTPS");

		Set tcs = m_maptc.entrySet();
		Iterator tc_iter = tcs.iterator();

		// print all ids of all transaction classes and vms associated with them
		while (tc_iter.hasNext()) {
			Map.Entry tcentry = (Map.Entry) tc_iter.next();
			TransactionClass tc = (TransactionClass) tcentry.getValue();

			System.out.println(tcentry.getKey() + "," + tc.numOfDP() + ","
					+ df.format(tc.getArrivalRate()) + ","
					+ df.format(tc.desiredRespTime()) + ","
					+ df.format(tc.actualRespTime()) + ","
					+ df.format(tc.desiredTPS()) + ","
					+ df.format(tc.actualTPS()));
		}

		System.out
				.println("class,#(partitions),arrival_rates,penalty_basic($),respPenalty_unit_time($/h),tpsPenalty_unit_time($/h)");

		tcs = m_maptc.entrySet();
		tc_iter = tcs.iterator();

		// print all ids of all transaction classes and vms associated with them
		while (tc_iter.hasNext()) {
			Map.Entry tcentry = (Map.Entry) tc_iter.next();
			TransactionClass tc = (TransactionClass) tcentry.getValue();

			System.out.println(tcentry.getKey() + "," + tc.numOfDP() + ","
					+ df.format(tc.getArrivalRate()) + ","
					+ df.format(tc.basepenalty()) + ","
					+ df.format(CostModel.binaryPenaltyResponseTime(tc)) + ","
					+ df.format(CostModel.binaryPenaltyThroughput(tc)));
		}
	}

	void printVMmap() {
		System.out.println("VMMAP for config(id=" + id() + ") with #VMs = "
				+ numOfVMs() + ":");

		int[] vmmap = new int[VM.typesOfVM()];

		// initialize all to zero
		for (int i = 0; i < VM.typesOfVM(); i++) {
			vmmap[i] = 0;
		}

		// print all ids of all VMs and transaction classes associated with them
		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			VM vm = (VM) vmentry.getValue();
			vmmap[vm.type()]++;
		}

		// initialize all to zero
		for (int i = 0; i < VM.typesOfVM(); i++) {
			System.out.println("#vm_type(" + i + ")=" + vmmap[i]);
		}

		System.out.println("...........................");

	}
	
	void printcsvVMmap(FileWriter csv) {
		printcsvVMmap(csv, "");
	}

	void printcsvVMmap(FileWriter csv, String alogName) {
		int[] vmmap = new int[VM.typesOfVM()];

		// initialize all to zero
		for (int i = 0; i < VM.typesOfVM(); i++) {
			vmmap[i] = 0;
		}

		// print all ids of all VMs and transaction classes associated with them
		Set vms = m_mapvm.entrySet();
		Iterator vm_iter = vms.iterator();

		while (vm_iter.hasNext()) {
			Map.Entry vmentry = (Map.Entry) vm_iter.next();
			VM vm = (VM) vmentry.getValue();
			vmmap[vm.type()]++;
		}

		try {
			csv.write("Algorithm, small,hp-med,large,hm-xl,xlarge\n");
			csv.write(alogName + "(VMs),");
			// initialize all to zero
			for (int i = 0; i < VM.typesOfVM(); i++) {
				csv.write(vmmap[i] + ",");
			}
			csv.write("\n\n");

			// print all ids of all VMs and transaction classes associated with
			// them

			csv
					.write("VM_id(#),VM_type,responseTime(s),throughput(tps),utilization,classes...\n");

			vms = m_mapvm.entrySet();
			vm_iter = vms.iterator();

			while (vm_iter.hasNext()) {
				Map.Entry vmentry = (Map.Entry) vm_iter.next();
				VM vm = (VM) vmentry.getValue();

				csv.write(vm.id() + "," + vm.type() + ","
						+ df.format(vm.responseTime()) + ","
						+ df.format(vm.throughput()) + ","
						+ df.format(vm.utilization()) + ",");

				Vector<Integer> tcids = vm.getAlltc();
				Iterator<Integer> iter = tcids.iterator();
				while (iter.hasNext()) {
					Integer tcid = (Integer) iter.next();
					csv.write(tcid + ",");
				}
				csv.write("\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void printSLA(FileWriter csv) {

		try {
			csv
					.write("class,#(partitions),arrival_rates,population,desiredRespTime,actualRespTime,desiredTPS,actualTPS,throughput(tps),utilization\n");
			Set tcs = m_maptc.entrySet();
			Iterator tc_iter = tcs.iterator();

			// print all ids of all transaction classes and vms associated with
			// them
			while (tc_iter.hasNext()) {
				Map.Entry tcentry = (Map.Entry) tc_iter.next();
				TransactionClass tc = (TransactionClass) tcentry.getValue();

				csv.write(tcentry.getKey() + "," + tc.numOfDP() + ","
						+ df.format(tc.getArrivalRate()) + ","
						+ df.format(tc.population()) + ","
						+ df.format(tc.desiredRespTime()) + ","
						+ df.format(tc.actualRespTime()) + ","
						+ df.format(tc.desiredTPS()) + ","
						+ df.format(tc.actualTPS()) + ","
						+ df.format(tc.throughput()) + ","
						+ df.format(tc.utilization()) + "\n");
			}

			csv
					.write("class,#(partitions),arrival_rates,penalty_basic($),respPenalty_unit_time($/h),tpsPenalty_unit_time($/h)\n");

			tcs = m_maptc.entrySet();
			tc_iter = tcs.iterator();

			// print all ids of all transaction classes and vms associated with
			// them
			while (tc_iter.hasNext()) {
				Map.Entry tcentry = (Map.Entry) tc_iter.next();
				TransactionClass tc = (TransactionClass) tcentry.getValue();

				csv.write(tcentry.getKey() + "," + tc.numOfDP() + ","
						+ df.format(tc.getArrivalRate()) + ","
						+ df.format(tc.basepenalty()) + ","
						+ df.format(CostModel.binaryPenaltyResponseTime(tc))
						+ ","
						+ df.format(CostModel.binaryPenaltyThroughput(tc))
						+ "\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
