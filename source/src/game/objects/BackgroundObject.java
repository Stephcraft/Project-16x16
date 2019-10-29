package game.objects;

import processing.core.PImage;
import processing.core.PVector;
import game.engine.sidescroller.Tileset;

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

		baseObjectData.setPos(new PVector(x, y));
		setGraphic(id);
	}

	public void display() {
		float pixelOffsetX = 0;
		float pixelOffsetY = 0;

		if (baseObjectData.getHeight() / 4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if (baseObjectData.getWidth() / 4 % 2 != 0) {
			pixelOffsetX = 2;
		}

		applet.image(image, baseObjectData.getPos().x + pixelOffsetX, baseObjectData.getPos().y + pixelOffsetY);
	}

	public void update() {

	}

	public void setGraphic(String name) {
		image = Tileset.getTile(name);
		baseObjectData.setId(name);
		baseObjectData.setWidth(image.width);
		baseObjectData.setHeight(image.height);
	}
}
