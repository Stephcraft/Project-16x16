package ParticleSystem.Modifier;

import java.util.ArrayList;

import ParticleSystem.Particle;
import components.AnimationComponent;
import processing.core.PImage;
import sidescroller.Tileset;

public class ParticleAnimationModifier implements ParticleModifier {

	ArrayList<PImage> images;
	
	private int rate;
	
	public ParticleAnimationModifier(String animationName, int rate)
	{
		images = Tileset.getAnimation(animationName);
		this.rate = 4;
	}
	
	@Override
	public void update(Particle particle) {
		particle.image = getImage(particle.getFrameCount());
	}
	
	private PImage getImage(int frameCount) {
		int id = (frameCount/rate) % images.size();
		return images.get(id);
	}

	@Override
	public void onSpawn(Particle particle) {
	}
}
