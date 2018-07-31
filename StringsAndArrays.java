import java.util.ArrayList;
import java.util.Arrays;

public class StringsAndArrays {

	// palindromic partitions; these are combinations of all substrings, of full
	// length n!!!
	private void addPalindrome(String s, int start, ArrayList<String> partition, ArrayList<ArrayList<String>> result) {
		// stop condition
		if (start == s.length()) {
			ArrayList<String> temp = new ArrayList<String>(partition);
			result.add(temp);
			return;
		}

		for (int i = start + 1; i <= s.length(); i++) {
			String str = s.substring(start, i);
			if (isPalindrome(str)) {
				partition.add(str);
				addPalindrome(s, i, partition, result);
				partition.remove(partition.size() - 1);
			}
		}
	}

	private boolean isPalindrome(String str) {
		int left = 0;
		int right = str.length() - 1;

		while (left < right) {
			if (str.charAt(left) != str.charAt(right))
				return false;

			left++;
			right--;
		}

		return true;
	}

	static void zigZag(int[] arr) {
		// Flag true indicates relation "<" is expected,
		// else ">" is expected. The first expected relation
		// is "<"
		boolean flag = true;

		int temp = 0;

		for (int i = 0; i <= arr.length - 2; i++) {
			if (flag) /* "<" relation expected */
			{
				/*
				 * If we have a situation like A > B > C, we get A > B < C by swapping B and C
				 */
				if (arr[i] > arr[i + 1]) {
					// swap
					temp = arr[i];
					arr[i] = arr[i + 1];
					arr[i + 1] = temp;
				}

			} else /*
					 * If we have a situation like A < B < C, we get A < C > B by swapping B and C
					 */
			if (arr[i] < arr[i + 1]) {
				// swap
				temp = arr[i];
				arr[i] = arr[i + 1];
				arr[i + 1] = temp;
			}
			flag = !flag; /* flip flag */
		}
	}

	// Function to print all substring
	public static void SubString(String str, int n) {
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j <= n; j++) // interesting : <=n!!!

