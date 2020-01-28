package fr.umlv.wallj.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import fr.umlv.wallj.display.Displayable;
import fr.umlv.wallj.pathfinding.Node;

/**
 * Represents a player in the game. A player has coordinates (the {@code (x, y)} fields}, 
 * and implements Displayable as it can be drawn in the graphic window.
 * @author Severin Gosset - Denis Biguenet.
 */
public class Player implements Displayable {
	private int x;
	private int y;
	
	/**
	 * Creates a new player in the given x, y positions.
	 * @param x the x of the player
	 * @param y the y of the player
	 */
	public Player(int x, int y) {
		this.x = x;
		this.y = y;	
	}
	
	/**
	 * Moves the player of one case towards the given node
	 * The player must be adjacent to the node.
	 * @param next the node to go.
	 */
	public void moveOneCell(Node next) {
		if (Math.pow(next.getX() - x, 2) > 1 || Math.pow(next.getY() - y, 2) > 1)
			throw new IllegalArgumentException();
		
		x = next.getX();
		y = next.getY();
	}
	
	/**
	 * Sets the position of the player.
	 * @param x the new x pos of the player.
	 * @param y the new y pos of the player.
	 */
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;	
	}
	
	/**
	 * Returns the abscissa of the position of the player
	 * @return the x of the player
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the ordinate of the position of the player
	 * @return the x of the player
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Draws the player in the given graphic context.
	 * The player is represented by a red circle, and is not drawn if its not on the board.
	 */
	@Override
	public void draw(Graphics2D graphics) {
		if (x > 0) { 
		    graphics.setColor(Color.RED);
		    graphics.fill(new Ellipse2D.Float(x * Sizes.STEP + Sizes.TOP_MARGIN + Sizes.CIRCLES_MARGIN, 
					y * Sizes.STEP + Sizes.LEFT_MARGIN + Sizes.CIRCLES_MARGIN, 
					Sizes.CIRCLES_DIAMETER, Sizes.CIRCLES_DIAMETER));
		}
	}
}
