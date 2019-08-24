package ParticleSystem.events;

import ParticleSystem.Particle;
import processing.core.PVector;

public class ParticalVelocityController implements ParticleEventListener {

	PVector velocity;
	
	public ParticalVelocityController(PVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public void onParticleSpawnEvent(Particle particle) {
		particle.velocity.add(velocity);
	}
}
