import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class Graph {

	private ArrayList<Vertex> vertices;

	public Graph(int numberVertices) {
		vertices = new ArrayList<Vertex>(numberVertices);
		for (int i = 0; i < numberVertices; i++)
			vertices.add(new Vertex(i));
	}

	public void addEdge(int src, int dest, int weight) {
		Vertex s = vertices.get(src);
		Edge new_edge = new Edge(s, vertices.get(dest), weight);
		s.neighbours.add(new_edge);
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public ArrayList<Edge> getAllEdges() { // assume no duplicates
		ArrayList edges = new ArrayList();
		for (Vertex vertex : getVertices())
			edges.addAll(vertex.neighbours);
		return edges;
	}

	public Vertex getVertex(int vert) {
		return vertices.get(vert);
	}

	class Edge implements Comparable<Edge> {
		public final Vertex target;
		public final Vertex src;
		public final int weight;

		public Edge(Vertex src, Vertex target, int weight) {
			this.src = src;
			this.target = target;
			this.weight = weight;
		}

		@Override
		public int compareTo(Edge compareEdge) {
			return (this.weight - compareEdge.weight);
		}
	}

	class Vertex implements Comparable<Vertex> {
		public final Integer name;
		public LinkedList<Edge> neighbours;
		public LinkedList<Vertex> path;
		public double minDistance = Double.POSITIVE_INFINITY;
		public boolean visited;

		@Override
		public int compareTo(Vertex other) {
			return Double.compare(minDistance, other.minDistance);
		}

		public Vertex(Integer name) {
			this.name = name;
			neighbours = new LinkedList<Edge>();
			path = new LinkedList<Vertex>();
		}

		@Override
		public String toString() {
			return name + "";
		}
	}

	// prints BFS traversal from a given source s
	void breadthFirstTraversal(Graph.Vertex s) {

		// Create a queue for BFS
		LinkedList<Graph.Vertex> queue = new LinkedList<Graph.Vertex>();

		// Mark the current node as visited and enqueue it
		s.visited = true;
		queue.add(s);

		while (queue.size() != 0) {
			// Dequeue a vertex from queue and print it
			s = queue.poll(); // linkedList.poll = queue.first!!!
			System.out.print(s + " ");

			// Get all adjacent vertices of the dequeued vertex s
			// If a adjacent has not been visited, then mark it
			// visited and enqueue it
			Iterator<Graph.Edge> i = s.neighbours.iterator();
			while (i.hasNext()) {
				Graph.Edge n = i.next();
				if (!n.target.visited) {
					n.target.visited = true;
					queue.add(n.target);
				}
			}
		}
	}

	// A function used by DFS
	void DFSUtil(Graph.Vertex s) {
		// Create a stack for DFS
		Stack<Graph.Vertex> stack = new Stack<>();

		// Push the current source node
		stack.push(s);

		while (stack.empty() == false) {
			// Pop a vertex from stack and print it
			s = stack.peek();
			stack.pop();

			// Stack may contain same vertex twice. So
			// we need to print the popped item only
			// if it is not visited.
			if (s.visited == false) {
				System.out.print(s + " ");
				s.visited = true;
			}

			// Get all adjacent vertices of the popped vertex s
			// If a adjacent has not been visited, then push it
			// to the stack.
			Iterator<Graph.Edge> itr = s.neighbours.iterator();

			while (itr.hasNext()) {
				Graph.Edge n = itr.next();
				if (!n.target.visited) {
					n.target.visited = true;
					stack.push(n.target);
				}
			}

		}
	}

	// The function to do DFS traversal, works even on disconnected graphs. It uses
	// recursive DFSUtil()
	void DFS() {
		// Mark all the vertices as not visited(set as
		// false by default in java)
		List<Boolean> visited = new ArrayList();

		// Call the recursive helper function to print DFS traversal
		// starting from all vertices one by one
		for (Graph.Vertex vertex : getVertices())
			if (vertex.visited == false)
				DFSUtil(vertex);
	}

	// a single node is the "source" node, and for each node this calculates
	// shortest paths from the
	// source
	// DOES NOT WORK ON CYCLICAL GRAPHS WITH NEGATIVE WEIGHTS!!
	public void calculateDijkstra(Vertex source) {

		// 1. Take the unvisited node with minimum weight.
		// 2. Visit all its neighbors.
		// 3. Update the distances for all the neighbors (in the Priority Queue).
		// Repeat the process till all the connected nodes are visited.

		source.minDistance = 0;
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
		queue.add(source);

		while (!queue.isEmpty()) {

			Vertex previousNode = queue.poll();

			for (Edge neighbour : previousNode.neighbours) {
				Double newDist = previousNode.minDistance + neighbour.weight; // distance from source

				if (neighbour.target.minDistance > newDist) {
					// Remove the node from the queue to update the distance value.
					queue.remove(neighbour.target);// java PQ allows duplicates...
					neighbour.target.minDistance = newDist;

					// Take the path visited till now and add the new node
					neighbour.target.path = new LinkedList<Vertex>(previousNode.path);
					neighbour.target.path.add(previousNode);

					// Reenter the node with new distance.
					queue.add(neighbour.target);
				}
			}
		}
	}

	// ALL PAIRS SHORTEST PATH - find shortest distances between every pair of
	// vertices in a given edge weighted directed graph

	void floydWarshall(int graph[][]) {
		int V = graph.length;
		int dist[][] = new int[V][V];
		int i, j, k;

		/*
		 * Initialize the solution matrix same as input graph matrix. Or we can say the
		 * initial values of shortest distances are based on shortest paths considering
		 * no intermediate vertex.
		 */
		for (i = 0; i < V; i++)
			for (j = 0; j < V; j++)
				dist[i][j] = graph[i][j];

		/*
		 * Add all vertices one by one to the set of intermediate vertices. ---> Before
		 * start of a iteration, we have shortest distances between all pairs of
		 * vertices such that the shortest distances consider only the vertices in set
		 * {0, 1, 2, .. k-1} as intermediate vertices. ----> After the end of a
		 * iteration, vertex no. k is added to the set of intermediate vertices and the
		 * set becomes {0, 1, 2, .. k}
		 */
		for (k = 0; k < V; k++)
			// Pick all vertices as source one by one
			for (i = 0; i < V; i++)
				// Pick all vertices as destination for the
				// above picked source
				for (j = 0; j < V; j++)
					// If vertex k is on the shortest path from
					// i to j, then update the value of dist[i][j]
					if (dist[i][k] + dist[k][j] < dist[i][j])
						dist[i][j] = dist[i][k] + dist[k][j];

		// Print the shortest distance matrix
		// printSolution(dist);
	}

	void printSolution(int dist[][]) {
		Integer V = dist.length;
		System.out.println("Following matrix shows the shortest " + "distances between every pair of vertices");
		for (int i = 0; i < V; ++i) {
			for (int j = 0; j < V; ++j)
				System.out.print(dist[i][j] + "   ");
			System.out.println();
		}
	}

	// union-find to detect cycles
	// https://www.cs.princeton.edu/~rs/AlgsDS07/01UnionFind.pdf
	private int root(int i, int id[]) {
		while (i != id[i])
			i = id[i];
		// time proportional to depth of i
		return i;
	}

	/*
	 * public boolean find(int p, int q, int id[]) { // time proportional to depth
	 * of p and q return root(p, id) == root(q, id); }
	 */

	public void union(int p, int q, int[] id) {
		int i = root(p, id);
		int j = root(q, id);
		id[i] = j;
	}

	int isCycle() {
		// Allocate memory for creating V subsets
		int parent[] = new int[getVertices().size()];

		// Initialize all subsets as single element sets
		for (int i = 0; i < parent.length; ++i)
			parent[i] = -1; // or itself??

		// Iterate through all edges of graph, find subset of both
		// vertices of every edge, if both subsets are same, then
		// there is cycle in graph.
		for (int i = 0; i < getVertices().size(); ++i)
			for (int j = 0; i < getVertices().get(i).neighbours.size(); ++j) {
				int x = root(getVertices().get(i).name, parent);
				int y = root(getVertices().get(i).neighbours.get(j).target.name, parent);
				if (x == y)
					return 1;

				union(x, y, parent);
			}
		return 0;
	}
	/*
	 * a) Pick a vertex u which is not there in mstSet and has minimum key value. b)
	 * Include u to mstSet. …. c) Update key value of all adjacent vertices of u. To
	 * update the key values, iterate through all adjacent vertices. For every
	 * adjacent vertex v, if weight of edge u-v is less than the previous key value
	 * of v, update the key value as weight of u-v
	 */

	// Function to construct and print MST for a graph represented
	// using adjacency matrix representation
	void primMST(int graph[][]) {
		// Array to store constructed MST
		int V = graph.length;
		int parent[] = new int[V];

		// Key values used to pick minimum weight edge in cut
		int key[] = new int[V];

		// To represent set of vertices not yet included in MST
		Boolean mstSet[] = new Boolean[V];

		// Initialize all keys as INFINITE
		for (int i = 0; i < V; i++) {
			key[i] = Integer.MAX_VALUE;
			mstSet[i] = false;
		}

		// Always include first 1st vertex in MST.
		key[0] = 0; // Make key 0 so that this vertex is
					// picked as first vertex
		parent[0] = -1; // First node is always root of MST

		// The MST will have V vertices
		for (int count = 0; count < V - 1; count++) {
			// Pick thd minimum key vertex from the set of vertices
			// not yet included in MST
			int u = minKey(key, mstSet);

			// Add the picked vertex to the MST Set
			mstSet[u] = true;

			// Update key value and parent index of the adjacent
			// vertices of the picked vertex. Consider only those
			// vertices which are not yet included in MST
			for (int v = 0; v < V; v++)

				// graph[u][v] is non zero only for adjacent vertices of m
				// mstSet[v] is false for vertices not yet included in MST
				// Update the key only if graph[u][v] is smaller than key[v]
				if (graph[u][v] != 0 && mstSet[v] == false && graph[u][v] < key[v]) {
					parent[v] = u;
					key[v] = graph[u][v];
				}
		}

		// print the constructed MST
		System.out.println("Edge   Weight");
		for (int i = 1; i < V; i++)
			System.out.println(parent[i] + " - " + i + "    " + graph[i][parent[i]]);
	}

	// A utility function to find the vertex with minimum key
	// value, from the set of vertices not yet included in MST
	int minKey(int key[], Boolean mstSet[]) {
		// Initialize min value
		int min = Integer.MAX_VALUE, min_index = -1, V = key.length;

		for (int v = 0; v < V; v++)
			if (mstSet[v] == false && key[v] < min) {
				min = key[v];
				min_index = v;
			}

		return min_index;
	}

	void KruskalMST() {

		int V = getVertices().size();
		Edge result[] = new Edge[V]; // This will store the resultant MST
		int e = 0; // An index variable, used for result[]
		int i = 0; // An index variable, used for sorted edges
		// for (i = 0; i < V; ++i)
		// result[i] = new Edge();

		// Step 1: Sort all the edges in non-decreasing order of their
		// weight. If we are not allowed to change the given graph, we
		// can create a copy of array of edges
		Edge[] edge = new Edge[V];
		getAllEdges().toArray(edge);
		Arrays.sort(edge);

		// Allocate memory for creating V subsets
		int parent[] = new int[V];
		// subset subsets[] = new subset[V];
		/*
		 * for (i = 0; i < V; ++i) subsets[i] = new subset();
		 */

		// Create V subsets with single elements
		for (int v = 0; v < V; ++v)
			parent[v] = v;

		i = 0; // Index used to pick next edge

		// Number of edges to be taken is equal to V-1
		while (e < V - 1) {
			// Step 2: Pick the smallest edge. And increment
			// the index for next iteration
			Edge next_edge = edge[e++];

			int x = root(next_edge.src.name, parent);
			int y = root(next_edge.target.name, parent);

			// If including this edge does't cause cycle,
			// include it in result and increment the index
			// of result for next edge
			if (x != y) {
				result[i++] = next_edge;
				union(x, y, parent);
			}
			// Else discard the next_edge
		}

		// print the contents of result[] to display
		// the built MST
		System.out.println("Following are the edges in " + "the constructed MST");
		for (i = 0; i < e; ++i)
			System.out.println(result[i].src.name + " -- " + result[i].target.name + " == " + result[i].weight);
	}

	void topologicalSortUtil(int v, boolean visited[], Stack stack) {
		// Mark the current node as visited.
		visited[v] = true;
		Integer i;

		// Recur for all the vertices adjacent to this
		// vertex
		LinkedList<Graph.Edge> it = getVertex(v).neighbours;
		for (Graph.Edge node : it)
			if (!visited[node.target.name])
				topologicalSortUtil(node.target.name, visited, stack);

		// Push current vertex to stack which stores result
		stack.push(new Integer(v));
	}
	// This algorithm is simply DFS with an extra stack. In DFS, we start from a
	// vertex, we first print it and then recursively call DFS for its adjacent
	// vertices. In topological sorting, we use a temporary stack. We don’t print
	// the vertex immediately, we first recursively call topological sorting for all
	// its adjacent vertices, then push it to a stack.

	// The function to do Topological Sort. It uses
	// recursive topologicalSortUtil()
	void topologicalSort() {
		int V = getVertices().size();
		Stack stack = new Stack();

		// Mark all the vertices as not visited
		boolean visited[] = new boolean[V];
		for (int i = 0; i < V; i++)
			visited[i] = false;

		// Call the recursive helper function to store
		// Topological Sort starting from all vertices
		// one by one
		for (int i = 0; i < V; i++)
			if (visited[i] == false)
				topologicalSortUtil(i, visited, stack);

		// Print contents of stack
		while (stack.empty() == false)
			System.out.print(stack.pop() + " ");
	}

}