package entities;

import processing.core.*;
import projectiles.Swing;
//import sidescroller.PClass;
import sidescroller.SideScroller;

import java.util.ArrayList;

import components.AnimationComponent;
import objects.Collision;
import objects.EditableObject;

/**
 * <h1>Player Class</h1>
 * <p>
 * This class handles the playable character. It covers controlling, animating,
 * displaying, and updating the character. 
 * </p>
 */
public class Player extends EditableObject {
	public float px;
	public float py;

	public PGraphics image;

	PGraphics lifeOn;
	PGraphics lifeOff;

	public float gravity;

	public int prevKey;

	public float speedX;
	public float speedY;
	public float pSpeedX;
	public float pSpeedY;

	public int accX;
	public int accY;
	public int pAccX;
	public int pAccY;

	public int speedWalk;
	public int speedJump;

	public int direction;

	public int life;
	public int lifeCapacity;

	public boolean flying;
	public boolean pflying;

	public boolean attack;
	public boolean dashing;

	// Player Projectile
	public ArrayList<Swing> swings;

	// Animation Component
	public AnimationComponent animation;

	// Animation frames
	public ArrayList<PGraphics> anim_squish;
	public ArrayList<PGraphics> anim_idle;
	public ArrayList<PGraphics> anim_walk;
	public ArrayList<PGraphics> anim_attack;
	public ArrayList<PGraphics> anim_shoot;

	/**
	 * Constructor
	 * @param a SideScroller game controller.
	 */
	public Player(SideScroller a) {
		super(a);

		pos = new PVector(100, 300);
		gravity = 2;

		animation = new AnimationComponent();
		swings = new ArrayList<Swing>();

		anim_squish = new ArrayList<PGraphics>();
		anim_idle = new ArrayList<PGraphics>();
		anim_walk = new ArrayList<PGraphics>();
		anim_attack = applet.gameGraphics.getAnimation("PLAYER::WALK");
		anim_shoot = new ArrayList<PGraphics>();

		animation.length = 7;
		animation.loop = true;
		animation.rate = 6;

		// Set life
		lifeCapacity = 3;
		life = lifeCapacity;

		speedWalk = 10;
		speedJump = 25; // 20

		width = 14 * 4;
		height = 16 * 4;

		flying = true;
	}

	/**
	 * load any needed assets.
	 * @param sheet sprite sheet as PImage.
	 */
	public void load(PImage sheet) {
		image = util.pg(sheet.get(0, 258, 14, 14), 4);

		lifeOn = util.pg(sheet.get(144, 256, 9, 9), 4);
		lifeOff = util.pg(sheet.get(160, 256, 9, 9), 4);

		anim_walk.add(util.pg(sheet.get(0, 272, 14, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 1 + 2, 272, 14, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 2 + 4, 272, 14, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 3 + 6, 272, 14, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 4 + 8, 272, 15, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 5 + 10, 272, 15, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 6 + 12, 272, 15, 15), 4));
		anim_walk.add(util.pg(sheet.get(14 * 7 + 14, 272, 14, 15), 4));

		anim_squish.add(util.pg(sheet.get(0, 258, 14, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 1 + 2, 258, 14, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 2 + 4, 258, 14, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 3 + 6, 258, 14, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 4 + 8, 258, 15, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 5 + 10, 258, 15, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 6 + 12, 258, 15, 14), 4));
		anim_squish.add(util.pg(sheet.get(14 * 7 + 14, 258, 14, 14), 4));

		animation.frames = anim_walk;

		setAnimation("IDLE");
	}

	/**
	 * The display method controls how to display the character to the screen
	 * with what animation.
	 */
	public void display() {

		// Display Swing Projectiles
		for (int i = 0; i < swings.size(); i++) {
			swings.get(i).display();
		}

		if (direction == LEFT) {
			applet.pushMatrix();
			applet.translate(pos.x - applet.originX, pos.y - applet.originY);
			applet.scale(-1, 1);
			applet.image(image, 0, 0);
			applet.popMatrix();
		} else {
			applet.image(image, pos.x - applet.originX, pos.y - applet.originY);
		}

		if (applet.debug) {
			// debug info
			applet.fill(255, 0, 0);
			applet.textSize(15);
			applet.textAlign(RIGHT, CENTER);
			applet.text("X: " + pos.x + "Y: " + pos.y, applet.width, 5);
			applet.text("SX: " + speedX + " SY: " + speedY, applet.width, 15);
			applet.text("anim: " + animation.name, applet.width, 25);
			applet.text("f: " + animation.frame + " ends: " + animation.length, applet.width, 35);
			applet.text("fly: " + flying + " att: " + attack + " dash: " + dashing, applet.width, 45);

			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(pos.x, pos.y, width, height);
		}
	}

