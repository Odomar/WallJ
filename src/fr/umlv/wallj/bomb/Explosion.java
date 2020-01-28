package fr.umlv.wallj.bomb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import fr.umlv.wallj.display.Displayable;
import fr.umlv.wallj.game.Sizes;

/**
 * Represents an Explosion of a Bomb. An explosion is represented by severals rays, 
 * each going in a different direction.
 * It implements JBox2D's {@code RayCastCallback} interface, thus we can use reportFixture 
 * in the {@code world.rayCast()} method.
 * The {@code Position} field represents the position of the center of the Explosion. 
 * It's the position of the body of the Bomb, and is the position on the application window.
 * The {@code rayNumber} field is the number of rays fired by the bomb. 
 * This is then the number of {@code rayCast} for each Bomb.
 * The {@code endList} field is the list of all the positions of the end of the rays, 
 * the start being the position of the explosion.
 * @author Séverin Gosset - Denis Biguenet
 */
public class Explosion implements RayCastCallback, Displayable {
	private final Vec2 position;
	private final int rayNumber;
	private final ArrayList<Vec2> endList;
	
	/**
	 * Create a new explosion, with the given position and number of rays.
	 * The endList of the explosion is created in order to have rays in circle around the explosion.
	 * @param position the posiotion of the center of the Bomb.
	 * @param rayNumber the number of rays of the bomb.
	 */
	public Explosion(Vec2 position, int rayNumber) {
		if (position == null || position.x < 0 || position.y < 0)
			throw new IllegalArgumentException("position must be non null !");
		if (rayNumber <= 0) 
			throw new IllegalArgumentException("rayNumber must be positive !");
		this.position = position;
		this.rayNumber = rayNumber;
		this.endList = new ArrayList<>();
		createEndList();
	}
	
	private void createEndList() {
		float degPerRad = 0.017453f;
		for(double i = 0; i < rayNumber; i++) {
			double angle = (i  / rayNumber) * 360 * degPerRad;
			Vec2 dir = new Vec2((float)Math.sin(angle), (float)Math.cos(angle));
			endList.add(new Vec2(dir.y * Sizes.EXPLOSION_RADIUS + position.y, dir.x * Sizes.EXPLOSION_RADIUS + position.x));
		}
	}
	
	/**
	 * The methode used by {@code world.rayCast}. It allows to treat each fixture hit by the ray, 
	 * and to apply a linear impulse to it (if it must be affected by the explsion), 
	 * in order to make the garbage move.
	 * @param fixture the fixture hit by the ray.
	 * @param point the point of the fixture hit.
	 * @param normal the side of the fixture hit.
	 * @param fraction the fraction of the length of the ray which has hit the fixture. 
	 * It's used to know the force to apply to the fixture.
	 */
	@Override
	public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
		if(fixture.getBody().m_type == BodyType.DYNAMIC) {
			Vec2 force = fixture.getBody().getPosition().sub(point);
					force.x *= 60 * (1 - fraction) * Sizes.EXPLOSION_RADIUS;
					force.y *= 60 * (1 - fraction) * Sizes.EXPLOSION_RADIUS;
			Body b = fixture.getBody();
			b.applyLinearImpulse(force, point);
			
			return 1;
		}
		return 1;
	}
	
	/**
	 * returns the endList of the explosion.
	 * @return the endlist.
	 */
	public ArrayList<Vec2> getEndList() {
		return endList;
	}

	/**
	 * Draws the explosion in the given graphic context. An explosion is represented by {@code rayNumber} 
	 * rays in circle around the center of the explosion.
	 * @param graphics the graphic context in which draw the explosion.
	 */
	@Override
	public void draw(Graphics2D graphics) {
		for (Vec2 end : endList) {
			graphics.setColor(Color.ORANGE);
			graphics.draw(new Line2D.Float(position.x, position.y, end.y, end.x));
		}
	}

}
