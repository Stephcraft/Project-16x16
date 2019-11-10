package project_16x16.objects;

import java.util.ArrayList;

import project_16x16.components.AnimationComponent;
import processing.core.PImage;
import project_16x16.scene.GameplayScene;
import project_16x16.SideScroller;
import project_16x16.Tileset;

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


	/**
	* Return the animation of a given tile 
	*
	* @param the name of a tile
	* @ return animation of given tile
	**/
	protected ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}

	/**
	* Returns a specific tile given its coordinates and dimensions
	* @param x coordinate
	* @param y coordinate
	* @param w width
	* @param h height
	*
	* @return a tile given its coordinates and dimensions
	**/
	protected PImage g(int x, int y, int w, int h) {
		return Tileset.getTile(x, y, w, h);
	}

	protected PImage g(int x, int y, int w, int h, float s) {
		return Tileset.getTile(x, y, w, h);
	}
} 
