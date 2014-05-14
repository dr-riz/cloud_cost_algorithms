public class NaiveTabuSearch extends Algorithm {

	/**************************
	 * member variables
	 ***************************/
	int cheapest_vm_type_in_conf = VM.small;

	/**************************
	 * constructors
	 ***************************/

	NaiveTabuSearch(double wl, double ar, TransactionClass tc, Partition p,
			int useCase) {
		super(wl, ar, tc, p, useCase);
		// TODO Auto-generated constructor stub
	}

	/*************** step 1 *************/
	public void generateBaselineConfig() {
		System.out.println("generating baseline config (id=" + Config.counter()
				+ ")");
		m_conf = Config.generateConfig(m_workload, m_arrival_rate,
				cheapest_vm_type_in_conf, m_c1, m_dp);
		CostModel.calculateCost(m_conf);
		m_conf.iteration(m_step);
		printStep("naiveTabuSearch", m_scenario, m_conf, m_minimumCost, m_numMinima);
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
		printStep("naiveTabuSearch", m_scenario, m_confp, m_minimumCost, m_numMinima);
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
