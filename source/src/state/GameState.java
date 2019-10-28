package state;

import lombok.Getter;
import lombok.Setter;
import processing.core.PVector;
import sidescroller.SideScroller;

@Getter
public class GameState {
	// Game Rendering
	private final PVector windowSize = new PVector(1280, 720); // Game window size -- to be set via options
	private final PVector gameResolution = new PVector(1280, 720); // Game rendering resolution -- to be set

	@Setter
	private SideScroller applet;

	private static GameState gameState;

	public static GameState getInstance() {
		if (gameState == null) {
			gameState = new GameState();
		}

		return gameState;
	}
}
