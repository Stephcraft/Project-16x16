package game.projectiles;

import java.util.ArrayList;

import components.AnimationComponent;
import game.objects.BaseObjectData;
import game.objects.CollidableObject;
import game.objects.EditableObject;
import processing.core.PImage;
import processing.core.PVector;
import game.engine.sidescroller.Tileset;

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

	public ProjectileObject() {
		super();

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
		BaseObjectData collisionBaseObjectData = collision.getBaseObjectData();

		return (pos.x + width / 2 > collisionBaseObjectData.getPos().x - collisionBaseObjectData.getWidth() / 2
				&& pos.x - width / 2 < collisionBaseObjectData.getPos().x + collisionBaseObjectData.getWidth() / 2)
				&& (pos.y + height / 2 > collisionBaseObjectData.getPos().y - collisionBaseObjectData.getHeight() / 2
						&& pos.y - height / 2 < collisionBaseObjectData.getPos().y
								+ collisionBaseObjectData.getHeight() / 2);
	}

	protected ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}
}
