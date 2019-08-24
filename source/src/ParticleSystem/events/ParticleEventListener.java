package ParticleSystem.events;

import ParticleSystem.Particle;

public interface ParticleEventListener {
	
	default public void onUpdateEvent() {};
	
	default public void onParticleRunEvent(Particle particle) {};
	
	default public void onParticleSpawnEvent(Particle particle) {};
	
	default public void onParticleDeathEvent(Particle particle) {};
}
