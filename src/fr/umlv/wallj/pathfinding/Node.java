package fr.umlv.wallj.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import fr.umlv.wallj.game.Board;

/**
 * A node represents a grid's cell during the A* algorithm. It contains informations about the node : \n
 * The {@code x} is the node abscissa.
 * The {@code y} is the node orderly.
 * The {@code cost} is the cost to go from the start node to the current node.
 * The {@code previous} is the previous player's position according the current node.
 * The {@code heuristicCost} is the theoric cost to go from start node to the arrival node.
 * The heuristicCost is the sum of cost and distance between the current node and the arrival.
 * @author Severin Gosset - Denis Biguenet
 */
public class Node {
	final private int x;
	final private int y;
	private double cost;
	private Node previous;
	private double heuristicCost;
	
	/**
	 * Creates a new new node in the given position, with the given node as its previous.
	 * @param x the x of the node
	 * @param y the y of the node
	 * @param currentNode the current node, that will becme the previous of the new node.
	 */
	public Node(int x, int y, Node currentNode) {
		if (x < 0 || y < 0)
			throw new IllegalArgumentException("the node must have positive coordinates !");
		this.x = x;
		this.y = y;
		previous = currentNode;
	}
	
	/**
	 * Returns a String representation of the node.
	 * @return the string.
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ") cost : " + cost + ", heuristicCost : " + heuristicCost + "\n";
	}
	
	/**
	 * Returns a integer representation of the node.
	 * @return the node's hashCode.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	/**
	 * Test if the current node is equal to the Object obj.
	 * @param obj an object to compare with the current node.
	 * @return true if the current node is equal to obj, else, return false.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	/**
	 * Returns the x of the node.
	 * @return the x value.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the x of the node.
	 * @return the x value.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Calculates the distance between two nodes.
	 * @param arrival arrival point, where the player want to go.
	 * @return a double value corresponding to the distance between arrival and the current node.
	 */
	private double distance(Node arrival) {
		return Math.sqrt(Math.pow((x - arrival.x), 2) + Math.pow((y - arrival.y), 2));
	}
	
	/**
	 * Calculates the theoretic closest node of arrival point.
	 * @param stack an ArrayList of unvisited Nodes.
	 * @return the node which have the lowest heuristicCost.
	 */
	private static Node findClosestNode(ArrayList<Node> stack) {
		Node closestNode = stack.get(0);
		// The closest node is the one with the lowest heuristicCost
		// In case of conflicts the last seen in the list "wons"
		for(Node current : stack) {
			if(current.heuristicCost <= closestNode.heuristicCost) {
				closestNode = current;
			}
		}
		return closestNode;
	}
	
	/**
	 * Stacks the currentNode in the ArrayList stack depending on the contents of stack and seenNodes.
	 * @param stack an ArrayList of unvisited nodes.
	 * @param seenNodes an HashSet of visited nodes.
	 */
	private void stackNeighbor(ArrayList<Node> stack, HashSet<Node> seenNodes) {
		Iterator<Node> iter = stack.iterator();
		/* If this is in seenNodes it already have been treated : nothing to do */
		if(seenNodes.contains(this)) {
			return;
		}
		while(iter.hasNext()) {
			Node current = iter.next();
			if(current.equals(this)) { // if this is in the stack : 
				if(current.heuristicCost <= this.heuristicCost) { // If there already is a cheaper path, nothing to do (we know a better path)
					return;
				}
				iter.remove(); // If it already exists in the stack, more expensively, we remove it.
			}
		}
		stack.add(this); // We add the node in the stack.
	}
	
	/**
	 * Rebuilds the path between arrival and start with the "previous" field.
	 * @return a Path containing the ArrayList of Nodes, corresponding to the path.
	 */
	private Path rebuildPath() {
		ArrayList<Node> path = new ArrayList<>();
		Node current = this;
		while(current != null) {
			path.add(current);
			current = current.previous;
		}
		return new Path(path);
	}
	
