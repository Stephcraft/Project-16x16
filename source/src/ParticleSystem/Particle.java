package ParticleSystem;

import java.util.function.Consumer;

import processing.core.PImage;
import processing.core.PVector;
import sidescroller.SideScroller;

/**
 * Particle
 * <p>
 * Can change any public variable on runtime.
 *
 * @author petturtle
 */
public class Particle {

	private SideScroller applet;
	
	public PImage image;
	public PVector position;
	public PVector velocity;
	public PVector acceleration;
	
	public float size = 40; //TODO: create better way to control
	public float maxLifespan; // lifespan of particle when it was spawned
	public float lifespan;
	public int frameCount;
	
	public Particle (SideScroller applet, PImage image) {
		this.applet = applet;
		this.image = image;
		frameCount = 0;
	}
	
	public void spawn(Consumer<Particle> consumer, float lifespan) {
		consumer.accept(this);
		setLifespan(lifespan);
	}
	
	public boolean isDead() {
		if (lifespan <= 0.0)
			return true;
		else
			return false;
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
		applet.image(image, position.x, position.y, size, size);
	}
	
	private void setLifespan(float lifespan)
	{
		maxLifespan = lifespan;
		this.lifespan = lifespan;
	}
}
