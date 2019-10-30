package project_16x16.entities;

import processing.core.*;
import project_16x16.projectiles.Swing;
import project_16x16.scene.GameplayScene;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Util;
import project_16x16.SideScroller.debugType;

import java.util.ArrayList;

import project_16x16.components.AnimationComponent;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;

/**
 * <h1>Player Class</h1>
 * <p>
 * This class handles the playable character. It covers controlling, animating,
 * displaying, and updating the character.
 * </p>
 */
public final class Player extends EditableObject {
	/**
	 * Current player sprite
	 */
	private static PImage image;

	private static PImage lifeOn;
	private static PImage lifeOff;

	private float gravity;

	private PVector velocity = new PVector(0, 0);

	private static final int collisionRange = 145;

	private int speedWalk;
	private int speedJump;

	private int life;
	private int lifeCapacity;

	// Player Projectile
	public ArrayList<Swing> swings;

	// Animation Component
	public AnimationComponent animation;

	/**
	 * Possible player actions/states
	 */
	private enum ACTIONS {
		WALK, IDLE, JUMP, LAND, FALL, ATTACK, DASH, DASH_ATTACK
	}
	
	private class PlayerState {
		public PVector pos;
		public boolean flying;
		public boolean attacking;
		public boolean dashing;
		public int facingDir;
		public boolean landing;
		public boolean jumping;
		
		public PlayerState() {
			pos = new PVector(0, 0);
			flying = false;
			attacking = false;
			dashing = false;
			facingDir = RIGHT;
			jumping = false;
			landing = false;
		}
		
		public PlayerState(PlayerState ps) {
			pos = ps.pos;
			flying = ps.flying;
			attacking = ps.attacking;
			dashing = ps.dashing;
			facingDir = ps.facingDir;
			landing = ps.landing;
			jumping = ps.jumping;
		}
	}
	
	private PlayerState pstate;
	private PlayerState state;
	
	static {
		image = Tileset.getTile(0, 258, 14, 14, 4);
		lifeOn = Tileset.getTile(144, 256, 9, 9, 4);
		lifeOff = Tileset.getTile(160, 256, 9, 9, 4);
	}

	/**
	 * Constructor
	 * 
	 * @param a SideScroller game controller.
	 */
	public Player(SideScroller a, GameplayScene g) {
		
		super(a, g);

		pos = new PVector(100, 300);
		gravity = 1;

		animation = new AnimationComponent();
		swings = new ArrayList<Swing>();

		// Set life
		lifeCapacity = 3;
		life = lifeCapacity;

		speedWalk = 7;
		speedJump = 18;

		width = 14 * 4;
		height = 16 * 4;
		
		state = new PlayerState();
		state.pos = pos;
		
		setAnimation(ACTIONS.IDLE);
	}
	
	public PVector getVelocity() {
		return new PVector(velocity.x, velocity.y);
	}

	/**
	 * The display method controls how to display the character to the screen with
	 * what animation.
	 */
	public void display() {

		// Display Swing Projectiles
		for (int i = 0; i < swings.size(); i++) {
			swings.get(i).display();
		}

		applet.pushMatrix();
		applet.translate(pos.x, pos.y);
		if (state.facingDir == LEFT) {
			applet.scale(-1, 1);
		}
		applet.image(image, 0, 0);
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
		pstate = state;
		state = new PlayerState(pstate);
		state.pos = pos;
		
		// Dash
		if (applet.keyPressed && applet.keyPress(Options.dashKey)) {
			state.dashing = true;
		}

		// Attack
		if (applet.mousePressed && applet.mouseButton == LEFT && !state.attacking) {
			state.attacking = true;
			// Create Swing Projectile
			swings.add(new Swing(applet, gameScene, (int) pos.x, (int) pos.y, state.facingDir));
		}

		// End Dash
		if (animation.name == "DASH" && animation.ended) {
			state.dashing = false;
		}
		
		// End Dash Attack
		if (animation.name == "DASH_ATTACK" && animation.ended) {
			state.dashing = false;
			state.attacking = false;
		}
		// End Attack
		if (animation.name == "ATTACK" && animation.ended) {
			state.attacking = false;
		}
		
		// End Jumping
		if (animation.name == "JUMP" && animation.ended) {
			state.jumping = false;
		}
		
		if (animation.name == "LAND" && animation.ended) {
			state.landing = false;
		}

		// update velocity on the x axis
		if (applet.keyPress(Options.moveRightKey) || applet.keyPress(68)) {
			velocity.x = speedWalk * applet.deltaTime;
			if (state.dashing) {
				velocity.x *= 1.5;
			}
			state.facingDir = RIGHT;
		} else if (applet.keyPress(Options.moveLeftKey) || applet.keyPress(65)) {
			velocity.x = -speedWalk * applet.deltaTime;
			if (state.dashing) {
				velocity.x *= 1.5;
			}
			state.facingDir = LEFT;
		} else {
			velocity.x = 0;
			state.dashing = false;
		}

		// update velocity on the y axis
		if (!state.flying && applet.keyPressed && (applet.keyPress(Options.jumpKey) || applet.keyPress(' '))) {
			state.flying = true;
			state.jumping = true;
			velocity.y -= speedJump;
			if (state.dashing) {
				state.dashing = false;
				velocity.y *= 1.2;
			}
		}
		
		velocity.y += gravity * applet.deltaTime;

		if (applet.debug == debugType.ALL) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(pos.x, pos.y, collisionRange * 2, collisionRange * 2);
		}

