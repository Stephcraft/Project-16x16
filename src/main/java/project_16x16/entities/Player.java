package project_16x16.entities;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.Audio;
import project_16x16.Audio.SFX;
import project_16x16.Constants;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.SideScroller.DebugType;
import project_16x16.Tileset;
import project_16x16.Time;
import project_16x16.Utility;
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

	/** Velocity (px/s). */
	private final PVector velocity = new PVector(0, 0);
	/** Displacement over the current physics step (px); reused to avoid allocation. */
	private final PVector displacement = new PVector(0, 0);

	private static final int COLLISION_RANGE = 145;
	private static final float DASH_MULTIPLIER = 1.5f; // Movement multiplier from holding dash key
	private static final float JUMP_HEIGHT = 153; // px
	/** How long an attack lasts (ms); the player can't attack again until it ends. */
	private static final float ATTACK_MILLIS = 150;
	private static final float DASH_ATTACK_MILLIS = 83; // attacks recover faster while dashing

	private final float speedWalk; // px/s
	private final float speedJump; // px/s

	private final boolean isMultiplayerPlayer;

	public int life; // public for debugging TODO make private
	public int lifeCapacity; // public for debugging TODO make private

	public ArrayList<Swing> swings; // Player Projectile TODO make private
	public AnimationComponent animation; // Animation Component. TODO make private

	/** Time left in the current attack (ms). */
	private float attackRemaining = 0;
	/** Animation of the current attack, fixed when the attack starts. */
	private ACTION attackAction = ACTION.ATTACK;

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
		playerAnimationSequences = new HashMap<>();
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
	 * @param sideScroller SideScroller game controller.
	 */
	public Player(SideScroller sideScroller, GameplayScene gameplayScene, boolean isMultiplayerPlayer) {
		super(sideScroller, gameplayScene);

		position = new PVector(100, 300); // Spawn LOC. TODO get from current level

		animation = new AnimationComponent();
//		animation.setSFX(Audio.SFX.STEP, 2);
		swings = new ArrayList<>();
		image = Tileset.getTile(0, 258, 14, 14, 4);
		lifeOn = Tileset.getTile(144, 256, 9, 9, 4);
		lifeOff = Tileset.getTile(160, 256, 9, 9, 4);

		// Set life
		lifeCapacity = 6;
		life = 3;

		speedWalk = 420;
		speedJump = (float) Math.sqrt(2 * Constants.GAME_GRAVITY * JUMP_HEIGHT); // launch speed that peaks at JUMP_HEIGHT

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
	@Override
	public void display() {
		// Display Swing Projectiles
		for (Swing swing : swings) {
			swing.display();
		}

		applet.pushMatrix();
		applet.translate(position.x, position.y);
		if (state.facingDir == LEFT) {
			applet.scale(-1, 1);
		}
		if (isMultiplayerPlayer) {
			applet.tint(255, 125, 0);
		}
		image = animation.getFrame();
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
		velocity.x = 0;

		handleKeyboardInput();
		handleMouseInput();

		final int steps = Time.substeps(Constants.PHYSICS_MAX_STEP);
		final float dt = Time.delta() / steps;
		for (int i = 0; i < steps; i++) {
			step(dt);
		}

		animation.update();
		chooseAnimation();
		if (position.y > 2000) { // out of bounds check
			position.set(0, -100); // TODO set to spawn loc PVector
			velocity.mult(0);
		}
		if (applet.debug == DebugType.ALL) {
			applet.noFill();
			applet.stroke(255, 0, 0);
			applet.strokeWeight(1);
			applet.ellipse(position.x, position.y, COLLISION_RANGE * 2, COLLISION_RANGE * 2);
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
			velocity.x = speedWalk * (state.dashing ? DASH_MULTIPLIER : 1);
			state.facingDir = RIGHT;
		}
		if (applet.isKeyDown(Options.moveLeftKey)) { // Move Left
			velocity.x = -speedWalk * (state.dashing ? DASH_MULTIPLIER : 1);
			state.facingDir = LEFT;
		}
	}

	private void handleMouseInput() {
		attackRemaining = Math.max(0, attackRemaining - Time.deltaMillis());
		state.attacking = attackRemaining > 0;
		if (applet.mousePressed && applet.mouseButton == LEFT && !state.attacking) { // Attack
			state.attacking = true;
			attackRemaining = state.dashing ? DASH_ATTACK_MILLIS : ATTACK_MILLIS;
			attackAction = state.dashing ? ACTION.DASH_ATTACK : ACTION.ATTACK;
			// Create Swing Projectile
			swings.add(new Swing(applet, gameplayScene, (int) position.x, (int) position.y, state.facingDir));
		}
		for (Swing swing : swings) { // Update Swing Projectiles
			swing.update();
		}
		swings.removeIf(Swing::expired);
	}

	/**
	 * Advances the player's motion by one physics step, resolving collisions.
	 *
	 * @param dt step duration (seconds)
	 */
	private void step(float dt) {
		// exact for constant acceleration, so jump arcs don't depend on step size
		displacement.set(velocity.x * dt, velocity.y * dt + 0.5f * Constants.GAME_GRAVITY * dt * dt);
		velocity.y += Constants.GAME_GRAVITY * dt;

		checkPlayerCollision();
		if (velocity.y != 0) {
			state.flying = true;
		}
		position.add(displacement);
	}

	/**
	 * Checks whether the player's next {@link #displacement} collides with nearby
	 * collision objects; if so, snaps the player to the object's edge and cancels
	 * motion along that axis.
	 */
	private void checkPlayerCollision() {
		for (EditableObject o : gameplayScene.objects) {
			if (o instanceof CollidableObject) {
				CollidableObject collision = (CollidableObject) o;
				if (Utility.fastInRange(position, collision.position, COLLISION_RANGE)) { // In Player Range
					if (applet.debug == DebugType.ALL) {
						applet.strokeWeight(2);
						applet.rect(collision.position.x, collision.position.y, collision.width, collision.height);
						applet.fill(255, 0, 0);
						applet.ellipse(collision.position.x, collision.position.y, 5, 5);
						applet.noFill();
					}
					if (collidesAfterMove(collision, displacement.x, 0)) {
						// player left of collision
						if (position.x < collision.position.x) {
							position.x = collision.position.x - collision.width / 2 - width / 2;
							// player right of collision
						} else {
							position.x = collision.position.x + collision.width / 2 + width / 2;
						}
						velocity.x = 0;
						displacement.x = 0;
						state.dashing = false;
					}
					if (collidesAfterMove(collision, 0, displacement.y)) {
						// player above collision
						if (position.y < collision.position.y) {
							if (state.flying) {
								state.landing = true;
							}
							position.y = collision.position.y - collision.height / 2 - height / 2;
							state.flying = false;
							// player below collision
						} else {
							position.y = collision.position.y + collision.height / 2 + height / 2;
							state.jumping = false;
						}
						velocity.y = 0;
						displacement.y = 0;
					}
				}
			}
		}
	}

	/**
	 * Picks the animation that reflects the player's current state.
	 */
	private void chooseAnimation() {
		// The jump/land squashes are purely visual: they play once, then give way to
		// the state-based animation. Gameplay state never waits on an animation.
		if (animation.ended) {
			if (animation.name.equals(ACTION.JUMP.name())) {
				state.jumping = false;
			} else if (animation.name.equals(ACTION.LAND.name())) {
				state.landing = false;
			}
		}
		if (state.jumping) {
			setAnimation(ACTION.JUMP);
		} else if (state.landing) {
			setAnimation(ACTION.LAND);
		} else if (state.attacking) {
			setAnimation(attackAction);
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
	 * Determines whether the character would collide with an object after moving
	 * by (dx, dy).
	 */
	private boolean collidesAfterMove(CollidableObject collision, float dx, float dy) {
		return (position.x + dx + width / 2 > collision.position.x - collision.width / 2
				&& position.x + dx - width / 2 < collision.position.x + collision.width / 2)
				&& (position.y + dy + height / 2 > collision.position.y - collision.height / 2
						&& position.y + dy - height / 2 < collision.position.y + collision.height / 2);
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
			case WALK:
				animation.changeAnimation(animSequence, true, 100);
				break;
			case IDLE:
				animation.changeAnimation(animSequence, true, 333);
				break;
			case JUMP:
				animation.changeAnimation(animSequence, false, 67);
				break;
			case LAND:
				animation.changeAnimation(animSequence, false, 33);
				break;
			case FALL:
				animation.changeAnimation(animSequence, true, 333);
				break;
			case ATTACK: // spans the attack
				animation.changeAnimation(animSequence, false, ATTACK_MILLIS / animSequence.size());
				break;
			case DASH:
				animation.changeAnimation(animSequence, true, 100);
				break;
			case DASH_ATTACK:
				animation.changeAnimation(animSequence, false, DASH_ATTACK_MILLIS / animSequence.size());
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
