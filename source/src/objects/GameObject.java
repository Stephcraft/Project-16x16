package objects;

import java.util.ArrayList;

import components.AnimationComponent;
import lombok.Data;
import processing.core.PImage;
import sidescroller.Tileset;

/**
 * Extends {@link EditableObject}.
 */
@Data
public class GameObject extends EditableObject {

	// Animation Component
	protected AnimationComponent animation;

	// Collision Component
	protected CollidableObject collision;

	protected PImage image;

	public GameObject() {
		super();

		animation = new AnimationComponent();
	}

	public void display() {
	}

	public void update() {
	}

	public void delete() {
	}

	protected ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}

	protected PImage g(int x, int y, int w, int h) {
		return Tileset.getTile(x, y, w, h);
	}

	// TODO: not sure s (size) should still be here
	protected PImage g(int x, int y, int w, int h, float s) {
		return Tileset.getTile(x, y, w, h);
	}
}