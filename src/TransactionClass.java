import java.util.Iterator;
import java.util.Vector;

public class TransactionClass<Int> implements Cloneable {

	/**************************************
	 * static variables
	 **************************************/

	static private int m_id_counter = 0;
	static private boolean m_trans_wl = true;
	static private boolean DEBUG = false;

	/**************************************
	 * member variables
	 **************************************/

	private double[] m_Texe;
	private double[] m_IO;
	private Vector<Integer> m_dps;
	private Vector<Integer> m_vms;
	private int m_vid;

	private int m_id;
	private int m_num_of_cores;
	private double m_arrival_rate;

	private double m_desiredResTime;
	private double m_actualResTime;
	private double m_desiredTPS;
	private double m_actualTPS;
	private double m_basepenalty;
	private double m_penalty;
	public double m_degree; // degree of SLA met

	private double m_population = 0;
	private double m_think = 0;
	public double queue_len = -1;
	public double arrival_instant_q = -1;
	public double m_throughput = -1;
	public double m_residence = -1;
	public double m_utilization = -1;
	public double m_tolerance = -1;

	public int m_wl_type = -1;

	/**************************************
	 * constructors
	 **************************************/

	public static TransactionClass createTC(double arrival_rate) {
		TransactionClass tc = new TransactionClass(arrival_rate);
		;
		tc.id(m_id_counter);
		m_id_counter++;
		return tc;
	}

	public static TransactionClass createTC() {
		TransactionClass tc = new TransactionClass();
		tc.id(m_id_counter);
		m_id_counter++;
		return tc;
	}

	public static TransactionClass shallowCopy(int id, double arrival_rate) {
		TransactionClass tc = new TransactionClass(arrival_rate);
		tc.id(id);
		return tc;
	}

	public static TransactionClass shallowCopy(int id) {
		TransactionClass tc = new TransactionClass();
		tc.id(id);
		return tc;
	}

	private TransactionClass(double arrival_rate) {
		m_trans_wl = true;
		m_arrival_rate = arrival_rate;
		common_init();
	}

	private TransactionClass() {
		m_trans_wl = false;
		common_init();
	}

	private void common_init() {
		m_num_of_cores = 0;

		m_Texe = new double[VM.typesOfVM()];
		// initialize
		for (int i = 0; i < VM.typesOfVM(); i++) {
			m_Texe[i] = -1;
		}

		m_IO = new double[VM.typesOfVM()];
		// initialize
		for (int i = 0; i < VM.typesOfVM(); i++) {
			m_IO[i] = -1;
		}

		m_dps = new Vector<Integer>();
		m_vms = new Vector<Integer>();
		m_vid = -1;

		m_desiredResTime = -1;
		m_actualResTime = -1;
		m_desiredTPS = -1;
		m_actualTPS = -1;
		m_basepenalty = -1;
		m_penalty = -1;
		m_degree = 1; // 0 means SLA not met by default

		m_population = 0;
		m_think = 0;

		queue_len = -1;
		arrival_instant_q = -1;
		m_throughput = -1;
		m_residence = -1;
		m_utilization = -1;
		m_tolerance = -1;

		m_wl_type = -1;
	}

	public static TransactionClass copy(TransactionClass tc) {
		if (DEBUG) {
			System.out.println("TransactionClass: copy");
			System.out.println("original tc:");
			tc.print();
		}

		TransactionClass c1 = null;
		if (m_trans_wl) {
			c1 = shallowCopy(tc.id(), tc.getArrivalRate());
		} else {
			c1 = shallowCopy(tc.id());
		}

		for (int i = 0; i < VM.typesOfVM(); i++) {
			c1.m_Texe[i] = tc.m_Texe[i];
		}

		for (int i = 0; i < VM.typesOfVM(); i++) {
			c1.m_IO[i] = tc.m_IO[i];
		}

		c1.m_num_of_cores = tc.numOfCores();
		c1.m_desiredResTime = tc.m_desiredResTime;
		c1.m_actualResTime = tc.m_actualResTime;
		c1.m_desiredTPS = tc.m_desiredTPS;
		c1.m_actualTPS = tc.m_actualTPS;
		c1.m_penalty = tc.m_penalty;
		c1.m_degree = tc.m_degree;
		c1.m_basepenalty = tc.m_basepenalty;
		c1.m_trans_wl = tc.m_trans_wl;

		c1.m_population = tc.m_population;
		c1.m_think = tc.m_think;

		c1.queue_len = tc.queue_len;
		c1.arrival_instant_q = tc.arrival_instant_q;
		c1.m_throughput = tc.m_throughput;
		c1.m_residence = tc.m_residence;
		c1.m_utilization = tc.m_utilization;
		c1.m_tolerance = tc.m_tolerance;

		c1.m_wl_type = tc.m_wl_type;

		c1.m_vms = (Vector<Integer>) tc.m_vms.clone();
		c1.m_vid = tc.m_vid;
		c1.m_dps = (Vector<Integer>) tc.m_dps.clone();

		if (DEBUG) {
			System.out.println("copied tc:");
			c1.print();
		}
		return c1;
	}

