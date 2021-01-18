/**
 * File: BreakoutConstants.java
 * -------------------
 * This file declares all the constants used in Breakout.java
 */

import java.awt.Color;

public interface BreakoutConstants {
	
	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 600;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	public static final int WIDTH = APPLICATION_WIDTH;
	public static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	public static final int PADDLE_WIDTH = 90;
	public static final int PADDLE_HEIGHT = 15;

	/** Other paddle parameters */
	public static final int PADDLE_WIDTH_HALF = PADDLE_WIDTH / 2;
	public static final int MAX_PADDLE_SPEED = 5;

	/** Offset of the paddle up from the bottom */
	public static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	public static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	public static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	public static final int BRICK_SEP = 4;

	/** Width of a brick */
	public static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	public static final int BRICK_HEIGHT = 10;

	/** Brick color parameters */
	public static final Color[] BRICK_COLORS = new Color[] { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
			Color.CYAN };
	public static final int NBRICK_COLORS = BRICK_COLORS.length;

	/** Offset of the top brick row from the top */
	public static final int BRICK_Y_OFFSET = 70;

	/** Offset of the leftmost brick column from the left */
	public static final double BRICK_X_OFFSET = (WIDTH - NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP) / 2;

	/** Radius of the ball in pixels */
	public static final int BALL_RADIUS = 10;

	/** Ball parameters */
	public static final int BALL_DIAMETER = BALL_RADIUS * 2;

	/** Ball movement parameters */
	public static final double INITIAL_VELOCITY_Y = 1.5;
	public static final double ACCELERATION_Y = 1.02;
	public static final double MAX_VELOCITY_Y = 4;
	public static final int MIN_VELOCITY_X = 2;
	public static final int MAX_VELOCITY_X = 3;

	/** Number of lives */
	public static final int LIVES = 3;

	/** Heart symbol parameters */
	public static final int HEART_HEIGHT = BALL_DIAMETER;
	public static final int HEART_WIDTH = BALL_DIAMETER;
	public static final int HEART_OFFSET_Y = 15;
	public static final int HEART_OFFSET_X = 10;
	public static final int HEART_SEP = 5;

	public static final Color HEART_COLOR = Color.red;
	public static final Color BROKEN_HEART_COLOR = Color.lightGray;

	/** Score board parameters */
	public static final int SCORE_BOARD_OFFSET_X = 10;
	public static final int SCORE_BOARD_OFFSET_Y = 20;
	public static final int SCORE_BOARD_SEP = 15;
	public static final int SCORE_BOARD_FONT_SIZE = 25;

	public static final Color SCORE_BOARD_COLOR = Color.orange;

	/** Score board emblem parameters */
	public static final int EMBLEM_SIZE = HEART_HEIGHT;
	public static final int EMBLEM_SIDES = 6;

	public static final Color EMBLEM_COLOR_1 = Color.orange;
	public static final Color EMBLEM_COLOR_2 = Color.lightGray;

	/** Notification label parameters */
	public static final int BIG_NOTIFICATION_FONT_SIZE = 40;
	public static final int NOTIFICATION_FONT_SIZE = 25;
	public static final int HIGHSCORE_FONT_SIZE = 20;
	public static final int MENU_OFFSET_Y = 200;
	public static final int MENU_OFFSET_X = 35;
	public static final int MENU_SEP = 20;

	public static final int PAUSE_OFFSET_X = WIDTH - 30;
	public static final int PAUSE_OFFSET_Y = HEIGHT;

	public static final Color NOTIFICATION_COLOR = Color.black;
	public static final Color[] HIGHSCORE_COLORS = { new Color(219, 198, 59), new Color(128, 125, 108),
			new Color(171, 64, 2) };

	public static final int MAX_HIGHSCORE_COUNT = 9;

	/** Enumeration for the menu pages */
	public enum MenuPages {

		START_MENU, WINNER_HIGHSCORES, LOSER_HIGHSCORES, SETTINGS, GAME_ON, PAUSE, HIGHSCORE_INPUT, END_MENU, EXIT

	}

	/** Timer (lower left of screen) parameters */
	public static final int TIMER_OFFSET_X = 10;
	public static final int TIMER_OFFSET_Y = 10;
	public static final int TIMER_FONT_SIZE = 15;

	public static final Color TIMER_COLOR = Color.pink;

	/** Pause time between ball movements */
	public static final int PAUSE_TIME = 5;

	/** Time interval before the ball can rebound on the paddle. */
	public static final int PADDLE_REBOUND_DELAY = 150;

	/** Time interval before the ball can rebound on the runaway brick */
	public static final int RUNAWAY_BRICK_REBOUND_DELAY = 150;

	/** Part of the paddle that triggers corner rebound */
	public static final double PADDLE_EDGE_WIDTH = PADDLE_WIDTH * 0.15;

	/** Parameters for the final bricks movements */
	public static final double RUNAWAY_BRICK_REVERSE_CHANCE = 0.2;

	/** Angle width of the side of the ball considered as one hit point */
	public static final int INTERVAL_SIZE = 90;

	/**
	 * Amount of equally spaced points on the circumference of the ball that are
	 * checked
	 */
	public static final int CHECK_POINTS = 36;

	/** Maximum input size for saving a new highscore */
	public static final int MAX_INPUT_SIZE = 6;

}