package ParticleSystem.emissions;

import java.util.Random;

import processing.core.PVector;

public class DirectionalEmission implements ParticleEmission {

	private PVector position;
	private float velocity;
	private float acceleration;
	private float spread;
	private float angle;
	
	private PVector newPosition;
	private PVector newVelocity;
	private PVector newAcceleration;
	
	public DirectionalEmission(PVector position, float velocity, float acceleration, float spread, float angle) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.spread = spread;
		this.angle = angle;
	}
	
	@Override
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
