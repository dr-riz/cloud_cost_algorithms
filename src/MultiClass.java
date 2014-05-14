import java.util.Iterator;
import java.util.Vector;

public class MultiClass extends QueuingModel{

	private double m_proc_capacity;
	private double m_utilization;
	private boolean m_delayCenter;
	private double m_queueLength;
	private double m_throughput;
	Config m_conf;
	VM m_vm;
	private Vector<Integer> m_tcIDs = null;

	// printing help
	boolean m_verbose = false;

	@SuppressWarnings("unused")
	private MultiClass() {
		;
	}

	MultiClass(VM vm, Config conf, boolean delayCenter, boolean verbose) {
		m_conf = conf;
		m_vm = vm;
		m_tcIDs = vm.getAlltc();
		m_delayCenter = delayCenter;
		m_proc_capacity = -1;
		m_utilization = -1;
		m_queueLength = -1;
		m_throughput = -1;
		m_verbose = verbose;
	}

	// processing capacity
	public double proc_capacity() {
		double ret = 0;
		if (m_proc_capacity == -1) {
			Iterator<Integer> tcid_iter = m_tcIDs.iterator();
			while (tcid_iter.hasNext()) {
				int tid = tcid_iter.next();
				double Dc = serviceDemand4tc(tid);
				Double x_tc = throughput4tc(tid);
				ret += Dc * x_tc;
			}
			m_proc_capacity = ret;
		} else {
			ret = m_proc_capacity;
		}
		if (m_verbose)
			System.out.println("proc_capacity=" + ret);
		return ret;
	}

	public double serviceDemand4tc(int tid) {
		TransactionClass tc = m_conf.gettc(tid);
		double Dc = tc.Texe(m_vm.type()) / ((double) m_vm.cores());
		if (m_verbose)
			System.out.println("serviceDemand4tc(" + tid + ")=" + Dc);
		return Dc;
	}

	public double throughput4tc(int tid) {
		TransactionClass tc = m_conf.gettc(tid);
		double ret = tc.getArrivalRate();				
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

	public double utilization4tc(int tid) {
		double Dc = serviceDemand4tc(tid);
		Double x_tc = throughput4tc(tid);
		double ret = Dc * x_tc;
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
			m_utilization = ret;
		} else {
			ret = m_utilization;
		}
		if (m_verbose)
			System.out.println("utilization=" + ret);
		return m_utilization;
	}

	public double residenceTime4tc(int tid) {
		double ret = serviceDemand4tc(tid);
		if (!m_delayCenter) {
			ret = ret / (1 - utilization());
		}
		TransactionClass tc = m_conf.gettc(tid);
		tc.actualRespTime(ret);
		if (m_verbose)
			System.out.println("residenceTime4tc(" + tid + ")=" + ret);
		return ret;
	}

	public double qLen4tc(int tid) {
		double ret = utilization4tc(tid) / (1 - utilization());
		if (m_verbose)
			System.out.println("qLen4tc(" + tid + ")=" + ret);
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
			if (high <  utilization4tc(tid)) {
				ret = tid;
			}
		}
		if (m_verbose)
			System.out.println("highestUtilTC=" + ret);
		return ret;
	}

}
