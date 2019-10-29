package game.engine.ParticleSystem.events;

import java.util.ArrayList;

import game.engine.ParticleSystem.Particle;
import processing.core.PApplet;
import processing.core.PImage;
import game.engine.sidescroller.Tileset;

/**
 * Particle Animation Controller
 * <p>
 * Add animation to particle.
 *
 * @author petturtle
 */
public class ParticleAnimationController implements ParticleEventListener {

	private ArrayList<PImage> images;
	private int rate;
	
	/**
	 * Add animation to particle
	 *
	 * @param animationName animation name
	 * @param rate 			animation speed, high value = slow speed, -1 = match life span of particle
	 */
	public ParticleAnimationController(String animationName, int rate) {
		this(Tileset.getAnimation(animationName), rate);
	}
	
	/**
	 * Add animation to particle
	 *
	 * @param animationName animation ArrayList
	 * @param rate 			animation speed, high value = slow speed, -1 = match life span of particle
	 */
	public ParticleAnimationController(ArrayList<PImage> images, int rate){
		this.images = images;
		this.rate = rate;
	}
	
	@Override
	public void onParticleSpawnEvent(Particle particle) {
		setParticle(particle);
	};
	
	@Override
	public void onParticleRunEvent(Particle particle) {
		setParticle(particle);
	}
	
	
	
	@Override
	public ParticleEventListener copy() {
		return new ParticleAnimationController(images, rate);
	}
	
	private void setParticle(Particle particle) {
		if (rate == -1) {
			particle.image = getImage(particle.maxLifespan, particle.lifespan);
		} else {
			particle.image = getImage(particle.frameCount);
		}
	}
	
	private PImage getImage(int frameCount) {
		int id = (frameCount/rate) % images.size();
		return images.get(id);
	}
	
	private PImage getImage(float maxLife, float currentLife) {
		int id = (int) PApplet.map(currentLife, maxLife, 0, 0, images.size()-1);
		return images.get(id);
	}
}
