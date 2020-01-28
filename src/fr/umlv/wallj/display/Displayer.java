package fr.umlv.wallj.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import fr.umlv.wallj.game.Board;
import fr.umlv.wallj.game.Sizes;
import fr.umlv.wallj.pathfinding.Path;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.ScreenInfo;

/**
 * An object allowing to open a window a draw the game. Also manages the events to ensure they are valid, 
 * and give the position of the last click.
 * The {@code b} field is the board associated with the displayer. 
 * It contains all the objects that must be drawn.
 * The {@code context} field is the context of the application. 
 * It is used to render the window with {@code context.renderFrame()} method.
 * The {@code (xClick, yClick)} fields are the position of the last valid click in the window,
 * converted to match with the Board dimension.
 * @author Sevrin Gosset - Denis Biguenet
 */
public class Displayer {
	private final Board b;
	private final ApplicationContext context;
	private int xClick;
	private int yClick;
	
	/**
	 * Creates a new displayer, with the given board in the given context.
	 * @param b the Board to draw with this displayer.
	 * @param context the context in which the displayer will draw.
	 */
	public Displayer(Board b, ApplicationContext context) {
		this.b = Objects.requireNonNull(b);
		this.context = Objects.requireNonNull(context);
	}
	
	/**
	 * Draws the current frame. If path is not null, draws it.
	 * @param path the path to draw. If it's null, only the board will be drawn.
	 */
	public void render(Path path) {
		context.renderFrame(graphics -> {
			b.draw(graphics);
			if (path != null) {
				path.draw(graphics);
			}
		});
	}
	
	/**
	 * Returns the abscissa of the last valid click (i.e. inside the board)
	 * @return the abscissa of the click 
	 */
	public int getXClick() {
		return xClick;
	}

	/**
	 * Returns the ordinate of the last valid click (i.e. inside the board)
	 * @return the ordinate of the click 
	 */
	public int getYClick() {
		return yClick;
	}
	
	/**
	 * Returns the width of the board on the screen.
	 * @return the width of the board on the screen, in pixels.
	 */
	private int getPixelBoardWidth() {
		return b.getWidth() * Sizes.STEP;
	}
	
	/**
	 * Returns the length of the board on the screen.
	 * @return the length of the board on the screen, in pixels.
	 */
	private int getPixelBoardLength() {
		return b.getLength() * Sizes.STEP;
	}
	
	/**
	 * Returns an event from the user. If there is no event, do nothing.
	 * If the users pressed a keyboard button, exits the application, 
	 * and if the user clicked inside the board, fills the xClick and yClick fields with the coordinates of the click,
	 * then convert them into the board's corresponding case coordinate.
	 * @return -2 if there is no event or a false event, -1 if the event is a click inside of the board,
	 * or the value of the pressed key.
	 */
	public int waitEvent() {
		Event event = context.pollEvent();
        if (event == null) {
            return -2;
        }
        Action action = event.getAction();
        if (action == Action.KEY_PRESSED) {
            return event.getKey().ordinal();
        }
        else if (action == Action.POINTER_UP) {
        	xClick = (int)event.getLocation().x;
        	yClick = (int)event.getLocation().y;
        	if (xClick <= Sizes.LEFT_MARGIN || xClick >= Sizes.LEFT_MARGIN + getPixelBoardWidth()) {
        		return -2;
        	}
        	if (yClick <= Sizes.TOP_MARGIN || yClick >= Sizes.TOP_MARGIN + getPixelBoardLength()) {
        		return -2;
        	}
        	xClick = (xClick - Sizes.LEFT_MARGIN) / Sizes.STEP;
        	yClick = (yClick - Sizes.TOP_MARGIN) / Sizes.STEP;
	        return -1;
        }
        return -2;
	}
	
	private Font loadFont(Graphics2D graphics) {
		Font font;
		try {
			FileInputStream stream = new FileInputStream("font/ethnocentric rg.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, Sizes.FONT_SIZE);
			stream.close();
			graphics.setFont(font);
		} catch (FontFormatException | IOException e) {
			System.err.println(e.getMessage());
			System.err.println("Font ethnocentric rg.ttf not found in font directory !");
			font = new Font("arial", Font.PLAIN, Sizes.FONT_SIZE);
		}
		return font;
	}
	
	/**
	 * Draws several messages, represented by an array of Strings. Each string will be drawn on a new line, 
	 * and all the text will be centered in both axes.
	 * @param messages the messages to draw.
	 */
	public void drawMessage(String[] messages) {
		context.renderFrame(graphics -> {
			ScreenInfo info = context.getScreenInfo();
			Font font = loadFont(graphics);
			graphics.setColor(Color.BLACK);
			graphics.fill(new Rectangle2D.Float(0, 0, info.getWidth(), info.getHeight()));
			for (int i = 0; i < messages.length; i++) {
				graphics.setColor(Color.WHITE);
				float x = (info.getWidth() - graphics.getFontMetrics(font).stringWidth(messages[i]))/2;
				float y = (info.getHeight() + (messages.length + 2 * i) * (Sizes.FONT_SIZE * 1.25f))/2;
				graphics.drawString(messages[i], x, y);
			}
		});
	}
	
	/**
	 * Draws the interface of the game, with the name of the game and the current level.
	 * @param level the current level to display.
	 */
	public void drawInterface(int level) {
		context.renderFrame(graphics -> {
			ScreenInfo info = context.getScreenInfo();
			Font font = loadFont(graphics);
			graphics.setColor(Color.BLACK);
			graphics.fill(new Rectangle2D.Float(0, 0, info.getWidth(), info.getHeight()));
			graphics.setColor(Color.WHITE);
			float x = (info.getWidth() - graphics.getFontMetrics(font).stringWidth("WALL-J"))/2;
			float y = (Sizes.TOP_MARGIN - Sizes.FONT_SIZE)/2;
			graphics.drawString("WALL-J", x, y);
			
			String str = "Level " + level;
			x = (info.getWidth() - graphics.getFontMetrics(font).stringWidth(str))/2;
			y = Sizes.TOP_MARGIN + getPixelBoardLength() + (3 * Sizes.FONT_SIZE / 2);
			graphics.drawString(str, x, y);
		});
	}
}
