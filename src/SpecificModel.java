abstract public class SpecificModel {
	final public static int num_of_input_vars = 8;
	final public static int Q1 = 0;
	final public static int Q6 = 1;
	final public static int Q12 = 2;
	final public static int Q21 = 3;
	final public static int new_order = 4;
	final public static int payment = 5;
	final public static int trade_order = 6;
	final public static int trade_update = 7;
	final public static int userload = 8;
	final public static int constant = 9;

	final public static int na = -999;

	abstract public double predict(int rid, double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update, double users);
	
	public double predict(int rid, double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update) {
		return 0;
	} 

}
