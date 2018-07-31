public class DiningPhilosophers {

	Philosopher[] philosophers; // = new Philosopher[5];
	Object[] forks;

	private class Philosopher extends Thread {
		private int id;
		private int fork1;
		private int fork2;
		volatile boolean flag = true;

		Philosopher(int id, int fork1, int fork2) {
			this.id = id;
			this.fork1 = fork1;
			this.fork2 = fork2;
		}

		@Override
		public void run() {
			status("Ready to eat using forks " + fork1 + " and " + fork2);

			while (flag) {
				status("Picking up fork " + fork1);
				synchronized (forks[fork1]) {
					status("Picking up fork " + fork2);
					synchronized (forks[fork2]) {
						status("Eating");
					}
				}

			}
			status("EXITING!!");
			// System.exit(0);
		}

		public void status(String msg) {
			System.out.println("Philosopher " + id + ": " + msg);
		}

	}

	private DiningPhilosophers(int num) { // constructor
		forks = new Object[num];
		philosophers = new Philosopher[num];
		for (int i = 0; i < num; ++i) {
			forks[i] = new Object();
			// philosophers[i] = new Philosopher(i, i, (i + 1) % num); // naive
			int fork1 = i;
			int fork2 = (i + 1) % num;
			if ((i % 2) == 0)
				philosophers[i] = new Philosopher(i, fork2, fork1);
			else
				philosophers[i] = new Philosopher(i, fork1, fork2);
		}
	}

	public void startEating() throws InterruptedException {
		for (int i = 0; i < philosophers.length; ++i)
			philosophers[i].start();

		Thread.currentThread();
		// Suspend the main thread until the first philosopher
		// stops eating, which
		// will never happen -- this keeps the simulation running indefinitely?
		Thread.sleep(5 * 1000);
		for (int i = 0; i < philosophers.length; ++i) {
			philosophers[i].flag = false;
			philosophers[i].join();
		}
	}

	public static void main(String[] args) {
		try {
			DiningPhilosophers d = new DiningPhilosophers(5);
			d.startEating();
		} catch (InterruptedException e) {
		}
	}

}
