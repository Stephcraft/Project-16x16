package ParticleSystem.events;

import ParticleSystem.Particle;
import processing.core.PVector;

public class ParticalVelocityModifier implements ParticleEventListener {

	PVector velocity;
	
	public ParticalVelocityModifier(PVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public void onParticleSpawnEvent(Particle particle) {
		particle.velocity.add(velocity);
	}
}
