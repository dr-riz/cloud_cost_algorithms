import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.Vector;

public class QingModel {

	public static DecimalFormat df;
	static FileWriter csv;

	final static boolean assertOn = false;

	public static void main(String args[]) {

		System.out.println("QingModel");
		df = new DecimalFormat("#.###");

		// { // regress test VMs
		// // initializing VM templates in the order of cost rank
		// // small
		// VM sample = new VM();
		// sample.cores(1);
		// sample.cu(1);
		// sample.memory(1.7);
		// sample.platform(32);
		// sample.price(0.085);
		// VM.add_vmtype(sample);
		//		
		// // hp_med
		// sample = new VM();
		// sample.cores(2);
		// sample.cu(2.5);
		// sample.memory(1.7);
		// sample.platform(32);
		// sample.price(0.17);
		// VM.add_vmtype(sample);
		//
		// // large
		// sample = new VM();
		// sample.cores(2);
		// sample.cu(2);
		// sample.memory(7.5);
		// sample.platform(64);
		// sample.price(0.34);
		// VM.add_vmtype(sample);
		//
		// // hm_2xl
		// sample = new VM();
		// sample.cores(4);
		// sample.cu(13);
		// sample.memory(34.2);
		// sample.platform(64);
		// sample.price(1);
		// VM.add_vmtype(sample);
		// }
		// { // Jost Luis VM
		// // initializing VM templates in the order of cost rank
		// // small; cr = 0
		// VM sample = new VM();
		// sample.cores(1);
		// sample.cu(1);
		// sample.memory(1.7);
		// sample.platform(32);
		// sample.price(0.085);
		// VM.add_vmtype(sample);
		//
		// // hp_med; cr = 1
		// sample = new VM();
		// sample.cores(2);
		// sample.cu(2.5);
		// sample.memory(1.7);
		// sample.platform(32);
		// sample.price(0.17);
		// VM.add_vmtype(sample);
		//
		// // large; cr = 2
		// sample = new VM();
		// sample.cores(2);
		// sample.cu(2);
		// sample.memory(7.5);
		// sample.platform(64);
		// sample.price(0.34);
		// VM.add_vmtype(sample);
		//
		// // hp_xl; cr = 3
		// sample = new VM();
		// sample.price(0.68);
		// sample.memory(7);
		// sample.cu(20);
		// sample.cores(8);
		// sample.platform(64);
		// VM.add_vmtype(sample);
		//
		// // xl; cr = 4
		// sample = new VM();
		// sample.price(0.68);
		// sample.memory(15);
		// sample.cu(8);
		// sample.cores(4);
		// sample.platform(64);
		// VM.add_vmtype(sample);
		//
		// // hm_2xl; cr = 5
		// sample = new VM();
		// sample.price(1);
		// sample.memory(34.2);
		// sample.cu(13);
		// sample.cores(4);
		// sample.platform(64);
		// VM.add_vmtype(sample);
		//
		// // hm_4xl; cr = 6
		// sample = new VM();
		// sample.price(2);
		// sample.memory(68.4);
		// sample.cu(26);
		// sample.cores(8);
		// sample.platform(64);
		// VM.add_vmtype(sample);
		// }

		{ // CCGrid
			// initializing VM templates in the order of cost rank
			// small; cr = 0
			VM sample = new VM();
			sample.memory(1.7);
			sample.cores(1);
			sample.cu(1);
			sample.platform(64);
			sample.price(0.08);
			sample.mpl(14); // optimal mpl=14
			VM.add_vmtype(sample);

			// // hp-med; cr = 1
			// sample = new VM();
			// sample.cores(2);
			// sample.cu(2.5);
			// sample.memory(1.7);
			// sample.platform(64);
			// sample.price(0.165);
			// sample.mpl(115); // optimal mpl=20
			// VM.add_vmtype(sample);

			// large; cr = 2
			sample = new VM();
			sample.memory(7.5);
			sample.cores(2);
			sample.cu(2);
			sample.platform(64);
			sample.price(0.32);
			sample.mpl(75); // optimal mpl=75
			VM.add_vmtype(sample);

			// // hm-xl; cr = 3
			// sample = new VM();
			// sample.cores(2);
			// sample.cu(3.25);
			// sample.memory(17.1);
			// sample.platform(64);
			// sample.price(0.450);
			// sample.mpl(135); // optimal mpl=115
			// VM.add_vmtype(sample);

			// xlarge; cr = 4
			sample = new VM();
			sample.memory(15);
			sample.cores(4);
			sample.cu(2);
			sample.platform(64);
			sample.price(0.64);
			sample.mpl(115); // optimal mpl=115
			VM.add_vmtype(sample);
		}

		// /************************************************
		// * Sanity: 1-4
		// ***********************************************/
		// scenario1();
		// scenario2();
		// scenario3(); // work with original set of VMs
		// scenario4(); // dp replication and cost
		//		
		// /************************************************
		// * getCheapestConf: 5, 7
		// ***********************************************/
		// scenario5();
		// scenario7();
		//		
		// /************************************************
		// * greedOnInvestment: 6, 8
		// ***********************************************/
		// scenario6();
		// scenario8();
		//		
		// /************************************************
		// * adaptiveGreedy: 9, 10
		// ***********************************************/
		// scenario9();
		// scenario10();
		//		
		// /************************************************
		// * tabuSearch: 15-18
		// ***********************************************/
		// scenario15();
		// scenario16();
		//		
		// /************************************************
		// * Multi-class: 19-30
		// ***********************************************/
		// scenario19();
		// scenario20();
		// scenario21();
		//		
		// // penalty
		// scenario22();
		// scenario23();
		// scenario24();
		//		
		// // partition
		// scenario25();
		// scenario26();
		// scenario27();
		//		
		// // penalty + partition
		// scenario28();
		// scenario29();
		// scenario30();
		//		
		// /************************************************
		// * terminal transactions: 50-100
		// ***********************************************/
		// term_trans(); //<---- simple QNM
		//		
		// term_trans_multi_center(1);
		// for (int users = 4; users <= 20; users += 4)
		// term_trans_multi_center(users);
		//
		// term_trans_multi_class();
		//		
		// term_trans_multi_class_multi_center(1);
		// for (int users = 10; users <= 100; users += 10)
		// term_trans_multi_class_multi_center(users);
		//		
		// term_trans_multi_class_multi_center_book_example(0);
		//		 
		// //
		// // /************************************************
		// // * Validation: 100-200
		// // ***********************************************/
		// //
		// validation_closed_single(1);
		// for (int users = 3; users <= 15; users += 3)
		// validation_closed_single(users);
		//		
		// validation_closed_multi(1);
		// for (int users = 3; users <= 15; users += 3)
		// validation_closed_multi(users);
		//
		// demand_validation_closed(1);
		//		
		// // /************************************************
		// // * TPC-x predictions: 500 - 600
		// // ***********************************************/
		// tpcc(1);
		// for (int users = 10; users <= 100; users += 10)
		// tpcc(users);

		// tpch_1(8);

//		mgc();
		// algorithms();
//		noSLO();
		// extremeSLO();
//		noSLOBreachLP();
		mgcAlog();
		mgcAlogSomeViolations();
		// debug();

	}

	public static void init() {
		System.out.println("Resetting counters (config, VM, TC, DP) to 0");
		VM.resetVMCounter();
		Partition.resetCounters();
		TransactionClass.resetCounters();
		Config.resetCounter();
		Runtime.getRuntime().gc();
	}

	/************************************************
	 * Sanity: 1-4
	 ***********************************************/

	public static void scenario1() {
		init();
		int scenario = 1;
		System.out.println("scenario" + scenario);

		final double arrivalrate = 0.005; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		TransactionClass c2 = TransactionClass.createTC(arrivalrate);
		c2.Texe(0, 60); // small
		c2.Texe(1, 35); // hp-med
		c2.Texe(2, 40); // large
		c2.Texe(3, 10); // hp-xl

		c2.addDP(dp);
		Config conf = Algorithm.getCheapestConf(arrivalrate, dp, c2, scenario);

		System.out.println("scenario" + scenario + ": the cheapest conf (id="
				+ conf.id() + ") costs=$" + df.format(conf.cost()));

		if (assertOn)
			assertEquals("$Cost", "0.185", df.format(conf.cost()));
		System.out.println("=================================");
	}

