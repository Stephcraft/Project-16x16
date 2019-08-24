package ParticleSystem;

import java.util.ArrayList;

import ParticleSystem.emissions.*;
import ParticleSystem.events.*;
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
	private ArrayList<ParticleEventListener> listeners;
	
	
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
		listeners = new ArrayList<ParticleEventListener>();
	}
	
	public void setEmission(ParticleEmission emission) {
		this.emission = emission;
	}
	
	public void addEvent(ParticleEventListener modifier) {
		listeners.add(modifier);
	}

	public boolean removeEvent(ParticleEventListener modifier) {
		boolean hasModifier = listeners.contains(modifier);
		if (hasModifier)
			listeners.remove(modifier);
		
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
		
		listeners.forEach(l -> l.onUpdateEvent());
	}

	private void runParticles()
	{
		ArrayList<Particle> deadParticles = new ArrayList<Particle>();
		for(Particle p : activeParticles) {
			p.run();
			
			listeners.forEach(l -> l.onParticleRunEvent(p));
			
			if (p.isDead()) {
				deadParticles.add(p);
				listeners.forEach(l -> l.onParticleDeathEvent(p));
			}
				
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
	
	private Particle newParticle() {
		Particle p = new Particle(applet, image);
		emission.generateNew();
		p.spawn(emission, lifespan*FRAMERATE);
		
		listeners.forEach(l -> l.onParticleSpawnEvent(p));
		activeParticles.add(p);
		return p;
	}
	
	private ArrayList<Particle> addParticles(int amount) {
		ArrayList<Particle> particles = new ArrayList<Particle>();
		for(int i = 0; i < amount; i++) {
			Particle p = newParticle();
			particles.add(p);
		}
		return particles;
	}
	
	private void respawnParticle(Particle particle) {
		emission.generateNew();
		particle.spawn(emission, lifespan*FRAMERATE);
		
		listeners.forEach(l -> l.onParticleSpawnEvent(particle));
	}
	
	private boolean nextTick(int frameCount) {
		return frameCount % (FRAMERATE/spawnRate) == 0;
	}
}
