import java.util.Random;
import java.util.Vector;

public class AdaptiveRandom extends Algorithm {

	/**************************
	 * member variables
	 ***************************/
	Random generator = new Random( random_seed );
	int initialVM = generator.nextInt( VM.numOfVMTypes() );//VM.ATOMIC_VM;		

	/**************************
	 * constructors
	 ***************************/

	AdaptiveRandom(double ar, TransactionClass tc,
			int useCase) {
		super(ar, tc, useCase);
		m_name = "adaptiveRandom";
		// TODO Auto-generated constructor stub
	}

	AdaptiveRandom(Vector<TransactionClass> tcs, Vector<Partition> dps, int useCase) {
		super(tcs, dps, useCase);
		m_name = "adaptiveRandom";
		// TODO Auto-generated constructor stub
	}

	/*************** step 1 *************/
	public void generateBaselineConfig() {
		System.out.println("generating baseline config (id=" + Config.counter()
				+ ")");
		if (m_tc == null) {
			m_conf = Config.generateConfig(m_arrival_rate, null, 
					initialVM, m_c1);
		} else {
			m_conf = Config.generateConfig(m_tc, m_dp, initialVM);
		}
		CostModel.calculateCost(m_conf);
		m_conf.iteration(m_step);
		m_conf.action(999);
		m_minimum = m_conf;
		printStep(m_name, m_scenario, m_conf, m_minimumCost,
				m_numMinima);
	}

	/**********************************************************
	 * nextMove: take next cheapest step based on cost of if unstable
	 *********************************************************/
	public void makeNextMove() {
		takeNextRandomStep();
	}
	
	public boolean toSkipIter() {
		return toSkipIter_2n();
	}
	
	public boolean toTerminate() {
		boolean ret = false;
		if (m_minimumFound && (m_counter == m_stepsInFindingMinimum)) {
			ret = true;
		}
		return ret;
	}

	/*************** step 3 *************/
	public void calcCostOf_new_conf() {
		CostModel.calculateCost(m_confp);
		m_confp.iteration(m_step);
		printStep("adaptiveRandom", m_scenario, m_confp, m_minimumCost,
				m_numMinima);
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
