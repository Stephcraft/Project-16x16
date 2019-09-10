package entities;

import processing.core.*;
import projectiles.Swing;
import sidescroller.Options;
//import sidescroller.PClass;
import sidescroller.SideScroller;
import sidescroller.Tileset;
import sidescroller.Util;
import sidescroller.SideScroller.debugType;

import java.util.ArrayList;

import components.AnimationComponent;
import objects.CollidableObject;
import objects.EditableObject;

/**
 * <h1>Player Class</h1>
 * <p>
 * This class handles the playable character. It covers controlling, animating,
 * displaying, and updating the character.
 * </p>
 */
public final class Player extends EditableObject {

	/**
	 * Previous position.
	 */
	private float px;
	private float py;

	/**
	 * Current player sprite
	 */
	private PImage image;

	private PImage lifeOn;
	private PImage lifeOff;

	private float gravity;

	public float speedX;
	public float speedY;

	private static final int collisionRange = 145;

	private int speedWalk;
	private int speedJump;

	private int direction;

	private int life;
	private int lifeCapacity;

	public boolean flying;
	private boolean pflying;

	public boolean attack;
	private boolean dashing;

	// Player Projectile
	public ArrayList<Swing> swings;

	// Animation Component
	public AnimationComponent animation;

	/**
	 * Possible player actions/states
	 */
	private enum ACTIONS {
		WALK, IDLE, SQUISH, FALL, ATTACK, DASH, DASH_ATTACK
	}

	/**
	 * Constructor
	 * 
	 * @param a SideScroller game controller.
	 */
	public Player(SideScroller a) {
		super(a);

		pos = new PVector(100, 300);
		gravity = 1;

		animation = new AnimationComponent();
		swings = new ArrayList<Swing>();

		// Set life
		lifeCapacity = 3;
		life = lifeCapacity;

		speedWalk = 7;
		speedJump = 18; // 20

		width = 14 * 4;
		height = 16 * 4;

		flying = true;
	}

	/**
	 * load any needed assets.
	 * 
	 * @param sheet sprite sheet as PImage.
	 */
	public void load(PImage sheet) {
		image = Util.pg(sheet.get(0, 258, 14, 14), 4);

		lifeOn = Util.pg(sheet.get(144, 256, 9, 9), 4);
		lifeOff = Util.pg(sheet.get(160, 256, 9, 9), 4);

		setAnimation(ACTIONS.IDLE);
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

		if (direction == LEFT) { // flips sprite along vertical line
			applet.pushMatrix();
			applet.translate(pos.x, pos.y);
			applet.scale(-1, 1);
			applet.image(image, 0, 0);
			applet.popMatrix();
		} else {
			applet.image(image, pos.x, pos.y);
		}

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

		if (!dashing) {
			speedY += gravity * applet.deltaTime;
		} else {
			speedY += gravity * applet.deltaTime * .5;
		}

		// Save Previous State
		px = pos.x;
		py = pos.y;
		pflying = flying;

		// Move on the x axis
		if (applet.keyPress(Options.moveRightKey) || applet.keyPress(68)) {
			if (applet.keyPressEvent && !attack && !dashing) {
				setAnimation(ACTIONS.WALK);
			}
			if (!dashing) {
				speedX = (speedWalk * applet.deltaTime);
			} else {
				speedX = (float) (speedWalk * applet.deltaTime * 1.5);
			}
			direction = RIGHT;
		} else if (applet.keyPress(Options.moveLeftKey) || applet.keyPress(65)) {
			if (applet.keyPressEvent && !attack && !dashing) {
				setAnimation(ACTIONS.WALK);
			}
			if (!dashing) {
				speedX = -speedWalk * applet.deltaTime;
			} else {
				speedX = (float) (-speedWalk * applet.deltaTime * 1.5);
			}

			direction = LEFT;

		} else {
			speedX = 0;
		}
		// Dash
		if (applet.keyPress(Options.dashKey)) {
			if (applet.keyPressEvent && !dashing) {
				setAnimation(ACTIONS.DASH);
				dashing = true;
			}
		}

		// Move on the y axis
		if (applet.keyPress(Options.jumpKey) || applet.keyPress(' ')) {
			if (applet.keyPressEvent && !flying) { // && speedY == 0 && !flying
				flying = true;
				if (!dashing) {
					speedY -= (int) (speedJump * applet.deltaTime);
				} else {
					speedY -= (float) (speedJump * applet.deltaTime * 1.2);
				}

			}
		}

		// Attack
		if (applet.mousePressed && !attack) {
			if (applet.mouseButton == LEFT) {
				attack = true;
				if (!dashing) {
					setAnimation(ACTIONS.ATTACK);
				} else if (dashing) {
					setAnimation(ACTIONS.DASH_ATTACK);
				}
			}

			// Create Swing Projectile
			swings.add(new Swing(applet, (int) pos.x, (int) pos.y, direction));
		}

		// End Dash

		if (animation.name == "DASH" && animation.ended) {
			dashing = false;
			if (flying) {
				speedY = 0;
			}
			setAnimation(ACTIONS.WALK);
		}
		// End Dash Attack
		if (animation.name == "DASH_ATTACK" && animation.ended) {
			dashing = false;
			attack = false;
			if (flying) {
				speedY = 0;
			}
			setAnimation(ACTIONS.WALK);
		}
		// End Attack
		if (animation.name == "ATTACK" && animation.ended) {
			attack = false;
			if (speedX != 0) {
				setAnimation(ACTIONS.WALK);
			} else if (speedX == 0) {
				setAnimation(ACTIONS.IDLE);
			}
		}
		// boolean collides = false;

		if (applet.debug == debugType.ALL) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(pos.x, pos.y, collisionRange * 2, collisionRange * 2);
		}