	public static void scenario2() {
		init();
		int scenario = 2;
		System.out.println("scenario" + scenario);

		final double arrivalrate = 0.05; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		TransactionClass c2 = TransactionClass.createTC(arrivalrate);
		c2.Texe(0, 60); // small
		c2.Texe(1, 35); // hp-med
		c2.Texe(2, 40); // large
		c2.Texe(3, 10); // hp-xl

		c2.addDP(dp);

		Config conf = Algorithm.getCheapestConf(arrivalrate, dp, c2, scenario);
		System.out.println("scenario2: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		if (assertOn)
			assertEquals("$Cost", "0.27", df.format(conf.cost()));
		System.out.println("=================================");
	}

	public static void scenario3() {
		init();
		int scenario = 3;
		System.out.println("scenario" + scenario);

		final double workload = 1000;
		final double arrivalrate = 0.5; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		TransactionClass c2 = TransactionClass.createTC(arrivalrate);
		c2.Texe(0, 60); // small
		c2.Texe(1, 35); // hp-med
		c2.Texe(2, 40); // large
		c2.Texe(3, 10); // hp-xl
		c2.addDP(dp);

		Config conf = Algorithm.getCheapestConf(arrivalrate, dp, c2, scenario);
		if (conf != null) {
			System.out.println("scenario3: the cheapest conf (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			if (assertOn)
				assertEquals("$Cost", "0.78", df.format(conf.cost()));
		} else {
			System.out.println("scenario3: no config available to satisfy"
					+ " the arrival rate (" + arrivalrate + "t/s)!!!");
		}
		System.out.println("=================================");
	}

	public static void scenario4() {
		init();
		int scenario = 4;
		System.out.println("scenario" + scenario);

		final double arrivalrate = 0.005; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);

		TransactionClass c2 = TransactionClass.createTC(arrivalrate);
		c2.Texe(0, 60); // small
		c2.Texe(1, 35); // hp-med
		c2.Texe(2, 40); // large
		c2.Texe(3, 10); // hp-xl
		c2.addDP(dp);

		TransactionClass c3 = TransactionClass.createTC(arrivalrate);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl
		c3.addDP(dp);

		TransactionClass c4 = TransactionClass.createTC(arrivalrate);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		c4.addDP(dp);
		c4.addDP(dp2);

		Config conf = Config.createConfig();

		// create copies of partition and TransactionClass
		// but leave original untouched
		int vm_type = VM.ATOMIC_VM;
		VM vm1 = VM.createVM(vm_type);
		c2.addOnlyVM(vm1); // assign vm1 to class c2
		c3.addOnlyVM(vm1); // assign vm1 to class c3
		c4.addOnlyVM(vm1); // assign vm1 to class c4
		vm1.print();

		conf.addtc(c2); // add class c1 to config
		conf.addtc(c3); // add class c1 to config
		conf.addtc(c4);
		conf.adddp(dp);
		conf.adddp(dp2);
		conf.addvm(vm1); // add vm1 to config
		conf.print();

		CostModel.calculateCost(conf);
		Action.updateMoveCosts(conf);
		conf = Action.Upgrade.move(conf);

		CostModel.calculateCost(conf);
		Action.updateMoveCosts(conf);
		conf = Action.AddSame.move(conf);

		CostModel.calculateCost(conf);
		Action.updateMoveCosts(conf);
		conf = Action.AddCheapest.move(conf);

		double cost = Algorithm.get$CostOfConfig(conf);
		System.out.println("scenario4: the conf (id=" + conf.id() + ") costs=$"
				+ df.format(conf.cost()));
		conf.print();

		if (assertOn)
			assertEquals("$Cost", "0.825", df.format(conf.cost()));
		System.out.println("=================================");
	}

	/************************************************
	 * getCheapestConf: 5, 7
	 ***********************************************/

	public static void scenario5() {
		init();
		int scenario = 5;
		System.out.println("scenario" + scenario);

		final double arrivalrate = 0.005; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);
		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		Config conf = Algorithm.getCheapestConf(arrivalrate, dp, c1, scenario);
		if (conf != null) {
			System.out.println("scenario5: the cheapest conf (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			if (assertOn)
				assertEquals("$Cost", "0.085", df.format(conf.cost()));
		} else {
			System.out.println("scenario5: no config available to satisfy"
					+ " the arrival rate (" + arrivalrate + "t/s)!!!");
		}

		System.out.println("=================================");
	}

	public static void scenario7() {
		init();
		int scenario = 7;
		System.out.println("scenario" + scenario + ":getCheapestConf");

		final double arrivalrate = 0.05; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);

		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		Config conf = Algorithm.getCheapestConf(arrivalrate, dp, c1, scenario);
		System.out.println("scenario7: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		if (assertOn)
			assertEquals("Result", "0.17", df.format(conf.cost()));
		System.out.println("=================================");
	}

	/************************************************
	 * greedOnInvestment: 6, 8
	 ***********************************************/

	public static void scenario6() {
		init();
		int scenario = 6;
		System.out.println("scenario" + scenario + ":greedOnInvestment");

		final double arrivalrate = 0.005; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);
		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		Algorithm alog = new GreedOnInvestment(arrivalrate, c1, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario6: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		if (assertOn)
			assertEquals("Result", "0.085", df.format(conf.cost()));
		System.out.println("=================================");
	}

	public static void scenario8() {
		init();
		int scenario = 8;
		System.out.println("scenario" + scenario + ":greedOnInvestment");

		final double arrivalrate = 0.05; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);
		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		c1.print();

		Algorithm alog = new GreedOnInvestment(arrivalrate, c1, scenario);
		Config conf = alog.getCheapestConfig();

		if (conf != null) {
			System.out.println("scenario8: the cheapest conf (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			if (assertOn)
				assertEquals("$Cost", "0.17", df.format(conf.cost()));
		} else {
			System.out.println("scenario8: no config available to satisfy"
					+ " the arrival rate (" + arrivalrate + "t/s)!!!");
		}
		System.out.println("=================================");
	}

	/************************************************
	 * adaptiveGreedy: 9, 10
	 ***********************************************/
	public static void scenario9() {
		init();
		int scenario = 9;
		System.out.println("scenario" + scenario
				+ ":adaptiveGreedy_slc (penalty)");

		final double arrivalrate = 0.005; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);

		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		c1.desiredRespTime(5);
		c1.basepenalty(0.40);

		Algorithm alog = new AdaptiveGreedy(arrivalrate, c1, scenario);
		Config conf = alog.getCheapestConfig();
		conf.print();
		c1 = conf.gettc(c1.id());
		c1.print();
		System.out.println("scenario9: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		if (assertOn)
			assertEquals("Result", "0.485", df.format(conf.cost()));
		System.out.println("=================================");
	}

	public static void scenario10() {
		init();
		int scenario = 10;
		System.out.println("scenario" + scenario + ":adaptiveGreedy");

		final double arrivalrate = 0.05; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);
		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		Algorithm alog = new AdaptiveGreedy(arrivalrate, c1, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario10: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		if (assertOn)
			assertEquals("Result", "0.17", df.format(conf.cost()));
		System.out.println("=================================");
	}

	/************************************************
	 * tabuSearch: 15-18
	 ***********************************************/
	public static void scenario15() {
		init();
		int scenario = 15;
		System.out.println("scenario" + scenario + ":tsShort_single-class");

		final double arrivalrate = 0.005; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);
		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		Algorithm alog = new TSShort(arrivalrate, c1, dp, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario15: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		if (assertOn)
			assertEquals("Result", "0.085", df.format(conf.cost()));
		System.out.println("=================================");
	}

	public static void scenario16() {
		init();
		int scenario = 16;
		System.out.println("scenario" + scenario + ":tsShort_single-class");

		final double arrivalrate = 0.05; // unit: transaction per second

		Partition dp = null; // Partition.createPartition(0.8);
		TransactionClass c1 = TransactionClass.createTC(arrivalrate);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		Algorithm alog = new TSShort(arrivalrate, c1, dp, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario16: the cheapest conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		if (assertOn)
			assertEquals("Result", "0.17", df.format(conf.cost()));
		System.out.println("=================================");
	}

	/************************************************
	 * Multi-class: 19-30
	 ***********************************************/

	public static void scenario19() {
		init();
		int scenario = 19;
		System.out.println("scenario" + scenario
				+ ":multi-class greedOnInvestment");

		final double ar4 = 0.04; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.07; // unit: transaction per second

		TransactionClass c4 = TransactionClass.createTC(ar4);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		TransactionClass c5 = TransactionClass.createTC(ar5);
		c5.Texe(0, 90); // small
		c5.Texe(1, 40); // hp-med
		c5.Texe(2, 50); // large
		c5.Texe(3, 20); // hp-xl

		TransactionClass c6 = TransactionClass.createTC(ar6);
		c6.Texe(0, 60); // small
		c6.Texe(1, 35); // hp-med
		c6.Texe(2, 40); // large
		c6.Texe(3, 10); // hp-xl

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c4);
		tcs.add(c5);
		tcs.add(c6);
		tcs.add(c7);
		tcs.add(c8);

		Algorithm alog = new GreedOnInvestment(tcs, null, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario19: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "1.595", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario20() {
		init();
		int scenario = 20;
		System.out.println("scenario" + scenario
				+ ":multi-class adaptiveGreedy");

		final double ar4 = 0.04; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.07; // unit: transaction per second

		TransactionClass c4 = TransactionClass.createTC(ar4);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		TransactionClass c5 = TransactionClass.createTC(ar5);
		c5.Texe(0, 90); // small
		c5.Texe(1, 40); // hp-med
		c5.Texe(2, 50); // large
		c5.Texe(3, 20); // hp-xl

		TransactionClass c6 = TransactionClass.createTC(ar6);
		c6.Texe(0, 60); // small
		c6.Texe(1, 35); // hp-med
		c6.Texe(2, 40); // large
		c6.Texe(3, 10); // hp-xl

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c4);
		tcs.add(c5);
		tcs.add(c6);
		tcs.add(c7);
		tcs.add(c8);

		Algorithm alog = new AdaptiveGreedy(tcs, null, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario20: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "1.595", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario21() {
		init();
		int scenario = 21;
		System.out.println("scenario" + scenario + ":multi-class tsshort");

		final double w4 = 18;
		final double w5 = 9;
		final double ar4 = 0.04; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.07; // unit: transaction per second

		TransactionClass c4 = TransactionClass.createTC(ar4);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		TransactionClass c5 = TransactionClass.createTC(ar5);
		c5.Texe(0, 90); // small
		c5.Texe(1, 40); // hp-med
		c5.Texe(2, 50); // large
		c5.Texe(3, 20); // hp-xl

		TransactionClass c6 = TransactionClass.createTC(ar6);
		c6.Texe(0, 60); // small
		c6.Texe(1, 35); // hp-med
		c6.Texe(2, 40); // large
		c6.Texe(3, 10); // hp-xl

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c4);
		tcs.add(c5);
		tcs.add(c6);
		tcs.add(c7);
		tcs.add(c8);

		Algorithm alog = new TSShort(tcs, null, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario21: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "1.085", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario22() {
		init();
		int scenario = 22;
		System.out.println("scenario" + scenario
				+ ":multi-class greedOnInvestment (penalty)");

		final double ar4 = 0.004; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.02; // unit: transaction per second

		TransactionClass c4 = TransactionClass.createTC(ar4);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		c4.desiredRespTime(100);
		c4.basepenalty(0.20);

		TransactionClass c5 = TransactionClass.createTC(ar5);
		c5.Texe(0, 90); // small
		c5.Texe(1, 40); // hp-med
		c5.Texe(2, 50); // large
		c5.Texe(3, 20); // hp-xl

		c5.desiredRespTime(60);
		c5.basepenalty(0.30);

		TransactionClass c6 = TransactionClass.createTC(ar6);
		c6.Texe(0, 60); // small
		c6.Texe(1, 35); // hp-med
		c6.Texe(2, 40); // large
		c6.Texe(3, 10); // hp-xl

		c6.desiredRespTime(120);
		c6.basepenalty(0.80);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		c7.desiredRespTime(90);
		c7.basepenalty(0.40);

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		c8.desiredRespTime(40);
		c8.basepenalty(0.40);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c4);
		tcs.add(c5);
		tcs.add(c6);
		tcs.add(c7);
		tcs.add(c8);

		Algorithm alog = new GreedOnInvestment(tcs, null, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario22: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "2.04", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario23() {
		init();
		int scenario = 23;
		System.out.println("scenario" + scenario
				+ ":multi-class adaptiveGreedy  (penalty)");

		final double ar4 = 0.004; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.02; // unit: transaction per second

		TransactionClass c4 = TransactionClass.createTC(ar4);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		c4.desiredRespTime(100);
		c4.basepenalty(0.20);

		TransactionClass c5 = TransactionClass.createTC(ar5);
		c5.Texe(0, 90); // small
		c5.Texe(1, 40); // hp-med
		c5.Texe(2, 50); // large
		c5.Texe(3, 20); // hp-xl

		c5.desiredRespTime(60);
		c5.basepenalty(0.30);

		TransactionClass c6 = TransactionClass.createTC(ar6);
		c6.Texe(0, 60); // small
		c6.Texe(1, 35); // hp-med
		c6.Texe(2, 40); // large
		c6.Texe(3, 10); // hp-xl

		c6.desiredRespTime(120);
		c6.basepenalty(0.80);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		c7.desiredRespTime(90);
		c7.basepenalty(0.40);

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		c8.desiredRespTime(40);
		c8.basepenalty(0.40);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c4);
		tcs.add(c5);
		tcs.add(c6);
		tcs.add(c7);
		tcs.add(c8);

		Algorithm alog = new AdaptiveGreedy(tcs, null, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario23: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "2.04", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario24() {
		init();
		int scenario = 24;
		System.out.println("scenario" + scenario
				+ ":multi-class tsshort  (penalty)");

		final double ar4 = 0.004; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.02; // unit: transaction per second

		TransactionClass c4 = TransactionClass.createTC(ar4);
		c4.Texe(0, 60); // small
		c4.Texe(1, 35); // hp-med
		c4.Texe(2, 40); // large
		c4.Texe(3, 10); // hp-xl

		c4.desiredRespTime(100);
		c4.basepenalty(0.20);

		TransactionClass c5 = TransactionClass.createTC(ar5);
		c5.Texe(0, 90); // small
		c5.Texe(1, 40); // hp-med
		c5.Texe(2, 50); // large
		c5.Texe(3, 20); // hp-xl

		c5.desiredRespTime(60);
		c5.basepenalty(0.30);

		TransactionClass c6 = TransactionClass.createTC(ar6);
		c6.Texe(0, 60); // small
		c6.Texe(1, 35); // hp-med
		c6.Texe(2, 40); // large
		c6.Texe(3, 10); // hp-xl

		c6.desiredRespTime(120);
		c6.basepenalty(0.80);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		c7.desiredRespTime(90);
		c7.basepenalty(0.40);

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		c8.desiredRespTime(40);
		c8.basepenalty(0.40);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c4);
		tcs.add(c5);
		tcs.add(c6);
		tcs.add(c7);
		tcs.add(c8);

		Algorithm alog = new TSShort(tcs, null, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario24: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "0.34", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario25() {
		init();
		int scenario = 25;
		System.out.println("scenario" + scenario
				+ ":multi-class greedOnInvestment with partitions");

		final double ar4 = 0.03; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.01; // unit: transaction per second
		final double ar8 = 0.022; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);
		Partition dp3 = Partition.createPartition(0.9);

		TransactionClass c1 = TransactionClass.createTC(ar4);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl
		c1.addDP(dp);
		c1.addDP(dp2);

		TransactionClass c2 = TransactionClass.createTC(ar5);
		c2.Texe(0, 90); // small
		c2.Texe(1, 40); // hp-med
		c2.Texe(2, 50); // large
		c2.Texe(3, 20); // hp-xl
		c2.addDP(dp3);

		TransactionClass c3 = TransactionClass.createTC(ar6);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl
		c3.addDP(dp);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c7);
		tcs.add(c8);
		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);
		dps.add(dp2);
		dps.add(dp3);

		Algorithm alog = new GreedOnInvestment(tcs, dps, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario25: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "0.91", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario26() {
		init();
		int scenario = 26;
		System.out.println("scenario" + scenario
				+ ":multi-class adaptiveGreedy with partitions");

		final double ar4 = 0.03; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.01; // unit: transaction per second
		final double ar8 = 0.022; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);
		Partition dp3 = Partition.createPartition(0.9);

		TransactionClass c1 = TransactionClass.createTC(ar4);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl
		c1.addDP(dp);
		c1.addDP(dp2);

		TransactionClass c2 = TransactionClass.createTC(ar5);
		c2.Texe(0, 90); // small
		c2.Texe(1, 40); // hp-med
		c2.Texe(2, 50); // large
		c2.Texe(3, 20); // hp-xl
		c2.addDP(dp3);

		TransactionClass c3 = TransactionClass.createTC(ar6);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl
		c3.addDP(dp);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c7);
		tcs.add(c8);
		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);
		dps.add(dp2);
		dps.add(dp3);

		Algorithm alog = new AdaptiveGreedy(tcs, dps, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario26: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "0.91", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario27() {
		init();
		int scenario = 27;
		System.out.println("scenario" + scenario
				+ ":multi-class tsshort with partitions");

		final double ar4 = 0.03; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.01; // unit: transaction per second
		final double ar8 = 0.022; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);
		Partition dp3 = Partition.createPartition(0.9);

		TransactionClass c1 = TransactionClass.createTC(ar4);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl
		c1.addDP(dp);
		c1.addDP(dp2);

		TransactionClass c2 = TransactionClass.createTC(ar5);
		c2.Texe(0, 90); // small
		c2.Texe(1, 40); // hp-med
		c2.Texe(2, 50); // large
		c2.Texe(3, 20); // hp-xl
		c2.addDP(dp3);

		TransactionClass c3 = TransactionClass.createTC(ar6);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl
		c3.addDP(dp);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c7);
		tcs.add(c8);
		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);
		dps.add(dp2);
		dps.add(dp3);

		Algorithm alog = new TSShort(tcs, dps, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario27: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "1.25", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario28() {
		init();
		int scenario = 28;
		System.out.println("scenario" + scenario
				+ ":multi-class greedOnInvestment (penalty+partition)");

		final double ar4 = 0.004; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.02; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);
		Partition dp3 = Partition.createPartition(0.9);

		TransactionClass c1 = TransactionClass.createTC(ar4);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		c1.addDP(dp);
		c1.addDP(dp2);
		c1.desiredRespTime(100);
		c1.basepenalty(0.20);

		TransactionClass c2 = TransactionClass.createTC(ar5);
		c2.Texe(0, 90); // small
		c2.Texe(1, 40); // hp-med
		c2.Texe(2, 50); // large
		c2.Texe(3, 20); // hp-xl

		c2.addDP(dp3);
		c2.desiredRespTime(60);
		c2.basepenalty(0.30);

		TransactionClass c3 = TransactionClass.createTC(ar6);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl

		c3.addDP(dp);
		c3.desiredRespTime(60);
		c3.basepenalty(0.30);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		c7.desiredRespTime(90);
		c7.basepenalty(0.40);

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		c8.desiredRespTime(40);
		c8.basepenalty(0.40);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c7);
		tcs.add(c8);
		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);
		dps.add(dp2);
		dps.add(dp3);

		Algorithm alog = new GreedOnInvestment(tcs, dps, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario28: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "1.84", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario29() {
		init();
		int scenario = 29;
		System.out.println("scenario" + scenario
				+ ":multi-class adaptiveGreedy (penalty+partition)");

		final double ar4 = 0.004; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.02; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);
		Partition dp3 = Partition.createPartition(0.9);

		TransactionClass c1 = TransactionClass.createTC(ar4);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		c1.addDP(dp);
		c1.addDP(dp2);
		c1.desiredRespTime(100);
		c1.basepenalty(0.20);

		TransactionClass c2 = TransactionClass.createTC(ar5);
		c2.Texe(0, 90); // small
		c2.Texe(1, 40); // hp-med
		c2.Texe(2, 50); // large
		c2.Texe(3, 20); // hp-xl

		c2.addDP(dp3);
		c2.desiredRespTime(60);
		c2.basepenalty(0.30);

		TransactionClass c3 = TransactionClass.createTC(ar6);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl

		c3.addDP(dp);
		c3.desiredRespTime(60);
		c3.basepenalty(0.30);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		c7.desiredRespTime(90);
		c7.basepenalty(0.40);

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		c8.desiredRespTime(40);
		c8.basepenalty(0.40);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c7);
		tcs.add(c8);
		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);
		dps.add(dp2);
		dps.add(dp3);

		Algorithm alog = new AdaptiveGreedy(tcs, dps, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario29: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "1.84", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void scenario30() {
		init();
		int scenario = 30;
		System.out.println("scenario" + scenario
				+ ":multi-class tsshort (penalty+partition)");

		final double w4 = 18;
		final double w5 = 9;
		final double ar4 = 0.004; // unit: transaction per second
		final double ar5 = 0.01; // unit: transaction per second
		final double ar6 = 0.03; // unit: transaction per second
		final double ar7 = 0.005; // unit: transaction per second
		final double ar8 = 0.02; // unit: transaction per second

		Partition dp = Partition.createPartition(0.8);
		Partition dp2 = Partition.createPartition(0.9);
		Partition dp3 = Partition.createPartition(0.9);

		TransactionClass c1 = TransactionClass.createTC(ar4);
		c1.Texe(0, 60); // small
		c1.Texe(1, 35); // hp-med
		c1.Texe(2, 40); // large
		c1.Texe(3, 10); // hp-xl

		c1.addDP(dp);
		c1.addDP(dp2);
		c1.desiredRespTime(100);
		c1.basepenalty(0.20);

		TransactionClass c2 = TransactionClass.createTC(ar5);
		c2.Texe(0, 90); // small
		c2.Texe(1, 40); // hp-med
		c2.Texe(2, 50); // large
		c2.Texe(3, 20); // hp-xl

		c2.addDP(dp3);
		c2.desiredRespTime(60);
		c2.basepenalty(0.30);

		TransactionClass c3 = TransactionClass.createTC(ar6);
		c3.Texe(0, 60); // small
		c3.Texe(1, 35); // hp-med
		c3.Texe(2, 40); // large
		c3.Texe(3, 10); // hp-xl

		c3.addDP(dp);
		c3.desiredRespTime(60);
		c3.basepenalty(0.30);

		TransactionClass c7 = TransactionClass.createTC(ar7);
		c7.Texe(0, 90); // small
		c7.Texe(1, 40); // hp-med
		c7.Texe(2, 50); // large
		c7.Texe(3, 20); // hp-xl

		c7.desiredRespTime(90);
		c7.basepenalty(0.40);

		TransactionClass c8 = TransactionClass.createTC(ar8);
		c8.Texe(0, 60); // small
		c8.Texe(1, 35); // hp-med
		c8.Texe(2, 40); // large
		c8.Texe(3, 10); // hp-xl

		c8.desiredRespTime(40);
		c8.basepenalty(0.40);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c7);
		tcs.add(c8);
		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);
		dps.add(dp2);
		dps.add(dp3);

		Algorithm alog = new TSShort(tcs, dps, scenario);
		Config conf = alog.getCheapestConfig();

		System.out.println("scenario30: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		if (assertOn)
			assertEquals("$Cost", "0.64", df.format(conf.cost()));

		System.out.println("=================================");
	}

	/************************************************
	 * terminal transactions: 50-100
	 ***********************************************/
	public static void term_trans() {
		init();
		int scenario = 50;
		System.out.println("scenario" + scenario + ":terminal transactions");

		double tol = 0.001;

		int population = 8;
		double think = 1;

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		// data partition containing tpch database on 1dp
		Partition dp = Partition.createPartition(8);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		TransactionClass cluster0 = TransactionClass.createTC();
		cluster0.m_tolerance = tol;
		cluster0.population(population * 0.25);
		cluster0.think(1.0);
		cluster0.addDP(dp);

		TransactionClass cluster1 = TransactionClass.createTC();
		cluster1.think(1.0 / 6.0);
		cluster1.population(population * 0.25);
		cluster1.m_tolerance = tol;
		cluster1.addDP(dp);

		TransactionClass cluster2 = TransactionClass.createTC();
		cluster2.think(1.0 / 4.0);
		cluster2.population(population * 0.25);
		cluster2.m_tolerance = tol;
		cluster2.addDP(dp);

		TransactionClass cluster3 = TransactionClass.createTC();
		cluster3.think(1.0);
		cluster3.population(population * 0.25);
		cluster3.m_tolerance = tol;
		cluster3.addDP(dp);

		// micro; cr = 0
		cluster0.Texe(0, 20.664);
		cluster1.Texe(0, 113.852);
		cluster2.Texe(0, 235.227);
		cluster3.Texe(0, 415.148);

		// hm_xl; cr = 1
		cluster0.Texe(1, 0.639);
		cluster1.Texe(1, 5.153);
		cluster2.Texe(1, 9.724);
		cluster3.Texe(1, 30.392);

		// hp_xl; cr = 2
		cluster0.Texe(2, 0.799);
		cluster1.Texe(2, 6.499);
		cluster2.Texe(2, 12.414);
		cluster3.Texe(2, 38.098);

		/**
		 * penalty: threshold and value (start)
		 */

		// cluster1.desiredResTime(1);
		// cluster1.basepenalty(0.50);
		//
		// cluster2.desiredResTime(5);
		// cluster2.basepenalty(0.20);
		//
		// // threshold = transaction execution of hm-xl
		// cluster3.desiredResTime(10);
		// // penalty value = cost of hm-xl
		// cluster3.basepenalty(0.5);
		//
		// cluster4.desiredResTime(50);
		// cluster4.basepenalty(0.60);

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		// tcs.add(cluster1);
		// tcs.add(cluster2);
		// tcs.add(cluster3);
		tcs.add(cluster3);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);

		Config conf = null;

		/***********
		 * static config
		 **************/
		init();
		System.out.println("tpcc (static config)");

		conf = Config.createConfig();

		int vm_type = VM.COST_RANK_MAXED_OUT;
		VM vm1 = VM.createVM(vm_type);

		// assign vm to classes
		cluster0.addOnlyVM(vm1);
		cluster1.addOnlyVM(vm1);
		cluster2.addOnlyVM(vm1);
		cluster3.addOnlyVM(vm1);

		// assign classes and vm to conf
		conf.addtc(cluster0);
		conf.addtc(cluster1);
		conf.addtc(cluster2);
		conf.addtc(cluster3);
		conf.addvm(vm1); // add vm1 to config
		conf.adddp(dp);
		conf.print();

		CostModel.calculateCost(conf);

		cluster0.print();
		cluster1.print();
		cluster2.print();
		cluster3.print();

		vm1.print();
		conf.print();

		System.out.println("tpch (static config) (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("=================================");

	}

	public static void term_trans_multi_center(double pop) {
		init();
		int scenario = 51;
		System.out.println("scenario" + scenario
				+ ":terminal transactions_multi_center");

		int vm_type = VM.ATOMIC_VM; // 1 proc

		double population = pop;
		double proc_demand = 0.9843;
		double ioDemand = 6.1757;
		double think = 1;
		double tolerance = 0.001;

		TransactionClass request = TransactionClass.createTC();
		request.Texe(0, proc_demand); // small
		request.Texe(1, proc_demand); // hp-med
		request.Texe(2, proc_demand); // large
		request.Texe(3, proc_demand); // hp-xl

		request.ioDemand(vm_type, ioDemand);
		request.think(think);
		request.population(population);
		request.m_tolerance = tolerance;
		request.print();

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(request);

		Config conf = Config.createConfig();

		VM vm1 = VM.createVM(vm_type);
		// assign vm to class
		request.addOnlyVM(vm1);
		vm1.print();

		conf.addtc(request);

		conf.addvm(vm1); // add vm1 to config
		conf.print();

		CostModel.calculateCost(conf);

		System.out.println("scenario51: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (assertOn)
			assertEquals("$Cost", "0.085", df.format(conf.cost()));

		System.out.println("=================================");

	}

	public static void term_trans_multi_class() {
		init();
		int scenario = 60;
		System.out.println("scenario" + scenario
				+ ":terminal transactions multi-class");

		// int vm_type = VM.COST_RANK_MAXED_OUT; // multi-proc
		int vm_type = VM.ATOMIC_VM; // multi-proc
		double tol = 0.1;

		double ADemand = 1;
		TransactionClass A = TransactionClass.createTC();
		A.Texe(0, ADemand); // VM index 0
		A.Texe(1, ADemand); // VM index 1
		A.Texe(2, ADemand); // VM index 2

		A.think(3);
		A.population(5);
		A.m_tolerance = tol;
		A.print();

		double BDemand = 2;
		TransactionClass B = TransactionClass.createTC();
		B.Texe(0, BDemand);
		B.Texe(1, BDemand);
		B.Texe(2, BDemand);

		B.think(3);
		B.population(4);
		B.m_tolerance = tol;
		B.print();

		double CDemand = 0.5;
		TransactionClass C = TransactionClass.createTC();
		C.Texe(0, CDemand);
		C.Texe(1, CDemand);
		C.Texe(2, CDemand);

		C.think(3);
		C.population(1);
		C.m_tolerance = tol;
		C.print();

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(A);
		tcs.add(B);
		tcs.add(C);

		Config conf = Config.createConfig();

		// create copies of partition and TransactionClass
		// but leave original untouched
		VM vm1 = VM.createVM(vm_type);

		// assign vm to classes
		A.addOnlyVM(vm1);
		B.addOnlyVM(vm1);
		C.addOnlyVM(vm1);

		// assign classes and vm to conf
		conf.addtc(A);
		conf.addtc(B);
		conf.addtc(C);
		conf.addvm(vm1); // add vm1 to config

		CostModel.calculateCost(conf);

		A.print();
		B.print();
		C.print();
		vm1.print();
		conf.print();

		System.out.println("scenario60: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		if (assertOn)
			assertEquals("$Cost", "0.085", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void term_trans_multi_class_multi_center(double pop) {
		init();
		int scenario = 61;
		System.out.println("scenario" + scenario
				+ ":terminal transactions_multi class, multi center");

		int vm_type = VM.ATOMIC_VM; // 1 proc

		double tol = 0.001;
		double population = pop;

		TransactionClass A = TransactionClass.createTC();
		TransactionClass B = TransactionClass.createTC();
		TransactionClass C = TransactionClass.createTC();
		{
			// class A (new order)
			double Da_p = 0.0263; // DA,CPU
			double Da_d = 0.0047; // DA,disk
			double Na = 0.49 * population;
			double Za = 27.627;

			A.Texe(0, Da_p); // small

			A.ioDemand(vm_type, Da_d);
			A.population(Na);
			A.think(Za);
			A.m_tolerance = tol;
			A.print();
		}

		{
			// class B (Payment)
			double Db_p = 0.0147; // DB,CPU
			double Db_d = 0.0073; // DB,disk
			double Nb = 0.467 * population;
			double Zb = 12.6;

			B.Texe(0, Db_p); // small

			B.ioDemand(vm_type, Db_d);
			B.population(Nb);
			B.think(Zb);
			B.m_tolerance = tol;
			B.print();
		}

		{
			// class C (order-status)
			double Dc_p = 0.0068; // DB,CPU
			double Dc_d = 0.0002; // DB,disk
			double Nc = 0.043 * population;
			double Zc = 9.846;
			C.Texe(0, Dc_p); // small

			C.ioDemand(vm_type, Dc_d);
			C.population(Nc);
			C.think(Zc);
			C.m_tolerance = tol;
			C.print();
		}

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(A);
		tcs.add(B);
		tcs.add(C);

		Config conf = Config.createConfig();

		VM vm1 = VM.createVM(vm_type);
		// assign vm to class
		A.addOnlyVM(vm1);
		B.addOnlyVM(vm1);
		C.addOnlyVM(vm1);
		vm1.print();

		conf.addtc(A);
		conf.addtc(B);
		conf.addtc(C);

		conf.addvm(vm1); // add vm1 to config
		conf.print();

		CostModel.calculateCost(conf);

		System.out.println("scenario61: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		System.out.println("=================================");

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void term_trans_multi_class_multi_center_book_example(
			double pop) {
		init();
		int scenario = 61;
		System.out.println("scenario" + scenario
				+ ":terminal transactions_multi class, multi center");

		int vm_type = VM.ATOMIC_VM; // 1 proc

		double tol = 0.001;

		// class A (new order)
		double Da_p = 1; // DA,CPU
		double Da_d = 3; // DA,disk
		double Na = 1;
		double Za = 0;
		TransactionClass A = TransactionClass.createTC();
		A.Texe(0, Da_p); // small

		A.ioDemand(vm_type, Da_d);
		A.population(Na);
		A.think(Za);
		A.m_tolerance = tol;
		A.print();

		// class B (Payment)
		double Db_p = 2; // DB,CPU
		double Db_d = 4; // DB,disk
		double Nb = 1;
		double Zb = 0;
		TransactionClass B = TransactionClass.createTC();
		B.Texe(0, Db_p); // small

		B.ioDemand(vm_type, Db_d);
		B.population(Na);
		B.think(Za);
		B.m_tolerance = tol;
		B.print();

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(A);
		tcs.add(B);

		Config conf = Config.createConfig();

		VM vm1 = VM.createVM(vm_type);
		// assign vm to class
		A.addOnlyVM(vm1);
		B.addOnlyVM(vm1);
		vm1.print();

		conf.addtc(A);
		conf.addtc(B);

		conf.addvm(vm1); // add vm1 to config
		conf.print();

		CostModel.calculateCost(conf);

		System.out.println("scenario61: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		System.out.println("=================================");

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/************************************************
	 * Validation: 100-200
	 ***********************************************/

	public static void validation_open() {
		init();
		int scenario = 100;
		System.out.println("scenario" + scenario
				+ ":multi-class validation against benchmark using open model");

		final double w4 = 18;
		final double w5 = 9;

		int vm_type = VM.ATOMIC_VM; // 1 proc
		// int vm_type = VM.hm_2xl; // 4 proc
		// int vm_type = VM.hp_med; // 2 proc

		final double new_order_ar = 2.45; // unit: transaction per second
		// final double payment_ar = 0; // unit: transaction per second
		final double order_status_ar = 3; // unit: transaction per second
		final double delivery_ar = 4; // unit: transaction per second
		final double stock_level_ar = 5; // unit: transaction per second

		TransactionClass new_order = TransactionClass.createTC(new_order_ar);
		new_order.Texe(0, 60); // small
		new_order.Texe(1, 35); // hp-med
		new_order.Texe(2, 40); // large
		new_order.Texe(3, 10); // hp-xl

		// TransactionClass payment = TransactionClass.createTC(payment_ar, w5,
		// 0.999, 0.999, 0.999, 40, 20);

		// TransactionClass order_status =
		// TransactionClass.createTC(order_status_ar, w4, 0.005, 0.005, 0.005,
		// 35, 10);
		//
		// TransactionClass delivery = TransactionClass.createTC(delivery_ar,
		// w4, 0.251, 0.251, 0.251,
		// 35, 10);
		//
		// TransactionClass stock_level =
		// TransactionClass.createTC(stock_level_ar, w5, 0.001, 0.001, 0.001,
		// 40, 20);
		//

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(new_order);
		// tcs.add(payment);
		// tcs.add(order_status);
		// tcs.add(delivery);
		// tcs.add(stock_level);

		Config conf = Config.createConfig();

		// create copies of partition and TransactionClass
		// but leave original untouched
		VM vm1 = VM.createVM(vm_type);
		// assign vm to class
		new_order.addOnlyVM(vm1);
		// payment.addOnlyVM(vm1);
		// order_status.addOnlyVM(vm1);
		// delivery.addOnlyVM(vm1);
		// stock_level.addOnlyVM(vm1);
		vm1.print();

		conf.addtc(new_order);
		// conf.addtc(payment);
		// conf.addtc(order_status);
		// conf.addtc(delivery);
		// conf.addtc(stock_level);

		conf.addvm(vm1); // add vm1 to config
		conf.print();

		CostModel.calculateCost(conf);

		System.out.println("scenario100: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		if (assertOn)
			assertEquals("$Cost", "0.085", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void validation_closed_single(double pop) {
		init();
		int scenario = 150;
		System.out
				.println("scenario"
						+ scenario
						+ ":single-class validation against benchmark using approx mva");

		int vm_type = VM.ATOMIC_VM; // 1 proc

		double population = pop; // user load
		double tolerance = 0.001; // tolerance to decimal places (not %age)

		double ADemand = 20.664;
		double Athink = 1;

		TransactionClass A = TransactionClass.createTC();
		A.Texe(0, ADemand); // micro

		A.think(Athink);
		A.population(population);
		A.m_tolerance = tolerance;
		A.print();

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(A);

		Config conf = Config.createConfig();

		// create copies of partition and TransactionClass
		// but leave original untouched
		VM vm1 = VM.createVM(vm_type);

		// assign vm to classes
		A.addOnlyVM(vm1);

		// assign classes and vm to conf
		conf.addtc(A);
		conf.addvm(vm1); // add vm1 to config

		CostModel.calculateCost(conf);

		A.print();
		vm1.print();
		conf.print();

		System.out.println("scenario150: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		// if (assertOn)
		// assertEquals("$Cost", "0.085", df.format(conf.cost()));

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("=================================");
	}

	public static void validation_closed_multi(double pop) {
		init();
		int scenario = 152;
		System.out.println("scenario" + scenario
				+ ":multi-class validation against benchmark using approx mva");

		int vm_type = VM.ATOMIC_VM; // 1 proc

		double population = pop; // user load
		double tolerance = 0.001; // tolerance to decimal places (not %age)

		double cluster1 = 0.029; // c1
		double cluster2 = 0.025; // c2
		double cluster3 = 1.568; // c3
		double cluster4 = 2.101; // c4
		double cluster5 = 0.001; // c5

		double think = 0; // in seconds

		TransactionClass c1 = TransactionClass.createTC();
		c1.Texe(0, cluster1); // 1 machine

		c1.think(think);
		c1.population(population * .45);
		c1.m_tolerance = tolerance;
		c1.print();

		TransactionClass c2 = TransactionClass.createTC();
		c2.Texe(0, cluster2); // 1 machine

		c2.think(think);
		c2.population(population * .43);
		c2.m_tolerance = tolerance;
		c2.print();

		TransactionClass c3 = TransactionClass.createTC();
		c3.Texe(0, cluster3); // 1 machine

		c3.think(think);
		c3.population(population * .04);
		c3.m_tolerance = tolerance;
		c3.print();

		TransactionClass c4 = TransactionClass.createTC();
		c4.Texe(0, cluster4); // 1 machine

		c4.think(think);
		c4.population(population * .04);
		c4.m_tolerance = tolerance;
		c4.print();

		TransactionClass c5 = TransactionClass.createTC();
		c5.Texe(0, cluster5); // 1 machine

		c5.think(think);
		c5.population(population * .04);
		c5.m_tolerance = tolerance;
		c5.print();

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(c1);
		tcs.add(c2);
		tcs.add(c3);
		tcs.add(c4);
		tcs.add(c5);

		Config conf = Config.createConfig();

		// create copies of partition and TransactionClass
		// but leave original untouched
		VM vm1 = VM.createVM(vm_type);

		// assign vm to classes
		c1.addOnlyVM(vm1);
		c2.addOnlyVM(vm1);
		c3.addOnlyVM(vm1);
		c4.addOnlyVM(vm1);
		c5.addOnlyVM(vm1);

		// assign classes and vm to conf
		conf.addtc(c1);
		conf.addtc(c2);
		conf.addtc(c3);
		conf.addtc(c4);
		conf.addtc(c5);
		conf.addvm(vm1); // add vm1 to config

		CostModel.calculateCost(conf);

		c1.print();
		c2.print();
		c3.print();
		c4.print();
		c5.print();
		vm1.print();
		conf.print();

		System.out.println("scenario152: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		// if (assertOn)
		// assertEquals("$Cost", "0.085", df.format(conf.cost()));

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("=================================");
	}

	public static void demand_validation_closed(double pop) {
		init();
		int scenario = 151;
		System.out.println("scenario" + scenario
				+ ":service demand_validation_closed using approx mva");

		int vm_type = VM.ATOMIC_VM; // 1 proc

		double population = pop; // user load
		double tolerance = 0.001; // tolerance to decimal places (not %age)

		double ADemand = 0.034;
		double BDemand = 0.029;
		double CDemand = 1.861;
		double DDemand = 2.06;
		double EDemand = 0.001;

		double Athink = 28;
		double Bthink = 13;
		double Cthink = 10;
		double Dthink = 6;
		double Ethink = 6;

		TransactionClass aClass = TransactionClass.createTC();
		aClass.Texe(0, EDemand); // small

		aClass.think(Cthink);
		aClass.population(population);
		aClass.m_tolerance = tolerance;
		aClass.print();

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(aClass);

		Config conf = Config.createConfig();

		// create copies of partition and TransactionClass
		// but leave original untouched
		VM vm1 = VM.createVM(vm_type);

		// assign vm to classes
		aClass.addOnlyVM(vm1);

		// assign classes and vm to conf
		conf.addtc(aClass);
		conf.addvm(vm1); // add vm1 to config

		CostModel.calculateCost(conf);

		aClass.print();
		vm1.print();
		conf.print();

		System.out.println("scenario151: the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);
		// if (assertOn)
		// assertEquals("$Cost", "0.085", df.format(conf.cost()));

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("=================================");
	}

	/************************************************
	 * TPC-x predictions: 500 - 600
	 ***********************************************/

	public static void tpcc(double pop) {
		init();
		int scenario = 500;
		System.out.println("tpcc"
				+ ":multi-class predictions for tpcc benchmark");

		final double tol = 0.001;
		final double population = pop;

		// data partition containing tpcc database on 1dp
		Partition dp = Partition.createPartition(0.073);

		double penalty = 0.17;

		// creating transaction classes with provided service times and adding
		// single dp, desired response time and penalty for unit time

		TransactionClass new_order = TransactionClass.createTC();
		new_order.m_tolerance = tol;
		new_order.population(population * .45);
		new_order.think(28);
		new_order.addDP(dp);

		TransactionClass payment = TransactionClass.createTC();
		payment.think(13);
		payment.population(population * .43);
		payment.m_tolerance = tol;
		payment.addDP(dp);

		TransactionClass order_status = TransactionClass.createTC();
		order_status.think(9);
		order_status.population(population * .04);
		order_status.m_tolerance = tol;
		order_status.addDP(dp);

		TransactionClass delivery = TransactionClass.createTC();
		delivery.think(6);
		delivery.population(population * .04);
		delivery.m_tolerance = tol;
		delivery.addDP(dp);

		TransactionClass stock_level = TransactionClass.createTC();
		stock_level.think(6);
		stock_level.population(population * .04);
		stock_level.m_tolerance = tol;
		stock_level.addDP(dp);

		// small
		new_order.Texe(0, 3.87);
		payment.Texe(0, 0.844); // small
		order_status.Texe(0, 0.408); // small
		delivery.Texe(0, 9.094); // small
		stock_level.Texe(0, 0.218); // small

		// large
		new_order.Texe(2, 3.815);
		payment.Texe(2, 0.822); // large
		order_status.Texe(2, 0.395); // large
		delivery.Texe(2, 8.875); // large
		stock_level.Texe(2, 0.211); // large

		// hp-med
		new_order.Texe(1, 3.737);
		payment.Texe(1, 0.842); // hp-med
		order_status.Texe(1, 0.476); // hp-med
		delivery.Texe(1, 9.221); // hp-med
		stock_level.Texe(1, 0.254); // hp-med

		// hp-xl
		new_order.Texe(3, 4.623);
		payment.Texe(3, 1.054); // hp-xl
		order_status.Texe(3, 0.502); // hp-xl
		delivery.Texe(3, 11.428); // hp-xl
		stock_level.Texe(3, 0.27); // hp-xl

		int hp_med_index = 1;

		// threshold = transaction execution of hp-med
		new_order.desiredRespTime(new_order.Texe(hp_med_index));

		// penalty value = cost of hp-med
		new_order.basepenalty(penalty);

		payment.desiredRespTime(payment.Texe(hp_med_index));
		payment.basepenalty(penalty);

		order_status.desiredRespTime(order_status.Texe(hp_med_index));
		order_status.basepenalty(penalty);

		delivery.desiredRespTime(delivery.Texe(hp_med_index));
		delivery.basepenalty(penalty);

		stock_level.desiredRespTime(stock_level.Texe(hp_med_index));
		stock_level.basepenalty(stock_level.Texe(hp_med_index));

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(new_order);
		tcs.add(payment);
		tcs.add(order_status);
		tcs.add(delivery);
		tcs.add(stock_level);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);

		/***********
		 * static config
		 **************/
		init();
		System.out.println("tpcc (static config)");

		Config conf = Config.createConfig();

		int vm_type = VM.ATOMIC_VM; // 1 proc
		VM vm1 = VM.createVM(vm_type);

		// assign vm to classes
		new_order.addOnlyVM(vm1);
		payment.addOnlyVM(vm1);
		order_status.addOnlyVM(vm1);
		delivery.addOnlyVM(vm1);
		stock_level.addOnlyVM(vm1);

		// assign classes and vm to conf
		conf.addtc(new_order);
		conf.addtc(payment);
		conf.addtc(order_status);
		conf.addtc(delivery);
		conf.addtc(stock_level);
		conf.addvm(vm1); // add vm1 to config
		conf.adddp(dp);

		CostModel.calculateCost(conf);

		new_order.print();
		payment.print();
		order_status.print();
		delivery.print();
		stock_level.print();
		vm1.print();
		conf.print();

		System.out.println("tpcc (static config) (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();
		Algorithm.initCSV();
		Algorithm.finCSV(0, 0, conf, 1, -1);

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// alogs

		Algorithm alog = null;
		conf = null;

		init();
		System.out.println("tpcc (GreedOnInvestment)");
		alog = new GreedOnInvestment(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("tpcc (GreedOnInvestment): the conf (id="
				+ conf.id() + ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		init();
		System.out.println("tpcc (AdaptiveGreedy)");
		alog = new AdaptiveGreedy(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("tpcc (AdaptiveGreedy): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		init();
		System.out.println("tpcc (TSShort)");
		alog = new TSShort(tcs, dps, scenario);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpcc (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if (assertOn)
		// assertEquals("$Cost", "1.925", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void tpch_1(double pop) {
		init();
		int scenario = 510;
		System.out.println("tpch"
				+ ":multi-class predictions for tpch benchmark");

		final double tol = 0.001;
		final double population = pop;

		// data partition containing tpch database on 1dp
		Partition dp = Partition.createPartition(8);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		TransactionClass cluster0 = TransactionClass.createTC();
		cluster0.m_tolerance = tol;
		cluster0.population(population * 0.25);
		cluster0.think(1);
		cluster0.addDP(dp);

		TransactionClass cluster1 = TransactionClass.createTC();
		cluster1.think(1);
		cluster1.population(population * 0.25);
		cluster1.m_tolerance = tol;
		cluster1.addDP(dp);

		TransactionClass cluster2 = TransactionClass.createTC();
		cluster2.think(1);
		cluster2.population(population * 0.25);
		cluster2.m_tolerance = tol;
		cluster2.addDP(dp);

		TransactionClass cluster3 = TransactionClass.createTC();
		cluster3.think(1);
		cluster3.population(population * 0.25);
		cluster3.m_tolerance = tol;
		cluster3.addDP(dp);

		// micro; cr = 0
		cluster0.Texe(0, 20.664);
		cluster1.Texe(0, 113.852);
		cluster2.Texe(0, 235.227);
		cluster3.Texe(0, 415.148);

		// hm_xl; cr = 1
		cluster0.Texe(1, 0.639);
		cluster1.Texe(1, 5.153);
		cluster2.Texe(1, 9.724);
		cluster3.Texe(1, 30.392);

		// hp_xl; cr = 2
		cluster0.Texe(2, 0.799);
		cluster1.Texe(2, 6.499);
		cluster2.Texe(2, 12.414);
		cluster3.Texe(2, 38.098);

		/**
		 * penalty: threshold and value (start)
		 */

		cluster0.desiredRespTime(1);
		cluster0.basepenalty(0.10);

		cluster1.desiredRespTime(5);
		cluster1.basepenalty(0.20);

		// threshold = transaction execution of hm-xl
		cluster2.desiredRespTime(10);
		// penalty value = cost of hm-xl
		cluster2.basepenalty(0.50);

		cluster3.desiredRespTime(50);
		cluster3.basepenalty(0.60);

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(cluster0);
		tcs.add(cluster1);
		tcs.add(cluster2);
		tcs.add(cluster3);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp);

		Config conf = null;

		/***********
		 * static config
		 **************/
		// init();
		// System.out.println("tpch (static config)");
		//
		// conf = Config.createConfig();
		//		
		// int vm_type = VM.ATOMIC_VM;
		// int vm_type = VM.COST_RANK_MAXED_OUT;
		// VM vm1 = VM.createVM(vm_type);
		//
		// // assign vm to classes
		// cluster1.addOnlyVM(vm1);
		// cluster2.addOnlyVM(vm1);
		// cluster3.addOnlyVM(vm1);
		// cluster4.addOnlyVM(vm1);
		//
		// // assign classes and vm to conf
		// conf.addtc(cluster1);
		// conf.addtc(cluster2);
		// conf.addtc(cluster3);
		// conf.addtc(cluster4);
		// conf.addvm(vm1); // add vm1 to config
		// conf.adddp(dp);
		// conf.print();
		//
		// CostModel.calculateCost(conf);
		//
		// cluster1.print();
		// cluster2.print();
		// cluster3.print();
		// cluster4.print();
		//
		// vm1.print();
		// conf.print();
		//
		// System.out.println("tpch (static config) (id=" + conf.id()
		// + ") costs=$" + df.format(conf.cost()));
		// conf.printVMmap();
		// conf.printSLA();
		// conf.print();
		// Algorithm.initCSV();
		// Algorithm.finCSV(0, 0, conf, 1, -1);
		//
		// try {
		// FileWriter pred = new FileWriter("predictions.csv", true);
		// BufferedWriter out = new BufferedWriter(pred);
		// conf.printcsvVMmap(pred);
		// conf.printSLA(pred);
		// pred.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		/***********
		 * Algorithms
		 **************/
		Algorithm alog = null;
		conf = null;

		// GreedOnInvestment
		init();
		System.out.println("tpch (GreedOnInvestment)");
		alog = new GreedOnInvestment(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("tpch (GreedOnInvestment): the conf (id="
				+ conf.id() + ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// AdaptiveGreedy
		init();
		System.out.println("tpch (AdaptiveGreedy)");
		alog = new AdaptiveGreedy(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("tpch (AdaptiveGreedy): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TSShort
		// init();
		// System.out.println("tpch (TSShort)");
		// alog = new TSShort(tcs, dps, scenario);
		// conf = alog.getCheapestConfig();
		// conf.printVMmap();
		// conf.printSLA();
		// conf.print();
		//
		// System.out.println("tpch (TSShort): the conf (id=" + conf.id()
		// + ") costs=$" + df.format(conf.cost()));
		// conf.printVMmap();
		// conf.printSLA();
		// conf.print();
		//
		// try {
		// FileWriter pred = new FileWriter("predictions.csv", true);
		// BufferedWriter out = new BufferedWriter(pred);
		// conf.printcsvVMmap(pred);
		// conf.printSLA(pred);
		// pred.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// if (assertOn)
		// assertEquals("$Cost", "1.925", df.format(conf.cost()));

		System.out.println("=================================");
	}

	public static void populateLR() {
		LRPredictor lrp = LRPredictor.getInstance();

		int vm_type = 0; // small

		lrp.addLR(vm_type);

		for (int i = 0; i < SpecificModel.num_of_input_vars; i++) {
			lrp.initLRCoeff(vm_type, i, i, 0);
		}

		// lr for Q1
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.userload,
				23.4394);
		lrp
				.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.Q12,
						-219.0728);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.payment,
				149.3731);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.trade_order,
				-32.3843);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.constant,
				-7.4266);

		// lr for payment
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.userload,
				0.0004);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.Q21,
				0.019);
		lrp.initLRCoeff(vm_type, SpecificModel.payment,
				SpecificModel.trade_order, -0.0113);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.constant,
				0.0009);

		// lr for trade_update
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.userload, 0.0004);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update, SpecificModel.Q1,
				-0.0149);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update, SpecificModel.Q12,
				-0.0078);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update, SpecificModel.Q21,
				-0.1369);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.payment, 0.0846);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.trade_order, 0.1192);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.constant, 0.0069);

		vm_type = 1; // large

		lrp.addLR(vm_type);

		for (int i = 0; i < SpecificModel.num_of_input_vars; i++) {
			lrp.initLRCoeff(vm_type, i, i, 0);
		}

		// lr for Q1
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.userload,
				5.9646);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.Q6, 23.3417);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.Q21, -74.7793);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.payment,
				76.1616);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.constant,
				-6.9583);

		// lr for payment
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.userload,
				0.0053);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.Q12,
				0.0411);
		lrp.initLRCoeff(vm_type, SpecificModel.payment,
				SpecificModel.trade_order, -0.0155);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.constant,
				-0.0057);

		// lr for trade_update
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.userload, -0.0119);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update, SpecificModel.Q6,
				-0.2607);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.payment, -0.6161);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.constant, 0.1614);

		vm_type = 2; // xlarge

		lrp.addLR(vm_type);

		for (int i = 0; i < SpecificModel.num_of_input_vars; i++) {
			lrp.initLRCoeff(vm_type, i, i, 0);
		}

		// lr for Q1
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.userload,
				3.2489);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.Q1, 19.2889);
		lrp.initLRCoeff(vm_type, SpecificModel.Q1, SpecificModel.constant,
				-3.7632);

		// lr for payment
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.userload,
				0.0093);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.Q1,
				0.0207);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.Q21,
				0.0902);
		lrp.initLRCoeff(vm_type, SpecificModel.payment, SpecificModel.constant,
				-0.0213);

		// lr for trade_update
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.userload, 0.0255);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update, SpecificModel.Q6,
				0.0669);
		lrp.initLRCoeff(vm_type, SpecificModel.trade_update,
				SpecificModel.constant, -0.0279);

	}

	public static void populateSVR() {
		LRPredictor lrp = LRPredictor.getInstance();

		int vm_type = 0; // small

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/small_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/small_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/small_trade-update_tps.model");

		vm_type = 1; // hp-med

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/hp-med_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/hp-med_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/hp-med_trade-update_tps.model");

		vm_type = 2; // large

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/large_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/large_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/large_trade-update_tps.model");

		vm_type = 3; // hm-xl

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/hm-xl_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/hm-xl_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/hm-xl_trade-update_tps.model");

		vm_type = 4; // xlarge

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/xlarge_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/xlarge_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/xlarge_trade-update_tps.model");
	}

	public static void populateSVRStripped() {
		LRPredictor lrp = LRPredictor.getInstance();

		int vm_type = 0; // small

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/small_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/small_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/small_trade-update_tps.model");

		vm_type = 1; // large

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/large_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/large_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/large_trade-update_tps.model");

		vm_type = 2; // xlarge

		lrp.addSVM(vm_type);

		lrp.initSVR(vm_type, SpecificModel.Q1,
				"/home/mian/models_lhs@mpl/xlarge_Q1_resp.model");
		lrp.initSVR(vm_type, SpecificModel.payment,
				"/home/mian/models_lhs@mpl/xlarge_payment_tps.model");
		lrp.initSVR(vm_type, SpecificModel.trade_update,
				"/home/mian/models_lhs@mpl/xlarge_trade-update_tps.model");
	}

	public static void algorithms() {
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVR();

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpch = Partition.createPartition(1.92);
		Partition dp_tpcc = Partition.createPartition(1.08);
		Partition dp_tpce = Partition.createPartition(11.548);
		// Partition dp = Partition.createPartition(0);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		TransactionClass wl_a = TransactionClass.createTC();
		wl_a.addDP(dp_tpch);
		wl_a.wl_type(LRPredictor.wl_a);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		TransactionClass wl_c = TransactionClass.createTC();
		wl_c.addDP(dp_tpch);
		wl_c.addDP(dp_tpcc);
		wl_c.wl_type(LRPredictor.wl_c);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		CostModel.unitAccesses(LRPredictor.wl_a, 3, 128137); // hp-med
		CostModel.unitAccesses(LRPredictor.wl_b, 3, 4629925); // hp-med
		CostModel.unitAccesses(LRPredictor.wl_c, 3, 150575); // hp-med

		CostModel.unitAccesses(LRPredictor.wl_a, 4, 141391); // hm-xl
		CostModel.unitAccesses(LRPredictor.wl_b, 4, 292990); // hm-xl
		CostModel.unitAccesses(LRPredictor.wl_c, 4, 228073); // hm-xl

		/**
		 * penalty: threshold and value (start)
		 */

		// SLA on Q1
		wl_a.desiredRespTime(200);
		wl_a.basepenalty(0.05);

		// SLA on trade-update
		wl_b.desiredTPS(0.04);
		wl_b.basepenalty(0.15);

		// SLA on payment
		wl_c.desiredTPS(50);
		wl_c.basepenalty(0.10);

		try {
			FileWriter pred = new FileWriter("trail.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			// for (double penalty = 0; penalty <= 1; penalty+= 0.1) {
			// pred.write("payment_penalty=" + penalty +
			// "; grep keywords(exe)(cost)\n");
			// pred.flush();
			// wl_c.basepenalty(penalty);
			// algorithms_sensitivity_analysis(wl_a, wl_b, wl_c, dp_tpch,
			// dp_tpcc, dp_tpce);
			// }

			// varying penalty costs
			// for (int penalty_factor = 1; penalty_factor <= 5;
			// penalty_factor++) {
			// pred.write("penalty_factor=" + penalty_factor
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, memory(mb), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred.flush();
			// wl_a.basepenalty(wl_a.basepenalty() * penalty_factor);
			// wl_b.basepenalty(wl_b.basepenalty() * penalty_factor);
			// wl_c.basepenalty(wl_c.basepenalty() * penalty_factor);
			// algorithms_sensitivity_analysis(wl_a, wl_b, wl_c, dp_tpch,
			// dp_tpcc, dp_tpce);
			// }

			// varying SLO threshold
			for (double penalty_threshold = 0.5; penalty_threshold <= 5; penalty_threshold++) {
				pred.write("penalty_threshold=" + penalty_threshold
						+ ", grep keywords(exe)(cost)\n");
				pred
						.write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, memory(mb), grep keywords(exe)(cost)\n");
				pred
						.write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
				pred.flush();
				wl_a
						.desiredRespTime(wl_a.desiredRespTime()
								/ penalty_threshold);
				wl_b.desiredTPS(wl_b.desiredTPS() * penalty_threshold);
				wl_c.desiredTPS(wl_c.desiredTPS() * penalty_threshold);
				algorithms_sensitivity_analysis(wl_a, wl_b, wl_c, dp_tpch,
						dp_tpcc, dp_tpce);
			}

			// workloads
			// reset
			// SLA on Q1
			// wl_a.desiredRespTime(-1);
			// wl_a.basepenalty(-1); // $0.05
			//
			// // SLA on trade-update
			// wl_b.desiredTPS(-1);
			// wl_b.basepenalty(-1); // $0.15
			//
			// // SLA on payment
			// wl_c.desiredTPS(-1);
			// wl_c.basepenalty(-1); // $0.10
			//
			// // wl_a
			// int workload = LRPredictor.wl_a;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(wl_a, null, null, dp_tpch, null,
			// null);
			//
			// // wl_b
			// workload = LRPredictor.wl_b;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(null, wl_b, null, null, null,
			// dp_tpce);
			//
			// // wl_c
			// workload = LRPredictor.wl_c;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(null, null, wl_c, dp_tpch,
			// dp_tpcc,
			// null);
			//
			// // wl_ab
			// workload = LRPredictor.wl_a_b;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(wl_a, wl_b, null, dp_tpch, null,
			// dp_tpce);
			//
			// // wl_ac
			// workload = LRPredictor.wl_a_c;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(wl_a, null, wl_c, dp_tpch,
			// dp_tpcc,
			// null);
			//
			// // wl_bc
			// workload = LRPredictor.wl_b_c;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(null, wl_b, wl_c, dp_tpch,
			// dp_tpcc,
			// dp_tpce);
			//
			// // wl_abc
			// workload = LRPredictor.wl_a_b_c;
			// pred.write("workload(s)=" + LRPredictor.wl_type_str(workload)
			// + ", grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($), grep keywords(exe)(cost)\n");
			// pred
			// .write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima, grep keywords(exe)(cost)\n");
			// pred.flush();
			// algorithms_sensitivity_analysis(wl_a, wl_b, wl_c, dp_tpch,
			// dp_tpcc,
			// dp_tpce);

			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void algorithms_sensitivity_analysis(TransactionClass wl_a,
			TransactionClass wl_b, TransactionClass wl_c, Partition dp_tpch,
			Partition dp_tpcc, Partition dp_tpce) {

		int scenario = 520;
		Algorithm alog = null;
		Config conf = null;

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		if (wl_a != null)
			tcs.add(wl_a);
		if (wl_b != null)
			tcs.add(wl_b);
		if (wl_c != null)
			tcs.add(wl_c);

		Vector<Partition> dps = new Vector<Partition>();
		if (dp_tpch != null)
			dps.add(dp_tpch);
		if (dp_tpce != null)
			dps.add(dp_tpce);
		if (dp_tpcc != null)
			dps.add(dp_tpcc);

		/***********
		 * static config
		 **************/
		// init();
		// System.out.println("static config");
		//
		// conf = Config.createConfig();
		//
		// int vm_type = VM.ATOMIC_VM;
		// VM vm1 = VM.createVM(vm_type);
		// vm1.mpl(115);
		//
		// // assign vm to classes
		// wl_a.addOnlyVM(vm1);
		// wl_b.addOnlyVM(vm1);
		// wl_c.addOnlyVM(vm1);
		//
		// // assign classes and vm to conf
		// conf.addtc(wl_a);
		// conf.addtc(wl_b);
		// conf.addtc(wl_c);
		// conf.addvm(vm1); // add vm1 to config
		// conf.adddp(dp_tpch);
		// conf.adddp(dp_tpcc);
		// conf.adddp(dp_tpce);
		// conf.print();
		//
		// CostModel.calculateCost(conf);
		//
		// VM avm = conf.getMostUtilizedVM();
		// avm = conf.getHighestUtilityVM();
		// avm = conf.getBusietVM();
		//
		// wl_a.print();
		// wl_b.print();
		// wl_c.print();
		//
		// vm1.print();
		// conf.print();
		//
		// System.out.println("custom workloads (static config) (id=" +
		// conf.id()
		// + ") costs=$" + df.format(conf.cost()));
		// conf.printVMmap();
		// conf.printSLA();
		// conf.print();
		// Algorithm.initCSV();
		// Algorithm.finCSV(0, 0, conf, 1, -1);
		//
		// try {
		// FileWriter pred = new FileWriter("predictions.csv", true);
		// BufferedWriter out = new BufferedWriter(pred);
		// conf.printcsvVMmap(pred);
		// conf.printSLA(pred);
		// pred.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		/***********
		 * Algorithms
		 **************/
		// RandomAlg
		init();
		System.out.println("tpch (RandomAlg)");
		alog = new RandomAlg(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("RandomAlg the conf (id=" + conf.id() + ") costs=$"
				+ df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// GreedyNoSLOBreach
		init();
		System.out.println("tpch (GreedyNoSLOBreach)");
		alog = new GreedyNoSLOBreach(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("GreedyNoSLOBreach the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// GreedOnInvestment
		init();
		System.out.println("tpch (GreedOnInvestment)");
		alog = new GreedOnInvestment(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("GreedOnInvestment the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// AdaptiveRandom
		init();
		System.out.println("tpch (AdaptiveRandom)");
		alog = new AdaptiveRandom(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("tpch (AdaptiveRandom): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// AdaptiveGreedy
		init();
		System.out.println("tpch (AdaptiveGreedy)");
		alog = new AdaptiveGreedy(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		System.out.println("tpch (AdaptiveGreedy): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.GREEDY_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TSShort -- TABU_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.TABU_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TSShort -- TABU_GREEDY_HYBRID
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.TABU_GREEDY_HYBRID);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TSShort -- TABU_GREEDY_BACKFILL
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.TABU_GREEDY_BACKFILL);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// // if (assertOn)
		// // assertEquals("$Cost", "1.925", df.format(conf.cost()));
		//
		// System.out.println("=================================");
	}

	public static void noSLOBreach() {
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVR();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpch = Partition.createPartition(1.92);
		Partition dp_tpcc = Partition.createPartition(1.08);
		Partition dp_tpce = Partition.createPartition(11.548);
		// Partition dp = Partition.createPartition(0);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		TransactionClass wl_a = TransactionClass.createTC();
		wl_a.addDP(dp_tpch);
		wl_a.wl_type(LRPredictor.wl_a);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		TransactionClass wl_c = TransactionClass.createTC();
		wl_c.addDP(dp_tpch);
		wl_c.addDP(dp_tpcc);
		wl_c.wl_type(LRPredictor.wl_c);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		CostModel.unitAccesses(LRPredictor.wl_a, 3, 128137); // hp-med
		CostModel.unitAccesses(LRPredictor.wl_b, 3, 4629925); // hp-med
		CostModel.unitAccesses(LRPredictor.wl_c, 3, 150575); // hp-med

		CostModel.unitAccesses(LRPredictor.wl_a, 4, 141391); // hm-xl
		CostModel.unitAccesses(LRPredictor.wl_b, 4, 292990); // hm-xl
		CostModel.unitAccesses(LRPredictor.wl_c, 4, 228073); // hm-xl

		/**
		 * penalty: threshold and value (start)
		 */

		// SLA on Q1
		wl_a.desiredRespTime(300);
		wl_a.basepenalty(0.05); // $0.05

		// SLA on trade-update
		wl_b.desiredTPS(0.04);
		wl_b.basepenalty(0.15); // $0.15

		// SLA on payment
		wl_c.desiredTPS(50);
		wl_c.basepenalty(0.10); // $0.10

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_a);
		tcs.add(wl_b);
		tcs.add(wl_c);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpch);
		dps.add(dp_tpce);
		dps.add(dp_tpcc);

		/***********
		 * Algorithms
		 **************/
		// GreedOnInvestment
		init();
		System.out.println("tpch (GreedyNoSLOBreach)");
		alog = new GreedyNoSLOBreach(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		if (conf == null) {
			System.out
					.println("GreedyNoSLOBreach: non-SLO violating config using greedy heuristic not found");
		} else {
			System.out.println("GreedyNoSLOBreach the conf (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			conf.printVMmap();
			conf.printSLA();
			conf.print();

			try {
				FileWriter pred = new FileWriter("predictions.csv", true);
				BufferedWriter out = new BufferedWriter(pred);
				conf.printcsvVMmap(pred);
				conf.printSLA(pred);
				pred.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void noSLO() {
		// based on noSLOBreach()
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVRStripped();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpce = Partition.createPartition(11.548);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_b);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpce);

		/***********
		 * Algorithms
		 **************/
		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.GREEDY_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void extremeSLO() {
		// based on noSLOBreach()
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVRStripped();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpce = Partition.createPartition(11.548);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		/**
		 * penalty: threshold and value (start)
		 */

		// SLA on trade-update
		wl_b.desiredTPS(999);
		wl_b.basepenalty(0.15); // $0.15

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_b);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpce);

		/***********
		 * Algorithms
		 **************/
		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.GREEDY_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void noSLOBreachLP() {
		// based on noSLOBreach()
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVRStripped();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpce = Partition.createPartition(11.548);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		/**
		 * penalty: threshold and value (start)
		 */

		// SLA on trade-update
		wl_b.desiredTPS(0.30);
		wl_b.basepenalty(0.15); // $0.15

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_b);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpce);

		/***********
		 * Algorithms
		 **************/
		// GreedOnInvestment
		init();
		System.out.println("tpch (GreedyNoSLOBreach)");
		alog = new GreedyNoSLOBreach(tcs, dps, scenario);
		conf = alog.getCheapestConfig();

		if (conf == null) {
			System.out
					.println("GreedyNoSLOBreach: non-SLO violating config using greedy heuristic not found");
		} else {
			System.out.println("GreedyNoSLOBreach the conf (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			conf.printVMmap();
			conf.printSLA();
			conf.print();

			try {
				FileWriter pred = new FileWriter("predictions.csv", true);
				BufferedWriter out = new BufferedWriter(pred);
				conf.printcsvVMmap(pred);
				conf.printSLA(pred);
				pred.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.GREEDY_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void mgcAlog() {
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVRStripped();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpch = Partition.createPartition(1.92);
		Partition dp_tpcc = Partition.createPartition(1.08);
		Partition dp_tpce = Partition.createPartition(11.548);
		// Partition dp = Partition.createPartition(0);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		TransactionClass wl_a = TransactionClass.createTC();
		wl_a.addDP(dp_tpch);
		wl_a.wl_type(LRPredictor.wl_a);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		TransactionClass wl_c = TransactionClass.createTC();
		wl_c.addDP(dp_tpch);
		wl_c.addDP(dp_tpcc);
		wl_c.wl_type(LRPredictor.wl_c);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		// CostModel.unitAccesses(LRPredictor.wl_a, 3, 128137); // hp-med
		// CostModel.unitAccesses(LRPredictor.wl_b, 3, 4629925); // hp-med
		// CostModel.unitAccesses(LRPredictor.wl_c, 3, 150575); // hp-med
		//
		// CostModel.unitAccesses(LRPredictor.wl_a, 4, 141391); // hm-xl
		// CostModel.unitAccesses(LRPredictor.wl_b, 4, 292990); // hm-xl
		// CostModel.unitAccesses(LRPredictor.wl_c, 4, 228073); // hm-xl

		/**
		 * penalty: threshold and value (start)
		 */

		// SLA on Q1
		wl_a.desiredRespTime(200);
		wl_a.basepenalty(0.05); // $0.05

		// SLA on trade-update
		wl_b.desiredTPS(0.04);
		wl_b.basepenalty(0.15); // $0.15

		// SLA on payment
		wl_c.desiredTPS(50);
		wl_c.basepenalty(0.10); // $0.10

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_a);
		tcs.add(wl_b);
		tcs.add(wl_c);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpch);
		dps.add(dp_tpce);
		dps.add(dp_tpcc);

		/***********
		 * Algorithms
		 **************/
		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.GREEDY_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void mgcAlogSomeViolations() {
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVRStripped();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpch = Partition.createPartition(1.92);
		Partition dp_tpcc = Partition.createPartition(1.08);
		Partition dp_tpce = Partition.createPartition(11.548);
		// Partition dp = Partition.createPartition(0);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		TransactionClass wl_a = TransactionClass.createTC();
		wl_a.addDP(dp_tpch);
		wl_a.wl_type(LRPredictor.wl_a);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		TransactionClass wl_c = TransactionClass.createTC();
		wl_c.addDP(dp_tpch);
		wl_c.addDP(dp_tpcc);
		wl_c.wl_type(LRPredictor.wl_c);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		// CostModel.unitAccesses(LRPredictor.wl_a, 3, 128137); // hp-med
		// CostModel.unitAccesses(LRPredictor.wl_b, 3, 4629925); // hp-med
		// CostModel.unitAccesses(LRPredictor.wl_c, 3, 150575); // hp-med
		//
		// CostModel.unitAccesses(LRPredictor.wl_a, 4, 141391); // hm-xl
		// CostModel.unitAccesses(LRPredictor.wl_b, 4, 292990); // hm-xl
		// CostModel.unitAccesses(LRPredictor.wl_c, 4, 228073); // hm-xl

		/**
		 * penalty: threshold and value (start)
		 */

		// SLA on Q1
		wl_a.desiredRespTime(200);
		wl_a.basepenalty(0.05); // $0.05

		// SLA on trade-update
		wl_b.desiredTPS(0.06);
		wl_b.basepenalty(0.15); // $0.15

		// SLA on payment
		wl_c.desiredTPS(50);
		wl_c.basepenalty(0.10); // $0.10

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_a);
		tcs.add(wl_b);
		tcs.add(wl_c);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpch);
		dps.add(dp_tpce);
		dps.add(dp_tpcc);

		/***********
		 * Algorithms
		 **************/
		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new TSShort(tcs, dps, scenario, TSShort.GREEDY_CONSTRUCTS);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void debug() {
		init();
		int scenario = 520;
		System.out.println("lr_proto" + ":lpr predictions for mixed workload");
		// populateLR();
		populateSVRStripped();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpch = Partition.createPartition(1.92);
		Partition dp_tpcc = Partition.createPartition(1.08);
		Partition dp_tpce = Partition.createPartition(11.548);
		// Partition dp = Partition.createPartition(0);

		// creating request cluster classes with provided service times and
		// adding
		// single dp, desired response time and penalty for unit time

		// TransactionClass wl_a = TransactionClass.createTC();
		// wl_a.addDP(dp_tpch);
		// wl_a.wl_type(LRPredictor.wl_a);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		// TransactionClass wl_c = TransactionClass.createTC();
		// wl_c.addDP(dp_tpch);
		// wl_c.addDP(dp_tpcc);
		// wl_c.wl_type(LRPredictor.wl_c);

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137); // small
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925); // small
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575); // small

		CostModel.unitAccesses(LRPredictor.wl_a, 1, 129635); // large
		CostModel.unitAccesses(LRPredictor.wl_b, 1, 206796); // large
		CostModel.unitAccesses(LRPredictor.wl_c, 1, 122693); // large

		CostModel.unitAccesses(LRPredictor.wl_a, 2, 141391); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_b, 2, 292990); // xlarge
		CostModel.unitAccesses(LRPredictor.wl_c, 2, 228073); // xlarge

		// CostModel.unitAccesses(LRPredictor.wl_a, 3, 128137); // hp-med
		// CostModel.unitAccesses(LRPredictor.wl_b, 3, 4629925); // hp-med
		// CostModel.unitAccesses(LRPredictor.wl_c, 3, 150575); // hp-med
		//
		// CostModel.unitAccesses(LRPredictor.wl_a, 4, 141391); // hm-xl
		// CostModel.unitAccesses(LRPredictor.wl_b, 4, 292990); // hm-xl
		// CostModel.unitAccesses(LRPredictor.wl_c, 4, 228073); // hm-xl

		/**
		 * penalty: threshold and value (start)
		 */

		// // SLA on Q1
		// wl_a.desiredRespTime(300);
		// wl_a.basepenalty(0.05); // $0.05

		// SLA on trade-update
		wl_b.desiredTPS(0.04);
		wl_b.basepenalty(0.15); // $0.15

		// // SLA on payment
		// wl_c.desiredTPS(50);
		// wl_c.basepenalty(0.10); // $0.10

		/**
		 * penalty: threshold and value (end)
		 */

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		// tcs.add(wl_a);
		tcs.add(wl_b);
		// tcs.add(wl_c);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpch);
		dps.add(dp_tpce);
		dps.add(dp_tpcc);

		/***********
		 * static config
		 **************/
		// init();
		// System.out.println("static config");
		//		
		// conf = Config.createConfig();
		//		
		// int vm_type = VM.ATOMIC_VM;
		// VM vm1 = VM.createVM(vm_type);
		// vm1.mpl(115);
		//		
		// // assign vm to classes
		// wl_b.addOnlyVM(vm1);
		//		
		// // assign classes and vm to conf
		// conf.addtc(wl_b);
		// conf.addvm(vm1); // add vm1 to config
		// conf.adddp(dp_tpch);
		// conf.adddp(dp_tpcc);
		// conf.adddp(dp_tpce);
		// conf.print();
		//		
		// CostModel.calculateCost(conf);
		//		
		// VM avm = conf.getMostUtilizedVM();
		// avm = conf.getHighestUtilityVM();
		// avm = conf.getBusietVM();
		//		
		// wl_b.print();
		//		
		// vm1.print();
		// conf.print();
		//		
		// System.out.println("custom workloads (static config) (id=" +
		// conf.id()
		// + ") costs=$" + df.format(conf.cost()));
		// conf.printVMmap();
		// conf.printSLA();
		// conf.print();
		// Algorithm.initCSV();
		// Algorithm.finCSV(0, 0, conf, 1, -1);
		//		
		// try {
		// FileWriter pred = new FileWriter("predictions.csv", true);
		// BufferedWriter out = new BufferedWriter(pred);
		// conf.printcsvVMmap(pred);
		// conf.printSLA(pred);
		// pred.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		/***********
		 * Algorithms
		 **************/
		// TSShort -- GREEDY_CONSTRUCTS
		init();
		System.out.println("tpch (TSShort)");
		alog = new GreedOnInvestment(tcs, dps, scenario);
		conf = alog.getCheapestConfig();
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		System.out.println("tpch (TSShort): the conf (id=" + conf.id()
				+ ") costs=$" + df.format(conf.cost()));
		conf.printVMmap();
		conf.printSLA();
		conf.print();

		try {
			FileWriter pred = new FileWriter("predictions.csv", true);
			BufferedWriter out = new BufferedWriter(pred);
			conf.printcsvVMmap(pred);
			conf.printSLA(pred);
			pred.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mgc() {
		init();
		int scenario = 530;
		System.out.println("socc" + ":cost estimations for mixed workload");
		// populateLR();
		populateSVR();

		Algorithm alog = null;
		Config conf = null;

		// data partitions for tpch, tpcc and tpce
		Partition dp_tpch = Partition.createPartition(1.92);
		Partition dp_tpcc = Partition.createPartition(1.08);
		Partition dp_tpce = Partition.createPartition(11.548);

		TransactionClass wl_a = TransactionClass.createTC();
		wl_a.addDP(dp_tpch);
		wl_a.wl_type(LRPredictor.wl_a);

		TransactionClass wl_b = TransactionClass.createTC();
		wl_b.addDP(dp_tpce);
		wl_b.wl_type(LRPredictor.wl_b);

		TransactionClass wl_c = TransactionClass.createTC();
		wl_c.addDP(dp_tpch);
		wl_c.addDP(dp_tpcc);
		wl_c.wl_type(LRPredictor.wl_c);

		Vector<TransactionClass> tcs = new Vector<TransactionClass>();
		tcs.add(wl_a);
		tcs.add(wl_b);
		tcs.add(wl_c);

		Vector<Partition> dps = new Vector<Partition>();
		dps.add(dp_tpch);
		dps.add(dp_tpce);
		dps.add(dp_tpcc);

		int vm_type = 0;
		VM vm1 = null;

		CostModel.unitAccesses(LRPredictor.wl_a, VM.ATOMIC_VM, 128137);
		CostModel.unitAccesses(LRPredictor.wl_b, VM.ATOMIC_VM, 4629925);
		CostModel.unitAccesses(LRPredictor.wl_c, VM.ATOMIC_VM, 150575);

		/***********
		 * static config
		 **************/
		{
			init();
			System.out.println("use case: ab");

			vm_type = VM.ATOMIC_VM;
			vm1 = VM.createVM(vm_type);
			vm1.mpl(14); // specify optimal mpl

			conf = Config.createConfig();

			// assign vm to classes
			wl_a.addOnlyVM(vm1);
			wl_b.addOnlyVM(vm1);

			// assign classes and vm to conf
			conf.addtc(wl_a);
			conf.addtc(wl_b);
			conf.addvm(vm1); // add vm1 to config
			conf.adddp(dp_tpch);
			conf.adddp(dp_tpce);
			conf.print();

			CostModel.calculateCost(conf);

			wl_a.print();
			wl_b.print();

			vm1.print();
			conf.print();

			System.out.println("use case: ab (static config) (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			conf.printVMmap();
			conf.printSLA();
			conf.print();
			Algorithm.initCSV();
			Algorithm.finCSV(0, 0, conf, 1, -1);

			try {
				FileWriter pred = new FileWriter("predictions.csv", true);
				BufferedWriter out = new BufferedWriter(pred);
				conf.printcsvVMmap(pred);
				conf.printSLA(pred);
				pred.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/***********
		 * static config
		 **************/
		{
			init();
			System.out.println("use case: ac");

			vm_type = VM.ATOMIC_VM;
			vm1 = VM.createVM(vm_type);
			vm1.mpl(14); // specify optimal mpl

			conf = Config.createConfig();

			// assign vm to classes
			wl_a.addOnlyVM(vm1);
			wl_c.addOnlyVM(vm1);

			// assign classes and vm to conf
			conf.addtc(wl_a);
			conf.addtc(wl_c);
			conf.addvm(vm1); // add vm1 to config
			conf.adddp(dp_tpch);
			conf.adddp(dp_tpcc);
			conf.print();

			CostModel.calculateCost(conf);

			wl_a.print();
			wl_c.print();

			vm1.print();
			conf.print();

			System.out.println("use case: ac (static config) (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			conf.printVMmap();
			conf.printSLA();
			conf.print();
			Algorithm.initCSV();
			Algorithm.finCSV(0, 0, conf, 1, -1);

			try {
				FileWriter pred = new FileWriter("predictions.csv", true);
				BufferedWriter out = new BufferedWriter(pred);
				conf.printcsvVMmap(pred);
				conf.printSLA(pred);
				pred.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// /***********
		// * static config
		// **************/
		// {init();
		// System.out.println("use case: bc");
		//		
		// vm_type = VM.ATOMIC_VM;
		// vm1 = VM.createVM(vm_type);
		// vm1.mpl(14); // specify optimal mpl
		//
		// conf = Config.createConfig();
		//		
		// // assign vm to classes
		// wl_b.addOnlyVM(vm1);
		// wl_c.addOnlyVM(vm1);
		//
		// // assign classes and vm to conf
		// conf.addtc(wl_b);
		// conf.addtc(wl_c);
		// conf.addvm(vm1); // add vm1 to config
		// conf.adddp(dp_tpcc);
		// conf.adddp(dp_tpce);
		// conf.print();
		//		
		// CostModel.calculateCost(conf);
		//
		// wl_b.print();
		// wl_c.print();
		//
		// vm1.print();
		// conf.print();
		//
		// System.out.println("use case: bc (static config) (id=" + conf.id()
		// + ") costs=$" + df.format(conf.cost()));
		// conf.printVMmap();
		// conf.printSLA();
		// conf.print();
		// Algorithm.initCSV();
		// Algorithm.finCSV(0, 0, conf, 1, -1);
		//
		// try {
		// FileWriter pred = new FileWriter("predictions.csv", true);
		// BufferedWriter out = new BufferedWriter(pred);
		// conf.printcsvVMmap(pred);
		// conf.printSLA(pred);
		// pred.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }}

		/***********
		 * static config
		 **************/
		{
			init();
			System.out.println("use case: abc");

			vm_type = VM.ATOMIC_VM;
			vm1 = VM.createVM(vm_type);
			vm1.mpl(14); // specify optimal mpl

			conf = Config.createConfig();

			// assign vm to classes
			wl_a.addOnlyVM(vm1);
			wl_b.addOnlyVM(vm1);
			wl_c.addOnlyVM(vm1);

			// assign classes and vm to conf
			conf.addtc(wl_a);
			conf.addtc(wl_b);
			conf.addtc(wl_c);
			conf.addvm(vm1); // add vm1 to config
			conf.adddp(dp_tpch);
			conf.adddp(dp_tpcc);
			conf.adddp(dp_tpce);
			conf.print();

			CostModel.calculateCost(conf);

			wl_a.print();
			wl_b.print();
			wl_c.print();

			vm1.print();
			conf.print();

			System.out.println("use case: abc (static config) (id=" + conf.id()
					+ ") costs=$" + df.format(conf.cost()));
			conf.printVMmap();
			conf.printSLA();
			conf.print();
			Algorithm.initCSV();
			Algorithm.finCSV(0, 0, conf, 1, -1);

			try {
				FileWriter pred = new FileWriter("predictions.csv", true);
				BufferedWriter out = new BufferedWriter(pred);
				conf.printcsvVMmap(pred);
				conf.printSLA(pred);
				pred.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
