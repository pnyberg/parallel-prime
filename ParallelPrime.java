import java.util.LinkedList;

/**
 * TODO:
 * - Pollard rho
 * - Miller-Rabbin
 * - make it behave "good" when doing @benchmarks
 */
public class ParallelPrime {
	public static void main(String[] args) {
		long totalStartTime = System.currentTimeMillis();

		NumberController.init();

		PrimeTestingThread a = new PrimeTestingThread();
		PrimeTestingThread b = new PrimeTestingThread();
		PrimeTestingThread c = new PrimeTestingThread();
		PrimeTestingThread d = new PrimeTestingThread();

		a.start();
		b.start();
		c.start();
		d.start();

		/*@benchmark*/
		while(a.running || b.running || c.running || d.running) {
//			System.out.println("A: " + a.running + " B: " + b.running + " C: " + c.running + " D: " + d.running);
			System.out.print("");
		}

		long totalEndTime = System.currentTimeMillis();

		System.out.println((totalEndTime - totalStartTime) + " ms in total for program to finish!");
	}
}

class NumberController {
	private static int counter;
	public static LinkedList<Integer> primes;
	private static LinkedList<PrimeToken> active;

	private static long startTime;

	public static void init() {
		primes = new LinkedList<Integer>();
		active = new LinkedList<PrimeToken>();

		// add already know primes
		primes.add(2);
		primes.add(3);
		primes.add(5);
		primes.add(7);
		primes.add(11);
		primes.add(13);
	
		counter = primes.getLast() + 1; // we want to test the first number after the latest known prime

		startTime = System.currentTimeMillis();
	}

	// check if we can assign, if not, try finish an older assignment
	public static synchronized int assignNumber() {
		// if we cannot asure that all potential factors to the number has been found
		if (active.size() > 0 && active.get(0).number > Math.sqrt(counter + 1) /*@benchmark*/&& counter >= 10000) {
			for (int tryAssigned = 1 ; tryAssigned < 5 ; tryAssigned++) {
				for (int i = 0 ; i < active.size() ; i++) {
					if (active.get(i).assigned == tryAssigned) {
						active.get(i).assigned++;

						return active.get(i).number;
					}
				}
			}
		}

		int assignment = counter;
		counter += 2;
		counter = Math.min(counter, 10000); // for @benchmark

		active.add(new PrimeToken(assignment));

		return assignment;
	}

	public static synchronized void report(int number, boolean isPrime) {
		long endTime = System.currentTimeMillis();
//		System.out.println("Thread reported number [" + number + "] after " + (endTime - startTime) + " ms!");

		for (int i = 0 ; i < active.size() ; i++) {
			if (active.get(i).number == number) {
				active.remove(i);
				break;
			}
			if (i == (active.size() - 1)) {
				return;
			}
		}
		if (isPrime) {
			primes.add(number);
//			System.out.println(number + " added!");
		}
	}
}

class PrimeTestingThread extends Thread {
	public boolean running = true;

	public void run() {
		while (true) {
			int testNumber = NumberController.assignNumber();

			if (testNumber == 10000) {
				break;
			}

			boolean prime = testForPrime(testNumber);

			NumberController.report(testNumber, prime);
		}

		running = false;
	}

	private boolean testForPrime(int number) {
		for (int i = 0 ; i < NumberController.primes.size() ; i++) {
			if (number % NumberController.primes.get(i) == 0) {
				return false;
			}
		}

		return true;
	}
}

class PrimeToken {
	public int assigned;
	public int number;

	public PrimeToken (int number) {
		assigned = 1;
		this.number = number;
	}
}