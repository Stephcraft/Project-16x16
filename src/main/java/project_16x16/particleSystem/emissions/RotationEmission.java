package project_16x16.particleSystem.emissions;

import java.util.Random;
import java.util.function.Consumer;

import processing.core.PVector;
import project_16x16.particleSystem.Particle;

/**
 * RotationEmission
 * <p>
 * A experimental emission (could change) Emits particles in a direction which
 * increase for each particle. 0 is to the left, PI/2 is down.
 *
 * @author petturtle
 */
public class RotationEmission implements ParticleEmission {

	private PVector position;
	private float velocity;
	private float acceleration;
	private float spread;
	private float div;
	private float phi;

	private PVector newPosition;
	private PVector newVelocity;
	private PVector newAcceleration;

	/**
	 * Create a new RotationEmission.
	 *
	 * @param position     PVector position, set to a active entities PVector for
	 *                     the particle system to follow
	 * @param velocity     Start velocity of particle in facing direction.
	 * @param acceleration Start acceleration of particle in facing direction.
	 * @param spread       Deviation from spawn position
	 * @param div          Angle increase for each particle (radians)
	 */
	public RotationEmission(PVector position, float velocity, float acceleration, float spread, float div) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.spread = spread;
		this.div = div;
	}

	public void generateNew() {
		phi += div;
		newPosition();
		newVelocity(phi);
		newAcceleration(phi);
	}

	private void newPosition() {
		PVector p = position.copy();
		Random ran = new Random();
		p.x += (ran.nextFloat() * spread * 2f) - spread;
		p.y += (ran.nextFloat() * spread * 2f) - spread;
		newPosition = p;
	}

	private void newVelocity(float phi) {
		newVelocity = new PVector();
		newVelocity.x = (float) (velocity * Math.cos(phi));
		newVelocity.y = (float) (velocity * Math.sin(phi));
	}

	private void newAcceleration(float phi) {
		newAcceleration = new PVector();
		newAcceleration.x = (float) (acceleration * Math.cos(phi));
		newAcceleration.y = (float) (acceleration * Math.sin(phi));
	}

	@Override
	public Consumer<Particle> getConsumer() {
		return p -> {
			generateNew();
			p.position = newPosition;
			p.velocity = newVelocity;
			p.acceleration = newAcceleration;
		};
	}

	@Override
	public void setPosition(PVector position) {
		this.position = position;
	}

	@Override
	public ParticleEmission copy() {
		return new RotationEmission(position, velocity, acceleration, spread, div);
	}
}
