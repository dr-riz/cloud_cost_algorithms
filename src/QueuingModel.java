import java.util.Iterator;
import java.util.Vector;
	
public abstract class QueuingModel {
		// processing capacity
		abstract public double proc_capacity();

		abstract public double serviceDemand4tc(int tid); 

		abstract public double throughput4tc(int tid);

		abstract public double throughput();
			
		abstract public double utilization4tc(int tid);			

		abstract public double utilization();
			
		abstract public double residenceTime4tc(int tid);
			
		abstract public double qLen4tc(int tid);

		abstract public double responseTime();

		abstract public double queueLength();

		abstract public double serviceRate();

		abstract public int highestUtilTC();	
}
