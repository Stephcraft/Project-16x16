package settings;

import lombok.Getter;
import lombok.Setter;
import processing.core.PVector;
import sidescroller.SideScroller;

@Getter
public class GameSettings {
	// Game Rendering
	private final PVector windowSize = new PVector(1280, 720); // Game window size -- to be set via options
	private final PVector gameResolution = new PVector(1280, 720); // Game rendering resolution -- to be set

	@Setter
	private SideScroller applet;

	private static GameSettings gameSettings;

	public static GameSettings getInstance() {
		if (gameSettings == null) {
			gameSettings = new GameSettings();
		}

		return gameSettings;
	}
}
