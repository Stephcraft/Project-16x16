package project_16x16.scene;

import java.util.regex.Pattern;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Constants;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.TextInputField;

public class MultiplayerHostMenu extends PScene {

	public TextInputField ipInput;
	public Button pressMenu;
	public Button pressHost;

	private SideScroller game;

	private static final Pattern pattern;

	static {
		pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)" + "{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");
	}

	public MultiplayerHostMenu(SideScroller sideScroller) {
		super(sideScroller);
		game = sideScroller;

		pressMenu = new Button(sideScroller);
		ipInput = new TextInputField(sideScroller);
		pressHost = new Button(sideScroller);

		ipInput.setPosition(applet.width / 2, applet.height / 2 - 240);
		ipInput.setWidth(300);

		pressHost.setText("Host");
		pressHost.setPosition(applet.width / 2, applet.height / 2 - 80);
		pressHost.setTextSize(40);
		pressHost.setSize(300, 100);

		pressMenu.setText("Back to menu");
		pressMenu.setPosition(applet.width / 2, applet.height / 2 + 240);
		pressMenu.setTextSize(40);
		pressMenu.setSize(300, 100);
	}

	@Override
	public void draw() {
	}

	@Override
	public void drawUI() {
		background(Constants.Colors.MENU_GREY);
		ipInput.update();
		ipInput.display();
		pressMenu.manDisplay();
		pressHost.manDisplay();
	}

	public void update() {
		pressMenu.update();
		if (pressMenu.hover()) {
			game.swapToScene(GameScenes.MAIN_MENU);
		}
		if (pressHost.hover()) {
			if (pattern.matcher(ipInput.getText()).matches()) {
				String ip = ipInput.getText().split(":")[0]; // TODO does host need to give IP?
				int port = Integer.valueOf(ipInput.getText().split(":")[1]);
				try {
					Multiplayer m = new Multiplayer(game, ip, port, true);
					((GameplayScene) GameScenes.GAME.getScene()).setupMultiplayer(m);
					game.swapToScene(GameScenes.GAME);
				}
				catch (Exception e) {
					Notifications.addNotification("ERROR", "todo"); // TODO
				}
			}
			else {
				Notifications.addNotification("Invalid IP", "Include IP and port, eg:\n127.0.0.1:8080");
			}
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		update();
	}

	@Override
	void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case PConstants.ESC: // Pause
				game.returnScene();
				break;
			default:
				break;
		}
	}
}
