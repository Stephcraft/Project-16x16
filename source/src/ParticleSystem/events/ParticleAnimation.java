package ParticleSystem.events;

import java.util.ArrayList;

import ParticleSystem.Particle;
import components.AnimationComponent;
import processing.core.PImage;
import sidescroller.Tileset;

public class ParticleAnimation implements ParticleEventListener {

	ArrayList<PImage> images;
	
	private int rate;
	
	public ParticleAnimation(String animationName, int rate)
	{
		images = Tileset.getAnimation(animationName);
		this.rate = 4;
	}
	
	@Override
	public void onParticleRunEvent(Particle particle) {
		particle.image = getImage(particle.getFrameCount());
	}
	
	private PImage getImage(int frameCount) {
		int id = (frameCount/rate) % images.size();
		return images.get(id);
	}
}
