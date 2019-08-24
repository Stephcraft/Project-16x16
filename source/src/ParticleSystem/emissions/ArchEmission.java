package ParticleSystem.emissions;

import java.util.Random;

import processing.core.PVector;

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
	
	public ArchEmission(PVector position, float velocity, float acceleration, float spread, float minAngle, float maxAngle) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.spread = spread;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}
	
	@Override
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
