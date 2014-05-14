import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class SVR4VM extends SpecificModel {
	
	Classifier [] model;

	public SVR4VM() {
		model = new Classifier[num_of_input_vars];
		for (int i = 0; i < num_of_input_vars; i++) {
			model[i] = null;
		}
	}
	
	static Instances instances = null;
	public static void main(String[] args) {
		System.out
				.println("unit testing for SVR loading...");
		
		SVR4VM svr4vm = new SVR4VM();
		svr4vm.initSVR(payment, "/home/mian/workspace-java/PartitionAssignment/svm_payment.model");
		
		double metric = svr4vm.predict(payment, 0.167, 0.167, 0.167, 0.167, 0.167, 0.167, 0, 0, 2);
		System.out.println("payment (tps)=" + metric);
		
		
//		generate();
//		predict(svr4vm);
//		predict2();
	}

	public void initSVR(int rid, String path) {
		if (rid < 0 || rid > num_of_input_vars) {
			System.out.println("invalid rid:" + rid);
			return;
		}
		
		try {
			DataSource source = new DataSource("/home/mian/workspace-java/PartitionAssignment/dummy.csv");
			Classifier cls = (Classifier) weka.core.SerializationHelper.read(path);
			model[rid] = cls;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double predict(int rid, double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update, double users) {
		if (rid < 0 || rid > num_of_input_vars) {
			System.out.println("invalid rid:" + rid);
			return na;
		}
//		printSVRModel(rid);
//		System.out.println("rid=" + rid + ", q1=" + q1 + ", q6=" + q6 + ", q12="
//				+ q12 + ", q21=" + q21 + ", newo=" + newo + ", pay=" + pay
//				+ ", order=" + order + ", update=" + update);
		
		Instance ins = createInstance(q1, q6, q12, q21,
				newo, pay, order, update, users);

		double res = na;
		Classifier cls = model[rid];		
		try {
			res = cls.classifyInstance(ins);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public void printSVRModel(int rid) {	
		if (rid < 0 || rid > num_of_input_vars) {
			System.out.println("invalid SVR id:" + rid);
			return;
		}
		
		Classifier cls = model[rid];
		if(cls != null) {
			System.out.println("SVR for request type (" + rid + ") available");
		} else {
			System.out.println("SVR for request type (" + rid + ") NOT available");
		}
	}
	
	public static Instance createInstance (double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update, double users) {
		Instance ins = null;
		try {
			DataSource source = new DataSource("/home/mian/workspace-java/PartitionAssignment/dummy.csv");
			Instances data = source.getDataSet();
			instances = new Instances(data);
			instances.setClassIndex(instances.numAttributes() - 1);
			double [] attValues = {users, q1, q6, q12, q21, newo, pay, order, update}; 
			ins = instances.get(0);
			
			for(int i=0; i<num_of_input_vars; i++) {
				ins.setValue(i, attValues[i]);
			}
			
			instances.delete();		
			instances.add(ins);
			ins = instances.instance(0);			
			ins.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return ins;
	}
	
	public double predict(int rid, double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update) {
		if (rid < 0 || rid > num_of_input_vars) {
			System.out.println("invalid rid:" + rid);
			return na;
		}
//		printSVRModel(rid);
//		System.out.println("rid=" + rid + ", q1=" + q1 + ", q6=" + q6 + ", q12="
//				+ q12 + ", q21=" + q21 + ", newo=" + newo + ", pay=" + pay
//				+ ", order=" + order + ", update=" + update);
		
		Instance ins = createInstance(q1, q6, q12, q21,
				newo, pay, order, update);

		double res = na;
		Classifier cls = model[rid];		
		try {
			res = cls.classifyInstance(ins);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}
	
	public static Instance createInstance (double q1, double q6, double q12, double q21,
			double newo, double pay, double order, double update) {
		Instance ins = null;
		try {
			DataSource source = new DataSource("/home/mian/workspace-java/PartitionAssignment/dummy_wl.csv");
			Instances data = source.getDataSet();
			instances = new Instances(data);
			instances.setClassIndex(instances.numAttributes() - 1);
			double [] attValues = {q1, q6, q12, q21, newo, pay, order, update}; 
			ins = instances.get(0);
			
			for(int i=0; i<num_of_input_vars; i++) {
				ins.setValue(i, attValues[i]);
			}
			
			instances.delete();		
			instances.add(ins);
			ins = instances.instance(0);			
			ins.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return ins;
	}

}