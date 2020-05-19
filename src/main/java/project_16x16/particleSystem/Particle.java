package project_16x16.particleSystem;

import java.util.function.Consumer;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.Main;

/**
 * Particle
 * <p>
 * Can change any public variable on runtime.
 *
 * @author petturtle
 */
public class Particle {

	private Main applet;
	
	public PImage image;
	public PVector position;
	public PVector velocity;
	public PVector acceleration;
	
	public float size = 40; //TODO: create better way to control
	public boolean useCustomeSize = false;
	
	public float maxLifespan; // lifespan of particle when it was spawned
	public float lifespan;
	public int frameCount;
	
	public Particle (Main applet, PImage image) {
		this.applet = applet;
		this.image = image;
		frameCount = 0;
	}
	
	public void spawn(Consumer<Particle> consumer, float lifespan) {
		consumer.accept(this);
		setLifespan(lifespan);
	}
	
	public boolean isDead() {
		return lifespan <= 0.0;
	}
	
	public void run() {
		if (!isDead()) {
			update();
			draw();
		}
	}
	
	private void update() {
		velocity.add(acceleration);
		position.add(velocity);
		lifespan -= 1.0;
		frameCount++;
	}
	
	private void draw() {
		
		applet.pushMatrix();
		applet.translate(position.x, position.y);
		if (useCustomeSize)
			applet.scale(size, size);
		
		applet.image(image, 0, 0);
		applet.noTint();
		applet.popMatrix();
	}
	
	private void setLifespan(float lifespan) {
		maxLifespan = lifespan;
		this.lifespan = lifespan;
	}
}
