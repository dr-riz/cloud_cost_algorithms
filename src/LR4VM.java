import java.awt.image.VolatileImage;

public class LR4VM extends SpecificModel{
	double[][] model;

	public LR4VM() {
		model = new double[num_of_input_vars][];
		for (int i = 0; i < num_of_input_vars; i++) {
			model[i] = null;
		}
	}

	public void initCoeff(int lr, int rid, double coeff) {
		if (rid < 0 || rid > num_of_input_vars) {
			System.out.println("invalid rid:" + rid);
			return;
		}
		if (model[lr] == null) {
			model[lr] = new double[num_of_input_vars + 1]; // 1 for constant
			for (int id = 0; id < num_of_input_vars + 1; id++) {
				model[lr][id] = 0;
			}
		}
		model[lr][rid] = coeff;
	}

	public double predict(int lr, double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update, double users) {
		double res = na;
		double[] lrmodel = model[lr];
//		printLRCoeff(lr);

//		System.out.println("lr=" + lr + ", q1=" + q1 + ", q6=" + q6 + ", q12="
//				+ q12 + ", q21=" + q21 + ", newo=" + newo + ", pay=" + pay
//				+ ", order=" + order + ", update=" + update);

		res = q1 * lrmodel[Q1] + q6 * lrmodel[Q6] + q12 * lrmodel[Q12] + q21
				* lrmodel[Q21] + newo * lrmodel[new_order] + pay
				* lrmodel[payment] + order * lrmodel[trade_order] + update
				* lrmodel[trade_update] + users * lrmodel[userload] + lrmodel[constant];

		return res;
	}

	public void printLRCoeff(int lr) {
		double[] lrmodel = model[lr];
		if (lr < 0 || lr > num_of_input_vars) {
			System.out.println("invalid LR id:" + lr);
			return;
		}

		for (int id = 0; id < num_of_input_vars + 1; id++) {
			System.out.println("lr(" + lr + ")'s rid(" + id
					+ ") has coefficient =" + model[lr][id]);
		}
	}

	public static void main(String args[]) {
		System.out.println("hello world!");

		/* goes into QingModel.java */
		LR4VM sm = new LR4VM();
		sm.initCoeff(LR4VM.Q1, LR4VM.userload, 3.2489);
		sm.initCoeff(LR4VM.Q1, LR4VM.Q1, 19.2889);
		sm.initCoeff(LR4VM.Q1, LR4VM.constant, -3.7632);

		double metric = sm
				.predict(LR4VM.Q1, 0.172, 0.172, 0.155, 0.172, 0.155, 0.172, 0, 0, 2);
		System.out.println("Q1 (s)=" + metric);

		sm = new LR4VM();
		sm.initCoeff(LR4VM.payment, LR4VM.userload, 0.0093);
		sm.initCoeff(LR4VM.payment, LR4VM.Q1, 0.0207);
		sm.initCoeff(LR4VM.payment, LR4VM.Q21, 0.0902);
		sm.initCoeff(LR4VM.payment, LR4VM.constant, -0.0213);
		
		metric = sm
		.predict(LR4VM.payment, 0.167, 0.167, 0.167, 0.167, 0.167, 0.167, 0, 0, 2);
		System.out.println("payment (tps)=" + metric);	
		
	}

}
