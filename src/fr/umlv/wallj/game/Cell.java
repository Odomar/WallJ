package fr.umlv.wallj.game;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

/**
 * Represents a cell in the grid of a board. It can be either a Wall, a Garbage, a Trashcan, or an Empty cell.
 * The four constants {@code WALL}, {@code EMPTY}, {@code GARBAGE} and {@code TRASHCAN} are the differents types of Cells.
 * Each cells has x and y coordinates.
 * @author Severin Gosset - Denis Biguenet
 */
public interface Cell {
	public final static int WALL = 'W';
	public final static int EMPTY = ' ';
	public final static int GARBAGE = 'G';
	public final static int TRASHCAN = 'T';
	
	/**
	 * Returns the x of the cell.
	 * @return the x of the cell.
	 */
	public int getX();
	
	/**
	 * Returns the y of the cell.
	 * @return the y of the cell.
	 */
	public int getY();
	
	/**
	 * Returns if the cell is considered as Empty, which means the player can walk on it.
	 * @return true if the cell is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns a new Cell, following the given type.
	 * @param world the world where the new cell must be created
	 * @param x the x of the cell
	 * @param y the y of the cell
	 * @param type the type of the cell. Must be one of the constant of Cell (W, ' ', G, T).
	 * @return the newly created cell;
	 */
	public static DisplayableCell newCell(World world, int x, int y, int type) {
		if (x < 0 || y < 0)  
			throw new IllegalArgumentException("the cell must have positive Coordinates");
		DisplayableCell newCell;
		switch(type) {
			case ' ' :
				return new Empty(x, y);
			case 'G' : {
				Body body = DisplayableCell.initializeBody(world, x, y, BodyType.DYNAMIC, type);
				newCell = new Garbage(x, y, body);
				return newCell;
			}
			case 'W' :{
				Body body = DisplayableCell.initializeBody(world, x, y, BodyType.STATIC, type);
				newCell = new Wall(x, y, body);
				return newCell;
			}
			case 'T' :{
				Body body = DisplayableCell.initializeBody(world, x, y, BodyType.STATIC, type);
				newCell = new Trashcan(x, y, body);
				return newCell;
			}
			default : throw new IllegalArgumentException("invalid type");
		}
	}
	
}
