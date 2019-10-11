package objects;

import java.lang.reflect.Constructor;

import processing.core.PImage;
import processing.core.PVector;
import scene.GameplayScene;
import sidescroller.PClass;
import sidescroller.SideScroller;
import sidescroller.Tileset;
import sidescroller.Util;

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

	enum type {
		COLLISION, BACKGROUND, OBJECT
	}

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
	public GameplayScene gameScene;

	protected PVector editOffset;

	public EditableObject() {
		super();

		pos = new PVector(0, 0);
		editOffset =  new PVector(0, 0);

		this.gameScene = applet.getGame();

		// Get Edit Arrows
		editArrowX = Tileset.getTile(268, 278, 6, 5, 4);
		editArrowY = Tileset.getTile(275, 278, 5, 6, 4);
		editArrowXActive = Tileset.getTile(268, 284, 6, 5, 4);
		editArrowYActive = Tileset.getTile(275, 284, 5, 6, 4);
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

		if (applet.isMouseReleaseEvent()) {
			focusX = false; // defocus move arrows
			focusY = false; // defocus move arrows
			focusM = false;
			return;
		}

		// Focus Event
		if (applet.isMousePressEvent()) {
			if (mouseHover()) { // Focus Enable
				if (gameScene.focusedObject == null) {
					focus = true;
					gameScene.focusedObject = this;
				}
			} else {
				if (focus && !mouseHoverX() && !mouseHoverY()) { // Focus Disable
					gameScene.focusedObject = null;
					focus = false;
					focusX = false;
					focusY = false;
					focusM = false;
				}
			}
		}
		
		if (focus) { // When Focused
			if (applet.isMousePressEvent()) {
				if (mouseHoverX()) {
					focusX = true;
					focusY = false;
					focusM = false;
					editOffset = PVector.sub(pos, applet.getMouseCoordGame()).sub(new PVector(-100, 0));
					gameScene.focusedObject = this;
				} else if (mouseHoverY()) {
					focusY = true;
					focusX = false;
					focusM = false;
					editOffset = PVector.sub(pos, applet.getMouseCoordGame()).sub(new PVector(0, 100));
					gameScene.focusedObject = this;
				} else if (mouseHover()) {
					focusM = true;
					editOffset = PVector.sub(pos, applet.getMouseCoordGame());
				}
			}

			// Duplicate Object Shift
				if (applet.isKeyPressEvent() && applet.keyPress(SideScroller.SHIFT)) {
					EditableObject copy; // Duplicate Instance
					switch (type) {
						case COLLISION :
							copy = new CollidableObject(id, 0, 0);
							copy.focus = true;
							copy.focusX = focusX;
							copy.focusY = focusY;
							copy.pos = pos.copy();
							copy.editOffset = editOffset.copy();
							gameScene.collidableObjects.add((CollidableObject) copy);
							break;
						case OBJECT :
							try {
								Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(id);
								Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
								copy = (GameObject) ctor.newInstance(new Object[] { applet, this });
								copy.focus = true;
								copy.focusX = focusX;
								copy.focusY = focusY;
								copy.pos = pos.copy();
								copy.editOffset = editOffset.copy();
								gameScene.gameObjects.add((GameObject) copy);
								break;
							} catch (Exception e) {
								e.printStackTrace();
							}							
							switch (id) {
								case "MIRROR_BOX" :
									((MirrorBoxObject) gameScene.gameObjects.get(gameScene.gameObjects.size()
											- 1)).direction = ((MirrorBoxObject) this).direction;
									break;
							}
							break;
						default :
							break;
					}
					applet.setKeyPressEvent(false);
					focus = false;
					focusX = false;
					focusY = false;
				}

			// Focus Movement
			if (focusX) {
				pos.x = Util.roundToNearest(applet.getMouseCoordGame().x + editOffset.y - 100, applet.getSnapSize());
			}
			if (focusY) {
				pos.y = Util.roundToNearest(applet.getMouseCoordGame().y + editOffset.y + 100, applet.getSnapSize());
			}
			if (focusM) {
				pos = new PVector(Util.roundToNearest(applet.getMouseCoordGame().x + editOffset.x, applet.getSnapSize()),
						Util.roundToNearest(applet.getMouseCoordGame().y + editOffset.y, applet.getSnapSize()));
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
		return (applet.getMouseCoordGame().x > pos.x + 100 - 6 * 4 && applet.getMouseCoordGame().x < pos.x + 100 + 6 * 4)
				&& (applet.getMouseCoordGame().y > pos.y - 5 * 4 && applet.getMouseCoordGame().y < pos.y + 5 * 4);
	}

	/**
	 * Is mouse hovering the y-axis slider for the object?
	 * 
	 * @return boolean true if mouse hovering.
	 */
	private boolean mouseHoverY() {
		return (applet.getMouseCoordGame().x > pos.x - 6 * 4 && applet.getMouseCoordGame().x < pos.x + 6 * 4)
				&& (applet.getMouseCoordGame().y > pos.y - 100 - 5 * 4 && applet.getMouseCoordGame().y < pos.y - 100 + 5 * 4);
	}

	private boolean mouseHover() {
		if (applet.mouseX < 400 && applet.mouseY < 100) { // Over Inventory Bar
			return false;
		}
		return Util.hoverGame(pos.x, pos.y, width, height);
	}
}