	/***
	 * Detects if their is an obstacle on the way to the next node.
	 * Increments the cost of the node depending on the direction (diagonal or not).
	 * @param b level board
	 * @param neighbor the next node to visit.
	 * @param xDir direction on abscissa axis
	 * @param yDir  direction on orderly axis
	 * @return true if their is no obstacle, if not, false.
	 */
	private boolean detectObstacle(Board b, Node neighbor, int xDir, int yDir) {
		if(xDir == 0 || yDir == 0) {			
			if(b.getContent(y + yDir, x + xDir).isEmpty()) {
				neighbor.cost = cost + 1;
				return true;
			}
			return false;
		}
		if((b.getContent(y, x + xDir).isEmpty() || b.getContent(y + yDir, x).isEmpty()) && b.getContent(y + yDir, x + xDir).isEmpty()) {
			neighbor.cost = cost + Math.sqrt(2);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Applies the A* algorithm to go from the start node to the arrival node.
	 * The player can't move on a occupied cell (except the cell containing a bomb).
	 * If the player want to go in the unreachable cell, the A* fails and the player stays on his cell.
	 * @param b the current level.
	 * @param start the node where we are.
	 * @param arrival the arrival node.
	 * @return an ArrayList correspond to the better path between start and arrival.
	 */
	public static Path shortestWay(Board b, Node start, Node arrival) {
		ArrayList<Node> stack = new ArrayList<>(); // List of node to check.
		HashSet<Node> seenNodes = new HashSet<>(); // HashSet containing seen nodes.
		Node currentNode;
		Node neighbor;
		
		start.heuristicCost = start.distance(arrival);
		
		//If the arrival node isn't empty, we can't reach the node.
		if(!b.getContent(arrival.y, arrival.x).isEmpty())
			return null;
		stack.add(start);
				
		while(stack.size() > 0) {	// If the stack is empty there is no possible path.
			currentNode = findClosestNode(stack); // We seek the closest node of the end..
			if(currentNode.x == arrival.x && currentNode.y == arrival.y) 
				return currentNode.rebuildPath(); // If this node is the arrival, we rebuild the path.		
			
			stack.remove(currentNode);
			seenNodes.add(currentNode);
			// We check each neighbor (for each we need to check if its inside the grid).
			if(currentNode.y - 1 >= 0) { 
				for(int i = -1; i < 2; i++) {					
					if(currentNode.x + i >= 0 && currentNode.x + i < b.getWidth()) {
						neighbor = new Node(currentNode.x + i, currentNode.y - 1, currentNode);
						if(currentNode.detectObstacle(b, neighbor, i, -1)) {
							neighbor.heuristicCost = neighbor.distance(arrival) + neighbor.cost;
							neighbor.stackNeighbor(stack, seenNodes);
						}// We stack the neighbor if needed.			
					}
				}
			}
			if(currentNode.x + 1 < b.getWidth()) {
				for(int i = 0; i < 2; i++) {
					if(currentNode.y + i >= 0 && currentNode.y + i < b.getLength()) {
						neighbor = new Node(currentNode.x + 1, currentNode.y + i, currentNode);
						if(currentNode.detectObstacle(b, neighbor, 1, i)) {
							neighbor.heuristicCost = neighbor.distance(arrival) + neighbor.cost;
							neighbor.stackNeighbor(stack, seenNodes);
						}
					}
				}
			}
			if(currentNode.y + 1 < b.getLength()) {
				for(int i = 0; i < 2; i++) {
					if(currentNode.x - i >= 0 && currentNode.x - i < b.getWidth()) {
						neighbor = new Node(currentNode.x - i, currentNode.y + 1, currentNode);
						if(currentNode.detectObstacle(b, neighbor,  -i, 1)) {
							neighbor.heuristicCost = neighbor.distance(arrival) + neighbor.cost;
							neighbor.stackNeighbor(stack, seenNodes);	
						}
					}
				}
			}
			if(currentNode.x - 1 >= 0) {
				neighbor = new Node(currentNode.x - 1, currentNode.y, currentNode);
				if(currentNode.detectObstacle(b, neighbor, -1, 0)) {
					neighbor.heuristicCost = neighbor.distance(arrival) + neighbor.cost;
					neighbor.stackNeighbor(stack, seenNodes);	
				}
			}
		}
		return null;		
	}
}
