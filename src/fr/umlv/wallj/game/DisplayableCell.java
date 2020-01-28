package fr.umlv.wallj.game;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fr.umlv.wallj.display.Displayable;

/**
 * Represents a displayable cell, which means a cell that can be displayed in a graphic context.
 * The {@code (x, y)} fields are the position of the cell in the board.
 * The {@code type} field of the cell is used to know the type of the cell : a Wall, a Garbage, a Trashcan or an Empty cell.
 * The {@code body} field is the body used by JBox2D to manage physic of the cell.
 * @author Sevrin Gosset - Denis Biguenet
 */
abstract class DisplayableCell implements Cell, Displayable {
	// x and y means the position on the grid of the board. 
	// Position on the screen, and used by JBox2D, are Cell.body.getPosition().x/y
	private final int x;
	private final int y;
	private int type;
	private final Body body;
	
	DisplayableCell(int x, int y, int type, Body body) {
		if (x < 0 || y < 0) {
			throw new  IllegalArgumentException();
		}
		this.x = x;
		this.y = y;
		this.type = Objects.requireNonNull(type, "type must not be null !");
		this.body = body;
	}
	
	static Body initializeBody(World world, int x, int y, BodyType type, int cellType) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(Sizes.LEFT_MARGIN + x * Sizes.STEP + Sizes.STEP / 2, Sizes.TOP_MARGIN + y * Sizes.STEP + Sizes.STEP / 2);
		bodyDef.type = type;
		PolygonShape shape = new PolygonShape();	
		shape.setAsBox(Sizes.STEP / 2,  Sizes.STEP / 2);
		Body body = world.createBody(bodyDef);
		body.setUserData(cellType);
		FixtureDef fixDef = new FixtureDef();
		fixDef.density = 1;
		fixDef.shape = shape;
		fixDef.restitution = 0.85f;
		body.createFixture(fixDef);
		return body;
	}
	
	/**
	 * Returns the x of the case.
	 * @return the x.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y of the case.
	 * @return the y.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Returns a String representation of the Cell.
	 * @return the String.
	 */
	@Override
	public String toString() {
		return type + " : (" + x + ", " + y + ")";
	}
	
	Shape shape() {
		return new Rectangle2D.Float(body.getPosition().y - Sizes.STEP / 2, body.getPosition().x  - Sizes.STEP / 2, Sizes.STEP, Sizes.STEP);
	}
	
	/**
	 * returns the body of the cell.
	 * @return the body
	 */
	public Body getBody() {
		return body;
	}
}
