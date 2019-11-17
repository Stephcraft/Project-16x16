package project_16x16.objects;

import java.lang.reflect.Constructor;

import processing.core.PVector;
import processing.data.JSONObject;

import project_16x16.scene.GameplayScene;
import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Util;

/**
 * Extends {@link PClass}.
 */
public abstract class EditableObject extends PClass {

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
	private boolean focus;

	/**
	 * Child of gameObject.
	 */
	public boolean child;

	// Map Editor Scene
	public GameplayScene gameScene;

	protected PVector editOffset;

	public EditableObject(SideScroller a, GameplayScene g) {
		super(a);

		pos = new PVector(0, 0);
		editOffset = new PVector(0, 0);
		gameScene = g;
	}
	
	public abstract void display();
	public abstract void debug();
	public abstract JSONObject exportToJSON(); 

	/**
	 * Draws position edit arrows and bounding box if the object is selected
	 * (focused) in MODIFY mode.
	 */
	public void displayEdit() {
		// When Focused
		if (focus) {

			// Setup Style
			applet.strokeWeight(3);

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
		}
	}

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
				if (gameScene.focusedObject == null) {
					focus = true;
					gameScene.focusedObject = this;
				}
			} else {
				if (focus
//						&& !mouseHoverX() && !mouseHoverY()
				) { // Focus Disable
					gameScene.focusedObject = null;
					focus = false;
				}
			}
		}

		if (focus) { // When Focused
			if (applet.mousePressEvent) {
				if (mouseHover()) {
					focus = true;
					editOffset = PVector.sub(pos, applet.getMouseCoordGame());
				}
			}

			// Duplicate Object Shift
			if (applet.keyPressEvent && applet.isKeyDown(SideScroller.SHIFT)) {
				EditableObject copy; // Duplicate Instance
				switch (type) {
					case COLLISION :
						copy = new CollidableObject(applet, gameScene, id, 0, 0);
						copy.focus = true;
						copy.pos = pos.copy();
						copy.editOffset = editOffset.copy();
						gameScene.objects.add(copy);
						break;
					case OBJECT :
						try {
							Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(id);
							Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
							copy = (GameObject) ctor.newInstance(new Object[] { applet, this });
							copy.focus = true;
							copy.pos = pos.copy();
							copy.editOffset = editOffset.copy();
							gameScene.objects.add(copy);
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
						switch (id) {
							case "MIRROR_BOX" :
								((MirrorBoxObject) gameScene.objects.get(
										gameScene.objects.size() - 1)).direction = ((MirrorBoxObject) this).direction;
								break;
						}
						break;
					default :
						break;
				}
				applet.keyPressEvent = false;
				focus = false;
			}

			if (focus && applet.mousePressed && applet.mouseButton == LEFT) {
				pos = new PVector(
						Util.roundToNearest(applet.getMouseCoordGame().x + editOffset.x, SideScroller.snapSize),
						Util.roundToNearest(applet.getMouseCoordGame().y + editOffset.y, SideScroller.snapSize));
			}
		}
	}

	public void focus() {
		editOffset = PVector.sub(pos, applet.getMouseCoordGame());
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
		return Util.hoverGame(pos.x, pos.y, width, height);
	}
}
