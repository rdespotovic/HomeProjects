import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sort {

	// http://www.baeldung.com/java-bubble-sort - why last elements left out, they
	// already sorted. Called "sinking sort" sometimes
	// Also see http://www.baeldung.com/java-bubble-sort and
	// https://mathbits.com/MathBits/Java/arrays/Bubble.htm

	public void optimizedBubbleSort(Integer[] arr) {
		boolean swapNeeded = true;
		for (int i = 0; i < arr.length - 1 && swapNeeded; i++) {
			swapNeeded = false;
			for (int j = 1; j < arr.length - i; j++) // n - i !!!
				if (arr[j - 1] > arr[j]) {
					int temp = arr[j - 1];
					arr[j - 1] = arr[j];
					arr[j] = temp;
					swapNeeded = true;
				}
		}
	}

	// easier to understand
	public static void bubbleSort(int[] num) {
		int j;
		boolean flag = true; // set flag to true to begin first pass
		int temp; // holding variable

		while (flag) {
			flag = false; // set flag to false awaiting a possible swap
			for (j = 0; j < num.length - 1; j++)
				if (num[j] < num[j + 1]) // change to > for ascending sort
				{
					temp = num[j]; // swap elements
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true; // shows a swap occurred
				}
		}
	}

	/* Function to sort array using insertion sort */
	public static void InsertionSort(int[] arr) {
		int i; // the number of items sorted so far
		int key; // the item to be inserted
		int j;

		for (i = 1; i < arr.length; i++) // Start with 1 (not 0)
		{
			key = arr[i];
			for (j = i - 1; (j >= 0) && (arr[j] > key); j--)
				arr[j + 1] = arr[j];
			arr[j + 1] = key; // Put the key in its proper location
		}
	}

	public static List<Integer> mergesort(final List<Integer> values) {
		if (values.size() < 2)
			return values;
		final List<Integer> leftHalf = values.subList(0, values.size() / 2);
		final List<Integer> rightHalf = values.subList(values.size() / 2, values.size());
		return merge(mergesort(leftHalf), mergesort(rightHalf));
	}

	private static List<Integer> merge(final List<Integer> left, final List<Integer> right) {

		int leftPtr = 0;
		int rightPtr = 0;
		final List<Integer> merged = new ArrayList<>(left.size() + right.size());
		while (leftPtr < left.size() && rightPtr < right.size())
			if (left.get(leftPtr) < right.get(rightPtr)) {
				merged.add(left.get(leftPtr));
				leftPtr++;
			} else {
				merged.add(right.get(rightPtr));
				rightPtr++;
			}
		while (leftPtr < left.size()) {
			merged.add(left.get(leftPtr));
			leftPtr++;
		}
		while (rightPtr < right.size()) {
			merged.add(right.get(rightPtr));

			rightPtr++;
		}
		return merged;
	}

	// for explanation why leaf nodes do not need to be heap-ified:
	// http://javabypatel.blogspot.in/2015/11/heap-sort-algorithm.html
	public void heapSort(int arr[]) {
		int n = arr.length;

		// Build heap (rearrange array)
		for (int i = n / 2 - 1; i >= 0; i--)
			heapify(arr, n, i);

		// One by one extract an element from heap
		for (int i = n - 1; i >= 0; i--) {
			// Move current root to end
			int temp = arr[0];
			arr[0] = arr[i];
			arr[i] = temp;

			// call max heapify on the reduced heap
			heapify(arr, i, 0);
		}
	}

	// To heapify a subtree rooted with node i which is
	// an index in arr[]. n is size of heap
	void heapify(int arr[], int n, int i) {
		int largest = i; // Initialize largest as root
		int l = 2 * i + 1; // left = 2*i + 1
		int r = 2 * i + 2; // right = 2*i + 2

		// If left child is larger than root
		if (l < n && arr[l] > arr[largest])
			largest = l;

		// If right child is larger than largest so far
		if (r < n && arr[r] > arr[largest])
			largest = r;

		// If largest is not root
		if (largest != i) {
			int swap = arr[i];
			arr[i] = arr[largest];
			arr[largest] = swap;

			// Recursively heapify the affected sub-tree
			heapify(arr, n, largest);
		}
	}

	// see http://www.java2novice.com/java-sorting-algorithms/quick-sort/
	private void quickSort(int[] array, int lowerIndex, int higherIndex) {

		int i = lowerIndex;
		int j = higherIndex;
		// calculate pivot number, I am taking pivot as middle index number
		int pivot = array[lowerIndex + (higherIndex - lowerIndex) / 2];
		// Divide into two arrays
		while (i <= j) {
			/**
			 * In each iteration, we will identify a number from left side which is greater
			 * then the pivot value, and also we will identify a number from right side
			 * which is less then the pivot value. Once the search is done, then we exchange
			 * both numbers.
			 */
			while (array[i] < pivot)
				i++;
			while (array[j] > pivot)
				j--;
			if (i <= j) {
				exchangeNumbers(array, i, j);
				// move index to next position on both sides
				i++;
				j--;
			}
		}
		// call quickSort() method recursively
		if (lowerIndex < j)
			quickSort(array, lowerIndex, j);
		if (i < higherIndex)
			quickSort(array, i, higherIndex);
	}

	private void exchangeNumbers(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	// Kth largest value OR QuickSelect; see
	// http://www.geekviewpoint.com/java/search/quickselect
	private int quickselect(int[] G, int first, int last, int k) {
		if (first <= last) {
			int pivot = randomPartition(G, first, last);
			if (pivot == k)
				return G[k];
			if (pivot > k)
				return quickselect(G, first, pivot - 1, k);
			return quickselect(G, pivot + 1, last, k);
		}
		return Integer.MIN_VALUE;
	}

	private int randomPartition(int[] array, int first, int last) {
		int pivot = first + new Random().nextInt(last - first + 1); // or -1?
		// Divide into two arrays
		while (first <= last) {
			/**
			 * In each iteration, we will identify a number from left side which is greater
			 * then the pivot value, and also we will identify a number from right side
			 * which is less then the pivot value. Once the search is done, then we exchange
			 * both numbers.
			 */
			while (array[first] < array[pivot])
				first++;
			while (array[last] > array[pivot])
				last--;
			if (first <= last) {
				exchangeNumbers(array, first, last);
				// move index to next position on both sides
				first++;
				last--;
			}
		}
		return pivot;
	}

	// Prints the pair with sum closest to x
	static void printClosest(int arr[], int n, int x) {
		int res_l = 0, res_r = 0; // To store indexes of result pair

		// Initialize left and right indexes and difference between
		// pair sum and x
		int l = 0, r = n - 1, diff = Integer.MAX_VALUE;

		// While there are elements between l and r
		while (r > l) {
			// Check if this pair is closer than the closest pair so far
			if (Math.abs(arr[l] + arr[r] - x) < diff) {
				res_l = l;
				res_r = r;
				diff = Math.abs(arr[l] + arr[r] - x);
			}

			// If this pair has more sum, move to smaller values.
			if (arr[l] + arr[r] > x)
				r--;
			else // Move to larger values
				l++;
		}

		System.out.println(" The closest pair is " + arr[res_l] + " and " + arr[res_r]);
	}

}
