package game.objects;

import java.util.ArrayList;

import game.engine.ParticleSystem.ParticleSystem;
import game.engine.ParticleSystem.emissions.AreaEmission;
import game.engine.ParticleSystem.events.ParticleAnimationController;
import processing.core.PApplet;
import processing.core.PImage;
import game.projectiles.MagicProjectile;
import game.projectiles.Swing;
import game.engine.sidescroller.Tileset;
import game.engine.sidescroller.Util;
import processing.core.PVector;

/**
 * Extends {@link GameObject}.
 */
public class MagicSourceObject extends GameObject {

	private static ArrayList<PImage> particleAnimation;
	private ParticleSystem trail;

	public MagicSourceObject() {
		super();

		type = type.OBJECT;
		baseObjectData.setId("MAGIC_SOURCE");

		if (particleAnimation == null)
			setParticleAnimation();
		
		trail = new ParticleSystem(image, 5, 1, 0.4f);
		trail.setEmission(new AreaEmission(baseObjectData.getPos(), 1f, -0.01f, 5));
		trail.addEventListener(new ParticleAnimationController(particleAnimation, -1));

		baseObjectData.setWidth(48);
		baseObjectData.setHeight(48);

		baseObjectData.getPos().y = -80;
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
							.add(new MagicProjectile((int) baseObjectData.getPos().x, (int) baseObjectData.getPos().y, swing.direction));

						swing.activated = true;
					}
				}
			}
		}
	}

	public boolean collidesWithSwing(Swing swing) {
		PVector pos = baseObjectData.getPos();
		int width = baseObjectData.getWidth();
		int height = baseObjectData.getHeight();
		return (swing.pos.x + swing.width / 2 > pos.x - width / 2
				&& swing.pos.x - swing.width / 2 < pos.x + width / 2)
				&& (swing.pos.y + swing.height / 2 > pos.y - height / 2
						&& swing.pos.y - swing.height / 2 < pos.y + height / 2);
	}

	public boolean collidesWithPlayer() {
		PVector pos = baseObjectData.getPos();
		int width = baseObjectData.getWidth();
		int height = baseObjectData.getHeight();

		PVector playerPos = gameScene.getPlayer().getBaseObjectData().getPos();
		int playerHeight = gameScene.getPlayer().getBaseObjectData().getHeight();
		int playerWidth = gameScene.getPlayer().getBaseObjectData().getWidth();

		return (playerPos.x + playerWidth / 2 > pos.x - width / 2
				&& playerPos.x - playerWidth / 2 < pos.x + width / 2)
				&& (playerPos.y + playerHeight / 2 > pos.y
						- height / 2
						&& playerPos.y - playerHeight / 2 < pos.y
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
