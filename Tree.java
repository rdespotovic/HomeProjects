import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Created by rdespoto on 7/12/17.
 */

public class Tree {

	public static class Node<T extends Comparable> {
		private T value;
		int level;
		private Node<T> left;
		private Node<T> right;
		// constructors, getters and setters omitted

		public Node(T value, Node<T> left, Node<T> right) {
			this.value = value;
			this.left = left;
			this.right = right;
		}

		public T getValue() {
			return value;
		}

		public Node<T> getLeft() {

			return left;
		}

		public Node<T> getRight() {
			return right;
		}

		public Node<T> findValue(T value) {

			if (value == null)
				return null;
			Node<T> root = this;

			while (root != null)
				if (value.equals(root.getValue()))
					return root;
				else if (value.compareTo(root.getValue()) < 0)
					root = root.getLeft();
				else
					root = root.getRight();
			return null;

		}

		public void preorderTraverseStack() {
			Stack<Node<T>> stack = new Stack<Node<T>>();
			Node<T> tree = this;
			stack.push(tree);
			while (!stack.empty()) {
				Node<T> object = stack.pop();
				System.out.println("Traversed " + object.getValue());
				Node<T> right = object.getRight();
				// only push non-nulls
				if (right != null)
					stack.push(right);
				Node<T> left = object.getLeft();
				if (left != null)
					stack.push(left);
			}

		}

		public void preOrder(Node<T> tree) {
			if (tree == null)
				return;
			System.out.println(tree.value);
			preOrder(tree.getLeft());
			preOrder(tree.getRight());

		}
	}

	// reverse alternate levels of a tree

	void preorder(Node root1, Node root2, int lvl) {
		// Base cases
		if (root1 == null || root2 == null)
			return;

		// Swap subtrees if level is even
		if (lvl % 2 == 0) {
			int tmp = (int) root1.value;
			root1.value = root2.value;
			root2.value = tmp;
		}
		// swap(root1->key, root2->key);

		// Recur for left and right subtrees (Note : left of root1
		// is passed and right of root2 in first call and opposite
		// in second call.
		preorder(root1.left, root2.right, lvl + 1);
		preorder(root1.right, root2.left, lvl + 1);
	}

	// This function calls preorder() for left and right children
	// of root
	void reverseAlternate(Node root) {
		preorder(root.left, root.right, 0);
	}

	public static Node mirror(Node node) {
		if (node == null)
			return node;

		/* do the subtrees */
		Node left = mirror(node.left);
		Node right = mirror(node.right);

		/* swap the left and right pointers */
		node.left = right;
		node.right = left;

		return node;
	}

	/* Function to calculate the minimum depth of the tree */
	public int minimumDepth(Node root) {
		// Corner case. Should never be hit unless the code is
		// called on root = NULL
		if (root == null)
			return 0;

		// Base case : Leaf Node. This accounts for height = 1.
		if (root.left == null && root.right == null)
			return 1;

		// If left subtree is NULL, recur for right subtree
		if (root.left == null)
			return minimumDepth(root.right) + 1;

		// If right subtree is NULL, recur for right subtree
		if (root.right == null)
			return minimumDepth(root.left) + 1;

		return Math.min(minimumDepth(root.left), minimumDepth(root.right)) + 1;
	}

	// This function returns overall maximum path sum in 'res'
	// And returns max path sum going through root.
	int findMaxPathSum(Node<Integer> node, Integer res) {

		// Base Case
		if (node == null)
			return 0;

		// l and r store maximum path sum going through left and
		// right child of root respectively
		int l = findMaxPathSum(node.left, res);
		int r = findMaxPathSum(node.right, res);

		// Max path for parent call of root. This path must
		// include at-most one child of root
		int current = Math.max(node.value, Math.max(l + node.value, node.value + r));

		// Store the Maximum Result.
		// Only for root nodes both children can appear in the max path sum....
		res = Math.max(res, Math.max(current, l + node.value + r));

		return current; // see
						// https://www.programcreek.com/2013/02/leetcode-binary-tree-maximum-path-sum-java/
	}

	// is array preorder traversal of BST
	public boolean canRepresentBST(int pre[]) {
		// Create an empty stack
		Stack<Integer> s = new Stack<Integer>();

		// Initialize current root as minimum possible
		// value
		int nextGreater = Integer.MIN_VALUE;

		// Traverse given array
		for (int i = 0; i < pre.length; i++) {
			// Here we find next greater element and after finding next greater, if we find
			// a smaller element, then return false.
			if (pre[i] < nextGreater)
				return false;

			// If pre[i] is in right subtree of stack top,
			// Keep removing items smaller than pre[i]
			// and make the last removed item as new
			// root.
			while (!s.empty() && s.peek() < pre[i]) {
				nextGreater = s.peek();
				s.pop();
			}

			// At this point either stack is empty, or root is next greater number, push
			// pre[i]
			s.push(pre[i]);
		}
		return true;
	}

