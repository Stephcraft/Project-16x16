package objects;

import processing.core.*;
import sidescroller.SideScroller;

public class Collision extends EditableObject {

	private PImage image;

	public String flag;

	private float pixelOffsetX = 0;
	private float pixelOffsetY = 0;

	public Collision(SideScroller a) {
		super(a);

		flag = "";
		type = "COLLISION";
		pos = new PVector(0, 0);
	}

	public Collision(SideScroller a, int w, int h, int x, int y, boolean ch) {
		this(a, w, h, x, y);

		child = ch;
	}

	public Collision(SideScroller a, int w, int h, int x, int y) {
		this(a);

		// Get From Game Graphics Image
		pos = new PVector(x, y);
		width = w;
		height = h;
	}

	public Collision(SideScroller a, String t, int x, int y) {
		this(a);

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
			if (SideScroller.DEBUG) {
				applet.noFill();
				applet.strokeWeight(1);
				applet.stroke(0, 255, 200);
				applet.rect(pos.x + pixelOffsetX, pos.y + pixelOffsetY, width, height);
			}
		} else {
			applet.image(image, pos.x + pixelOffsetX, pos.y + pixelOffsetY);
		}

		if (SideScroller.DEBUG) {
			applet.noStroke();
			applet.fill(255);
			applet.ellipse(pos.x, pos.y, 5, 5);
			applet.noFill();
			applet.stroke(50, 120, 255);
			applet.rect(pos.x, pos.y, width, height);
		}
	}

	public void setGraphic(String _id) {
		image = applet.gameGraphics.get(_id);
		id = _id;
		width = image.width;
		height = image.height;
	}
}
