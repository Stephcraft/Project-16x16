package objects;

import processing.core.PImage;
import processing.core.PVector;
import scene.GameplayScene;
import sidescroller.Tileset;

public class BackgroundObject extends EditableObject {

	public PImage image;

	public BackgroundObject() {
		super();

		type = type.BACKGROUND;
	}

	public BackgroundObject(String id) {
		this();
		setGraphic(id);
	}

	public BackgroundObject(String id, int x, int y) {
		this();

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
