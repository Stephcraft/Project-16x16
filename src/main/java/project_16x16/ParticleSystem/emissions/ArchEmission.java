package project_16x16.ParticleSystem.emissions;

import java.util.Random;
import java.util.function.Consumer;

import project_16x16.ParticleSystem.Particle;
import processing.core.PVector;

/**
 * Arch Emission
 * <p>
 * Emits particles in an arch given min and max angles (radians).
 * 0 is to the left, PI/2 is down.
 *
 * @author petturtle
 */
public class ArchEmission implements ParticleEmission {

	private PVector position;
	private float velocity;
	private float acceleration;
	private float spread;
	private float minAngle;
	private float maxAngle;
	
	private PVector newPosition;
	private PVector newVelocity;
	private PVector newAcceleration;
	
	/**
     * Create a new ArchEmission.

     * @param position 	   PVector position, set to a active entities PVector for the particle system to follow
     * @param velocity     Start velocity of particle in the arch direction;
     * @param acceleration Start acceleration of particle in the arch direction;
     * @param spread	   Deviation from spawn position
     * @param minAngle	   minAngle (radians)
     * @param maxAngle	   maxAngle (radians)
     */
	public ArchEmission(PVector position, float velocity, float acceleration, float spread, float minAngle, float maxAngle) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.spread = spread;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}
	
	public void generateNew() {
		Random ran = new Random();
		float phi = ran.nextFloat()*(maxAngle-minAngle)+minAngle;
		newPosition();
		newVelocity(phi);
		newAcceleration(phi);
	}
	
	private void newPosition() {
		PVector p = position.copy();
		Random ran = new Random();
		p.x += (ran.nextFloat()*spread*2f)-spread;
		p.y += (ran.nextFloat()*spread*2f)-spread;
		newPosition = p;
	}

	private void newVelocity(float phi) {
		newVelocity = new PVector();
		newVelocity.x = (float) (velocity*Math.cos(phi));
		newVelocity.y = (float) (velocity*Math.sin(phi));
	}

	private void newAcceleration(float phi) {
		newAcceleration = new PVector();
		newAcceleration.x = (float) (acceleration*Math.cos(phi));
		newAcceleration.y = (float) (acceleration*Math.sin(phi));
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
		return new ArchEmission(position, velocity, acceleration, spread, minAngle, maxAngle);
	}
}