	public Object clone() {
		// !!! does not work properly
		try {
			TransactionClass c = (TransactionClass) super.clone();
			c.id(m_id_counter);
			m_id_counter++;

			c.id(m_id);
			c.m_num_of_cores = m_num_of_cores;
			c.m_arrival_rate = m_arrival_rate;

			c.m_desiredResTime = m_desiredResTime;
			c.m_actualResTime = m_actualResTime;
			c.m_desiredTPS = m_desiredTPS;
			c.m_actualTPS = m_actualTPS;
			c.m_penalty = m_penalty;
			c.m_degree = m_degree;
			c.m_basepenalty = m_basepenalty;

			c.m_trans_wl = m_trans_wl;
			c.m_population = m_population;
			c.m_think = m_think;

			c.queue_len = queue_len;
			c.arrival_instant_q = arrival_instant_q;
			c.m_throughput = m_throughput;
			c.m_residence = m_residence;
			c.m_utilization = m_utilization;
			c.m_tolerance = m_tolerance;

			c.m_wl_type = m_wl_type;

			c.m_Texe = new double[VM.typesOfVM()];
			for (int i = 0; i < VM.typesOfVM(); i++) {
				c.m_Texe[i] = m_Texe[i];
			}

			c.m_IO = new double[VM.typesOfVM()];
			for (int i = 0; i < VM.typesOfVM(); i++) {
				c.m_IO[i] = m_IO[i];
			}

			// not a deep clone

			c.m_dps = (Vector<Partition>) m_dps.clone(); // partition to VM
			// mapping
			c.m_vms = (Vector<VM>) m_vms.clone();
			c.m_vid = m_vid;
			return c;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}

	}

	/**************************************
	 * accessor methods
	 **************************************/

	public static void resetCounters() {
		m_id_counter = 0;
	}

	public double Texe(int type, double time) {
		if (type < VM.ATOMIC_VM || type > VM.typesOfVM()) {
			return -1; // unrecognized vm type
		} else {
			return m_Texe[type] = time;
		}
	}

	public double Texe(int type) {
		if (type < VM.ATOMIC_VM || type > VM.typesOfVM()) {
			return -1; // unrecognized vm type
		} else {
			return m_Texe[type];
		}
	}

	public double ioDemand(int type) {
		if (type < VM.ATOMIC_VM || type > VM.typesOfVM()) {
			return -1; // unrecognized vm type
		} else {
			return m_IO[type];
		}
	}

	public double ioDemand(int type, double demand) {
		if (type < VM.ATOMIC_VM || type > VM.typesOfVM()) {
			return -1; // unrecognized vm type
		} else {
			m_IO[type] = demand;
			return m_IO[type];
		}
	}

	private void id(int _id) {
		m_id = _id;
	}

	int id() {
		return m_id;
	}

	public Vector<Integer> getAllvm() {
		return m_vms;
	}

	public int getOnlyvid() {
		return m_vid;
	}

	public double getArrivalRate() {
		return m_arrival_rate;
	}

	public void addDP(Partition dp) {
		m_dps.add(dp.id());
	}

	public Vector<Integer> getAlldp() {
		return m_dps;
	}

