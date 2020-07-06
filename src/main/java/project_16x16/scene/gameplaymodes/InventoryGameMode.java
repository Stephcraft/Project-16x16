package project_16x16.scene.gameplaymodes;

import processing.event.MouseEvent;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;

public class InventoryGameMode extends GameplayMode {

	public InventoryGameMode(GameplayScene gameplayScene) {
		super(gameplayScene);
	}
	
	@Override
	public void enter() {
		scene.setZoomable(false);
	}
	
	@Override
	public GameModes getModeType() {
		return GameModes.INVENTORY;
	}
	
	@Override
	public void displayGUISlots() {}

	@Override
	protected boolean isNotInvalidGUIButtonMode() {
		return false;
	}
	
	@Override
	public void updateGUI() {
		scene.displayCreativeInventory();
	}
	
	@Override
	public void mouseWheelEvent(MouseEvent event) {
		scene.scrollInventoryBar(event);
	}
}
