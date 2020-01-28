package fr.umlv.wallj.game;

/**
 * Contains constants used by the game, in order to change them easily if needed.
 * @author Severin Gosset - Denis Biguenet
 */
public class Sizes {
	public final static int LEFT_MARGIN = 100;
	public final static int TOP_MARGIN = 100;
	public final static int STEP = 15;
	public final static int CIRCLES_MARGIN = 3;
	public final static int CIRCLES_DIAMETER = STEP - CIRCLES_MARGIN * 2;
	public final static int BOMB_RADIUS = (Sizes.CIRCLES_DIAMETER + Sizes.CIRCLES_MARGIN * 2) / 2;
	public final static int EXPLOSION_RADIUS = 4 * STEP;
	public final static long LOOP_TIME = 30;
	public final static int FONT_SIZE = 25;
}
