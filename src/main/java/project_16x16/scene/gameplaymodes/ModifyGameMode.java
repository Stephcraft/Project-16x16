package project_16x16.scene.gameplaymodes;

import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;
import project_16x16.entities.Player;
import project_16x16.objects.EditableObject;
import project_16x16.objects.EditorItem;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;

public class ModifyGameMode extends GameplayMode {

	private EditorItem editorItem;

	public ModifyGameMode(GameplayScene gameplayScene, EditorItem editorItem) {
		super(gameplayScene);
		this.editorItem = editorItem;
	}

	@Override
	public void enter() {
		scene.setZoomable(true);
	}

	@Override
	public GameModes getModeType() {
		return GameModes.MODIFY;
	}

	@Override
	public void displayWorldEdit() {
		scene.displayWorldEdit();
	}

	@Override
	public void updateEditableObject(EditableObject object) {
		object.updateEdit();
		object.displayEdit();
	}

	@Override
	public void displayDestination() {
		editorItem.displayDestination();
	}

	@Override
	public void updateLocalPlayer(Player localPlayer) {
		localPlayer.updateEdit();
		localPlayer.displayEdit();
	}

	@Override
	public void updateGUI() {
		editorItem.update();
		editorItem.display();
	}

	@Override
	public void mouseDraggedEvent(MouseEvent event, PVector origPos, PVector mouseDown) {
		if (event.getButton() == PConstants.CENTER) { // pan on MMB; TODO fix when zoom != 1.00
			scene.applet.camera.setCameraPositionNoLerp(PVector.add(origPos, PVector.sub(mouseDown, scene.applet.getMouseCoordScreen())));
		}
	}
}