		// All Collision Global Check
		for (int i = 0; i < gameScene.collidableObjects.size(); i++) {
			CollidableObject collision = gameScene.collidableObjects.get(i);
            if (Util.fastInRange(pos, collision.pos, collisionRange)) { // In Player Range
				if (applet.debug == debugType.ALL) {
					applet.strokeWeight(2);
					applet.rect(collision.pos.x, collision.pos.y, collision.width, collision.height);
					applet.fill(255, 0, 0);
					applet.ellipse(collision.pos.x, collision.pos.y, 5, 5);
					applet.noFill();
				}
				
				if (collidesFuturX(collision)) {
					// player left of collision
					if (pos.x < collision.pos.x) {
						pos.x = collision.pos.x - collision.width / 2 - width / 2;
					// player right of collision
					} else {
						pos.x = collision.pos.x + collision.width / 2 + width / 2;
					}
					velocity.x = 0;
					state.dashing = false;
				}
				if (collidesFuturY(collision)) {
					// player above collision
					if (pos.y < collision.pos.y) {
						if (state.flying) {
							state.landing = true;
						}
						pos.y = collision.pos.y - collision.height / 2 - height / 2;
						state.flying = false;
					// player below collision
					} else {
						pos.y = collision.pos.y + collision.height / 2 + height / 2;
						state.jumping = false;
					}
					velocity.y = 0;
				}
			}
		}
		
		if (velocity.y != 0) {
			state.flying = true;
		}

		if (state.jumping) {
			setAnimation(ACTIONS.JUMP);
		} else if (state.landing) {
			setAnimation(ACTIONS.LAND);
		} else if (state.attacking) {
			if (state.dashing) {
				setAnimation(ACTIONS.DASH_ATTACK);
			} else {
				setAnimation(ACTIONS.ATTACK);
			}
		} else if (state.flying) {
			setAnimation(ACTIONS.FALL);
		} else if (velocity.x != 0) {
			if (state.dashing) {
				setAnimation(ACTIONS.DASH);
			} else {
				setAnimation(ACTIONS.WALK);
			}
		} else {
			setAnimation(ACTIONS.IDLE);
		}
		
		pos.x += velocity.x;
		pos.y += velocity.y;

		// out of bounds check
		if (pos.y > 2000) {
			pos.y = -100; // TODO set to spawn loc
			pos.x = 0; // TODO set to spawn loc
			velocity.x = 0;
			velocity.y = 0;
		}

		// Update Swing Projectiles
		for (int i = 0; i < swings.size(); i++) {
			swings.get(i).update();
		}
		image = animation.animate();
	}

	/**
	 * Displays life capacity as long as the character has health.
	 */
	public void displayLife() {
		for (int i = 0; i < lifeCapacity; i++) {
			if (i <= life) {
				applet.image(lifeOn, 30 + i * 50, 30);
			} else {
				applet.image(lifeOff, 30 + i * 50, 30);
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

	/**
	 * Sets the current animation sequence for the Player to use
	 * 
	 * @param anim the animation id
	 */
	private void setAnimation(ACTIONS anim) {
		if (animation.name == anim.name() && !animation.ended) {
			return;
		}
		
		switch (anim) {
			case WALK :
				animation.changeAnimation(getAnimation("PLAYER::WALK"), true, 6);
				break;
			case IDLE :
				animation.changeAnimation(getAnimation("PLAYER::IDLE"), true, 20);
				break;
			case JUMP :
				animation.changeAnimation(getAnimation("PLAYER::SQUISH"), false, 4);
				break;
			case LAND :
				animation.changeAnimation(getAnimation("PLAYER::SQUISH"), false, 2);
				break;
			case FALL :
				animation.changeAnimation(getAnimation("PLAYER::IDLE"), true, 20);
				break;
			case ATTACK :
				animation.changeAnimation(getAnimation("PLAYER::ATTACK"), false, 4);
				break;
			case DASH :
				animation.changeAnimation(getAnimation("PLAYER::SQUISH"), false, 6);
				break;
			case DASH_ATTACK :
				animation.changeAnimation(getAnimation("PLAYER::ATTACK"), false, 2);
				break;
		}
		animation.ended = false;
		animation.name = anim.name();
	}

	/**
	 * getter for the currently used animation.
	 * 
	 * @param id the animation id
	 * @return the animation being used.
	 */
	private ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}
}
