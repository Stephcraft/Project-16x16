package project_16x16.ParticleSystem;

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
	
	private static PVector positionDeltaIntegral(Particle p, int frames) {
		float deltaX = (float) (p.position.x + p.velocity.x*frames + 0.5*p.acceleration.x*frames*frames);
		float deltaY = (float) (p.position.y + p.velocity.y*frames + 0.5*p.acceleration.y*frames*frames);
		return new PVector(deltaX, deltaY);
	}
}
