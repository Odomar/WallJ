package fr.umlv.wallj.game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Manages the collision in the JBox2D world. Implements ContactListener. 
 * Only beginContact is used among the ContactListener method, so the others are empty.
 * @author Severin Gosset - Denis Biguenet
 */
public class CollisionHandler implements ContactListener{
	/**
	 * Defines the way the collision are handled. This specific method is called at the start of a contact :
	 * we know at least one of the fixtures involved in the contact is a garbage, as only the garbages moves.
	 * So if one of the fixture is a trashcan, the other is a garbage, and it must be destryed. 
	 * We don't destroy the garbage in this method because it can cause some NullPointerException.
	 * @param contact the contact to manage.
	 */
	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.m_fixtureA;
		Fixture b = contact.m_fixtureB;
		
		if(a.getBody().m_userData.equals(Cell.TRASHCAN))
			b.getBody().setUserData(Garbage.DESTROY);
		
		else if(b.getBody().m_userData.equals(Cell.TRASHCAN))
			a.getBody().setUserData(Garbage.DESTROY);
	}

	@Override
	public void endContact(Contact contact) { }

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) { }

	@Override
	public void preSolve(Contact contact, Manifold man) { }

}
