package project_16x16.particleSystem;

import java.util.ArrayList;

import project_16x16.Main;

/**
 * Particles
 * <p>
 * Keeps take of and runs all active and dead particles.
 * Instead of create new particles first checks if it can bring back dead particles;
 *
 * @author petturtle
 */
public class Particles {

	private Main applet;
	private ParticleSystem particleSystem;
	
	public ArrayList<Particle> activeParticles;
	public ArrayList<Particle> inactiveParticles;
	
	public Particles(ParticleSystem particleSystem, Main applet) {
		this.applet = applet;
		this.particleSystem = particleSystem;
		
		activeParticles = new ArrayList<>();
		inactiveParticles = new ArrayList<>();
	}
	
	public void run() {
		runParticles();
		
		if (nextTick())
			spawnParticles(particleSystem.spawnAmount);
		
		particleSystem.onUpdateEvent();
	}
	
	public boolean hasActiveParticles() {
		return activeParticles.size() >= 1;
	}
	
	public Particle newParticle() {
		Particle p = new Particle(applet, particleSystem.image);
		p.spawn(particleSystem.getEmissionConsumer(), particleSystem.lifespan*ParticleSystem.FRAMERATE);
		particleSystem.onParticleSpawnEvent(p);
		activeParticles.add(p);
		return p;
	}
	
	private void runParticles() {
		ArrayList<Particle> deadParticles = new ArrayList<>();
		for(Particle p : activeParticles) {
			p.run();
			particleSystem.onParticleRunEvent(p);
			if (p.isDead()) {
				deadParticles.add(p);
				particleSystem.onParticleDeathEvent(p);
			}
		}
		activeParticles.removeAll(deadParticles);
		inactiveParticles.addAll(deadParticles);
	}
	
	private void spawnParticles(int spawnAmount) {
		int amount = spawnAmount;
		amount -= loopParticles(amount);
		for(int i = 0; i < amount; i++)
			newParticle();
	}
	
	private int loopParticles(int amount) {
		ArrayList<Particle> particles = new ArrayList<>();
		for(Particle particle : inactiveParticles) {
			if (particle.isDead()) {
				respawnParticle(particle);
				particles.add(particle);
			}
			if (particles.size() >= amount) break;
		}
		inactiveParticles.removeAll(particles);
		activeParticles.addAll(particles);
		return particles.size();
	}
	
	private void respawnParticle(Particle p) {
		p.spawn(particleSystem.getEmissionConsumer(), particleSystem.lifespan*ParticleSystem.FRAMERATE);
		particleSystem.onParticleSpawnEvent(p);
	}
	
	private boolean nextTick() {
		return particleSystem.spawn && applet.frameCount % (ParticleSystem.FRAMERATE/particleSystem.spawnRate) == 0;
	}
}
