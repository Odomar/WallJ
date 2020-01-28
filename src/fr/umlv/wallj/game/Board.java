package fr.umlv.wallj.game;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.umlv.wallj.bomb.Bomb;
import fr.umlv.wallj.display.Displayable;

/**
 * A board represents a whole level of a game. It contains all the info about the current level :\n
 * The {@code player} field reprensents the player, with its position.
 * The {@code bombLeft} field is the number of remaining bomb which the player can land.
 * The {@code world} is the JBox2D world which manage all th physic of the level.
 * The {@code grid} field is the grid of all the walls, loaded from the level.txt file.
 * The {@code width/length} fields are the dimension of the level.
 * The {@code bombMap} field is the map containing all the Bomb. 
 * It is reprensresented by a HashMap because the order does not matter, and we want a quick access to the bomb. 
 * That's also why the Key of the map is a Vec2, in order to represents the coordinates of the bomb, 
 * thus we can quickly have access to the bomb in a {@code (x, y)} given position.
 * The {@code garbageList} field is the list of all the garbage of the level.
 * @author odomar
 *
 */
public class Board implements Displayable{
	private Player player;
	private int bombLeft;
	private final World world;
	private final DisplayableCell[][] grid;
	private final int width;
	private final int length;
	private final HashMap<Vec2, Bomb> bombMap;
	private final ArrayList<DisplayableCell> garbageList;
	
	private Board(World world, int length, int width, DisplayableCell[][] grid, ArrayList<DisplayableCell> garbageList) {
		player = new Player(-1, -1);
		bombLeft = 3;
		this.world = world; 
		this.width = width;
		this.length = length;
		this.grid = grid;
		this.garbageList = garbageList;
		bombMap =  new HashMap<>();
	}
	
	/**
	 * Initialize the board from a file.
	 * @param level : the level's number, used to file opening.
	 * @param world : a world for JBox2D.
	 * @return a board to represent the level.
	 * @throws IOException when the required level.txt is not found, or another IOException happens.
	 */
	public static Board initializeBoard(int level, World world) throws IOException {
		Board b;
		Path p = Paths.get("levels/level" + level + ".txt");
		int width = 0;
		int length = 0;
		ArrayList<DisplayableCell> garbageList =  new ArrayList<>();
		ArrayList<ArrayList<Cell>> levelArray = fillGrid(world, p, garbageList);
		width = levelArray.get(0).size();
		length = levelArray.size();
		DisplayableCell[][] grid = createLevelArray(levelArray, length, width);
		b = new Board(world, length, width, grid, garbageList);
		return b;
	}
	
	/**
	 * Fills the grid with the 'letters' (in ASCII code) of the given file.
	 * Also tests if the board may be legal, i.e. the file only contains corrects letters : W, T, G, J,
	 * and there is one and only one player
	 * 
	 * @param p the path to the file.
	 * @return true if the grid is correctly filled, false if not. 
	 * @throws IOException when the required level.txt is not found, or another IOException happens.
	 */
	private static ArrayList<ArrayList<Cell>> fillGrid(World world, Path p, ArrayList<DisplayableCell> garbageList) throws IOException {
		ArrayList<ArrayList<Cell>> levelArray =  new ArrayList<>();
		String line;
		int i = 0, j = 0;
		
		BufferedReader reader = Files.newBufferedReader(p);
		line = reader.readLine();
		while(line != null) {
			ArrayList<Cell> lineArray = new ArrayList<>();
			i = 0;
			for(int c : line.toCharArray()) {
				if(charIsLegal(c)) {
					DisplayableCell cell = Cell.newCell(world, j, i, c);
					lineArray.add(cell);
					if (c == Cell.GARBAGE) {
						garbageList.add(cell);
					}
				}
				else {
					throw new IllegalStateException("Illegal character in the level.");
				}
				i++;
			}
			j++;
			levelArray.add(lineArray);
			line = reader.readLine();
		}	
		return levelArray;
	}
	
