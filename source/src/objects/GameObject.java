package objects;

import java.util.ArrayList;

import components.AnimationComponent;
import processing.core.PImage;
import sidescroller.SideScroller;
import sidescroller.Tileset;

/**
 * Extends {@link EditableObject}.
 */
public class GameObject extends EditableObject {

	// Animation Component
	public AnimationComponent animation;

	// Collision Component
	public Collision collision;

	public PImage image;

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

	protected ArrayList<PImage> getAnimation(String id) {
		return Tileset.getAnimation(id);
	}

	protected PImage g(int x, int y, int w, int h) {
		return util.pg(Tileset.getTile(x, y, w, h));
	}

	protected PImage g(int x, int y, int w, int h, float s) {
		return util.pg(Tileset.getTile(x, y, w, h), s);
	}
}