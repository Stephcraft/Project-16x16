package objects;

import processing.core.*;

import sidescroller.PClass;
import sidescroller.SideScroller;

public class EditorItem extends PClass {

	public PVector pos;
	public boolean focus;

	public PGraphics image;

	public String id;
	public String type;

	private String mode;

	public EditorItem(SideScroller a) {
		super(a);

		setTile("BOX");
		setMode("CREATE");

		pos = new PVector(0, 0);
	}

	public void display() {
		if (focus) {
			applet.image(image, pos.x, pos.y, image.width * (float) 0.5, image.height * (float) 0.5);
		}
	}

	public void update() {

		if (focus) {
			pos.x = applet.getMouseX();
			pos.y = applet.getMouseY();

			if (applet.mouseReleaseEvent) {
				focus = false;

				if (mode == "CREATE") {
					PVector realPos = applet.camera.getDispToCoord(new PVector(applet.getMouseX(), applet.getMouseY())); // transform from screen mouse pos to game pos
					// Create new instance from dragged icon
					switch (type) {
					case "COLLISION" :
						Collision c = new Collision(applet, id, 0, 0);
						c.pos.x = realPos.x;
						c.pos.y = realPos.y;
						c.focus();
						applet.collisions.add(c);
						break;
					case "BACKGROUND" :
						BackgroundObject bObject = new BackgroundObject(applet, id, 0, 0);
						bObject.pos.x = realPos.x;
						bObject.pos.y = realPos.y;
						bObject.focus();
						applet.backgroundObjects.add(bObject);
						break;
					case "OBJECT" :
						GameObject obj = applet.gameGraphics.getObjectClass(id);
						obj.focus();
						obj.pos.x = realPos.x;
						obj.pos.y = realPos.y;
						applet.gameObjects.add(obj);
						break;
					}
				}
			}
		}
	}

	public void displayDestination() {
		if (focus) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(round((applet.getMouseX()) / 4) * 4, round((applet.getMouseY()) / 4) * 4, image.width,
					image.height);
		}
	}

	public void setMode(String m) {
		mode = m;
	}

	public void setTile(String t) {
		image = applet.gameGraphics.get(t);
		type = applet.gameGraphics.getType(t);

		id = t;
	}
}
