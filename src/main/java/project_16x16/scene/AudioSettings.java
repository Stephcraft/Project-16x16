package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import project_16x16.Audio;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.Slider;

/**
 * 
 * @author micycle1
 *
 */
public final class AudioSettings extends PScene {

	private SideScroller game;
	
	private Button quit;
	private Button apply;
	private Slider masterVolume;

	public AudioSettings(SideScroller a) {
		super(a);
		
		game = a;

		apply = new Button(applet);
		apply.setText("Apply");
		apply.setPosition(a.width / 2, 500);

		quit = new Button(a);
		quit.setText("Quit");
		quit.setPosition(a.width / 2, 600);

		masterVolume = new Slider(game, 0.75f);
		masterVolume.setText("Volume");
		masterVolume.setPosition(a.width/2, 300);

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
		masterVolume.display();
	}

	@Override
	void mousePressed(processing.event.MouseEvent e) {
		masterVolume.update();
		// logarithmic volume control
		float volume = 20 * (float) Math.log(masterVolume.getValue());
		Audio.setGainBGM(volume);
		Audio.setGainSFX(volume);
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
			return;
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
