
/*
 * File: ScoreEmblem.java 
 * ---------------------
 * This class creates an emblem, which can flip in two directions.
 */

import java.awt.Color;
import acm.graphics.*;
import acm.util.RandomGenerator;

public class ScoreEmblem extends GCompound implements Runnable {

	/** Ratio of the "radius" of the inner polygon to the outer */
	private static final double POLYGON_RATIO = 0.65;

	/** Animation parameters */
	private static final int FLIP_STAGES = 15;
	private static final int PAUSE_TIME = 15;

	private static final double FLIP_STEP_SIZE = 0.8;

	/**
	 * Constructor:
	 * 
	 * @param width          : Width of the emblem
	 * @param height         : Height of the emblem
	 * @param sides          : Sides of the polygon
	 * @param primaryColor   : Outer color
	 * @param secondaryColor : Inner color
	 */
	public ScoreEmblem(double width, double height, int sides, Color primaryColor, Color secondaryColor) {

		this.radius = width / 2;
		this.sides = sides;

		angleStep = 2 * Math.PI / sides;

		outer = createPoly(radius, primaryColor);
		inner = createPoly(radius * POLYGON_RATIO, secondaryColor);

		scale(1, height / width);

	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		exit = false;

		flip();

		exit = true;

	}

	/**
	 * method: getColor();
	 * 
	 * Returns the color of the polygon at the respective index.
	 * 
	 * @param i : Index of the polygon
	 * @return Color of the polygon at respective index
	 */
	public Color getColor(int i) {

		if (i == 1) {

			return outer.getFillColor();

		}

		else if (i == 2) {

			return inner.getFillColor();

		}

		else {

			return Color.white;

		}

	}

	/**
	 * method: setColor();
	 * 
	 * Sets the colors of the polygons.
	 * 
	 * @param color1 : Color of the outer polygon
	 * @param color2 : Color of the inner polygon
	 */
	public void setColor(Color color1, Color color2) {

		outer.setColor(color1);
		outer.setFillColor(color1);

		inner.setColor(color2);
		inner.setFillColor(color2);

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
	 * method: createPoly();
	 * 
	 * Creates and returns a polygon with the given parameters.
	 * 
	 * @param polyRadius : Radius of the circle inside which the polygon is drawn
	 * @param color      : Color of the Polygon
	 * @return reference to a GPolygon object
	 */
	private GPolygon createPoly(double polyRadius, Color color) {

		GPolygon poly = new GPolygon();

		double x = 0, y = 0, angle = -Math.PI / 2;

		for (int i = 0; i < sides; i++) {

			x = radius + polyRadius * Math.cos(angle);
			y = radius + polyRadius * Math.sin(angle);

			poly.addVertex(x, y);

			angle += angleStep;

		}

		poly.setColor(color);
		poly.setFilled(true);
		poly.setFillColor(color);

		add(poly);

		return poly;

	}

	/**
	 * method: flip();
	 * 
	 * Flips the emblem in a random direction.
	 */
	private void flip() {

		boolean flipDirection = rando.nextBoolean();

		if (flipDirection) {

			flipInDirection(FLIP_STEP_SIZE, 1);

		} else {

			flipInDirection(1, FLIP_STEP_SIZE);

		}

	}

	private void flipInDirection(double flipStepSizeX, double flipStepSizeY) {

		double reverseX = 1 / flipStepSizeX;
		double reverseY = 1 / flipStepSizeY;

		double x = getX() + radius;
		double y = getY() + radius;

		for (int i = 0; i < FLIP_STAGES; i++) {

			scale(flipStepSizeX, flipStepSizeY);

			setLocation(x - getWidth() / 2, y - getHeight() / 2);

			pause(PAUSE_TIME);

		}

		for (int i = 0; i < FLIP_STAGES; i++) {

			scale(reverseX, reverseY);

			setLocation(x - getWidth() / 2, y - getHeight() / 2);

			pause(PAUSE_TIME);

		}

	}

	RandomGenerator rando = new RandomGenerator();

	private int sides;
	private double radius;
	private double angleStep;

	private GPolygon outer;
	private GPolygon inner;

	private volatile boolean exit = true;

}