package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Audio.BGM;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.ui.Button;
import project_16x16.ui.MenuBackground;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;

/**
 *
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	public Button pressStart;
	public Button pressQuit;
	public Button pressSettings;
	public Button pressMultiplayer;

	private SideScroller game;
	private final MenuNav nav;

	public MainMenu(SideScroller a) {
		super(a);
		game = a;

		pressStart = new Button(a);
		pressMultiplayer = new Button(a);
		pressQuit = new Button(a);
		pressSettings = new Button(a);

		pressStart.setText("Start Game");
		pressStart.setPosition(applet.width / 2, applet.height / 2 - 150);
		pressStart.setSize(360, 90);
		pressStart.setTextSize(40);

		pressMultiplayer.setText("Multiplayer");
		pressMultiplayer.setPosition(applet.width / 2, applet.height / 2 - 30);
		pressMultiplayer.setSize(360, 90);
		pressMultiplayer.setTextSize(40);

		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width / 2, applet.height / 2 + 90);
		pressSettings.setSize(360, 90);
		pressSettings.setTextSize(40);

		pressQuit.setText("Quit Game");
		pressQuit.setPosition(applet.width / 2, applet.height / 2 + 210);
		pressQuit.setSize(360, 90);
		pressQuit.setTextSize(40);

		nav = new MenuNav(a);
		nav.add(pressStart, () -> {
			((GameplayScene) GameScenes.GAME.getScene()).setSingleplayer(true);
			game.swapToScene(GameScenes.GAME);
		});
		nav.add(pressMultiplayer, () -> game.swapToScene(GameScenes.MULTIPLAYER_MENU));
		nav.add(pressSettings, () -> game.swapToScene(GameScenes.SETTINGS_MENU));
		nav.add(pressQuit, () -> System.exit(0));
	}

	@Override
	public void switchTo() {
		super.switchTo();
		Audio.play(BGM.TEST4);
	}

	@Override
	public void drawUI() {
		MenuBackground.draw(true);

		MenuStyle.title(game, "PROJECT 16x16", game.height / 2f - 300);
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
			case 8 : // BACKSPACE
			case PConstants.ESC : // Pause
				game.returnScene();
				break;
			default :
				break;
		}
	}
}
