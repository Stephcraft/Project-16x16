package project_16x16.components;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.map.MultiValueMap;

import processing.core.PImage;
import project_16x16.Audio.SFX;
import project_16x16.SideScroller;
import project_16x16.factory.AudioFactory;

/**
 * The Animation Class
 */
public class AnimationComponent {

	private static SideScroller applet;
	private ArrayList<PImage> frames;
	private boolean loop;
	private int length;
	private int rate;
	private int start;
	private int firstFrame;
	private float currentFrame;
	public String name;
	public boolean ended;
	private final MultiValueMap sounds;

	public AnimationComponent() {
		sounds = new MultiValueMap();
	}

	public static void assignApplet(SideScroller applet) {
		AnimationComponent.applet = applet;
	}

	/**
	 * The most simple method to change current animation sequence.
	 * 
	 * @param frames PImage frame sequence.
	 * @param loop   Whether the animation should loop.
	 * @param rate   Every x frames the next frame is loaded.
	 */
	public void changeAnimation(ArrayList<PImage> frames, boolean loop, int rate) {
		changeAnimation(frames, loop, rate, frames.size() - 1);
	}

	/**
	 * A method to change current animation sequence. Can specify animation frame
	 * length.
	 * 
	 * @param frames PImage frame sequence.
	 * @param loop   Whether the animation should loop.
	 * @param rate   Every x frames the next frame is loaded.
	 * @param length Set a custom anim length
	 */
	public void changeAnimation(ArrayList<PImage> frames, boolean loop, int rate, int length) {
		this.frames = frames;
		this.loop = loop;
		this.rate = rate;
		this.length = length;
		start = 0;
		currentFrame = start;
		firstFrame = applet.frameCount;
	}

	/**
	 * This method controls the animation of elements (cycles through frames).
	 * 
	 * @return PImage image
	 */
	@SuppressWarnings("unchecked")
	public PImage animate() {
		PImage frame = frames.get((int) currentFrame);
		if ((applet.frameCount - firstFrame) % rate == 0) {
			currentFrame++;
			if (currentFrame > length) {
				if (!loop) {
					ended = true;
				}
				currentFrame = 0;
			}
		}
		Collection<SFX> coll = (Collection<SFX>) sounds.get((int) currentFrame); // TODO high overhead?
		if (coll != null) {
			coll.forEach(sound -> AudioFactory.getInstance().play(sound));
		}
		return frame;
	}

	/**
	 * Return current frame without animating further.
	 * 
	 * @return
	 */
	public PImage getFrame() { return frames.get((int) currentFrame); }

	/**
	 * Retrieves the number of remaining frames
	 * 
	 * @return The number of remaining frames as an int
	 */
	public int remainingFrames() {
		return (int) (length - currentFrame);
	}

	/**
	 * Retrieves the current frame ID
	 *
	 * @return the current frame as a float
	 **/
	public int getFrameID() { return (int) currentFrame; }

	/**
	 * Set frame (for multiplayer)
	 * 
	 * @param frame
	 */
	public void setFrame(int frame) {
		if (frame >= 0 && frame <= frames.size() - 1) {
			currentFrame = frame;
		}
	}

	/**
	 * Retrieves the length of the animation
	 *
	 * @return the time of the animation as an int
	 **/
	public int getAnimLength() { return length; }

	/**
	 * Set a SFX to play trigger at a given animation frame. TODO frames param
	 * 
	 * @param sound
	 * @param frameNumber
	 */
	public void setSFX(SFX sound, int frameNumber) {
		sounds.put(frameNumber, sound);
	}
}
