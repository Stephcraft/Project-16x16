package project_16x16.objects;

import java.util.ArrayList;

import processing.core.PImage;
import processing.data.JSONObject;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.components.AnimationComponent;
import project_16x16.scene.GameplayScene;

/**
 * Extends {@link EditableObject}.
 */
public class GameObject extends EditableObject {

	// Animation Component
	public AnimationComponent animation;

	// Collision Component
	public CollidableObject collision;

	public PImage image;

	public GameObject(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);

		animation = new AnimationComponent();
	}

	@Override
	public void display() {
	}

	public void update() {
	}

	public void delete() {
	}

	/**
	 *
	 * @param name the name of a tile
	 * @return the animation of a given tile
	 */
	protected ArrayList<PImage> getAnimation(String name) {
		return Tileset.getAnimation(name);
	}

	/**
	 * Returns a specific tile given its coordinates and dimensions
	 *
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

	@Override
	public void debug() {
		applet.stroke(255, 190, 200);
		applet.noFill();
		applet.rect(position.x, position.y, width, height);
	}

	@Override
	public JSONObject exportToJSON() {
		JSONObject item = new JSONObject();
		item.setString("id", id);
		item.setString("type", "OBJECT");
		item.setInt("x", (int) position.x);
		item.setInt("y", (int) position.y);
		return item;
	}
}
