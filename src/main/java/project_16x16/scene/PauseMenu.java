/**
 *
 */
package project_16x16.scene;

import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.Utility;
import project_16x16.ui.Button;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;

/**
 * @author Quillbert182
 *
 */
public class PauseMenu extends PScene {

	public Button pressResume;
	public Button pressMenu; // Retruns to main menu
	public Button pressSettings; // TODO add settings menu

	private SideScroller game;
	private PImage backdrop;
	private final MenuNav nav;

	public PauseMenu(SideScroller sideScroller) {
		super(sideScroller);
		game = sideScroller;

		pressResume = new Button(sideScroller);
		pressSettings = new Button(sideScroller);
		pressMenu = new Button(sideScroller);

		pressResume.setText("Resume Game");
		pressResume.setPosition(applet.width / 2, applet.height / 2 - 90);
		pressResume.setTextSize(40);
		pressResume.setSize(360, 90);

		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width / 2, applet.height / 2 + 30);
		pressSettings.setTextSize(40);
		pressSettings.setSize(360, 90);

		pressMenu.setText("Main Menu");
		pressMenu.setPosition(applet.width / 2, applet.height / 2 + 150);
		pressMenu.setTextSize(40);
		pressMenu.setSize(360, 90);

		nav = new MenuNav(sideScroller);
		nav.add(pressResume, () -> game.returnScene());
		nav.add(pressSettings, () -> game.swapToScene(GameScenes.SETTINGS_MENU));
		nav.add(pressMenu, () -> game.swapToScene(GameScenes.MAIN_MENU));
	}

	/**
	 * Sets the game frame shown (blurred) behind the menu. Called by the game when
	 * it pauses.
	 */
	public void setBackdrop(PImage frame) {
		backdrop = Utility.blur(frame, 3, 2);
	}

	@Override
	public void drawUI() {
		if (backdrop != null) {
			applet.image(backdrop, applet.width / 2f, applet.height / 2f, applet.width, applet.height);
		} else {
			applet.background(0);
		}
		MenuStyle.dim(game, 110);
		MenuStyle.title(game, "PAUSED", game.height / 2f - 210);
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
		switch (e.getKeyCode()) {
			case PConstants.ESC: // Pause
				game.returnScene();
				break;
			default:
				break;
		}
	}
}
