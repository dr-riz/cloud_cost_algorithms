import java.util.HashMap;

public class LRPredictor {
	// map of vm_type to specific model of LR
	private HashMap<Integer, SpecificModel> m_map;

	private double[] m_respone_times = null;

	// help with output
	private boolean verbose = false;

	// workload types
	final public static int wl_a = 3; // read only
	final public static int wl_b = 5; // update heavy
	final public static int wl_c = 7; // read heavy

	final public static int wl_a_b = 8;
	final public static int wl_b_c = 12;
	final public static int wl_a_c = 10;
	final public static int wl_a_b_c = 15;

	/*
	 * workloads
	 */
	// request order: Q1, Q6, <tpcc>, broker-volume, security-detail

	final public static double[] wl_a_type = { 0.5, 0.5, 0, 0, 0, 0, 0, 0 };
	final public static double[] wl_b_type = { 0, 0, 0, 0, 0, 0, 0.5, 0.5 };
	final public static double[] wl_c_type = { 0, 0, 0.250, 0.250, 0.250,
			0.250, 0, 0 };

	// combos
	final public static double[] wl_a_b_type = { 0.25, 0.25, 0, 0, 0, 0, 0.25,
			0.25 };
	final public static double[] wl_b_c_type = { 0, 0, 0.167, 0.167, 0.167,
			0.167, 0.167, 0.167 };
	final public static double[] wl_a_c_type = { 0.167, 0.167, 0.167, 0.167,
			0.167, 0.167, 0, 0 };
	final public static double[] wl_a_b_c_type = { 0.125, 0.125, 0.125, 0.125,
			0.125, 0.125, 0.125, 0.125 };

	private static LRPredictor instance = null;

	public static LRPredictor getInstance() {
		if (instance == null) {
			instance = new LRPredictor();
		}
		return instance;
	}

	private LRPredictor() {
		m_map = new HashMap<Integer, SpecificModel>();
	}

	public void addLR(int vm_type) {
		LR4VM lr = new LR4VM();
		m_map.put(vm_type, lr);

	}
	
	public void addSVM(int vm_type) {
		SVR4VM lr = new SVR4VM();
		m_map.put(vm_type, lr);

	}

	public void initLRCoeff(int vm_type, int lr, int rid, double coeff) {
		LR4VM sm = (LR4VM) m_map.get(vm_type);
		sm.initCoeff(lr, rid, coeff);
	}
	
	public void initSVR(int vm_type, int rid, String path) {
		SVR4VM sm = (SVR4VM) m_map.get(vm_type);
		sm.initSVR(rid, path);
	}

	public double prediction(int vm_type, int lr, double q1, double q6,
			double q12, double q21, double newo, double pay, double order,
			double update, int users) {
		SpecificModel sm = (SpecificModel) m_map.get(vm_type);
		return sm.predict(lr, q1, q6, q12, q21, newo, pay, order, update,
				users);
	}

	public double prediction(int vm_type, int workload, int lr, double mpl) {
		SpecificModel sm = (SpecificModel) m_map.get(vm_type);
		double[] workload_type = wl2wl_type(workload);
		double pred = 0; // assume request type is not present in the
							// workload_type
		if (workload_type[lr] != 0) {
			pred = sm.predict(lr, workload_type[SpecificModel.Q1] * mpl,
					workload_type[SpecificModel.Q6] * mpl,
					workload_type[SpecificModel.Q12] * mpl,
					workload_type[SpecificModel.Q21] * mpl,
					workload_type[SpecificModel.new_order] * mpl,
					workload_type[SpecificModel.payment] * mpl,
					workload_type[SpecificModel.trade_order] * mpl,
					workload_type[SpecificModel.trade_update] * mpl);
		}

		return pred;
	}
	
	public double prediction(int vm_type, int workload, int lr) {
		SpecificModel sm = (SpecificModel) m_map.get(vm_type);
		double[] workload_type = wl2wl_type(workload);
		double pred = 0; // assume request type is not present in the
							// workload_type
		if (workload_type[lr] != 0) {
			pred = sm.predict(lr, workload_type[SpecificModel.Q1],
					workload_type[SpecificModel.Q6],
					workload_type[SpecificModel.Q12],
					workload_type[SpecificModel.Q21],
					workload_type[SpecificModel.new_order],
					workload_type[SpecificModel.payment],
					workload_type[SpecificModel.trade_order],
					workload_type[SpecificModel.trade_update]);
		}

		return pred;
	}