	/* this function checks if a binary tree is full or not */
	boolean isFullTree(Node node) {
		// if empty tree
		if (node == null)
			return true;

		// if leaf node
		if (node.left == null && node.right == null)
			return true;

		if ((node.left != null) && (node.right != null))
			return (isFullTree(node.left) && isFullTree(node.right));

		// if none work
		return false;
	}

	public static Node removeShortPathNodesUtil(Node node, int level, int k) {
		// Base condition
		if (node == null)
			return null;

		// Traverse the tree in postorder fashion so that if a leaf
		// node path length is shorter than k, then that node and
		// all of its descendants till the node which are not
		// on some other path are removed.
		node.left = removeShortPathNodesUtil(node.left, level + 1, k);
		node.right = removeShortPathNodesUtil(node.right, level + 1, k);

		// If root is a leaf node and it's level is less than k then
		// remove this node.
		// This goes up and check for the ancestor nodes also for the
		// same condition till it finds a node which is a part of other
		// path(s) too.
		if (node.left == null && node.right == null && level < k)
			return null;

		// Return root;
		return node;
	}

	/*
	 * Function to find Lowest Common Ancestor of n1 and n2. The function assumes
	 * that both n1 and n2 are present in BST
	 */
	Node lca(Node node, int n1, int n2) {
		if (node == null)
			return null;

		// If both n1 and n2 are smaller than root, then LCA lies in left
		if ((int) node.value > n1 && (int) node.value > n2)
			return lca(node.left, n1, n2);

		// If both n1 and n2 are greater than root, then LCA lies in right
		if ((int) node.value < n1 && (int) node.value < n2)
			return lca(node.right, n1, n2);

		return node;
	}

	// see
	// http://algorithms.tutorialhorizon.com/print-the-bottom-view-of-the-binary-tree/
	public void bottomView(Node root) {
		if (root == null)
			return;

		// Initialize a variable 'hd' with 0 for the root element.
		int level = 0;

		// TreeMap which stores key value pair sorted on key value
		Map<Integer, Comparable> map = new TreeMap<>();

		// Queue to store tree nodes in level order traversal
		Queue<Node> queue = new LinkedList<Node>();

		// Assign initialized horizontal distance value to root
		// node and add it to the queue.
		root.level = level;
		queue.add(root);

		// Loop until the queue is empty (standard level order loop)
		while (!queue.isEmpty()) {
			Node temp = queue.remove();

			// Extract the horizontal distance value from the
			// dequeued tree node.
			level = temp.level;

			// Put the dequeued tree node to TreeMap having key
			// as horizontal distance. Every time we find a node
			// having same horizontal distance we need to replace
			// the data in the map.
			// if (map.containsKey(level)) {} else { THIS GIVES TOP VIEW OF THE TREE!!
			map.put(level, temp.value); // }

			// If the dequeued node has a left child add it to the
			// queue with a horizontal distance hd-1.
			if (temp.left != null) {
				temp.left.level = level - 1;
				queue.add(temp.left);
			}
			// If the dequeued node has a left child add it to the
			// queue with a horizontal distance hd+1.
			if (temp.right != null) {
				temp.right.level = level + 1;
				queue.add(temp.right);
			}
		}

		// Extract the entries of map into a set to traverse
		// an iterator over that.
		Set<Entry<Integer, Comparable>> set = map.entrySet();

		// Make an iterator
		Iterator<Entry<Integer, Comparable>> iterator = set.iterator();

		// Traverse the map elements using the iterator.
		while (iterator.hasNext()) {
			Map.Entry<Integer, Comparable> me = iterator.next();
			System.out.print(me.getValue() + " ");
		}
	}

	// reverse elements in alternate levels of a binary tree; arr needs to
	// be reversed between store and modify...

	// function to store alternate levels in a tree
	void storeAlternate(Node node, int arr[], Integer index, int l) {
		// base case
		if (node == null)
			return;
		// store elements of left subtree
		storeAlternate(node.left, arr, index, l + 1);

		// store this node only if level is odd
		if (l % 2 != 0) {
			arr[index] = (Integer) node.value;
			index++;
		}

		storeAlternate(node.right, arr, index, l + 1);
	}

	// Function to modify Binary Tree (All odd level nodes are
	// updated by taking elements from array in inorder fashion)
	void modifyTree(Node node, int arr[], Integer index, int l) {

		// Base case
		if (node == null)
			return;

		// Update nodes in left subtree
		modifyTree(node.left, arr, index, l + 1);

		// Update this node only if this is an odd level node
		if (l % 2 != 0) {
			node.value = arr[index];
			index++;
		}

		// Update nodes in right subtree
		modifyTree(node.right, arr, index, l + 1);
	}

	public static void main(String[] args) {
		Node<Integer> tree = new Node<Integer>(1, null, null);
		Node<Integer> newTree = new Node<Integer>(2, null, tree);
		newTree.left = tree;
		// Node<Integer> found = newTree.findValue(1);
		// System.out.print("Found " + found.getValue());
		// newTree.preorderTraverseStack();
		newTree.preOrder(newTree);
	}

}