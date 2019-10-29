package game.objects;

import processing.core.*;
import game.engine.sidescroller.DebugMode;
import game.engine.sidescroller.Tileset;


public class CollidableObject extends EditableObject {

	private PImage image;

	public String flag;

	private float pixelOffsetX = 0;
	private float pixelOffsetY = 0;

	public CollidableObject() {
		super();

		flag = "";
		type = type.COLLISION;
		baseObjectData.setPos(new PVector(0, 0));
	}

	public CollidableObject(int w, int h, int x, int y, boolean ch) {
		this(w, h, x, y);

		child = ch;
	}

	public CollidableObject( int w, int h, int x, int y) {
		this();

		// Get From Game Graphics Image
		baseObjectData.setPos(new PVector(x, y));
		baseObjectData.setWidth(w);
		baseObjectData.setHeight(h);
	}

	public CollidableObject(String t, int x, int y) {
		this();

		// Get From Game Graphics Image
		setGraphic(t);
		baseObjectData.setPos(new PVector(x, y));
	}

	public void display() {

		if (baseObjectData.getHeight() / 4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if (baseObjectData.getWidth() / 4 % 2 != 0) {
			pixelOffsetX = 2;
		}

		if (baseObjectData.getId() == null) {
			if (DebugMode.ALL.equals(applet.getDebug())) {
				applet.noFill();
				applet.strokeWeight(1);
				applet.stroke(0, 255, 200);
				applet.rect(baseObjectData.getPos().x + pixelOffsetX, baseObjectData.getPos().y + pixelOffsetY, baseObjectData.getWidth(), baseObjectData.getHeight());
			}
		} else {
			applet.image(image, baseObjectData.getPos().x + pixelOffsetX, baseObjectData.getPos().y + pixelOffsetY);
		}
	}

	public void setGraphic(String name) {
		image = Tileset.getTile(name);
		baseObjectData.setId(name);
		baseObjectData.setWidth(image.width);
		baseObjectData.setHeight(image.height);
	}
}
