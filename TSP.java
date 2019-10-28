/**
 * This program contains methods to test and compare different search algorithms
 * for the traveling salesman problem. Greedy, uniform cost, and in-class heuristic
 * 
 * @author Raxalon
 * @version 10/28/2019
 */

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

public class TSP {
	private final static int CITIES = 120; // total number of cities
	private final static int N = 20; // smaller portion of cities
	
	// for readability of coordinates
	private final static int X = 0;
	private final static int Y = 1;
	
	// counters
	private static int nqs;
	private static int dqs;
	
	private static int[][] coords = new int[CITIES][2];
	private static float[][] dists = new float[CITIES][CITIES];
	private static boolean[] visited = new boolean[CITIES];
	private static float[] lengths = new float[CITIES]; // store lengths from greedy search
	
	/**
	 * Creates distance matrix from file by reading in coordinates and using
	 * the Pythagorean theorem
	 * @throws IOException
	 */
	public static void createMatrix() throws IOException {
		Scanner fileIn = new Scanner(new FileInputStream("TSPDataComma.csv"));
		float temp;
		
		fileIn.useDelimiter(",|\\r|\\n");
		
		for (int row = 0; row < CITIES; row++) { // read in coords of each city
			coords[row][0] = Integer.parseInt(fileIn.next());
			coords[row][1] = Integer.parseInt(fileIn.next());
		}
		
		for (int row = 0; row < CITIES; row++) { // calc distances btwn every city
			for (int col = row; col < CITIES; col++) {
				// pythagorean theorem
				temp = (float) Math.sqrt(Math.pow(coords[row][0] - coords[col][0], 2)
										 + Math.pow(coords[row][1] - coords[col][1], 2));
				dists[row][col] = dists[col][row] = temp;
			}
		}
		
		fileIn.close();
	}

	// Helper class for priority queue. Contains methods to check if empty, enqueue, dequeue
	private static class PQueue {
		private PQNode start;
		
		public PQueue(PQNode start) {
			this.start = start;
		}
		
		public boolean isEmpty() {
			if (start == null)
				return true;
			return false;
		}
		
		public void enqueue(PQNode node) {
			if (this.start == null) {
				this.start = node;
			}
			else if (node.cost < start.cost) { // if node cheaper than start
				node.next = start; // replace start
				start = node;
			}
			else if (start.next == null) // if start is only node
				start.next = node;
			else {
				PQNode prvNode = start;
				PQNode curNode = start.next;
				
				while (curNode.next != null) { // traverse queue
					if (node.cost < curNode.cost) { // if node cheaper than curNode
						prvNode.next = node; // insert node between prv and cur
						node.next = curNode;
						return; // break out of enqueue
					}
					prvNode = curNode;
					curNode = curNode.next;
				}
				curNode.next = node; // reached end of queue, stick node on the end
			}
		}
		
		public PQNode dequeue() {
			PQNode dq = start;
			this.start = start.next;
			dqs++; // increase dequeue count
			return dq;
		}
	}
	
	// Helper class that contains information of each path and reference to next in queue
	private static class PQNode {
		private byte[] tour;
		private byte latestIdx;
		private float cost;
		private PQNode next;
		
		public PQNode(byte[] path, byte latestIdx, byte latest, float cost) {
			this.tour = Arrays.copyOf(path, path.length);
			this.tour[++latestIdx] = latest;
			this.latestIdx = latestIdx;
			this.cost = cost;
			this.next = null;
			nqs++; // increase enqueue count
		}
		
		// returns true if passed city is in this node's path
		public boolean inPath(int city) {
			for (int i = 0; i < tour.length; i++) {
				if (tour[i] == city) // if city is in path
					return true;
			}
			return false;
		}
		
		// returns true if path visits all cities in search
		public boolean allCities() {
			if (this.latestIdx == this.tour.length - 1)
				return true;
			return false;
		}
		
