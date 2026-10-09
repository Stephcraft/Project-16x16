package project_16x16.particleSystem.events;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import project_16x16.Tileset;
import project_16x16.particleSystem.Particle;

/**
 * Particle Animation Controller
 * <p>
 * Add animation to particle.
 *
 * @author petturtle
 */
public class ParticleAnimationController implements ParticleEventListener {

	private ArrayList<PImage> images;
	/** How long each image is displayed (ms), or -1 to span the particle's life. */
	private int frameMillis;

	/**
	 * Add animation to particle
	 *
	 * @param animationName animation name
	 * @param frameMillis   how long each image is displayed (ms), -1 = match life
	 *                      span of particle
	 */
	public ParticleAnimationController(String animationName, int frameMillis) {
		this(Tileset.getAnimation(animationName), frameMillis);
	}

	/**
	 * Add animation to particle
	 *
	 * @param images      animation ArrayList
	 * @param frameMillis how long each image is displayed (ms), -1 = match life
	 *                    span of particle
	 */
	public ParticleAnimationController(ArrayList<PImage> images, int frameMillis) {
		this.images = images;
		this.frameMillis = frameMillis;
	}

	@Override
	public void onParticleSpawnEvent(Particle particle) {
		setParticle(particle);
	}

	@Override
	public void onParticleRunEvent(Particle particle) {
		setParticle(particle);
	}

	@Override
	public ParticleEventListener copy() {
		return new ParticleAnimationController(images, frameMillis);
	}

	private void setParticle(Particle particle) {
		if (frameMillis == -1) {
			particle.image = getImage(particle.maxLifespan, particle.lifespan);
		} else {
			particle.image = getImage(particle.age);
		}
	}

	private PImage getImage(float age) {
		int id = (int) (age * 1000 / frameMillis) % images.size();
		return images.get(id);
	}

	private PImage getImage(float maxLife, float currentLife) {
		int id = (int) PApplet.map(currentLife, maxLife, 0, 0, images.size() - 1);
		return images.get(id);
	}
}
