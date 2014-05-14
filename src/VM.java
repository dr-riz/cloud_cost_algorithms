import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class VM {
	private static Vector<VM> VM_types = new Vector<VM>();

	public static final int ATOMIC_VM = 0;
	public static final int COST_RANK_MINNED_OUT = ATOMIC_VM;
	public static int COST_RANK_MAXED_OUT = ATOMIC_VM;
	public static boolean m_verbose = false;

	public static double BP_fraction = 0.8;

	static private int m_id_counter = 0;

	private int m_type;
	private int m_cores;
	private double m_cu;
	private double m_memory; // in giga bytes
	private int m_platform;
	private double m_pricePerHour; // dollar per hour
	private int m_num_of_dp;
	private int m_num_of_replicas;
	private double m_numOfUsers;
	private Vector<Integer> m_tc;
	private HashMap<Integer, Integer> m_mapdp; // map: partition -> count
	private double m_utilization;
	private double m_responseTime;
	private double m_throughput;
	private int m_busiestTC;
	private double m_weight;
	private double m_degrees;
	private double m_mpl;

	// metadata
	private int m_id;
	private int m_costrank;

	/**************************************
	 * constructors
	 **************************************/

	@SuppressWarnings("unused")
	public VM() {
		m_num_of_replicas = 0;
		m_num_of_dp = 0;
		m_tc = null;
		m_mapdp = null;
		m_utilization = -1;
		m_responseTime = -1;
		m_throughput = -1;
		m_busiestTC = -1;
		m_numOfUsers = 0;
		m_weight = 0;
		m_degrees = 0; // All SLA violated by default
		m_mpl=0;
	}

	public static VM createVM(int type) {
		VM v = new VM(type);
		v.id(m_id_counter);
		m_id_counter++;
		return v;
	}

	public static VM createVM(int type, VM basedOn) {
		VM v = new VM(type);
		v.m_num_of_replicas = basedOn.m_num_of_replicas;
		v.m_num_of_dp = basedOn.m_num_of_dp;
		v.m_numOfUsers = basedOn.m_numOfUsers;

		Vector<Integer> master_tcs = basedOn.getAlltc();
		v.m_tc = (Vector<Integer>) master_tcs.clone();

		v.m_mapdp = new HashMap<Integer, Integer>(); // pid to count

		Set dps = basedOn.m_mapdp.entrySet();
		Iterator dp_iter = dps.iterator();

		while (dp_iter.hasNext()) {
			Map.Entry dpentry = (Map.Entry) dp_iter.next();
			int pid = (Integer) dpentry.getKey();
			int count = (Integer) dpentry.getValue();
			v.m_mapdp.put(pid, count);
		}

		v.id(m_id_counter);
		m_id_counter++;
		return v;
	}

	public static VM copy(VM master) {
		VM copy = new VM();

		copy.m_type = master.type();
		copy.m_cores = master.cores();
		copy.m_cu = master.cu();
		copy.m_memory = master.memory(); // in giga bytes
		copy.m_platform = master.platform();
		copy.m_pricePerHour = master.price(); // dollar per hour
		copy.m_num_of_dp = master.numOfDP();
		copy.m_num_of_replicas = master.m_num_of_replicas;
		copy.m_numOfUsers = master.numOfUsers();
		copy.m_utilization = master.m_utilization;
		copy.m_responseTime = master.m_responseTime;
		copy.m_throughput = master.m_throughput;
		copy.m_busiestTC = master.m_busiestTC;
		copy.m_weight = master.m_weight;
		copy.m_degrees = master.m_degrees;
		copy.m_mpl=master.mpl();

		// metadata
		copy.m_id = master.id();
		copy.m_costrank = master.costRank();

		Vector<Integer> master_tcs = master.getAlltc();
		copy.m_tc = (Vector<Integer>) master_tcs.clone();

		copy.m_mapdp = new HashMap<Integer, Integer>(); // pid to count

		Set dps = master.m_mapdp.entrySet();
		Iterator dp_iter = dps.iterator();

		while (dp_iter.hasNext()) {
			Map.Entry dpentry = (Map.Entry) dp_iter.next();
			int pid = (Integer) dpentry.getKey();
			int count = (Integer) dpentry.getValue();
			copy.m_mapdp.put(pid, count);
		}

		return copy;
	}

	public static void add_vmtype(VM sample) {
		VM template = new VM();

		// vm_type (cannot be reset)
		template.m_type = VM_types.size();

		template.m_cores = sample.cores();
		template.m_cu = sample.cu();
		template.m_memory = sample.memory(); // in giga bytes
		template.m_platform = sample.platform();
		template.m_pricePerHour = sample.price(); // dollar per hour
		template.m_mpl = sample.mpl();

		COST_RANK_MAXED_OUT = VM_types.size();
		VM_types.add(template);
	}

	public static int typesOfVM() {
		return VM_types.size();
	}

	private VM(int type) {
		// vm_type (cannot be reset)
		m_type = type;

		m_num_of_replicas = 0;
		m_num_of_dp = 0;
		m_numOfUsers = 0;
		m_tc = new Vector<Integer>();
		m_mapdp = new HashMap<Integer, Integer>(); // pid to count
		m_utilization = -1;
		m_responseTime = -1;
		m_throughput = -1;
		m_busiestTC = -1;
		m_weight = 0;
		m_degrees = 0;

		VM template = VM_types.get(type);

		m_cores = template.cores();
		m_cu = template.cu();
		m_memory = template.memory();
		m_platform = template.platform();
		m_pricePerHour = template.price();
		m_costrank = type;
		m_mpl = template.mpl();
	}

	/**************************************
	 * accessor methods
	 **************************************/

	public int cores() {
		return m_cores;
	}

	public void cores(int c) {
		m_cores = c;
	}

	public double cu() {
		return m_cu;
	}

	public void cu(double u) {
		m_cu = u;
	}

	public double memory() {
		return m_memory;
	}

	public void memory(double m) {
		m_memory = m;
	}

	public int platform() {
		return m_platform;
	}

	public void platform(int p) {
		m_platform = p;
	}

	public double price() {
		return m_pricePerHour;
	}

	public void price(double p) {
		m_pricePerHour = p;
	}

	public double $cost() {
		return m_pricePerHour;
	}

	public void $cost(double c) {
		m_pricePerHour = c;
	}

	private void id(int _id) {
		m_id = _id;
	}

	int id() {
		return m_id;
	}

	int type() {
		return m_type;
	}

	void incrdp() {
		m_num_of_dp++;
	}

	int numOfDP() {
		return m_num_of_dp;
	}

	public long numOfClasses() {
		return m_tc.size();
	}

	public Vector<Integer> getAlltc() {
		return m_tc;
	}

	public int sizeOfPartitionTable() {
		return m_mapdp.size();
	}

	public HashMap<Integer, Integer> getAlldp() {
		return m_mapdp;
	}

	public int getNumOfReplica() {
		return m_num_of_replicas;
	}

	public void incrReplica() {
		m_num_of_replicas++;
	}

	public double numOfUsers() {
		return m_numOfUsers;
	}

	public void numOfUsers(double t) {
		m_numOfUsers = t;
	}
	
	public double mpl() {
		return m_mpl;
	}

	public void mpl(double t) {
		m_mpl = t;
	}

	public int costRank() {
		return m_costrank;
	}

	public double utilization() {
		if (m_utilization != 1) {
			if (m_verbose) {
				System.out.println("utilization: #workloads=" + m_tc.size()
						+ ", weight=" + m_weight + ", bp=" + m_memory
						* BP_fraction + ", cores=" + m_cores + ", cu=" + m_cu);
			}
			m_utilization = (m_tc.size() * m_weight)
					/ (m_memory * BP_fraction * m_cores * m_cu);
		}

		if (m_verbose) {
			System.out.println("vm with id(" + m_id + ")'s utilization="
					+ m_utilization);
		}
		return m_utilization;
	}

	public void utilization(double util) {
		m_utilization = util;
	}

	public double responseTime() {
		return m_responseTime;
	}

	public void responseTime(double t) {
		m_responseTime = t;
	}

	public double throughput() {
		return m_throughput;
	}

	public void throughput(double t) {
		m_throughput = t;
	}

	public int busiestTC() {
		return m_busiestTC;
	}

	public void busiestTC(int ar) {
		m_busiestTC = ar;
	}

	public double weight() {
		if (m_verbose) {
			System.out.println("vm with id(" + m_id + ")'s weight=" + m_weight);
		}
		return m_weight;
	}

	public double degrees() {
		return m_degrees;
	}

	public void degrees(double w) {
		m_degrees = w;
	}

	public double utility() {
		if (m_verbose) {
			System.out.println("calculating utility: #workloads=" + m_tc.size()
					+ ", m_degrees=" + m_degrees + ", m_pricePerHour="
					+ m_pricePerHour);
		}

		double utility = m_tc.size() * m_degrees / m_pricePerHour;

		if (m_verbose) {
			System.out.println("vm with id(" + m_id + ")'s utility=" + utility);
		}

		return utility;
	}

	public double busy() {
		if (m_verbose) {
			System.out.println("#workloads=" + m_tc.size() + ", utilization="
					+ utilization());
		}

		double busy = m_tc.size() * utilization();

		if (m_verbose) {
			System.out.println("vm with id(" + m_id + ")'s busy=" + busy);
		}

		return busy;
	}

	public static int cheapeast_vmType() {
		return VM_types.get(0).type();
	}

	public static int expensive_Type() {
		int ret = VM_types.get(0).type();
		double high_cost = $costofVM(ret);
		for (int i = 0; i < VM_types.size(); i++) {
			double cost = $costofVM(i);
			if (cost > high_cost) {
				ret = i;
				high_cost = $costofVM(ret);
			}
		}
		return ret;
	}

	/**************************************
	 * logic methods
	 **************************************/

	public void addtc(TransactionClass tc) {
		m_tc.add(tc.id());
		m_weight += LRPredictor.weight(tc.wl_type());
		m_numOfUsers += tc.population();

		Vector<Integer> dps = tc.getAlldp();
		Iterator<Integer> dp_iter = dps.iterator();
		while (dp_iter.hasNext()) {
			int pid = dp_iter.next();
			Integer res = m_mapdp.get(pid);
			if (res == null) {
				m_mapdp.put(pid, 1);
			} else {
				int count = (int) res;
				count++;
				m_mapdp.put(pid, count);
			}
		}
		// print();
	}

	public void deltc(TransactionClass tc) {
		int tid = tc.id();
		m_weight -= LRPredictor.weight(tc.wl_type());
		int index = 0;
		// get an Iterator object for Vector using iterator() method.
		Iterator<Integer> tc_itr = m_tc.iterator();

		// use hasNext() and next() methods of Iterator to iterate through the
		// elements
		while (tc_itr.hasNext()) {
			int tcid = tc_itr.next();
			if (tcid == tid) {
				break;
			}
			index++;
		}

		m_tc.remove(index);
		m_numOfUsers -= tc.population();

		// cleaning up m_mapdp since a class has left
		Vector<Integer> dps = tc.getAlldp();
		Iterator<Integer> dp_iter = dps.iterator();
		while (dp_iter.hasNext()) {
			int pid = dp_iter.next();
			Integer res = m_mapdp.get(pid);
			if (res != null) {
				if (res > 1) {
					int count = (int) res;
					count--;
					m_mapdp.put(pid, count);
				} else {
					// res == 1
					m_mapdp.remove(pid);
				}
			}
		}
	}

	public static void resetVMCounter() {
		m_id_counter = 0;
	}

	public void recyle() {
		// m_id: unique id -- dont reset?
		m_num_of_replicas = 0;
		m_tc = new Vector<Integer>();
		m_mapdp = new HashMap<Integer, Integer>(); // partition to VM mapping
		// m_type:vm_type (cannot be reset)
		m_num_of_dp = 0;
	}

	public static int upgradeVMtype(int vm_type) {
		int type = vm_type + 1;
		if (vm_type > VM_types.size())
			return -1;
		else
			return type;
	}

	public static int downgradeVMtype(int vm_type) {
		int type = vm_type - 1;
		if (vm_type < ATOMIC_VM)
			return -1;
		else
			return type;
	}

	public static double vmUpgrade$Cost(int vm_type) {
		return $costofVM(upgradeVMtypeBy$Cost(vm_type));
	}

	public static double vmUpgrade$CostRank(int vm_type) {
		return $costofVM(upgradeVMtypeByCostRank(vm_type));
	}

	public static int upgradeVMtypeBy$Cost(int vm_type) {
		return (vm_type + 1);
	}

	public static int upgradeVMtypeByCostRank(int vm_type) {
		return (vm_type + 1);
	}

	public static int downgradeVMtypeByCostRank(int vm_type) {
		return (vm_type - 1);
	}

	public static double $costofVM(int vm_type) {
		double cost = 0;
		VM template_vm = VM_types.get(vm_type);
		cost = template_vm.price();
		return cost;
	}
	
	public static int numOfVMTypes() {
		return VM_types.size();
	}

	/**************************************
	 * helper methods e.g. printing etc.
	 **************************************/
	public void print() {
		System.out.println("--------- VM id=" + m_id + ", m_type=" + m_type
				+ ", cost_rank=" + m_costrank + " optimal mpl= " + m_mpl + "-----------");
		System.out.println("#(DPs)=" + m_num_of_dp + ", #(replicas)="
				+ m_num_of_replicas + ", #users=" + m_numOfUsers + ", $cost="
				+ m_pricePerHour + "$/h");

		System.out.println("m_cores=" + m_cores + ", CU=" + m_cu + ", memory="
				+ m_memory + ", arch=" + m_platform + ", response_time="
				+ m_responseTime + ", m_throughput=" + m_throughput
				+ ", utilization=" + m_utilization + ", busiestTC="
				+ m_busiestTC + ", weight=" + m_weight);

		Iterator<Integer> tc_iter = m_tc.iterator();
		System.out.print("TC(ids): ");
		// print all ids of all transaction classes and vms associated with them
		while (tc_iter.hasNext()) {
			Integer tcid = tc_iter.next();

			System.out.print(tcid + ", ");
		}
		System.out.println();

		// print all partitions and along with their count
		if (m_mapdp != null) {
			Set<Integer> dps = m_mapdp.keySet();
			System.out.print("(Parition_id, count):");
			// iterate through the Set of keys
			Iterator<Integer> dp_iter = dps.iterator();
			while (dp_iter.hasNext()) {
				int pid = dp_iter.next();
				int count = m_mapdp.get(pid);
				System.out.print("(" + pid + "," + count + "), ");
			}
			System.out.println();
		}
	}

}