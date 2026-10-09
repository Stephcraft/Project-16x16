package project_16x16.particleSystem;

import java.util.function.Consumer;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.SideScroller;
import project_16x16.Time;

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
	/** px/s */
	public PVector velocity;
	/** px/s² */
	public PVector acceleration;

	public float size = 40; // TODO: create better way to control
	public boolean useCustomeSize = false;

	public float maxLifespan; // lifespan of particle when it was spawned (seconds)
	public float lifespan; // remaining lifespan (seconds)
	public float age; // time since spawn (seconds)

	public Particle(SideScroller applet, PImage image) {
		this.applet = applet;
		this.image = image;
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
		final float dt = Math.min(Time.delta(), lifespan); // don't simulate beyond death
		position.add(velocity.x * dt + 0.5f * acceleration.x * dt * dt, velocity.y * dt + 0.5f * acceleration.y * dt * dt);
		velocity.add(acceleration.x * dt, acceleration.y * dt);
		lifespan -= dt;
		age += dt;
	}

	private void draw() {

		applet.pushMatrix();
		applet.translate(position.x, position.y);
		if (useCustomeSize) {
			applet.scale(size, size);
		}

		applet.image(image, 0, 0);
		applet.noTint();
		applet.popMatrix();
	}

	private void setLifespan(float lifespan) {
		maxLifespan = lifespan;
		this.lifespan = lifespan;
		age = 0;
	}
}
