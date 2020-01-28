package fr.umlv.wallj.main;

import java.awt.Color;
import java.io.IOException;

import fr.umlv.wallj.game.Game;
import fr.umlv.zen5.Application;

/**
 * The entry point of the game, contain the {@code main} method.
 * @author odomar
 *
 */
public class Main {
	public static void main(String[] args) {
	    Application.run(Color.BLACK, context -> {
			int level = 0;
    		Game game = new Game();
	    	while(true) {
	    		try {
	    			game.nextLevel(level, context);
					game.setFirstPlayerPos();
					while(game.refresh()) {
						/* Waiting for the game to be played, stops when 'F' is pressed */
					}
					game.setPhysics();
					while(game.physics()) {
						/* Waiting for the physics to be done : either there is no more garbage, 
						 * or the user presses a keyboard button
						 */
					}
					if (game.endGame())
						level++;
	    		} catch(IOException e) {
	    			/* There is no way to ensure the IOException is thrown because there is no next level, 
	    			 * except by checking that the exception message is the name of the requested level.
	    			 */
	    			if (e.getMessage().equals("levels/level" + level + ".txt"))
	    				System.out.println("Well played ! You made it through all the levels !");
	    			else
	    				System.err.println("Unexpected IOException : " + e.getMessage());
	    			context.exit(0);
	    		}
	    	}
	    });
	}

}
