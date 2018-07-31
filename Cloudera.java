
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Cloudera {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		char[] chars = decimalToBinary(50);
		System.out.println(String.valueOf(chars));
	}

	public static float sqrt(int number) {
		if (number < 0)
			return -1;
		if (number == 0 || number == 1)
			return number;
		float start = 0.0f;
		float end = number;
		float precision = 0.001f;
		float middle = start;
		float difference = (float) Math.abs(Math.pow(middle, 2) - number);
		while (difference >= precision) {
			middle = (start + end) / 2.0f;
			if (Math.pow(middle, 2) > number)
				end = middle;
			else
				start = middle;
			difference = (float) Math.abs(Math.pow(middle, 2) - number);
		}
		return middle;
	}

	// javarevisited.blogspot.com/2016/10/how-to-find-square-root-of-number-in-java-algorithm.html#ixzz55Wp2kxf4

	public static class LinkedListNode {
		int val;
		LinkedListNode next;

		LinkedListNode(int node_value) {
			val = node_value;
			next = null;
		}
	};

	public static LinkedListNode _insert_node_into_singlylinkedlist(LinkedListNode head, LinkedListNode tail, int val) {
		if (head == null) {
			head = new LinkedListNode(val);
			tail = head;
		} else {
			tail.next = new LinkedListNode(val);
			tail = tail.next;
		}
		return tail;
	}

	static LinkedListNode removeNodes(LinkedListNode list, int x) {
		if (list == null)
			return null;
		LinkedListNode result = null;
		LinkedListNode result_tail = null;
		int element;
		int i = 0;

		while (list != null) {
			element = list.val;
			if (element <= x) {
				i++;
				// first element in the result treated specially
				if (i == 1) {
					result = _insert_node_into_singlylinkedlist(result, result_tail, element);
					result_tail = result;
				} else
					result_tail = _insert_node_into_singlylinkedlist(result, result_tail, element);
			}
			list = list.next;
		}
		return result;
	}

	static String mergeStrings(String a, String b) {
		final int lengthA = a.length(), lengthB = b.length();
		StringBuilder mixedString = new StringBuilder(lengthA + lengthB);
		int resultLength = Math.min(lengthA, lengthB);
		// mix characters
		for (int i = 0; i < resultLength; i++)
			mixedString.append(a.charAt(i)).append(b.charAt(i));
		// append the remaining characters
		mixedString.append(a, resultLength, lengthA).append(b, resultLength, lengthB);
		return mixedString.toString();

	}

	static int printMaxOfMin(int x, int[] arr) {
		// Initialize max of min for current window size k
		int maxOfMin = Integer.MIN_VALUE;
		int length = arr.length;

		// Traverse through all windows of current size k
		for (int i = 0; i <= length - x; i++) {
			// Find minimum of current window
			int min = arr[i];
			for (int j = 1; j < x; j++)
				if (arr[i + j] < min)
					min = arr[i + j];

			// Update maxOfMin if required
			if (min > maxOfMin)
				maxOfMin = min;
		}
		return maxOfMin;
	}

	private static boolean isBalanced(String expression) {

		if (expression == null || expression.length() == 0)
			return false;

		char openCurl = '{';
		char openBrac = '[';
		char openPare = '(';

		char closeCurl = '}';
		char closeBrac = ']';
		char closePare = ')';

		int length = expression.length();
		char[] exp = expression.toCharArray();
		Stack<Character> stack = new Stack<>();

		for (int i = 0; i < length; i++) {
			if (exp[i] == openCurl || exp[i] == openBrac || exp[i] == openPare)
				stack.push(exp[i]);
			if (exp[i] == closeCurl || exp[i] == closeBrac || exp[i] == closePare)
				if (!stack.isEmpty()) {
					char lastElementPopped = stack.pop();
					if (exp[i] == closeCurl && lastElementPopped != openCurl)
						return false;
					if (exp[i] == closeBrac && lastElementPopped != openBrac)
						return false;
					if (exp[i] == closePare && lastElementPopped != openPare)
						return false;
				} else
					return false;
		}
		if (stack.isEmpty())
			return true;
		else
			return false;
	}

	static String[] braces(String[] values) {
		if (values == null || values.length == 0)
			return values;
		String[] result = new String[values.length];
		for (int i = 0; i < values.length; i++)
			if (isBalanced(values[i]))
				result[i] = "YES";
			else
				result[i] = "NO";

		return result;

	}

	private static Map<Character, Integer> romanCharacters = new HashMap<>();

	private static void init() {
		romanCharacters.put('I', 1);
		romanCharacters.put('V', 5);
		romanCharacters.put('X', 10);
		romanCharacters.put('L', 50);
	}

	private static int convertRomanCharactersToNumber(String romanString) {

		char[] arr = romanString.toCharArray();
		int total = 0;
		int lastSeen = 0;
		// reverse traversal
		for (int i = arr.length - 1; i >= 0; i--) {
			int value = romanCharacters.get(arr[i]);
			if (value >= lastSeen) {
				// bigger number needs to be added to current result; LX = 60 (10+50=60)
				lastSeen = value;
				total += value;
			} else
				// smaller number needs to be subtracted from current result; XL = 40 (50-10=40)
				total -= value;
		}
		return total;
	}

	private static class RomanNameComparator implements Comparator<String> {

		public RomanNameComparator() {
			super();
			init();
		}

		@Override
		public int compare(String royalName1, String royalName2) {

			String[] royalNameSplit1 = royalName1.split(" ");
			String[] royalNameSplit2 = royalName2.split(" ");

			if (royalNameSplit1[0].compareTo(royalNameSplit2[0]) == 0) {
				int romanNumber1 = convertRomanCharactersToNumber(royalNameSplit1[1]);
				int romanNumber2 = convertRomanCharactersToNumber(royalNameSplit2[1]);

				return (romanNumber1 == romanNumber2 ? 0 : (romanNumber1 > romanNumber2 ? 1 : -1));

			} else
				return royalNameSplit1[0].compareTo(royalNameSplit2[0]);
		}
	}

	static String[] getSortedList(String[] names) {

		RomanNameComparator romanNameComparator = new RomanNameComparator();
		Arrays.sort(names, romanNameComparator);
		return names;

	}

	private static char[] decimalToBinary(int n) {
		StringBuffer sb = new StringBuffer();
		while (n > 0) {
			sb.append(n % 2);
			n >>= 1; // n = n/2
		}
		return sb.reverse().toString().toCharArray();
	}

	private static int binaryToDecimal(char[] binary) {
		int decimal = 0;
		int power = 1;
		for (int i = (binary.length - 1); i >= 0; i--) {
			char c = binary[i]; // Integer.valueOf(c - '0');
			decimal = decimal + (Integer.parseInt(c + "") * power);
			power <<= 1; // power = power * 2;
		}
		return decimal;
	}

}
