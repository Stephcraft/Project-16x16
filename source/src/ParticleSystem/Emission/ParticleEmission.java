package ParticleSystem.Emission;

import processing.core.PVector;

public interface ParticleEmission {
	public void generateNew();
	public PVector getPosition();
	public PVector getVelocity();
	public PVector getAcceleration();
}
