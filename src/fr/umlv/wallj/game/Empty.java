package fr.umlv.wallj.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Empty is a cell where the elements can move. Empty cells doesn't have body. 
 * @author Severin Gosset - Denis Biguenet
 */
public class Empty extends DisplayableCell{
	/**
	 * Creates a new empty cell.
	 * @param x the x of the cell
	 * @param y the y of the cell
	 */
	public Empty(int x, int y) {
		super(x, y, EMPTY, null);
	}
	
	/**
	 * Tests if the current case is empty or not, which means there is no wall or garbage or trashcan.
	 * @return true, as an empty cell is empty.
	 */
	public boolean isEmpty() {
		return true;
	}
	
	/**
	 * Draws a empty cell with a white color.
	 * @param graphics the graphics context.
	 */
	public void draw(Graphics2D graphics) {
	    graphics.setColor(Color.WHITE);
	    graphics.fill(new Rectangle2D.Float(getY() * Sizes.STEP + Sizes.LEFT_MARGIN, getX() * Sizes.STEP + Sizes.TOP_MARGIN, Sizes.STEP, Sizes.STEP));
	}
}
