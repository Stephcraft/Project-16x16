package project_16x16.scene.gameplaymodes;

import processing.event.KeyEvent;
import project_16x16.scene.GameplayScene;
import project_16x16.ui.Tab;
import project_16x16.windows.SaveLevelWindow;

public class SaveGameMode extends GameplayMode {

	public SaveGameMode(GameplayScene gameplayScene) {
		super(gameplayScene);
	}
	
	@Override
	public void enter() {
		scene.setZoomable(false);
	}
	
	@Override
	public String getModeName() {
		return "SAVE";
	}

	@Override
	protected boolean isNotInvalidGUIButtonMode() {
		return false;
	}
	
	@Override
	public void updateGUI() {
		// Save , Load
		// The if statement below should be used in each window that includes a tab.
		// switch the number to the id of the button it's checking for
		
		Tab windowTabs = scene.getWindowTabs();
		SaveLevelWindow window_saveLevel = scene.getWindowSaveLevel();
		
		if (windowTabs.getActiveButton() != 1) {
			windowTabs.moveActive(1);
		}
		window_saveLevel.privacyDisplay();
		windowTabs.update();
		windowTabs.display();
		window_saveLevel.update();
		window_saveLevel.display();
		// This is an example of how to switch windows when another tab button is
		// pressed.
		if (windowTabs.getButton(0).event()) {
			windowTabs.moveActive(0);
			scene.changeMode("LOADEXAMPLE");
		}
		if (windowTabs.getButton(2).event()) {
			windowTabs.moveActive(2);
			scene.changeMode("IMPORT");
		}
	}
	
	@Override
	public void keyReleasedEvent(KeyEvent event) {}
}
