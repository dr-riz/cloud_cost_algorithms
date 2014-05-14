import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

abstract public class Algorithm {

	/**************************************
	 * static variables
	 **************************************/

	protected static DecimalFormat df;
	static FileWriter csv;
	static boolean opened = false;
	static long random_seed = 2;
	Random generator = new Random(random_seed);
	/**************************************
	 * member variables
	 **************************************/

	long m_step;
	Config m_conf; // current configuration
	Config m_confp; // new configuration
	Config m_minimum; // a minimum
	String m_name;

	double m_arrival_rate;
	TransactionClass m_c1;
	int m_scenario;
	boolean m_stopSearching = false;
	Vector<TransactionClass> m_tc = null;
	Vector<Partition> m_dp = null;

	long m_stepsInFindingMinimum = 0, m_counter = 0, m_numMinima = 0;
	long m_look_ahead = 10; // look ahead twice nice the <number of iterations>
	boolean m_minimumFound = false;
	double m_minimumCost = -1;
	int m_tenure = 3;
	Action m_chosenAction = null;
	long m_iterations_skipped = 0;
	long m_elapsed_nano_sec = 0;

	/**************************************
	 * member methods
	 **************************************/
	void init() {
		initCSV();
		Action.initAction(); // set counters to 0
		m_elapsed_nano_sec = System.nanoTime();
	}

	void fin() {
		m_elapsed_nano_sec = System.nanoTime() - m_elapsed_nano_sec;
		double execution_time_ms = (double) m_elapsed_nano_sec / (1000 * 1000);
		System.out.println("ITERATIONS SKIPPED=" + m_iterations_skipped);
		finCSV(m_step, m_iterations_skipped, m_minimum, m_numMinima,
				execution_time_ms, m_name);
		Action.printAll();
	}

	abstract void makeNextMove();

	/*************** step 1 *************/
	abstract void generateBaselineConfig();

	abstract public boolean toTerminate();

	abstract public boolean toSkipIter();

	/*************** step 3 *************/
	abstract void calcCostOf_new_conf();

	/*************** step 4 *************/
	abstract boolean bothConfigUnstable();

	/*************** step 5 *************/
	abstract boolean onlyCurrentConfigUnstable();

	/*************** step 6 *************/
	abstract boolean currentConfigStableButNewUnstable();

	/*************** step 7 *************/
	abstract boolean currentConfCostsLessThanNewConf();

	/**************************************
	 * Constructors
	 **************************************/
	Algorithm(double ar, TransactionClass tc, int use_case) {
		m_arrival_rate = ar;
		m_c1 = tc;
		m_scenario = use_case;

		m_step = 0;
		m_conf = null; // current configuration
		m_confp = null; // new configuration
		m_minimum = null; // a minimum
	}

	Algorithm(Vector<TransactionClass> tcs, Vector<Partition> dps, int use_case) {
		m_tc = tcs;
		m_dp = dps;
		m_scenario = use_case;

		m_step = 0;
		m_conf = null; // current configuration
		m_confp = null; // new configuration
		m_minimum = null; // a minimum

		// dont need the following params
		m_arrival_rate = -1;
		m_c1 = null;
	}

	// a template method
	public final Config getCheapestConfig() {
		init();
		/*************** step 1 *************/
		generateBaselineConfig();

		/*************** step 2 *************/
		while (true) { // keep searching for a good conf
			m_step++;
			this.makeNextMove();

			if (toSkipIter()) {
				continue;
			}

			if (toTerminate()) {
				this.fin();
				System.out.println("no action available, terminating...");
				return m_minimum;
			}

			/*************** step 3 *************/
			calcCostOf_new_conf();

			/*************** step 4 *************/
			if (this.bothConfigUnstable()) {
				continue;
			}

			/*************** step 5 *************/
			if (this.onlyCurrentConfigUnstable()) {
				continue;
			}

			/*************** step 6 *************/
			boolean curr_conf_stable_but_new_unstable = currentConfigStableButNewUnstable();
			if (m_stopSearching) {
				this.fin();
				return m_minimum;
			} else if (curr_conf_stable_but_new_unstable) {
				continue;
			}

			/*************** step 7 *************/
			boolean curr_conf_cost_lest_than_new = currentConfCostsLessThanNewConf();
			// Action.printAll();
			if (m_stopSearching) {
				this.fin();
				return m_minimum;
			} else if (curr_conf_cost_lest_than_new) {
				continue;
			}
		}
	}

