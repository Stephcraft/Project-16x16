package project_16x16.scene;

import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.ui.Button;

/**
 * 
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	public Button pressStart;
	public Button pressQuit;
	public Button pressSettings;

	private SideScroller game;

	public MainMenu(SideScroller a) {
		super(a);
		game = a;

		pressStart = new Button(a);
		pressQuit = new Button(a);
		pressSettings = new Button(a);

		pressStart.setText("Start Game");
		pressStart.setPosition(applet.width / 2, applet.height / 2 - 150);
		pressStart.setSize(300, 100);
		pressStart.setTextSize(40);

		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width / 2, applet.height / 2);
		pressSettings.setSize(300, 100);
		pressSettings.setTextSize(40);

		pressQuit.setText("Quit Game");
		pressQuit.setPosition(applet.width / 2, applet.height / 2 + 150);
		pressQuit.setSize(300, 100);
		pressQuit.setTextSize(40);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawUI() {
		background(29, 33, 45);
		pressStart.manDisplay();
		pressSettings.manDisplay();
		pressQuit.manDisplay();
	}

	private void update() {
		pressStart.update();
		if (pressStart.hover()) {
			game.swapToScene(game.game);
		}

		pressSettings.update();
		if (pressSettings.hover()) {
			game.swapToScene(game.settings);
		}

		pressQuit.update();
		if (pressQuit.hover()) {
			System.exit(0);
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		update();
	}
}
