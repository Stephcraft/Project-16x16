package ParticleSystem.events;

import ParticleSystem.Particle;
import processing.core.PApplet;

public class ParticleSizeController implements ParticleEventListener {

	private float startSize;
	private float endSize;
	
	public ParticleSizeController(float startSize, float endSize) {
		this.startSize = startSize;
		this.endSize = endSize;
	}
	
	@Override
	public void onParticleRunEvent(Particle particle) {	
		float size = particle.getLifeSpanNormalized()*(startSize-endSize)+endSize;
		particle.size = size;
	}
}
