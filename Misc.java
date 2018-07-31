import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.StringJoiner;

public class Misc {

	private static final int PHONE_NUMBER_LENGTH = 7;

	public static int strToInt(String numStr) {

		char ch[] = numStr.toCharArray();
		int sum = 0;
		// get ascii value for zero
		int zeroAscii = '0';
		for (char c : ch) {
			int tmpAscii = c;
			sum = (sum * 10) + (tmpAscii - zeroAscii);
		}
		return sum;
	}

	float powerNegativeAndFloat(float x, int y) {
		float temp;
		if (y == 0)
			return 1;
		temp = power(x, y / 2);
		if (y % 2 == 0)
			return temp * temp;
		else if (y > 0)
			return x * temp * temp;
		else
			return (temp * temp) / x;
	}

	// If n<>2,3
	// is+prime,+then+there+is+an+integer+k++such+that+n=6k%E2%88%921+n+=+6+k+%E2%88%92+1++or+n=6k%2B1+n+=+6+k+%2B+1+.&ie=UTF-8&oe=UTF-8

	static BitSet genPrimes(int n) {
		BitSet primes = new BitSet(n);
		primes.set(2); // add 2 explicitly
		primes.set(3); // add 3 explicitly
		for (int i = 6; i <= n; i += 6) { // step by 6 instead of multiplication
			primes.set(i - 1);
			primes.set(i + 1);
		}
		int max = (int) Math.sqrt(n); // don't need to filter multiples of primes bigger than max

		// this for loop enumerates all set bits starting from 5 till the max
		// sieving 2 and 3 is meaningless: n*6+1 and n*6-1 are never divisible by 2 or 3
		for (int i = primes.nextSetBit(5); i >= 0 && i <= max; i = primes.nextSetBit(i + 1))
			// The actual sieve algorithm like in your code
			for (int j = i * i; j <= n; j += i)
				primes.clear(j);
		return primes;
	}

	// Given a number n, print all primes smaller than or equal to n. It is also
	// given that n is a small number.
	// 0 and 1 are not prime numbers - Primes must have only 2 factors, 1 and itself
	// <> 1
	// sum 1/k = O(log n)!!!
	// The classical Sieve of Eratosthenes algorithm takes O(N log (log N))
	static void sieveOfEratosthenes(int n, List<Integer> primes) {
		// Create a boolean array "prime[2..n]" and initialize
		// all entries it as true. A value in prime[i] will
		// finally be false if it is Not a prime, else true.
		boolean mark[] = new boolean[n + 1];
		mark[2] = true;
		for (int i = 3; i <= n; i = i + 2)
			mark[i] = true;// odd numbers more likely to be prime

		for (int p = 2; p * p <= n; p++)
			// If prime[p] is not changed, then it is a prime
			if (mark[p] == true)
				// Update all multiples of p
				for (int i = 2 * p; i <= n; i += p) // or p*p
					mark[i] = false;

		// Print all prime numbers
		for (int i = 2; i <= n; i++)
			if (mark[i] == true) {
				System.out.print(i + " ");
				primes.add(i);
			}
	}

	static void segmentedSieve(int n) {
		// Compute all primes smaller than or equal
		// to square root of n using simple sieve
		int limit = (int) (Math.floor(Math.sqrt(n)) + 1);
		List<Integer> prime = new ArrayList<>();
		sieveOfEratosthenes(limit, prime);

		// Divide the range [0..n-1] in different segments
		// We have chosen segment size as sqrt(n).
		int low = limit;
		int high = 2 * limit;

		// While all segments of range [0..n-1] are not processed,
		// process one segment at a time
		while (low < n) {
			// To mark primes in current range. A value in mark[i]
			// will finally be false if 'i-low' is Not a prime,
			// else true.
			boolean mark[] = new boolean[limit + 1];

			for (int i = 0; i < mark.length; i++)
				mark[i] = true;

			// Use the found primes by simpleSieve() to find
			// primes in current range
			for (int i = 0; i < prime.size(); i++) {
				// Find the minimum number in [low..high] that is
				// a multiple of prime.get(i) (divisible by prime.get(i))
				// For example, if low is 31 and prime.get(i) is 3,
				// we start with 33.
				int loLim = (int) (Math.floor(low / prime.get(i)) * prime.get(i));
				if (loLim < low)
					loLim += prime.get(i);

				/*
				 * Mark multiples of prime.get(i) in [low..high]: We are marking j - low for j,
				 * i.e. each number in range [low, high] is mapped to [0, high-low] so if range
				 * is [50, 100] marking 50 corresponds to marking 0, marking 51 corresponds to 1
				 * and so on. In this way we need to allocate space only for range
				 */
				for (int j = loLim; j < high; j += prime.get(i))
					mark[j - low] = false;
			}

			// Numbers which are not marked as false are prime
			for (int i = low; i < high; i++)
				if (mark[i - low] == true)
					System.out.print(i + "  ");

			// Update low and high for next segment
			low = low + limit;
			high = high + limit;
			if (high >= n)
				high = n;
		}
	}

	public static int findMinimum(int[] array) {
		if (array == null || array.length == 0)
			throw new RuntimeException("Enter input array");
		int min = array[0];
		for (int i = 0; i < array.length; i++)
			if (array[i] < min)
				min = array[i];
		return min;
	}

