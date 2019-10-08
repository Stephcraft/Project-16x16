package org.project16x16.objects;

import java.util.ArrayList;

import org.project16x16.components.AnimationComponent;
import processing.core.PImage;
import org.project16x16.scene.GameplayScene;
import org.project16x16.sidescroller.SideScroller;
import org.project16x16.sidescroller.Tileset;

/**
 * Extends {@link EditableObject}.
 */
public class GameObject extends EditableObject {

	// Animation Component
	public AnimationComponent animation;

	// Collision Component
	public CollidableObject collision;

	public PImage image;

	public GameObject(SideScroller a, GameplayScene g) {
		super(a, g);

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