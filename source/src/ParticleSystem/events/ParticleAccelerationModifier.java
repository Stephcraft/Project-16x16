package ParticleSystem.events;

import ParticleSystem.Particle;
import processing.core.PVector;

public class ParticleAccelerationModifier implements ParticleEventListener {

	PVector acceleration;
	
	public ParticleAccelerationModifier(PVector acceleration) {
		this.acceleration = acceleration;
	}
	
	@Override
	public void onParticleSpawnEvent(Particle particle) {
		particle.acceleration.add(acceleration);
	}
}