	public int numOfDP() {
		return m_dps.size();
	}

	public int numOfCores() {
		return m_num_of_cores;
	}

	public double desiredRespTime() {
		return m_desiredResTime;
	}

	public void desiredRespTime(double t) {
		m_desiredResTime = t;
	}

	public double actualRespTime() {
		return m_actualResTime;
	}

	public void actualRespTime(double t) {
		m_actualResTime = t;
	}

	public double desiredTPS() {
		return m_desiredTPS;
	}

	public void desiredTPS(double t) {
		m_desiredTPS = t;
	}

	public double actualTPS() {
		return m_actualTPS;
	}

	public void actualTPS(double t) {
		m_actualTPS = t;
	}

	public double throughput() {
		return m_throughput;
	}

	public void throughput(double t) {
		m_throughput = t;
	}

	public double utilization() {
		return m_utilization;
	}

	public void utilization(double t) {
		m_utilization = t;
	}

	public double penalty() {
		return m_penalty;
	}

	public void penalty(double t) {
		m_penalty = t;
	}

	public double degreeOfSLAmet() {
		return m_degree;
	}

	public void degreeOfSLAmet(double t) {
		m_degree = t;
	}

	public double basepenalty() {
		return m_basepenalty;
	}

	public void basepenalty(double t) {
		m_basepenalty = t;
	}

	static public boolean transWL() {
		return m_trans_wl;
	}

	static public void transWL(boolean b) {
		m_trans_wl = b;
	}

	// static public boolean batchWL() {
	// boolean ret = !m_trans_wl && m_think == 0;
	// return ret;
	// }
	//
	// static public void batchWL(boolean b) {
	// m_trans_wl = !b;
	// }
	//
	// static static public boolean interWL() {
	// boolean ret = !m_trans_wl && m_think > 0;
	// return ret;
	// }
	//
	// static public void interWL(boolean b, double t) {
	// m_trans_wl = !b;
	// m_think = t;
	// }

	public double population() {
		return m_population;
	}

	public void population(double p) {
		m_trans_wl = false;
		m_population = p;
	}

	public double think() {
		return m_think;
	}

	public void think(double p) {
		m_think = p;
	}

	public int wl_type() {
		return m_wl_type;
	}

	public void wl_type(int wl) {
		m_wl_type = wl;
	}

	/**************************************
	 * logic methods
	 **************************************/

	public void resetVMs() {
		m_vms = new Vector<Integer>();
		m_vid = -1;
		m_num_of_cores = 0;
		m_desiredResTime = -1;
		m_actualResTime = -1;
		m_desiredTPS = -1;
		m_actualTPS = -1;
	}

	public void addvm(VM vm) {
		m_num_of_cores += vm.cores();
		vm.addtc(this); // add reference to this class in vm
		m_vms.add(vm.id());
	}

	public void addOnlyVM(VM vm) {
		m_num_of_cores += vm.cores();
		vm.print();
		vm.addtc(this); // add reference to this class in vm
		vm.print();
		m_vid = vm.id();
	}

	public void addOnlyVMid(int vid) {
		m_vid = vid;
	}

	public void removevm(VM oldvm) {
		int numOfVMS = m_vms.size();
		// if there are no vms in tc,
		// then there is no need to remove
		if (numOfVMS != 0) {

			m_num_of_cores -= oldvm.cores();

			// get the index of the vm being removed
			int index = 0;
			for (index = 0; index < numOfVMS; index++) {
				int vid = m_vms.get(index);
				// System.out.println("vmid: (index, id)=(" + index + "," +
				// vm.id());

				if (vid == oldvm.id()) {
					break;
				}
			}
			// oldvm.removetc(this); // remove reference to this class in vm --
			// but we
			// dont care about the old one....todo: IS IT SAFE?

			m_vms.removeElementAt(index);
		}
	}

	public void removeOnlyVM() {
		m_vid = -1;
	}

	public void replacevm(VM oldvm, VM newvm) {
		removevm(oldvm);
		addvm(newvm);
	}

	public void replaceOnlyvm(VM oldvm, VM newvm) {
		removeOnlyVM();
		addOnlyVM(newvm);
	}

