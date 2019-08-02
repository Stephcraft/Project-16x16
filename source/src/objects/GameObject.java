package objects;

import java.util.ArrayList;

import components.AnimationComponent;
import processing.core.PGraphics;
import sidescroller.SideScroller;

/**
 * Extends {@link EditableObject}.
 */
public class GameObject extends EditableObject {

	// Animation Component
	public AnimationComponent animation;

	// Collision Component
	public Collision collision;

	public PGraphics image;

	public GameObject(SideScroller a) {
		super(a);

		animation = new AnimationComponent();
	}

	public void display() {
	}
	public void update() {
	}

	public void delete() {
	}

	protected ArrayList<PGraphics> getAnimation(String id) {
		return applet.tileset.getAnimationGraphic(id, 4);
	}

	protected PGraphics g(int x, int y, int w, int h) {
		return util.pg(applet.tileset.getTile(x, y, w, h));
	}

	protected PGraphics g(int x, int y, int w, int h, float s) {
		return util.pg(applet.tileset.getTile(x, y, w, h), s);
	}
}