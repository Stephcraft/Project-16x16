package ParticleSystem.Modifier;

import ParticleSystem.Particle;
import processing.core.PApplet;

public class ParticleSizeModifier implements ParticleModifier {

	private float startSize;
	private float endSize;
	
	public ParticleSizeModifier(float startSize, float endSize) {
		this.startSize = startSize;
		this.endSize = endSize;
	}
	
	@Override
	public void update(Particle particle) {	
		float size = particle.getLifeSpanNormalized()*(startSize-endSize)+endSize;
		particle.size = size;
	}

	@Override
	public void onSpawn(Particle particle) {
		// TODO Auto-generated method stub
		
	}
}
