
/*
 * File: Brick.java 
 * ---------------------
 * This class is for the bricks in breakout.
 */

import acm.graphics.*;
import acm.util.RandomGenerator;

public class Brick extends GCompound implements Runnable {

	/** Color names for the bricks */
	private static final String[] COLORS = new String[] { "red", "orange", "yellow", "green", "cyan" };
	private static final int NCOLORS = COLORS.length;

	/** Chance for the brick to randomly fidget */
	private static final double FIDGET_CHANCE = 10;
	private static final int FIDGET_AMPLITUDE = 1;
	private static final int FIDGET_FRAME_DELAY = 1000;

	/** Fade animation parameters */
	private static final double FADE_SPEED = 0.25;
	private static final int NANIMATION_FRAMES_FADE = 55;
	private static final int ANIMATION_FRAME_DELAY_FADE = 15;
	private static final int BLINK_A = 300;
	private static final int BLINK_B = 3;

	/** Reduces the load from the fidget threads */
	private static final int FIDGET_PAUSE_TIME = 100;

	/** Amount of hits required to break the final brick */
	private static final int RUNAWAY_LIVES = 3;
	private static final double RUNAWAY_BRICK_VELOCITY = 2;

	/** shakeVertically() animation parameters */
	private static final int NANIMATION_FRAMES_SHAKE = 5;
	private static final int ANIMATION_FRAME_DELAY_SHAKE = 11;

	private static final int BASE_SHAKE_SPEED = 1;
	private static final int SHAKE_REPEATS = 10;
	private static final double DAMPENING_SPEED = 0.7;

	/** wavePass() animation parameters */
	private static final int NANIMATION_FRAMES_WAVE = 20;
	private static final int ANIMATION_FRAME_DELAY_WAVE = 10;
	private static final double WAVE_RATIO = 0.2;
	private static final double WAVE_PASS_REPEATS = 2;

	/**
	 * Constructor:
	 * 
	 * @param width      : Width of the brick
	 * @param height     : Height of the brick
	 * @param colorIndex : Index of the brick's color in the COLORS array
	 */
	public Brick(double width, double height, int colorIndex) {

		this.width = width;
		this.height = height;
		this.colorIndex = colorIndex;

		lives = RUNAWAY_LIVES;

		makeBrick();

	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		exit = false;

		if (runaway) {

			lives--;
			deflect();

		} else if (destroyed) {

			fade();

		} else if (rando.nextDouble(0, 100) < FIDGET_CHANCE) {

			fidget();

		}

		exit = true;

	}

	/**
	 * method: getLives();
	 * 
	 * returns the amount of lives the brick has left.
	 * 
	 * @return an integer corresponding to the amount of lives left
	 */
	public int getLives() {

		return lives;

	}

	/**
	 * method: getColorIndex();
	 * 
	 * returns the index of the brick's color in the BRICK_COLORS/COLORS array.
	 * 
	 * @return an integer representing the index
	 */
	public int getColorIndex() {

		return colorIndex;

	}

	/**
	 * method: getScoreMultiplier();
	 * 
	 * returns the score multiplier for a brick.
	 * 
	 * @return an integer representing the score
	 */
	public int getScoreMultiplier() {

		return 2 * NCOLORS - colorIndex;

	}

	/**
	 * method: changeToRunaway();
	 * 
	 * Turns the instance of a brick into a runaway brick.
	 */
	public void changeToRunaway() {

		runaway = true;

		brick.setImage("./images/brick/lava.png");

		brick.setSize(width, height);

		wave = new GImage("./images/brick/wave.png");

		wave.setSize(width * WAVE_RATIO, height);

		add(wave);

		wavePass();

		runawayBrickVelocity = RUNAWAY_BRICK_VELOCITY;

	}

	/**
	 * method: setDeflectDirection();
	 * 
	 * Sets the deflect direction for the deflect animation
	 * 
	 * @param direction : an integer representing the angle of collision in degrees.
	 */
	public void setDeflectDirection(int direction) {

		deflectDirection = direction;

	}

	/**
	 * method: destroy();
	 * 
	 * Removes the brick from the screen with, but let's it have one final
	 * flamboyant go at life.
	 */
	public void destroy() {

		destroyed = true;

	}

	/**
	 * method: isHittable();
	 * 
	 * Returns a boolean value corresponding to the brick's state.
	 * 
	 * @return a boolean : true -> brick is destroyed
	 */
	public boolean isHittable() {

		return !destroyed;

	}

	/**
	 * method: getVelocity();
	 * 
	 * returns the the velocity of the runaway brick.
	 * 
	 * @return a double representing the velocity
	 */
	public double getVelocity() {

		return runawayBrickVelocity;

	}

	/**
	 * method: reverseVelocity();
	 * 
	 * reverses the runaway brick's velocity.
	 */
	public void reverseVelocity() {

		runawayBrickVelocity *= -1;

	}

