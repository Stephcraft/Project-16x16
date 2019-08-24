package ParticleSystem;

import ParticleSystem.emissions.*;
import processing.core.PImage;
import processing.core.PVector;
import sidescroller.SideScroller;

public class Particle {

	public PImage image;
	public PVector position;
	public PVector velocity;
	public PVector acceleration;
	public float size = 40; //TODO: create better way to control
	
	private SideScroller applet;
	
	private float maxLifespan;
	private float lifespan;
	private int frameCount;
	
	public Particle (SideScroller applet, PImage image) {
		this.applet = applet;
		this.image = image;
		frameCount = 0;
	}
	
	public void spawn(ParticleEmission emission, float lifespan) {
		position = emission.getPosition();
		velocity = emission.getVelocity();
		acceleration = emission.getAcceleration();
		maxLifespan = lifespan;
		this.lifespan = lifespan;
	}
	
	public void preLoad(int frames) {
		lifespan -= frames;
		frameCount = frames;
		if (isNotDead()) {
			position.add(positionDeltaIntegral(frames));
			velocity.add(acceleration.copy().mult(frames));
		}
	}
	
	public void run() {
		if (isNotDead()) {
			update();
			draw();
		}
	}
	
	public float getLifeSpanNormalized() {
		return lifespan/maxLifespan;
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	
	public boolean isDead() {
		if (lifespan <= 0.0)
			return true;
		else
			return false;
	}
	
	public boolean isNotDead() {
		return !isDead();
	}
	
	private void update() {
		velocity.add(acceleration);
		position.add(velocity);
		lifespan -= 1.0;
		frameCount++;
	}
	
	private void draw() {
		applet.image(image, position.x, position.y, size, size);
	}
	
	private PVector positionDeltaIntegral(int frames) {
		float deltaX = (float) (position.x + velocity.x*frames + 0.5*acceleration.x*frames*frames);
		float deltaY = (float) (position.y + velocity.y*frames + 0.5*acceleration.y*frames*frames);
		return new PVector(deltaX, deltaY);
	}
}
