package fr.umlv.wallj.pathfinding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Objects;

import fr.umlv.wallj.display.Displayable;
import fr.umlv.wallj.game.Sizes;

/**
 *  Path represents the best way between two cells. \n
 *  The {@code path} is an ArrayList containing the way to go from the start node (player's position)
 *  to the arrival node (player's choice).
 *  The {@code index} is the current index of the path.
 * @author Severin Gosset - Denis Biguenet
 */
public class Path implements Displayable {
	private final ArrayList<Node> path;
	private int index;
	
	/**
	 * Creates a new path with the given list of nodes.
	 * @param path the list of nodes of the path.
	 */
	public Path(ArrayList<Node> path) {
		this.path = Objects.requireNonNull(path, "Path must not be null !");
		index = 0;
	}
	
	/**
	 * Returns the size of the current path.
	 * @return the size.
	 */
	public int size() {
		return path.size();
	}
	
	/**
	 * Increments the index of the current path and returns the next node.
	 * @return the next node.
	 */
	public Node next() {
		index++;
		if (index < path.size())
			return path.get(index);
		return null;
	}

	
	/**
	 * Draws the path in the game window.
	 */
	public void draw(Graphics2D graphics) {
		for (int i = 0; i < path.size(); i++) {
		    graphics.setColor(color(i));
		    graphics.fill(shape(i));
		}
	}
	
	/**
	 * Returns a color to draw the path.
	 * @param i the index.
	 * @return a color depending on the value of i. 
	 */
	private Color color(int i) {
		if (i < index)
			return Color.ORANGE;
		return Color.GREEN;
	}

	/**
	 * Creates a new shape and returns it.
	 * @param i : the current index of the path.
	 * @return the created shape.
	 */
	public Shape shape(int i) {
		return new Ellipse2D.Float(path.get(i).getX() * Sizes.STEP + Sizes.TOP_MARGIN + Sizes.CIRCLES_MARGIN, 
				path.get(i).getY() * Sizes.STEP + Sizes.LEFT_MARGIN + Sizes.CIRCLES_MARGIN, 
				Sizes.CIRCLES_DIAMETER, Sizes.CIRCLES_DIAMETER);
	}

}