				// Please refer below article for details
				// of substr in Java
				// http://www.geeksforgeeks.org/java-lang-string-substring-java/
				System.out.println(str.substring(i, j));
	}

	// most efficient!!
	public static void subString(char[] str, int n) {

		for (int i = 0; i < n; i++) {
			StringBuffer result = new StringBuffer("");
			result.append(str[i]);
			for (int k = i + 1; k <= n; k++) {
				System.out.println(result);
				if (k < n)
					result.append(str[k]);
			}
		}
	}

	// called with substringsRecursive("12345", ""); has a problem with
	// non-consecutives present in result - these are actually COMBINATIONS!!!!
	public static void substringsRecursive(String initial, String combined) {
		System.out.print(combined + " ");
		for (int i = 0; i < initial.length(); i++)
			substringsRecursive(initial.substring(i + 1), combined + initial.charAt(i));
	}

	public static void substrings(String str, String temp) {
		if (str.length() == 0) {
			System.out.println(temp);
			return;
		}

		substrings(str.substring(1), temp + str.substring(0, 1)); // with first character
		substrings(str.substring(1), temp); // without first character, just go through all substrings starting with
											// second/next character
	}

	// alternate elements from 2 sorted arrays
	/*
	 * Function to generates and prints all sorted arrays from alternate elements of
	 * 'A[i..m-1]' and 'B[j..n-1]' If 'flag' is true, then current element is to be
	 * included from A otherwise from B. 'len' is the index in output array C[]. We
	 * print output array each time before including a character from A only if
	 * length of output array is greater than 0. We try than all possible
	 * combinations
	 */
	void generateAlternateArrayRecursive(int A[], int B[], int C[], int i, int j, int m, int n, int len, boolean flag) {
		if (flag) // Include valid element from A
		{
			// Print output if there is at least one 'B' in output array 'C'; we know that
			// previous call was from B!
			if (len != 0)
				printArr(C, len + 1);

			// Recur for all elements of A after current index
			for (int k = i; k < m; k++)
				if (len == 0) {
					/*
					 * this block works for the very first call to include the first element in the
					 * output array
					 */
					C[len] = A[k];

					// don't increment length as B is not included yet?
					generateAlternateArrayRecursive(A, B, C, k + 1, j, m, n, len, !flag);
				}

				/* include valid element from A and recur */
				else if (A[k] > C[len]) {
					C[len + 1] = A[k];
					generateAlternateArrayRecursive(A, B, C, k + 1, j, m, n, len + 1, !flag);
				}
		} else
			for (int l = j; l < n; l++)
				if (B[l] > C[len]) {
					C[len + 1] = B[l];
					generateAlternateArrayRecursive(A, B, C, i, l + 1, m, n, len + 1, !flag);
				}
	}

	/* Wrapper function */
	void generateAlternateArray(int A[], int B[], int m, int n) {
		int C[] = new int[m + n];

		/* output array */
		generateAlternateArrayRecursive(A, B, C, 0, 0, m, n, 0, true);
	}

	// A utility function to print an array
	void printArr(int arr[], int n) {
		for (int i = 0; i < n; i++)
			System.out.print(arr[i] + " ");
		System.out.println("");
	}

	static int countTriplets(int[] arr, int n, int sum) {
		// Sort input array
		Arrays.sort(arr);

		// Initialize result
		int ans = 0;

		// Every iteration of loop counts triplet with
		// first element as arr[i].
		for (int i = 0; i < n - 2; i++) {
			// Initialize other two elements as corner elements
			// of subarray arr[j+1..k]
			int j = i + 1, k = n - 1;

			// Use Meet in the Middle concept
			while (j < k)
				// If sum of current triplet is more or equal,
				// move right corner to look for smaller values
				if (arr[i] + arr[j] + arr[k] >= sum)
					k--;

				// Else move left corner
				else {
					// This is important. For current i and j, there
					// can be total k-j + 1? third elements.
					ans += (k - j);
					j++;
				}
		}
		return ans;
	}

	// Returns true if there is a triplet with following property
	// A[i]*A[i] = A[j]*A[j] + A[k]*[k]
	// Note that this function modifies given array
	static boolean isPitagoreanTriplet(int arr[], int n) {
		// Square array elements
		for (int i = 0; i < n; i++)
			arr[i] = arr[i] * arr[i];

		// Sort array elements
		Arrays.sort(arr);

		// Now fix one element one by one and find the other two
		// elements
		for (int i = n - 1; i >= 2; i--) {
			// To find the other two elements, start two index
			// variables from two corners of the array and move
			// them toward each other
			int l = 0; // index of the first element in arr[0..i-1]
			int r = i - 1; // index of the last element in arr[0..i-1]
			while (l < r) {
				// A triplet found
				if (arr[l] + arr[r] == arr[i])
					return true;

				// Else either move 'l' or 'r'
				if (arr[l] + arr[r] < arr[i])
					l++;
				else
					r--;
			}
		}

		// If we reach here, then no triplet found
		return false;
	}

	// Returns length of the longest contiguous subarray (1, 2, 3...)
	int findLength(int arr[], int n) {
		int max_len = 1; // Initialize result
		for (int i = 0; i < n - 1; i++) {
			// Initialize min and max for all subarrays starting with i
			int mn = arr[i], mx = arr[i];

			// Consider all subarrays starting with i and ending with j
			for (int j = i + 1; j < n; j++) {
				// Update min and max in this subarray if needed
				mn = Math.min(mn, arr[j]);
				mx = Math.max(mx, arr[j]);

				// If current subarray has all contiguous elements
				if ((mx - mn) == j - i)
					max_len = Math.max(max_len, mx - mn + 1);
			}
		}
		return max_len; // Return result
	}

	// Returns length of smallest subarray with sum greater than x.
	// If there is no subarray with given sum, then returns n+1
	static int smallestSubWithSumGreaterThan(int arr[], int n, int x) {
		// Initialize current sum and minimum length
		int curr_sum = 0, min_len = n + 1;

		// Initialize starting and ending indexes
		int start = 0, end = 0;
		while (end < n) {
			// Keep adding array elements while current sum
			// is smaller than x
			while (curr_sum <= x && end < n)
				curr_sum += arr[end++];

			// If current sum becomes greater than x.
			while (curr_sum > x && start < n) {
				// Update minimum length if needed
				if (end - start < min_len)
					min_len = end - start;

				// remove starting elements
				curr_sum -= arr[start++];
			}
		}
		return min_len;
	}

	/*
	 * Returns true if the there is a subarray of arr[] with sum EQUAL to 'sum'
	 * otherwise returns false. Also, prints the result
	 */
	int subArraySum(int arr[], int n, int sum) {
		int curr_sum = arr[0], start = 0, i;

		// Pick a starting point
		for (i = 1; i <= n; i++) {
			// If curr_sum exceeds the sum, then remove the starting elements
			while (curr_sum > sum && start < i - 1) {
				curr_sum = curr_sum - arr[start];
				start++;
			}

			// If curr_sum becomes equal to sum, then return true
			if (curr_sum == sum) {
				int p = i - 1;
				System.out.println("Sum found between indexes " + start + " and " + p);
				return 1;
			}

			// Add this element to curr_sum
			if (i < n)
				curr_sum = curr_sum + arr[i];

		}

		System.out.println("No subarray found");
		return 0;
	}

	// recursive solution!!!
	boolean isGroupSum(int[] arr, int target, int start) {
		/* Base condition */
		if (start >= arr.length)
			return (target == 0);

		/* we found the target sum, return true */
		if (target == 0)
			return true;

		/*
		 * else, check if sum can be obtained by any of the following 1) Including the
		 * current element 2) Excluding the current element
		 */
		return isGroupSum(arr, target - arr[start], start + 1) || isGroupSum(arr, target, start + 1);
	}

	// see
	// https://codereview.stackexchange.com/questions/57943/determining-maximum-profit-to-be-made-from-selling-shares
	public void buySell(int[] prices) {
		long[] maximums = new long[prices.length];
		maximums[prices.length - 1] = prices[prices.length - 1];
		// fill maximums array, from the end
		for (int i = prices.length - 2; i >= 0; i--)
			if (prices[i] > maximums[i + 1])
				maximums[i] = prices[i];
			else
				maximums[i] = maximums[i + 1];

		// Trade!
		long profit = 0;
		for (int i = 0; i < prices.length; i++)
			if (prices[i] > maximums[i])
				System.out.println("Sell on day: " + i); // sell on this day
			else if (prices[i] < maximums[i]) {
				profit += maximums[i] - prices[i];
				System.out.println("Buy on day: " + i + " for future profit " + (maximums[i] - prices[i]));
			} // or else do nothing?

	}

	// Solution structure
	class Interval {
		int buy, sell;
	}

	// simple solution for buy sell when overlaps not allowed
	// This function finds the buy sell schedule for maximum profit
	void stockBuySell(int price[], int n) {
		// Prices must be given for at least two days
		if (n == 1)
			return;

		int count = 0;

		// solution array
		ArrayList<Interval> sol = new ArrayList<Interval>();

		// Traverse through given price array
		int i = 0;
		while (i < n - 1) {
			// Find Local Minima. Note that the limit is (n-2) as we are
			// comparing present element to the next element.
			while ((i < n - 1) && (price[i + 1] <= price[i]))
				i++;

			// If we reached the end, break as no further solution possible
			if (i == n - 1)
				break;

			Interval e = new Interval();
			e.buy = i++;
			// Store the index of minima

			// Find Local Maxima. Note that the limit is (n-1) as we are
			// comparing to previous element
			while ((i < n) && (price[i] >= price[i - 1]))
				i++;

			// Store the index of maxima
			e.sell = i - 1;
			sol.add(e);

			// Increment number of buy/sell
			count++;
		}

		// print solution
		if (count == 0)
			System.out.println("There is no day when buying the stock " + "will make profit");
		else
			for (int j = 0; j < count; j++)
				System.out.println("Buy on day: " + sol.get(j).buy + "        " + "Sell on day : " + sol.get(j).sell);

		return;
	}

	// max difference between 2 elements (maximize profit but only 1 buy/sell), O(n)
	int maxDiff(int arr[], int arr_size) {
		int max_diff = arr[1] - arr[0]; // max difference
		int min_element = arr[0]; // min element; result is final max difference between any element and min
									// element
		int i;
		for (i = 1; i < arr_size; i++) {
			if (arr[i] - min_element > max_diff)
				max_diff = arr[i] - min_element;
			if (arr[i] < min_element)
				min_element = arr[i];
		}
		return max_diff;
	}
}
