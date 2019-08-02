package components;

import java.util.ArrayList;

import processing.core.PImage;

/**
 * The Animation Class
 */
public class AnimationComponent {
	public ArrayList<PImage> frames;

	public float frame;
	public int length;

	public boolean loop;

	public int sx;
	public int sy;
	public int swidth;
	public int sheight;
	public int cellCount;

	public int rate;
	public int start;

	public int count;

	public String name;
	public String pName;

	public boolean ended;

	public PImage image;

	/**
	 * This method controls the animation of elements
	 * @param  frameCount The number of frames in the animation
	 * @param  dt         The width of the frames
	 * @return            PImage image
	 */
	public PImage animate(int frameCount, float dt) {
		try {
			image = frames.get((int) frame);
		} catch (Exception e) {
			// PApplet.println("E : " + frame);
		}

		if ((int) frame >= length) {
			if (loop) {
				frame = start;
			} else {
				frame = length;
				ended = true;
			}
		} else {
			if (frameCount % rate == 0) {
				frame += dt;
			}
		}
		return image;
	}

	/**
	 * Extends the length of the animation
	 * @param newLength The new length for the animation
	 */
	public void extendAnimation(int newLength) {
		length = newLength;
	}

	/**
	 * Retrieves the number of remaining frames
	 * @return The number of remaining frames as an int
	 */
	public int remainingFrames() {
		return (int) (length - frame);
	}
}
