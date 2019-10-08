package org.project16x16.objects;

import processing.core.PImage;
import processing.core.PVector;
import org.project16x16.scene.GameplayScene;
import org.project16x16.sidescroller.SideScroller;
import org.project16x16.sidescroller.SideScroller.debugType;
import org.project16x16.sidescroller.Tileset;

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
}