		// returns true if passed city results in crossing of current path
		public boolean crosses(byte city) {
			for (int i = 1; i < this.latestIdx; i++) { // checks each line segment in current path
				if (intersects(city, this.tour[this.latestIdx], this.tour[i-1], this.tour[i]))
					return true;
			}
			return false;
		}
	}
	
	/**
	 * The following three methods are code by Princi Singh taken from geeksforgeeks.org
	 * Checks to see if one line segment crosses another using orientation of an
	 * ordered triplet, accounts for special cases
	 */
	public static boolean onSegment(byte a, byte b, byte c) {
		if (coords[b][X] <= Math.max(coords[a][X], coords[c][X])
			&& coords[b][X] >= Math.min(coords[a][X], coords[c][X])
			&& coords[b][Y] <= Math.max(coords[a][Y], coords[c][Y])
			&& coords[b][Y] >= Math.min(coords[a][Y], coords[c][Y]))
			return true;
		return false;
	}
	
	public static int orientation(byte a, byte b, byte c) {
		int val = (coords[b][Y] - coords[a][Y]) * (coords[c][X] - coords[b][X])
				  - (coords[b][X] - coords[a][X]) * (coords[c][Y] - coords[b][Y]);
		if (val == 0)
			return 0;
		if (val > 0)
			return 1;
		else
			return 2;
	}
	
	public static boolean intersects(byte p1, byte q1, byte p2, byte q2) {
		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);
		
		if (o1 != o2 && o3 != o4)
			return true;
		
		if (o1 == 0 && onSegment(p1, p2, q1))
			return true;
		if (o2 == 0 && onSegment(p1, q2, q1))
			return true;
		if (o3 == 0 && onSegment(p2, p1, q2))
			return true;
		if (o4 == 0 && onSegment(p2, q1, q2))
			return true;
		
