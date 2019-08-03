package projectiles;

import objects.Collision;
import processing.core.PApplet;
import processing.core.PVector;
import sidescroller.SideScroller;

public class MagicProjectile extends ProjectileObject {

	final int PROJECTILE_SIZE_X = 22, PROJECTILE_SIZE_Y = 10;
	final int PROJECTILE_SCALE = 4;
	final int PROJECTILE_SPEED = 10;
	final int ANIM_LENGTH = 8, ANIM_RATE = 4;
	final boolean IS_ANIM_LOOP = true;

	public MagicProjectile(SideScroller a, int x, int y, int dir) {
		super(a);
		id = "MAGIC";
		pos = new PVector(x, y);
		direction = dir;
		speed = PROJECTILE_SPEED;
		// Setup Animation
		setupAnimation("MAGIC::MOVE");
		// Set the width and height of projectile to its appropiate proportions.
		setWidthHeight(PROJECTILE_SIZE_X * PROJECTILE_SCALE, PROJECTILE_SIZE_Y * PROJECTILE_SCALE);
	}

	public void display() {
		switch (direction) {// No need writing the same can make a method to handle it works easier...
		case LEFT:
			displayProjectile(true, -180);// Handles the direction the projectile is flying..
			break;
		case RIGHT:
			displayProjectile(false, 0);
			break;
		case UP:
			displayProjectile(true, -90);
			break;
		case DOWN:
			displayProjectile(true, 90);
			break;
		}
		debugMode(); // detection...
	}

	public void update() {
		image = animation.animate(applet.frameCount, applet.deltaTime);
		// Destroy on collide
		if (!hit) {
			for (int i = 0; i < applet.collisions.size(); i++) {
				Collision collision = applet.collisions.get(i);

				if (collides(collision) && !collision.flag.equals("TRANSPARENT_BULLET")) {
					hit = true;
					setWidthHeight(ANIM_LENGTH * ANIM_RATE, ANIM_LENGTH * ANIM_RATE);
					// Realistic Colliding
					realisticColliding(collision);
					// Setup Animation
					setupAnimation("MAGIC::IDLE");
					// Override Animation
					image = animation.animate(applet.frameCount, applet.deltaTime);
					// applet.projectileObjects.remove(this);
				}
			}
		}
		// Move Projectile
		if (!hit) {
			switch (direction) {
			case LEFT:
				pos.x -= speed;
				setWidthHeight(PROJECTILE_SIZE_Y * PROJECTILE_SCALE, PROJECTILE_SIZE_X * PROJECTILE_SCALE);
				break;
			case RIGHT:
				pos.x += speed;
				setWidthHeight(PROJECTILE_SIZE_Y * PROJECTILE_SCALE, PROJECTILE_SIZE_X * PROJECTILE_SCALE);
				break;
			case UP:
				pos.y -= speed;
				setWidthHeight(PROJECTILE_SIZE_Y * PROJECTILE_SCALE, PROJECTILE_SIZE_X * PROJECTILE_SCALE);
				break;
			case DOWN:
				pos.y += speed;
				setWidthHeight(PROJECTILE_SIZE_Y * PROJECTILE_SCALE, PROJECTILE_SIZE_X * PROJECTILE_SCALE);
				break;
			}
		}
	}

	// DEBUG MODE
	public void debugMode() {
		applet.stroke(255);
		applet.noFill();
		applet.rect(pos.x - applet.originX, pos.y - applet.originY, width, height);// For displaying collision
	}

	public void hit(Collision collision) {
		hit = true;
		setWidthHeight(ANIM_LENGTH * ANIM_RATE, ANIM_LENGTH * ANIM_RATE);
		// Realistic Colliding
		realisticColliding(collision);
		// Setup Animation
		setupAnimation("MAGIC::IDLE");
		// Override Animation
		image = animation.animate(applet.frameCount, applet.deltaTime);
	}

	public void setupAnimation(String anim) {
		animation.frames = getAnimation(anim);
		animation.loop = IS_ANIM_LOOP;
		animation.length = ANIM_LENGTH;
		animation.rate = ANIM_RATE;
		animation.frame = 0;
		animation.start = 0;
	}

	// ---------------------------------------
	// Setting the width and height
	public void setWidthHeight(int w, int h) {
		width = w;
		height = h;
	}

	// ---------------------------------------
	// COLLISION AND DISPLAYING OF PROJECTILE
	// Added collision method because like it used so much...
	public boolean isColliding(float point1, float point2, float point3) {
		return point1 - point2 < point3 - point2;
	}

	//
	public void realisticColliding(Collision collision) {
		if (isColliding(pos.x, applet.originX, collision.pos.x)) {
			pos.x = collision.pos.x - collision.width / 2;
		} else if (isColliding(collision.pos.x, applet.originX, pos.x)) {
			pos.x = collision.pos.x + collision.width / 2;
		} else if (isColliding(pos.y, applet.originY + height / 2, collision.pos.y)) {
			pos.y = collision.pos.y - collision.height / 2 - height / 2;
		} else if (isColliding(collision.pos.y, applet.originY, pos.y)) {
			pos.y = collision.pos.y + collision.height / 2 + height / 2;
		}
	}

	// Method for the direction of drawing the projectile ....
	public void displayProjectile(boolean rotate, float rotation) {
		applet.pushMatrix();
		applet.translate(pos.x - applet.originX, pos.y - applet.originY);
		if (rotate)
			applet.rotate(PApplet.radians(rotation));
		applet.image(image, 0, 0);
		applet.popMatrix();
	}
	// ---------------------------------------
}
