import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ServiceCenter {
	public boolean delay; //delay or queuing
	
	public LinkedHashMap<Integer, Double> m_demand;
	public LinkedHashMap<Integer, Double> m_arrival_q;
	public LinkedHashMap<Integer, Double> m_queue;
	public LinkedHashMap<Integer, Double> m_residence;		
	
	private boolean m_verbose = true;
	
	ServiceCenter() { ; }
	
	ServiceCenter(boolean t) {
		delay = t;
		m_demand = new LinkedHashMap<Integer, Double>();
		m_arrival_q = new LinkedHashMap<Integer, Double>();
		m_queue = new LinkedHashMap<Integer, Double>();
		m_residence = new LinkedHashMap<Integer, Double>();		
	}	

	public double demand_for_tc(int tid){
		double ret = m_demand.get(tid);
		return ret;
	}
	
	public void demand_for_tc(int tid, double demand){
		m_demand.put(tid, demand);		
	}
	
	public double arrivalq_for_tc(int tid){
		double ret = m_arrival_q.get(tid);
		return ret;
	}
	
	public void arrivalq_for_tc(int tid, double arrivalq){
		m_arrival_q.put(tid, arrivalq);		
	}
	
	public double qlen_for_tc(int qlen){
		double ret = m_queue.get(qlen);
		return ret;
	}
	
	public void qlen_for_tc(int tid, double qlen){
		m_queue.put(tid, qlen);		
	}
	
	public double queue_len_at_sc(){
		double ret = 0;
		Set queues = m_queue.entrySet();
		Iterator q_iter = queues.iterator();

		while (q_iter.hasNext()) {				
			Map.Entry tc_entry = (Map.Entry) q_iter.next();
			int tid = (Integer) tc_entry.getKey(); // (Integer)

			double qlen_4_tc = qlen_for_tc(tid);
			ret += qlen_4_tc;		
		}
		
		return ret;
	}
	
	public double res_for_tc(int tid){
		double ret = m_residence.get(tid);
		return ret;
	}
	
	public void res_for_tc(int tid, double res){
		m_residence.put(tid, res);		
	}	
	
}
