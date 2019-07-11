package projectiles;

import java.util.ArrayList;

import components.AnimationComponent;
import objects.Collision;
import processing.core.PGraphics;
import processing.core.PVector;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class ProjectileObject extends PClass {

	public AnimationComponent animation;

	public PVector pos;

	public PGraphics image;

	public int direction;

	public int width;
	public int height;

	public int speed;

	// Identification
	public String id;

	public boolean hit;

	public ProjectileObject(SideScroller a) {
		super(a);

		id = "";

		animation = new AnimationComponent();
		pos = new PVector(0, 0);
	}

	public void display() {
	}
	public void update() {
	}

	public boolean collides(Collision collision) {
		return (pos.x - applet.originX + width / 2 > collision.pos.x - applet.originX - collision.width / 2
				&& pos.x - applet.originX - width / 2 < collision.pos.x - applet.originX + collision.width / 2)
				&& (pos.y - applet.originY + height / 2 > collision.pos.y - applet.originY - collision.height / 2
						&& pos.y - applet.originY - height / 2 < collision.pos.y - applet.originY
								+ collision.height / 2);
	}

	protected ArrayList<PGraphics> getAnimation(String id) {
		return applet.gameGraphics.getAnimation(id);
	}
}
