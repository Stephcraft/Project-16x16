package projectiles;

import java.util.ArrayList;

import components.AnimationComponent;
import objects.CollidableObject;
import processing.core.PImage;
import processing.core.PVector;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class ProjectileObject extends PClass {

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

	public ProjectileObject(SideScroller a) {
		super(a);

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
		return (pos.x + width / 2 > collision.pos.x - collision.width / 2
				&& pos.x - width / 2 < collision.pos.x + collision.width / 2)
				&& (pos.y + height / 2 > collision.pos.y - collision.height / 2
						&& pos.y - height / 2 < collision.pos.y
								+ collision.height / 2);
	}

	protected ArrayList<PImage> getAnimation(String id) {
		return applet.gameGraphics.getAnimation(id);
	}
}
