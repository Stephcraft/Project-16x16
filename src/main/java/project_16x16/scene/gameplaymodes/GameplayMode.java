package project_16x16.scene.gameplaymodes;

import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.entities.Player;
import project_16x16.objects.EditableObject;
import project_16x16.scene.GameplayScene;

public abstract class GameplayMode {

	protected GameplayScene scene;
	
	public GameplayMode(GameplayScene gameplayScene) {
		this.scene = gameplayScene;
	}
	
	public void enter() {}

	public void displayWorldEdit() {}

	public void updateEditableObject(EditableObject object) {}

	public void displayDestination() {}

	public void updateLocalPlayer(Player localPlayer) {}

	public void displayGUISlots() {
		scene.displayGUISlots();
	}

	public void updateGUIButton(int xAnchor, PImage activeIcon, PImage inactiveIcon, String mode, boolean isHighlighted) {
		if (getModeName().equals(mode)) {
			drawGUIButton(activeIcon, xAnchor, 120);
		} else if (isHighlighted){
			if (scene.applet.mousePressEvent) {
				scene.changeMode(mode);
			}
			drawGUIButton(activeIcon, xAnchor, 120);
		} else {
			drawGUIButton(inactiveIcon, xAnchor, 120);
		}
	}
	
	protected boolean isNotInvalidGUIButtonMode() {
		return true;
	}

	protected void drawGUIButton(PImage icon, int x, int y) {
		scene.image(icon, x, y);
	}
	
	public abstract String getModeName();

	public void updateGUI() {}

	public void mouseDraggedEvent(MouseEvent event, PVector origPos, PVector mouseDown) {}

	public void mouseWheelEvent(MouseEvent event) {}

	public void keyReleasedEvent(KeyEvent event) {
		scene.switchModeOnKeyEvent(event);
	}

}
