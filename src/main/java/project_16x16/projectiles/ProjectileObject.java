package project_16x16.projectiles;

import java.util.ArrayList;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Time;
import project_16x16.components.AnimationComponent;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.scene.GameplayScene;

public class ProjectileObject extends EditableObject {

	/** How long projectiles live before being removed (ms). */
	public static final long LIFETIME_MILLIS = 10_000;

	public AnimationComponent animation;

	public PVector position;

	public PImage image;

	public int direction;
	public int prevDirection;
	public int width;
	public int height;

	/** Game time at which the projectile was created (ms). */
	public long spawnTime;

	/** px/s */
	public float speed;

	// Identification
	public String id;

	public boolean hit;

	public ProjectileObject(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);

		id = "";
		spawnTime = Time.millis();
		animation = new AnimationComponent();
		position = new PVector(0, 0);
	}

	@Override
	public void display() {
	}

	public void update() {
	}

	public boolean collides(CollidableObject collision) {
		return (position.x + width / 2 > collision.position.x - collision.width / 2 && position.x - width / 2 < collision.position.x + collision.width / 2)
				&& (position.y + height / 2 > collision.position.y - collision.height / 2
						&& position.y - height / 2 < collision.position.y + collision.height / 2);
	}

	protected ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}

	@Override
	public void debug() {
	}

	@Override
	public JSONObject exportToJSON() {
		return null;
	}
}
