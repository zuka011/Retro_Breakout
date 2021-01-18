
/*
 * File: FancyFont.java 
 * --------------------- 
 * This class creates a GCompound consisting of GLabels that can change colors.
 */

import java.awt.Color;
import java.awt.Font;

import acm.graphics.*;
import acm.util.RandomGenerator;

public class FancyFont extends GCompound implements Runnable {

	/** Delay between frames when changing color */
	private static final int ANIMATION_FRAME_DELAY = 7;

	/**
	 * Constructor:
	 * 
	 * @param text      : Text for the label
	 * @param font      : Base font for the label
	 * @param fontSize  : The size of the font
	 * @param baseColor : The base color
	 */
	public FancyFont(String text, Font font, int fontSize, Color baseColor) {

		this.text = text;
		this.baseColor = baseColor;
		this.font = font.deriveFont(Font.BOLD, fontSize);

		lines = 1;

		/* This is for counting text lines. */
		int textLength = text.length();

		for (int i = 0; i < textLength; i++) {

			if (text.charAt(i) == '\n') {

				lines++;

			}

		}

		labels = new GLabel[lines];

		createLabel(textLength);

	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		exit = false;

		changeColors();

		exit = true;

	}

	/**
	 * method: getLabel();
	 * 
	 * Returns the text in the FancyFont object.
	 * 
	 * @return A string containing the text of the FancyFont object
	 */
	public String getLabel() {

		return text;

	}

	/**
	 * method: setState();
	 * 
	 * Sets the state of the FancyFont object.
	 * 
	 * @param state : true - > Random color animation, false -> Base color
	 */
	public void setState(boolean newState) {

		currState = newState;

	}

	/**
	 * method: changeColors();
	 * 
	 * Changes the colors of the lines to something random or the original.
	 */
	public void changeColors() {

		if (currState) {

			Color color;

			short colorR = 1, colorG = (short) rando.nextInt(0, 255), colorB = (short) rando.nextInt(0, 255);
			short iterator = 1;

			while (!exit) {

				color = new Color(colorR, colorG, colorB);

				for (int j = 0; j < lines; j++) {

					labels[j].setColor(color);

				}

				pause(ANIMATION_FRAME_DELAY);

				iterator *= (colorR >= 255 || colorR <= 0) ? -1 : 1;

				colorR += iterator;

			}

		} else {

			for (int i = 0; i < lines; i++) {

				labels[i].setColor(baseColor);

			}

		}

	}

	/**
	 * method: getThreadState();
	 * 
	 * returns the state of the thread.
	 * 
	 * @return boolean true -> thread not running : false -> thread running
	 */
	public boolean getThreadState() {

		return exit;

	}

	/**
	 * method: exit();
	 * 
	 * Ends the animation if it is running.
	 */
	public void exit() {

		exit = true;

	}

	/**
	 * method: createLine();
	 * 
	 * Creates the lines of text for the FancyFont object.
	 * 
	 * @param line : N of the line
	 * @param text : The text in that line
	 * @param font : Font used in that line
	 */
	private void createLine(int line, String text, Font font) {

		labels[line] = new GLabel(text);
		labels[line].setFont(font);
		labels[line].setColor(baseColor);

	}

	/**
	 * method: createLabel();
	 * 
	 * Creates and adds the lines of the label to the GCompound.
	 * 
	 * @param textLength : length of the string passed to the constructor
	 */
	private void createLabel(int textLength) {

		int lastCut = 0, currLabel = 0;
		double currY = 0;

		for (int i = 0; i < textLength; i++) {

			if (text.charAt(i) == '\n') {

				createLine(currLabel, text.substring(lastCut, i), font);

				add(labels[currLabel], -labels[currLabel].getWidth() / 2, currY);

				currY += labels[currLabel].getHeight();

				lastCut = i + 1;
				currLabel++;

			}

		}

		createLine(currLabel, text.substring(lastCut), font);

		add(labels[currLabel], -labels[currLabel].getWidth() / 2, currY);

	}

	private RandomGenerator rando = new RandomGenerator();

	private GLabel[] labels;

	private int lines;
	private String text;
	private Font font;
	private Color baseColor;

	private boolean currState;

	private volatile boolean exit = true;

}