	/**
	 * Convert the ArrayList of Arraylist of Cell levelArray to a double dimensions Array of Cell.
	 * @param levelArray : the ArrayList that represents the grid.
	 * @param length : the grid's length.
	 * @param width : the grid's width.
	 * @return
	 */
	private static DisplayableCell[][] createLevelArray(ArrayList<ArrayList<Cell>> levelArray, int length, int width) {
		int i = 0;
		if(length <= 2 || width <= 2)
			throw new IllegalStateException("The grid shouldn't be flat!");
		DisplayableCell[][] level = new DisplayableCell[length][width];
		for(ArrayList<Cell> line : levelArray) {
			line.toArray(level[i]);
			i++;
		}
		return level;
	}
	
	/**
	 * Returns the content of the grid in the (x, y) position.
	 * @param x the x of the required cell
	 * @param y the y of the required cell
	 * @return the content of the grid.
	 */
	public Cell getContent(int x, int y) {
		return grid[x][y];
	}
	
	/**
	 * Test if the current level is legal or not : 
	 * The level is legal if it exists a structure with many walls/trashes who come back to the start case (a polygon).
	 * @return true if the level is legal, if not return false;
	 */
	public boolean isValidLevel() {
		ArrayList<Cell> visitedStructures = new ArrayList<>();
		ArrayList<Cell> newStructure = new ArrayList<>();
		for(int i = 0; i < length; i++) {
			for(int j = 0; j < width; j++) {
				if(grid[i][j].isEmpty()) {
					continue;
				}
				if(visitedStructures.contains(grid[i][j])) {
					continue;
				}
				if(isValidStructure(i, j, newStructure)) {
					return true;
				}
				visitedStructures.addAll(newStructure);
				newStructure.clear();
			}
		}
		return false;
	}
	
	/** 
	 * When we visit a new case (wall/trash) we have a structure (a set of walls/trashes).
	 * This method tests if this structure is legal or not.
	 * 
	 * @param x : structure's start abscissa.
	 * @param y : structure's start ordored.
	 * @pram structure
	 * @return true if the structure is a polygon and false in the opposite case.
	 */
	boolean isValidStructure(int x, int y, ArrayList<Cell> structure) {
		if(x >= width || x < 0 || y >= length || y < 0) {
			return false;
		}
		
		if(grid[x][y].isEmpty())
			return false;
		
		if(structure.contains(grid[x][y])) {
			if(structure.get(0).equals(grid[x][y])) {
				Cell start = structure.get(structure.size() - 1);
				if((start.getX() == x - 1 || start.getX() == x + 1 || start.getX() == x) && (start.getY() == y - 1 || start.getY() == y + 1 || start.getY() == y)) {
					return true;
				}
			}
			else 
				return false;
		}
		
		else {
			structure.add(grid[x][y]);
			if(isValidStructure(x + 1, y, structure))
		        return true;
		    if(isValidStructure(x - 1, y, structure))
		        return true;
		    if(isValidStructure(x, y + 1, structure))
		        return true;
		    if(isValidStructure(x, y - 1, structure))
		        return true;
		}
		return false;
	}
	
	/**
	 * Returns the width of the grid.
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the length of the grid.
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Returns the player of the Game
	 * @return the player.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Tests wether the char represented by c is a legal character or not.
	 * @param c the int representing the character to test.
	 * @return  true if c is either a wall, a trashcan, a garbage, the player, or an empty case, false if not.
	 */
	public static boolean charIsLegal(int c) {
		return c == DisplayableCell.WALL || c == DisplayableCell.TRASHCAN || c == DisplayableCell.GARBAGE || Character.isWhitespace(c);
	}
	
	/**
	 * Returns a string representing the grid of the Board, with letters.
	 * @return the String representing the board.
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int j = 0; j < width; j++) {
			for(int i = 0; i < length; i++) {
				//str.append(grid[i][j].getType());
			}
			str.append('\n');
		}
		return str.toString();
	}
	
	/**
	 * Drops a bomb in the player's current position or take the bomb.
	 */
	public void dropBomb() {
		int xPos = player.getX();
		int yPos = player.getY();
		Vec2 vec = new Vec2(xPos, yPos);
		if(bombMap.get(vec) != null) {
			bombMap.remove(vec);
			bombLeft += 1;
		}
		else if(bombLeft > 0) {
			bombMap.put(vec, Bomb.newBomb(world, yPos, xPos));
			bombLeft -= 1;
		}	
	}
	
