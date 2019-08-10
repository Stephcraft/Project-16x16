package objects;

import processing.core.PImage;
import processing.core.PVector;
import scene.SceneMapEditor;
import sidescroller.PClass;
import sidescroller.SideScroller;

/**
 * Extends {@link PClass}.
 */
public class EditableObject extends PClass {

	// Base Data
	public PVector pos;
	public int width;
	public int height;

	// Image data
	public String id;
	
	enum type {COLLISION, BACKGROUND, OBJECT}
	protected type type;

	// Focus
	public boolean focus;
	protected boolean focusX;
	protected boolean focusY;
	private boolean focusM;

	public boolean child;

	// Arrows Graphics
	private PImage editArrowX;
	private PImage editArrowY;
	private PImage editArrowXActive;
	private PImage editArrowYActive;

	// Map Editor Scene
	private SceneMapEditor scene;

	protected int editOffsetX;
	protected int editOffsetY;

	public EditableObject(SideScroller a) {
		super(a);

		pos = new PVector(0, 0);

		scene = (SceneMapEditor) a.mapEditor;

		// Get Edit Arrows
		editArrowX = util.pg(applet.graphicsSheet.get(268, 278, 6, 5), 4);
		editArrowY = util.pg(applet.graphicsSheet.get(275, 278, 5, 6), 4);
		editArrowXActive = util.pg(applet.graphicsSheet.get(268, 284, 6, 5), 4);
		editArrowYActive = util.pg(applet.graphicsSheet.get(275, 284, 5, 6), 4);
	}

	/**
	 * Draws position edit arrows and bounding box if the object is selected
	 * (focused) in MODIFY mode.
	 */
	public void displayEdit() {
		// When Focused
		if (focus) {

			// Setup Style
			applet.strokeWeight(1);

			// 16x16 support
			applet.noFill();
			applet.stroke(255, 100);
			applet.rect(pos.x, pos.y, 64, 64);

			// Main Border
			applet.noFill();
			applet.stroke(0, 255, 200);
			applet.rect(pos.x, pos.y, width, height);

			// Setup Style
			applet.strokeWeight(4);

			if (child) {
				return;
			}

			// Axis X
			if (focusX) {
				applet.stroke(255, 213, 63);
				applet.line(pos.x, pos.y, pos.x + 100, pos.y);
				applet.image(editArrowXActive, pos.x + 100, pos.y);
			} else {
				applet.stroke(239, 64, 96);
				applet.line(pos.x, pos.y, pos.x + 100, pos.y);
				applet.image(editArrowX, pos.x + 100, pos.y);
			}

			// Axis Y
			if (focusY) {
				applet.stroke(255, 213, 63);
				applet.line(pos.x, pos.y, pos.x, pos.y - 100);
				applet.image(editArrowYActive, pos.x, pos.y - 100);
			} else {
				applet.stroke(185, 255, 99);
				applet.line(pos.x, pos.y, pos.x, pos.y - 100);
				applet.image(editArrowY, pos.x, pos.y - 100);
			}
		}
	}

	public void updateEdit() {
		if (child) {
			return;
		}
		
		if (applet.mouseReleaseEvent) {
			focusX = false; // defocus move arrows
			focusY = false; // defocus move arrows
			focusM = false;
//			focus = false;
//			scene.focusedOnObject = false;
		}
		
		// Focus Event
		if (applet.mousePressEvent) {
			if (mouseHover()) { // Focus Enable
				if (scene.focusedObject == null) { // TODO
					focus = true;
					scene.focusedObject = this;
				}
			} else {
				if (focus && !mouseHoverX() && !mouseHoverY() ) { // Focus Disable
					scene.focusedObject = null;
					focus = false;
					focusX = false;
					focusY = false;
					focusM = false;
				}
			}
		}

		// When Focused
		if (focus) {
			// Focus Arrow Event
			if (applet.mousePressEvent) {
				if (mouseHoverX()) {
					focusX = true;
					focusY = false;
					focusM = false;
					editOffsetX = (int) pos.x + 100 - applet.getMouseX();
					editOffsetY = (int) pos.y - applet.getMouseY();
					scene.focusedObject = this;
				} else if (mouseHoverY()) {
					focusY = true;
					focusX = false;
					focusM = false;
					editOffsetX = (int) pos.x - applet.getMouseX();
					editOffsetY = (int) pos.y - 100 - applet.getMouseY();
					scene.focusedObject = this;
				} else if (mouseHover()) {
					focusM = true;
					editOffsetX = (int) pos.x - applet.getMouseX();
					editOffsetY = (int) pos.y - applet.getMouseY();
				}
			}

			// Duplicate Object Shift
			if (applet.mousePressed) {
				if (applet.keyPressEvent && applet.keyPress(16)) {

					// Duplicate Instance
					switch (type) {
						case COLLISION :
							applet.collidableObjects.add(new CollidableObject(applet, id, 0, 0));
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).focus = true;
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).focusX = focusX;
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).focusY = focusY;
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).pos.x = pos.x;
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).pos.y = pos.y;
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).editOffsetX = editOffsetX;
							applet.collidableObjects.get(applet.collidableObjects.size() - 1).editOffsetY = editOffsetY;
							applet.keyPressEvent = false;
							break;
						case OBJECT :
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
						default:
							break;
					}

					focus = false;
					focusX = false;
					focusY = false;

				}
			}

			// Focus Movement
			if (focusX) {
				pos.x = round((applet.getMouseX() + editOffsetX - 100) / 4) * 4;
			}
			if (focusY) {
				pos.y = round((applet.getMouseY() + editOffsetY + 100) / 4) * 4;
			}
			if (focusM) {
				pos.x = round((applet.getMouseX() + editOffsetX) / 4) * 4;
				pos.y = round((applet.getMouseY() + editOffsetY) / 4) * 4;
			}
		}
	}

	public void focus() {
		focus = true;
	}

	/**
	 * Is mouse hovering the x-axis slider for the object?
	 * 
	 * @return boolean true if mouse hovering.
	 */
	private boolean mouseHoverX() {
		return (applet.getMouseX() > pos.x + 100 - 6 * 4 && applet.getMouseX() < pos.x + 100 + 6 * 4)
				&& (applet.getMouseY() > pos.y - 5 * 4 && applet.getMouseY() < pos.y + 5 * 4);
	}

	/**
	 * Is mouse hovering the y-axis slider for the object?
	 * 
	 * @return boolean true if mouse hovering.
	 */
	private boolean mouseHoverY() {
		return (applet.getMouseX() > pos.x - 6 * 4 && applet.getMouseX() < pos.x + 6 * 4)
				&& (applet.getMouseY() > pos.y - 100 - 5 * 4 && applet.getMouseY() < pos.y - 100 + 5 * 4);
	}

	private boolean mouseHover() {
		if (applet.mouseX < 400 && applet.mouseY < 100) { // Over Inventory Bar
			return false;
		}
		return util.hover(pos.x, pos.y, width, height);
	}
}
