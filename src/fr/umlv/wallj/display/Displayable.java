package fr.umlv.wallj.display;

import java.awt.Graphics2D;

/**
 * A Displayable object is an object that can be represented in a Zen5 graphic window.
 * It must implements the draw methode, which allows it to be drawn.
 * @author Severin Gosset - Denis Biguenet
 */
public interface Displayable {
	/**
	 * Draws an object in the given graphic context.
	 * @param graphics the graphic in which the Displayable must be drawn.
	 */
	public abstract void draw(Graphics2D graphics);
}
