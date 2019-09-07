package ParticleSystem.events;

import java.util.ArrayList;

import ParticleSystem.Particle;
import processing.core.PImage;
import sidescroller.Tileset;

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
	 * @param rate 			animation speed, high value = slow speed
	 */
	public ParticleAnimationController(String animationName, int rate) {
		this(Tileset.getAnimation(animationName), rate);
	}
	
	/**
	 * Add animation to particle
	 *
	 * @param animationName animation ArrayList
	 * @param rate 			animation speed, high value = slow speed
	 */
	public ParticleAnimationController(ArrayList<PImage> images, int rate){
		this.images = images;
		this.rate = rate;
	}
	
	@Override
	public void onParticleRunEvent(Particle particle) {
		particle.image = getImage(particle.frameCount);
	}
	
	@Override
	public ParticleEventListener copy() {
		return new ParticleAnimationController(images, rate);
	}
	
	private PImage getImage(int frameCount) {
		int id = (frameCount/rate) % images.size();
		return images.get(id);
	}
}
