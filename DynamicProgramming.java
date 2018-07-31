import java.util.Arrays;

public class DynamicProgramming {

	public static int longestCommonSubsequence(char[] X, char[] Y, int m, int n) {
		if (m == 0 || n == 0)
			return 0;
		if (X[m - 1] == Y[n - 1])
			return 1 + longestCommonSubsequence(X, Y, m - 1, n - 1);
		else
			return Math.max(longestCommonSubsequence(X, Y, m, n - 1), longestCommonSubsequence(X, Y, m - 1, n));
	}

	public static int lcsIterative(char[] X, char[] Y, int m, int n) {
		int L[][] = new int[m + 1][n + 1];

		/*
		 * Following steps build L[m+1][n+1] in bottom up fashion. Note that L[i][j]
		 * contains length of LCS of X[0..i-1] and Y[0..j-1]
		 */
		for (int i = 0; i <= m; i++)
			for (int j = 0; j <= n; j++)
				if (i == 0 || j == 0)
					L[i][j] = 0;
				else if (X[i - 1] == Y[j - 1])
					L[i][j] = L[i - 1][j - 1] + 1;
				else
					L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
		return L[m][n];
	}

	// naive, but dynamic...
	public static int longestIncreasingSubsequence(int arr[], int n) {
		int lis[] = new int[n];
		int i, j, max = 0;

		/* Initialize LIS values for all indexes */
		for (i = 0; i < n; i++)
			lis[i] = 1;

		/* Compute optimized LIS values in bottom up manner */
		for (i = 1; i < n; i++)
			for (j = 0; j < i; j++) // so far equivalent (j<i)
				if (arr[i] > arr[j] && lis[i] < lis[j] + 1)
					lis[i] = lis[j] + 1;

		/* Pick maximum of all LIS values */
		for (i = 0; i < n; i++)
			if (max < lis[i])
				max = lis[i];

		return max;
	}

	// the best LCIS! although O n^2...
	public static int lengthOfLIS(int[] nums) {
		if (nums == null || nums.length == 0)
			return 0;

		int[] max = new int[nums.length];
		Arrays.fill(max, 1);

		int result = 1;
		for (int i = 0; i < nums.length; i++) {
			for (int j = 0; j < i; j++)
				if (nums[i] > nums[j])
					max[i] = Math.max(max[i], max[j] + 1); // how many times a smaller element appear so far!!!
			result = Math.max(max[i], result);
		}
		return result;
	}

	public static int maxSumSubsequence(int[] l) { // must be consecutive
		int best = 0, cur = 0;
		for (int elem : l)
			cur = Math.max(cur + elem, elem);
		best = Math.max(best, cur);
		return best;

	}

	// see https://algorithmsandme.in/2017/08/27/boolean-parenthesization-problem/
	int countNumberOfWaysToParenthesize(char operands[], char operators[], int n) {
		int[][] T = new int[n][n];
		int[][] F = new int[n][n];

		// Fill diaginal entries first
		// All diagonal entries in T[i][i] are 1 if symbol[i]
		// is T (true). Similarly, all F[i][i] entries are 1 if
		// symbol[i] is F (False)
		for (int i = 0; i < n; i++) {
			F[i][i] = (operands[i] == 'F') ? 1 : 0;
			T[i][i] = (operands[i] == 'T') ? 1 : 0;
		}

		// Now fill T[i][i+1], T[i][i+2], T[i][i+3]... in order
		// And F[i][i+1], F[i][i+2], F[i][i+3]... in order
		for (int gap = 1; gap < n; ++gap)
			for (int i = 0, j = gap; j < n; ++i, ++j) {
				T[i][j] = F[i][j] = 0;
				for (int g = 0; g < gap; g++) {
					// Find place of parenthesization using current value
					// of gap
					int k = i + g;

					// Store Total[i][k] and Total[k+1][j]
					int tik = T[i][k] + F[i][k];
					int tkj = T[k + 1][j] + F[k + 1][j];

					// Follow the recursive formulas according to the current
					// operator
					if (operators[k] == '&') {
						T[i][j] += T[i][k] * T[k + 1][j];
						F[i][j] += (tik * tkj - T[i][k] * T[k + 1][j]);
					}
					if (operators[k] == '|') {
						F[i][j] += F[i][k] * F[k + 1][j];
						T[i][j] += (tik * tkj - F[i][k] * F[k + 1][j]);
					}
					if (operators[k] == '^') {
						T[i][j] += F[i][k] * T[k + 1][j] + T[i][k] * F[k + 1][j];
						F[i][j] += T[i][k] * T[k + 1][j] + F[i][k] * F[k + 1][j];
					}
				}
			}
		return T[0][n - 1];
	}

	public static int editDist(String str1, String str2, int m, int n) {
		// If first string is empty, the only option is to
		// insert all characters of second string into first
		if (m == 0)
			return n;

		// If second string is empty, the only option is to
		// remove all characters of first string
		if (n == 0)
			return m;

		// If last characters of two strings are same, nothing
		// much to do. Ignore last characters and get count for
		// remaining strings.
		if (str1.charAt(m - 1) == str2.charAt(n - 1))
			return editDist(str1, str2, m - 1, n - 1);

		// If last characters are not same, consider all three
		// operations on last character of first string, recursively
		// compute minimum cost for all three operations and take
		// minimum of three values.
		return 1 + Math.min(editDist(str1, str2, m, n - 1), // Insert
				Math.min(editDist(str1, str2, m - 1, n), // Remove
						editDist(str1, str2, m - 1, n - 1)) // Replace
		);
	}

