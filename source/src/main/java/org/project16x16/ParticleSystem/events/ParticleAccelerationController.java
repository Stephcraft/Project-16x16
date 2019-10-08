package org.project16x16.ParticleSystem.events;

import org.project16x16.ParticleSystem.Particle;
import processing.core.PVector;

/**
 * Partical Acceleration Controller
 * <p>
 * Adds acceleration to a particle on spawn.
 * Useful is using a emission that only adds random or directional acceleration.
 *
 * @author petturtle
 */
public class ParticleAccelerationController implements ParticleEventListener {

	PVector acceleration;
	
	/**
	 * Adds acceleration to a particle on spawn.
	 * Useful is using a emission that only adds random or directional acceleration.
	 *
	 * @param acceleration PVector acceleration
	 */
	public ParticleAccelerationController(PVector acceleration) {
		this.acceleration = acceleration;
	}
	
	@Override
	public void onParticleSpawnEvent(Particle particle) {
		particle.acceleration.add(acceleration);
	}
	
	@Override
	public ParticleEventListener copy() {
		return new ParticleAccelerationController(acceleration);
	}
}
