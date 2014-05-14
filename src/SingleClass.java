public class SingleClass {

	double m_Dmax;
	double m_proc_capacity;
	double m_arrival_rate;
	double m_utilization;
	boolean m_delayCenter;
	double m_Dk;
	double m_residenceTime;
	double m_queueLength;
	Config m_conf;
	VM m_vm;
	TransactionClass m_onlytc;
	
	boolean m_verbose = false;

	@SuppressWarnings("unused")
	private SingleClass() {
		;
	}

	SingleClass(VM vm, Config conf, boolean delayCenter, boolean verbose) {
		m_conf = conf;
		m_vm = vm;

		// for single class, there is only one service center
		// and that is also Dmax
		int tid = vm.getAlltc().get(0);
		m_onlytc = m_conf.gettc(tid);
		m_arrival_rate = m_onlytc.getArrivalRate();
		m_Dk = (m_onlytc.Texe(m_vm.type())) / ((double) vm.cores());
		m_Dmax = m_Dk;
		m_proc_capacity = 1 / m_Dmax;

		m_delayCenter = delayCenter;
		m_utilization = -1;
		m_residenceTime = -1;
		m_queueLength = -1;
		
		m_verbose = verbose;		
	}

	public double serviceDemand() {
		if(m_verbose) System.out.println("serviceDemand=" + m_Dk);
		return m_Dk;
	}

	// processing capacity
	public double proc_capacity() {
		if(m_verbose) System.out.println("proc_capacity=" + m_proc_capacity);
		return m_proc_capacity;
	}

	public double throughput() {
		if(m_verbose) System.out.println("throughput=" + m_arrival_rate);
		return m_arrival_rate;
	}

	public double utilization() {
		m_utilization = throughput() * m_Dk;
		if(m_verbose) System.out.println("utilization=" + m_utilization);
		return m_utilization;
	}

	public double residenceTime() {
		if (m_delayCenter) {
			m_residenceTime = m_Dk;
		} else {
			m_residenceTime = m_Dk / (1 - utilization());
		}
		if(m_verbose) System.out.println("residenceTime=" + m_residenceTime);
		return m_residenceTime;
	}

	public double queueLength() {
		double util = utilization();
		if (m_delayCenter) {
			m_queueLength = util;
		} else {
			m_queueLength = util / (1 - util);
		}
		if(m_verbose) System.out.println("queueLength=" + m_queueLength);
		return m_queueLength;
	}

	public double serviceRate() {
		double ret = m_onlytc.getArrivalRate() * m_vm.cores();
		if(m_verbose) System.out.println("serviceRate=" + ret);
		return ret;
	}
	
	public int highestUtilTC() {
		int ret = m_onlytc.id();
		
		if (m_verbose)
			System.out.println("highestUtilTC=" + ret);
		return ret;
	}

}
