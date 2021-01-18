
/*
 * File: Paddle.java 
 * ---------------------
 * This class is for the paddle in breakout.
 */

import acm.graphics.*;

public class Paddle extends GCompound implements Runnable {

	/** Distance between the paddle pieces */
	private static final int PADDLE_SEP = 3;

	/** Parameters for the deflect and levitate animations */
	private static final int DEFLECT_SPEED = 1;
	private static final int NANIMATION_FRAMES_DEFLECT = 2;
	private static final int ANIMATION_FRAME_DELAY_DEFLECT = 50;

	private static final double LEVITATION_SPEED = 1;
	private static final int ANIMATION_FRAME_DELAY_LEVITATE = 950;

	/**
	 * Constructor:
	 * 
	 * @param width  : Width of the paddle
	 * @param height : Height of the paddle
	 */
	public Paddle(int width, int height) {

		this.width = width;
		this.height = height;

		makePaddle();

	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		exit = false;

		if (deflect) {

			deflectBounce();

		}

		levitate();

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
	 * method: makePaddle();
	 * 
	 * Adds the paddle pieces to the GCompound.
	 */
	private void makePaddle() {

		paddleTop = new GImage("./images/paddle/paddle top.png");

		paddleTop.setSize(width, height);

		add(paddleTop);

		paddleBottom = new GImage("./images/paddle/paddle bottom.png");

		paddleBottom.setSize(width, height);

		add(paddleBottom, 0, PADDLE_SEP);

	}

	/**
	 * method: deflectBall();
	 * 
	 * If called the next thread will execute the deflect animation.
	 */
	public void deflectBall() {

		deflect = true;

	}

	/**
	 * method: deflectBounce();
	 * 
	 * Animation for deflecting the ball.
	 */
	private void deflectBounce() {

		int deflectSpeed = DEFLECT_SPEED;

		amplitude(deflectSpeed);

		deflectSpeed *= -1;

		amplitude(deflectSpeed);
		
		deflect = false;

	}

	/**
	 * method: amplitude();
	 * 
	 * Animates an amplitude of the deflect animation.
	 * 
	 * @param speed : The speed of movement during the animation
	 */
	private void amplitude(double speed) {

		for (int i = 0; i < NANIMATION_FRAMES_DEFLECT; i++) {

			paddleTop.move(0, speed);

			pause(ANIMATION_FRAME_DELAY_DEFLECT);

		}

	}

	/**
	 * method: levitate();
	 * 
	 * executes the levitation animation on the lower paddle piece.
	 */
	private void levitate() {

		double levitateSpeed = LEVITATION_SPEED;
		boolean phase = false;

		while (!exit) {

			if (System.currentTimeMillis() - timer > ANIMATION_FRAME_DELAY_LEVITATE) {

				if (!phase) {

					phase = true;

				} else {

					levitateSpeed *= -1;
					phase = false;

				}

				paddleBottom.move(0, levitateSpeed);

				timer = System.currentTimeMillis();

			}

		}

		paddleBottom.setLocation(0, PADDLE_SEP);

	}

	private GImage paddleTop;
	private GImage paddleBottom;

	private int width;
	private int height;
	private long timer;

	private boolean deflect;
	private volatile boolean exit;

}