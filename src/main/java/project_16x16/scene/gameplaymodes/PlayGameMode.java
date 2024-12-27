package project_16x16.scene.gameplaymodes;

import project_16x16.entities.Player;
import project_16x16.objects.EditableObject;
import project_16x16.objects.GameObject;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;

public class PlayGameMode extends GameplayMode {

	private Player localPlayer;

	public PlayGameMode(GameplayScene gameplayScene, Player localPlayer) {
		super(gameplayScene);
		this.localPlayer = localPlayer;
	}

	@Override
	public void enter() {
		scene.setZoomable(true);
	}

	@Override
	public GameModes getModeType() {
		return GameModes.PLAY;
	}

	@Override
	public void updateEditableObject(EditableObject object) {
		if (object instanceof GameObject) {
			((GameObject) object).update();
		}
	}

	@Override
	public void updateLocalPlayer(Player localPlayer) {
		localPlayer.update();
	}

	@Override
	public void updateGUI() {
		localPlayer.displayLife();
	}
}
