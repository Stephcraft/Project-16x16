package project_16x16.particleSystem.events;

import processing.core.PVector;
import project_16x16.particleSystem.Particle;

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
