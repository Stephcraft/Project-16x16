package ParticleSystem.emissions;

import java.util.Random;

import processing.core.PVector;

public class RectEmission implements ParticleEmission {

	private PVector position;
	private float velocity;
	private float acceleration;
	private int width;
	private int height;
	
	private PVector newPosition;
	private PVector newVelocity;
	private PVector newAcceleration;
	
	public RectEmission(PVector position, float velocity, float acceleration, int width, int height) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.width = width;
		this.height = height;
	}
	
	@Override
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
	public PVector getPosition() {
		return newPosition;
	}

	@Override
	public PVector getVelocity() {
		return newVelocity;
	}

	@Override
	public PVector getAcceleration() {
		return newAcceleration;
	}
}
