package org.project16x16.ParticleSystem.emissions;

import java.util.function.Consumer;

import org.project16x16.ParticleSystem.Particle;
import processing.core.PVector;

public interface ParticleEmission {
	public Consumer<Particle> getConsumer();
	
	public void setPosition(PVector position);
	
	public ParticleEmission copy();
}
