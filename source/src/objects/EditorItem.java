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
					int x = (int) round((applet.getMouseX()) / 4) * 4;
					int y = (int) round((applet.getMouseY()) / 4) * 4;

					// Create new Instance
					switch (type) {
						case "COLLISION" :
							applet.collisions.add(new Collision(applet, id, 0, 0));
							applet.collisions.get(applet.collisions.size() - 1).focus();
							applet.collisions.get(applet.collisions.size() - 1).pos.x = x;
							applet.collisions.get(applet.collisions.size() - 1).pos.y = y;
							applet.keyPressEvent = false;
							break;
						case "BACKGROUND" :
							applet.backgroundObjects.add(new BackgroundObject(applet, id, 0, 0));
							applet.backgroundObjects.get(applet.backgroundObjects.size() - 1).focus();
							applet.backgroundObjects.get(applet.backgroundObjects.size() - 1).pos.x = x;
							applet.backgroundObjects.get(applet.backgroundObjects.size() - 1).pos.y = y;
							break;
						case "OBJECT" :
							GameObject obj = applet.gameGraphics.getObjectClass(id);

							applet.gameObjects.add(obj);
							applet.gameObjects.get(applet.gameObjects.size() - 1).focus();
							applet.gameObjects.get(applet.gameObjects.size() - 1).pos.x = x;
							applet.gameObjects.get(applet.gameObjects.size() - 1).pos.y = y;

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
			applet.rect(round((applet.getMouseX()) / 4) * 4, round((applet.getMouseY()) / 4) * 4, image.width, image.height);
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