	/**
	 * Set the position of the player in the given position. 
	 * @param x the x of the new position of the player.
	 * @param y the y of the new position of the player.
	 */
	public void setPlayerPos(int x, int y) {
		player.setPos(x, y);
	}

	/**
	 * Returns the content of the grid in the current player's position.
	 * @return the content of the grid.
	 */
	public Cell getContentAtPlayer() {
		if (player.getY() == -1) {
			throw new IllegalStateException("player position is (-1, -1)");
		}
		return grid[player.getY()][player.getX()];
	}
	
	/**
	 * Draws the board in the given graphics. A board draws everything it contains : 
	 * the grid, the bombs, the garbages, and the player.
	 */
	@Override
	public void draw(Graphics2D graphics) {
	    for(int i = 0; i < length; i ++) {
		    for (int j = 0; j < width; j ++) {
		    	DisplayableCell cell = grid[i][j];
		    	cell.draw(graphics);
		    }
	    }
	    for (DisplayableCell cell : garbageList) {
	    	cell.draw(graphics);
	    }
	    for (Map.Entry<Vec2, Bomb> entry : bombMap.entrySet()) {
	    	entry.getValue().draw(graphics);
	    }
	    player.draw(graphics);
	}
	
	/**
	 * Increments or decrements the timeLeft of the nomb at player position.
	 * @param increment Tell if the time must be incremented or decremented.
	 */
	public void setTimeLeft(boolean increment) {
		Bomb b = bombMap.get(new Vec2(player.getX(), player.getY()));
		if (b != null) {
			if (increment)
				b.incrementTimeLeft();
			else
				b.decrementTimeLeft();
		}
	}
	
	/**
	 * Runs all the bomb of the map, decreasing their timeLeft by Sizes.LOOP_TIME 
	 * (divided by 1000 because TimeLeft is in seconds).
	 * Checks if the bomb must explode, and make it explode if needed
	 * @return false if the bombMap still has elements, true if not.
	 */
	public boolean runBomb() {
		Iterator<Bomb> it = bombMap.values().iterator();
		while (it.hasNext()) {
			Bomb b = it.next();
			b.decrementTimeLeft(Sizes.LOOP_TIME / 1000d);
			if (b.isExploding() && !b.explosionDone()) {
				b.explode();
			}
		}
		return bombMap.isEmpty();
	}
	
	/**
	 * Removes all the garbage from the grid. We don't need them in the grid anymore for A*,
	 * and we need display an empty case instead. The garbage still exists in garbageList.
	 */
	public void removeGarbage() {
		for(DisplayableCell cell : garbageList) {
			int x = cell.getX();
			int y = cell.getY();
			grid[x][y] = new Empty(x, y);
		}
	}
	
	/**
	 * Destroys the garbage that has the "DESTROYED" flag in Body.userData. Destroys means destroying the fixture of the body, 
	 * and then remove the garbage from the list.
	 */
	public void destroyGarbage() {
		Predicate<DisplayableCell> toBeDestroyed = cell -> {return cell.getBody().getUserData().equals(Garbage.DESTROY);};
		garbageList.stream()
				   .filter(toBeDestroyed)
				   .forEach(cell -> {world.destroyBody(cell.getBody());});
		garbageList.removeIf(toBeDestroyed);
	}
	
	/**
	 * Steps once the world of the board.
	 */
	public void worldStep() {
		world.step(1/60f, 8, 3);
	}
	
	/**
	 * Set all the physics and more generally all that must be done before the physic phase of the game.
	 * Hides the player and creates a collision handler.
	 */
	public void setPhysics() {
		setPlayerPos(-1, -1);
		CollisionHandler handler = new CollisionHandler();
		world.setContactListener(handler);
	}
	
	/**
	 * Tells if the gale is currently won, which means there is no more garbage in th list.
	 * @return a boolean telling if the gale is won.
	 */
	public boolean isWon() {
		return garbageList.isEmpty();
	}
}
