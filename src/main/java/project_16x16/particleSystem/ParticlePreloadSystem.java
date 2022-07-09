package project_16x16.particleSystem;

import java.util.function.Consumer;

import processing.core.PVector;

/**
 * Particle Preload System
 * <p>
 * Preloads the particles position, velocity, lifespan and frameCount.
 * It only takes into account the particles spawn position, velocity and acceleration.
 * Runtime changes like collision and outside forces will not be taken into affect.
 *
 * @author petturtle
 */
public class ParticlePreloadSystem {

	public static Consumer<Particle> preload(int frames) {
		return p -> {
			p.lifespan -= frames;
			p.frameCount = frames;
			if (!p.isDead()) {
				p.position.add(positionDeltaIntegral(p, frames));
				p.velocity.add(p.acceleration.copy().mult(frames));
			}
		};
	}
	
	private static PVector positionDeltaIntegral(Particle particle, int frames) {
		float deltaX = (float) (particle.position.x + particle.velocity.x*frames + 0.5*particle.acceleration.x*frames*frames);
		float deltaY = (float) (particle.position.y + particle.velocity.y*frames + 0.5*particle.acceleration.y*frames*frames);
		return new PVector(deltaX, deltaY);
	}
}
