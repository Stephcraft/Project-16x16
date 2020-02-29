package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import project_16x16.Main;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;

/**
 * 
 * @author micycle1
 *
 */
public final class AudioSettings extends PScene {

	private Main game;
	
	private Button quit;
	private Button apply;

	public AudioSettings(Main a) {
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
		// TODO
	}

	@Override
	public void drawUI() {
		game.background(255);
		apply.display();
		quit.display();
	}
	
	@Override
	void mouseReleased(processing.event.MouseEvent e) {
		apply.update();
		quit.update();
		
		if (quit.hover()) {
			game.returnScene();
			return;
		}
		if (apply.hover()) {
			// TODO apply settings
			Notifications.addNotification("Sound Settings Applied", "Your configuration has been successfully applied.");
		}
	}
	
	@Override
	void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == PConstants.ESC) { // Pause
			game.returnScene();
		}
	}

}
