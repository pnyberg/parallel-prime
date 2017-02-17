public class PrimeComparer {
	public static void calculate() {
		while (true) {
			int testNumber = NumberController.assignNumber();

			if (testNumber == 10000) {
				break;
			}

			boolean prime = testForPrime(testNumber);

			NumberController.report(testNumber, prime);
		}
	}

	private static boolean testForPrime(int number) {
		for (int i = 0 ; i < NumberController.primes.size() ; i++) {
			if (number % NumberController.primes.get(i) == 0) {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		long totalStartTime = System.currentTimeMillis();

		NumberController.init();

		calculate();

		long totalEndTime = System.currentTimeMillis();

		System.out.println((totalEndTime - totalStartTime) + " ms in total for program to finish!");
	}
}