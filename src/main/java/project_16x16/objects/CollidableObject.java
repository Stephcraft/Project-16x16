package project_16x16.objects;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.SideScroller;
import project_16x16.SideScroller.DebugType;
import project_16x16.Tileset;
import project_16x16.scene.GameplayScene;

public class CollidableObject extends EditableObject {

	private PImage image;

	public String flag;

	private float pixelOffsetX = 0;
	private float pixelOffsetY = 0;

	public CollidableObject(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);

		flag = "";
		type = ObjectType.COLLISION;
		position = new PVector(0, 0);
	}

	public CollidableObject(SideScroller sideScroller, GameplayScene gameplayScene, int w, int h, int x, int y, boolean child) {
		this(sideScroller, gameplayScene, w, h, x, y);

		super.child = child;
	}

	public CollidableObject(SideScroller sideScroller, GameplayScene gameplayScene, int w, int h, int x, int y) {
		this(sideScroller, gameplayScene);

		// Get From Game Graphics Image
		position = new PVector(x, y);
		width = w;
		height = h;
	}

	public CollidableObject(SideScroller sideScroller, GameplayScene gameplayScene, String t, int x, int y) {
		this(sideScroller, gameplayScene);

		// Get From Game Graphics Image
		setGraphic(t);
		position = new PVector(x, y);
	}

	public void display() {
		if (height / 4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if (width / 4 % 2 != 0) {
			pixelOffsetX = 2;
		}
		if (id == null) {
			if (applet.debug == DebugType.ALL) {
				applet.noFill();
				applet.strokeWeight(1);
				applet.stroke(0, 255, 200);
				applet.rect(position.x + pixelOffsetX, position.y + pixelOffsetY, width, height);
			}
		}
		else {
			applet.image(image, position.x + pixelOffsetX, position.y + pixelOffsetY, width, height);
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
		applet.rect(position.x, position.y, width, height);
	}

	@Override
	public JSONObject exportToJSON() {
		JSONObject item = new JSONObject();
		item.setString("id", id);
		item.setString("type", "COLLISION");
		item.setInt("x", (int) position.x);
		item.setInt("y", (int) position.y);
		return item;
	}

}
