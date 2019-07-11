package projectiles;

import objects.Collision;
import processing.core.PApplet;
import processing.core.PVector;
import sidescroller.SideScroller;

public class MagicProjectile extends ProjectileObject {

	public MagicProjectile(SideScroller a, int x, int y, int dir) {
		super(a);

		id = "MAGIC";

		pos = new PVector(x, y);

		direction = dir;

		// Setup Animation
		animation.frames = getAnimation("MAGIC::MOVE");
		animation.loop = true;
		animation.length = 8;
		animation.rate = 4;
		animation.frame = 0;
		animation.start = 0;

		speed = 10;

		width = 22 * 4;
		height = 10 * 4;
	}

	@Override
	public void display() {
		switch (direction) {
			case LEFT :
				applet.pushMatrix();
				applet.translate(pos.x - applet.originX, pos.y - applet.originY);
				applet.scale(-1, 1);
				applet.image(image, 0, 0);
				applet.popMatrix();
				break;
			case RIGHT :
				applet.image(image, pos.x - applet.originX, pos.y - applet.originY);
				break;
			case UP :
				applet.pushMatrix();
				applet.translate(pos.x - applet.originX, pos.y - applet.originY);
				applet.rotate(PApplet.radians(-90));
				applet.image(image, 0, 0);
				applet.popMatrix();
				break;
			case DOWN :
				applet.pushMatrix();
				applet.translate(pos.x - applet.originX, pos.y - applet.originY);
				applet.rotate(PApplet.radians(90));
				applet.image(image, 0, 0);
				applet.popMatrix();
				break;
		}
		applet.stroke(255);
		applet.noFill();
		applet.rect(pos.x - applet.originX, pos.y - applet.originY, width, height);
	}

	@Override
	public void update() {
		image = animation.animate(applet.frameCount, applet.deltaTime);

		// Destroy on collide
		if (!hit) {
			for (int i = 0; i < applet.collisions.size(); i++) {
				Collision collision = applet.collisions.get(i);

				if (collides(collision) && !collision.flag.equals("TRANSPARENT_BULLET")) {
					hit = true;

					width = 8 * 4;
					height = 8 * 4;

					// Realistic Colliding
					if (pos.x - applet.originX < collision.pos.x - applet.originX) {
						pos.x = collision.pos.x - collision.width / 2;
					} else if (pos.x - applet.originX > collision.pos.x - applet.originX) {
						pos.x = collision.pos.x + collision.width / 2;
					} else if (pos.y - applet.originY + height / 2 < collision.pos.y - applet.originY) {
						pos.y = collision.pos.y - collision.height / 2 - height / 2;
					} else if (pos.y - applet.originY > collision.pos.y - applet.originY) {
						pos.y = collision.pos.y + collision.height / 2 + height / 2;
					}

					// Setup Animation
					animation.frames = getAnimation("MAGIC::IDLE");
					animation.loop = true;
					animation.length = 8;
					animation.rate = 4;
					animation.frame = 0;
					animation.start = 0;

					// Override Animation
					image = animation.animate(applet.frameCount, applet.deltaTime);

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

	public void hit(Collision collision) {
		hit = true;

		width = 8 * 4;
		height = 8 * 4;

		// Realistic Colliding
		if (pos.x - applet.originX < collision.pos.x - applet.originX) {
			pos.x = collision.pos.x - collision.width / 2;
		} else if (pos.x - applet.originX > collision.pos.x - applet.originX) {
			pos.x = collision.pos.x + collision.width / 2;
		} else if (pos.y - applet.originY + height / 2 < collision.pos.y - applet.originY) {
			pos.y = collision.pos.y - collision.height / 2 - height / 2;
		} else if (pos.y - applet.originY > collision.pos.y - applet.originY) {
			pos.y = collision.pos.y + collision.height / 2 + height / 2;
		}

		// Setup Animation
		animation.frames = getAnimation("MAGIC::IDLE");
		animation.loop = true;
		animation.length = 8;
		animation.rate = 4;
		animation.frame = 0;
		animation.start = 0;

		// Override Animation
		image = animation.animate(applet.frameCount, applet.deltaTime);
	}
}
