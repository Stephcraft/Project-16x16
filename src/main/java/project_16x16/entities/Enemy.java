package project_16x16.entities;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.Audio;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.SideScroller.debugType;
import project_16x16.Tileset;
import project_16x16.Utility;
import project_16x16.Audio.SFX;
import project_16x16.components.AnimationComponent;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.objects.GameObject;
import project_16x16.projectiles.Swing;
import project_16x16.scene.GameplayScene;

/**
 * <h1>Enemy Class</h1>
 * <p>
 * This class handles the enemy parent behavior.
 * </p>
 */
public class Enemy extends CollidableObject {

	private PImage image;

	float gravity;

	final PVector velocity = new PVector(0, 0);

	private static final int collisionRange = 145;
	
	final int speedWalk;
	private final int speedJump;

	public int health;
	
	
	
	EnemyState enemyState;

	/**
	 * Constructor
	 * 
	 * @param a SideScroller game controller.
	 */
	public Enemy(SideScroller a, GameplayScene g) {
		super(a,g);
		gravity = 1;
		image = Tileset.getTile(0, 258, 14, 14, 4);
		health = 2;
		speedWalk = 7;
		speedJump = 18;
		width = 14 * 4;
		height = 10 * 4;
		enemyState = new EnemyState();
	}
	
	/**
	 * The display method controls how to display the character to the screen with
	 * what animation.
	 */
	public void display() {
		applet.pushMatrix();
		applet.translate(pos.x, pos.y);
		if (enemyState.facingDir == LEFT) {
			applet.scale(-1, 1);
		}
		applet.image(image, 0, 0);
		applet.noTint();
		applet.popMatrix();

		if (applet.debug == debugType.ALL) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(pos.x, pos.y, width, height); // display player bounding box
		}
	}

	/**
	 * The update method handles updating the character.
	 */
	public void update() {
		//velocity.set(0, velocity.y + gravity);

		checkEnemyCollision();
		if (velocity.y != 0) {
			enemyState.flying = true;
		}
		pos.add(velocity);

		
		
		if (pos.y > 2000) { // out of bounds check
			//Destroy(gameObject);
		}
		
		
		if (applet.isKeyDown(KeyEvent.VK_9)) {
						
		}
		
		
		if (applet.debug == debugType.ALL) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(pos.x, pos.y, collisionRange * 2, collisionRange * 2);
		}
	}

	public PVector getVelocity() {
		return velocity.copy();
	}
	
	public EnemyState getState() {
		return enemyState;
	}

	private void checkEnemyCollision() {
		for (EditableObject o : gameScene.objects) {
			if(o.equals(this)) continue;
			if (o instanceof CollidableObject) {
				CollidableObject collision = (CollidableObject) o;
				if (Utility.fastInRange(pos, collision.pos, collisionRange)) { // In Player Range
					if (applet.debug == debugType.ALL) {
						applet.strokeWeight(2);
						applet.rect(collision.pos.x, collision.pos.y, collision.width, collision.height);
						applet.fill(255, 0, 0);
						applet.ellipse(collision.pos.x, collision.pos.y, 5, 5);
						applet.noFill();
					}

					if (collidesFuturX(collision)) {
						// enemy left of collision
						if (pos.x < collision.pos.x) {
							pos.x = collision.pos.x - collision.width / 2 - width / 2;
							// enemy right of collision
						} else {
							pos.x = collision.pos.x + collision.width / 2 + width / 2;
						}
						velocity.x = 0;
						enemyState.dashing = false;
					}
					if (collidesFuturY(collision)) {
						// enemy above collision
						if (pos.y < collision.pos.y) {
							if (enemyState.flying) {
								enemyState.landing = true;
							}
							pos.y = collision.pos.y - collision.height / 2 - height / 2;
							enemyState.flying = false;
							// enemy below collision
						} else {
							pos.y = collision.pos.y + collision.height / 2 + height / 2;
							enemyState.jumping = false;
						}
						velocity.y = 0;
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
		return (pos.x + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y - height / 2 < collision.pos.y + collision.height / 2);
	}

	// TODO: optimize these (unused)
	private boolean collidesEqual(CollidableObject collision) {
		return (pos.x + width / 2 >= collision.pos.x - collision.width / 2
				&& pos.x - width / 2 <= collision.pos.x + collision.width / 2)
				&& (pos.y + height / 2 >= collision.pos.y - collision.height / 2
						&& pos.y - height / 2 <= collision.pos.y + collision.height / 2);
	}

	private boolean collidesFutur(CollidableObject collision) {
		return (pos.x + velocity.x + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x + velocity.x - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + velocity.y + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y + velocity.y - height / 2 < collision.pos.y + collision.height / 2);
	}

	private boolean collidesFuturX(CollidableObject collision) {
		return (pos.x + velocity.x + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x + velocity.x - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + 0 + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y + 0 - height / 2 < collision.pos.y + collision.height / 2);
	}

	private boolean collidesFuturY(CollidableObject collision) {
		return (pos.x + 0 + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x + 0 - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + velocity.y + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y + velocity.y - height / 2 < collision.pos.y + collision.height / 2);
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
