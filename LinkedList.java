import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedList {
	static Node head;

	static class Node {
		int data;
		Node next;

		Node(int d) {
			data = d;
			next = null;
		}

		Node(int data, Node next) {
			this.data = data;
			this.next = next;
		}
	}

	// Function that detects loop in the list
	int detectAndRemoveLoop(Node node) {
		Node slow = node, fast = node;
		while (fast != null && fast.next != null)
			if (slow == fast.next || slow == fast.next.next) {
				// remove loop
				fast.next = null;
				return 1;
			} else {
				fast = fast.next.next;
				slow = slow.next;
			}
		return 0;
	}

	public static Node reverseList(Node originalList) {
		if (originalList == null || originalList.next == null)
			return originalList;
		// 1->2->3
		Node next = originalList.next;
		originalList.next = null; // break link between 1 and 2
		Node tail = reverseList(originalList.next); // reverse 2 and 3
		next.next = originalList; // make 2 point to 1
		return tail;
	}

	private Node reverseNodes(Node prevNode, Node currentNode) {
		if (currentNode == null)
			return prevNode;
		Node nextNode = currentNode.next;
		currentNode.next = prevNode;
		return reverseNodes(currentNode, nextNode);
	}

	public Node reverseIteratively(Node head) {
		if (head == null || head.next == null)
			return head;
		Node prev = null;
		Node cur = head;
		Node next = head.next;
		while (next != null) {
			cur.next = prev;
			prev = cur;
			cur = next;
			next = next.next;
		}
		return cur;
	}

	public void insertFirst(int element) {

		Node newNode = new Node(element);
		newNode.next = head;
		head = newNode;
	}

	// delete and return what was deleted
	public Node deleteFirst() {
		Node deleted = head;
		if (head != null)
			head = head.next; // not deleting anything, just skipping...
		return deleted;
	}

	public void insertLast(int element) {

		Node newNode = new Node(element);
		if (head == null) {
			head = newNode;
			return;
		}
		Node current = head;
		Node previous = null;
		// find the end of the list
		while (current != null) {
			previous = current;
			current = current.next;
		}
		previous.next = newNode;
	}

	public Node deleteLast() {
		if (head == null)
			return null;
		Node current = head;
		Node previous = null;
		// find the end of the list
		while (current.next != null) {
			previous = current;
			current = current.next;
		}
		// was it just one element
		if (previous == null)
			head = head.next;
		else
			previous.next = current.next;
		return current;

	}

	public Node findArbitrary(int value) {
		Node current = head;
		while (current != null && current.data != value)
			current = current.next;
		return current;

	}

	public Node deleteArbitrary(int value) {
		if (head == null)
			return null;
		Node current = head;
		Node previous = null;
		while (current != null && current.data != value) {
			previous = current;
			current = current.next;
		}
		if (current == null) // not found
			return null;
		// update head if necessary; check the position of what was found
		if (current == head)
			head = head.next; // skip head
		else
			previous.next = current.next;// skip current
		return current;

	}

	public static void addTwoLists(Node first, Node second) {

		Node temp = null;
		Node result = null;
		// LinkedList res = result;

		int carry = 0, sum;
		// assume lists are already reversed

		while (first != null || second != null) // while at least one lists exists
		{

			sum = carry + (first != null ? first.data : 0) + (second != null ? second.data : 0);

			// update carry for next calculation
			carry = (sum >= 10) ? 1 : 0;

			// update sum if it is greater than 10
			sum = sum % 10;

			// Create a new node with sum as data

			// if this is the first node then set it as head of
			// the resultant list

			if (result == null)
				result = new Node(sum);
			else {
				Node res = result;
				while ((res.next != null))
					res = res.next;
				res.next = new Node(sum);
			}

			// Move first and second pointers to next nodes
			if (first != null)
				first = first.next;
			if (second != null)
				second = second.next;
		} // end of while

		if (carry > 0) {
			Node res = result;
			while ((res.next != null))
				res = res.next;
			res.next = new Node(carry);
		}

		// System.out.println(res);

	}

	public static void fiveWaysOfIterating() {
		List<String> list = new ArrayList<String>();
		// 1
		for (int i = 0; i < list.size(); i++)
			System.out.println(list.get(i));
		// 2
		for (String s : list)
			System.out.println(s);
		// 3
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext())
			System.out.println(iterator.next());
		// 4
		int i = 0;

		while (i < list.size()) {
			System.out.println(list.get(i));
			i++;
		}
		// 5 - streams
		list.forEach(s -> {
			System.out.println(s);
		});
	}

	public static void main(String[] args) {

	}

}
