package project_16x16.particleSystem;

import java.util.ArrayList;

import project_16x16.SideScroller;

/**
 * Particles
 * <p>
 * Keeps take of and runs all active and dead particles. Instead of create new
 * particles first checks if it can bring back dead particles;
 *
 * @author petturtle
 */
public class Particles {

	private SideScroller applet;
	private ParticleSystem particleSystem;

	public ArrayList<Particle> activeParticles;
	public ArrayList<Particle> inactiveParticles;

	public Particles(ParticleSystem particleSystem, SideScroller applet) {
		this.applet = applet;
		this.particleSystem = particleSystem;

		activeParticles = new ArrayList<Particle>();
		inactiveParticles = new ArrayList<Particle>();
	}

	public void run() {
		runParticles();
		if (nextTick()) {
			spawnParticles(particleSystem.spawnAmount);
		}

		particleSystem.onUpdateEvent();
	}

	public boolean hasActiveParticles() {
		return activeParticles.size() >= 1;
	}

	public Particle newParticle() {
		Particle particle = new Particle(applet, particleSystem.image);
		particle.spawn(particleSystem.getEmissionConsumer(), particleSystem.lifespan * ParticleSystem.FRAMERATE);
		particleSystem.onParticleSpawnEvent(particle);
		activeParticles.add(particle);
		return particle;
	}

	private void runParticles() {
		ArrayList<Particle> deadParticles = new ArrayList<Particle>();
		for (Particle particle : activeParticles) {
			particle.run();
			particleSystem.onParticleRunEvent(particle);
			if (particle.isDead()) {
				deadParticles.add(particle);
				particleSystem.onParticleDeathEvent(particle);
			}
		}
		activeParticles.removeAll(deadParticles);
		inactiveParticles.addAll(deadParticles);
	}

	private void spawnParticles(int spawnAmount) {
		int amount = spawnAmount;
		amount -= loopParticles(amount);
		for (int i = 0; i < amount; i++) {
			newParticle();
		}
	}

	private int loopParticles(int amount) {
		ArrayList<Particle> particles = new ArrayList<Particle>();
		for (Particle particle : inactiveParticles) {
			if (particle.isDead()) {
				respawnParticle(particle);
				particles.add(particle);
			}
			if (particles.size() >= amount) {
				break;
			}
		}
		inactiveParticles.removeAll(particles);
		activeParticles.addAll(particles);
		return particles.size();
	}

	private void respawnParticle(Particle particle) {
		particle.spawn(particleSystem.getEmissionConsumer(), particleSystem.lifespan * ParticleSystem.FRAMERATE);
		particleSystem.onParticleSpawnEvent(particle);
	}

	private boolean nextTick() {
		return particleSystem.spawn && applet.frameCount % (ParticleSystem.FRAMERATE / particleSystem.spawnRate) == 0;
	}
}
