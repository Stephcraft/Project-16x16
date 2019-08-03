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
	final int COLLISION_SIZE = 2;
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
			displayProjectile(-180);// Handles the direction the projectile is flying..
			pos.x -= speed;
			setWidthHeight(PROJECTILE_SIZE_X * PROJECTILE_SCALE, PROJECTILE_SIZE_Y * PROJECTILE_SCALE);
			break;
		case RIGHT:
			displayProjectile(0);
			pos.x += speed;
			setWidthHeight(PROJECTILE_SIZE_X * PROJECTILE_SCALE, PROJECTILE_SIZE_Y * PROJECTILE_SCALE);
			break;
		case UP:
			displayProjectile(-90);
			pos.y -= speed;
			setWidthHeight(PROJECTILE_SIZE_X * PROJECTILE_SCALE, PROJECTILE_SIZE_Y * PROJECTILE_SCALE);
			break;
		case DOWN:
			displayProjectile(90);
			pos.y += speed;
			setWidthHeight(PROJECTILE_SIZE_X * PROJECTILE_SCALE, PROJECTILE_SIZE_Y * PROJECTILE_SCALE);
			break;
		}
		// detection...
	}

	public void update() {
		// Destroy on collide
		for (int i = 0; i < applet.collisions.size(); i++) {
			Collision collision = applet.collisions.get(i);
			if (collides(collision) && !collision.flag.equals("TRANSPARENT_BULLET")) {
				hit = true;
				setWidthHeight(ANIM_LENGTH * ANIM_RATE, ANIM_LENGTH * ANIM_RATE);
				// Realistic Colliding
				realisticColliding(collision);
				// Setup Animation
				setupAnimation("MAGIC::IDLE");
			}
		}
		debugMode(pos.x - applet.originX, pos.y - applet.originY, width, height);
		image = animation.animate(applet.frameCount, applet.deltaTime);
		// Override Animation
	}

	// DEBUG MODE
	public void debugMode(float x, float y, float w, float h) {
		applet.stroke(255);
		if (hit)
			applet.stroke(255, 0, 0);
		applet.noFill();
		applet.rect(x, y, w, h); // For debug displaying // collision
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

	// Setting the width and height
	public void setWidthHeight(int w, int h) {
		width = w;
		height = h;
	}

	// COLLISION AND DISPLAYING OF PROJECTILE
	// Added collision method because like it used so much...
	public boolean collider(float point1, float point2, float point3) {
		return point1 - point2 < point3 - point2;
	}

	//
	public void realisticColliding(Collision collision) {
		// LEFT
		if (collider(pos.x, applet.originX, collision.pos.x)) {
			pos.x = collision.pos.x - collision.width / COLLISION_SIZE;
		} // RIGHT
		else if (collider(collision.pos.x, applet.originX, pos.x)) {
			pos.x = collision.pos.x + collision.width / COLLISION_SIZE;
		} // DOWN
		else if (collider(pos.y, applet.originY + height / COLLISION_SIZE, collision.pos.y)) {
			pos.y = collision.pos.y - collision.height / COLLISION_SIZE - height / COLLISION_SIZE;
		} // UP
		else if (collider(collision.pos.y, applet.originY, pos.y)) {
			pos.y = collision.pos.y + collision.height / COLLISION_SIZE + height / COLLISION_SIZE;
		}
	}

	// Method for the direction of drawing the projectile ....
	public void displayProjectile(float rotation) {
		applet.pushMatrix();
		applet.translate(pos.x - applet.originX, pos.y - applet.originY);
		applet.rotate(PApplet.radians(rotation));
		applet.image(image, 0, 0);
		applet.popMatrix();
	}
}-