	/*************** step 2 *************/
	public void takeNextCheapestStep() {
		System.out.println("---------ITERATION=" + m_step + " -------------");
		m_conf.printVMmap();

		PriorityQueue<Action> candidateMoves = Action
				.generateAllPossibleMovesOrderedOnCost(m_conf);

		candidateMoves = Action.printMoves(candidateMoves);

		if (candidateMoves.size() != 0) {
			m_chosenAction = candidateMoves.remove();
			if (m_chosenAction != null) {
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			}
		} else {
			m_chosenAction = null;
		}
	}

	public void takeNextRandomStep() {
		System.out.println("---------ITERATION=" + m_step + " -------------");
		m_conf.printVMmap();

		Vector<Action> candidateMoves = Action.generateAllPossibleMoves(m_conf);

		Action.printMoves(candidateMoves);

		if (candidateMoves.size() != 0) {
			int randomIndex = generator.nextInt(candidateMoves.size());
			m_chosenAction = candidateMoves.elementAt(randomIndex);
			if (m_chosenAction != null) {
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			}
		} else {
			m_chosenAction = null;
		}
	}

	/*************** step 4 *************/
	public boolean bothConfigUnstable_xn() {
		// if both current configs are unstable
		if (m_conf.unstable() && m_confp.unstable()) {
			System.out.println("both current (id=" + m_conf.id()
					+ ") and new config (id=" + m_confp.id()
					+ ") are unstable; " + "accept new one regardless\n");

			if (m_minimumFound) {
				m_counter++;
			}
			m_conf = m_confp;
			return true;
		} else {
			return false;
		}
	}

	/*************** step 5 *************/
	public boolean onlyCurrentConfigUnstable_xn() {
		// if only the current config is unstable,
		// accept new one
		if (m_conf.unstable() && !m_confp.unstable()) {
			System.out.println("current config(id=" + m_conf.id()
					+ ") is unstable, " + "so accept new config(id="
					+ m_confp.id() + ")\n");
			m_conf = m_confp;
			Action.action(m_confp.action()).lastLoweredCostAt(m_step);
			if (m_minimumFound) {
				m_counter++;
			}
			// look for another one
			return true;
		} else {
			return false;
		}
	}

