package project_16x16.projectiles;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Util;
import project_16x16.ParticleSystem.ParticleSystem;
import project_16x16.ParticleSystem.emissions.AreaEmission;
import project_16x16.ParticleSystem.events.ParticleAnimationController;
import project_16x16.ParticleSystem.events.ParticleNoLoopController;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.scene.GameplayScene;

public class MagicProjectile extends ProjectileObject {

	final int SCALE = 4;
	final int PROJECTILE_SPEED = 10;
	final int PROJECTILE_IDLE_SIZE = 8;
	
	private static ArrayList<PImage> particleAnimation;
	private ParticleSystem trail;
	private ParticleSystem explode;
	
	public MagicProjectile(SideScroller a, GameplayScene g, int x, int y, int dir) {
		super(a, g);
		
		id = "MAGIC";
		pos = new PVector(x, y);
		direction = dir;
		prevDirection = dir; // Used for tracking the prevDirection of the projectile
		
		speed = PROJECTILE_SPEED;
		width = PROJECTILE_IDLE_SIZE * SCALE;
		height = PROJECTILE_IDLE_SIZE * SCALE;
		
		if (particleAnimation == null)
			setParticleAnimation(a);

		trail = new ParticleSystem(a, image, 40, 1, 0.2f);
		trail.setEmission(new AreaEmission(pos, 0.8f, -0.01f, 8));
		trail.addEventListener(new ParticleAnimationController(particleAnimation, -1));
		
		explode = new ParticleSystem(a, image, 15, 5, 0.4f);
		explode.setEmission(new AreaEmission(pos, 3, -0.13f, 10));
		explode.addEventListener(new ParticleAnimationController(particleAnimation, -1));
		explode.addEventListener(new ParticleNoLoopController(10));
	}
	
	@Override
	public void debug() {
		applet.stroke(255,0,0);
		applet.noFill();
		if (direction == LEFT || direction == RIGHT)
			applet.rect(pos.x, pos.y, width, height);
		else
			applet.rect(pos.x, pos.y, height, width);
	}

	public void update() {	
		trail.run();
		if (hit)
			explode.run();
		else {
			moveProjectile();
//			destroyProjectile();
		}
	}
	
	public void destroyProjectile() {
		for (EditableObject o : gameScene.objects) {
			if (o instanceof CollidableObject) {
				CollidableObject collidable = (CollidableObject) o;
				if (collides(collidable) && collidable.flag.equals("TRANSPARENT_BULLET")) {
					hit = true;
					trail.spawn = false;
				}
			}
		}
	}

	public void moveProjectile() {
		switch (direction) {
		case LEFT:
			pos.x -= speed;
			break;
		case RIGHT:
			pos.x += speed;
			break;
		case UP:
			pos.y -= speed;
			break;
		case DOWN:
			pos.y += speed;
			break;
		}
	}

	public void hit(CollidableObject collision) {
		hit = true;
		trail.spawn = false;
	}
	
	private void setParticleAnimation(SideScroller a) {
		particleAnimation = new ArrayList<PImage>();
		PImage image = Tileset.getTile("MAGIC_SOURCE");
		float angle = PApplet.radians((float) (Math.random() * 360));
		particleAnimation.add(Util.resizeImage(Util.rotateImage(image.copy(), angle), 0.25f));
	}

}
