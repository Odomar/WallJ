package fr.umlv.wallj.game;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.dynamics.Body;

/**
 * Garbage is the element that the player must destroy to win.
 * The {@code DESTROY} field is the constant telling that a garbage must be destroyed, and placed in Body.userData.
 * @author Severin Gosset - Denis Biguenet.
 */
public class Garbage extends DisplayableCell {
	public static final int DESTROY = -1;
	/**
	 * Creates a new garbage.
	 * @param x the x of the garbage
	 * @param y the y of the garbage
	 * @param body the body of the garbage
	 */
	public Garbage(int x, int y, Body body) {
		super(x, y, GARBAGE, body);
	}
	
	/**
	 * Tests if the current case is empty or not, which means there is no wall or garbage or trashcan.
	 * @return false garbage isn't an empty cell.
	 */
	public boolean isEmpty() {
		return false;
	}
	
	/**
	 * Draws a garbage with a green color.
	 * @param graphics the graphics context.
	 */
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.GREEN);
	    graphics.fill(shape());
	}
}
