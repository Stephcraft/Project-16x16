package sidescroller;

import processing.core.*;
import sidescroller.Util;

/**
 * <h1>PClass</h1>
 * <p>
 * This is the base class for most objects in the game.
 * It contains the SideScroller, gives some keys identifiers
 * as well as some other basic information for object ins the
 * game. Also acts as a wrapper for some processing functions making
 * them easier to call.
 * </p>
 */
public class PClass {
	public SideScroller applet;
	public Util util;

	public int CENTER;
	public int CORNER;

	// Gives the keys identifiers.
	public final int UP = PApplet.UP;
	public final int DOWN = PApplet.DOWN;
	public final int LEFT = PApplet.LEFT;
	public final int RIGHT = PApplet.RIGHT;
	public final int SHIFT = PApplet.SHIFT;
	public final int TOP;
	public final int KEY_W = 87;
	public final int KEY_A = 65;
	public final int KEY_S = 83;
	public final int KEY_D = 68;

	/**
	 * Constructor
	 * @param a The SideScroller game controller.
	 */
	public PClass(SideScroller a) {
		applet = a;
		util = new Util(a);

		CENTER = PConstants.CENTER;
		CORNER = PConstants.CORNER;

		TOP = PConstants.TOP;
	}

	/**
	 * prints a line to the applet. most likely for debugging purposes.
	 * @param msg The line to be written.
	 */
	public void println(String msg) {
		PApplet.println(msg);
	}

	/**
	 * Controlls the background color of the applet.
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public void background(int r, int g, int b) {
		applet.background(r, g, b);
	}

	/**
	 * Controls the background grey scale of the applet
	 * @param g grey scale
	 */
	public void background(int g) {
		applet.background(g);
	}

	/**
	 * Sets the background image of the applet.
	 * @param img The image to use as PGraphics
	 * @param x the x position for it
	 * @param y the y position for it
	 */
	public void image(PGraphics img, float x, float y) {
		applet.image(img, x, y);
	}

	/**
	 * loads an image from source to use.
	 * @param src the source path for the image
	 * @return the image as a PImage
	 */
	public PImage loadImage(String src) {
		return applet.loadImage(src);
	}

	/**
	 * rounds a number
	 * @param n the number to round
	 * @return the rounded number
	 */
	public float round(float n) {
		return PApplet.round(n);
	}
}
