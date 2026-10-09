package project_16x16.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PImage;
import project_16x16.Audio;
import project_16x16.Audio.SFX;
import project_16x16.Time;

/**
 * The Animation Class. Owners advance it with {@link #update()} from their
 * update logic, and draw {@link #getFrame()}. Animations are purely visual:
 * gameplay timing (attack duration, etc.) should be kept by the owner, not
 * derived from when an animation ends.
 */
public class AnimationComponent {

	private ArrayList<PImage> frames;
	private boolean loop;
	private int length;
	/** How long each image is displayed (ms). */
	private float frameMillis;
	/** Time the current image has been displayed for (ms). */
	private float frameElapsed;
	private int currentFrame;
	public String name;
	public boolean ended;
	private final Map<Integer, List<SFX>> sounds = new HashMap<>();

	public AnimationComponent() {
	}

	/**
	 * The most simple method to change current animation sequence.
	 *
	 * @param frames      PImage frame sequence.
	 * @param loop        Whether the animation should loop.
	 * @param frameMillis How long each frame is displayed (ms).
	 */
	public void changeAnimation(ArrayList<PImage> frames, boolean loop, float frameMillis) {
		changeAnimation(frames, loop, frameMillis, frames.size() - 1);
	}

	/**
	 * A method to change current animation sequence. Can specify animation frame
	 * length.
	 *
	 * @param frames      PImage frame sequence.
	 * @param loop        Whether the animation should loop.
	 * @param frameMillis How long each frame is displayed (ms).
	 * @param length      Set a custom anim length
	 */
	public void changeAnimation(ArrayList<PImage> frames, boolean loop, float frameMillis, int length) {
		this.frames = frames;
		this.loop = loop;
		this.frameMillis = frameMillis;
		this.length = length;
		currentFrame = 0;
		frameElapsed = 0;
		ended = false;
	}

	/**
	 * Advances the animation by the current frame's duration. Call once per game
	 * update (not from drawing code).
	 */
	public void update() {
		frameElapsed += Time.deltaMillis();
		while (frameElapsed >= frameMillis && !ended) {
			frameElapsed -= frameMillis;
			if (currentFrame < length) {
				currentFrame++;
			} else if (loop) {
				currentFrame = 0;
			} else {
				ended = true; // hold the last frame
				break;
			}
			List<SFX> coll = sounds.get(currentFrame);
			if (coll != null) {
				coll.forEach(Audio::play);
			}
		}
	}

	/**
	 * @return the image to draw for the current animation frame
	 */
	public PImage getFrame() {
		return frames.get(currentFrame);
	}

	/**
	 * Retrieves the number of remaining frames
	 *
	 * @return The number of remaining frames as an int
	 */
	public int remainingFrames() {
		return length - currentFrame;
	}

	/**
	 * Retrieves the current frame ID
	 *
	 * @return the current frame
	 **/
	public int getFrameID() {
		return currentFrame;
	}

	/**
	 * Set frame (for multiplayer)
	 *
	 * @param frame
	 */
	public void setFrame(int frame) {
		if (frame >= 0 && frame <= frames.size() - 1) {
			currentFrame = frame;
			frameElapsed = 0;
		}
	}

	/**
	 * Retrieves the length of the animation
	 *
	 * @return the time of the animation as an int
	 **/
	public int getAnimLength() {
		return length;
	}

	/**
	 * Set a SFX to play when the animation advances to a given frame.
	 *
	 * @param sound
	 * @param frameNumber
	 */
	public void setSFX(SFX sound, int frameNumber) {
		sounds.computeIfAbsent(frameNumber, k -> new ArrayList<>()).add(sound);
	}
}