	public static Integer findMinimum(List<Integer> ints) {
		if (ints == null || ints.isEmpty())
			throw new RuntimeException("Enter input list");
		int min = ints.get(0);
		for (int i : ints)
			if (i < min)
				min = ints.get(i);
		Iterator<Integer> iterator = ints.iterator();
		while (iterator.hasNext()) {
			int element = iterator.next();
			if (element < min)
				min = element;
		}
		ints.forEach(i -> System.out.println(i));
		return min;
	}

	public static void findMiddleElement(String[] strings) {
		if (strings == null || strings.length == 0)
			throw new RuntimeException("Enter input array");
		int middle = strings.length / 2;
		if (strings.length % 2 == 0)
			System.out.println("double middle is " + strings[middle - 1] + " and " + strings[middle]);
		else
			System.out.println("middle is " + strings[middle]);

	}

	public static int select2(int[] arr, int k) {
		if (k < 1 || arr == null || arr.length == 0 || k > arr.length)
			throw new RuntimeException("Enter input array and selection criteria");
		int min;
		int minindex;
		int tmp;
		for (int i = 0; i < k && i < arr.length - 1; i++) {
			min = arr[i];
			minindex = i;
			for (int j = i + 1; j < arr.length; j++)
				if (arr[j] < arr[minindex])
					minindex = j;
			if (i != minindex) {
				tmp = arr[minindex];
				arr[minindex] = arr[i];
				arr[i] = tmp;
			}
		}
		return arr[k - 1];
	}

	public static void insertsort(int arr[]) {
		int n = arr.length;
		for (int i = 1; i < n; ++i) {
			int key = arr[i];
			int j = i - 1;

			/*
			 * Move elements of arr[0..i-1], that are greater than key, to one position
			 * ahead of their current position
			 */
			while (j >= 0 && arr[j] > key) {
				arr[j + 1] = arr[j];
				j = j - 1;
			}
			arr[j + 1] = key;
		}
	}

	public static void insertsort2(int[] arr) {
		int min;
		int head;
		for (int i = 1; i < arr.length; i++) {
			min = arr[i];
			head = i - 1;
			// push greater head elements to the right
			// there is no swapping
			while (head >= 0 && arr[head] > min) {
				arr[head + 1] = arr[head];
				head--;
			}
			// insert in the head
			arr[head + 1] = min; // because of j--, j is one too small

		}

	}

	public static int stringToInt(String str) {
		boolean isNegative = false;
		int number = 0;
		for (char c : str.toCharArray()) {
			if (c == '-') {
				isNegative = true;
				continue;
			}
			if (c < '0' || c > '9')
				continue;
			number *= 10;
			number += (c - '0');
		}
		if (isNegative)
			number = -number;
		return number;
	}

	public static int swapOddEvenBits(int x) {
		return (((x & 0xaaaaaaaa) >> 1) | ((x & 0x55555555) << 1));
	}

	public static int pairAndSum(int arr[], int n) {
		int ans = 0; // Initialize result

		// Consider all pairs (arr[i], arr[j) such that
		// i < j
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				ans += arr[i] & arr[j];

		return ans;
	}

	// all elements occur 3 times except one
	public static int getSingleOccurence(int arr[], int n) {
		// Initialize result
		int result = 0;

		int x, sum;

		// Iterate through every bit of 32 bits
		for (int i = 0; i < 32; i++) {
			// Find sum of set bits at ith position in all
			// array elements
			sum = 0;
			x = (1 << i);
			for (int j = 0; j < n; j++)
				if ((arr[j] & x) != 0)
					sum++;

			// The bits with sum not multiple of 3, are the
			// bits of element with single occurrence.
			if ((sum % 3) != 0)
				result |= x;
		}

		return result;
	}

	/* Function to left rotate n by d bits */
	int leftRotate(int n, int d) {
		/*
		 * In n<<d, last d bits are 0. To put first 3 bits of n at last, do bitwise or
		 * of n<<d with n >>(INT_BITS - d)
		 */
		return (n << d) | (n >> (32 - d));
	}

	/* Function to right rotate n by d bits */
	int rightRotate(int n, int d) {
		/*
		 * In n>>d, first d bits are 0. To put last 3 bits of at first, do bitwise or of
		 * n>>d with n <<(INT_BITS - d)
		 */
		return (n >> d) | (n << (32 - d));
	}

	// count number of bits to be flipped to convert A to B
	int FlippedCount(int a, int b) {
		// Return count of set bits in
		// a XOR b
		return numOfOnesInBinary(a ^ b);
	}

	public static boolean checkSparse(int n) {
		// n is not sparse if there is set
		// in AND of n and n/2
		if ((n & (n >> 1)) != 0)
			return false;

		return true;
	}

	public static void nextSparse(int num) {

		int val1 = Integer.parseInt("0101010101010101010101010101010", 2);
		int val2 = Integer.parseInt("1010101010101010101010101010101", 2);

		// while ((num | val1) != val1 && ((num | val2) != val2))
		// while (!((num | val1) == val1 || (num | val2) == val2))
		while ((num & (num >> 1)) != 0) // much more natural than above..assumes integers, not floats...
			num++;

		System.out.println(num);
	}

