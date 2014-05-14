public class Partition implements Cloneable {

	final public static double price = 0.10; // $0.10 per GB per month
	static private int m_id_counter = 0;

	private int m_id;
	private double m_size; // in gigabytes
	boolean master;

	@SuppressWarnings("unused")
	private Partition() {
		;
	}

	public static Partition createPartition(double sz) {
		Partition p = new Partition();
		p.size(sz);
		p.id(m_id_counter);
		m_id_counter++;
		p.master = true;
		return p;
	}
	
	public static Partition copy(Partition dp) {
		if(dp == null) return null;
		Partition p = new Partition();
		p.m_id = dp.m_id;
		p.size(dp.size());
		p.master = false;
		return p;
	}

	// USE COPY INSTEAD
	public Object clone() {
		try {
			Partition c = (Partition) super.clone();
			c.m_id = m_id;
			c.m_size = m_size;
			c.master = false;
			return c;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}

	}

	private void size(double sz) {
		m_size = sz;
	}

	double size() {
		return m_size;
	}

	private void id(int _id) {
		m_id = _id;
	}

	int id() {
		return m_id;
	}
	
	public static void resetCounters (){
		m_id_counter = 0;
	}

}
