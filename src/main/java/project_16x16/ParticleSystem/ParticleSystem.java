package project_16x16.ParticleSystem;

import java.util.ArrayList;
import java.util.function.Consumer;

import project_16x16.ParticleSystem.emissions.*;
import project_16x16.ParticleSystem.events.*;
import processing.core.PImage;
import processing.core.PVector;
import project_16x16.SideScroller;
import project_16x16.Tileset;

/**
 * Particle System
 * <p>
 * To create a custom emission {@link ParticleEmission} can the position, velocity and acceleration for each particle.
 * To create a custom Controller {@link ParticleEventListener} can control almost any part of the particle.
 *
 * @author petturtle
 */
public class ParticleSystem {

	public static final int FRAMERATE = 60;
	
	private SideScroller applet;
	public PImage image;
	public ParticleEmission emission;
	public Particles particles;
	
	private ArrayList<ParticleEventListener> listeners = new ArrayList<ParticleEventListener>();
	
	public int spawnRate;
	public int spawnAmount;
	public float lifespan;
	public boolean spawn = true;

	/**
     * Create a new particle system.
     * Set emission with setEmission() for a different effect, default provided
     * 
     * @param applet 	   SideScroller
     * @param imageName    particles image name
     * @param spawnRate    How many times a second will particles be spawned
     * @param spawnAmount  How many particles will be spawned
     * @param lifespan     How long will the particle be displayed (seconds)
     */
	public ParticleSystem(SideScroller applet, String imageName, int spawnRate, int spawnAmount, float lifespan) {
		this(applet, Tileset.getTile(imageName), spawnRate, spawnAmount, lifespan);
	}
	
	/**
     * Create a new particle system.
     * Set emission with setEmission() for a different effect, default provided
     * 
     * @param applet 	   SideScroller
     * @param image        image of particle
     * @param spawnRate    How many times a second will particles be spawned
     * @param spawnAmount  How many particles will be spawned
     * @param lifespan     How long will the particle be displayed (seconds)
     */
	public ParticleSystem(SideScroller applet, PImage image, int spawnRate, int spawnAmount, float lifespan) {
		this.applet = applet;
		this.spawnRate = spawnRate;
		this.spawnAmount = spawnAmount;
		this.lifespan = lifespan;
		this.image = image;
		
		emission = new AreaEmission(new PVector(0,0), 1, 1, 0);
		particles = new Particles(this, applet);
	}
	
	public void run() {
		particles.run();
	}
	
	public void preLoad() {
		for(int i = 0; i < lifespan*FRAMERATE; i+=FRAMERATE/spawnRate)
			for(int k = 0; k < spawnAmount; k++)
					ParticlePreloadSystem.preload(i).accept(particles.newParticle());
	}
	
	public ParticleSystem copy() {
		ParticleSystem copy = new ParticleSystem(applet, image, spawnRate, spawnAmount, lifespan);
		copy.setEmission(emission.copy());
		for(ParticleEventListener mod : listeners)
			copy.addEventListener(mod.copy());
		return copy;
	}
	
	public void setEmission(ParticleEmission emission) {
		this.emission = emission;
	}
	public ParticleEmission getEmission(){ return emission; }
	
	public Consumer<Particle> getEmissionConsumer() {
		return emission.getConsumer();
	}
	
	public void addEventListener(ParticleEventListener modifier) {
		modifier.onCreateEvent(this);
		listeners.add(modifier);
	}

	public boolean removeEventListener(ParticleEventListener modifier) {
		return listeners.remove(modifier);
	}
	
	public void onUpdateEvent() {
		listeners.forEach(l -> l.onUpdateEvent());
	}
	
	public void onParticleRunEvent(Particle particle) {
		listeners.forEach(l -> l.onParticleRunEvent(particle));
	}
	
	public void onParticleSpawnEvent(Particle particle) {
		listeners.forEach(l -> l.onParticleSpawnEvent(particle));
	}
	
	public void onParticleDeathEvent(Particle particle) {
		listeners.forEach(l -> l.onParticleDeathEvent(particle));
	}
}
