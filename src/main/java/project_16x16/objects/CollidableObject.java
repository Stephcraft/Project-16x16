package project_16x16.objects;

import processing.core.*;
import processing.data.JSONObject;
import project_16x16.Main;
import project_16x16.scene.GameplayScene;
import project_16x16.Main.debugType;
import project_16x16.Tileset;

public class CollidableObject extends EditableObject {

	private PImage image;

	public String flag;

	private float pixelOffsetX = 0;
	private float pixelOffsetY = 0;

	public CollidableObject(Main a, GameplayScene g) {
		super(a, g);

		flag = "";
		type = type.COLLISION;
		pos = new PVector(0, 0);
	}

	public CollidableObject(Main a, GameplayScene g, int w, int h, int x, int y, boolean ch) {
		this(a, g, w, h, x, y);

		child = ch;
	}

	public CollidableObject(Main a, GameplayScene g, int w, int h, int x, int y) {
		this(a, g);

		// Get From Game Graphics Image
		pos = new PVector(x, y);
		width = w;
		height = h;
	}

	public CollidableObject(Main a, GameplayScene g, String t, int x, int y) {
		this(a, g);

		// Get From Game Graphics Image
		setGraphic(t);
		pos = new PVector(x, y);
	}

	public void display() {

		if (height / 4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if (width / 4 % 2 != 0) {
			pixelOffsetX = 2;
		}

		if (id == null) {
			if (applet.debug == debugType.ALL) {
				applet.noFill();
				applet.strokeWeight(1);
				applet.stroke(0, 255, 200);
				applet.rect(pos.x + pixelOffsetX, pos.y + pixelOffsetY, width, height);
			}
		} else {
			applet.image(image, pos.x + pixelOffsetX, pos.y + pixelOffsetY, width, height);
		}
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
		applet.stroke(50, 120, 255);
		applet.noFill();
		applet.rect(pos.x, pos.y, width, height);
	}

	@Override
	public JSONObject exportToJSON() {
		JSONObject item = new JSONObject();
		item.setString("id", id);
		item.setString("type", "COLLISION");
		item.setInt("x", (int) pos.x);
		item.setInt("y", (int) pos.y);
		return item;
	}

}
