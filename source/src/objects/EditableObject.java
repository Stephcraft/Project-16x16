package objects;

import processing.core.PGraphics;
import processing.core.PVector;
import scene.SceneMapEditor;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class EditableObject extends PClass {

	// Base Data
	public PVector pos;
	public int width;
	public int height;

	// Image data
	public String id;
	public String type;

	// Focus
	public boolean focus;
	public boolean focusX;
	public boolean focusY;
	public boolean focusM;

	public boolean child;

	// Arrows Graphics
	public PGraphics editArrowX;
	public PGraphics editArrowY;
	public PGraphics editArrowXActive;
	public PGraphics editArrowYActive;

	// Map Editor Scene
	public SceneMapEditor scene;

	int editOffsetX;
	int editOffsetY;

	public EditableObject(SideScroller a) {
		super(a);

		pos = new PVector(0, 0);

		scene = (SceneMapEditor) a.scene;

		// Get Edit Arrows
		editArrowX = util.pg(applet.graphicsSheet.get(268, 278, 6, 5), 4);
		editArrowY = util.pg(applet.graphicsSheet.get(275, 278, 5, 6), 4);
		editArrowXActive = util.pg(applet.graphicsSheet.get(268, 284, 6, 5), 4);
		editArrowYActive = util.pg(applet.graphicsSheet.get(275, 284, 5, 6), 4);
	}

	public void displayEdit() {
		// When Focused
		if (focus) {

			// Setup Style
			applet.strokeWeight(1);

			// 16x16 support
			applet.noFill();
			applet.stroke(255, 100);
			applet.rect(pos.x - applet.originX, pos.y - applet.originY, 64, 64);

			// Main Border
			applet.noFill();
			applet.stroke(0, 255, 200);
			applet.rect(pos.x - applet.originX, pos.y - applet.originY, width, height);

			// Setup Style
			applet.strokeWeight(4);

			if (child) {
				return;
			}

			// Axis X
			if (focusX) {
				applet.stroke(255, 213, 63);
				applet.line(pos.x - applet.originX, pos.y - applet.originY, pos.x + 100 - applet.originX,
						pos.y - applet.originY);
				applet.image(editArrowXActive, pos.x + 100 - applet.originX, pos.y - applet.originY);
			} else {
				applet.stroke(239, 64, 96);
				applet.line(pos.x - applet.originX, pos.y - applet.originY, pos.x + 100 - applet.originX,
						pos.y - applet.originY);
				applet.image(editArrowX, pos.x + 100 - applet.originX, pos.y - applet.originY);
			}

			// Axis Y
			if (focusY) {
				applet.stroke(255, 213, 63);
				applet.line(pos.x - applet.originX, pos.y - applet.originY, pos.x - applet.originX,
						pos.y - 100 - applet.originY);
				applet.image(editArrowYActive, pos.x - applet.originX, pos.y - 100 - applet.originY);
			} else {
				applet.stroke(185, 255, 99);
				applet.line(pos.x - applet.originX, pos.y - applet.originY, pos.x - applet.originX,
						pos.y - 100 - applet.originY);
				applet.image(editArrowY, pos.x - applet.originX, pos.y - 100 - applet.originY);
			}
		}
	}

	public void updateEdit() {
		if (child) {
			return;
		}

		// Focus Event
		if (applet.mousePressEvent) {
			if (mouseHover()) {
				// Focus Enable
				if (!scene.focusedOnObject) {
					focus = true;
					scene.focusedOnObject = true;
				}
			} else {
				// Focus Disable
				if (!mouseHoverX() && !mouseHoverY()) {
					focus = false;
					focusX = false;
					focusY = false;
					focusM = false;
				}
			}
		}
		if (applet.mouseReleaseEvent) {
			focusX = false;
			focusY = false;
			focusM = false;
		}

		// When Focused
		if (focus) {

			// Focus Arrow Event
			if (applet.mousePressEvent) {
				if (mouseHoverX()) {
					focusX = true;
					focusY = false;
					focusM = false;
					editOffsetX = (int) pos.x + 100 - applet.mouseX;
					editOffsetY = (int) pos.y - applet.mouseY;
					scene.focusedOnObject = true;
				} else if (mouseHoverY()) {
					focusY = true;
					focusX = false;
					focusM = false;
					editOffsetX = (int) pos.x - applet.mouseX;
					editOffsetY = (int) pos.y - 100 - applet.mouseY;
					scene.focusedOnObject = true;
				} else if (mouseHover()) {
					focusM = true;
					editOffsetX = (int) pos.x - applet.mouseX;
					editOffsetY = (int) pos.y - applet.mouseY;
				}
			}

			// Duplicate Object Shift
			if (applet.mousePressed) {
				if (applet.keyPressEvent && applet.keyPress(16)) {

					// Duplicate Instance
					switch (type) {
						case "COLLISION" :
							applet.collisions.add(new Collision(applet, id, 0, 0));
							applet.collisions.get(applet.collisions.size() - 1).focus = true;
							applet.collisions.get(applet.collisions.size() - 1).focusX = focusX;
							applet.collisions.get(applet.collisions.size() - 1).focusY = focusY;
							applet.collisions.get(applet.collisions.size() - 1).pos.x = pos.x;
							applet.collisions.get(applet.collisions.size() - 1).pos.y = pos.y;
							applet.collisions.get(applet.collisions.size() - 1).editOffsetX = editOffsetX;
							applet.collisions.get(applet.collisions.size() - 1).editOffsetY = editOffsetY;
							applet.keyPressEvent = false;
							break;
						case "OBJECT" :
							applet.gameObjects.add(applet.gameGraphics.getObjectClass(id));
							applet.gameObjects.get(applet.gameObjects.size() - 1).focus = true;
							applet.gameObjects.get(applet.gameObjects.size() - 1).focusX = focusX;
							applet.gameObjects.get(applet.gameObjects.size() - 1).focusY = focusY;
							applet.gameObjects.get(applet.gameObjects.size() - 1).pos.x = pos.x;
							applet.gameObjects.get(applet.gameObjects.size() - 1).pos.y = pos.y;
							applet.gameObjects.get(applet.gameObjects.size() - 1).editOffsetX = editOffsetX;
							applet.gameObjects.get(applet.gameObjects.size() - 1).editOffsetY = editOffsetY;

							switch (id) {
								case "MIRROR_BOX" :
									((MirrorBoxObject) applet.gameObjects.get(applet.gameObjects.size()
											- 1)).direction = ((MirrorBoxObject) this).direction;
									break;
							}

							applet.keyPressEvent = false;
							break;
					}

					focus = false;
					focusX = false;
					focusY = false;

				}
			}

			// Focus Movement
			if (focusX) {
				pos.x = round((applet.mouseX + editOffsetX - 100) / 4) * 4;
			}
			if (focusY) {
				pos.y = round((applet.mouseY + editOffsetY + 100) / 4) * 4;
			}
			if (focusM) {
				pos.x = round((applet.mouseX + editOffsetX) / 4) * 4;
				pos.y = round((applet.mouseY + editOffsetY) / 4) * 4;
			}
		}
	}

	public void focus() {
		focus = true;
	}

	// Utility
	private boolean mouseHoverX() {
		return (applet.mouseX > pos.x - applet.originX + 100 - 6 * 4
				&& applet.mouseX < pos.x - applet.originX + 100 + 6 * 4)
				&& (applet.mouseY > pos.y - applet.originY - 5 * 4 && applet.mouseY < pos.y - applet.originY + 5 * 4);
	}

	private boolean mouseHoverY() {
		return (applet.mouseX > pos.x - applet.originX - 6 * 4 && applet.mouseX < pos.x - applet.originX + 6 * 4)
				&& (applet.mouseY > pos.y - applet.originY - 100 - 5 * 4
						&& applet.mouseY < pos.y - applet.originY - 100 + 5 * 4);
	}

	private boolean mouseHover() {
		if (applet.mouseX < 400 && applet.mouseY < 100) {
			return false;
		} // On Inventory Bar

		return (applet.mouseX > pos.x - applet.originX - width / 2
				&& applet.mouseX < pos.x - applet.originX + width / 2)
				&& (applet.mouseY > pos.y - applet.originY - height / 2
						&& applet.mouseY < pos.y - applet.originY + height / 2);
	}
}
