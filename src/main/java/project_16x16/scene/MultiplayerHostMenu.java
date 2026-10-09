package project_16x16.scene;

import java.util.regex.Pattern;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;
import project_16x16.ui.Notifications;
import project_16x16.ui.TextInputField;

public class MultiplayerHostMenu extends PScene {

	private final TextInputField ipInput;
	private final MenuNav nav;
	private final SideScroller game;

	private static final Pattern pattern;

	static {
		pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)" + "{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");
	}

	public MultiplayerHostMenu(SideScroller sideScroller) {
		super(sideScroller);
		game = sideScroller;

		final int cx = game.width / 2;
		ipInput = new TextInputField(sideScroller);
		ipInput.set(cx, game.height / 2 - 40, 440, 44);
		ipInput.setPlaceholder("127.0.0.1:8080");

		nav = new MenuNav(sideScroller).setLetterKeys(false); // letters are typed into the text field
		nav.button("Host", cx, game.height / 2 + 50, 440, 80, 36, this::submit);
		nav.button("Back", cx, game.height / 2 + 150, 440, 80, 36, () -> game.swapToScene(GameScenes.MAIN_MENU));
	}

	private void submit() {
		if (pattern.matcher(ipInput.getText()).matches()) {
			String ip = ipInput.getText().split(":")[0]; // TODO does host need to give IP?
			int port = Integer.valueOf(ipInput.getText().split(":")[1]);
			try {
				Multiplayer m = new Multiplayer(game, ip, port, true);
				((GameplayScene) GameScenes.GAME.getScene()).setupMultiplayer(m);
				game.swapToScene(GameScenes.GAME);
			} catch (Exception e) {
				Notifications.addNotification("ERROR", "todo"); // TODO
			}
		} else {
			Notifications.addNotification("Invalid IP", "Include IP and port, eg:\n127.0.0.1:8080");
		}
	}

	@Override
	public void drawUI() {
		MenuStyle.panel(game, "HOST GAME");
		MenuStyle.caption(game, "Address to host on (ip:port)", game.width / 2f, game.height / 2f - 90);
		ipInput.update();
		ipInput.display();
		nav.display();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == PConstants.ENTER && nav.selectedButton() == null) {
			submit();
			return;
		}
		if (nav.keyReleased(e)) {
			return;
		}
		if (e.getKeyCode() == PConstants.ESC) {
			game.returnScene();
		}
	}
}
