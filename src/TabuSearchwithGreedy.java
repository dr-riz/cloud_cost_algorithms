public class TabuSearchwithGreedy extends Algorithm {

	/**************************
	 * member variables
	 ***************************/
	int cheapest_vm_type_in_conf = VM.small;
	int tenure = 2; // tenure of tabu status

	/**************************
	 * constructors
	 ***************************/

	TabuSearchwithGreedy(double wl, double ar, TransactionClass tc, Partition p,
			int useCase) {
		super(wl, ar, tc, p, useCase);
		// TODO Auto-generated constructor stub
	}
	
	/*************** step 1 *************/
	public void generateBaselineConfig() {
		Algorithm alog = new GreedOnInvestment(m_workload, m_arrival_rate, m_c1,
				m_dp, m_scenario);
		m_conf = alog.getCheapestConfig();
		initCSV();
		
		CostModel.calculateCost(m_conf);
		// need to start from iterations that greedOnInvestment took
		// this is now conf.iteration() + step for rejected conf (1) + new step
		// (1)
		m_step = m_conf.iteration() + 1 + 1;
		m_conf.iteration(m_step);

		// min_found = true;
		m_numMinima = 1;
		m_minimum = m_conf;
		m_minimumCost = m_minimum.cost();

		initCSV();
		printStep("tabuSearchwithGreedy", m_scenario, m_conf, m_minimumCost,
				m_numMinima);
		Action.initAction();
	}

	/**********************************************************
	 * nextMove: take next cheapest step based on cost of if unstable
	 *********************************************************/
	public void makeNextMove() {
		takeNextNonTabuedCheapestStep();
		}

	/*************** step 3 *************/
	public void calcCostOf_new_conf() {
		CostModel.calculateCost(m_confp);
		m_confp.iteration(m_step);
		printStep("tabuSearchwithGreedy", m_scenario, m_confp, m_minimumCost, m_numMinima);
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
}