		// All Collision Global Check
		for (int i = 0; i < applet.collidableObjects.size(); i++) {
			CollidableObject collision = applet.collidableObjects.get(i);
            if (Util.fastInRange(pos, collision.pos, collisionRange)) { // In Player Range
				if (applet.debug == debugType.ALL) {
					applet.strokeWeight(2);
					applet.rect(collision.pos.x, collision.pos.y, collision.width, collision.height);
					applet.fill(255, 0, 0);
					applet.ellipse(collision.pos.x, collision.pos.y, 5, 5);
					applet.noFill();
				}
				if (collides(collision)) {
					if (px + width / 2 < collision.pos.x + collision.width / 2) {
						pos.x = collision.pos.x - collision.width / 2 - width / 2;
					} else if (px - width / 2 > collision.pos.x - collision.width / 2) { // +collision.width/2
						pos.x = collision.pos.x + collision.width / 2 + width / 2;
					}
					if (dashing) {
						dashing = false;
						setAnimation(ACTIONS.IDLE);
					}

				}
				if (collidesFuturX(collision)) {
					if (px < collision.pos.x) {
						speedX = 0;
					} else if (px > collision.pos.x) {
						speedX = 0;
					}
					if (dashing) {
						dashing = false;
						setAnimation(ACTIONS.IDLE);
					}
				}
				if (collidesFuturY(collision)) {
					if (py + height / 2 < collision.pos.y) {
						pos.y = collision.pos.y - collision.height / 2 - height / 2;
						speedY = 0;
						flying = false;
					} else if (pos.y > collision.pos.y) {
						pos.y = collision.pos.y + collision.height / 2 + height / 2;
						speedY = 0;
					}
				}
			}
		}

		if (flying && !attack && !dashing) {
			setAnimation(ACTIONS.FALL);
		}

		// On Ground Event
		if (!flying && pflying && !attack && !dashing) {
			setAnimation(ACTIONS.SQUISH);
			applet.camera.shake(0.2f); // TODO consider removing
		}

		// Idle Animation
		if (speedX == 0 && speedY == 0 && !attack && !dashing && !flying && !pflying) {
			setAnimation(ACTIONS.IDLE);
		}

		if (animation.name == "SQUISH" && speedX != 0 && !attack) {
			setAnimation(ACTIONS.WALK);
		}

		if (animation.name == "WALK" && speedX == 0 && !attack) {
			setAnimation(ACTIONS.IDLE);
		}

		if (speedX == 0 && animation.name == "WALK" && attack) {
			attack = false;
		}

		if (dashing) {
			if (speedY < -25) {
				speedY = -25;
			}
		}
		pos.x += speedX;
		pos.y += speedY;

		// out of bounds check
		if (pos.y > 2000) {
			pos.y = -100; // TODO set to spawn loc
			pos.x = 0; // TODO set to spawn loc
			speedX = 0;
			speedY = 0;
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
		return (pos.x + speedX + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x + speedX - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + speedY + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y + speedY - height / 2 < collision.pos.y + collision.height / 2);
	}

	private boolean collidesFuturX(CollidableObject collision) {
		return (pos.x + speedX + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x + speedX - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + 0 + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y + 0 - height / 2 < collision.pos.y + collision.height / 2);
	}

	private boolean collidesFuturY(CollidableObject collision) {
		return (pos.x + 0 + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x + 0 - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + speedY + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y + speedY - height / 2 < collision.pos.y + collision.height / 2);
	}

	/**
	 * Sets the current animation sequence for the Player to use
	 * 
	 * @param anim the animation id
	 */
	private void setAnimation(ACTIONS anim) {
		switch (anim) {
			case WALK :
				animation.changeAnimation(getAnimation("PLAYER::WALK"), true, 6);
				break;
			case IDLE :
				animation.changeAnimation(getAnimation("PLAYER::IDLE"), true, 20);
				break;
			case SQUISH :
				animation.changeAnimation(getAnimation("PLAYER::SQUISH"), false, 4);
				break;
			case FALL :
				animation.changeAnimation(getAnimation("PLAYER::SQUISH"), false, 6);
				break;
			case ATTACK :
				animation.changeAnimation(getAnimation("PLAYER::ATTACK"), false, 4);
				break;
			case DASH :
				animation.changeAnimation(getAnimation("PLAYER::SQUISH"), false, 6);
				break;
			case DASH_ATTACK :
				animation.changeAnimation(getAnimation("PLAYER::ATTACK"), false, 4, 2 + animation.remainingFrames());
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
