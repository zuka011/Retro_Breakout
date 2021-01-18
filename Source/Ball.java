
/*
 * File: Ball.java 
 * ---------------------
 * This class is for the ball in breakout.
 */

import acm.graphics.*;

public class Ball extends GCompound implements Runnable {

	/** Spin animation parameters */
	private static final int ANIMATION_FRAME_DELAY = 100;
	private static final int NANIMATION_FRAMES = 4;

	/**
	 * Constructor:
	 * 
	 * @param diameter : Diameter of the ball
	 */
	public Ball(double diameter) {

		this.diameter = diameter;

		makeBall();

	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		exit = false;

		spin();

		exit = true;
		
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
	 * method: makeBall();
	 * 
	 * Adds the pieces of the ball to the GCompound.
	 */
	private void makeBall() {

		for (int i = 0; i < NANIMATION_FRAMES; i++) {

			ball[i] = new GImage("./images/ball/ball" + i + ".png");

			ball[i].setSize(diameter, diameter);

			add(ball[i]);
			ball[i].setVisible(false);

		}

		ball[0].setVisible(true);

	}

	/**
	 * method: spin();
	 * 
	 * Animates the ball, it's supposed to look like it's spinning.
	 */
	private void spin() {

		int frame = 0;

		while (!exit) {

			ball[frame].setVisible(false);

			frame++;

			if (frame == NANIMATION_FRAMES) {

				frame = 0;

			}

			ball[frame].setVisible(true);

			pause(ANIMATION_FRAME_DELAY);

		}

	}

	private GImage[] ball = new GImage[NANIMATION_FRAMES];

	private double diameter;

	private volatile boolean exit;
}