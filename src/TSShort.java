import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class TSShort extends Algorithm {

	/**************************
	 * member variables
	 ***************************/
	Random generator = new Random(random_seed);
	int initialVM = generator.nextInt(VM.numOfVMTypes()); // VM.ATOMIC_VM;
	final static int TABU_CONSTRUCTS = 100;
	final static int GREEDY_CONSTRUCTS = 200;
	final static int TABU_GREEDY_HYBRID = 300;
	final static int TABU_GREEDY_BACKFILL = 400;
	int m_tabu_policy = TABU_GREEDY_HYBRID;

	/**************************
	 * constructors
	 ***************************/

	TSShort(double ar, TransactionClass tc, Partition p, int useCase) {
		super(ar, tc, useCase);
		m_tabu_policy = TABU_GREEDY_HYBRID;
		m_name = "TABU_GREEDY_HYBRID";
	}

	TSShort(Vector<TransactionClass> tcs, Vector<Partition> dps, int useCase) {
		super(tcs, dps, useCase);
		m_tabu_policy = TABU_GREEDY_HYBRID;
	}

	TSShort(Vector<TransactionClass> tcs, Vector<Partition> dps, int useCase,
			int tt) {
		super(tcs, dps, useCase);
		m_tabu_policy = tt;
	}

	/*************** step 1 *************/
	public void generateBaselineConfig() {
		System.out.println("generating baseline config (id=" + Config.counter()
				+ ")");
		if (m_tc == null) {
			m_conf = Config.generateConfig(m_arrival_rate, null, initialVM,
					m_c1);
		} else {
			m_conf = Config.generateConfig(m_tc, m_dp, initialVM);
		}
		CostModel.calculateCost(m_conf);
		m_conf.iteration(m_step);
		m_conf.action(999);
		printStep("tsShort-" + tabu_policy(m_tabu_policy), m_scenario, m_conf, m_minimumCost, m_numMinima);
	}

	public void makeNextMove() {
		makeNextMove(m_tabu_policy);
		m_name = tabu_policy(m_tabu_policy);
	}

	/**********************************************************
	 * nextMove: take next cheapest step based on cost of if unstable
	 *********************************************************/
	public void makeNextMove(int approach) {
		Action.updateTabuStatus();

		System.out.println("---------ITERATION=" + m_step + " -------------");
		m_conf.printVMmap();

		Action.updateMoveCosts(m_conf);

		LinkedHashMap<Integer, Boolean> candidateMoves = null;

		switch (approach) {
		case (TABU_CONSTRUCTS): {
			boolean backfill_with_greedy = false;
			candidateMoves = Action.generateMovesUsingTabuConstructs(m_conf,
					backfill_with_greedy);
			break;
		}
		case (TABU_GREEDY_BACKFILL): {
			boolean backfill_with_greedy = true;
			candidateMoves = Action.generateMovesUsingTabuConstructs(m_conf,
					backfill_with_greedy);
			break;
		}
		case (TABU_GREEDY_HYBRID): {
			candidateMoves = Action.generateMovesOrderedByCostUsingTabu(m_conf);
			break;
		}
		case (GREEDY_CONSTRUCTS): {
			candidateMoves = Action
					.generateAllPossibleMovesOrderedOnCost_LH(m_conf);
			break;
		}
		}

		System.out.println("printing candidate moves (" + candidateMoves.size()
				+ "):");
		Action.printMoves(candidateMoves);

		Set moves = candidateMoves.entrySet();
		Iterator move_iter = moves.iterator();

		if (candidateMoves.size() == 0) {
			m_chosenAction = null;
			m_confp = null;
		} else {
			while (move_iter.hasNext()) {
				Map.Entry move_entry = (Map.Entry) move_iter.next();
				int move_type = (Integer) move_entry.getKey(); // (Integer)

				m_chosenAction = Action.action(move_type);

				if (!m_chosenAction.tabu() || candidateMoves.size() == 1) {
					m_chosenAction.tabu(true);
					m_tenure = generator.nextInt(candidateMoves.size()) + 1; //adding one to avoid zero tenure
					m_chosenAction.tenure(m_tenure);
					m_confp = null;
					break;
				} else if (m_minimumFound) { // tabu = true
					System.out
							.println(m_chosenAction.name()
									+ ": checking if aspiration criterion overrides tabu");
					m_confp = m_chosenAction.move(m_conf);
					CostModel.calculateCost(m_confp);
					if (!m_confp.unstable()
							&& m_confp.cost() < m_minimum.cost()) {
						// chosenAction.lastLoweredCostAt(step);
						System.out.println("ITERATION=" + m_step
								+ "; ASPIRATION OVERIDE, "
								+ m_chosenAction.name()
								+ " gives lower cost config (id="
								+ m_confp.id() + ",$"
								+ df.format(m_confp.cost()) + ") than "
								+ m_numMinima + "'th minima " + "(id="
								+ m_minimum.id() + ",$"
								+ df.format(m_minimum.cost()) + ")");

						m_numMinima++;
						m_stepsInFindingMinimum = m_counter * m_look_ahead; // reset
						m_counter = 1; // reset counter
						m_minimum = m_confp;
						m_minimumCost = m_minimum.cost();

						break;
					} else {
						System.out.println(m_chosenAction.name()
								+ ": aspiration criterion (config id="
								+ m_confp.id() + ",$"
								+ df.format(m_confp.cost())
								+ ") DID NOT override tabu" + "(i.e. current ("
								+ m_numMinima + "'th) minima " + "id="
								+ m_minimum.id() + ",$"
								+ df.format(m_minimum.cost()) + ")");
						m_chosenAction = null;
						m_confp = null;
					}
				} else {
					m_chosenAction = null;
					m_confp = null;
				}
			}
			if (m_chosenAction != null && m_confp == null) {
				m_chosenAction.tabu(true);
				m_tenure = generator.nextInt(candidateMoves.size()) + 1; //adding one to avoid zero tenure
				m_chosenAction.tenure(m_tenure);
				m_confp = m_chosenAction.move(m_conf);
			}
		}
	}

	public boolean toSkipIter() {
		return toSkipIter_2n();
	}

	public boolean toTerminate() {
		boolean ret = false;
		if (m_minimumFound && (m_counter >= m_stepsInFindingMinimum)) {
			ret = true;
		}
		return ret;
	}

	/*************** step 3 *************/
	public void calcCostOf_new_conf() {
		// if (m_confp == null) {
		// m_confp = m_chosenAction.move(m_conf);
		// CostModel.calculateCost(m_confp);
		// }

		CostModel.calculateCost(m_confp);
		m_confp.iteration(m_step);
		m_chosenAction.incrChosen();
		printStep("tsShort-" + tabu_policy(m_tabu_policy), m_scenario, m_confp, m_minimumCost, m_numMinima);
	}

	/*************** step 4 *************/
	public boolean bothConfigUnstable() {
		return bothConfigUnstable_xn();
	}

	/*************** step 5 *************/
	public boolean onlyCurrentConfigUnstable() {
		return onlyCurrentConfigUnstable_xn();
	}

	/*************** step 6 *************/
	public boolean currentConfigStableButNewUnstable() {
		return currentConfigStableButNewUnstable_xn();
	}

	/*************** step 7 *************/
	public boolean currentConfCostsLessThanNewConf() {
		return currentConfCostsLessThanNewConf_xn();
	}

	public static String tabu_policy(int tp) {
		switch (tp) {
		case (TABU_CONSTRUCTS): {
			return "tabuContructs";
		}
		case (GREEDY_CONSTRUCTS): {
			return "tabuGreedy";
		}
		case (TABU_GREEDY_HYBRID): {
			return "tabuGreedyHybrid";
		}
		case (TABU_GREEDY_BACKFILL): {
			return "tabuGreedyBackfill";
		}
		default: {
			return "unknown policy";
		}
		}
	}
}
