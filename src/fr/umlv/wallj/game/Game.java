package fr.umlv.wallj.game;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.umlv.wallj.display.Displayer;
import fr.umlv.wallj.pathfinding.Node;
import fr.umlv.wallj.pathfinding.Path;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.KeyboardKey;

/**
 * The main class of the game. It contains everything needed in the game, and its methods.
 * The {@code level} field is the number of the current level.
 * The {@code board} field is the current board of the game. It has the grid and the Jbox2D world.
 * The {@code disp} field is the displayer of the game, that will draw evrything of the game.
 * The {@code path} field is the the path between the player and its destination.
 * The {@code keyboardKeyMap} field is the map of key used for the keyboard events.
 * The {@code victory} fields tell if the game is currently won.
 * The {@code pos} field is the current position of the player.
 * The {@code dest} field is the current destination of the player.
 * These two last fields are used by the A* algorithm, and thus must be Nodes.
 * @author Severin Gosset - Denis Biguenet
 */
public class Game {
	private int level;
	private Board board;
	private Displayer disp;
	private Path path;
	private final Hashtable<Integer, KeyboardKey> keyboardKeyMap;
	private boolean victory;
	private Node pos;
	private Node dest;
	
	/**
	 * Create a new game by initializing the map of keys used for the events.
	 */
	public Game() {
		keyboardKeyMap = new Hashtable<Integer, KeyboardKey>();
		keyboardKeyMap.put(KeyboardKey.B.ordinal(), KeyboardKey.B);
		keyboardKeyMap.put(KeyboardKey.F.ordinal(), KeyboardKey.F);
		keyboardKeyMap.put(KeyboardKey.UP.ordinal(), KeyboardKey.UP);
		keyboardKeyMap.put(KeyboardKey.DOWN.ordinal(), KeyboardKey.DOWN);
	}
	
	/**
	 * Initialize the next level of the game, by creating the board and the displayer of the current level.
	 * @param level the number of the current level.
	 * @param context the application context in which the level will be drawn.
	 * @throws IOException if the next level doesn't exisrs, or another IOException occurs.
	 */
	public void nextLevel(int level, ApplicationContext context) throws IOException {
		board = Board.initializeBoard(level, new World(new Vec2(0, 0)));
		disp = new Displayer(board, Objects.requireNonNull(context));
		victory = false;
		this.level = level;
	}
	
	/**
	 * Sets the position of the player, when it is not on the board. Waits a click and places the player in the position.
	 */
	public void setFirstPlayerPos() {
		disp.drawInterface(level);
		disp.render(null);
		do {
			while((disp.waitEvent() != -1)) {
				/* Waiting for a click, to set the position */
			}
			board.setPlayerPos(disp.getXClick(), disp.getYClick());
		} while(!board.getContentAtPlayer().isEmpty());
		disp.render(null);
	}
	
	/**
	 * Moves the player between its current location and the position of the click, if it's possible. 
	 * Draws the path while doing so.
	 */
	private void move(){
		pos = new Node(board.getPlayer().getX(), board.getPlayer().getY(), null);
		path = Node.shortestWay(board, dest, pos);
		if (path != null) {
			Node next = path.next();
			while(next != null) {
				long t0 = System.nanoTime();
				disp.render(path);
				board.getPlayer().moveOneCell(next);
				long t1 = System.nanoTime() - t0;
				try {
					Thread.sleep(Sizes.LOOP_TIME - t1/1000000);
				} catch (InterruptedException e) {
					// Since main is the only thread, there should not be any InterruptedEcxeption
					e.printStackTrace();
				}
				next = path.next();
			}
			board.setPlayerPos(dest.getX(), dest.getY());
		}
		dest = null;
	}
	
	/**
	 * Runs once the main loop of the first part of the game. Waits an event : 
	 * if it's a click, moves the player to the position, 
	 * if it's 'B', drops a Bomb,
	 * if it's 'F', triggers all the bombs.
	 * @return false if the loop must be stopped (the user pressed 'F'), true if not
	 */
	public boolean refresh() {
		dest = null;
		int res;
		res = disp.waitEvent();
		if(res >= 0) {
			disp.render(null);
			KeyboardKey pressedKey = keyboardKeyMap.get(res);
			if(pressedKey != null) {
				switch(pressedKey) {
					case B : {
						board.dropBomb();
						disp.render(null);
						return true;
					}
					case F : {
						disp.render(null);
						return false;
					}
					case UP : {
						
						board.setTimeLeft(true);
						disp.render(null);
						return true;
					}
					case DOWN : {
						board.setTimeLeft(false);
						disp.render(null);
						return true;
					}
					default : throw new IllegalStateException("key found in the hashtable, but not found with the switch.");
				}
			}
			return true;
		}		
		else if(res == -1) {
			int x = disp.getXClick();
			int y = disp.getYClick();
			if (board.getContent(y, x).isEmpty())
				dest = new Node(x, y, null);
		}
				
		if (dest != null) {
			move();
			disp.render(null);
		}
		return true;
	}
	
	/**
	 * Runs once the main loop of the second partof the game. Launches the physic :
	 * Run the Bomb and the world.
	 * Destroy all the garbage that hit a trashcan.
	 * May also takes an event :
	 * If all the garbages are gone, stops the loop and set victory flag to true.
	 * If the user press a keyboard button, stops the loop.
	 * Else, does not stop the loop.
	 * @return if the loop must continue.
	 */
	public boolean physics() {
		//One call of the function must be exactly Sizes.LOOP_TIME long
		long t0 = System.nanoTime();
		board.runBomb();
		board.worldStep();
		board.destroyGarbage();
		disp.render(null);
		if(board.isWon()) {
			victory = true;
			return false;
		}
		if (disp.waitEvent() > 0) {
			return false;
		}
		long t1 = System.nanoTime() - t0;
		try {
			if (Sizes.LOOP_TIME - t1/1000000 > 0l)
				Thread.sleep(Sizes.LOOP_TIME - t1/1000000);
		} catch (InterruptedException e) {
			// Since main is the only thread, there should not be any InterruptedEcxeption
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Sets up the physic of the game :
	 * Remove the garbage from the board's grid
	 */
	public void setPhysics() {
		board.removeGarbage();
		board.setPhysics();
	}
	
	/**
	 * Makes the game end, by drawing the defeat or victory messages, and waiting for a key to be pressed.
	 * @return if game is won.
	 */
	public boolean endGame() {
		String[] endMessage = new String[2];
		if (victory) {
			endMessage[0] = "VICTORY !";
			endMessage[1] = "Press any key to continue to next level";
		}
		else {
			endMessage[0] = "DEFEAT !";
			endMessage[1] = "Press any key to replay level";
		}
		disp.drawMessage(endMessage);
		while(disp.waitEvent() == -2) {
			// Waiting for the user to leave the game.
		}
		return victory;
	}
}
