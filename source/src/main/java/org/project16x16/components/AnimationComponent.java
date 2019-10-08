package org.project16x16.components;

import java.util.ArrayList;

import processing.core.PImage;
import org.project16x16.sidescroller.SideScroller;

/**
 * The Animation Class
 */
public class AnimationComponent {
	
	public static SideScroller applet;

	private ArrayList<PImage> frames;
	private boolean loop;
	private int length;
	private int rate;
	private int start;
	private int firstFrame;
	private float currentFrame;
	public String name;
	public boolean ended;

	/**
	 * The most simple method to change current animation sequence.
	 * 
	 * @param frames PImage frame sequence.
	 * @param loop   Whether the animation should loop.
	 * @param rate   Every x frames the next frame is loaded.
	 */
	public void changeAnimation(ArrayList<PImage> frames, boolean loop, int rate) {
		if (!frames.equals(this.frames) || ended) { // change animation only if it's a different one.
			this.frames = frames;
			this.loop = loop;
			this.rate = rate;
			length = frames.size() - 1;
			start = 0;
			currentFrame = start;
			firstFrame = applet.frameCount;
		}
	}

	/**
	 * A method to change current animation sequence. Can specify animation frame length.
	 * @param frames PImage frame sequence.
	 * @param loop   Whether the animation should loop.
	 * @param rate   Every x frames the next frame is loaded.
	 * @param length Set a custom anim length
	 */
	public void changeAnimation(ArrayList<PImage> frames, boolean loop, int rate, int length) {
		if (!frames.equals(this.frames) || ended) { // change animation only if it's a different one.
			this.frames = frames;
			this.loop = loop;
			this.rate = rate;
			this.length = length;
			start = 0;
			currentFrame = start;
			firstFrame = applet.frameCount;
		}
	}

	/**
	 * This method controls the animation of elements (cycles through frames).
	 * @return PImage image
	 */
	public PImage animate() {
		
		PImage frame = frames.get((int) currentFrame);

		if ((applet.frameCount - firstFrame) % rate == 0) {
			currentFrame += applet.deltaTime;
			if (currentFrame > length) {
				if (!loop) {
					ended = true;
				}
				currentFrame = 0;
			}
		}
		return frame;
	}

	/**
	 * Retrieves the number of remaining frames
	 * 
	 * @return The number of remaining frames as an int
	 */
	public int remainingFrames() {
		return (int) (length - currentFrame);
	}

	public float getFrame() {
		return currentFrame;
	}
	
	public int getAnimLength() {
		return length;
	}
}
