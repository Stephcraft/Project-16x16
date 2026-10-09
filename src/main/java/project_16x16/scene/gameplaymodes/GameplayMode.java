package project_16x16.scene.gameplaymodes;

import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.entities.Player;
import project_16x16.objects.EditableObject;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;

public abstract class GameplayMode {

	protected GameplayScene scene;

	public GameplayMode(GameplayScene gameplayScene) {
		this.scene = gameplayScene;
	}

	public void enter() {
	}

	public void displayWorldEdit() {
	}

	public void updateEditableObject(EditableObject object) {
	}

	public void displayDestination() {
	}

	public void updateLocalPlayer(Player localPlayer) {
	}

	public void updateGUIButton(int x, int y, PImage activeIcon, PImage inactiveIcon, GameModes mode, boolean isHighlighted) {
		if (getModeType().equals(mode)) {
			drawGUIButton(activeIcon, x, y);
		} else if (isNotInvalidGUIButtonMode() && isHighlighted) {
			if (scene.applet.mousePressEvent) {
				scene.changeMode(mode);
			}
			drawGUIButton(activeIcon, x, y);
		} else {
			drawGUIButton(inactiveIcon, x, y);
		}
	}

	protected boolean isNotInvalidGUIButtonMode() {
		return true;
	}

	protected void drawGUIButton(PImage icon, int x, int y) {
		scene.image(icon, x, y);
	}

	public abstract GameModes getModeType();

	/**
	 * @return whether level objects can be selected and moved in this mode
	 */
	public boolean allowsWorldEditing() {
		return false;
	}

	/**
	 * @return whether the screen position is over UI belonging to this mode
	 */
	public boolean isOverUI(PVector screen) {
		return false;
	}

	/**
	 * Offers a tile dragged from the editor to this mode's UI.
	 *
	 * @return whether the UI took the tile (so it shouldn't be placed in the
	 *         level)
	 */
	public boolean dropTile(String tileName, PVector screen) {
		return false;
	}

	public void updateGUI() {
	}

	public void mouseDraggedEvent(MouseEvent event, PVector origPos, PVector mouseDown) {
	}

	public void mouseWheelEvent(MouseEvent event) {
	}

	public void keyReleasedEvent(KeyEvent event) {
		scene.switchModeOnKeyEvent(event);
	}

}
