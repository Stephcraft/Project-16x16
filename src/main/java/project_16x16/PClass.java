package project_16x16;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * <h1>PClass</h1>
 * <p>
 * This is the base class for most objects in the game. It contains the
 * SideScroller, gives some keys identifiers as well as some other basic
 * information for object ins the game. Also acts as a wrapper for some
 * processing functions making them easier to call.
 * </p>
 */
public abstract class PClass {
	public SideScroller applet;

	public static final int CENTER = PConstants.CENTER;
	public static final int CORNER = PConstants.CORNER;

	// Gives the keys identifiers.
	public static final int UP = PConstants.UP;
	public static final int DOWN = PConstants.DOWN;
	public static final int LEFT = PConstants.LEFT;
	public static final int RIGHT = PConstants.RIGHT;
	public static final int SHIFT = PConstants.SHIFT;
	public static final int TOP = PConstants.TOP;
	public static final int KEY_W = 87;
	public static final int KEY_A = 65;
	public static final int KEY_S = 83;
	public static final int KEY_D = 68;

	/**
	 * Constructor
	 * 
	 * @param a The SideScroller game controller.
	 */
	public PClass(SideScroller sideScroller) {
		applet = sideScroller;
	}

	/**
	 * prints a line to the applet. most likely for debugging purposes.
	 * 
	 * @param msg The line to be written.
	 */
	public void println(String msg) {
		PApplet.println(msg);
	}

	/**
	 * Controlls the background color of the applet.
	 * 
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public void background(int r, int g, int b) {
		applet.background(r, g, b);
	}

	/**
	 * Controls the background grey scale of the applet
	 * 
	 * @param g grey scale
	 */
	public void background(int g) {
		applet.background(g);
	}

	/**
	 * Sets the background image of the applet.
	 * 
	 * @param icon_eyeActive The image to use as PImage
	 * @param x              the x position for it
	 * @param y              the y position for it
	 */
	public void image(PImage icon_eyeActive, float x, float y) {
		applet.image(icon_eyeActive, x, y);
	}

	/**
	 * loads an image from source to use.
	 * 
	 * @param src the source path for the image
	 * @return the image as a PImage
	 */
	public PImage loadImage(String src) {
		return applet.loadImage(src);
	}

	/**
	 * rounds a number
	 * 
	 * @param n the number to round
	 * @return the rounded number
	 */
	public float round(float n) {
		return PApplet.round(n);
	}
}
