package project_16x16.scene.gameplaymodes;

import project_16x16.scene.GameplayScene;
import project_16x16.ui.Tab;
import project_16x16.windows.ImportLevelWindow;

public class ImportGameMode extends GameplayMode {

	public ImportGameMode(GameplayScene gameplayScene) {
		super(gameplayScene);
	}

	@Override
	public String getModeName() {
		return "IMPORT";
	}
	
	@Override
	public void updateGUI() {
		Tab windowTabs = scene.getWindowTabs();
		ImportLevelWindow window_importlevel = scene.getWindowImportLevel();

		// Import Level
		if (windowTabs.getActiveButton() != 2) {
			windowTabs.moveActive(2);
		}
		window_importlevel.privacyDisplay();
		windowTabs.update();
		windowTabs.display();
		window_importlevel.update();
		window_importlevel.display();
		
		if (windowTabs.getButton(0).event()) {
			windowTabs.moveActive(0);
			scene.changeMode("LOADEXAMPLE");
		}
		if (windowTabs.getButton(1).event()) {
			windowTabs.moveActive(1);
			scene.changeMode("SAVE");
		}
	}

}
