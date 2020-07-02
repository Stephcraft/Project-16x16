package project_16x16.scene.gameplaymodes;

import project_16x16.scene.GameplayScene;

public class MoveGameMode extends GameplayMode {

	public MoveGameMode(GameplayScene gameplayScene) {
		super(gameplayScene);
	}

	@Override
	public String getModeName() {
		return "MOVE";
	}

}
