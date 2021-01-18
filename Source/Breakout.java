
/*
 * File: Breakout.java
 * -------------------
 * This file implements the game of Breakout.
 * 
 * None of the art (background, font) belongs to me. 
 * 
 * Some Ideas about the possible features were supplied by Avtandil kakhishvili.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Breakout extends GraphicsProgram implements BreakoutConstants {

	public static void main() {
		new Breakout().start();
	}

	public void init() {

		loadResources();
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
	}

	/** Runs the Breakout program. */
	public void run() {

		while (menuPage != MenuPages.EXIT) {

			setupGame();
			gameOver(startGame());
			clearGame();
		}
	}

	/**
	 * method: loadResources();
	 * 
	 * This method is for loading audio files, fonts etc.
	 */
	private void loadResources() {

		try {

			String fontName = "/fonts/main font.TTF";
			InputStream is = Breakout.class.getResourceAsStream(fontName);
			gameFontBase = Font.createFont(Font.TRUETYPE_FONT, is);

			highscoresTime = new File("./files/time highscores.txt");
			highscores = new File("./files/highscores.txt");

		} catch (FontFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		checkDataFile(highscores);
		checkDataFile(highscoresTime);

		addKeyListeners(keyListener);
	}

	/**
	 * method: checkDataFile();
	 * 
	 * A small check for the highscore file formats, not very effective but enough
	 * to make sure the rest of the program works.
	 * 
	 * @param file : The file being checked.
	 */
	private void checkDataFile(File file) {

		List<String> lines = readDataFile(file);

		for (String line : lines) {

			if (line.indexOf('|') < 0 || line.indexOf('-') < 0) {

				clearDataFile(file);
				break;

			} else {

				try {
					Integer.parseInt(line.substring(line.indexOf('|') + 2));
				} catch (NumberFormatException | NullPointerException e) {

					clearDataFile(file);
					break;
				}
			}
		}
	}

	/**
	 * method: setupGame();
	 * 
	 * Creates and adds all the game elements to the window.
	 */
	private void setupGame() {

		setBackground();

		menu();

		setPaddle();
		setBricks();
		setLives();
		setBall();
		setScoreBoard();
		setTimer();
		setPause(false);

		myRuntime.gc();
	}

	/**
	 * method: setBackground();
	 * 
	 * Sets the background.
	 */
	private void setBackground() {

		background = new GImage("./images/background/breakout background.jpg");

		background.setSize(BreakoutConstants.WIDTH, BreakoutConstants.HEIGHT);

		add(background, 0, 0);

		background.addMouseMotionListener(mouseMotionListener);
	}

	/**
	 * method: setBricks();
	 * 
	 * Creates the rows of bricks for the game.
	 */
	private void setBricks() {

		bricks = new Brick[NBRICK_ROWS * NBRICKS_PER_ROW];

		maxScore = 0;
		runawayBrick = null;
		Color currColor = null;
		Color additionalRowColor = BRICK_COLORS[NBRICK_COLORS - 1];

		final double brickDistanceY = BRICK_HEIGHT + BRICK_SEP;

		double y = 0;

		/* Amount of consecutive rows of the same color. Can't be zero. */
		int sameColorRows = NBRICK_ROWS / NBRICK_COLORS;
		sameColorRows += sameColorRows > 0 ? 0 : 1;

		int mainRows = sameColorRows * NBRICK_COLORS;

		/*
		 * The brick rows are filled with this for loop.
		 */
		for (int i = 0; i < NBRICK_ROWS; i++) {

			if (i < mainRows) currColor = BRICK_COLORS[i / sameColorRows];
			else currColor = additionalRowColor;

			y = BRICK_Y_OFFSET + i * brickDistanceY;
			fillBrickRow(y, currColor, i);
		}

	}

	/**
	 * method: fillBrickRow();
	 * 
	 * Creates a row of Brick objects of the given color at the given y coordinate.
	 * 
	 * @param y         : y coordinate of the bricks
	 * @param color     : color of the brick
	 * @param rowNumber : used for storing references to the brick objects
	 */
	private void fillBrickRow(double y, Color color, int rowNumber) {

		final double brickDistanceX = BRICK_WIDTH + BRICK_SEP;
		final int colorIndex = getBrickColorIndex(color);
		final int colorScoreMultiplier = 2 * NBRICK_COLORS - colorIndex;

		double x = 0;

		Brick brick = null;

		for (int i = 0; i < NBRICKS_PER_ROW; i++) {

			x = BRICK_X_OFFSET + i * brickDistanceX;

			brick = new Brick(BRICK_WIDTH, BRICK_HEIGHT, colorIndex);

			Thread fidget = new Thread(brick);
			fidget.start();

			add(brick, x, y);

			bricks[i + rowNumber * NBRICKS_PER_ROW] = brick;

			maxScore += colorScoreMultiplier;
		}
	}

	/**
	 * method: setPaddle();
	 * 
	 * Creates and places the paddle as well as the area on which the cursor
	 * movement is detected.
	 */
	private void setPaddle() {

		paddle = new Paddle(PADDLE_WIDTH, PADDLE_HEIGHT);

		add(paddle, (BreakoutConstants.WIDTH - PADDLE_WIDTH) / 2,
				BreakoutConstants.HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);

		Thread levitation = new Thread(paddle);
		levitation.start();
	}

	/**
	 * method: setBall();
	 * 
	 * Creates and places the ball.
	 */
	private void setBall() {

		ball = new Ball(BALL_DIAMETER);
		placeBall();
	}

	/**
	 * method: placeBall();
	 * 
	 * Places the ball in the center of the screen. Called at the start and every
	 * time the players loses the ball. Waits for player to click before proceeding.
	 */
	private void placeBall() {

		add(ball, BreakoutConstants.WIDTH / 2, BreakoutConstants.HEIGHT / 2);

		Thread phase = new Thread(ball);
		phase.start();

		ball.sendToBack();
		background.sendToBack();

		velocityY = INITIAL_VELOCITY_Y;

		velocityX = rando.nextDouble(MIN_VELOCITY_X, MAX_VELOCITY_X) * (rando.nextBoolean() ? 1 : -1);

		long delayStartPoint = System.currentTimeMillis();

		waitForClick();

		gameTimer += getTimeElapsed(delayStartPoint);
		paddleTimer = System.currentTimeMillis();
	}

	/**
	 * method: setLives();
	 * 
	 * Sets the number of lives the user has, also generates the heart symbols.
	 */
	private void setLives() {

		livesLeft = LIVES;

		int heartDistance = HEART_WIDTH + HEART_SEP;
		int x = 0;
		int y = HEART_OFFSET_Y;

		for (int i = 0; i < LIVES; i++) {

			hearts[i] = new Heart(HEART_WIDTH, HEART_HEIGHT, HEART_COLOR);

			x = HEART_OFFSET_X + i * heartDistance;
			add(hearts[i], x, y);
		}
	}

	/**
	 * method: decrementLives();
	 * 
	 * Decreases the number of lives left and also causes a heart-crack animation
	 * when called.
	 */
	private void decrementLives() {

		ball.exit();

		livesLeft--;

		hearts[livesLeft].setColor(BROKEN_HEART_COLOR);
		hearts[livesLeft].setCracked(true);

		Thread heartBreak = new Thread(hearts[livesLeft]);
		heartBreak.start();
	}

	/**
	 * method: setScoreBoard();
	 * 
	 * Creates the scoreboard and adds it to the screen.
	 */
	private void setScoreBoard() {

		score = 0;

		int totalDigits = ("" + maxScore).length();

		char[] blankScore = new char[totalDigits];
		Arrays.fill(blankScore, '0');

		scoreBoard = new GLabel(new String(blankScore));

		scoreBoard.setFont(gameFontBase.deriveFont(Font.PLAIN, SCORE_BOARD_FONT_SIZE));
		scoreBoard.setColor(SCORE_BOARD_COLOR);

		scoreEmblem = new ScoreEmblem(EMBLEM_SIZE, EMBLEM_SIZE, EMBLEM_SIDES, EMBLEM_COLOR_1, EMBLEM_COLOR_2);

		placeScoreBoard();
	}

	/**
	 * method: incrementScore();
	 * 
	 * Changes the value displayed on the scoreboard.
	 */
	private void incrementScore() {

		removeBrick((Brick) colObject);

		int remainingZeros = scoreBoard.getLabel().length() - ("" + score).length();

		/* This fills the scoreboard with leading zeroes. */
		char[] zeroes = new char[remainingZeros];
		Arrays.fill(zeroes, '0');

		scoreBoard.setLabel(new String(zeroes) + score);

		/*
		 * This is to make sure the label flipping animation isn't called before
		 * finishing the previous one.
		 */
		if (scoreEmblem.getThreadState()) {

			Thread flipEmblem = new Thread(scoreEmblem);
			flipEmblem.start();
		}

		scoreEmblem.setColor(EMBLEM_COLOR_1, BRICK_COLORS[((Brick) colObject).getColorIndex()]);

		placeScoreBoard();
		checkForLastBrick();
	}

	/**
	 * method: removeBrick();
	 * 
	 * Notifies the Brick object that it has been destroyed.
	 * 
	 * @param brick : The Brick that is to be removed
	 */
	private void removeBrick(Brick brick) {

		score += brick.getScoreMultiplier();

		brick.exit();

		if (brick == runawayBrick) remove(brick);
		else {

			brick.destroy();

			Thread fade = new Thread(brick);
			fade.start();
		}
	}

	/**
	 * method: checkForLastBrick();
	 * 
	 * The runawayBrick feature is activated here. Brick scores are assigned in the
	 * range [NBRICK_COLORS + 1; 2 * NBRICK_COLORS]. When there are two bricks
	 * remaining the score can at max be (maxScore - 2 * (NBRICK_COLORS + 1).
	 */
	private void checkForLastBrick() {

		if (score > maxScore - 2 * (NBRICK_COLORS + 1) && runawayBrick == null) {

			if ((runawayBrick = getRunawayBrick()) != null) {

				runawayBrick.exit();
				runawayBrick.changeToRunaway();
			}
		}
	}

	/**
	 * method: getBrickColorIndex();
	 * 
	 * Returns the index of where the color is in the BRICK_COLORS array.
	 * 
	 * @param brickColor : The color of the brick
	 * @return index of the color
	 */
	private int getBrickColorIndex(Color brickColor) {

		for (int i = 0; i < NBRICK_COLORS; i++) {
			
			if (brickColor.equals(BRICK_COLORS[i])) return i;			
		}
		return -1;
	}

	/**
	 * method: placeScoreBoard();
	 * 
	 * Adds the scoreboard to the screen.
	 */
	private void placeScoreBoard() {

		double x = 0;
		double y = 0;

		x = BreakoutConstants.WIDTH - SCORE_BOARD_OFFSET_X - scoreBoard.getWidth();
		y = SCORE_BOARD_OFFSET_Y + scoreBoard.getAscent() / 2;

		add(scoreBoard, x, y);

		add(scoreEmblem, x - SCORE_BOARD_SEP - EMBLEM_SIZE, y - EMBLEM_SIZE);
	}

	/**
	 * method: setTimer();
	 * 
	 * Adds a timer to the lower left part of the screen. Keeps track of how long
	 * the game has been going.
	 */
	private void setTimer() {

		gameTimer = System.currentTimeMillis();

		long timeElapsed = getTimeElapsed(gameTimer);

		timer = new GLabel(timeElapsedToString(timeElapsed));

		timer.setFont(gameFontBase.deriveFont(Font.BOLD, TIMER_FONT_SIZE));
		timer.setColor(TIMER_COLOR);

		add(timer, TIMER_OFFSET_X, BreakoutConstants.HEIGHT - TIMER_OFFSET_Y);
	}

	/**
	 * method: updateTimer();
	 * 
	 * Updates the game timer.
	 */
	private void updateTimer() {

		long timeElapsed = getTimeElapsed(gameTimer);
		timer.setLabel(timeElapsedToString(timeElapsed));
	}

	/**
	 * method: getTimeElapsed();
	 * 
	 * Returns the difference in time between the current point and some reference
	 * point.
	 * 
	 * @param startPoint : The reference point in time (milliseconds)
	 * @return a long type value corresponding to the difference in time
	 */
	private long getTimeElapsed(long startPoint) {

		return System.currentTimeMillis() - startPoint;
	}

	/**
	 * method: timeElapsedToString();
	 * 
	 * Returns a string representing time passed from reference point.
	 * 
	 * @param timeInMillis : The elapsed time in milliseconds
	 * @return timeElapsed : Elapsed time in hh:mm:ss format as a string
	 */
	private String timeElapsedToString(long timeInMillis) {

		long time = timeInMillis / (int) 1e3;

		String seconds = "" + time % 60;
		seconds = (seconds.length() > 1 ? "" : "0") + seconds;

		time /= 60;

		String minutes = "" + time % 60;
		minutes = (minutes.length() > 1 ? "" : "0") + minutes;

		time /= 60;

		String hours = "" + time % 60;
		hours = (hours.length() > 1 ? "" : "0") + hours;

		String timeElapsed = hours + "-" + minutes + "-" + seconds;

		return timeElapsed;
	}

	/**
	 * method: startGame();
	 * 
	 * Starts the game, keeps track of the player's lives and score.
	 * 
	 * @return boolean representing the outcome of the game : true -> win
	 */
	private boolean startGame() {

		gameTimer = System.currentTimeMillis();

		while (livesLeft > 0) {

			while (menuPage == MenuPages.PAUSE) pause(PAUSE_TIME);

			updateTimer();

			moveBall();

			pause(PAUSE_TIME);

			if (score == maxScore) {

				menuPage = MenuPages.END_MENU;
				return true;
			}

			if (runawayBrick != null) runawayBrick();
		}
		
		menuPage = MenuPages.END_MENU;
		
		return false;
	}

	/**
	 * method: moveBall();
	 * 
	 * Moves the ball and checks if it has collided with anything.
	 */
	private void moveBall() {

		ball.move(velocityX, velocityY);

		double currBallX = ball.getX();
		double currBallY = ball.getY();

		checkBorderCol(currBallX, currBallY);

		checkObjCol(currBallX, currBallY);
	}

	/**
	 * method: checkBorderCol()
	 * 
	 * Checks if any border collisions have occurred and rebounds the ball
	 * accordingly (or decrements lives if the crossed border is the bottom one).
	 * 
	 * @param x, y : current position of the ball
	 */
	private void checkBorderCol(double x, double y) {

		/* The velocity is checked to make sure the ball doesn't get stuck in a wall. */
		if ((x <= 0 && velocityX < 0) || (x + BALL_DIAMETER >= BreakoutConstants.WIDTH && velocityX > 0)) {

			velocityX = -velocityX;

		}
		
		if (y <= 0) velocityY = -velocityY;
		else if (y >= BreakoutConstants.HEIGHT) {

			decrementLives();

			if (livesLeft > 0) placeBall();
		}
	}

	/**
	 * method: checkObjCol();
	 * 
	 * Checks for collisions with objects. If collision was with a brick, it
	 * increases the score and removes the brick. The rebound happens differently
	 * for paddle/bricks.
	 * 
	 * @param x, y : current position of the ball
	 */
	private void checkObjCol(double x, double y) {

		/*
		 * checkCollider() returns the angle of collision. Negative value = no
		 * collision.
		 */
		int angleOfCollision = checkCollider(x, y);

		if (angleOfCollision >= 0) {

			if (colObject == paddle) reboundPaddle(x, y);
			else if (colObject == runawayBrick) reboundRunawayBrick(angleOfCollision);
			else {

				reboundBrick(angleOfCollision);
				incrementScore();
			}
		}
	}

	/**
	 * method: checkCollider();
	 * 
	 * Sets colObject to reference whatever the ball collided with (if at all). Also
	 * returns the angle at which that object touched the balls circumference
	 * (approximate).
	 * 
	 * @param x, y : current position of the ball
	 * @return collision angle (approximated) or -1 if no collision
	 */
	private int checkCollider(double x, double y) {

		/* Holds the angle value (rad) at which it is checking. */
		double checkPoint = 0;
		double finalCheckPoint = -1;
		/* Angle interval size at which the checks are made. */
		double step = 2 * Math.PI / CHECK_POINTS;
		/* radius of the circumference around which the checks are made. */
		double checkRadius = 1.1 * BALL_RADIUS;

		GObject tempObject;

		x += BALL_RADIUS;
		y += BALL_RADIUS;

		/*
		 * Collision is checked using this for loop. Once one is detected, the object
		 * reference and the angle is saved in colObject and finalCheckPoint
		 * accordingly, the checkRadius is decremented and everything else is reset so
		 * the checking starts again, until it can no longer detect collision. This is
		 * because the ball moves several pixels at a time and at higher speeds it will
		 * get lodged inside objects before anything is checked. By checking this way
		 * the accuracy is higher. The previous to the final values are saved and used
		 * afterwards.
		 */

		for (int i = 0; i < CHECK_POINTS; i++) {

			tempObject = getElementAt(x + checkRadius * Math.cos(checkPoint), y - checkRadius * Math.sin(checkPoint));

			/* Since the hit angle isn't important when hitting the paddle. */
			if (tempObject == paddle) {

				colObject = paddle;
				return 0;

			} else if (tempObject instanceof Brick) {

				if (((Brick) tempObject).isHittable()) {

					colObject = tempObject;
					finalCheckPoint = checkPoint;

					i = 0;
					checkPoint = -step;
					checkRadius--;
				}
			}
			checkPoint += step;
		}

		/* No collisions if this variable still has the initial negative value. */
		if (finalCheckPoint >= 0) return findInterval((int) Math.toDegrees(finalCheckPoint));
		else return -1;
	}

	/**
	 * method: findInterval();
	 * 
	 * Returns the multiple of INTERVAL_SIZE that is closest to the input.
	 * 
	 * @param angle : The angle that's being approximated
	 * @return The approximated angle as an int
	 */
	private int findInterval(double angle) {

		angle += INTERVAL_SIZE / 2;

		int interval = (int) (angle / INTERVAL_SIZE) * INTERVAL_SIZE;

		return interval;
	}

	/**
	 * method: reboundPaddle();
	 * 
	 * This method bounces the ball, but it's only called if the paddle was hit.
	 * 
	 * @param x, y : current position of the ball
	 */
	private void reboundPaddle(double x, double y) {

		if (y + BALL_RADIUS < BreakoutConstants.HEIGHT - PADDLE_Y_OFFSET) {

			/* This is to keep the ball from getting stuck inside the paddle. */
			if (System.currentTimeMillis() - paddleTimer > PADDLE_REBOUND_DELAY) {

				velocityY *= -1;

				fancyRebound(x, y);

				paddleTimer = System.currentTimeMillis();

				paddle.exit();

				paddle.deflectBall();

				Thread bounce = new Thread(paddle);
				bounce.start();

			}

		} else velocityX *= (paddleSpeed * velocityX > 0) ? 1 : -1;
	}

	/**
	 * method: fancyRebound();
	 * 
	 * This method changes the ball's horizontal speed, based on where it hit the
	 * paddle and how fast the paddle was moving.
	 * 
	 * @param x, y : current position of the ball
	 */
	private void fancyRebound(double x, double y) {

		double paddleX = paddle.getX();

		/* Horizontal distances between the opposite sides of the object. */
		double leftMargin = x + BALL_DIAMETER - paddleX;
		double rightMargin = paddleX + PADDLE_WIDTH - x;

		/* Horizontal direction of the ball (positive/negative). */
		int direction = velocityX > 0 ? 1 : -1;

		if (direction > 0 && leftMargin <= PADDLE_EDGE_WIDTH) cornerRebound(leftMargin, direction);
		else if (direction < 0 && rightMargin <= PADDLE_EDGE_WIDTH) cornerRebound(rightMargin, direction);
		else speedRebound();
	}

	/**
	 * method: cornerRebound();
	 *
	 * This method changes the horizontal speed based on where it hit the paddle's
	 * corner.
	 * 
	 * @param margin    : |(x of ball) - (x of paddle)|
	 * @param direction : direction of the ball's horizontal velocity
	 */
	private void cornerRebound(double margin, int direction) {

		double newVelocity = MAX_VELOCITY_X * (PADDLE_EDGE_WIDTH - margin) / PADDLE_EDGE_WIDTH;

		if (newVelocity > MIN_VELOCITY_X) velocityX = newVelocity * -direction;
		else velocityX = MIN_VELOCITY_X * -direction;
	}

	/**
	 * method: speedRebound()
	 * 
	 * Changes the balls horizontal velocity according to what the paddle's speed
	 * was when it hit.
	 */
	private void speedRebound() {

		double reboundedBallSpeed = MAX_VELOCITY_X * paddleSpeed / MAX_PADDLE_SPEED;

		if (Math.abs(reboundedBallSpeed) > MIN_VELOCITY_X) {

			if (Math.abs(paddleSpeed) >= MAX_PADDLE_SPEED) velocityX = MAX_VELOCITY_X * (paddleSpeed > 0 ? 1 : -1);
			else velocityX = reboundedBallSpeed;
		}
	}

	/**
	 * method: reboundBrick();
	 * 
	 * Changes the balls velocity according to what side of the ball was hit.
	 * Generally it reverses the vertical velocity if the collision was from the
	 * top/bottom, otherwise it reverses the horizontal velocity.
	 * 
	 * @param angleOfCollision (approximate)
	 */
	private void reboundBrick(int angleOfCollision) {

		if (angleOfCollision % 180 == 90) velocityY *= -1;
		else velocityX *= -1;

		if (Math.abs(velocityY) < MAX_VELOCITY_Y) velocityY *= ACCELERATION_Y;
	}

	/**
	 * method: reboundRunawayBrick();
	 * 
	 * A version of the reboundBrick() for the final brick, which also triggers the
	 * animation thread and keeps track of the brick's health points.
	 * 
	 * @param angleOfCollision : angle at which the ball hit the brick
	 */
	private void reboundRunawayBrick(int angleOfCollision) {

		if (getTimeElapsed(runawayBrickTimer) > RUNAWAY_BRICK_REBOUND_DELAY) {

			if (runawayBrick.getThreadState()) {

				runawayBrick.setDeflectDirection(angleOfCollision);

				Thread deflect = new Thread(runawayBrick);
				deflect.start();

				if (runawayBrick.getLives() == 0) incrementScore();
			}

			reboundBrick(angleOfCollision);

			runawayBrickTimer = System.currentTimeMillis();
		}
	}

	/**
	 * method: clearGame();
	 * 
	 * Clears all objects from the screen.
	 */
	private void clearGame() {

		removeAll();
		endAllThreads();
	}

	/**
	 * method: endAllThreads();
	 * 
	 * Tells all the remaining threads to finish executing.
	 */
	private void endAllThreads() {

		paddle.exit();
		for (Brick brick : bricks) brick.exit();
	}

	/**
	 * method: gameOver();
	 * 
	 * Displays the appropriate message and records the score.
	 * 
	 * @param success : the outcome of the game. true -> win
	 */
	private void gameOver(boolean success) {

		String messageText = "";
		String resultText = "";
		long result = 0;

		if (success) {

			messageText = "YOU WON!\n\nYOUR TIME:\n";
			result = getTimeElapsed(gameTimer);
			resultText = timeElapsedToString(result);

		} else {

			messageText = "YOU LOST!\nYOUR SCORE:\n";
			result = score;
			resultText = "" + result;
		}

		FancyFont messageLabel = addMessage(BreakoutConstants.WIDTH / 2, BreakoutConstants.HEIGHT / 2, messageText,
				BIG_NOTIFICATION_FONT_SIZE, NOTIFICATION_COLOR, false);
		FancyFont resultLabel = addMessage(BreakoutConstants.WIDTH / 2,
				BreakoutConstants.HEIGHT / 2 + messageLabel.getHeight(), resultText, BIG_NOTIFICATION_FONT_SIZE,
				NOTIFICATION_COLOR, false);

		messageReact(resultLabel, true);

		waitForClick();

		resultLabel.exit();

		remove(messageLabel);
		remove(resultLabel);

		addHighscore(result, success ? highscoresTime : highscores);
	}

	/**
	 * method: menu();
	 * 
	 * Displays the game's menu.
	 */
	private void menu() {

		setStartMenuLabels();

		while (menuPage != MenuPages.GAME_ON) {

			if (menuPage == MenuPages.START_MENU) {

				arrowsSetVisible(false);

				startGame.setVisible(true);
				highscore.setVisible(true);
			} else {

				arrowsSetVisible(true);

				startGame.setVisible(false);
				highscore.setVisible(false);

				showScores();
			}
		}

		remove(startGame);
		remove(highscore);
		remove(next);
		remove(previous);
	}

	/**
	 * method: setStartMenuLabels();
	 * 
	 * Adds all the labels in the start menu to the screen.
	 */
	private void setStartMenuLabels() {

		String startGameLabel = "";

		if (menuPage == MenuPages.START_MENU) startGameLabel = "Start Game";
		else if (menuPage == MenuPages.END_MENU) startGameLabel = "Try Again";

		startGame = addMessage(BreakoutConstants.WIDTH / 2, MENU_OFFSET_Y, startGameLabel, BIG_NOTIFICATION_FONT_SIZE,
				NOTIFICATION_COLOR, true);

		highscore = addMessage(BreakoutConstants.WIDTH / 2, MENU_OFFSET_Y + startGame.getHeight() + MENU_SEP,
				"Highscores", BIG_NOTIFICATION_FONT_SIZE, NOTIFICATION_COLOR, true);

		next = addMessage(BreakoutConstants.WIDTH - MENU_OFFSET_X, BreakoutConstants.HEIGHT / 2, ">",
				BIG_NOTIFICATION_FONT_SIZE, NOTIFICATION_COLOR, true);
		previous = addMessage(MENU_OFFSET_X, BreakoutConstants.HEIGHT / 2, "<", BIG_NOTIFICATION_FONT_SIZE,
				NOTIFICATION_COLOR, true);

		menuPage = MenuPages.START_MENU;
	}

	/**
	 * method: toMenuPage();
	 * 
	 * Sets menuPage to whatever was clicked.
	 * 
	 * @param clickSource : a reference to a FancyFont object that was clicked.
	 */
	private void toMenuPage(FancyFont clickSource) {

		switch (menuPage) {

		case END_MENU:
		case START_MENU:

			if (clickSource == startGame) menuPage = MenuPages.GAME_ON;
			else if (clickSource == highscore) {

				menuPage = MenuPages.WINNER_HIGHSCORES;
				switchPage = true;
			}
			break;

		case WINNER_HIGHSCORES:

			if (clickSource == next) {

				menuPage = MenuPages.LOSER_HIGHSCORES;
				switchPage = true;
				
			} else if (clickSource == previous) {

				menuPage = MenuPages.START_MENU;
				switchPage = true;
				
			} else if (clickSource == clearScores) {

				clear = true;
				switchPage = true;
			}
			break;

		case LOSER_HIGHSCORES:

			if (clickSource == previous) {

				menuPage = MenuPages.WINNER_HIGHSCORES;
				switchPage = true;
				
			} else if (clickSource == clearScores) {

				switchPage = true;
				clear = true;
			}
			break;

		case GAME_ON:

			if (clickSource == pause) {

				menuPage = MenuPages.PAUSE;
				setPause(true);
			}
			break;

		case PAUSE:

			if (clickSource == pause) {

				menuPage = MenuPages.GAME_ON;
				setPause(false);
			}
			break;

		default: System.out.println("Looks like you've got a bug.");
		}
	}

	/**
	 * method: addHighscore();
	 * 
	 * Records the new highscore to the specified highscore text file.
	 * 
	 * @param data : The score that's being added
	 * @param file : The file to which the score's being added
	 */
	private void addHighscore(long data, File file) {

		List<String> lines = readDataFile(file);

		String userName = askForInput("Enter Your\nName: ");

		boolean scoreAdded = addNewScore(data, file, lines, userName);

		if (!scoreAdded) appendNewScore(data, file, lines, userName);

		writeToDataFile(file, lines.subList(0, Math.min(lines.size(), MAX_HIGHSCORE_COUNT)));
	}

	/**
	 * method: addNewScore();
	 * 
	 * Tries to add the new score in between already existing elements of the list.
	 * returns false if unsuccessful.
	 * 
	 * @param data     : The data that's being added
	 * @param file     : The file the data's being added to
	 * @param lines    : The List<String> containing the data file's lines
	 * @param userName : The user's chosen alias for the given score
	 * @return scoreAdded : true -> score was added
	 */
	private boolean addNewScore(long data, File file, List<String> lines, String userName) {

		int length = lines.size();
		boolean scoreAdded = false;

		for (int i = 0; i < length; i++) {

			if (scoreAdded) lines.set(i, (i + 1) + lines.get(i).substring(1));

			else if (file == highscores && data > parseScore(lines.get(i))) {

				lines.add(i, (i + 1) + ". " + userName + " - " + data + " | " + data);
				scoreAdded = true;
				length++;
				
			} else if (file == highscoresTime && data < parseScore(lines.get(i))) {

				lines.add(i, (i + 1) + ". " + userName + " - " + timeElapsedToString(data) + " | " + data);
				scoreAdded = true;
				length++;
			}
		}

		return scoreAdded;
	}

	/**
	 * method: parseScore();
	 * 
	 * Parses the score from a line of one of the highscore files.
	 * 
	 * @param line : The line from the highscore file
	 * 
	 * @return an int representing the score;
	 */
	private int parseScore(String line) {

		String scoreSubstring = "";

		scoreSubstring = line.substring(line.indexOf('|') + 2);

		return Integer.parseInt(scoreSubstring);
	}

	/**
	 * method: askForInput();
	 * 
	 * Asks the user for an input.
	 * 
	 * @param prompt : The message displayed to the user
	 * @return The user's input as a string
	 */
	private String askForInput(String prompt) {

		buffer = "";

		String input = "";

		FancyFont inputLabel = null;
		FancyFont promptLabel = addMessage(BreakoutConstants.WIDTH / 2, MENU_OFFSET_Y, prompt,
				BIG_NOTIFICATION_FONT_SIZE, NOTIFICATION_COLOR, false);

		menuPage = MenuPages.HIGHSCORE_INPUT;

		double inputLabelX = BreakoutConstants.WIDTH / 2;
		double inputLabelY = MENU_OFFSET_Y + promptLabel.getHeight();

		while (menuPage == MenuPages.HIGHSCORE_INPUT) {

			inputLabel = addMessage(inputLabelX, inputLabelY, buffer, NOTIFICATION_FONT_SIZE, NOTIFICATION_COLOR,
					false);

			pause(PAUSE_TIME);

			remove(inputLabel);
		}

		input = buffer;
		remove(promptLabel);

		return input;
	}

	/**
	 * method: toBuffer();
	 * 
	 * Adds the corresponding character to the buffer string.
	 * 
	 * @param keyCode : The code of the key pressed
	 */
	private void toBuffer(int keyCode) {

		if (keyCode == '\b' && !buffer.equals("")) buffer = buffer.substring(0, buffer.length() - 1);
		else if (keyCode >= (int) ' ' && keyCode <= 126 && buffer.length() < MAX_INPUT_SIZE) buffer += (char) keyCode;
	}

	/**
	 * method: appendNewScore();
	 * 
	 * Adds the new score to the end of the list.
	 * 
	 * @param data     : The data that's being added
	 * @param file     : The file the data's being added to
	 * @param lines    : The List<String> containing the data file's lines
	 * @param userName : The user's chosen alias for the given score
	 */
	private void appendNewScore(long data, File file, List<String> lines, String userName) {

		int length = lines.size();

		if (length < MAX_HIGHSCORE_COUNT) {

			if (file == highscores) lines.add((length + 1) + ". " + userName + " - " + data + " | " + data);
			else lines.add((length + 1) + ". " + userName + " - " + timeElapsedToString(data) + " | " + data);
		}
	}

	/**
	 * method: showScores();
	 * 
	 * Displays the highscores to the screen.
	 */
	private void showScores() {

		switchPage = false;

		boolean scoreType = (menuPage == MenuPages.WINNER_HIGHSCORES);

		String descriptionText = (scoreType ? "Winner's" : "Loser's") + "\nScore Board";

		FancyFont description = addMessage(BreakoutConstants.WIDTH / 2, MENU_OFFSET_Y / 2, descriptionText,
				BIG_NOTIFICATION_FONT_SIZE, Color.orange, false);

		clearScores = addMessage(PAUSE_OFFSET_X, PAUSE_OFFSET_Y, "Clear   ", NOTIFICATION_FONT_SIZE * 3 / 4,
				NOTIFICATION_COLOR, true);

		FancyFont[] scoreLabels = addScoreLabels(scoreType);

		while (!switchPage) {

			waitForClick();

			if (clear) {

				clearDataFile(scoreType ? highscoresTime : highscores);
				clear = false;
			}
		}
		
		remove(description);
		remove(clearScores);
		removeScores(scoreLabels);
	}

	/**
	 * method: addScoreLabels();
	 * 
	 * Reads the appropriate score
	 * 
	 * @param scoreType : boolean value. true -> Time highscores : false ->
	 *                  highscores
	 * @return A FancyFont array containing the references to the score labels
	 */
	private FancyFont[] addScoreLabels(boolean scoreType) {

		List<String> lines = readDataFile(scoreType ? highscoresTime : highscores);
		int length = lines.size();

		FancyFont[] scoreLabels = new FancyFont[length];

		double center = BreakoutConstants.WIDTH / 2;
		double offsetY = (BreakoutConstants.HEIGHT - (length * NOTIFICATION_FONT_SIZE)) / 2;

		String currentLine = "";
		Color currColor = null;

		for (int i = 0; i < length; i++) {

			currentLine = lines.get(i).substring(0, lines.get(i).indexOf('|'));

			currColor = i < 3 ? HIGHSCORE_COLORS[i] : NOTIFICATION_COLOR;

			scoreLabels[i] = addMessage(center, offsetY + (HIGHSCORE_FONT_SIZE + MENU_SEP) * i, currentLine,
					HIGHSCORE_FONT_SIZE, currColor, true);
		}

		return scoreLabels;
	}

	/**
	 * method: removeScores();
	 * 
	 * Removes all the score labels from the screen.
	 * 
	 * @param scores : An array containing the references to the FancyFont objects
	 */
	private void removeScores(FancyFont[] scores) {

		for (FancyFont score : scores) remove(score);
	}

	/**
	 * method: readDataFile();
	 * 
	 * Reads all the lines from a file.
	 * 
	 * @param file : The file that's being read
	 * @return a List with all the lines from the file
	 */
	private List<String> readDataFile(File file) {

		List<String> lines = null;

		try {

			lines = Files.readAllLines(file.toPath());

		} catch (IOException e) {

			e.printStackTrace();
			System.out.println("Error reading file " + file.toString());
		}

		return lines;
	}

	/**
	 * method: writeToDataFile();
	 * 
	 * Writes the elements of a sting list as lines to a text file.
	 * 
	 * @param file  : The file that's being overwritten
	 * @param lines : The string List containing the lines of text
	 */
	private void writeToDataFile(File file, List<String> lines) {

		try {

			Files.write(file.toPath(), lines);

		} catch (IOException e) {

			e.printStackTrace();
			System.out.println("Error writing to file " + file.toString());
		}
	}

	/**
	 * method: clearDataFile();
	 * 
	 * Clears the given data file.
	 * 
	 * @param file : The data file that is to be cleared
	 */
	private void clearDataFile(File file) {

		try {

			Files.write(file.toPath(), new ArrayList<String>(0));

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * method: arrowsSetVisible();
	 * 
	 * Set's the menu navigation arrows visibility.
	 * 
	 * @param visible : true -> arrows are visible
	 */
	private void arrowsSetVisible(boolean visible) {

		if (visible) {

			next.setVisible(true);
			previous.setVisible(true);

		} else {

			next.setVisible(false);
			previous.setVisible(false);
		}
	}

	/**
	 * method: setPause();
	 * 
	 * Adds the pause button to the screen.
	 * 
	 * @param gamePaused : The state of the game
	 */
	private void setPause(boolean gamePaused) {

		String pauseText = "";
		boolean pauseAdded = pause != null;

		if (gamePaused) {

			remove(pause);
			pauseText = ">>";

		} else {

			if (pauseAdded) remove(pause);
			pauseText = "II";
		}

		pause = addMessage(PAUSE_OFFSET_X, PAUSE_OFFSET_Y, pauseText, NOTIFICATION_FONT_SIZE, Color.black, true);
	}

	/**
	 * method: addMessage();
	 * 
	 * Adds a message with the given parameters to the given location on the screen
	 * and returns the reference.
	 * 
	 * @param x           : X coordinate of the message
	 * @param y           : Y coordinate of the message
	 * @param text        : The text that is displayed
	 * @param size        : Font size of the text
	 * @param color       : Base color of the text
	 * @param interactive : Whether it reacts to mouse events or not
	 * @return a reference to a FancyFont object
	 */
	private FancyFont addMessage(double x, double y, String text, int size, Color color, boolean interactive) {

		FancyFont message = new FancyFont(text, gameFontBase, size, color);
		add(message, x, y - message.getHeight() / 2);
		message.sendToFront();

		if (interactive) message.addMouseListener(mouseListener);

		return message;
	}

	/**
	 * method: messageReact();
	 * 
	 * Triggers the message animation.
	 * 
	 * @param message : The FancyFont object to trigger the animation on
	 * @param toState : The state to which the FancyFont object changes
	 */
	private void messageReact(FancyFont message, boolean toState) {

		message.setState(toState);

		Thread changeColor = new Thread(message);
		changeColor.start();

		myRuntime.gc();
	}

	/**
	 * method: getRunawayBrick();
	 * 
	 * Checks the all the locations where the bricks where placed to find the last
	 * remaining brick.
	 * 
	 * @return a reference to a Brick object (The last brick); null if there is no
	 *         such brick.
	 */
	private Brick getRunawayBrick() {

		GObject temp;

		final double brickDistanceX = BRICK_WIDTH + BRICK_SEP;
		final double brickDistanceY = BRICK_HEIGHT + BRICK_SEP;

		double x = 0, y = 0;

		for (int i = 0; i < NBRICK_ROWS; i++) {

			y = BRICK_Y_OFFSET + i * brickDistanceY + BRICK_HEIGHT / 2;

			for (int j = 0; j < NBRICKS_PER_ROW; j++) {

				x = j * brickDistanceX + BRICK_WIDTH / 2;
				temp = getElementAt(x, y);

				if (temp instanceof Brick && ((Brick) temp).isHittable()) return (Brick) temp;
			}
		}

		return null;
	}

	/**
	 * method: runawayBrick()
	 * 
	 * Moves the last brick horizontally at a fixed speed. The brick will sometimes
	 * change directions at random.
	 */
	private void runawayBrick() {

		double x = runawayBrick.getX();

		if (x < 0 || x + BRICK_WIDTH > BreakoutConstants.WIDTH) runawayBrick.reverseVelocity();
		else if (rando.nextDouble(0.0, 100.0) < RUNAWAY_BRICK_REVERSE_CHANCE) runawayBrick.reverseVelocity();		

		runawayBrick.move(runawayBrick.getVelocity(), 0);
	}

	/**
	 * Mouse listener methods:
	 */
	private MouseAdapter mouseListener = new MouseAdapter() {

		public void mouseClicked(MouseEvent e) {

			Object source = e.getSource();
			
			if (source instanceof FancyFont) toMenuPage((FancyFont) source);
		}

		public void mouseEntered(MouseEvent e) {

			if (e.getSource() instanceof FancyFont) {

				FancyFont menuLabel = (FancyFont) e.getSource();

				if (menuLabel.getThreadState()) messageReact(menuLabel, true);
			}
		}

		public void mouseExited(MouseEvent e) {

			if (e.getSource() instanceof FancyFont) {

				FancyFont temp = (FancyFont) e.getSource();

				temp.exit();

				messageReact(temp, false);
			}
		}
	};

	/**
	 * Mouse motion listener methods:
	 */
	private MouseMotionAdapter mouseMotionListener = new MouseMotionAdapter() {

		public void mouseMoved(MouseEvent e) {

			double mouseX = e.getX();

			if (menuPage == MenuPages.GAME_ON) {

				double dx = 0;

				if (mouseX > PADDLE_WIDTH_HALF && mouseX < BreakoutConstants.WIDTH - PADDLE_WIDTH_HALF) {

					/* Last horizontal mouse displacement. */
					dx = mouseX - (paddle.getX() + PADDLE_WIDTH_HALF);
				}

				paddle.move(dx, 0);

				/* This way the paddle speed changes more gradually. */
				paddleSpeed = (paddleSpeed + dx) / 2;
			}
		}
	};

	/**
	 * Key listener methods:
	 */
	private KeyAdapter keyListener = new KeyAdapter() {

		public void keyPressed(KeyEvent e) {

			if (menuPage == MenuPages.HIGHSCORE_INPUT) {

				int keyCode = e.getKeyCode();

				if (keyCode == '\n') {

					if (buffer.length() > 0) menuPage = MenuPages.END_MENU;
	
				} else toBuffer(keyCode);
				
			} else if (e.getKeyChar() == 'p') toMenuPage(pause);
		}
	};

	private Runtime myRuntime = Runtime.getRuntime();

	private RandomGenerator rando = new RandomGenerator();

	private Heart[] hearts = new Heart[LIVES];

	private GImage background;
	private Paddle paddle;
	private Ball ball;
	private Brick[] bricks;
	private GLabel scoreBoard;
	private GLabel timer;
	private ScoreEmblem scoreEmblem;
	private GObject colObject;

	private MenuPages menuPage = MenuPages.START_MENU;

	private double velocityX;
	private double velocityY;
	private double paddleSpeed;

	private int livesLeft;
	private int score;
	private int maxScore;

	private long paddleTimer;
	private long runawayBrickTimer;
	private long gameTimer;

	private boolean switchPage;
	private boolean clear;

	private String buffer;

	private FancyFont startGame;
	private FancyFont highscore;
	private FancyFont previous;
	private FancyFont next;
	private FancyFont clearScores;
	private FancyFont pause;

	private Font gameFontBase;

	private Brick runawayBrick;

	File highscoresTime;
	File highscores;

}