	static int editDistDynamic(String str1, String str2, int m, int n) {
		// Create a table to store results of subproblems
		int dp[][] = new int[m + 1][n + 1];

		// Fill d[][] in bottom up manner
		for (int i = 0; i <= m; i++)
			for (int j = 0; j <= n; j++)
				// If first string is empty, only option is to
				// isnert all characters of second string
				if (i == 0)
					dp[i][j] = j; // Min. operations = j

				// If second string is empty, only option is to
				// remove all characters of second string
				else if (j == 0)
					dp[i][j] = i; // Min. operations = i

				// If last characters are same, ignore last char
				// and recur for remaining string
				else if (str1.charAt(i - 1) == str2.charAt(j - 1))
					dp[i][j] = dp[i - 1][j - 1];

				// If last character are different, consider all
				// possibilities and find minimum
				else
					dp[i][j] = 1 + Math.min(dp[i][j - 1], // Insert
							Math.min(dp[i - 1][j], // Remove
									dp[i - 1][j - 1])); // Replace

		return dp[m][n];
	}

	public static boolean isSubsetSum(int set[], int n, int sum) {
		// Base Cases
		if (sum == 0)
			return true;
		if (n == 0 && sum != 0)
			return false;

		// If last element is greater than sum, then ignore it
		if (set[n - 1] > sum)
			return isSubsetSum(set, n - 1, sum);

		/*
		 * else, check if sum can be obtained by any of the following (a) including the
		 * last element (b) excluding the last element
		 */
		return isSubsetSum(set, n - 1, sum) || isSubsetSum(set, n - 1, sum - set[n - 1]);
	}

	// Function to find the minimum sum difference between 2 partitions, invoke with
	// i = n first
	public static int findMinSumDifference(int arr[], int i, int sumCalculated, int sumTotal) {
		// If we have reached last element.
		// Sum of one subset is sumCalculated,
		// sum of other subset is sumTotal-
		// sumCalculated. Return absolute
		// difference of two sums.
		if (i == 0)
			return Math.abs((sumTotal - sumCalculated) - sumCalculated);

		// For every item arr[i], we have two choices
		// (1) We do not include it first set
		// (2) We include it in first set
		// We return minimum of two choices
		return Math.min(findMinSumDifference(arr, i - 1, sumCalculated + arr[i - 1], sumTotal),
				findMinSumDifference(arr, i - 1, sumCalculated, sumTotal));
	}

	// Function returns count of ways to cover 'dist' with 1, 2, and 3 steps
	static int printCountDP(int dist) {
		int[] count = new int[dist + 1];

		// Initialize base values. There is one way to
		// cover 0 and 1 distances and two ways to
		// cover 2 distance
		count[0] = 1;
		count[1] = 1;
		count[2] = 2;

		// Fill the count array in bottom up manner
		for (int i = 3; i <= dist; i++)
			count[i] = count[i - 1] + count[i - 2] + count[i - 3];

		return count[dist];
	}

	static int findLongestFromCell(int i, int j, int mat[][], int dp[][]) {
		// Base case
		if (i < 0 || i >= mat.length || j < 0 || j >= mat.length)
			return 0;

		// If this subproblem is already solved
		if (dp[i][j] != -1)
			return dp[i][j];

		// Since all numbers are unique and in range from 1 to n*n,
		// there is at most one possible direction from any cell!!!!!
		if (j < mat.length - 1 && ((mat[i][j] + 1) == mat[i][j + 1]))
			return dp[i][j] = 1 + findLongestFromCell(i, j + 1, mat, dp);

		if (j > 0 && (mat[i][j] + 1 == mat[i][j - 1]))
			return dp[i][j] = 1 + findLongestFromCell(i, j - 1, mat, dp);

		if (i > 0 && (mat[i][j] + 1 == mat[i - 1][j]))
			return dp[i][j] = 1 + findLongestFromCell(i - 1, j, mat, dp);

		if (i < mat.length - 1 && (mat[i][j] + 1 == mat[i + 1][j]))
			return dp[i][j] = 1 + findLongestFromCell(i + 1, j, mat, dp);

		// If none of the adjacent fours is one greater
		return dp[i][j] = 1;
	}

	// Function that returns length of the longest path
	// beginning with any cell
	static int finLongestOverAll(int mat[][]) {
		// Initialize result
		int result = 1;

		int n = mat.length;
		// Create a lookup table and fill all entries in it as -1
		int[][] dp = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				dp[i][j] = -1;

		// Compute longest path beginning from all cells
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				if (dp[i][j] == -1)
					findLongestFromCell(i, j, mat, dp);

				// Update result if needed
				result = Math.max(result, dp[i][j]);
			}

		return result;
	}

	// Returns the maximum value that can be put in a knapsack of capacity W
	static int knapSack(int W, int wt[], int val[], int n) {
		// Base Case
		if (n == 0 || W == 0)
			return 0;

		// If weight of the nth item is more than Knapsack capacity W, then
		// this item cannot be included in the optimal solution
		if (wt[n - 1] > W)
			return knapSack(W, wt, val, n - 1);

		// Return the maximum of two cases:
		// (1) nth item included
		// (2) not included
		else
			return Math.max(val[n - 1] + knapSack(W - wt[n - 1], wt, val, n - 1), knapSack(W, wt, val, n - 1));
	}

}
