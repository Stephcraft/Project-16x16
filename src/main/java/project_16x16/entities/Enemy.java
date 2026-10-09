package project_16x16.entities;

import java.awt.event.KeyEvent;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.Constants;
import project_16x16.SideScroller;
import project_16x16.SideScroller.DebugType;
import project_16x16.Tileset;
import project_16x16.Time;
import project_16x16.Utility;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.scene.GameplayScene;

/**
 * <h1>Enemy Class</h1>
 * <p>
 * This class handles the enemy parent behavior.
 * </p>
 */
public class Enemy extends CollidableObject {

	private PImage image;

	float gravity; // px/s²

	/** Velocity (px/s). */
	final PVector velocity = new PVector(0, 0);
	/** Displacement over the current physics step (px); reused to avoid allocation. */
	private final PVector displacement = new PVector(0, 0);

	private static final int collisionRange = 145;

	final float speedWalk; // px/s
	private final float speedJump; // px/s

	public int health;

	EnemyState enemyState;

	/**
	 * Constructor
	 *
	 * @param sideScroller SideScroller game controller.
	 */
	public Enemy(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);
		gravity = Constants.GAME_GRAVITY;
		image = Tileset.getTile(0, 258, 14, 14, 4);
		health = 2;
		speedWalk = 420;
		speedJump = 1050;
		width = 14 * 4;
		height = 10 * 4;
		enemyState = new EnemyState();
	}

	/**
	 * The display method controls how to display the character to the screen with
	 * what animation.
	 */
	@Override
	public void display() {
		applet.pushMatrix();
		applet.translate(position.x, position.y);
		if (enemyState.facingDir == LEFT) {
			applet.scale(-1, 1);
		}
		applet.image(image, 0, 0);
		applet.noTint();
		applet.popMatrix();
		if (applet.debug == DebugType.ALL) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(position.x, position.y, width, height); // display player bounding box
		}
	}

	/**
	 * The update method handles updating the character.
	 */
	public void update() {
		final int steps = Time.substeps(Constants.PHYSICS_MAX_STEP);
		final float dt = Time.delta() / steps;
		for (int i = 0; i < steps; i++) {
			step(dt);
		}
		if (position.y > 2000) { // out of bounds check
			// Destroy(gameObject);
		}
		if (applet.isKeyDown(KeyEvent.VK_9)) {
		}
		if (applet.debug == DebugType.ALL) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(position.x, position.y, collisionRange * 2, collisionRange * 2);
		}
	}

	public PVector getVelocity() {
		return velocity.copy();
	}

	public EnemyState getState() {
		return enemyState;
	}

	/**
	 * Advances the enemy's motion by one physics step, resolving collisions.
	 *
	 * @param dt step duration (seconds)
	 */
	private void step(float dt) {
		displacement.set(velocity.x * dt, velocity.y * dt + 0.5f * gravity * dt * dt);
		velocity.y += gravity * dt;

		checkEnemyCollision();
		if (velocity.y != 0) {
			enemyState.flying = true;
		}
		position.add(displacement);
	}

	private void checkEnemyCollision() {
		for (EditableObject o : gameplayScene.objects) {
			if (o.equals(this)) {
				continue;
			}
			if (o instanceof CollidableObject) {
				CollidableObject collision = (CollidableObject) o;
				if (Utility.fastInRange(position, collision.position, collisionRange)) { // In Player Range
					if (applet.debug == DebugType.ALL) {
						applet.strokeWeight(2);
						applet.rect(collision.position.x, collision.position.y, collision.width, collision.height);
						applet.fill(255, 0, 0);
						applet.ellipse(collision.position.x, collision.position.y, 5, 5);
						applet.noFill();
					}
					if (collidesAfterMove(collision, displacement.x, 0)) {
						// enemy left of collision
						if (position.x < collision.position.x) {
							position.x = collision.position.x - collision.width / 2 - width / 2;
							// enemy right of collision
						} else {
							position.x = collision.position.x + collision.width / 2 + width / 2;
						}
						velocity.x = 0;
						displacement.x = 0;
						enemyState.dashing = false;
					}
					if (collidesAfterMove(collision, 0, displacement.y)) {
						// enemy above collision
						if (position.y < collision.position.y) {
							if (enemyState.flying) {
								enemyState.landing = true;
							}
							position.y = collision.position.y - collision.height / 2 - height / 2;
							enemyState.flying = false;
							// enemy below collision
						} else {
							position.y = collision.position.y + collision.height / 2 + height / 2;
							enemyState.jumping = false;
						}
						velocity.y = 0;
						displacement.y = 0;
					}
				}
			}
		}
	}

	/**
	 *
	 * Determines is the character has collided with an object of type Collision.
	 *
	 * @param collision The other object
	 * @return boolean if it has or has not collided with the object.
	 */
	private boolean collides(CollidableObject collision) {
		return (position.x + width / 2 > collision.position.x - collision.width / 2 && position.x - width / 2 < collision.position.x + collision.width / 2)
				&& (position.y + height / 2 > collision.position.y - collision.height / 2
						&& position.y - height / 2 < collision.position.y + collision.height / 2);
	}

	// TODO: optimize these (unused)
	private boolean collidesEqual(CollidableObject collision) {
		return (position.x + width / 2 >= collision.position.x - collision.width / 2 && position.x - width / 2 <= collision.position.x + collision.width / 2)
				&& (position.y + height / 2 >= collision.position.y - collision.height / 2
						&& position.y - height / 2 <= collision.position.y + collision.height / 2);
	}

	/**
	 * Determines whether the enemy would collide with an object after moving by
	 * (dx, dy).
	 */
	private boolean collidesAfterMove(CollidableObject collision, float dx, float dy) {
		return (position.x + dx + width / 2 > collision.position.x - collision.width / 2
				&& position.x + dx - width / 2 < collision.position.x + collision.width / 2)
				&& (position.y + dy + height / 2 > collision.position.y - collision.height / 2
						&& position.y + dy - height / 2 < collision.position.y + collision.height / 2);
	}

	public class EnemyState {
		public boolean flying;
		public boolean attacking;
		public boolean dashing;
		public int facingDir;
		public boolean landing;
		public boolean jumping;

		EnemyState() {
			flying = false;
			attacking = false;
			dashing = false;
			facingDir = RIGHT;
			jumping = false;
			landing = false;
		}
	}

	@Override
	public void debug() {
		// TODO Auto-generated method stub
	}

	@Override
	public JSONObject exportToJSON() {
		// TODO Auto-generated method stub
		return null;
	}
}
