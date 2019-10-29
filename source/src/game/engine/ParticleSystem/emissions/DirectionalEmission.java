package game.engine.ParticleSystem.emissions;

import java.util.Random;
import java.util.function.Consumer;

import game.engine.ParticleSystem.Particle;
import processing.core.PVector;

/**
 * AreaEmission
 * <p>
 * Emits particles in a direction given angle (radians).
 * 0 is to the left, PI/2 is down.
 *
 * @author petturtle
 */
public class DirectionalEmission implements ParticleEmission {

	private PVector position;
	private float velocity;
	private float acceleration;
	private float spread;
	private float angle;
	
	private PVector newPosition;
	private PVector newVelocity;
	private PVector newAcceleration;
	
	/**
     * Create a new DirectionalEmission.

     * @param position 	   PVector position, set to a active game.entities PVector for the particle system to follow
     * @param velocity     Start velocity of particle in random direction;
     * @param acceleration Start acceleration of particle in random direction;
     * @param spread	   Deviation from spawn position
     * @param angle		   direction angle (radians)
     */
	public DirectionalEmission(PVector position, float velocity, float acceleration, float spread, float angle) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.spread = spread;
		this.angle = angle;
	}
	
	public void generateNew() {
		newPosition();
		newVelocity();
		newAcceleration();
	}
	
	private void newPosition() {
		PVector p = position.copy();
		Random ran = new Random();
		float offset = (ran.nextFloat()*spread*2f)-spread;
		p.x += (float) (offset*Math.cos(angle+Math.PI/2));
		p.y += (float) (offset*Math.sin(angle+Math.PI/2));
		newPosition = p;
	}

	private void newVelocity() {
		newVelocity = new PVector();
		newVelocity.x = (float) (velocity*Math.cos(angle));
		newVelocity.y = (float) (velocity*Math.sin(angle));
	}

	private void newAcceleration() {
		newAcceleration = new PVector();
		newAcceleration.x = (float) (acceleration*Math.cos(angle));
		newAcceleration.y = (float) (acceleration*Math.sin(angle));
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
		return new  DirectionalEmission(position, velocity, acceleration, spread, angle);
	}
}
