package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;

public final class GraphicsSettings extends PScene {

	private SideScroller game;

	private Button quit;
	private Button apply;

	public GraphicsSettings(SideScroller a) {
		super(a);

		game = a;

		apply = new Button(applet);
		apply.setText("Apply");
		apply.setPosition(a.width / 2, 500);

		quit = new Button(a);
		quit.setText("Quit");
		quit.setPosition(a.width / 2, 600);

	}

	@Override
	public void switchTo() {
		super.switchTo();
	}

	@Override
	public void drawUI() {
		displayWindow();
		apply.display();
		quit.display();
	}

	private void displayWindow() {
		background(19, 23, 35);
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.gameResolution.x / 2, applet.gameResolution.y / 2, applet.gameResolution.x * 0.66f - 8, applet.gameResolution.y - 8);
	}

	@Override
	void mouseDragged(MouseEvent e) {

	}

	@Override
	void mouseReleased(MouseEvent e) {
		apply.update();
		quit.update();

		if (quit.hover()) {
			// revert sound changes if menu is quit
			game.returnScene();
			return;
		}
		if (apply.hover()) {
			Notifications.addNotification("Graphics Settings Applied", "Your configuration has been successfully applied.");
			game.returnScene();
		}

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
