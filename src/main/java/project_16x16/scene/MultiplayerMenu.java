package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Constants;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.ui.Button;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;

public class MultiplayerMenu extends PScene {

	public Button pressHost;
	public Button pressClient;
	public Button pressMenu;

	private SideScroller game;
	private final MenuNav nav;

	public MultiplayerMenu(SideScroller sideScroller) {
		super(sideScroller);
		game = sideScroller;

		pressHost = new Button(sideScroller);
		pressClient = new Button(sideScroller);
		pressMenu = new Button(sideScroller);

		pressHost.setText("Host a game");
		pressHost.setPosition(applet.width / 2, applet.height / 2 - 90);
		pressHost.setTextSize(40);
		pressHost.setSize(440, 90);

		pressClient.setText("Connect to a game");
		pressClient.setPosition(applet.width / 2, applet.height / 2 + 30);
		pressClient.setTextSize(40);
		pressClient.setSize(440, 90);

		pressMenu.setText("Back to menu");
		pressMenu.setPosition(applet.width / 2, applet.height / 2 + 150);
		pressMenu.setTextSize(40);
		pressMenu.setSize(440, 90);

		nav = new MenuNav(sideScroller);
		nav.add(pressHost, () -> game.swapToScene(GameScenes.HOST_MENU));
		nav.add(pressClient, () -> game.swapToScene(GameScenes.CLIENT_MENU));
		nav.add(pressMenu, () -> game.swapToScene(GameScenes.MAIN_MENU));
	}

	@Override
	public void drawUI() {
		background(Constants.Colors.MENU_GREY);
		MenuStyle.title(game, "MULTIPLAYER", game.height / 2f - 210);
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
