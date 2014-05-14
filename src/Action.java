import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class Action {

	/**************************************
	 * static variables
	 **************************************/
	private static final int UPGRADE = 0; // upgrade m to higher cost_rank in V
	private static final int ADD_CHEAPEST = 1; // add cheapest vm_type to V
	private static final int ADD_SAME = 2; // add m' of same vm_type as m in V
	private static final int ADD_EXPENSIVE = 3; // add expensive vm_type to V
	private static final int DOWNGRADE = 4; // downgrade m to lesser cost_rank
	private static final int LOADBALANCE = 5; // loadbalance classes amongst vms
	private static final int DOWNSIZE = 6; // downsize by offloading work

	private static final int GENERATE_NEW = 7; // generate new config

	// upgrading vm
	public static final int NUM_OF_ACTIONS = 8; // ignoring Generate_new for now

	public static final Action Upgrade = new Action(UPGRADE);
	public static final Action AddCheapest = new Action(ADD_CHEAPEST);
	public static final Action AddSame = new Action(ADD_SAME);
	public static final Action AddExpensive = new Action(ADD_EXPENSIVE);
	public static final Action Downgrade = new Action(DOWNGRADE);
	public static final Action LoadBalance = new Action(LOADBALANCE);
	public static final Action Downsize = new Action(DOWNSIZE);
	public static final Action GenerateNew = new Action(GENERATE_NEW);

	// helper
	private static DecimalFormat df = new DecimalFormat("#.###");
	
	private static Random generator = new Random(Algorithm.random_seed);
	private static int random_vm_type = 0; //default

	/**************************************
	 * member variables
	 **************************************/

	private int m_action_type;
	private boolean m_tabu;
	private int m_tenure;
	private double m_cost;
	private boolean m_possible;
	private long m_last_iteration_lowered_cost; // recency
	private long m_times_lowered_cost; // quality
	private long m_times_considered; // quality
	private long m_times_chosen;	

	/**************************************
	 * constructors
	 **************************************/

	private Action() {
		;
	}

	private Action(int action) {
		m_action_type = action;
		m_tabu = false;
		m_tenure = 0;
		m_cost = -1;
		m_possible = true;
		m_last_iteration_lowered_cost = -1; // recency
		m_times_lowered_cost = 0; // quality
		m_times_considered = 0;
		m_times_chosen = 0;
	}

	public void init() {
		m_tabu = false;
		m_tenure = 0;
		m_cost = -1;
		m_possible = true;
		m_last_iteration_lowered_cost = -1; // recency
		m_times_lowered_cost = 0; // quality
		m_times_considered = 0;
		m_times_chosen = 0;
	}

	/**************************************
	 * accessor methods
	 **************************************/
	public int type() {
		return m_action_type;
	}

	public String name() {
		String ret = null;
		switch (m_action_type) {
		case (UPGRADE): {
			ret = new String("Upgrade (diversify)");
			break;
		}
		case (ADD_CHEAPEST): {
			ret = new String("AddCheapest");
			break;
		}
		case (ADD_SAME): {
			ret = new String("AddSame (intensify)");
			break;
		}
		case (ADD_EXPENSIVE): {
			ret = new String("AddExpensive");
			break;
		}
		case (DOWNGRADE): {
			ret = new String("Downgrade");
			break;
		}
		case (LOADBALANCE): {
			ret = new String("LoadBalance");
			break;
		}
		case (DOWNSIZE): {
			ret = new String("Downsize");
			break;
		}
		case (GENERATE_NEW): {
			ret = new String("GenerateNew (diversify)");
			break;
		}
		default:
			System.out.println("UNKNOWN ACTION...EXITING");
			System.exit(-1);
		}
		return ret;
	}

	public boolean tabu() {
		return m_tabu;
	}

	public void tabu(boolean t) {
		m_tabu = t;
	}

	public int tenure() {
		return m_tenure;
	}

	public void tenure(int duration) {
		m_tenure = duration + 1;
	}

	public void decTenure() {
		if (m_tenure > 0) {
			m_tenure--;
		}
	}

	public double cost() {
		return m_cost;
	}

	public void cost(double $) {
		m_cost = $;
	}

	public void lastLoweredCostAt(long iteration) {
		m_times_lowered_cost++; // quality
		m_last_iteration_lowered_cost = iteration; // recency
	}

	public long recency() {
		return m_last_iteration_lowered_cost;
	}

	public double quality() {
		double quality = -1; // indicating never chosen since 0 indicates never
		// improved quality
		if (m_times_chosen == 0) {
			return -1;
		} else {
			quality = ((double) m_times_lowered_cost)
					/ ((double) m_times_chosen);
		}
		return quality;
	}

	public boolean possible() {
		return m_possible;
	}

	public void incrChosen() {
		m_times_chosen++;
	}

	public long timesConsidered() {
		return m_times_considered;
	}

	public long timesChosen() {
		return m_times_chosen;
	}

	public long timesLoweredCost() {
		return m_times_lowered_cost;
	}

	/**************************************
	 * logic methods
	 **************************************/

	public static void initAction() {
		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			action.init();
		}
	}

	public static Action action(int act) {
		Action ret = null;
		switch (act) {
		case (UPGRADE): {
			ret = Upgrade;
			break;
		}
		case (ADD_CHEAPEST): {
			ret = AddCheapest;
			break;
		}
		case (ADD_SAME): {
			ret = AddSame;
			break;
		}
		case (ADD_EXPENSIVE): {
			ret = AddExpensive;
			break;
		}
		case (DOWNGRADE): {
			ret = Downgrade;
			break;
		}
		case (LOADBALANCE): {
			ret = LoadBalance;
			break;
		}
		case (DOWNSIZE): {
			ret = Downsize;
			break;
		}
		case (GENERATE_NEW): {
			ret = GenerateNew;
			break;
		}
		default:
			System.out.println("UNKNOWN ACTION...EXITING");
			System.exit(-1);
		}
		return ret;
	}

	public static void updateTabuStatus() {
		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			action.decTenure();
			if (action.tenure() == 0) {
				action.tabu(false);
			}
		}

	}

	public static Action getCheapestPossibleMove() {
		Action ret = Action.AddCheapest;
		double cheapestCost = ret.cost();
		for (int i = 1; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.cost() < cheapestCost && action.possible()) {
				ret = action;
			}
		}
		// System.out.println("getCheapestMove's Action=" + ret.type());
		return ret;
	}

	public static Action getExpensivePossibleMove() {
		Action ret = Action.AddExpensive;
		double expensiveCost = ret.cost();
		for (int i = 1; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.cost() > expensiveCost && action.possible()) {
				ret = action;
			}
		}
		// System.out.println("getCheapestMove's Action=" + ret.type());
		return ret;
	}

	public static Action getHighestQualityPossibleMove() {
		Action ret = Action.Upgrade;
		double quality = ret.quality();
		for (int i = 1; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.quality() > quality && action.possible()) {
				ret = action;
			}
		}

		if (quality == -1) {
			// none of the moves have been chosen before
			ret = null;

		}
		return ret;
	}

	public static Action getHighestChosenMove() {
		Action ret = Action.Upgrade;
		long chosen = ret.m_times_chosen;
		for (int i = 1; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.m_times_chosen > chosen && action.possible()) {
				ret = action;
			}
		}

		if (chosen == -1) {
			// none of the moves have been chosen before
			ret = null;

		}
		return ret;
	}

	public static Action getRecentPossibleMovethatLoweredCost() {
		Action ret = Action.Upgrade;
		long last = ret.recency();
		for (int i = 1; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if ((action.recency() > last) && action.possible()) {
				ret = action;
			}
		}

		if (last == -1) {
			// none of the moves have lowered cost before
			ret = null;

		}
		return ret;
	}

	public static Action getNeverConsideredPossibleMove() {
		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.m_times_considered == 0 && action.possible()) {
				return action;
			}
		}
		return null; // null indicates there is no such move
	}

	public static Action getNeverChosenPossibleMove() {
		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.m_times_chosen == 0 && action.possible()) {
				return action;
			}
		}
		return null; // null indicates there is no such move
	}

	public static Action getNeverLoweredCostMove() {
		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (action.possible() && action.m_times_lowered_cost == 0) {
				return action;
			}
		}
		return null; // null indicates there is no such move
	}

	/**************************************
	 * config generation (most interesting bit)
	 **************************************/

	public Config move(Config conf) {
		Config confp = null;
		m_times_considered++;
		switch (m_action_type) {
		case (UPGRADE): {
			System.out.println("move: UPGRADE");

			VM candidate = conf.getMostUtilizedVM();
			if (candidate.costRank() == VM.COST_RANK_MAXED_OUT) {
				candidate = conf.getSmallestCostRank_VM();
			}

			int upgrade_vm_type = VM.upgradeVMtypeByCostRank(candidate.type());
			if (!(upgrade_vm_type == -1 || candidate.costRank() == VM.COST_RANK_MAXED_OUT)) {
				confp = Config.generateConfigReplacingVM(conf, candidate,
						upgrade_vm_type);
			}
			break;
		}
		case (ADD_CHEAPEST): {
			System.out.println("move: ADD_CHEAPEST");
			confp = Config
					.generateConfigbyAddingVM(conf, VM.cheapeast_vmType());
			break;
		}
		case (ADD_SAME): {
			VM highest_utilized_vm = conf.getHighestUtilityVM();
			System.out.println("move: ADD_SAME");
			confp = Config.generateConfigbyAddingVM(conf, highest_utilized_vm
					.type());
			break;
		}
		case (ADD_EXPENSIVE): {
			System.out.println("move: ADD_EXPENSIVE");
			confp = Config.generateConfigbyAddingVM(conf, VM.expensive_Type());
			break;
		}
		case (DOWNGRADE): {
			VM lowest_utility_vm = conf.getLowestUtilityVM();
			System.out.println("move: DOWNGRADE");

			int downgrade_vm_type = VM
					.downgradeVMtypeByCostRank(lowest_utility_vm.type());

			if (!(downgrade_vm_type == -1 || lowest_utility_vm.costRank() == VM.COST_RANK_MINNED_OUT)) {
				confp = Config.generateConfigReplacingVM(conf,
						lowest_utility_vm, downgrade_vm_type);
			} else {
				// System.out.println("move: DOWNGRADE not possible");
			}
			break;
		}
		case (LOADBALANCE): {
			System.out.println("move: LOADBALANCE");
			if (conf.numOfVMs() > 1 && conf.getBusietVM() != null) {
				VM beneficiary = conf.getBusietVM();
				VM helper = conf.getLeastUtilizedVM();
				confp = Config.generateConfigbyLoadBalancing(conf, beneficiary,
						helper);
			} else {
				System.out.println("update status: LOADBALANCE not possible");
				System.exit(-1);
			}
			break;
		}
		case (DOWNSIZE): {
			System.out.println("move: DOWNSIZE");
			if (conf.numOfVMs() > 1) {
				VM useless_vm = conf.getLowestUtilityVM();
				VM least_utilized_vm = conf.getLeastUtilizedVM();
				VM most_useful_vm = conf.getHighestUtilityVM();
				if (useless_vm.id() != least_utilized_vm.id()) {
					confp = Config.generateConfigbyDownsizing(conf, useless_vm,
							least_utilized_vm);
				} else {
					confp = Config.generateConfigbyDownsizing(conf, useless_vm,
							most_useful_vm);
				}
			} else {
				System.out.println("update status: DOWNSIZE not possible");
				System.exit(-1);
			}

			break;
		}
		case (GENERATE_NEW): {
			System.out.println("move: GENERATE_NEW");			
			VM new_start = VM.createVM(Action.random_vm_type);

			confp = Config.createConfig();
			confp.copy(conf);
			confp.addvm(new_start);
			
			do {				
				List<Integer> sortedKeys=new ArrayList<Integer>(confp.getVMmap().keySet());
				Collections.sort(sortedKeys);

				VM candidateVM = confp.getVM(sortedKeys.get(0));
				VM takeover = confp.getVM(new_start.id());
				confp = Config.generateConfigbyDownsizing(confp, candidateVM,
						takeover);
			} while (confp.getVMmap().size()>1);		
			break;
		}
		default:
			System.out.println("UNKNOWN MOVE...EXITING");
			System.exit(-1);
		}
		if (confp != null) {
			confp.action(m_action_type);
		}
		return confp;
	}

	public static void updateMoveCosts(Config conf) {

		for (int i = 0; i < NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			switch (i) {
			case (UPGRADE): {
				VM candidate = conf.getMostUtilizedVM();
				if (candidate.costRank() == VM.COST_RANK_MAXED_OUT) {
					candidate = conf.getSmallestCostRank_VM();
				}
				if (candidate != null
						&& candidate.costRank() != VM.COST_RANK_MAXED_OUT) {
					double cost_of_highest_utilized_vm = VM.$costofVM(candidate
							.type());
					int upgrade_vm_type = VM.upgradeVMtypeByCostRank(candidate
							.type());
					double cost = VM.$costofVM(upgrade_vm_type)
							- cost_of_highest_utilized_vm;
					action.cost(cost);
					action.m_possible = true;
				} else {
					action.m_possible = false;
				}

				break;
			}
			case (ADD_CHEAPEST): {
				if (conf.numOfVMs() < conf.numOfTC()) {
					double cost = VM.$costofVM(VM.cheapeast_vmType());
					action.cost(cost);
					action.m_possible = true;
				} else {
					action.m_possible = false;
				}
				break;
			}
			case (ADD_SAME): {
				if (conf.numOfVMs() < conf.numOfTC()) {
					VM highest_utility_vm = conf.getHighestUtilityVM();
					double cost = VM.$costofVM(highest_utility_vm.type());
					action.cost(cost);
					action.m_possible = true;
				} else {
					action.m_possible = false;
				}
				break;
			}
			case (ADD_EXPENSIVE): {
				if (conf.numOfVMs() < conf.numOfTC()) {
					double cost = VM.$costofVM(VM.expensive_Type());
					action.cost(cost);
					action.m_possible = true;
				} else {
					action.m_possible = false;
				}
				break;
			}
			case (DOWNGRADE): {
				VM lowest_utility_vm = conf.getLowestUtilityVM();
				if (lowest_utility_vm.costRank() != VM.COST_RANK_MINNED_OUT) {
					double cost_of_lowest_utilized_vm = VM
							.$costofVM(lowest_utility_vm.type());
					int downgrade_vm_type = VM
							.downgradeVMtypeByCostRank(lowest_utility_vm.type());
					double cost = VM.$costofVM(downgrade_vm_type)
							- cost_of_lowest_utilized_vm;
					action.cost(cost);
					action.m_possible = true;
				} else {
					// System.out.println("update status: DOWNGRADE not possible");
					action.m_possible = false;
				}

				break;
			}
			case (LOADBALANCE): {
				if (conf.numOfVMs() > 1 && conf.getBusietVM() != null) {
					VM beneficiary = conf.getBusietVM();
					VM helper = conf.getLeastUtilizedVM();
					if (beneficiary.id() != helper.id()) {
						// no investment cost
						double cost = 0;
						action.cost(cost);
						action.m_possible = true;
					} else {
						action.m_possible = false;
					}
				} else {
					// System.out.println("update status: LOADBALANCE not possible");
					action.m_possible = false;
				}
				break;
			}
			case (DOWNSIZE): {
				if (conf.numOfVMs() > 1) {
					VM useless_vm = conf.getLowestUtilityVM();
					VM least_utilized_vm = conf.getLeastUtilizedVM();
					VM most_useful_vm = conf.getHighestUtilityVM();

					if (useless_vm.id() != least_utilized_vm.id()
							|| useless_vm.id() != most_useful_vm.id()) {

						double saving = VM.$costofVM(useless_vm.type());

						// -ve since getting rid of a vm
						double cost = -saving;
						action.cost(cost);
						action.m_possible = true;
					} else {
						action.m_possible = false;
					}
				} else {
					// System.out.println("update status: DOWNGRADE not possible");
					action.m_possible = false;
				}

				break;
			}

			case (GENERATE_NEW): {
				Action.random_vm_type = Action.generator.nextInt(VM.numOfVMTypes());
											
				double cost = VM.$costofVM(Action.random_vm_type) - conf.vm_cost();
				
				action.cost(cost);
				action.m_possible = true;
				break;
			}
			default:
				System.out.println("UNKNOWN ACTION...EXITING");
				System.exit(-1);
			}
		}

	}

	public static LinkedHashMap<Integer, Boolean> generateMovesUsingTabuConstructs(
			Config conf, boolean backfill) {
		// System.out.println("generateMoves");
		updateMoveCosts(conf);

		// using LinkedHashMap to maintain order
		LinkedHashMap<Integer, Boolean> moves = new LinkedHashMap<Integer, Boolean>();

		/**
		 * intensify
		 */

		// recency
		Action recent = Action.getRecentPossibleMovethatLoweredCost();
		if (isvalidMove(recent, conf) && (moves.get(recent.type()) == null)) {
			moves.put(recent.type(), true);
		}

		// quality

		Action quality = Action.getHighestQualityPossibleMove();
		if (isvalidMove(quality, conf) && (moves.get(quality.type()) == null)) {
			moves.put(quality.type(), true);
		}

		// frequency

		Action frequent = Action.getHighestChosenMove();
		if (isvalidMove(frequent, conf) && (moves.get(frequent.type()) == null)) {
			moves.put(frequent.type(), true);
		}

		/*
		 * diversify
		 */

		Action unchosen = Action.getNeverChosenPossibleMove();
		if (isvalidMove(unchosen, conf) && (moves.get(unchosen.type()) == null)) {
			moves.put(unchosen.type(), true);
		}

		Action neverImprove = Action.getNeverLoweredCostMove();
		if (isvalidMove(neverImprove, conf)
				&& (moves.get(neverImprove.type()) == null)) {
			moves.put(neverImprove.type(), true);
		}

		/*
		 * intensify/diversify depending on the VMs in the current config
		 */

		Action cheapest = Action.getCheapestPossibleMove();
		if (isvalidMove(cheapest, conf)) {
			moves.put(cheapest.type(), true);
		}

		Action expensive = Action.getExpensivePossibleMove();
		if (isvalidMove(expensive, conf)
				&& (moves.get(expensive.type()) == null)) {
			moves.put(expensive.type(), true);
		}

		/**
		 * plan b: if list is still empty
		 */
		if (backfill && moves.size() == 0) {
			System.out
					.println("generateMovesUsingTabuConstructs: no moves available with Tabu constructs, so backfill with greedy");
			PriorityQueue<Action> queue = generateAllPossibleMovesOrderedOnCost(conf);

			while (queue.size() != 0) {
				Action action = queue.remove();
				action.print();
				moves.put(action.type(), true);
			}
		}

		return moves;
	}

	public static LinkedHashMap<Integer, Boolean> generateMovesOrderedByCostUsingTabu(
			Config conf) {

		boolean backfill = true;
		LinkedHashMap<Integer, Boolean> candidateMoves = Action
				.generateMovesUsingTabuConstructs(conf, backfill);

		Set moves = candidateMoves.entrySet();
		Iterator move_iter = moves.iterator();

		Comparator<Action> comparator = new MoveContainer();
		PriorityQueue<Action> queue = new PriorityQueue<Action>(10, comparator);

		while (move_iter.hasNext()) {
			Map.Entry move_entry = (Map.Entry) move_iter.next();
			int move_type = (Integer) move_entry.getKey(); // (Integer)

			Action action = Action.action(move_type);
			queue.add(action);

		}

		LinkedHashMap<Integer, Boolean> ret = new LinkedHashMap<Integer, Boolean>();

		while (queue.size() != 0) {
			Action action = queue.remove();
			ret.put(action.type(), true);
		}

		return ret;
	}

	public static PriorityQueue<Action> generateAllPossibleMovesOrderedOnCost(
			Config conf) {

		updateMoveCosts(conf);
		Comparator<Action> comparator = new MoveContainer();
		PriorityQueue<Action> queue = new PriorityQueue<Action>(10, comparator);

		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (isvalidMove(action, conf)) {
				queue.add(action);
			}
		}

		return queue;
	}

	public static LinkedHashMap<Integer, Boolean> generateAllPossibleMovesOrderedOnCost_LH(
			Config conf) {

		updateMoveCosts(conf);
		Comparator<Action> comparator = new MoveContainer();
		PriorityQueue<Action> queue = new PriorityQueue<Action>(10, comparator);

		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (isvalidMove(action, conf)) {
				queue.add(action);
			}
		}

		LinkedHashMap<Integer, Boolean> ret = new LinkedHashMap<Integer, Boolean>();

		while (queue.size() != 0) {
			Action action = queue.remove();
			ret.put(action.type(), true);
		}

		return ret;
	}
	
	public static Vector<Action> generateAllPossibleMoves(Config conf) {
		
		updateMoveCosts(conf);
		Vector<Action> moves = new Vector<Action>();
		for (int i = 0; i < Action.NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			if (isvalidMove(action, conf)) {
				moves.add(action);
			}
		}

		return moves;
	}

	public static boolean isvalidMove(Action act, Config conf) {
		boolean valid = true;

		if (act == null) {
			valid = false;
		} else {

			switch (act.type()) {
			case (UPGRADE): {
				valid = act.possible();
				break;
			}
			case (LOADBALANCE): {
				if (!act.possible() || conf.unstable() || conf.numOfVMs() == 1
						|| !conf.minimumFound()) {
					valid = false;
				}
				break;
			}
			case (DOWNGRADE): {
				if (!act.possible() || conf.unstable() || !conf.minimumFound()) {
					valid = false;
				}
				break;
			}
			case (DOWNSIZE): {
				if (!act.possible() || conf.unstable() || conf.numOfVMs() < 2
						|| !conf.minimumFound()) {
					valid = false;
				}
				break;
			}
			case (ADD_CHEAPEST): {
				if (conf.numOfTC() == conf.numOfVMs()) {
					valid = false;
				}
				break;
			}

			case (ADD_SAME): {
				if (conf.numOfTC() == conf.numOfVMs()) {
					valid = false;
				}
				break;
			}
			case (ADD_EXPENSIVE): {
				if (conf.numOfTC() == conf.numOfVMs()) {
					valid = false;
				}
				break;
			}
			case (GENERATE_NEW): {
				if (!act.possible() || conf.unstable() || !conf.minimumFound()) {
					valid = false;
				}
				break;
			}

			}
		}
		return valid;
	}

	/**************************************
	 * helper methods e.g. printing etc.
	 **************************************/

	public void print() {
		System.out.println("--------- Action: name=" + name() + ", possible="
				+ m_possible + ", cost=" + m_cost + ", tabu=" + m_tabu
				+ ", tenure=" + m_tenure + "-----------");

		System.out.println("Recency=(last iteration lowered cost)="
				+ m_last_iteration_lowered_cost + ", Quality="
				+ df.format(quality())
				+ ", (times_considered, times_chosen, times_lowered_cost)=("
				+ m_times_considered + "," + m_times_chosen + ","
				+ m_times_lowered_cost + ")");
	}

	public static void printcsv(FileWriter csv, long last_iteration,
			long skipped, Config min, long numOfMin, double execution_time_ms, String alogName) {
		try {
			long totalIterations = last_iteration + 1; // iteration count start
			// from 0
			
			long totalMem = Runtime.getRuntime().totalMemory();
			long freeMem = Runtime.getRuntime().freeMemory();
			long usedMem = totalMem - freeMem;
			
			csv.write("\n");
			csv
					.write("Alogrithm, #iterations,#iterations_skipped,execution_time(ms),$(minimum),#minima,memory(mb)\n");
			csv.write(alogName + "," + totalIterations + "," + skipped + ","
					+ df.format(execution_time_ms) + "," + min.cost() + ","
					+ numOfMin + "," + ((double)usedMem)/1000000 + ",grep(exe)\n\n");

			csv.write("Action,last_iteration_lowered_cost,#(considered),"
					+ "#(chosen),#(lowered_cost),recency,quality\n");
			for (int i = 0; i < NUM_OF_ACTIONS; i++) {
				Action action = Action.action(i);
				double recencyFactor = -1;
				if (action.recency() != -1) {
					recencyFactor = ((double) action.recency())
							/ ((double) last_iteration);
				}
				csv.write(action.name() + "," + action.recency() + ","
						+ action.timesConsidered() + "," + action.timesChosen()
						+ "," + action.timesLoweredCost() + ",");

				if (recencyFactor == -1) {
					csv.write("n/a,");
				} else {
					csv.write(df.format(recencyFactor) + ",");
				}

				if (action.quality() == -1) {
					csv.write("n/a,");
				} else {
					csv.write(df.format(action.quality()) + ",");
				}
				csv.write("\n");
			}
			csv.write("\n");
			min.printcsvVMmap(csv, alogName);
			csv.write("\n");
			csv
					.write("Alogrithm, vm_cost($),storage_cost($),network_cost($),penalty($),constant($),total($)\n");
			csv.write(alogName + "," + df.format(min.vm_cost()) + ","
					+ df.format(min.storage_cost()) + ","
					+ df.format(min.network_cost()) + ","
					+ df.format(min.penalty_cost()) + ","
					+ df.format(min.constant_cost()) + ","
					+ df.format(min.cost()) + ",grep=(cost)\n\n");
			min.printSLA(csv);
			csv.write("\n");
			csv.write("\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printAll() {
		for (int i = 0; i < NUM_OF_ACTIONS; i++) {
			Action action = Action.action(i);
			action.print();
		}
	}

	public static void printMoves(LinkedHashMap<Integer, Boolean> candidateMoves) {
		Set moves = candidateMoves.entrySet();
		Iterator move_iter = moves.iterator();

		// print all ids of all transaction classes and vms associated with
		// them
		while (move_iter.hasNext()) {
			Map.Entry move_entry = (Map.Entry) move_iter.next();
			int move_type = (Integer) move_entry.getKey(); // (Integer)

			Action chosenAction = Action.action(move_type);
			chosenAction.print();
		}
		System.out.println("...................");
	}

	public static PriorityQueue<Action> printMoves(PriorityQueue<Action> moves) {

		Comparator<Action> comparator = new MoveContainer();
		PriorityQueue<Action> queue = new PriorityQueue<Action>(10, comparator);

		while (moves.size() != 0) {
			Action action = moves.remove();
			action.print();
			queue.add(action);
		}

		System.out.println("...................");
		return queue;
	}
	
	public static void printMoves(Vector <Action> candidateMoves) {		
		Iterator<Action> move_iter = candidateMoves.iterator();

		// print all ids of all transaction classes and vms associated with
		// them
		while (move_iter.hasNext()) {
			Action move = (Action) move_iter.next();
			move.print();
		}
		System.out.println("...................");
	}

}