	public boolean classPresent(int _pid) {
		boolean ret = false;

		Iterator<Integer> dp_iter = m_dps.iterator();

		while (dp_iter.hasNext()) {
			int pid = dp_iter.next();
			if (pid == _pid) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	/**************************************
	 * helper methods e.g. printing etc.
	 **************************************/

	void print() {
		System.out.println("----- Transaction Class id=" + m_id + ", vid ="
				+ m_vid + ", wl_type=" + LRPredictor.wl_type_str(m_wl_type)
				+ ", #(cores)=" + m_num_of_cores + ", open workload="
				+ m_trans_wl + "-----");

		System.out.print("desiredResTime=" + m_desiredResTime
				+ "s, actualResTime=" + m_actualResTime + "s, ");

		System.out.print("m_desiredTPS=" + m_desiredTPS + "s, actualTPS="
				+ m_actualTPS + "tps, ");

		System.out.println("basepenalty=$" + m_basepenalty + ", penalty=$"
				+ m_penalty + ", degree=" + m_degree);

		System.out.print("Texe: ");
		for (int i = 0; i < VM.typesOfVM(); i++) {
			System.out.print("vm_type(" + i + ")=" + m_Texe[i] + "s; ");
		}
		System.out.println();

		// print all ids of all VMs
		Iterator<Integer> vm_iter = m_vms.iterator();
		System.out.print("VMs Ids: ");

		while (vm_iter.hasNext()) {
			int vid = vm_iter.next();
			System.out.print(vid + ", ");
		}
		System.out.println();

		// print all partitions
		Iterator<Integer> dp_iter = m_dps.iterator();
		System.out.print("Partition Ids: ");

		while (dp_iter.hasNext()) {
			int pid = dp_iter.next();
			System.out.print(pid + ", ");
		}
		System.out.println();
	}

	void printQNM() {
		System.out.println("----- Transaction Class id=" + m_id + ", vid ="
				+ m_vid + ", #(cores)=" + m_num_of_cores + ", open workload="
				+ m_trans_wl + "-----");

		if (m_trans_wl) { // transaction wl
			System.out.println("transactional class: arrival_rate="
					+ m_arrival_rate);
		} else { // batch or terminal wl
			if (m_think == 0) {
				System.out.println("batch class: population=" + m_population
						+ " job(s)");
			} else {
				System.out.println("terminal class: population(N)="
						+ m_population + " user(s), thinktime(Z)=" + m_think
						+ "s");
			}

			System.out.println("queue_len=" + queue_len
					+ ", arrival_instant_q=" + arrival_instant_q
					+ ", throughput=" + m_throughput + ", residence="
					+ m_residence + ", utilization=" + m_utilization
					+ ", tolerance=" + m_tolerance);
		}
		
		System.out.print("desiredResTime=" + m_desiredResTime
				+ "s, actualResTime=" + m_actualResTime + "s, ");

		System.out.print("m_desiredTPS=" + m_desiredTPS + "s, actualTPS="
				+ m_actualTPS + "tps, ");

		System.out.println("basepenalty=$" + m_basepenalty + ", penalty=$"
				+ m_penalty + ", degree=" + m_degree);
		
		System.out.print("Texe: ");
		for (int i = 0; i < VM.typesOfVM(); i++) {
			System.out.print("vm_type(" + i + ")=" + m_Texe[i] + "s; ");
		}
		System.out.println();

		System.out.print("IO demand:: ");
		for (int i = 0; i < VM.typesOfVM(); i++) {
			System.out.print("vm_type(" + i + ")=" + m_IO[i] + "s; ");
		}
		System.out.println();

		// print all ids of all VMs
		Iterator<Integer> vm_iter = m_vms.iterator();
		System.out.print("VMs Ids: ");

		while (vm_iter.hasNext()) {
			int vid = vm_iter.next();
			System.out.print(vid + ", ");
		}
		System.out.println();

		// print all partitions
		Iterator<Integer> dp_iter = m_dps.iterator();
		System.out.print("Partition Ids: ");

		while (dp_iter.hasNext()) {
			int pid = dp_iter.next();
			System.out.print(pid + ", ");
		}
		System.out.println();
	}
}
