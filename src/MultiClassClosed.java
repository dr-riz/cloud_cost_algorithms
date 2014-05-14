import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MultiClassClosed extends QueuingModel {

	private double m_utilization;
	private double m_queueLength;
	private double m_throughput;
	Config m_conf;
	VM m_vm;
	private Vector<Integer> m_tcIDs = null;
	private Vector<ServiceCenter> m_sc;
	private double K;
	private boolean iocenter = false;

	// printing help
	boolean m_verbose = false;

	@SuppressWarnings("unused")
	private MultiClassClosed() {
		;
	}

	MultiClassClosed(VM vm, Config conf, boolean delayCenter, boolean verbose) {
		m_conf = conf;
		m_vm = vm;
		m_tcIDs = vm.getAlltc();
		m_utilization = -1;
		m_queueLength = -1;
		m_throughput = -1;
		m_verbose = verbose;
		// m_verbose = true;// uncomment to force verbose output

		m_sc = new Vector<ServiceCenter>();
		ServiceCenter proc = new ServiceCenter(false);
		m_sc.add(proc);

		Iterator<Integer> tcid_iter = m_tcIDs.iterator();
		if (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
		
		TransactionClass atc = m_conf.gettc(tid);
		if (atc.ioDemand(vm.type()) == -1) {
			iocenter = false;
		} else {
			iocenter = true;
		}
		}

		ServiceCenter io = null;
		if (iocenter) {
			io = new ServiceCenter(false);
			m_sc.add(io);
		}

		tcid_iter = m_tcIDs.iterator();
		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			TransactionClass tc = m_conf.gettc(tid);

			proc.demand_for_tc(tid, serviceDemand4tc(tid));
			if (iocenter)
				io.demand_for_tc(tid, tc.ioDemand(vm.type()));

			// initialize
			proc.arrivalq_for_tc(tid, 0);
			if (iocenter)
				io.arrivalq_for_tc(tid, 0);

			proc.qlen_for_tc(tid, 0);
			if (iocenter)
				io.qlen_for_tc(tid, 0);

			proc.res_for_tc(tid, 0);
			if (iocenter)
				io.res_for_tc(tid, 0);
		}

		K = m_sc.size();

		init();
	}

	private void init() {
		// boot start
		if (m_verbose)
			System.out.println("---boot start---");

		Iterator<Integer> tcid_iter = m_tcIDs.iterator();
		tcid_iter = m_tcIDs.iterator();
		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			TransactionClass tc = m_conf.gettc(tid);

			double aggregate_res_time_4_tc_across_SCs = 0;
			for (int i = 0; i < m_sc.size(); i++) {
				ServiceCenter sc = m_sc.get(i);
				double que = tc.population() / K;
				sc.qlen_for_tc(tid, que);

				if (m_verbose)
					System.out.println("tc(" + tc.id() + ")'s queue len=" + que
							+ " at service center=" + i);
			}
		}

		int n = 1;
		boolean keepIterating = true;
		while (keepIterating) {
			keepIterating = false;
			if (m_verbose)
				System.out.println("==== iteration " + n + "=====");

			// calculate revised arrival instant queue
			if (m_verbose)
				System.out
						.println("--- calculate revised arrival instant queue---");

			for (int i = 0; i < m_sc.size(); i++) {
				ServiceCenter sc = m_sc.get(i);

				// aggregate queue lenghts
				double aggregate_q_len = sc.queue_len_at_sc();

				Set queues = sc.m_arrival_q.entrySet();
				Iterator iter = queues.iterator();
				while (iter.hasNext()) {
					Map.Entry tc_entry = (Map.Entry) iter.next();
					int tid = (Integer) tc_entry.getKey(); // (Integer)
					TransactionClass tc = m_conf.gettc(tid);

					double qlen_4_tc = sc.qlen_for_tc(tid);
					double A_c_k = (tc.population() - 1) / tc.population()
							* qlen_4_tc + aggregate_q_len - qlen_4_tc;

					sc.arrivalq_for_tc(tid, A_c_k);
					tc.arrival_instant_q = A_c_k;

					if (m_verbose)
						System.out.println("tc(" + tc.id()
								+ ")'s arrival instant q=" + A_c_k);
				}
			}

			// calculate residence times
			if (m_verbose)
				System.out.println("---calculate residence times---");

			for (int i = 0; i < m_sc.size(); i++) {
				ServiceCenter sc = m_sc.get(i);

				Set residence_set = sc.m_residence.entrySet();
				Iterator iter = residence_set.iterator();
				while (iter.hasNext()) {
					Map.Entry tc_entry = (Map.Entry) iter.next();
					int tid = (Integer) tc_entry.getKey(); // (Integer)

					TransactionClass tc = m_conf.gettc(tid);

					double resTime = sc.demand_for_tc(tid)
							* (1 + sc.arrivalq_for_tc(tid));
					sc.res_for_tc(tid, resTime);
					tc.m_residence = resTime;

					if (m_verbose)
						System.out.println("tc(" + tc.id()
								+ ")'s residence time =" + sc.res_for_tc(tid) // resTime
								+ " at center " + i);
				}
			}

			// calculate throughput
			if (m_verbose)
				System.out.println("--- calculate throughput ---");

			// Iterator<Integer> tcid_iter = m_tcIDs.iterator();
			tcid_iter = m_tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				TransactionClass tc = m_conf.gettc(tid);

				double aggregate_res_time_4_tc_across_SCs = 0;
				for (int i = 0; i < m_sc.size(); i++) {
					ServiceCenter sc = m_sc.get(i);
					aggregate_res_time_4_tc_across_SCs += sc.res_for_tc(tid);
				}
				if (m_verbose)
					System.out.println("aggregated residence time across SC = "
							+ aggregate_res_time_4_tc_across_SCs);
				tc.m_throughput = tc.population()
						/ (tc.think() + aggregate_res_time_4_tc_across_SCs);
				if (m_verbose)
					System.out.println("tc(" + tc.id() + ")'s throughput="
							+ tc.m_throughput);
			}

			// revise queues
			if (m_verbose)
				System.out.println("--- revise queues ---");

			for (int i = 0; i < m_sc.size(); i++) {
				ServiceCenter sc = m_sc.get(i);

				Set queues = sc.m_queue.entrySet();
				Iterator iter = queues.iterator();
				while (iter.hasNext()) {
					Map.Entry tc_entry = (Map.Entry) iter.next();
					int tid = (Integer) tc_entry.getKey(); // (Integer)

					TransactionClass tc = m_conf.gettc(tid);
					double new_q = tc.m_throughput * sc.res_for_tc(tid);
					if (Math.abs(new_q - sc.qlen_for_tc(tid)) > tc.m_tolerance) {
						keepIterating = true;
					}

					sc.qlen_for_tc(tid, new_q);
					tc.queue_len = new_q;

					if (m_verbose)
						System.out.println("tc(" + tc.id() + ")'s queue len="
								+ new_q);
				}

				if (m_verbose)
					System.out.println("sc(" + i + ")'s queue len="
							+ sc.queue_len_at_sc());
			}
			n++;
		}

		// calculate response times of classes
		if (m_verbose)
			System.out.println("--- calculate response times of classes ---");

		tcid_iter = m_tcIDs.iterator();
		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			TransactionClass tc = m_conf.gettc(tid);
			double responseTime = tc.population() / tc.m_throughput
					- tc.think();
			tc.actualRespTime(responseTime);
			if (m_verbose)
				System.out.println("tc(" + tc.id() + ")'s response time="
						+ tc.actualRespTime());
		}
	}

	public double throughput4tc(int tid) {
		TransactionClass tc = m_conf.gettc(tid);
		double ret = tc.m_throughput;
		if (m_verbose)
			System.out.println("throughput4tc(" + tid + ")=" + ret);
		return ret;
	}

	public double throughput() {
		double ret = 0;
		if (m_throughput == -1) {
			Iterator<Integer> tcid_iter = m_tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				ret += throughput4tc(tid);
			}
			m_throughput = ret;
		} else {
			ret = m_throughput;
		}
		if (m_verbose)
			System.out.println("throughput=" + ret);
		return ret;
	}

	public double residenceTime4tc(int tid) {
		TransactionClass tc = m_conf.gettc(tid);
		double ret = tc.actualRespTime();

		if (m_verbose)
			System.out.println("residenceTime4tc(" + tid + ")=" + ret);
		return ret;
	}

	public double responseTime() {
		double ret = 0;
		Iterator<Integer> tcid_iter = m_tcIDs.iterator();
		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			ret += (residenceTime4tc(tid) * throughput4tc(tid));
		}
		ret = ret / throughput();
		if (m_verbose)
			System.out.println("responseTime=" + ret);
		return ret;
	}

	public double utilization4tc(int tid) {
		double ret = 0;

		TransactionClass tc = m_conf.gettc(tid);
		double total_demand = 0;
		for (int i = 0; i < m_sc.size(); i++) {
			ServiceCenter sc = m_sc.get(i);
			total_demand += sc.demand_for_tc(tid);

		}

		double util = tc.throughput() * total_demand;
		tc.utilization(util);
		ret = util;

		if (m_verbose)
			System.out.println("utilization4tc(" + tid + ")=" + ret);
		return ret;
	}

	public double utilization() {
		double ret = 0;
		if (m_utilization == -1) {
			Iterator<Integer> tcid_iter = m_tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				ret += utilization4tc(tid);
			}

			// adjusting utilization of vm if #users < #cores
			double factor = ((double) Math.min(Math.ceil(m_vm.numOfUsers()),
					m_vm.cores())) / ((double) m_vm.cores());			
			ret = ret * factor;

			m_utilization = ret;
		} else {
			ret = m_utilization;
		}
		if (m_verbose)
			System.out.println("utilization=" + ret);
		return m_utilization;
	}

	public double qLen4tc(int tid) {
		double ret = 0;

		for (int i = 0; i < m_sc.size(); i++) {
			ServiceCenter sc = m_sc.get(i);
			ret += sc.qlen_for_tc(tid);
		}

		if (m_verbose)
			System.out.println("qLen4tc(" + tid + ")=" + ret);
		return ret;
	}

	public double queueLength() {
		double ret = 0;
		if (m_queueLength == -1) {
			Iterator<Integer> tcid_iter = m_tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				ret += qLen4tc(tid);
			}
			m_queueLength = ret;
		} else {
			ret = m_queueLength;
		}
		if (m_verbose)
			System.out.println("utilization=" + ret);
		return ret;
	}

	// service rate ... is it a valid concept for closed model?
	public double serviceRate4tc(int tid) {
		double ret = throughput4tc(tid) * ((double) m_vm.cores());
		if (m_verbose)
			System.out.println("serviceRate4tc(" + tid + ")=" + ret);
		return ret;
	}

	public double serviceRate() {
		double ret = throughput() * ((double) m_vm.cores());
		if (m_verbose)
			System.out.println("serviceRate=" + ret);
		return ret;
	}

	public int highestUtilTC() {
		int ret = 0;

		Iterator<Integer> tcid_iter = m_tcIDs.iterator();
		ret = tcid_iter.next();
		double high = utilization4tc(ret);

		while (tcid_iter.hasNext()) {
			int tid = tcid_iter.next();
			if (high < utilization4tc(tid)) {
				ret = tid;
			}
		}
		if (m_verbose)
			System.out.println("highestUtilTC=" + ret);
		return ret;
	}

	@Override
	public double proc_capacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double serviceDemand4tc(int tid) {
		TransactionClass tc = m_conf.gettc(tid);

		double Dc = tc.Texe(m_vm.type())
				/ ((double) Math.min(Math.ceil(m_vm.numOfUsers()), m_vm
						.cores()));

		if (m_verbose)
			System.out.println("serviceDemand4tc(" + tid + ")=" + Dc);
		return Dc;
	}
}
