package objects;

import processing.core.PImage;
import processing.core.PVector;
import sidescroller.SideScroller;

public class BackgroundObject extends EditableObject {

	public PImage image;

	public BackgroundObject(SideScroller a) {
		super(a);

		type = type.BACKGROUND;
	}

	public BackgroundObject(SideScroller a, String id) {
		this(a);

		setGraphic(id);
	}

	public BackgroundObject(SideScroller a, String id, int x, int y) {
		this(a);

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

		if (SideScroller.DEBUG) {
			applet.noStroke();
			applet.fill(255);
			applet.ellipse(pos.x, pos.y, 5, 5);
			applet.noFill();
			applet.stroke(50, 255, 120);
			applet.rect(pos.x, pos.y, width, height);
		}
	}

	public void update() {

	}

	public void setGraphic(String _id) {
		image = applet.gameGraphics.get(_id);
		id = _id;
		width = image.width;
		height = image.height;
	}
}
