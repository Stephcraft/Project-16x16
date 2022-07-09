package project_16x16.objects;

import java.lang.reflect.Constructor;

import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Utility;
import project_16x16.scene.GameplayScene;

/**
 * Extends {@link PClass}.
 */
public abstract class EditableObject extends PClass {

	// Base Data
	public PVector position;
	public int width;
	public int height;

	// Image data
	public String id;

	enum type {
		COLLISION, BACKGROUND, OBJECT
	}

	protected type type;

	// Focus
	private boolean focus;

	/**
	 * Child of gameObject.
	 */
	public boolean child;

	// Map Editor Scene
	public GameplayScene gameplayScene;

	protected PVector editOffset;

	public EditableObject(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller);

		position = new PVector(0, 0);
		editOffset = new PVector(0, 0);
		this.gameplayScene = gameplayScene;
	}

	public abstract void display();

	public abstract void debug();

	public abstract JSONObject exportToJSON();

	/**
	 * Draws position edit arrows and bounding box if the object is selected
	 * (focused) in MODIFY mode.
	 */
	public void displayEdit() {
		if (focus) { // focus = held by player
			// draw border around object
			applet.strokeWeight(10);
			applet.noFill();
			applet.stroke(0, 255, 200);
			applet.rect(position.x, position.y, width, height);
			applet.strokeWeight(4); // reset style
		}
	}

	/**
	 * Called during modify mode when mouse pressed
	 */
	public void updateEdit() {
		if (child) {
			return;
		}
		if (applet.mouseReleaseEvent && applet.mouseButton == LEFT) {
			focus = false;
			return;
		}

		// Focus Event
		if (applet.mousePressEvent && applet.mouseButton == LEFT && !focus) {
			if (mouseHover()) { // Focus Enable
				if (gameplayScene.focusedObject == null) {
					focus = true;
					gameplayScene.focusedObject = this;
				}
			}
			else {
				if (focus) { // Focus Disable
					gameplayScene.focusedObject = null;
					focus = false;
				}
			}
		}
		if (focus) { // When Focused
			if (applet.mousePressEvent) {
				if (mouseHover()) {
					focus = true;
					editOffset = PVector.sub(position, applet.getMouseCoordGame());
				}
			}

			// Duplicate Object Shift
			if (applet.keyPressEvent && applet.isKeyDown(SideScroller.SHIFT)) {
				EditableObject copy; // Duplicate Instance
				switch (type) {
					case COLLISION:
						copy = new CollidableObject(applet, gameplayScene, id, 0, 0);
						copy.focus = true;
						copy.position = position.copy();
						copy.editOffset = editOffset.copy();
						gameplayScene.objects.add(copy);
						break;
					case OBJECT:
						try {
							Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(id);
							Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
							copy = (GameObject) ctor.newInstance(new Object[] { applet, this });
							copy.focus = true;
							copy.position = position.copy();
							copy.editOffset = editOffset.copy();
							gameplayScene.objects.add(copy);
							break;
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						switch (id) {
							case "MIRROR_BOX":
								((MirrorBoxObject) gameplayScene.objects.get(gameplayScene.objects.size() - 1)).direction = ((MirrorBoxObject) this).direction;
								break;
						}
						break;
					default:
						break;
				}
				applet.keyPressEvent = false;
				focus = false;
			}
			if (focus && applet.mousePressed && applet.mouseButton == LEFT) {
				position = new PVector(Utility.roundToNearest(applet.getMouseCoordGame().x + editOffset.x, SideScroller.snapSize), Utility.roundToNearest(applet.getMouseCoordGame().y + editOffset.y, SideScroller.snapSize));
			}
		}
	}

	public void focus() {
		editOffset = PVector.sub(position, applet.getMouseCoordGame());
		focus = true;
	}

	public void unFocus() {
		focus = false;
	}

	public boolean isFocused() {
		return focus;
	}

	public boolean mouseHover() {
		if (applet.mouseX < 400 && applet.mouseY < 100) { // Over Inventory Bar -- rough approximation
			return false;
		}
		return Utility.hoverGame(position.x, position.y, width, height);
	}
}