	/*************** step 6 *************/
	public boolean currentConfigStableButNewUnstable_xn() {
		// if the new config is unstable, but the current one is stable hit
		// minimum

		boolean ret = false;

		if (m_confp.unstable() && !m_conf.unstable())// if the new config
		// is unstable,
		// but the current one is stable
		{
			ret = true;
			if (!m_minimumFound) {
				// first minimum
				m_minimumFound = true;
				m_counter = 1;
				m_stepsInFindingMinimum = m_step * m_look_ahead;
				m_numMinima = 1;

				System.out.println("ITERATION=" + m_step
						+ "; hit minimum current config (id=" + m_conf.id()
						+ ", " + m_conf.cost() + ") is stable where"
						+ " new config is unstable"
						+ ", so staring exploring for other minimums for "
						+ m_stepsInFindingMinimum + " steps");

				m_minimum = m_conf;
				m_minimum.minimumFound(true);
				m_minimumCost = m_minimum.cost();
				m_confp.minimumFound(true);
				m_conf = m_confp;
			} else {
				if (m_conf.cost() < m_minimum.cost()) {
					// hit new minimum
					m_numMinima++;
					m_stepsInFindingMinimum = m_counter * m_look_ahead; // reset
					// min
					// number
					// of
					// iterations
					// since last local minimum
					// iterations
					m_counter = 1; // reset counter

					System.out.println("ITERATION=" + m_step
							+ "; hit another minimum costing $"
							+ df.format(m_conf.cost())
							+ " where new minimum costs $"
							+ df.format(m_minimum.cost() - m_conf.cost())
							+ " less than old minimum ("
							+ df.format(m_minimum.cost())
							+ "), so staring exploring for other minimums for "
							+ m_stepsInFindingMinimum + " steps");

					m_minimum = m_conf;
					m_minimum.minimumFound(true);
					m_minimumCost = m_minimum.cost();
					m_confp.minimumFound(true);
					m_conf = m_confp;

				} else if (m_counter <= m_stepsInFindingMinimum) {
					m_counter++;
					m_conf = m_confp;
				} else {
					System.out
							.println("ITERATION="
									+ m_step
									+ "; lease expired, current minimum (id="
									+ m_minimum.id()
									+ ",$"
									+ df.format(m_minimum.cost())
									+ ") is stable;"
									+ " new configs are unstable for "
									+ m_stepsInFindingMinimum
									+ " steps;"
									+ " so suffice on the current minimum config with cost =$"
									+ df.format(m_minimum.cost()));
					m_stopSearching = true;
				}
			}
		}

		return ret;
	}

	/*************** step 7 *************/
	public boolean currentConfCostsLessThanNewConf_xn() {
		boolean ret = true;

		if (m_conf.cost() < m_confp.cost()) {
			// encountered minimum
			// exploring i_min more steps in hope of finding a better minimum
			ret = true;
			// m_conf.print();
			// Action.action(m_conf.action()).lastLoweredCostAt(m_step - 1);
			if (!m_minimumFound) {
				// Action.action(m_conf.action()).lastLoweredCostAt(m_step - 1);
				// first minimum
				m_minimumFound = true;
				m_counter = 1;
				m_stepsInFindingMinimum = m_step * m_look_ahead;
				m_numMinima = 1;

				System.out.println("ITERATION="
						+ m_step
						+ "; hit minimum costing $"
						+ df.format(m_conf.cost())
						+ " where new config costs $"
						// + df.format(costp - cost) + " more than minimum"
						+ df.format(m_confp.cost() - m_conf.cost())
						+ " more than minimum"
						+ ", so staring exploring for other minimums for "
						+ m_stepsInFindingMinimum + " steps");

				m_minimum = m_conf;
				m_minimum.minimumFound(true);
				m_minimumCost = m_minimum.cost();
				m_confp.minimumFound(true);
				m_conf = m_confp;
			} else {
				if (m_conf.cost() < m_minimum.cost()) {
					// hit new minimum
					m_numMinima++;
					m_stepsInFindingMinimum = m_counter * m_look_ahead; // reset
					// min
					// number
					// of
					// iterations
					// since last local minimum
					// iterations
					m_counter = 1; // reset counter

					System.out.println("ITERATION=" + m_step
							+ "; hit another minimum costing $"
							+ df.format(m_conf.cost())
							+ " where new minimum costs $"
							+ df.format(m_minimum.cost() - m_conf.cost())
							+ " less than old minimum ("
							+ df.format(m_minimum.cost())
							+ "), so staring exploring for other minimums for "
							+ m_stepsInFindingMinimum + " steps");

					m_minimum = m_conf;
					m_minimum.minimumFound(true);
					m_minimumCost = m_minimum.cost();
					m_confp.minimumFound(true);
					m_conf = m_confp;

				} else if (m_counter <= m_stepsInFindingMinimum) {
					m_counter++;
					m_conf = m_confp;
				} else {
					// new configs for i_min cost more than the
					// current
					// minimum, so return the
					// current minimum
					System.out.println("counter=" + m_counter);
					System.out
							.println("ITERATION="
									+ m_step
									+ "; lease expired, new config (id="
									+ m_confp.id()
									+ ",$"
									+ df.format(m_confp.cost())
									+ ") for "
									+ m_stepsInFindingMinimum
									+ " steps cost $"
									+ df.format((m_confp.cost() - m_minimum
											.cost()))
									+ " more than the current minimum (id="
									+ m_minimum.id()
									+ ", $"
									+ df.format(m_minimum.cost())
									+ ")"
									+ ", so sufficing on the current minimum config (id="
									+ m_minimum.id() + ")");
					m_stopSearching = true;
				}
			}

		} else {
			// current conf costs more than new_conf
			ret = false;
			Action.action(m_confp.action()).lastLoweredCostAt(m_step);
			if (m_minimumFound) {
				// do we want to stop if the (counter > min_steps?)
				// or overlook the value of counter if the cost is a drop
				if (m_counter <= m_stepsInFindingMinimum) {
					m_counter++;
				} else {
					// new configs for i_min cost more than the
					// current
					// minimum, so return the
					// current minimum
					System.out.println("counter=" + m_counter);
					System.out
							.println("ITERATION="
									+ m_step
									+ "; lease expired, new config (id="
									+ m_confp.id()
									+ ",$"
									+ df.format(m_confp.cost())
									+ ") for "
									+ m_stepsInFindingMinimum
									+ " steps cost $"
									+ df.format((m_confp.cost() - m_minimum
											.cost()))
									+ " more than the current minimum (id="
									+ m_minimum.id()
									+ ", $"
									+ df.format(m_minimum.cost())
									+ ")"
									+ ", so sufficing on the current minimum config (id="
									+ m_minimum.id() + ")");
					m_stopSearching = true;
					return ret;
				}
			}
			// set the new config to be the current one and generate
			// a new config
			System.out.println("set the new config (id=" + m_confp.id()
					+ ", cost=$" + df.format(m_confp.cost())
					+ ") to be the current config (id=" + m_conf.id() + "),"
					+ " and generate a new config (id=" + Config.counter()
					+ ")");

			m_conf = m_confp;
		}
		return ret;
	}

