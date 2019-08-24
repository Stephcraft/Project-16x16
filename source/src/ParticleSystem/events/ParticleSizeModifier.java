package ParticleSystem.events;

import ParticleSystem.Particle;
import processing.core.PApplet;

public class ParticleSizeModifier implements ParticleEventListener {

	private float startSize;
	private float endSize;
	
	public ParticleSizeModifier(float startSize, float endSize) {
		this.startSize = startSize;
		this.endSize = endSize;
	}
	
	@Override
	public void onParticleRunEvent(Particle particle) {	
		float size = particle.getLifeSpanNormalized()*(startSize-endSize)+endSize;
		particle.size = size;
	}
}
