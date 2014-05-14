import java.util.Comparator;
import java.util.PriorityQueue;


public class MoveContainer implements Comparator<Action> {

	public static void main(String[] args) {
		Comparator<Action> comparator = new MoveContainer();
		PriorityQueue<Action> queue = new PriorityQueue<Action>(10, comparator);
		queue.add(Action.AddSame);
		queue.add(Action.Upgrade);
		queue.add(Action.AddCheapest);
		System.out.println("size of Q=" + queue.size());
		while (queue.size() != 0) {
			Action act = queue.remove();
			act.print();
			System.out.println();
		}
	}

	@Override
	public int compare(Action x, Action y) {
		// Assume neither Action is null. Real code should
		// probably be more robust
		if (x.cost() < y.cost()) {
			return -1;
		}
		if (x.cost() > y.cost()) {
			return 1;
		}
		return 0;
	}
}