		return false;
	}
	
	/**
	 * Calls the greedySearch method for the specified number of repetitions
	 * Calls shortestGreedy to find the shortest of the generated tours
	 */
	public static void greedy() {
		for (int i = 0; i < CITIES; i++)
			greedySearch(i);
	
		shortestGreedy();
	}

	/**
	 * Greedy search. Finds successive shortest distances and adds onto path.
	 * @param start	The starting city
	 */
	public static void greedySearch(int start) {
		float length = 0;
		int min = nextUnvisited(); // initialize to next available city
		int current = start;
		
		System.out.print(start); // print first city
		
		for (int i = 0; i < CITIES; i++) {
			visited[current] = true; // visit current city
			min = nextUnvisited(); // set minimum to next available city
			
			if (min == -1) { // no more available cities, end of tour
				length += dists[current][start]; // add distance from end to start
				System.out.println("-" + start); // print first again
			}
			else {
				for (int j = 0; j < CITIES; j++) { // find the shortest distance to available cities
					if (!visited[j] && dists[current][j] < dists[current][min])
						min = j; // replace min if shorter
				}
				length += dists[current][min]; // add the distance to min
				System.out.print("-" + min); // print min city
				current = min; // change the current city to min
			}
		}
		lengths[start] = length; // add length to array lengths
		System.out.printf("Length: %.2f\n\n", length); // print the length
		
		resetVisited(); // reset visited array for next search
	}

	// Helper for greedy. Finds the shortest of the generated tours and prints result
	public static void shortestGreedy() {
		int minIdx = 0;
		
		for (int i = 0; i < CITIES; i++)
			if (lengths[i] < lengths[minIdx])
				minIdx = i;
		
		System.out.printf("Shortest tour length: %.2f\nStart city: %d\n", lengths[minIdx], minIdx);
	}

	// Helper for greedySearch. Finds next unvisited city and returns its index.
	// Returns -1 if all cities are visited
	public static int nextUnvisited() {
		for (int i = 0; i < CITIES; i++) {
			if (!visited[i])
				return i;
		}
		return -1;
	}
	
	// Helper for greedySearch. Checks for any unvisited nodes and returns true if none.
	public static boolean allVisited() {
		for (int i = 0; i < CITIES; i++) {
			if (!visited[i])
				return false;
		}
		return true;
	}
	
	// Helper for greedySearch. Resets visited array to false
	public static void resetVisited() {
		for (int i = 0; i < CITIES; i++) {
			visited[i] = false;
		}
	}
	
	/**
	 * Calls regular ucSearch N amount of times, times each search, prints result
	 */
	public static void uniformCost() {
		for (byte i = 2; i <= N; i++) {
			long timeStart = System.nanoTime();
			
			ucSearch(i, false);
			
			long timeEnd = System.nanoTime();
			System.out.println("Runtime: " + (timeEnd - timeStart)/1000000 + " ms\n");
		}
	}
	
	/**
	 * Uniform cost search. Finds the shortest tour between a set of cities using breadth first
	 * search and comparing cumulative path lengths. Has the option to apply the condition that
	 * only nodes that don't cross existing path are enqueued. Prints resulting tour, length of
	 * tour, and the number of enqueues and dequeues.
	 * 
	 * @param size	Number of cities included in search
	 * @param inclass	Determines whether to use in-class heuristic
	 */
	public static void ucSearch(byte size, boolean inclass) {
		// reset static variables
		nqs = 0;
		dqs = 0;
		
		byte[] completeTour = new byte[size];
		float shortest = Float.MAX_VALUE; // completed path length, initialize to max value
		float curLength = 0; // start at zero
		
		PQNode current = new PQNode(completeTour, (byte) -1, (byte) 0, curLength); // first node
		PQueue pqueue = new PQueue(current); // create priority queue
		
		while(!pqueue.isEmpty()) {
			current = pqueue.dequeue();
			
			if (current.cost < shortest) { // dequeued node is shorter than shortest tour so far
				if (current.allCities()) { // checks if current.tour visits all cities
					// add distance from end to start to complete tour
					curLength = current.cost + dists[current.tour[current.latestIdx]][0];
					
					if (curLength < shortest) { // if complete tour is shorter than shortest
						completeTour = current.tour; // replace curPath
						shortest = curLength; // replace shortest
					}
				}
				else { // current.tour still needs cities
					for (byte i = 0; i < size; i++) { // loop through all cities
						if (!current.inPath(i)) { // if i is not already in current.tour
							if (inclass) { // if using in-class heuristic
								if (!current.crosses(i)) // check for path crossing
									pqueue.enqueue(new PQNode(current.tour, current.latestIdx, i,
											dists[current.tour[current.latestIdx]][i] + current.cost));
							}
							else // not using in-class heuristic
								pqueue.enqueue(new PQNode(current.tour, current.latestIdx, i,
										dists[current.tour[current.latestIdx]][i] + current.cost));
						}
					}
				}
			}
			else // dequeued node is longer than shortest, no need to keep searching
				break;
		}
		System.out.println(new java.util.Date());
		// print completed tour and other info
		for (int i = 0; i < size; i++)
			System.out.print(completeTour[i] + "-");
		System.out.println(0);
		
		System.out.println("Size: " + size);
		System.out.println("Distance: " + shortest);
		System.out.println("Enqueues: " + nqs);
		System.out.println("Dequeues: " + dqs);
	}
	
	/**
	 * Calls ucSearch including inclass condition N amount of times, times each search, prints result
	 */
	public static void inClass() {
		for (byte i = 13; i <= N; i++) {
			long timeStart = System.nanoTime();
			
			ucSearch(i, true);
			
			long timeEnd = System.nanoTime();
			System.out.println("Runtime: " + (timeEnd - timeStart)/1000000 + " ms\n");
		}
	}
		
	// Main method. Calls methods for distance matrix and the search algorithms
	public static void main(String[] args) {
		try {
			createMatrix();
			
			//greedy();
			//uniformCost();
			inClass();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
