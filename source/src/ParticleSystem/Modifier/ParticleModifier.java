package ParticleSystem.Modifier;

import ParticleSystem.Particle;

public interface ParticleModifier {
	public void update(Particle particle);
	
	public void onSpawn(Particle particle);
}
