package fr.umlv.wallj.game;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.dynamics.Body;

/**
 * Wall represents a basic obstacle, the garbages bounce on it. 
 * @author Severin Gosset - Biguenet Denis
 */

public class Wall extends DisplayableCell {
	/**
	 * Creates a new wall.
	 * @param x the x of the wall
	 * @param y the y of the wall
	 * @param body the body of the wall
	 */
	public Wall(int x, int y, Body body) {
		super(x, y, WALL, body);
	}

	/**
	 * Tests if the current case is empty or not, which means there is no wall or garbage or trashcan.
	 * @return false the wall isn't an empty cell.
	 */
	public boolean isEmpty() {
		return false;
	}
	
	/**
	 * Draws a wall with a gray color.
	 * @param graphics the graphics context.
	 */
	public void draw(Graphics2D graphics) {
	    graphics.setColor(Color.GRAY);
	    graphics.fill(shape());
	}
}
