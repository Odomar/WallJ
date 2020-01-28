package fr.umlv.wallj.game;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.dynamics.Body;

/**
 * Trashcan allows to the player to destroy the garbages. 
 * @author Sevrin Gosset - Denis Biguenet
 */
public class Trashcan extends DisplayableCell {
	/**
	 * Creates a new trashcan.
	 * @param x the x of the trashcan
	 * @param y the y of the trashcan
	 * @param body the body of the trashcan
	 */
	public Trashcan(int x, int y, Body body) {
		super(x, y, TRASHCAN, body);
	}
	
	/**
	 * Tests if the current case is empty or not, which means there is no wall or garbage or trashcan.
	 * @return false trashcan isn't an empty cell.
	 */
	public boolean isEmpty() {
		return false;
	}
	
	/**
	 * Draws a trashcan with a yellow color.
	 * @param graphics the graphics context.
	 */
	public void draw(Graphics2D graphics) {
	    graphics.setColor(Color.YELLOW);
	    graphics.fill(shape());
	}
}
