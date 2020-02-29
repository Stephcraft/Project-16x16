package project_16x16.projectiles;

import java.util.ArrayList;

import project_16x16.Main;
import project_16x16.components.AnimationComponent;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.scene.GameplayScene;
import project_16x16.Tileset;

public class ProjectileObject extends EditableObject {

	public AnimationComponent animation;

	public PVector pos;

	public PImage image;

	public int direction;
	public int prevDirection;
	public int width;
	public int height;
	
	public int spawnTime;

	public int speed;

	// Identification
	public String id;

	public boolean hit;

	public ProjectileObject(Main a, GameplayScene g) {
		super(a, g);

		id = "";
		spawnTime = applet.frameCount;
		animation = new AnimationComponent();
		pos = new PVector(0, 0);
	}

	public void display() {
	}
	public void update() {
	}

	public boolean collides(CollidableObject collision) {
		return (pos.x + width / 2.0f > collision.pos.x - collision.width / 2.0f
				&& pos.x - width / 2.0f < collision.pos.x + collision.width / 2.0f)
				&& (pos.y + height / 2.0f > collision.pos.y - collision.height / 2.0f
						&& pos.y - height / 2.0f < collision.pos.y
								+ collision.height / 2.0f);
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
