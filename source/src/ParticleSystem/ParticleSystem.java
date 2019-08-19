package ParticleSystem;

import java.util.ArrayList;

import ParticleSystem.Emission.ParticleEmission;
import ParticleSystem.Modifier.ParticleModifier;
import processing.core.PImage;
import sidescroller.SideScroller;
import sidescroller.Tileset;

public class ParticleSystem {

	private static int FRAMERATE = 60;
	
	private SideScroller applet;
	private PImage image;
	
	private ParticleEmission emission;
	private ArrayList<Particle> particles;
	private ArrayList<ParticleModifier> modifiers;
	
	public int spawnRate = 1;
	public int spawnPerRate = 1;
	public int limit = 100000;
	private float lifeSpan;
	
	public boolean spawn = true;
	public boolean pause = false;
	
	public ParticleSystem(SideScroller applet, String imageName) {
		this.applet = applet;
		
		image = Tileset.getTile(imageName);
		modifiers = new ArrayList<ParticleModifier>();
	}
	
	public void addModifier(ParticleModifier modifier)
	{
		modifiers.add(modifier);
	}

	public boolean removeModifier(ParticleModifier modifier)
	{
		boolean hasModifier = modifiers.contains(modifier);
		if (hasModifier)
			modifiers.remove(modifier);
		
		return hasModifier;
	}
	
	public void setEmission(ParticleEmission emission, float lifeSpan)
	{
		this.emission = emission;
		this.lifeSpan = lifeSpan;
	}
	
	public void simulate(int spawnRate, int spawnPerRate, ParticleEmission emission, float lifeSpan) {
		
		setEmission(emission, lifeSpan);
		
		this.spawnRate = spawnRate;
		this.spawnPerRate = spawnPerRate;
		particles = new ArrayList<Particle>();
		limitParticles();
	}
	
	public void preLoad() {
		for(int i = 0; i < lifeSpan*FRAMERATE; i++) {
			if (i % (FRAMERATE/spawnRate) == 0) {
				ArrayList<Particle> newParticles = resetParticles(spawnPerRate);
				for(Particle particle : newParticles)
					particle.preLoad(i);
			}
		}
	}
	
	public void run() {
		if (!pause) {
			limitParticles();
			runParticles();
		}
		if (spawn && nextTick()) {
			resetParticles(spawnPerRate);
		}
	}

	private void runParticles()
	{
		for(Particle particle : particles) {
			for(ParticleModifier modifier : modifiers)
				modifier.update(particle);
			particle.run();
		}
	}
	
	private Particle addParticle() {
		Particle newParticle = new Particle(applet, image);
		particles.add(newParticle);
		return newParticle;
	}
	
	private void removeParticles(int amount) {
		int count = 0;
		ArrayList<Particle> temp = new ArrayList<Particle>();
		for(Particle particle : particles) {
			if (particle.isDead()) {
				temp.add(particle);
				count++;
			}
			if (count >= amount) break;
		}
		for(Particle particle : temp) {
			particles.remove(particle);
		}
	}
	
	private void resetParticle(Particle particle) {
		emission.generateNew();
		particle.spawn(emission, lifeSpan*FRAMERATE);
	}
	// Remove Extra Particles
	private void limitParticles()
	{
		int limitDelta = particles.size() - limit;
		if (limitDelta > 0)
		{
			removeParticles(limitDelta);
		}
	}

	private ArrayList<Particle> resetParticles(int amount) {
		int count = 0;
		ArrayList<Particle> resetParticles = new ArrayList<Particle>();
		for(Particle particle : particles)
		{
			if (particle.isDead())
			{
				resetParticle(particle);
				resetParticles.add(particle);
				count++;
			}
			if (count >= amount) return resetParticles;
		}
		int delta = amount - count;
		for(int i = 0; i < delta; i++)
		{
			Particle particle = addParticle();
			resetParticle(particle);
			resetParticles.add(particle);
		}	
		
		return resetParticles;
	}
	
	private boolean nextTick() {
		return applet.frameCount % (FRAMERATE/spawnRate) == 0;
	}
}