	public double predictResponseTimeOverEntireWL(int vm_type, int workload) {
		SpecificModel sm = (SpecificModel) m_map.get(vm_type);
		double response = Integer.MAX_VALUE;

		double[] workload_type = wl2wl_type(workload);
		System.out.println("responese time for workload_type="
				+ wl_type_str(workload));
		for (int request_type = 0; request_type < SpecificModel.num_of_input_vars; request_type++) {

			if (workload_type[request_type] == 0) {
				// request type with zero instances is not included in the
				// analysis
				continue;
			}
			double resp = m_respone_times[request_type];

			if (verbose) {
				System.out.println("respone time of request type("
						+ request_type + ")=" + resp);
			}

			if (resp < response) {
				response = resp;
			}

		}
		return response;
	}

	public double predictResponseOverEntireWL(int vm_type, int workload) {
		SpecificModel sm = (SpecificModel) m_map.get(vm_type);
		double response = Integer.MAX_VALUE;

		double[] workload_type = wl2wl_type(workload);
		System.out.println("responese time for workload_type="
				+ wl_type_str(workload));
		for (int request_type = 0; request_type < SpecificModel.num_of_input_vars; request_type++) {

			if (workload_type[request_type] == 0) {
				// request type with zero instances is not included in the
				// analysis
				continue;
			}
			double resp = m_respone_times[request_type];

			if (verbose) {
				System.out.println("respone time of request type("
						+ request_type + ")=" + resp);
			}

			if (resp < response) {
				response = resp;
			}

		}
		return response;
	}

	public static double[] wl2wl_type(int wl) {
		double[] wltype = null;

		switch (wl) {
		case (wl_a): {
			wltype = wl_a_type;
			break;
		}
		case (wl_b): {
			wltype = wl_b_type;
			break;
		}
		case (wl_c): {
			wltype = wl_c_type;
			break;
		}
		case (wl_a_b): {
			wltype = wl_a_b_type;
			break;
		}
		case (wl_b_c): {
			wltype = wl_b_c_type;
			break;
		}
		case (wl_a_c): {
			wltype = wl_a_c_type;
			break;
		}
		case (wl_a_b_c): {
			wltype = wl_a_b_c_type;
			break;
		}
		}

		return wltype;
	}

	public static String wl_type_str(int wl) {
		String wltype = null;

		switch (wl) {
		case (wl_a): {
			wltype = "read_only(a)";
			break;
		}
		case (wl_b): {
			wltype = "update_heavy(b)";
			break;
		}
		case (wl_c): {
			wltype = "read_write(c)";
			break;
		}
		case (wl_a_b): {
			wltype = "read_only+update_heavy(a+b)";
			break;
		}
		case (wl_b_c): {
			wltype = "update_heavy+read_write(b+c)";
			break;
		}
		case (wl_a_c): {
			wltype = "read_only+read_write(a+c)";
			break;
		}
		case (wl_a_b_c): {
			wltype = "read_only+update_heavy+read_write(a+b+c)";
			break;
		}
		}

		return wltype;
	}

	public static double weight(int wltype) {
		double ret = 0;
		switch (wltype) {
		case (wl_a): { // readonly
			ret = 1;
			break;
		}
		case (wl_b): { // update-heavy
			ret = 0.2;
			break;
		}
		case (wl_c): { // readonly
			ret = 0.8;
			break;
		}
		case (wl_a_b): {
			ret = 1.2;
			break;
		}
		case (wl_b_c): {
			ret = 1;
			break;
		}
		case (wl_a_c): {
			ret = 1.8;
			break;
		}
		case (wl_a_b_c): {
			ret = 2;
			break;
		}
		}
		return ret;
	}

	 public static void main(String args[]) {
	 System.out.println("LR Predictor");
	
	 int vm_type = 0;
	 /* goes into QingModel.java */
	 LRPredictor lrp = new LRPredictor();
	 lrp.addSVM(vm_type);
	
	 lrp.initSVR(vm_type, SpecificModel.trade_update, "/home/mian/models_lhs@mpl/xlarge_trade-update_tps.model");
	 double metric = lrp.prediction(vm_type, wl_a_b_c, SpecificModel.trade_update, 115);
			 
	 System.out.println("metric (s or tps)=" + metric);
	 }

}
