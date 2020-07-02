package project_16x16.scene.gameplaymodes;

import project_16x16.scene.GameplayScene;
import project_16x16.ui.Tab;
import project_16x16.windows.LoadLevelWindow;

public class LoadExampleGameMode extends GameplayMode {

	public LoadExampleGameMode(GameplayScene gameplayScene) {
		super(gameplayScene);
	}

	@Override
	public String getModeName() {
		return "LOADEXAMPLE";
	}

	@Override
	public void updateGUI() {
		Tab windowTabs = scene.getWindowTabs();
		LoadLevelWindow window_loadLevel = scene.getWindowLoadLevel();
		
		if (windowTabs.getActiveButton() != 0) {
			windowTabs.moveActive(0);
		}
		windowTabs.update();
		windowTabs.display();
		window_loadLevel.display();
		window_loadLevel.update();
		if (windowTabs.getButton(1).event()) {
			windowTabs.moveActive(1);
			scene.changeMode("SAVE");
		}
		if (windowTabs.getButton(2).event()) {
			windowTabs.moveActive(2);
			scene.changeMode("IMPORT");
		}
	}
}
