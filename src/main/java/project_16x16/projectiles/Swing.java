package project_16x16.projectiles;

import java.util.ArrayList;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Time;
import project_16x16.components.AnimationComponent;
import project_16x16.scene.GameplayScene;

public class Swing extends ProjectileObject { // PClass

	AnimationComponent animation;

	public boolean activated;

	/** How long the swing's hitbox exists (ms). */
	private static final float LIFETIME_MILLIS = 200;
	private float age = 0; // ms

	public Swing(SideScroller sideScroller, GameplayScene gameplayScene, int x, int y, int dir) {
		super(sideScroller, gameplayScene);
		animation = new AnimationComponent();

		direction = dir;

		switch (direction) {
			case LEFT:
				position = new PVector(x - 60, y);
				break;
			case RIGHT:
				position = new PVector(x + 60, y);
				break;
		}

		width = 28 * 4;
		height = 9 * 4;

		// Setup Animation (spans the swing's lifetime)
		ArrayList<PImage> frames = Tileset.getAnimation("Swing");
		animation.changeAnimation(frames, false, LIFETIME_MILLIS / frames.size());
		image = animation.getFrame();
	}

	@Override
	public void display() {
		applet.pushMatrix();
		applet.translate(position.x, position.y);
		if (direction == LEFT) {
			applet.scale(-1, 1);
		}
		image = animation.getFrame();
		applet.image(image, 0, 0);
		applet.popMatrix();
	}

	@Override
	public void update() {
		age += Time.deltaMillis();
		animation.update();
	}

	/**
	 * @return whether the swing has finished and should be removed
	 */
	public boolean expired() {
		return age >= LIFETIME_MILLIS;
	}
}
