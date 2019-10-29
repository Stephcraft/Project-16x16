package game.engine.ParticleSystem.emissions;

import java.util.function.Consumer;

import game.engine.ParticleSystem.Particle;
import processing.core.PVector;

public interface ParticleEmission {
	public Consumer<Particle> getConsumer();
	
	public void setPosition(PVector position);
	
	public ParticleEmission copy();
}