	/**
	 * The update method handles updating the character. 
	 */
	public void update() {
		image = animation.animate(applet.frameCount, applet.deltaTime);

		if (!dashing) {
			speedY += gravity * applet.deltaTime;
		} else {
			speedY += gravity * applet.deltaTime * .5;
		}

		// Save Previous State
		pSpeedX = speedX;
		pSpeedY = speedY;

		px = pos.x;
		py = pos.y;

		pflying = flying;
		prevKey = applet.key;

		// Move on the x axis
		if (applet.keyPress(RIGHT) || applet.keyPress(68)) {
			if (applet.keyPressEvent && !attack && !dashing) {
				setAnimation("WALK");
			}
			if (!dashing) {
				speedX = (speedWalk * applet.deltaTime);
			} else {
				speedX = (float) (speedWalk * applet.deltaTime * 1.5);
			}
			direction = RIGHT;
		} else if (applet.keyPress(LEFT) || applet.keyPress(65)) {
			if (applet.keyPressEvent && !attack && !dashing) {
				setAnimation("WALK");
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
		if (applet.keyPress(SHIFT)) {
			if (applet.keyPressEvent && !dashing) {
				setAnimation("DASH");
				dashing = true;
			}
		}

		// Move on the y axis
		if (applet.keyPress(UP) || applet.keyPress(' ')) {
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
					setAnimation("ATTACK");
				} else if (dashing) {
					setAnimation("DASH_ATTACK");
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
			setAnimation("WALK");
		}
		// End Dash Attack
		if (animation.name == "DASH_ATTACK" && animation.ended) {
			dashing = false;
			attack = false;
			if (flying) {
				speedY = 0;
			}
			setAnimation("WALK");
		}
		// End Attack
		if (animation.name == "ATTACK" && animation.ended) {
			attack = false;
			if (speedX != 0) {
				setAnimation("WALK");
			} else if (speedX == 0) {
				setAnimation("IDLE");
			}
		}
		// boolean collides = false;

		if (applet.debug) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(pos.x - applet.originX, pos.y - applet.originY, 400, 400);
		}

		// All Collision Global Check
		for (int i = 0; i < applet.collisions.size(); i++) {
			Collision collision = applet.collisions.get(i);

			// In Player Range
			if (PApplet.dist(pos.x, pos.y, collision.pos.x, collision.pos.y) < 200) {

				if (applet.debug) {
					applet.rect(collision.pos.x - applet.originX, collision.pos.y - applet.originY, 20, 20);
				}
				if (collides(collision)) {
					if (px - applet.originX + width / 2 < collision.pos.x + collision.width / 2 - applet.originX) {
						pos.x = collision.pos.x - collision.width / 2 - width / 2;
					} else if (px - applet.originX - width / 2 > collision.pos.x - collision.width / 2
							- applet.originX) { // +collision.width/2
						pos.x = collision.pos.x + collision.width / 2 + width / 2;
					}
					if (dashing) {
						dashing = false;
						setAnimation("IDLE");
					}

				}
				if (collidesFuturX(collision)) {
					if (px - applet.originX < collision.pos.x - applet.originX) {
						speedX = 0;
					} else if (px - applet.originX > collision.pos.x - applet.originX) {
						speedX = 0;
					}
					if (dashing) {
						dashing = false;
						setAnimation("IDLE");
					}
				}
				if (collidesFuturY(collision)) {
					if (py - applet.originY + height / 2 < collision.pos.y - applet.originY) {
						pos.y = collision.pos.y - collision.height / 2 - height / 2;
						speedY = 0;
						flying = false;
					} else if (pos.y - applet.originY > collision.pos.y - applet.originY) {
						pos.y = collision.pos.y + collision.height / 2 + height / 2;
						speedY = 0;
					}
				}
			}
		}

		if (flying && !attack && !dashing) {
			setAnimation("FALL");
		}

		// On Ground Event
		if (!flying && pflying && !attack && !dashing) {
			setAnimation("SQUISH");
		}

		// Idle Animation
		if (speedX == 0 && speedY == 0 && !attack && !dashing && !flying && !pflying) {
			setAnimation("IDLE");
		}

		if (animation.name == "SQUISH" && speedX != 0 && !attack) {

			setAnimation("WALK");
		}

		if (animation.name == "WALK" && speedX == 0 && !attack) {

			setAnimation("IDLE");
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
			pos.y = 0;
			pos.x = 50;
		}

		// Apply World Transformation
		if (pos.x - applet.originX - width / 2 < applet.width / 2 - applet.screenX / 2) {
			applet.originTargetX -= PApplet.abs(speedX - 5);
		} else if (pos.x - applet.originX + width / 2 > applet.width / 2 + applet.screenX / 2) {
			applet.originTargetX += PApplet.abs(speedX + 5);
		}
		if (pos.y - applet.originY - height / 2 < applet.height / 2 - applet.screenY / 2) {
			applet.originTargetY -= PApplet.abs(speedY - 5);
		} else if (pos.y - applet.originY + height / 2 > applet.height / 2 + applet.screenY / 2) {
			applet.originTargetY += PApplet.abs(speedY + 5);
		}

		// Update Swing Projectiles
		for (int i = 0; i < swings.size(); i++) {
			swings.get(i).update();
		}
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
	public boolean collides(Collision collision) {
		return (pos.x - applet.originX + width / 2 > collision.pos.x - applet.originX - collision.width / 2
				&& pos.x - applet.originX - width / 2 < collision.pos.x - applet.originX + collision.width / 2)
				&& (pos.y - applet.originY + height / 2 > collision.pos.y - applet.originY - collision.height / 2
						&& pos.y - applet.originY - height / 2 < collision.pos.y - applet.originY
								+ collision.height / 2);
	}

	// TODO: optimize these
	public boolean collidesEqual(Collision collision) {
		return (pos.x - applet.originX + width / 2 >= collision.pos.x - applet.originX - collision.width / 2
				&& pos.x - applet.originX - width / 2 <= collision.pos.x - applet.originX + collision.width / 2)
				&& (pos.y - applet.originY + height / 2 >= collision.pos.y - applet.originY - collision.height / 2
						&& pos.y - applet.originY - height / 2 <= collision.pos.y - applet.originY
								+ collision.height / 2);
	}

	public boolean collidesFutur(Collision collision) {
		return (pos.x - applet.originX + speedX + width / 2 > collision.pos.x - applet.originX - collision.width / 2
				&& pos.x - applet.originX + speedX - width / 2 < collision.pos.x - applet.originX + collision.width / 2)
				&& (pos.y - applet.originY + speedY + height / 2 > collision.pos.y - applet.originY
						- collision.height / 2
						&& pos.y - applet.originY + speedY - height / 2 < collision.pos.y - applet.originY
								+ collision.height / 2);
	}

	public boolean collidesFuturX(Collision collision) {
		return (pos.x - applet.originX + speedX + width / 2 > collision.pos.x - applet.originX - collision.width / 2
				&& pos.x - applet.originX + speedX - width / 2 < collision.pos.x - applet.originX + collision.width / 2)
				&& (pos.y - applet.originY + 0 + height / 2 > collision.pos.y - applet.originY - collision.height / 2
						&& pos.y - applet.originY + 0 - height / 2 < collision.pos.y - applet.originY
								+ collision.height / 2);
	}

	public boolean collidesFuturY(Collision collision) {
		return (pos.x - applet.originX + 0 + width / 2 > collision.pos.x - applet.originX - collision.width / 2
				&& pos.x - applet.originX + 0 - width / 2 < collision.pos.x - applet.originX + collision.width / 2)
				&& (pos.y - applet.originY + speedY + height / 2 > collision.pos.y - applet.originY
						- collision.height / 2
						&& pos.y - applet.originY + speedY - height / 2 < collision.pos.y - applet.originY
								+ collision.height / 2);
	}

	/**
	 * Sets the current animation for the Player to use
	 * @param anim the animation id
	 */
	public void setAnimation(String anim) {
		switch (anim) {
			case "WALK" :
				animation.frames = getAnimation("PLAYER::WALK"); // anim_walk;
				animation.loop = true;
				animation.length = 7;
				animation.rate = 6; // 6
				animation.frame = 0;
				animation.start = 0;
				break;
			case "IDLE" :
				animation.frames = getAnimation("PLAYER::IDLE"); // anim_squish;
				animation.loop = true;
				animation.length = 3;
				animation.rate = 20;
				animation.frame = 0;
				animation.start = 0;
				break;
			case "SQUISH" :
				animation.frames = getAnimation("PLAYER::SQUISH"); // anim_squish;
				animation.loop = false;
				animation.length = 5; // 7
				animation.rate = 4;
				animation.frame = 0; // 3
				animation.start = 0; // 3
				break;
			case "FALL" :
				animation.frames = anim_squish;
				animation.loop = false;
				animation.length = 0;
				animation.rate = 6;
				animation.frame = 0;
				animation.start = 0;
				break;
			case "ATTACK" :
				animation.frames = getAnimation("PLAYER::ATTACK");
				animation.loop = false;
				animation.length = 3;
				animation.rate = 4;
				animation.frame = 0;
				animation.start = 0;
				break;
			case "DASH" :
				animation.frames = getAnimation("PLAYER::SQUISH");
				animation.loop = false;
				animation.length = 4;
				animation.rate = 6;
				animation.frame = 0;
				animation.start = 0;
				break;
			case "DASH_ATTACK" :
				animation.frames = getAnimation("PLAYER::ATTACK");
				animation.loop = false;
				animation.length = 2 + animation.remainingFrames();
				animation.rate = 4;
				animation.frame = 0;
				animation.start = 0;
		}
		animation.ended = false;
		animation.pName = animation.name;
		animation.name = anim;
	}

	/**
	 * getter for the currently used animation.
	 * @param id the animation id
	 * @return the animation being used.
	 */
	private ArrayList<PGraphics> getAnimation(String id) {
		return applet.gameGraphics.getAnimation(id);
	}
}
