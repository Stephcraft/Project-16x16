package project_16x16.scene.gameplaymodes;

import project_16x16.scene.GameplayScene;

public class TestGameMode extends GameplayMode {

	public TestGameMode(GameplayScene gameplayScene) {
		super(gameplayScene);
	}

	@Override
	public String getModeName() {
		return "TEST";
	}

}
