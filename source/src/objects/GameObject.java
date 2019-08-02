package objects;

import java.util.ArrayList;

import components.AnimationComponent;
import processing.core.PImage;
import sidescroller.SideScroller;

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
		return applet.gameGraphics.getAnimation(id);
	}

	protected PImage g(int x, int y, int w, int h) {
		return applet.gameGraphics.g(x, y, w, h);
	}

	protected PImage g(int x, int y, int w, int h, float s) {
		return applet.gameGraphics.g(x, y, w, h, s);
	}
}