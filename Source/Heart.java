
/*
 * File: Heart.java 
 * --------------------- 
 * This class can generate a Heart made of two oval's and one polygon.
 */

import java.awt.Color;

import acm.graphics.*;
import acm.util.*;

public class Heart extends GCompound implements Runnable {

	/** Scaling parameter for the components of the heart */
	private static final double PART_SIZE = 3.5 / 6;
	
	/** Parameters for the cracking animation */
	private static final int MIN_CRACK_POINTS = 4;
	private static final int MAX_CRACK_POINTS = 7;
	
	/** Delay between the frames during the cracking animation */
	private static final int PAUSE_TIME = 150;

	/**
	 * Constructor:
	 * 
	 * @param width  : Width of the heart
	 * @param height : Height of the heart
	 * @param color  : color of the heart
	 */
	public Heart(double width, double height, Color color) {

		this.width = width;
		this.partSize = width * PART_SIZE;
		this.color = color;

		createLeftOval();
		createRightOval();
		createCenter();

		scale(1, height / width);

	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		crack();

	}

	/**
	 * method: setColor();
	 * 
	 * This method changes the color of the heart.
	 * 
	 * @param color : New color
	 */
	public void setColor(Color color) {

		leftOval.setColor(color);
		leftOval.setFillColor(color);

		rightOval.setColor(color);
		rightOval.setFillColor(color);

		center.setColor(color);
		center.setFillColor(color);

	}

	/**
	 * method: setCracked();
	 * 
	 * Sets the heart's state to either cracked(true) or not cracked(false).
	 * 
	 * @param cracked The state of the heart
	 */
	public void setCracked(boolean cracked) {

		this.cracked = cracked;

	}

	/**
	 * method: createLeftOval();
	 * 
	 * Adds the left oval to the GCompound object.
	 */
	private void createLeftOval() {

		leftOval = new GOval(partSize, partSize);

		leftOval.setColor(color);
		leftOval.setFilled(true);
		leftOval.setFillColor(color);

		add(leftOval, 0, 0);

	}

	/**
	 * method: createRightOval();
	 * 
	 * Adds the right oval to the GCompound object.
	 */
	private void createRightOval() {

		rightOval = new GOval(partSize, partSize);

		rightOval.setColor(color);
		rightOval.setFilled(true);
		rightOval.setFillColor(color);

		add(rightOval, width - partSize, 0);

	}

	/**
	 * method: createCenter();
	 * 
	 * Adds a polygon to the center of the GCompound object.
	 */
	private void createCenter() {

		double dx = width - partSize, dy = width * Math.sqrt(2 * PART_SIZE - 1);
		double startX = width / 2, startY = (partSize - dy) / 2;

		center = new GPolygon();

		center.addVertex(startX, startY);
		center.addEdge(-dx, dy);
		center.addEdge(dx, dy);
		center.addEdge(dx, -dy);

		center.setColor(color);
		center.setFilled(true);
		center.setFillColor(color);

		add(center, 0, 0);

		drawCrack(startX, startY, startX, startY + 2 * dy);

	}

	/**
	 * method: drawCrack();
	 * 
	 * Adds randomly aligned invisible GLine's to the heart.
	 * 
	 * @param startX : X for start of crack
	 * @param startY : Y for start of crack
	 * @param endX   : X for end of crack
	 * @param endY   : Y for end of crack
	 */
	private void drawCrack(double startX, double startY, double endX, double endY) {

		int crackPoints = rando.nextInt(MIN_CRACK_POINTS, MAX_CRACK_POINTS);
		
		double maxHorizontalDeviation = width / 3;
		double maxVerticalCrackSpacing = (endY - startY) / crackPoints;
		
		double x1 = startX;
		double y1 = startY;
		double x2 = 0;
		double y2 = 0;

		crackLines = new GLine[crackPoints];

		crackPoints--;

		/* The random crack Lines are generated in this loop. */
		for (int i = 0; i < crackPoints; i++) {

			x2 = maxHorizontalDeviation * rando.nextDouble(0.3, 1);
			x2 = startX + x2 * (i % 2 == 0 ? 1 : -1);

			y2 = y1 + maxVerticalCrackSpacing * rando.nextDouble(0.9, 1);

			crackLines[i] = new GLine(x1, y1, x2, y2);

			add(crackLines[i]);
			crackLines[i].setVisible(false);

			x1 = x2;
			y1 = y2;
			
			maxHorizontalDeviation *= 0.7;

		}

		crackLines[crackPoints] = new GLine(x2, y2, endX, endY);

		add(crackLines[crackPoints]);

		crackLines[crackPoints].setVisible(false);

	}

	/**
	 * method: crack();
	 * 
	 * Initiates the cracking/uncracking animation.
	 */
	private void crack() {

		int crackPoints = crackLines.length;

		/*
		 * The animation is achieved by changing the visibility of GLine's on the heart.
		 */
		for (int i = 0; i < crackPoints; i++) {

			crackLines[i].setVisible(cracked);
			pause(PAUSE_TIME);

		}

	}

	RandomGenerator rando = new RandomGenerator();

	private double width;
	private double partSize;
	private Color color;
	private GOval leftOval;
	private GOval rightOval;
	private GPolygon center;
	private GLine[] crackLines;

	private boolean cracked;

}