package objects;

import processing.core.*;
import scene.GameplayScene;
import sidescroller.SideScroller;
import sidescroller.SideScroller.debugType;
import sidescroller.Tileset;


public class CollidableObject extends EditableObject {

	private PImage image;

	public String flag;

	private float pixelOffsetX = 0;
	private float pixelOffsetY = 0;

	public CollidableObject(SideScroller a, GameplayScene g) {
		super(a, g);

		flag = "";
		type = type.COLLISION;
		pos = new PVector(0, 0);
	}

	public CollidableObject(SideScroller a, GameplayScene g, int w, int h, int x, int y, boolean ch) {
		this(a, g, w, h, x, y);

		child = ch;
	}

	public CollidableObject(SideScroller a, GameplayScene g, int w, int h, int x, int y) {
		this(a, g);

		// Get From Game Graphics Image
		pos = new PVector(x, y);
		width = w;
		height = h;
	}

	public CollidableObject(SideScroller a, GameplayScene g, String t, int x, int y) {
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
			applet.image(image, pos.x + pixelOffsetX, pos.y + pixelOffsetY);
		}
	}

	public void setGraphic(String name) {
		image = Tileset.getTile(name);
		id = name;
		width = image.width;
		height = image.height;
	}
}
