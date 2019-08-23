package ParticleSystem;

import java.util.ArrayList;

import ParticleSystem.Emission.AreaEmission;
import ParticleSystem.Emission.ParticleEmission;
import ParticleSystem.Modifier.ParticleModifier;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import sidescroller.SideScroller;
import sidescroller.Tileset;

public class ParticleSystem {

	private static int FRAMERATE = 60;
	
	private SideScroller applet;
	private PImage image;
	
	private ParticleEmission emission;
	private ArrayList<Particle> activeParticles;
	private ArrayList<Particle> inactiveParticles;
	private ArrayList<ParticleModifier> modifiers;
	
	public int spawnRate;
	public int spawnAmount;
	public float lifespan;
	public boolean spawn = true;

	public ParticleSystem(SideScroller applet, String imageName, PVector position, int spawnRate, int spawnAmount, float lifespan) {
		this.applet = applet;
		image = Tileset.getTile(imageName);
		emission = new AreaEmission(position, 0, 0, 0);
		
		this.spawnRate = spawnRate;
		this.spawnAmount = spawnAmount;
		this.lifespan = lifespan;

		activeParticles = new ArrayList<Particle>();
		inactiveParticles = new ArrayList<Particle>();
		modifiers = new ArrayList<ParticleModifier>();
	}
	
	public void setEmission(ParticleEmission emission) {
		this.emission = emission;
	}
	
	public void addModifier(ParticleModifier modifier) {
		modifiers.add(modifier);
	}

	public boolean removeModifier(ParticleModifier modifier) {
		boolean hasModifier = modifiers.contains(modifier);
		if (hasModifier)
			modifiers.remove(modifier);
		
		return hasModifier;
	}
	
	public void preLoad() {
		for(int i = 0; i < lifespan*FRAMERATE; i++) {
			if (nextTick(i)) {
				ArrayList<Particle> newParticles = addParticles(spawnAmount);
				
				for(Particle particle : newParticles)
					particle.preLoad(i);
			}
		}
	}
	
	public void run() {
		runParticles();
		
		if (spawn && nextTick(applet.frameCount))
			spawnParticles();
	}

	private void runParticles()
	{
		ArrayList<Particle> deadParticles = new ArrayList<Particle>();
		for(Particle particle : activeParticles) {
			particle.run();
			
			for(ParticleModifier modifier : modifiers)
				modifier.update(particle);
			
			if (particle.isDead())
				deadParticles.add(particle);
		}
		activeParticles.removeAll(deadParticles);
		inactiveParticles.addAll(deadParticles);
	}
	
	private void spawnParticles() {
		int amount = spawnAmount;
		amount -= loopParticles(amount);
		if (amount > 0)
			addParticles(amount);
	}
	
	private int loopParticles(int amount) {
		ArrayList<Particle> particles = new ArrayList<Particle>();
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
	
	private Particle addParticle() {
		Particle particle = new Particle(applet, image);
		emission.generateNew();
		particle.spawn(emission, lifespan*FRAMERATE);
		
		for(ParticleModifier modifier : modifiers)
			modifier.onSpawn(particle);
		
		activeParticles.add(particle);
		return particle;
	}
	
	private ArrayList<Particle> addParticles(int amount) {
		ArrayList<Particle> newParticles = new ArrayList<Particle>();
		for(int i = 0; i < amount; i++) {
			Particle particle = addParticle();
			newParticles.add(particle);
		}
		return newParticles;
	}
	
	private void respawnParticle(Particle particle) {
		emission.generateNew();
		particle.spawn(emission, lifespan*FRAMERATE);
		
		for(ParticleModifier modifier : modifiers)
			modifier.onSpawn(particle);
	}
	
	private boolean nextTick(int frameCount) {
		return frameCount % (FRAMERATE/spawnRate) == 0;
	}
}
