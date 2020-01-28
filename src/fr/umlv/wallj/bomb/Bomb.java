package fr.umlv.wallj.bomb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fr.umlv.wallj.display.Displayable;
import fr.umlv.wallj.game.Sizes;

/**
 * Represents a Bomb. After a specific amount of time, a bomb can explode, 
 * applying an impulse to all nearby fixtures.
 * It implements Displayable as a Bomb can be drawn in the application window.
 * The {@code timeLeft} field is the time before the explosion, and after the start of the physics phase.
 * The {@code body} field is the body of the bomb. It represents its position in the JBox2D world, 
 * and its coordinates are differents from the Bomb's one.
 * The {@code (x, y)} couple of field are the coordinate of the Bomb in the Board. 
 * It varies from 0 to the length/width of the board. 
 * The {@code exp} field is the description of the explosion of the bomb. It contains all the information
 * about the explosion, and is null until the bomb explode
 * @author Séverin Gosset - Denis Biguenet
 */
public class Bomb implements Displayable {
	private double timeLeft;
	private final Body body;
	private final int x;
	private final int y;
	private Explosion exp;
	
	private Bomb(int x, int y, Body body) {
		if (x < 0 || y < 0)
			throw new IllegalArgumentException("bomb coordinates must be positives !");
		timeLeft = 1;
		this.x = x;
		this.y = y;
		this.body = Objects.requireNonNull(body);
		// While the bomb doesn't explode there is no explosion.
		this.exp = null;
	}
	
	/**
	 * Creates a new Bomb with the given coordinates in the given World.
	 * @param world the world in which the bomb should be created.
	 * @param x the abscissa of the bomb on the board.
	 * @param y the ordinate of the bomb on the board.
	 * @return the newly created Bomb.
	 */
	public static Bomb newBomb(World world, int x, int y) {
		Body body;
		BodyDef bombDef = new BodyDef();
		bombDef.position = new Vec2(Sizes.LEFT_MARGIN + (y * Sizes.STEP) + Sizes.STEP / 2, 
				Sizes.TOP_MARGIN + (x * Sizes.STEP) + Sizes.STEP / 2);
		bombDef.type = BodyType.STATIC;
		body = world.createBody(bombDef);
		CircleShape cs = new CircleShape();
		cs.m_radius = Sizes.BOMB_RADIUS;
		FixtureDef fixDef = new FixtureDef();
		fixDef.density = 1;
		fixDef.shape = cs;
		fixDef.restitution = 0.85f;
		body.createFixture(fixDef);
		return new Bomb(x, y, body);
	}
	
	/**
	 * Increase the time left before the explosion of the bomb (max 99s)
	 */
	public void incrementTimeLeft(){
		if(timeLeft < 99)
			timeLeft++;
	}
	
	/**
	 * Decrease the time left before the explosion of the bomb.
	 * This method can be used during game.refresh or during game.physics(), so we dont need to check timeLeft
	 * @param amount the amount of time that must be decreased.
	 */
	public void  decrementTimeLeft(double amount) {
		timeLeft -= amount;
	}
	
	/**
	 * Decrease the time left before the explosion of the bomb (min 1s)
	 * This method is used during game.refresh() (the placement of the bomb), thus we need to have timeLeft above 1, 
	 * in order to have a strictly positive time. 
	 */
	public void  decrementTimeLeft() {
		if(timeLeft > 1)
			decrementTimeLeft(1);
	}
	
	/**
	 * Returns the time left before the explosion of the bomb
	 * @return the time.
	 */
	public double getTimeLeft() {
		return timeLeft;
	}
	
	/**
	 * Returns a string representation of the bomb
	 * @return the String
	 */
	@Override
	public String toString() {
		Vec2 location = body.getPosition();
		return "Bomb (" + x + ", " + y + ")/(" + location.x + ", " + location.y + ") time left : " + timeLeft;
	}
	
	/**
	 * Returns if the bomb is exploding, which means the time before the explosion is null.
	 * @return true if the time is equal to 0, false if not.
	 */
	public boolean isExploding() {
		return timeLeft <= 0;
	}
	
	/**
	 * Returns if the bomb has already exploded, which means its {@code exp} field is not null.
	 * @return true if the explosion has been done.
	 */
	public boolean explosionDone() {
		return exp != null;
	}
	
	/**
	 * Draws the Bomb on the given graphics context. A bomb is represented by a orange filled circle.
	 * If the explosion has been done, it will also be drawn.
	 * @param graphics the graphics in which the bomb must be drawn.
	 */
	@Override
	public void draw(Graphics2D graphics) {
		Vec2 location = body.getPosition();
	    graphics.setColor(Color.ORANGE);
	    /* graphics.fill(new Ellipse2D.Float(
	    		y * Sizes.STEP + Sizes.TOP_MARGIN, 
				x * Sizes.STEP + Sizes.LEFT_MARGIN, 
				Sizes.BOMB_DIAMETER, Sizes.BOMB_DIAMETER)); */
	    graphics.fill(new Ellipse2D.Float(
		location.x - Sizes.STEP / 2, location.y - Sizes.STEP / 2,
		Sizes.BOMB_RADIUS * 2, Sizes.BOMB_RADIUS * 2));
	    if(exp == null) {
		    graphics.setColor(Color.BLACK);
	    	graphics.drawString(Double.toString(timeLeft), 
	    			y * Sizes.STEP + Sizes.TOP_MARGIN, 
	    			x * Sizes.STEP + Sizes.LEFT_MARGIN);
	    }
    	else {
			exp.draw(graphics);
		}
	}
	
	/**
	 * Makes the bomb explode, applying all the forces on the moveable situaded inside the radius of the explosion.
	 */
	public void explode() {
		Vec2 location = body.getPosition();
		this.exp = new Explosion(location, 32);
		/* Etrange : faut inverser les coordonnées, c'est degueu mais sinon ça marche pas */
		float x =  location.x;
		location.x = location.y;
		location.y = x;
		for (Vec2 end : exp.getEndList()) {
			body.m_world.raycast(exp, location, end);
		}
		
		x =  location.x;
		location.x = location.y;
		location.y = x;
	}
}
