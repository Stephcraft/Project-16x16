package game.engine.ParticleSystem.events;

import game.engine.ParticleSystem.*;

public interface ParticleEventListener {
	
	default public void onCreateEvent(ParticleSystem particleSystem) {};
	
	default public void onUpdateEvent() {};
	
	default public void onParticleRunEvent(Particle particle) {};
	
	default public void onParticleSpawnEvent(Particle particle) {};
	
	default public void onParticleDeathEvent(Particle particle) {};
	
	public ParticleEventListener copy();
}
