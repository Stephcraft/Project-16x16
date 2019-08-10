package projectiles;

import objects.CollidableObject;
import processing.core.PApplet;
import processing.core.PVector;
import sidescroller.SideScroller;

public class MagicProjectile extends ProjectileObject {

	public MagicProjectile(SideScroller a, int x, int y, int dir) {
		super(a);

		id = "MAGIC";

		pos = new PVector(x, y);

		direction = dir;
		
		animation.changeAnimation(getAnimation("MAGIC::MOVE"), true, 4); // Setup Animation

		speed = 10;

		width = 22 * 4;
		height = 10 * 4;
	}

	@Override
	public void display() {
		switch (direction) {
			case LEFT :
				applet.pushMatrix();
				applet.translate(pos.x, pos.y);
				applet.scale(-1, 1);
				applet.image(image, 0, 0);
				applet.popMatrix();
				break;
			case RIGHT :
				applet.image(image, pos.x, pos.y);
				break;
			case UP :
				applet.pushMatrix();
				applet.translate(pos.x, pos.y);
				applet.rotate(PApplet.radians(-90));
				applet.image(image, 0, 0);
				applet.popMatrix();
				break;
			case DOWN :
				applet.pushMatrix();
				applet.translate(pos.x, pos.y);
				applet.rotate(PApplet.radians(90));
				applet.image(image, 0, 0);
				applet.popMatrix();
				break;
		}
		applet.stroke(255);
		applet.noFill();
		applet.rect(pos.x, pos.y, width, height);
	}

	@Override
	public void update() {
		image = animation.animate();

		// Destroy on collide
		if (!hit) {
			for (int i = 0; i < applet.collidableObjects.size(); i++) {
				CollidableObject collision = applet.collidableObjects.get(i);

				if (collides(collision) && !collision.flag.equals("TRANSPARENT_BULLET")) {
					hit = true;

					width = 8 * 4;
					height = 8 * 4;

					// Realistic Colliding
					if (pos.x < collision.pos.x) {
						pos.x = collision.pos.x - collision.width / 2;
					} else if (pos.x > collision.pos.x) {
						pos.x = collision.pos.x + collision.width / 2;
					} else if (pos.y + height / 2 < collision.pos.y) {
						pos.y = collision.pos.y - collision.height / 2 - height / 2;
					} else if (pos.y > collision.pos.y) {
						pos.y = collision.pos.y + collision.height / 2 + height / 2;
					}

					// Setup Animation
					animation.changeAnimation(getAnimation("MAGIC::IDLE"), true, 4);

					// Override Animation
					image = animation.animate();

				}
			}
		}

		// Move Projectile
		if (!hit) {
			switch (direction) {
				case LEFT :
					pos.x -= speed;

					width = 22 * 4;
					height = 10 * 4;
					break;
				case RIGHT :
					pos.x += speed;

					width = 22 * 4;
					height = 10 * 4;
					break;
				case UP :
					pos.y -= speed;

					width = 10 * 4;
					height = 22 * 4;
					break;
				case DOWN :
					pos.y += speed;

					width = 10 * 4;
					height = 22 * 4;
					break;
			}
		}
	}

	public void hit(CollidableObject collision) {
		hit = true;

		width = 8 * 4;
		height = 8 * 4;

		// Realistic Colliding
		if (pos.x < collision.pos.x) {
			pos.x = collision.pos.x - collision.width / 2;
		} else if (pos.x > collision.pos.x) {
			pos.x = collision.pos.x + collision.width / 2;
		} else if (pos.y + height / 2 < collision.pos.y) {
			pos.y = collision.pos.y - collision.height / 2 - height / 2;
		} else if (pos.y > collision.pos.y) {
			pos.y = collision.pos.y + collision.height / 2 + height / 2;
		}

		// Setup Animation
		animation.changeAnimation(getAnimation("MAGIC::IDLE"), true, 4);

		// Override Animation
		image = animation.animate();
	}
}
