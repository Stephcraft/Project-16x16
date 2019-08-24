package ParticleSystem.emissions;

import java.util.Random;
import java.util.function.Consumer;

import ParticleSystem.Particle;
import processing.core.PVector;

/**
 * RectEmission
 * <p>
 * Spawns particles in a rect given center position, width, height.
 *
 * @author petturtle
 */
public class RectEmission implements ParticleEmission {

	private PVector position;
	private float velocity;
	private float acceleration;
	private int width;
	private int height;
	
	private PVector newPosition;
	private PVector newVelocity;
	private PVector newAcceleration;
	
	/**
     * Create a new AreaEmission.

     * @param position 	   PVector center position, set to a active entities PVector for the particle system to follow
     * @param velocity     Start velocity of particle in random direction;
     * @param acceleration Start acceleration of particle in random direction;
     * @param width		   width of rect
     * @param height	   height of rect
     */
	public RectEmission(PVector position, float velocity, float acceleration, int width, int height) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.width = width;
		this.height = height;
	}

	public void generateNew() {
		float phi = (float) (2*Math.PI*Math.random());
		newPosition();
		newVelocity(phi);
		newAcceleration(phi);
	}
	
	private void newPosition() {
		PVector p = position.copy();
		Random ran = new Random();
		p.x += (ran.nextFloat()*width/2f)-width/2;
		p.y += (ran.nextFloat()*height/2f)-height/2;
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
		return new  RectEmission(position, velocity, acceleration, width, height);
	}
}
