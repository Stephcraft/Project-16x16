package project_16x16.particleSystem.emissions;

import java.util.function.Consumer;

import processing.core.PVector;
import project_16x16.particleSystem.Particle;

public interface ParticleEmission {
	public Consumer<Particle> getConsumer();

	public void setPosition(PVector position);

	public ParticleEmission copy();
}
