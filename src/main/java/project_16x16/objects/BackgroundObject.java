package project_16x16.objects;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.scene.GameplayScene;

public class BackgroundObject extends EditableObject {

	public PImage image;

	public BackgroundObject(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);

		type = ObjectType.BACKGROUND;
	}

	public BackgroundObject(SideScroller sideScroller, GameplayScene gameplayScene, String id) {
		this(sideScroller, gameplayScene);
		setGraphic(id);
	}

	public BackgroundObject(SideScroller sideScroller, GameplayScene gameplayScene, String id, int x, int y) {
		this(sideScroller, gameplayScene);

		position = new PVector(x, y);
		setGraphic(id);
	}

	@Override
	public void display() {
		float pixelOffsetX = 0;
		float pixelOffsetY = 0;

		if (height / 4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if (width / 4 % 2 != 0) {
			pixelOffsetX = 2;
		}

		applet.image(image, position.x + pixelOffsetX, position.y + pixelOffsetY, width, height);
	}

	public void setGraphic(String name) {
		image = Tileset.getTile(name);
		id = name;
		width = image.width;
		height = image.height;
	}

	// WARNING: This can distort images
	public void setImageWidth(int w) {
		width = w;
	}

	public void setImageHeight(int h) {
		height = h;
	}

	@Override
	public void debug() {
		applet.stroke(50, 255, 120);
		applet.noFill();
		applet.rect(position.x, position.y, width, height);
	}

	@Override
	public JSONObject exportToJSON() {
		JSONObject item = new JSONObject();
		item.setString("id", id);
		item.setString("type", "BACKGROUND");
		item.setInt("x", (int) position.x);
		item.setInt("y", (int) position.y);
		return item;
	}
}