	/*************** step 2 *************/
	public void takeNextNonTabuedCheapestStep() {
		Action.updateTabuStatus();
		boolean backfill = true;
		Action.generateMovesUsingTabuConstructs(m_conf, backfill);

		VM cheapestVM = m_conf.getCheapestVM();
		int cheapest_vm_type_in_conf = cheapestVM.type();

		System.out.println("---------ITERATION=" + m_step + " -------------");
		System.out.println("NumberOfVMs in current config(id=" + m_conf.id()
				+ ") = " + m_conf.numOfVMs());

		// check if upgradable
		if (cheapestVM.costRank() == VM.COST_RANK_MAXED_OUT) {
			// generate a new config by just incrementing VM

			System.out.println("MAX_COST_RANK");

			if (!Action.AddCheapest.tabu()) {
				System.out.println("generating new config(id="
						+ Config.counter()
						+ ") with (only) incrementing NumberOfVMs to "
						+ (m_conf.numOfVMs() + 1));
				m_chosenAction = Action.AddCheapest;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else if (!Action.AddSame.tabu()) {
				System.out.println("generating new config(id="
						+ Config.counter()
						+ ") with (only) incrementing NumberOfVMs to "
						+ (m_conf.numOfVMs() + 1));
				m_chosenAction = Action.AddSame;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else {
				m_chosenAction = null;
			}
		} else if (m_conf.unstable()) { // current config is unstable
			int upgrade_vm_type = VM
					.upgradeVMtypeByCostRank(cheapest_vm_type_in_conf);
			System.out.print("UNSTABLE");

			if (!Action.Upgrade.tabu()) {
				System.out.println(" : generating new config(id="
						+ Config.counter() + ") by upgrading vmtype from "
						+ cheapest_vm_type_in_conf + " to " + upgrade_vm_type);
				m_chosenAction = Action.Upgrade;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else if (!Action.AddSame.tabu()) {
				System.out.print(" : generating new config(id="
						+ Config.counter() + ") by adding same vmtype ("
						+ cheapest_vm_type_in_conf
						+ ") as the cheapest in the config");
				System.out.println(" since Upgrade tabu");
				m_chosenAction = Action.AddSame;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else {
				m_chosenAction = null;
			}
		} else if (VM.vmUpgrade$CostRank(cheapest_vm_type_in_conf) <= (cheapestVM
				.$cost() * 2)) {
			// cost of upgrading vm is less than cost of adding a vm of the
			// current cheapest vm_type
			int upgrade_vm_type = VM
					.upgradeVMtypeByCostRank(cheapest_vm_type_in_conf);
			System.out.print("UPGRADE");

			if (!Action.Upgrade.tabu()) {
				System.out.println(": generating new config(id="
						+ Config.counter() + ") by upgrading vmtype from "
						+ cheapest_vm_type_in_conf + " to " + upgrade_vm_type);
				m_chosenAction = Action.Upgrade;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else if (!Action.AddCheapest.tabu()) {
				System.out.print(": generating new config(id="
						+ Config.counter() + ") by adding cheapest vmtype ("
						+ VM.cheapeast_vmType() + ") possible");
				System.out.println(" since Upgrade tabu");
				m_chosenAction = Action.AddCheapest;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else {
				m_chosenAction = null;
			}

		} else if (VM.vmUpgrade$CostRank(cheapest_vm_type_in_conf) > (cheapestVM
				.$cost() * 2)) {
			// cost of upgrading vm is more than cost of adding a vm of the
			// current cheapest vm_type
			System.out.print("INCREMENT");

			if (!Action.AddSame.tabu()) {
				System.out.println(": generating new config(id="
						+ Config.counter() + ") by incrementing vmtype of "
						+ cheapest_vm_type_in_conf);
				m_chosenAction = Action.AddSame;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else if (!Action.AddCheapest.tabu()) {
				System.out.print(": generating new config(id="
						+ Config.counter() + ") by adding cheapest vmtype ("
						+ VM.cheapeast_vmType() + ") possible");
				System.out.println(" since AddSame tabu");
				m_chosenAction = Action.AddCheapest;
				m_chosenAction.tabu(true);
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
				m_chosenAction.incrChosen();
			} else {
				m_chosenAction = null;
			}
		} else { // should never get here
			System.out.println("UNKNOWN CONDITION: EXITING!");
			System.exit(-1);
		}
	}

	public boolean toSkipIter_2n() {
		boolean ret = false;
		if (m_chosenAction == null) {
			if (!m_minimumFound) {
				if (!m_conf.unstable()) {
					// first minimum
					m_minimumFound = true;
					m_counter = 1;
					m_stepsInFindingMinimum = m_step * m_look_ahead;
					m_numMinima = 1;

					System.out
							.println("ITERATION="
									+ m_step
									+ "; hit minimum current config (id="
									+ m_conf.id()
									+ ", "
									+ m_conf.cost()
									+ ") is available but no new action is available to generate a new config"
									+ ", so staring exploring for other minimums for "
									+ m_stepsInFindingMinimum + " steps");
					m_conf.minimumFound(true);
					m_minimum = m_conf;
					m_minimumCost = m_minimum.cost();
				}
			} else { // minimum has been found previously
				m_counter++;
				if (m_counter >= m_stepsInFindingMinimum) {
					return false;
				}
			}
			ret = true;
			System.out
					.println(" !!! no move available to execute...skipping this iteration");
			m_iterations_skipped++;
		}
		return ret;
	}

	/**************************************
	 * logic methods
	 **************************************/

	public static double get$CostOfConfig(Config conf) {
		double cost = CostModel.calculateCost(conf);
		return cost;
	}

	public boolean minimumFound() {
		return m_minimumFound;
	}

	/**************************************
	 * Algorithms (brains)
	 **************************************/

	public static Config getCheapestConf(double ar, Partition dp,
			TransactionClass c1, int scenario) {
		long step = -1;

		Config conf = null;
		Config bestConf = conf;
		double cost = 0;
		double bestCost = 0;
		int vm_type = VM.ATOMIC_VM;
		boolean first_stale_values_set = false;

		initCSV();
		c1.print();

		while (vm_type < VM.typesOfVM()) {
			step++;

			System.out.println("generating new config(id=" + Config.counter()
					+ ") with vm_type=" + vm_type);
			if (step == 0) {
				conf = Config.generateConfig(ar, dp, vm_type, c1);
				VM vm = conf.getVM(0);
			} else {
				VM cheapestVM = conf.getCheapestVM();
				int cheapest_vm_type_in_conf = cheapestVM.type();
				conf = Config.generateConfigReplacingVM(conf, cheapestVM,
						vm_type);
			}
			cost = CostModel.calculateCost(conf);
			conf.print();

			conf.iteration(step);
			conf.cost(cost);
			printStep("getCheapestConf", scenario, conf, -1, 0);

			if (cost == -1) {
				System.out.println("system unstable, checking next vm\n");
				vm_type = VM.upgradeVMtype(vm_type);
				continue;
			}

			if (!first_stale_values_set) {
				first_stale_values_set = true;
				bestCost = cost;
				bestConf = conf;
			} else {
				if (cost < bestCost) {
					bestCost = cost;
					bestConf = conf;
				}
			}
			vm_type = VM.upgradeVMtype(vm_type);
		}

		return bestConf;
	}

	/************************************************
	 * helper methods
	 ***********************************************/
	protected static void initCSV() {
		df = new DecimalFormat("#.###");
		// Create file
		try {
			csv = new FileWriter("trail.csv", true);
			BufferedWriter out = new BufferedWriter(csv);
			if (!opened) {
				csv
						.write("AlgorithmType, scenario(id), iteration(step), config(id), "
								+ "#(partition), #(VMs), #(cores), util, thruput(t/s), R(s), "
								+ "cost($), action, $(minimum), #(minima)\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		opened = true;
	}
	
	protected static void finCSV(long last_iteration, long iterations_skipped,
			Config minimum, long numMinima, double execution_time_ms) {
		try {
			// Action.printcsv(csv, last_iteration);
			Action.printcsv(csv, last_iteration, iterations_skipped, minimum,
					numMinima, execution_time_ms, null);
			csv
					.write("AlgorithmType, scenario(id), iteration(step), config(id), "
							+ "#(partition), #(VMs), #(cores), util, thruput(t/s), R(s), "
							+ "cost($), action, $(minimum), #(minima)\n");
			csv.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static void finCSV(long last_iteration, long iterations_skipped,
			Config minimum, long numMinima, double execution_time_ms, String alogName) {
		try {
			// Action.printcsv(csv, last_iteration);
			Action.printcsv(csv, last_iteration, iterations_skipped, minimum,
					numMinima, execution_time_ms, alogName);
			csv
					.write("AlgorithmType, scenario(id), iteration(step), config(id), "
							+ "#(partition), #(VMs), #(cores), util, thruput(t/s), R(s), "
							+ "cost($), action, $(minimum), #(minima)\n");
			csv.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// iteration entry
	protected static void printStep(String str, int scenario, Config conf,
			double currentMin, long numMin) {
		try {
			csv.write(str + "," + scenario
					+ ","
					+ conf.iteration()
					+ ","
					+ conf.id()
					+ ","
					+ conf.numOfDP() // + conf.numOfReplicas()
					+ "," + conf.numOfVMs() + "," + conf.getNumberOfCores()
					+ "," + df.format(conf.utilization()) + ","
					+ df.format(conf.throughput()) + ","
					+ df.format(conf.responseTime()) + ","
					+ df.format(conf.cost()) + "," + conf.action() + ","
					+ df.format(currentMin) + "," + numMin + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}