	/**
	 * method: getThreadState();
	 * 
	 * Used to see if a thread is running on this object.
	 * 
	 * @return boolean value : true - > thread running
	 */
	public boolean getThreadState() {

		return exit;

	}

	/**
	 * method: exit();
	 * 
	 * Tells the thread to finish executing.
	 */
	public void exit() {

		exit = true;

	}

	/**
	 * method: makeBrick();
	 * 
	 * Adds a brick to the GCompound.
	 */
	private void makeBrick() {

		brick = new GImage("./images/brick/" + COLORS[colorIndex] + ".png");

		add(brick);

		brick.setSize(width, height);

	}

	/**
	 * method: fidget();
	 * 
	 * Makes the brick move around slightly in between random intervals of time.
	 */
	private void fidget() {

		int fidgetX = 0;
		int fidgetY = 0;

		double fidgetDelay = FIDGET_FRAME_DELAY;

		boolean phase = false;

		while (!exit) {

			if (System.currentTimeMillis() - fidgetTimer > fidgetDelay) {

				if (!phase) {

					fidgetX = FIDGET_AMPLITUDE * rando.nextInt(-1, 1);
					fidgetY = FIDGET_AMPLITUDE * rando.nextInt(-1, 1);

					brick.move(fidgetX, fidgetY);

				} else {

					fidgetX *= -1;
					fidgetY *= -1;

					brick.move(fidgetX, fidgetY);

				}

				fidgetDelay = FIDGET_FRAME_DELAY * rando.nextDouble(0.5, 4);
				fidgetTimer = System.currentTimeMillis();

				phase = !phase;

			}

			pause(FIDGET_PAUSE_TIME);

		}

	}

	/**
	 * method: fade();
	 * 
	 * Animates the death of a hero.
	 */
	private void fade() {

		amplitude(FADE_SPEED, NANIMATION_FRAMES_FADE, ANIMATION_FRAME_DELAY_FADE);

		remove(brick);

	}

	/**
	 * method: deflect();
	 * 
	 * Triggers the deflect animation based on the angle of collision.
	 */
	private void deflect() {

		if (deflectDirection % 180 == 90) {

			shakeVertically();

		} else {

			reverseVelocity();
			wavePass();

		}

	}

	/**
	 * method: shakeVertically();
	 * 
	 * Shakes the brick vertically based on the angle of collision.
	 */
	private void shakeVertically() {

		double shakeSpeed = BASE_SHAKE_SPEED * Math.sin(-Math.toRadians(deflectDirection));

		for (int i = 0; i < SHAKE_REPEATS; i++) {

			amplitude(shakeSpeed, NANIMATION_FRAMES_SHAKE, ANIMATION_FRAME_DELAY_SHAKE);

			shakeSpeed *= -1;

			amplitude(shakeSpeed, ANIMATION_FRAME_DELAY_SHAKE, ANIMATION_FRAME_DELAY_SHAKE);

			shakeSpeed *= DAMPENING_SPEED;

		}

	}

	/**
	 * method: amplitude();
	 * 
	 * Animates an amplitude of the shake animation.
	 * 
	 * @param speed : The speed of movement during the animation
	 */
	private void amplitude(double speed, int frames, int delay) {

		boolean blink = true;

		for (int i = 1; i <= frames; i++) {

			brick.move(0, speed);

			if (destroyed) {

				if (i % (BLINK_A /  (i * BLINK_B) + 1) == 0) {

					brick.setVisible(blink);
					blink = !blink;

				}

			}

			pause(delay);

		}

	}

	/**
	 * method: wavePass();
	 * 
	 * Animates a wave passing along the runaway brick, also dependent on the angle
	 * of collision.
	 */
	private void wavePass() {

		double traversableWidth = width - wave.getWidth();
		double speedX = traversableWidth / NANIMATION_FRAMES_WAVE;
		double waveStartPoint = 0;

		if (deflectDirection == 180) {

			waveStartPoint = traversableWidth;
			speedX *= -1;

		}

		for (int i = 0; i < WAVE_PASS_REPEATS; i++) {

			add(wave, waveStartPoint, brick.getY());

			for (int j = 0; j < NANIMATION_FRAMES_WAVE; j++) {

				wave.move(speedX, 0);

				pause(ANIMATION_FRAME_DELAY_WAVE);

			}

			remove(wave);

		}

	}

	private RandomGenerator rando = new RandomGenerator();

	private GImage brick;
	private GImage wave;

	private int lives;
	private int colorIndex;
	private double width;
	private double height;

	private double runawayBrickVelocity = 0;

	private int deflectDirection;

	private boolean runaway;
	private boolean destroyed;

	private long fidgetTimer;

	private volatile boolean exit = true;

}