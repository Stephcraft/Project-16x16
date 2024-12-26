package project_16x16.scene;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Constants;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.Slider;

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
		// TODO properly align audio value and slider position
		//volumeBGM.setValue(PApplet.map(originalVolumeBGM, -60, 0, 0, 1));
		//volumeSFX.setValue(PApplet.map(originalVolumeSFX, -60, 0, 0, 1));
		super.switchTo();
	}

	@Override
	public void drawUI() {
		game.background(Constants.Colors.MENU_GREY);
		apply.display();
		quit.display();
		
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
			case PConstants.ESC : // Pause
				game.returnScene();
				break;
			default :
				break;
		}
	}

}
