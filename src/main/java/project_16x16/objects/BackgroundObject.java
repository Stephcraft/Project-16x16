package project_16x16.objects;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;

import project_16x16.scene.GameplayScene;
import project_16x16.SideScroller;
import project_16x16.Tileset;

public class BackgroundObject extends EditableObject {

	public PImage image;

	public BackgroundObject(SideScroller a, GameplayScene g) {
		super(a, g);

		type = type.BACKGROUND;
	}

	public BackgroundObject(SideScroller a, GameplayScene g, String id) {
		this(a, g);
		setGraphic(id);
	}

	public BackgroundObject(SideScroller a, GameplayScene g, String id, int x, int y) {
		this(a, g);

		pos = new PVector(x, y);
		setGraphic(id);
	}

	public void display() {
		float pixelOffsetX = 0;
		float pixelOffsetY = 0;

		if (height / 4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if (width / 4 % 2 != 0) {
			pixelOffsetX = 2;
		}

		applet.image(image, pos.x + pixelOffsetX, pos.y + pixelOffsetY);
	}

	public void update() {

	}

	public void setGraphic(String name) {
		image = Tileset.getTile(name);
		id = name;
		width = image.width;
		height = image.height;
	}

	@Override
	public void debug() {
		applet.stroke(50, 255, 120);
		applet.noFill();
		applet.rect(pos.x, pos.y, width, height);
	}

	@Override
	public JSONObject exportToJSON() {
		JSONObject item = new JSONObject();
		item.setString("id", id);
		item.setString("type", "BACKGROUND");
		item.setInt("x", (int) pos.x);
		item.setInt("y", (int) pos.y);
		return item;
	}
}
