import java.util.Random;
import java.util.Vector;

public class GreedyNoSLOBreach extends Algorithm {

	/**************************
	 * member variables
	 ***************************/

	double cost = 0, costp = 0;
	Random generator = new Random(random_seed);
	int initialVM = generator.nextInt(VM.numOfVMTypes());// VM.ATOMIC_VM;

	/**************************
	 * constructors
	 ***************************/

	GreedyNoSLOBreach(double ar, TransactionClass tc, int useCase) {
		super(ar, tc, useCase);
		m_name = "greedyNoSLOBreach";
		// TODO Auto-generated constructor stub
	}

	GreedyNoSLOBreach(Vector<TransactionClass> tcs, Vector<Partition> dps,
			int useCase) {
		super(tcs, dps, useCase);
		m_name = "greedyNoSLOBreach";
		// TODO Auto-generated constructor stub
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
		cost = CostModel.calculateCost(m_conf);
		m_conf.iteration(m_step);
		m_conf.action(999);
		m_minimum = m_conf;
		printStep(m_name, m_scenario, m_conf, cost, m_numMinima);
	}

	/**********************************************************
	 * nextMove: take next cheapest step based on cost of if unstable
	 *********************************************************/
	public void makeNextMove() {
		takeNextCheapestStep();
	}

	public boolean toSkipIter() {
		boolean ret = false;
		return ret;
	}

	public boolean toTerminate() {
		boolean ret = false;
		if (m_chosenAction == null) {
			// m_minimum = Config.createConfig(); // incase want a null configuration
			m_minimum = m_conf;
			return ret = true;
		}
		return ret;
	}

	/*************** step 3 *************/
	public void calcCostOf_new_conf() {
		costp = CostModel.calculateCost(m_confp);
		m_confp.iteration(m_step);
		printStep("GreedyNoSLOBreach", m_scenario, m_confp, costp, m_numMinima);
	}

	/*************** step 4 *************/
	public boolean bothConfigUnstable() {
		if (m_conf.unstable() && m_confp.unstable())// if both current
		// configs are
		// unstable
		{
			System.out.println("both current (id=" + m_conf.id()
					+ ") and new config (id=" + m_confp.id()
					+ ") are unstable; " + "accept new one regardless\n");
			cost = costp;
			m_conf = m_confp;

			// look for another one
			return true;
		} else {
			return false;
		}
	}

	/*************** step 5 *************/
	public boolean onlyCurrentConfigUnstable() {
		// if only the current config is unstable,
		// accept new one
		if (m_conf.unstable() && !m_confp.unstable()) {
			System.out.println("current config(id=" + m_conf.id()
					+ ") is unstable, " + "so accept new config(id="
					+ m_confp.id() + ")\n");
			cost = costp;
			m_conf = m_confp;
			Action.action(m_confp.action()).lastLoweredCostAt(m_step);
			// look for another one
			return true;
		} else {
			return false;
		}
	}

	/*************** step 6 *************/
	public boolean currentConfigStableButNewUnstable() {
		// if the new config is unstable,
		// but the current one is stable
		if (m_confp.unstable() && !m_conf.unstable()) {
			System.out.println("ITERATION=" + m_step + "; current config (id="
					+ m_conf.id() + ") is stable;" + " new config(id="
					+ m_confp.id() + ") is unstable;"
					+ " so suffice on the current config (id=" + m_conf.id()
					+ ") with cost=$" + df.format(cost));
			m_minimum = m_conf;
			m_numMinima++;
			printStep("GreedyNoSLOBreach", m_scenario, m_conf,
					m_minimum.cost(), m_numMinima);
			m_stopSearching = true;
			return m_stopSearching;
		} else {
			return false;
		}
	}

	/*************** step 7 *************/
	public boolean currentConfCostsLessThanNewConf() {
		if (cost < costp) {
			// new config costs more than current one, so return the
			// current one

			System.out.println("ITERATION=" + m_step + "; new config(id="
					+ m_confp.id() + ") costs $" + df.format(costp - cost)
					+ " more than current config (id=" + m_conf.id() + ", $"
					+ df.format(cost) + ")" + ", so current config (id="
					+ m_conf.id() + ") is the required config.");
			m_minimum = m_conf;
			m_numMinima++;
			printStep("GreedyNoSLOBreach", m_scenario, m_conf,
					m_minimum.cost(), m_numMinima);
			if (m_minimum.penalty_cost() == 0) {
				m_stopSearching = true;
				return m_stopSearching;
			} else {
				m_conf = m_confp;
				return false;
			}
		} else {

			// current conf costs more than new_conf so the new step lowered
			// $cost
			Action.action(m_confp.action()).lastLoweredCostAt(m_step);

			// set the new config to be the current one and generate
			// a new config
			System.out.println("set the new config (id=" + m_confp.id()
					+ ", cost=$" + df.format(costp)
					+ ") to be the current config (id=" + m_conf.id() + "),"
					+ " and generate a new config (id=" + Config.counter()
					+ ")");
			m_conf = m_confp;
			cost = costp;
			return false;
		}
	}
}
