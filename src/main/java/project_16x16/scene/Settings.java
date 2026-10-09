package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;

/**
 * The settings hub, linking to each settings category.
 *
 * @author micycle1
 *
 */
public class Settings extends PScene {

	private final SideScroller game;
	private final MenuNav nav;

	public Settings(SideScroller sideScroller) {
		super(sideScroller);
		game = sideScroller;

		final int cx = game.width / 2;
		final int top = 230;
		final int step = 100;
		nav = new MenuNav(game);
		nav.button("Graphics", cx, top, 440, 80, 36, () -> game.swapToScene(GameScenes.GRAPHICS_SETTINGS));
		nav.button("Audio", cx, top + step, 440, 80, 36, () -> game.swapToScene(GameScenes.AUDIO_SETTINGS));
		nav.button("Controls", cx, top + 2 * step, 440, 80, 36, () -> game.swapToScene(GameScenes.CONTROLS_SETTINGS));
		nav.button("Back", cx, top + 3 * step + 20, 440, 80, 36, game::returnScene);
	}

	@Override
	public void drawUI() {
		MenuStyle.panel(game, "SETTINGS");
		nav.display();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (nav.keyReleased(e)) {
			return;
		}
		if (e.getKeyCode() == PConstants.ESC) {
			game.returnScene();
		}
	}
}
