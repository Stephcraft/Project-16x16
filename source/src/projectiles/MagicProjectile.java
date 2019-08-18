package projectiles;

import objects.CollidableObject;
import processing.core.PApplet;
import processing.core.PVector;
import sidescroller.SideScroller;

public class MagicProjectile extends ProjectileObject {

	final int FLYING_PROJECTILE_W = 22;
	final int FLYING_PROJECTILE_H = 10;
	final int SCALE = 4;
	final int PROJECTILE_SPEED = 10;
	final int PROJECTILE_IDLE_SIZE = 8;

	public MagicProjectile(SideScroller a, int x, int y, int dir) {
		super(a);

		id = "MAGIC";
		pos = new PVector(x, y);
		direction = dir;

		prevDirection = dir; // Used for tracking the prevDirection of the projectile
		// Setup Animation
		setAnimation("MAGIC::MOVE", 4);
		speed = PROJECTILE_SPEED;
		width = FLYING_PROJECTILE_W * SCALE;
		height = FLYING_PROJECTILE_H * SCALE;
	}

	public void setAnimation(String anim, int animRate) {
		animation.changeAnimation(getAnimation(anim), true, animRate); // Setup Animation
	}

	public void display() {
		switch (direction) {
		case LEFT:
			setProjectile(-180);
			break;
		case RIGHT:
			setProjectile(0);
			break;
		case UP:
			setProjectile(-90);
			break;
		case DOWN:
			setProjectile(90);
			break;
		}
		debugMode();
	}

	public void update() {

		image = animation.animate();
		if (!hit) {
			moveProjectile();
			destroyProjectile();
		}
	}


	public void destroyProjectile() {
		for (int i = 0; i < applet.collisions.size(); i++) {
			Collision collision = applet.collisions.get(i);
			if (collides(collision) && !collision.flag.equals("TRANSPARENT_BULLET")) {
				hit = true;
				setWidthHeight(PROJECTILE_IDLE_SIZE * SCALE, PROJECTILE_IDLE_SIZE * SCALE);
				checkCollision(collision);
				setAnimation("MAGIC::IDLE", 4);
				// Override Animation
			  image = animation.animate();
			}
		}
	}

	public void moveProjectile() {
		switch (direction) {
		case LEFT:
			pos.x -= speed;
			setWidthHeight(FLYING_PROJECTILE_W * SCALE, FLYING_PROJECTILE_H * SCALE);
			break;
		case RIGHT:
			pos.x += speed;
			setWidthHeight(FLYING_PROJECTILE_W * SCALE, FLYING_PROJECTILE_H * SCALE);
			break;
		case UP:
			pos.y -= speed;
			setWidthHeight(FLYING_PROJECTILE_W * SCALE, FLYING_PROJECTILE_H * SCALE);
			break;
		case DOWN:
			pos.y += speed;
			setWidthHeight(FLYING_PROJECTILE_W * SCALE, FLYING_PROJECTILE_H * SCALE);
			break;
		}
	}

	public void hit(CollidableObject collision) {
		hit = true;
		setWidthHeight(PROJECTILE_IDLE_SIZE * SCALE, PROJECTILE_IDLE_SIZE * SCALE);
		checkCollision(collision);
		setAnimation("MAGIC::IDLE", 4);
		// Override Animation
		image = animation.animate(applet.frameCount, applet.deltaTime);
	}

	public void setWidthHeight(int w, int h) {
		width = w;
		height = h;
	}

	public void checkCollision(Collision collision) {
		// LEFT
		if (pos.x < collision.pos.x ) {
			pos.x = collision.pos.x - collision.width / 2;
		}
		// RIGHT
		else if (pos.x > collision.pos.x) {
			pos.x = collision.pos.x + collision.width / 2;
		}
		// UP
		else if (pos.y + height / 2 < collision.pos.y ) {
			pos.y = collision.pos.y - collision.height / 2 - height / 2;
		}
		// DOWN
		else if (pos.y > collision.pos.y) {
			pos.y = collision.pos.y + collision.height / 2 + height / 2;
		}
	}


	public void setProjectile(float rotate) {
		applet.pushMatrix();
		applet.translate(pos.x , pos.y);
		applet.rotate(PApplet.radians(rotate));
		applet.image(image, 0, 0);
		applet.popMatrix();
	}
	public void debugMode() {
		applet.stroke(255,0,0);
		applet.noFill();
		if(direction == LEFT || direction == RIGHT)applet.rect(pos.x, pos.y, width, height);
		else applet.rect(pos.x, pos.y, height, width);
	}
}
