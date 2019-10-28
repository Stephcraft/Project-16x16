package objects;

import java.util.ArrayList;

import components.AnimationComponent;
import processing.core.PImage;
import scene.GameplayScene;
import sidescroller.SideScroller;
import sidescroller.Tileset;

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
@@ -48,4 +63,4 @@ protected PImage g(int x, int y, int w, int h) {
	protected PImage g(int x, int y, int w, int h, float s) {
		return Tileset.getTile(x, y, w, h);
	}
} 
}
