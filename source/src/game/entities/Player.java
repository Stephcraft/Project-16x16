package game.entities;

import processing.core.*;
import game.projectiles.Swing;
import game.engine.sidescroller.DebugMode;
import game.engine.sidescroller.Options;
import game.engine.sidescroller.Tileset;
import game.engine.sidescroller.Util;

import java.util.ArrayList;

import components.AnimationComponent;
import game.objects.CollidableObject;
import game.objects.EditableObject;

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
	private static PImage image;

	private static PImage lifeOn;
	private static PImage lifeOff;

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
	
	static {
		image = Tileset.getTile(0, 258, 14, 14, 4);
		lifeOn = Tileset.getTile(144, 256, 9, 9, 4);
		lifeOff = Tileset.getTile(160, 256, 9, 9, 4);
	}

	/**
	 * Constructor
	 */
	public Player() {
		super();

		baseObjectData.setPos(new PVector(100, 300));
		gravity = 1;

		animation = new AnimationComponent();
		swings = new ArrayList<Swing>();

		// Set life
		lifeCapacity = 3;
		life = lifeCapacity;

		speedWalk = 7;
		speedJump = 18; // 20

		baseObjectData.setWidth(14 * 4);
		baseObjectData.setHeight(16 * 4);

		flying = true;
		
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
			applet.translate(baseObjectData.getPos().x, baseObjectData.getPos().y);
			applet.scale(-1, 1);
			applet.image(image, 0, 0);
			applet.popMatrix();
		} else {
			applet.image(image, baseObjectData.getPos().x, baseObjectData.getPos().y);
		}

		if (DebugMode.ALL.equals(applet.getDebug())) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(baseObjectData.getPos().x, baseObjectData.getPos().y, baseObjectData.getWidth(), baseObjectData.getHeight()); // display player bounding box
		}
	}

	/**
	 * The update method handles updating the character.
	 */
	public void update() {
		// If falling, flying = true, triggering falling animation
		// This should fix the walking of edge animation problem
		if(baseObjectData.getPos().y > py) {
			flying = true;
		}

		if (!dashing) {
			speedY += gravity * applet.getDeltaTime();
		} else {
			speedY += gravity * applet.getDeltaTime() * .5;
		}

		// Save Previous State
		px = baseObjectData.getPos().x;
		py = baseObjectData.getPos().y;
		pflying = flying;

		// Move on the x axis
		if (applet.keyPress(Options.moveRightKey) || applet.keyPress(68)) {
			if (applet.isKeyPressEvent() && !attack && !dashing) {
				setAnimation(ACTIONS.WALK);
			}
			if (!dashing) {
				speedX = (speedWalk * applet.getDeltaTime());
			} else {
				speedX = (float) (speedWalk * applet.getDeltaTime() * 1.5);
			}
			direction = RIGHT;
		} else if (applet.keyPress(Options.moveLeftKey) || applet.keyPress(65)) {
			if (applet.isKeyPressEvent() && !attack && !dashing) {
				setAnimation(ACTIONS.WALK);
			}
			if (!dashing) {
				speedX = -speedWalk * applet.getDeltaTime();
			} else {
				speedX = (float) (-speedWalk * applet.getDeltaTime() * 1.5);
			}

			direction = LEFT;

		} else {
			speedX = 0;
		}
		// Dash
		if (applet.keyPress(Options.dashKey)) {
			if (applet.isKeyPressEvent() && !dashing) {
				setAnimation(ACTIONS.DASH);
				dashing = true;
			}
		}

		// Move on the y axis
		if (applet.keyPress(Options.jumpKey) || applet.keyPress(' ')) {
			if (applet.isKeyPressEvent() && !flying) { // && speedY == 0 && !flying
				flying = true;
				if (!dashing) {
					speedY -= (int) (speedJump * applet.getDeltaTime());
				} else {
					speedY -= (float) (speedJump * applet.getDeltaTime() * 1.2);
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
			swings.add(new Swing((int) baseObjectData.getPos().x, (int) baseObjectData.getPos().y, direction));
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

		if (DebugMode.ALL.equals(applet.getDebug())) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(baseObjectData.getPos().x, baseObjectData.getPos().y, collisionRange * 2, collisionRange * 2);
		}

		// All Collision Global Check
		for (int i = 0; i < gameScene.collidableObjects.size(); i++) {
			CollidableObject collision = gameScene.collidableObjects.get(i);
            if (Util.fastInRange(baseObjectData.getPos(), collision.getBaseObjectData().getPos(), collisionRange)) { // In Player Range

            	int collidableObjectWidth = collision.getBaseObjectData().getWidth();
            	int collidableObjectHeight = collision.getBaseObjectData().getHeight();
            	PVector collisionPosition = collision.getBaseObjectData().getPos();

				if (DebugMode.ALL.equals(applet.getDebug())) {
					applet.strokeWeight(2);
					applet.rect(collisionPosition.x, collisionPosition.y, collidableObjectWidth, collidableObjectHeight);
					applet.fill(255, 0, 0);
					applet.ellipse(collision.getBaseObjectData().getPos().x, collision.getBaseObjectData().getPos().y, 5, 5);
					applet.noFill();
				}
				if (collides(collision)) {
					if (px + baseObjectData.getWidth() / 2 < collisionPosition.x + collidableObjectWidth / 2) {
						baseObjectData.getPos().x = collisionPosition.x - collidableObjectWidth / 2 - baseObjectData.getWidth() / 2;
					} else if (px - baseObjectData.getWidth() / 2 > collisionPosition.x - collidableObjectWidth / 2) { // +collision.width/2
						baseObjectData.getPos().x = collisionPosition.x + collidableObjectHeight / 2 + baseObjectData.getHeight() / 2;
					}
					if (dashing) {
						dashing = false;
						setAnimation(ACTIONS.IDLE);
					}

				}
				if (collidesFuturX(collision)) {
					if (px < collisionPosition.x) {
						speedX = 0;
					} else if (px > collisionPosition.x) {
						speedX = 0;
					}
					if (dashing) {
						dashing = false;
						setAnimation(ACTIONS.IDLE);
					}
				}
				if (collidesFuturY(collision)) {
					if (py + collidableObjectHeight / 2 < collisionPosition.y) {
						baseObjectData.getPos().y = collisionPosition.y - collidableObjectHeight / 2 - baseObjectData.getHeight() / 2;
						speedY = 0;
						flying = false;
					} else if (baseObjectData.getPos().y > collisionPosition.y) {
						baseObjectData.getPos().y = collisionPosition.y + collidableObjectHeight / 2 + baseObjectData.getHeight() / 2;
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
			applet.getCamera().shake(0.2f); // TODO consider removing
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

		PVector pos = baseObjectData.getPos();

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
		PVector pos	= this.baseObjectData.getPos();
		PVector collidablePosition = collision.getBaseObjectData().getPos();
		int collidableWidth = collision.getBaseObjectData().getWidth();
		int collidableHeight = collision.getBaseObjectData().getHeight();
		return (pos.x + baseObjectData.getWidth() / 2 > collidablePosition.x - collidableWidth / 2
				&& pos.x - baseObjectData.getWidth() / 2 < collidablePosition.x + collidableWidth / 2)
				&& (pos.y + baseObjectData.getHeight() / 2 > collidablePosition.y - collidableHeight / 2
						&& pos.y - baseObjectData.getHeight() / 2 < collidablePosition.y + collidableHeight / 2);
	}

	// TODO: optimize these (unused)
	private boolean collidesEqual(CollidableObject collision) {
		PVector pos	= this.baseObjectData.getPos();
		PVector collidablePosition = collision.getBaseObjectData().getPos();
		int collidableWidth = collision.getBaseObjectData().getWidth();
		int collidableHeight = collision.getBaseObjectData().getHeight();
		return (pos.x + baseObjectData.getWidth() / 2 >= collidablePosition.x - collidableWidth / 2
				&& pos.x - baseObjectData.getWidth() / 2 <= collidablePosition.x + collidableWidth / 2)
				&& (pos.y + baseObjectData.getHeight() / 2 >= collidablePosition.y - collidableHeight / 2
						&& pos.y - baseObjectData.getHeight() / 2 <= collidablePosition.y + collidableHeight / 2);
	}

	private boolean collidesFutur(CollidableObject collision) {
		PVector pos	= this.baseObjectData.getPos();
		PVector collidablePosition = collision.getBaseObjectData().getPos();
		int collidableWidth = collision.getBaseObjectData().getWidth();
		int collidableHeight = collision.getBaseObjectData().getHeight();
		return (pos.x + speedX + baseObjectData.getWidth() / 2 > collidablePosition.x - collidableWidth / 2
				&& pos.x + speedX - baseObjectData.getWidth() / 2 < collidablePosition.x + collidableWidth / 2)
				&& (pos.y + speedY + baseObjectData.getHeight() / 2 > collidablePosition.y - collidableHeight / 2
						&& pos.y + speedY - baseObjectData.getHeight() / 2 < collidablePosition.y + collidableHeight / 2);
	}

	private boolean collidesFuturX(CollidableObject collision) {
		PVector pos	= this.baseObjectData.getPos();
		PVector collidablePosition = collision.getBaseObjectData().getPos();
		int collidableWidth = collision.getBaseObjectData().getWidth();
		int collidableHeight = collision.getBaseObjectData().getHeight();
		return (pos.x + speedX + baseObjectData.getWidth() / 2 > collidablePosition.x - collidableWidth / 2
				&& pos.x + speedX - baseObjectData.getWidth() / 2 < collidablePosition.x + collidableWidth / 2)
				&& (pos.y + 0 + baseObjectData.getHeight() / 2 > collidablePosition.y - collidableHeight / 2
						&& pos.y + 0 - baseObjectData.getHeight() / 2 < collidablePosition.y + collidableHeight / 2);
	}

	private boolean collidesFuturY(CollidableObject collision) {
		PVector pos	= this.baseObjectData.getPos();
		PVector collidablePosition = collision.getBaseObjectData().getPos();
		int collidableWidth = collision.getBaseObjectData().getWidth();
		int collidableHeight = collision.getBaseObjectData().getHeight();
		return (pos.x + 0 + baseObjectData.getWidth() / 2 > collidablePosition.x - collidableWidth / 2
				&& pos.x + 0 - baseObjectData.getWidth() / 2 < collidablePosition.x + collidableWidth / 2)
				&& (pos.y + speedY + baseObjectData.getHeight() / 2 > collidablePosition.y - collidableHeight / 2
						&& pos.y + speedY - baseObjectData.getHeight() / 2 < collidablePosition.y + collidableHeight / 2);
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
	 * @return the animation being used.
	 */
	private ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}
}
