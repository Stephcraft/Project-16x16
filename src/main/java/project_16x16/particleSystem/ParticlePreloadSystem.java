package project_16x16.particleSystem;

import java.util.function.Consumer;

import processing.core.PVector;

/**
 * Particle Preload System
 * <p>
 * Preloads the particles position, velocity, lifespan and age. It only
 * takes into account the particles spawn position, velocity and acceleration.
 * Runtime changes like collision and outside forces will not be taken into
 * affect.
 *
 * @author petturtle
 */
public class ParticlePreloadSystem {

	/**
	 * @param seconds how long the particle should appear to have been alive for
	 */
	public static Consumer<Particle> preload(float seconds) {
		return p -> {
			p.lifespan -= seconds;
			p.age = seconds;
			if (!p.isDead()) {
				p.position.add(positionDelta(p, seconds));
				p.velocity.add(PVector.mult(p.acceleration, seconds));
			}
		};
	}

	private static PVector positionDelta(Particle particle, float t) {
		float deltaX = particle.velocity.x * t + 0.5f * particle.acceleration.x * t * t;
		float deltaY = particle.velocity.y * t + 0.5f * particle.acceleration.y * t * t;
		return new PVector(deltaX, deltaY);
	}
}
