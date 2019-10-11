package objects;

import java.util.ArrayList;

import ParticleSystem.ParticleSystem;
import ParticleSystem.emissions.AreaEmission;
import ParticleSystem.events.ParticleAnimationController;
import processing.core.PApplet;
import processing.core.PImage;
import projectiles.MagicProjectile;
import projectiles.Swing;
import scene.GameplayScene;
import sidescroller.Tileset;
import sidescroller.Util;

/**
 * Extends {@link GameObject}.
 */
public class MagicSourceObject extends GameObject {

	private static ArrayList<PImage> particleAnimation;
	private ParticleSystem trail;

	public MagicSourceObject(GameplayScene g) {
		super(g);

		type = type.OBJECT;
		id = "MAGIC_SOURCE";

		if (particleAnimation == null)
			setParticleAnimation();
		
		trail = new ParticleSystem(image, 5, 1, 0.4f);
		trail.setEmission(new AreaEmission(pos, 1f, -0.01f, 5));
		trail.addEventListener(new ParticleAnimationController(particleAnimation, -1));
		
		width = 48;
		height = 48;

		pos.y = -80;
	}

	@Override
	public void display() {
		trail.run();
	}
	
	//oldMillis is used to calculate the difference in time between shots.
	//shotDelay denotes the "fire rate" of the MagicSource in milliseconds.
	int oldMillis = 0;
	int shotDelay = 500;
	
	@Override
	public void update() {
		// Create new Magic Projectiles
		for (int i = 0; i < gameScene.getPlayer().swings.size(); i++) {
			Swing swing = gameScene.getPlayer().swings.get(i);

			if (collidesWithSwing(swing)) {
				if (!swing.activated) {
					
					if(applet.millis() > oldMillis + shotDelay) {
						oldMillis = applet.millis();
						
						gameScene.projectileObjects
							.add(new MagicProjectile(gameScene, (int) pos.x, (int) pos.y, swing.direction));

						swing.activated = true;
					}
				}
			}
		}
	}

	public boolean collidesWithSwing(Swing swing) {
		return (swing.pos.x + swing.width / 2 > pos.x - width / 2
				&& swing.pos.x - swing.width / 2 < pos.x + width / 2)
				&& (swing.pos.y + swing.height / 2 > pos.y - height / 2
						&& swing.pos.y - swing.height / 2 < pos.y + height / 2);
	}

	public boolean collidesWithPlayer() {
		return (gameScene.getPlayer().pos.x + gameScene.getPlayer().width / 2 > pos.x - width / 2
				&& gameScene.getPlayer().pos.x - gameScene.getPlayer().width / 2 < pos.x + width / 2)
				&& (gameScene.getPlayer().pos.y + gameScene.getPlayer().height / 2 > pos.y
						- height / 2
						&& gameScene.getPlayer().pos.y - gameScene.getPlayer().height / 2 < pos.y
								+ height / 2);
	}
	
	private void setParticleAnimation() {
		particleAnimation = new ArrayList<PImage>();
		PImage image = Tileset.getTile("MAGIC_SOURCE");
		float scale = 0.12f;
		float angle = PApplet.radians(11);
		while(scale > 0.025f) {
			particleAnimation.add(Util.pg(Util.resizeImage(Util.rotateImage(image.copy(), angle), scale),4));
			angle += PApplet.radians(PApplet.radians(11));
			scale -= Math.random() * 0.03;
		}
	}
}
