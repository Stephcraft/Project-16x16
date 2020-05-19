package project_16x16.entities;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.*;
import project_16x16.Main;
import project_16x16.Main.debugType;
import project_16x16.Audio.SFX;
import project_16x16.components.AnimationComponent;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.projectiles.Swing;
import project_16x16.scene.GameplayScene;

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
	private PImage image;

	private final PImage lifeOn;
	private final PImage lifeOff;

	private final PVector velocity = new PVector(0, 0);

	private static final int collisionRange = 145;
	private static final float dashMultiplier = 1.5f; // Movement multiplier from holding dash key

	private final int speedWalk;
	private final int speedJump;
	
	private final boolean isMultiplayerPlayer;

	public int life; // public for debugging TODO make private
	public int lifeCapacity; // public for debugging TODO make private

	public ArrayList<Swing> swings; // Player Projectile TODO make private
	public AnimationComponent animation; // Animation Component. TODO make private

	/**
	 * Cache PImage animation sequences (rather than loading from JSON)
	 */
	private static final HashMap<ACTION, ArrayList<PImage>> playerAnimationSequences;

	/**
	 * Possible player actions/states
	 */
	private enum ACTION {
		WALK, IDLE, JUMP, LAND, FALL, ATTACK, DASH, DASH_ATTACK
	}

	private PlayerState state;

	static {
		playerAnimationSequences = new HashMap<ACTION, ArrayList<PImage>>();
		playerAnimationSequences.put(ACTION.WALK, Tileset.getAnimation("PLAYER::WALK"));
		playerAnimationSequences.put(ACTION.IDLE, Tileset.getAnimation("PLAYER::IDLE"));
		playerAnimationSequences.put(ACTION.JUMP, Tileset.getAnimation("PLAYER::SQUISH"));
		playerAnimationSequences.put(ACTION.LAND, Tileset.getAnimation("PLAYER::SQUISH"));
		playerAnimationSequences.put(ACTION.FALL, Tileset.getAnimation("PLAYER::IDLE"));
		playerAnimationSequences.put(ACTION.ATTACK, Tileset.getAnimation("PLAYER::ATTACK"));
		playerAnimationSequences.put(ACTION.DASH, Tileset.getAnimation("PLAYER::SQUISH"));
		playerAnimationSequences.put(ACTION.DASH_ATTACK, Tileset.getAnimation("PLAYER::ATTACK"));
	}

	/**
	 * Constructor
	 * 
	 * @param a SideScroller game controller.
	 */
	public Player(Main a, GameplayScene g , boolean isMultiplayerPlayer) {

		super(a, g);

		pos = new PVector(100, 300); // Spawn LOC. TODO get from current level

		animation = new AnimationComponent();
//		animation.setSFX(Audio.SFX.STEP, 2);
		swings = new ArrayList<Swing>();
		image = Tileset.getTile(0, 258, 14, 14, 4);
		lifeOn = Tileset.getTile(144, 256, 9, 9, 4);
		lifeOff = Tileset.getTile(160, 256, 9, 9, 4);

		// Set life
		lifeCapacity = 6;
		life = 3;

		speedWalk = 7;
		speedJump = 18;

		width = 14 * 4;
		height = 16 * 4;

		state = new PlayerState();

		setAnimation(ACTION.IDLE);
		this.isMultiplayerPlayer = isMultiplayerPlayer;
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
		if (isMultiplayerPlayer) {
			applet.tint(255, 125, 0);
			image = animation.getFrame();
		}
		else {
			image = animation.animate();
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
		velocity.set(0, velocity.y + Constants.GAME_GRAVITY);

		handleKeyboardInput();
		handleMouseInput();

		checkPlayerCollision();
		if (velocity.y != 0) {
			state.flying = true;
		}
		pos.add(velocity);
		
		chooseAnimation();

		if (pos.y > 2000) { // out of bounds check
			pos.set(0, -100); // TODO set to spawn loc PVector
			velocity.mult(0);
		}
		
		if (applet.debug == debugType.ALL) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(pos.x, pos.y, collisionRange * 2, collisionRange * 2);
		}
	}

	/**
	 * Displays life capacity as long as the character has health.
	 */
	public void displayLife() {
		applet.noStroke();
		applet.fill(100, 130, 145, 100);
		applet.rectMode(CORNER);
		applet.rect(50 - 20, applet.gameResolution.y - 50 - 20, 40 * lifeCapacity, 40);
		applet.rectMode(CENTER);
		for (int i = 0; i < lifeCapacity; i++) {
			image(lifeOff, 50 + 40 * i, applet.gameResolution.y - 50);
			if (i < life) {
				image(lifeOn, 50 + 40 * i, applet.gameResolution.y - 50);
			}
		}
	}

	public PVector getVelocity() {
		return velocity.copy();
	}
	
	public PlayerState getState() {
		return state;
	}

	private void handleKeyboardInput() {
		state.dashing = applet.isKeyDown(Options.dashKey);

		if (applet.isKeyDown(Options.jumpKey)) { // Jump
			if (!state.flying) {
				state.flying = true;
				state.jumping = true;
				velocity.y -= speedJump;
				Audio.play(SFX.JUMP);
				if (state.dashing) {
					state.dashing = false;
					velocity.y *= 1.2f;
				}
			}
		}

		if (applet.isKeyDown(Options.moveRightKey)) { // Move Right
			velocity.x = speedWalk * (state.dashing ? dashMultiplier : 1);
			state.facingDir = RIGHT;
		}

		if (applet.isKeyDown(Options.moveLeftKey)) { // Move Left
			velocity.x = -speedWalk * (state.dashing ? dashMultiplier : 1);
			state.facingDir = LEFT;
		}
	}

	private void handleMouseInput() {
		if (applet.mousePressed && applet.mouseButton == LEFT && !state.attacking) { // Attack
			state.attacking = true;
			// Create Swing Projectile
			swings.add(new Swing(applet, gameScene, (int) pos.x, (int) pos.y, state.facingDir));
		}
		for (int i = 0; i < swings.size(); i++) { // Update Swing Projectiles
			swings.get(i).update();
		}
	}

	private void checkPlayerCollision() {
		for (EditableObject o : gameScene.objects) {
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
		}
	}

	private void chooseAnimation() {
		// End animations
		if (animation.ended) {
			switch (animation.name) {
				case "DASH" :
					state.dashing = false;
					break;
				case "DASH_ATTACK" :
					state.dashing = false;
					state.attacking = false;
					break;
				case "ATTACK" :
					state.attacking = false;
					break;
				case "JUMP" :
					state.jumping = false;
					break;
				case "LAND" :
					state.landing = false;
				default :
					break;
			}
		}

		if (state.jumping) {
			setAnimation(ACTION.JUMP);
		} else if (state.landing) {
			setAnimation(ACTION.LAND);
		} else if (state.attacking) {
			if (state.dashing) {
				setAnimation(ACTION.DASH_ATTACK);
			} else {
				setAnimation(ACTION.ATTACK);
			}
		} else if (state.flying) {
			setAnimation(ACTION.FALL);
		} else if (velocity.x != 0) {
			if (state.dashing) {
				setAnimation(ACTION.DASH);
			} else {
				setAnimation(ACTION.WALK);
			}
		} else {
			setAnimation(ACTION.IDLE);
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
	
	public void setAnimation(String anim) {
		animation.ended = false;
		setAnimation(ACTION.valueOf(anim));
	}

	/**
	 * Sets the current animation sequence for the Player to use
	 * 
	 * @param anim the animation id
	 */
	private void setAnimation(ACTION anim) {
		if (animation.name == anim.name() && !animation.ended) {
			return;
		}
		ArrayList<PImage> animSequence = playerAnimationSequences.get(anim);

		switch (anim) {
			case WALK :
				animation.changeAnimation(animSequence, true, 6);
				break;
			case IDLE :
				animation.changeAnimation(animSequence, true, 20);
				break;
			case JUMP :
				animation.changeAnimation(animSequence, false, 4);
				break;
			case LAND :
				animation.changeAnimation(animSequence, false, 2);
				break;
			case FALL :
				animation.changeAnimation(animSequence, true, 20);
				break;
			case ATTACK :
				animation.changeAnimation(animSequence, false, 4);
				break;
			case DASH :
				animation.changeAnimation(animSequence, false, 6);
				break;
			case DASH_ATTACK :
				animation.changeAnimation(animSequence, false, 2);
				break;
		}
		animation.ended = false;
		animation.name = anim.name();
	}

	public class PlayerState {
		public boolean flying;
		public boolean attacking;
		public boolean dashing;
		public int facingDir;
		public boolean landing;
		public boolean jumping;

		PlayerState() {
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
