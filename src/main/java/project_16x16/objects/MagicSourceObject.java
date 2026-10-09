package project_16x16.objects;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Time;
import project_16x16.Utility;
import project_16x16.particleSystem.ParticleSystem;
import project_16x16.particleSystem.emissions.AreaEmission;
import project_16x16.particleSystem.events.ParticleAnimationController;
import project_16x16.projectiles.MagicProjectile;
import project_16x16.projectiles.Swing;
import project_16x16.scene.GameplayScene;

/**
 * Extends {@link GameObject}.
 */
public class MagicSourceObject extends GameObject {

	private static ArrayList<PImage> particleAnimation;
	private ParticleSystem trail;

	public MagicSourceObject(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);

		type = ObjectType.OBJECT;
		id = "MAGIC_SOURCE";

		if (particleAnimation == null) {
			setParticleAnimation(sideScroller);
		}

		trail = new ParticleSystem(sideScroller, image, 5, 1, 0.4f);
		trail.setEmission(new AreaEmission(position, 60, -36, 5));
		trail.addEventListener(new ParticleAnimationController(particleAnimation, -1));

		width = 48;
		height = 48;

		position.y = -80;
	}

	@Override
	public void display() {
		trail.getEmission().setPosition(position);
		trail.run();
	}

	// lastShotTime is the game time (ms) of the previous shot.
	// shotDelay denotes the "fire rate" of the MagicSource in milliseconds.
	long lastShotTime = -shotDelay;
	static final int shotDelay = 500;

	@Override
	public void update() {
		// Create new Magic Projectiles
		for (int i = 0; i < gameplayScene.getPlayer().swings.size(); i++) {
			Swing swing = gameplayScene.getPlayer().swings.get(i);
			if (collidesWithSwing(swing)) {
				if (!swing.activated) {
					if (Time.millis() > lastShotTime + shotDelay) {
						lastShotTime = Time.millis();

						gameplayScene.projectileObjects.add(new MagicProjectile(applet, gameplayScene, (int) position.x, (int) position.y, swing.direction));

						swing.activated = true;
					}
				}
			}
		}
	}

	public boolean collidesWithSwing(Swing swing) {
		return (swing.position.x + swing.width / 2 > position.x - width / 2 && swing.position.x - swing.width / 2 < position.x + width / 2)
				&& (swing.position.y + swing.height / 2 > position.y - height / 2 && swing.position.y - swing.height / 2 < position.y + height / 2);
	}

	public boolean collidesWithPlayer() {
		return (gameplayScene.getPlayer().position.x + gameplayScene.getPlayer().width / 2 > position.x - width / 2
				&& gameplayScene.getPlayer().position.x - gameplayScene.getPlayer().width / 2 < position.x + width / 2)
				&& (gameplayScene.getPlayer().position.y + gameplayScene.getPlayer().height / 2 > position.y - height / 2
						&& gameplayScene.getPlayer().position.y - gameplayScene.getPlayer().height / 2 < position.y + height / 2);
	}

	private void setParticleAnimation(SideScroller a) {
		particleAnimation = new ArrayList<>();
		PImage image = Tileset.getTile("MAGIC_SOURCE");
		float scale = 0.12f;
		float angle = PApplet.radians(11);
		while (scale > 0.025f) {
			particleAnimation.add(Utility.resizeImage(Utility.resizeImage(Utility.rotateImage(image.copy(), angle), scale), 4));
			angle += PApplet.radians(PApplet.radians(11));
			scale -= Math.random() * 0.03;
		}
	}
}