	public static void stringConcatWays() {

		// java 8 ways
		StringJoiner sj1 = new StringJoiner(", ");
		String joined = sj1.add("one").add("two").toString();
		// one, two
		System.out.println(joined);

		StringJoiner sj2 = new StringJoiner(", ", "{", "}");
		String joined2 = sj2.add("Jake").add("John").add("Carl").toString();
		// {Jake, John, Carl}
		System.out.println(joined2);

		// most readable
		int theNumber = 42;
		System.out.println(String.format("Your number is %d!", theNumber));

		// efficient
		StringBuilder buffer = new StringBuilder(16).append("Your number is ").append(theNumber).append('!');
		System.out.println(buffer);

		// classic, OK with primitives and non-loops although loops fine in jdk 5+)
		System.out.println("Your number is " + theNumber + "!");
	}

	// "(1 – 2) / 3 * (4 + 5 – 6 / 7)" => */-123-+45/67
	// Postfix same as Prefix, just swap ) and (
	public static String printPostFix(String str) {
		Stack stack = new Stack();
		String postfix = "";
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isLetter(c))
				postfix = postfix + c;
			else if (c == '(')
				continue;
			else if (c == ')')
				postfix = postfix + ((Character) stack.pop()).toString();
			else
				stack.push(c);
		}
		return postfix;

	}

	public static String printPreFix(String str) {
		Stack stack = new Stack();
		String prefix = "";
		for (int i = str.length() - 1; i >= 0; i--) {
			char c = str.charAt(i);
			if (Character.isLetter(c))
				prefix = ((Character) c).toString() + prefix;
			else if (c == '(')
				prefix = ((Character) stack.pop()).toString() + prefix;
			else if (c == ')')
				continue;
			else
				stack.push(c);
		}
		return prefix;

	}

	public static int evaluatePrefix(String exp) {
		String[] tokens = exp.split(" ");
		Stack<Integer> operand = new Stack();
		for (int i = tokens.length - 1; i >= 0; i--)
			if (tokens[i].compareTo("+") != 0 && tokens[i].compareTo("-") != 0 && tokens[i].compareTo("*") != 0
					&& tokens[i].compareTo("/") != 0)
				operand.push(Integer.parseInt(tokens[i]));
			else {
				int op1 = operand.pop();
				int op2 = operand.pop();
				int result;
				switch (tokens[i]) {
				case "+":
					result = op1 + op2;
					break;
				case "-":
					result = op1 - op2;
					break;
				case "*":
					result = op1 * op2;
					break;
				case "/":
					result = op1 / op2;
					break;
				default:
					result = 0;
				}
				operand.push(result);
			}
		return operand.pop();
	}

	// not working properly, almost finished...
	public static void infix_to_prefix(char[] cs) {
		int count, temp = 0;
		char next;
		char symbol;
		Stack<Character> stack = new Stack<>();
		char[] result = new char[cs.length];
		// cs = reverseInPlace(new String(cs)).toCharArray();
		for (count = cs.length - 1; count >= 0; count--) {
			symbol = cs[count];
			if (checker(symbol) != 0)
				switch (symbol) {
				case ')':
					stack.push(symbol);
					break;
				case '(': // ( = POP
					while (!stack.isEmpty() && (next = stack.pop()) != ')')
						result[temp++] = next;
					break;

				case '+':
				case '-':
				case '*':
				case '/': {
					while (!stack.isEmpty() && precedence(stack.peek()) > precedence(symbol)) // pop those strictly
																								// GREATER than current
						result[temp++] = stack.pop();
					stack.push(symbol);
					break;
				}
				default:
					result[temp++] = symbol;
				}
		}
		while (!stack.isEmpty())
			result[temp++] = stack.pop();
		System.out.println(reverseInPlace(new String(result, 0, temp)));

	}

	static int precedence(char symbol) {
		switch (symbol) {

		case ')':
			return 0;
		case '+':
		case '-':
			return 1;
		case '*':
		case '/':
			return 2;

		default:
			return -1;
		}
	}

	private static int pr(char elem) { /* Function for precedence */
		switch (elem) {
		case '#':
			return 0;
		case ')':
			return 1;
		case '+':
		case '-':
			return 2;
		case '*':
		case '/':
			return 3;
		default:
			return 0; // ?
		}
	}

	static int checker(char symbol) {
		if (symbol == '\t' || symbol == ' ')
			return 0;
		else
			return 1;
	}

	public static String reverseInPlace(String s) {
		if (s == null)
			return null;
		StringBuilder sb = new StringBuilder(s); // !!!!
		for (int i = 0; i < sb.length() / 2; i++) { // crucial: length/2
			char tmp = sb.charAt(i);
			sb.setCharAt(i, sb.charAt(sb.length() - i - 1)); // crucial: length - i - 1!!!
			sb.setCharAt(sb.length() - i - 1, tmp);
		}
		return sb.toString();

	}

	public static void reverseWords(String[] words) {
		if (words == null || words.length == 0)
			return;
		for (int i = 0; i < words.length / 2; i++) {
			String tmp = words[i];
			words[i] = words[words.length - i - 1];
			words[words.length - i - 1] = tmp;

		}
	}

	public static String reverseText(String text) {

		if (text == null || text.length() == 0)
			return text;
		String result = null;
		text = reverseInPlace(text);
		text = text.trim();
		char[] chars = text.toCharArray();
		int start = 0;
		int end = 0;
		String toReverse = "";
		int length = chars.length;
		while (end < length) {
			if (end == length - 1 || chars[end] == ' ') {
				toReverse = new String(chars, start, end == length - 1 ? end - start + 1 : end - start);
				String reversed = reverseInPlace(toReverse);
				for (int i = 0; i < reversed.length(); i++)
					chars[start++] = reversed.charAt(i);
				start = end + 1;

			}
			end++;
		}
		result = new String(chars, 0, length);
		return result;

	}

	public static int[] incrementWithoutOperators(int[] number) {
		char[] chars = new char[number.length + 1];
		int[] result = new int[number.length + 1];
		int i;

		for (i = 0; i < number.length; i++)
			chars[i] = (char) (number[i] + '0');
		String string = new String(chars, 0, chars.length);
		int toNumber = stringToInt(string);
		toNumber++;
		string = intToString(toNumber);
		chars = string.toCharArray();
		for (i = 0; i < chars.length; i++)
			result[i] = chars[i] - '0';
		if (i < result.length)
			for (int j = i; j < result.length; j++)
				result[j] = '-';

		return result;

	}

	public static String intToString(int number) {
		StringBuilder string = new StringBuilder(); // !!!
		boolean isNeg = false;
		if (number < 0) {
			isNeg = true;
			number = -number;
		}
		// special case
		if (number == 0)
			string.append("0");
		while (number != 0) {
			string.append(number % 10);
			number = number / 10;
		}
		if (isNeg)
			string.append("-");
		return reverseInPlace(string.toString());
	}

	public static String firstNonrepeatedCharacter(String s) {
		String result = "";
		HashMap<Character, Object> charHash = new HashMap<>();
		Object once = new Object();
		Object many = new Object();
		char[] chars = s.toCharArray();
		for (Character c : chars) {
			Object value = charHash.get(c);
			if (value == null)
				charHash.put(c, once);
			else
				charHash.put(c, many);

		}
		for (Character c : chars) {
			Object howMany = charHash.get(c);
			if (once.equals(howMany))
				return String.valueOf(c);

		}
		return result;

	}

	public static String removeCharacters(String str, char[] toRemove) {
		// StringBuilder result = new StringBuilder();
		char[] chars = str.toCharArray();
		// build a hashtable
		HashMap<Character, Boolean> removeTable = new HashMap<>();
		for (Character r : toRemove)
			removeTable.put(r, true);
		int j = 0;
		for (int i = 0; i < chars.length; i++) {
			Boolean found = removeTable.get(chars[i]);
			if (found == null) {
				chars[j] = chars[i];
				j++;
			}
		}
		return new String(chars, 0, j);

	}

	public static void segregateOddEven(int[] array) {

		int i = 0;
		int j = array.length - 1;
		int tmp;

		while (i < j) {
			while (array[i] % 2 == 0 && i < j)
				i++;

			/* Decrement right index while we see 1 at right */
			while (array[j] % 2 == 1 && i < j)
				j--;

			if (i < j) {
				/* Swap arr[left] and arr[right] */
				tmp = array[i];
				array[i] = array[i];
				array[j] = tmp;
				i++;
				j--;
			}

		}

	}

	/* Iterative Function to calculate (a^n)%p in O(logy) */
	public static int power(int a, int n, int p) {
		int res = 1; // Initialize result
		// a = a % p; // Update 'a' if 'a' >= p

		while (n > 0) {
			// If n is odd, multiply 'a' with result; will be odd at least once, at the end
			// when n==1
			if ((n & 1) != 0)
				res = (res * a);

			// n must be even now
			n = n >> 1; // n = n/2
			a = (a * a);
		}
		return res;
	}

	// recursive
	static float power(float x, int y) {
		float temp;
		if (y == 0)
			return 1;
		if (y == 1)
			return x;
		temp = power(x, y / 2);
		if (y % 2 == 0)
			return temp * temp;
		else if (y > 0)
			return x * temp * temp;
		else
			return (temp * temp) / x;
	}

	// Find 0 < x < m so that a x ≡ 1 (mod m)
	static int moduloInverse(int a, int m) {
		if (a <= 0 || m <= 0)
			return -1; // should throw an exception
		a = a % m;
		for (int x = 1; x < m; x++)
			if ((a * x) % m == 1)
				return x;
		return -1;
	}

	// Ferma, am-1 ≡ 1 (mod m) so a-1 ≡ a m-2 (mod m) if a not divisible by the
	// prime m
	void modInverse(int a, int m) {
		int g = gcd(a, m);
		if (g != 1)
			System.out.println("Inverse doesn't exist");
		else
			System.out.println("Modular multiplicative inverse is " + power(a, m - 2, m));
	}

	// O (log min (a,b))
	static int gcd(int a, int b) { // a should be < b
		if (a > b) {
			int tmp = a;
			a = b;
			b = tmp;
		}

		if (a == 0)
			return b;
		if (a == 1)
			return 1;

		return gcd(b % a, a);
	} // gcd (1, n) = 1, gcd (0, n) = n!!!

	static int gcdIterative(int a, int b) { // a should be < b
		if (a > b) {
			int tmp = a;
			a = b;
			b = tmp;
		}

		int tmp;

		while (a != 0) {
			tmp = a;
			a = b % a;
			b = tmp;
		}

		// return a + b;
		return b;
	}

	// A simple method to evaluate Euler Totient Function
	// Euler’s Totient function Φ(n) for an input n is count of numbers in {1, 2, 3,
	// …, n} that are
	// relatively prime to n, i.e., the numbers whose GCD (Greatest Common Divisor)
	// with n is 1.
	// O(nlog 10 n) // cause gcd equals to number of digits in n, and that is log 10
	// n
	int phi(int n) {
		int count = 1;
		for (int i = 2; i < n; i++)
			if (gcd(i, n) == 1)
				count++;
		return count;
	}

	// Eurler's product formula: Φ(n) is equal to n multiplied by product of (1 –
	// 1/p) for all prime factors p of n. It's O(sqrt(n) * logp n)
	int phiEfficient(int n) {
		float result = n; // Initialize result as n
		// Consider all prime factors of n and for every prime
		// factor p, multiply result with (1 - 1/p)
		for (int p = 2; p * p <= n; p++)
			// Check if p is a prime factor.
			if (n % p == 0) {
				// If yes, then update n and result
				while (n % p == 0)
					n /= p;
				result *= (1.0 - (1.0 / p));
			}

		// If n has a prime factor greater than sqrt(n)
		// (There can be at-most one such prime factor)
		if (n > 1)
			result *= (1.0 - (1.0 / n));

		return (int) result;

	}

	// ax + by = gcd
	// (b%a).x1 + a.y1 = gcd
	// b = b - (b/a) *a and hence the main equation
	public static int gcdExtended(int a, int b, Integer x, Integer y) {

		if (a > b) {
			int tmp = a;
			a = b;
			b = tmp;
		}
		// Base Case
		if (a == 0) {
			x = 0;
			y = 1;
			return b;
		}

		Integer x1 = 1, y1 = 1; // To store results of recursive call
		int gcd = gcdExtended(b % a, a, x1, y1);

		// Update x and y using results of recursive
		// call
		x = y1 - (b / a) * x1;
		y = x1;

		System.out.println("X is " + x);
		System.out.println("Y is " + y);
		System.out.println("GCD is " + gcd);
		return gcd;
	}

	// algorithm for combinations!!!
	public static List produceBinaryNumbers(int n) {
		List list = new ArrayList<String>();
		String number = convertBinary(0, n);
		int count = 0;
		while (!allDigitsOne(number)) {
			count++;
			number = convertBinary(count, n);
			list.add(number);

		}
		return list;
	}

	private static boolean allDigitsOne(String binaryNumber) {
		boolean result = true;
		for (char c : binaryNumber.toCharArray())
			if (c == '0')
				return false;
		return result;

	}

	public static void produceCombinations(String string) {
		List<String> binaryNumbers = produceBinaryNumbers(string.length());
		StringBuilder sb = new StringBuilder();

		for (String b : binaryNumbers) {
			sb = new StringBuilder();
			for (int i = 0; i < b.length(); i++)
				if (b.charAt(i) == '1')
					sb.append(string.charAt(i));
			System.out.println("Combination: " + sb.toString());

		}

	}

	public static String convertBinary(int num, int length) {
		char binary[] = new char[length];
		int index = 0;
		for (int i = 0; i < length; i++)
			binary[i] = '0';
		while (num > 0) {
			binary[index++] = (char) (num % 2 + '0');
			num = num / 2;
		}
		String string = new String(binary, 0, length);
		return reverseInPlace(string);
	}

	public static void numberToBinary(int n) {
		int i;
		for (i = 1 << 31; i > 0; i >>= 1) // i = i/2
			if ((n & i) != 0)
				System.out.print("1");
			else
				System.out.print("0");
	}

	public static void bin(int n) {
		/* step 1 */
		if (n > 1)
			bin(n / 2);

		/* step 2 */
		System.out.print(n % 2);
	}

	public static void countAllBitsSetUpToN(int n) {
		int c, i, result = 0; // r is the total bits set up to n. p is the bits set in each number
		for (i = 1; i <= n; i++) {
			int partial = 0;
			for (c = i; c > 0; c >>= 1)
				partial += c & 1;
			// System.out.print(p);
			result += partial;
		}
		System.out.printf("\nTotal bits set: %d", result);
	}

	// a(0) = 0, a(2n) = a(n)+a(n-1)+n, a(2n+1) = 2a(n)+n+1, very cool, Amazon
	// question from
	// https://stackoverflow.com/questions/9812742/finding-the-total-number-of-set-bits-from-1-to-n
	public static int countAllBitsSet(int n) {

		// cache[0] = 0, otherwise -1, should be a global array variable

		// if cache[n] != -1 return cache[n];
		int result = n % 2 != 0 ? (2 * countAllBitsSet(n / 2) + n / 2 + 1)
				: (countAllBitsSet(n / 2) + countAllBitsSet(n / 2 - 1) + n / 2);
		return result;

	}

	public static void permute(String in, boolean[] used, StringBuilder out) {
		// StringBuilder out = new StringBuilder();
		if (out.length() == in.length()) {
			System.out.println("Permutation: " + out);
			out = new StringBuilder();
			return;
		}
		for (int i = 0; i < in.length(); ++i) {
			if (used[i])
				continue;
			out.append(in.charAt(i));
			used[i] = true;
			permute(in, used, out);
			used[i] = false;
			out.setLength(out.length() - 1); // the appended character is deleted
		}
	}
	// BEST!!

	static void combinations2(String[] arr, int len, int startPosition, String[] result) {
		if (len == 0) {
			System.out.println(Arrays.toString(result));
			return;
		}
		for (int i = startPosition; i <= arr.length - len; i++) {
			result[result.length - len] = arr[i];
			combinations2(arr, len - 1, i + 1, result);
		}
	}

	private static void permutation(String permutation, String input) {
		int n = input.length();
		if (n == 0) {
			System.out.println("Permutations: " + permutation);
			permutation = "";
			return;
		} else
			for (int i = 0; i < n; i++)
				permutation(permutation + input.charAt(i), input.substring(0, i) + input.substring(i + 1));
	}

	// swap the characters at indices i and j
	private static void swap(char[] a, int i, int j) {
		char c = a[i];
		a[i] = a[j];
		a[j] = c;
	}

	public static void perm1(char[] a, int i) {
		if (i == a.length - 1) {
			// print the shuffled string
			System.out.println(String.valueOf(a));
			return;
		} else
			for (int j = i; j < a.length; j++) {
				swap(a, i, j);
				perm1(a, i + 1);
				swap(a, i, j);
			}
	}

	// MISSING ITERATIVE PERMUTATIONS, LATER....

	/**
	 * Returns an array list containing all permutations of the characters in s.
	 */
	public static ArrayList<String> permute(String s) { // you can rewrite more modern loops and collections
		ArrayList<String> perms = new ArrayList<>();
		int slen = s.length();
		if (slen > 0) {
			// Add the first character from s to the perms array list.
			perms.add(Character.toString(s.charAt(0)));

			// Repeat for all additional characters in s.
			for (int i = 1; i < slen; ++i) {

				// Get the next character from s.
				char c = s.charAt(i);

				// For each of the strings currently in perms do the following:
				int size = perms.size();
				for (int j = 0; j < size; ++j) {

					// 1. remove the string
					String p = perms.remove(0);
					int plen = p.length();

					// 2. Add plen + 1 new strings to perms. Each new string
					// consists of the removed string with the character c
					// inserted into it at a unique location.
					for (int k = 0; k <= plen; ++k)
						perms.add(p.substring(0, k) + c + p.substring(k));
				}
			}
		}
		return perms;
	}

	/**
	 * Iterative version of "permutation" method above. O(n!) as per
	 * http://www.ardendertat.com/2011/10/28/programming-interview-questions-11-all-permutations-of-string/
	 * 
	 * @param prefix
	 * @param word
	 */

	public void nonRecpermute(String prefix, String word) {
		String[] currentstr = { prefix, word };
		Stack<String[]> stack = new Stack<String[]>();
		stack.add(currentstr);
		while (!stack.isEmpty()) {
			currentstr = stack.pop();
			String currentPrefix = currentstr[0];
			String currentWord = currentstr[1];
			if (currentWord.equals(""))
				System.out.println("Word =" + currentPrefix);
			for (int i = 0; i < currentWord.length(); i++) {
				String[] newstr = new String[2];
				newstr[0] = currentPrefix + String.valueOf(currentWord.charAt(i));
				newstr[1] = currentWord.substring(0, i); // would be better to use String.valueOf and charArray as in
															// https://docs.oracle.com/javase/7/docs/api/java/lang/String.html
				if (i < currentWord.length() - 1)
					newstr[1] = newstr[1] + currentWord.substring(i + 1);
				stack.push(newstr);
			}

		}

	}

	public static void printWords(int curDigit, int[] phoneNum, char[] result) {
		if (curDigit == PHONE_NUMBER_LENGTH - 1) {
			System.out.println(new String(result));
			return;
		}
		for (int i = 1; i <= 3; ++i) {
			result[curDigit] = getCharKey(phoneNum[curDigit], i);
			printWords(curDigit + 1, phoneNum, result);
			if (phoneNum[curDigit] == 0 || phoneNum[curDigit] == 1)
				return;
		}
	}

	public static void printWordsIterative(int[] phoneNum) { // insufficient

		char[] result = new char[phoneNum.length];

		// Initialize result with first telephone word
		for (int i = 0; i < PHONE_NUMBER_LENGTH; ++i)
			result[i] = getCharKey(phoneNum[i], 1);

		for (int j = 0; j < result.length; j++)
			for (int i = 1; i <= 3; ++i) {
				result[j] = getCharKey(phoneNum[j], i);
				System.out.println(new String(result));
				if (phoneNum[j] == 0 || phoneNum[j] == 1)
					break;

			}

	}

	public static void printWords(int[] phoneNum, char[] result) {
		int count = 0;
		// Initialize result with first telephone word
		for (int i = 0; i < PHONE_NUMBER_LENGTH; ++i)
			result[i] = getCharKey(phoneNum[i], 1);

		for (;;) {

			System.out.println(new String(result));
			count++;

			/*
			 * Start at the end and try to increment from right * to left.
			 */
			for (int i = PHONE_NUMBER_LENGTH - 1; i >= -1; --i) {
				if (i == -1) {
					System.out.println("Count: " + count);
					return;
				}
				/*
				 * Start with special cases are dealt with right away
				 */
				if (getCharKey(phoneNum[i], 3) == result[i] || phoneNum[i] == 0 || phoneNum[i] == 1)
					result[i] = getCharKey(phoneNum[i], 1);
				// No break, so loop continues to next digit
				else if (getCharKey(phoneNum[i], 1) == result[i]) {
					result[i] = getCharKey(phoneNum[i], 2);
					break;
				} else if (getCharKey(phoneNum[i], 2) == result[i]) {
					result[i] = getCharKey(phoneNum[i], 3);
					break;
				}
			}
		}

	}

	private static char getCharKey(int i, int i2) {
		// char[] letters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
		// 'l', 'm', 'n', 'o', 'p', 'q', 'r',
		// 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		if (i == 0 || i == 1)
			return (char) (i + '0');
		String[] letters = new String[10];
		letters[2] = "abc";
		letters[3] = "def";
		letters[4] = "ghi";
		letters[5] = "jkl";
		letters[6] = "mno";
		letters[7] = "prs";
		letters[8] = "tuv";
		letters[9] = "wxy"; // q and z not used
		return letters[i].toCharArray()[i2 - 1];

	}

	int numOnesInBinary(int number) {
		int numOnes = 0;
		while (number != 0) {
			if ((number & 1) == 1)
				numOnes++;
			number = number >>> 1; // double right shift operator (unsigned!!!)
		}
		return numOnes;
	}

	int numOfOnesInBinary(int number) {
		int numOnes = 0;
		while (number != 0) {
			number = number & (number - 1);
			numOnes++;
		}
		return numOnes;
	}

	// Chinese Remainder Theorem!!

	int findMinX(int num[], int rem[], int k) {
		int x = 1; // Initialize result

		// As per the Chinese remainder theorem,
		// this loop will always break!!! 0 ≤ x < N, where N is product of all num[i]!
		while (true) {
			// Check if remainder of x % num[j] is
			// rem[j] or not (for all j from 0 to k-1)
			int j;
			for (j = 0; j < k; j++)
				if (x % num[j] != rem[j])
					break;

			// If all remainders matched, we found x
			if (j == k)
				return x;

			// Else try next number
			x++;
		}

		// return x;
	}

	public static int findInverse(int a, int m) {

		for (int x = 1; x < m; x++)
			if (a * x % m == 1)
				return x;

		if (true)
			throw new RuntimeException("Does not exist...");
		return Integer.MIN_VALUE;
	}

	// If n is prime, then always returns true, If n is
	// composite than returns false with high probability
	// Higher value of k increases probability of correct
	// result.
	public static boolean isPrimeByFermat(int n, int k) {
		// Corner cases
		if (n <= 1 || n == 4) // 0 1 4
			return false;
		if (n <= 3) // 2 3
			return true;

		// Try k times
		while (k > 0) {
			// Pick a random number in [5..n-1]
			// Above corner cases make sure that n > 4
			int a = 5 + new Random().nextInt(n - 6);

			// Fermat's little theorem
			if (power(a, n - 1, n) != 1)
				return false;

			k--;
		}

		return true;
	}

	// non-dynamic solution n choose k
	static int binomialCoeff(int n, int k) {
		int res = 1;

		// Since C(n, k) = C(n, n-k)
		if (k > n - k)
			k = n - k;

		// Calculate value of [n * (n-1) *---* (n-k+1)] / [k * (k-1) *----* 1]
		for (int i = 0; i < k; ++i) {
			res *= (n - i);
			res /= (i + 1);
		}

		return res;
	}

	// space optimized!!
	public static int binomialCoeffic(int n, int k, int p) {
		int[] C = new int[k + 1];

		C[0] = 1; // nC0 is 1
		for (int i = 1; i <= n; i++)
			// Compute next row of pascal triangle using
			// the previous row
			for (int j = Math.min(i, k); j > 0; j--)
				C[j] = (C[j] + C[j - 1]) % p;
		return C[k];
	}

	// not great recursive
	static long binomi(int n, int k) {
		if ((n == k) || (k == 0))
			return 1;
		else
			return binomi(n - 1, k) + binomi(n - 1, k - 1);
	}

	// Lucas Theorem based function that returns nCr % p
	// This function works like decimal to base p conversion
	// recursive function. First we compute last digits of
	// n and r in base p, then recur for remaining digits
	// O(p^2 log p n)
	int nCrModpLucas(int n, int r, int p) {
		// Base case
		if (r == 0)
			return 1;

		// Compute last digits of n and r in base p
		int ni = n % p, ri = r % p;

		// Compute result for last digits computed above, and
		// for remaining digits. Multiply the two results and
		// compute the result of multiplication in modulo p.
		return (nCrModpLucas(n / p, r / p, p) * // Last digits of n and r
				binomialCoeffic(ni, ri, p)) % p; // Remaining digits
	}

	// JAVA Code to find Orientation of 3
	// ordered points
	class Point {
		int x, y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	// To find orientation of ordered triplet
	// (p1, p2, p3). The function returns
	// following values
	// 0 --> p, q and r are colinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	public static int orientation(Point p1, Point p2, Point p3) {
		// See 10th slides from following link
		// for derivation of the formula
		int val = (p2.y - p1.y) * (p3.x - p2.x) - (p2.x - p1.x) * (p3.y - p2.y);

		if (val == 0)
			return 0; // colinear

		// clock or counterclock wise
		return (val > 0) ? 1 : 2;
	}

	// Prints convex hull of a set of n points. O(mn) where m number of hull points
	// in the result
	public static void convexHull(Point points[], int n) {
		// There must be at least 3 points
		if (n < 3)
			return;

		// Initialize Result
		List<Point> hull = new ArrayList<Point>();

		// Find the leftmost point
		int l = 0;
		for (int i = 1; i < n; i++)
			if (points[i].x < points[l].x)
				l = i;

		// Start from leftmost point, keep moving
		// counterclockwise until reach the start point
		// again. This loop runs O(h) times where h is
		// number of points in result or output.
		int p = l, q; // x cooordinate fo every point
		do {
			// Add current point to result
			hull.add(points[p]);

			// Search for a point 'q' such that
			// orientation(p, x, q) is counterclockwise
			// for all points 'x'. The idea is to keep
			// track of last visited most counterclock-
			// wise point in q. If any point 'i' is more
			// counterclock-wise than q, then update q.
			q = (p + 1) % n;

			for (int i = 0; i < n; i++)
				// If i is more counterclockwise than
				// current q, then update q
				if (orientation(points[p], points[i], points[q]) == 2)
					q = i;

			// Now q is the most counterclockwise with
			// respect to p. Set p as q for next iteration,
			// so that q is added to result 'hull'
			p = q;

		} while (p != l); // While we don't come to first
							// point

		// Print Result
		for (Point temp : hull)
			System.out.println("(" + temp.x + ", " + temp.y + ")");
	}

	public static int maxSubarrayXOR(int arr[], int n) {
		int ans = Integer.MIN_VALUE; // Initialize result

		// Pick starting points of subarrays
		for (int i = 0; i < n; i++) {
			// to store xor of current subarray
			int curr_xor = 0;

			// Pick ending points of subarrays starting with i
			for (int j = i; j < n; j++) {
				curr_xor = curr_xor ^ arr[j];
				ans = Math.max(ans, curr_xor);
			}
		}
		return ans;
	}

	// Function to find nth magic numebr
	int nthMagicNo(int n) {
		int pow = 1, answer = 0;

		// Go through every bit of n
		while (n != 0) {
			pow = pow * 5;

			// If last bit of n is set
			if ((n & 1) != 0)
				answer += pow;

			// proceed to next bit
			n >>= 1; // or n = n/2
		}
		return answer;
	}

	// program to compute sum of pairwise bit differences

	public static int sumBitDifferences(int arr[], int n) {
		int ans = 0; // Initialize result

		// traverse over all bits
		for (int i = 0; i < 32; i++) {
			// count number of elements with i'th bit set
			int count = 0;
			for (int j = 0; j < n; j++)
				if ((arr[j] & (1 << i)) != 0)
					count++;

			// Add "count * (n - count) * 2" to the answer
			ans += (count * (n - count) / 2); // ??
		}

		return ans;
	}

	// program to swap even and odd bits of a given number

	public static int swapBits(int x) {
		// Get all even bits of x
		int even_bits = x & 0xAAAAAAAA;

		// Get all odd bits of x
		int odd_bits = x & 0x55555555;

		even_bits >>= 1; // Right shift even bits
		odd_bits <<= 1; // Left shift odd bits

		return (even_bits | odd_bits); // Combine even and odd bits
	}

	public static void main(String[] args) {
		String string = "-------111177dsdsds23";
		int number = stringToInt(string);
		System.out.println(number);
		Integer[] array = { 1, 2, 3, 4, 5 };
		int min = findMinimum(Arrays.asList(array));
		System.out.println(min);
		int[] array2 = { 7, 8 };
		min = select2(array2, array2.length);
		System.out.println("Smallest element is " + min);
		// insertsort(array2);
		for (int ele : array2)
			System.out.println(ele);
		insertsort2(array2);
		for (int ele : array2)
			System.out.println("Insertion sort" + ele);

		String[] strings = { "1", "28", "3", "45" };
		// findMiddleElement(strings);
		string = intToString(-0);
		System.out.println("Int to string " + string);
		string = firstNonrepeatedCharacter("-------177dsdsds23");
		System.out.println(string);
		string = removeCharacters("-------177dsdsds23", "12".toCharArray());
		System.out.println(string);
		reverseWords(strings);
		for (String s : strings)
			System.out.println(s);
		string = reverseText("Rada Despotovic");
		System.out.println(string);
		array2 = incrementWithoutOperators(array2);
		for (int ele : array2)
			System.out.println(ele);
		int result = power(2, 10, 1);
		// List list = produceBinaryNumbers(5);
		produceCombinations("abcd");
		boolean[] used = { false, false, false };
		// permute("abc", used, new StringBuilder());
		// permutation("", "abc");
		// perm2("abc".toCharArray(), 3);
		// helper("abc".toCharArray(), 0);
		List<String> list = permute("abc");
		for (String s : list)
			System.out.println(s);
		char c = getCharKey(0, 1);
		System.out.println(c);
		c = getCharKey(1, 2);
		System.out.println(c);
		c = getCharKey(7, 3);
		System.out.println(c);
		int[] phoneNum = { 1, 2, 3, 4, 5, 6, 7 };
		char[] results = { '0', '0', '0', '0', '0', '0', '0' };
		// printWords(0, phoneNum, results);
		// printWordsIterative(phoneNum);
		// printWords(phoneNum, results);
		System.out.println("Test right shift operator: " + (13 >> 1));
		List<Integer> prime = new ArrayList<>();
		sieveOfEratosthenes(10, prime);
		BitSet set = genPrimes(10);
		System.out.println(set);
		System.out.println(gcd(0, 15));
		System.out.println(gcdIterative(30, 15));
		System.out.println(gcdExtended(1, 15, 1, 1));
		// "(((a–b)/c)*((d+e)–(f/g)))"
		System.out.println(printPreFix("(((a–b)/c)*((d+e)–(f/g))))")); // */-abc-+de/fg
		infix_to_prefix("(((a–b)/c)*((d+e)–(f/g)))".toCharArray());

	}
}
