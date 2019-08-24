package ParticleSystem.events;

import ParticleSystem.Particle;
import processing.core.PVector;

public class ParticleAccelerationController implements ParticleEventListener {

	PVector acceleration;
	
	public ParticleAccelerationController(PVector acceleration) {
		this.acceleration = acceleration;
	}
	
	@Override
	public void onParticleSpawnEvent(Particle particle) {
		particle.acceleration.add(acceleration);
	}
}
