package ParticleSystem.Emission;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;

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
	
	public RotationEmission(PVector position, float velocity, float acceleration, float spread, float div) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.spread = spread;
		this.div = div;
	}

	@Override
	public void generateNew() {
		phi += div;
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
