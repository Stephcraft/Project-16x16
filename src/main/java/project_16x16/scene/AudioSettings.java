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

	private float originalVolumeBGM;
	private float originalVolumeSFX;

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
		masterVolume.setPosition(a.width / 2, 300);

	}

	@Override
	public void switchTo() {
		originalVolumeBGM = Options.gainBGM;
		originalVolumeSFX = Options.gainSFX;
		// TODO properly align audio value and slider position
		masterVolume.setValue(PApplet.map(originalVolumeBGM, -60, 0, 0, 1));
		super.switchTo();
	}

	@Override
	public void drawUI() {
		game.background(Constants.Colors.MENU_GREY);
		apply.display();
		quit.display();
		masterVolume.display();
	}

	@Override
	void mouseDragged(MouseEvent e) {
		masterVolume.update();
		float volume = 20 * (float) Math.log(masterVolume.getValue());
		Audio.setGainBGM(volume);
		Audio.setGainSFX(volume);
	}

	@Override
	void mouseReleased(MouseEvent e) {
		apply.update();
		quit.update();

		if (quit.hover()) {
			// revert sound changes if menu is quit
			Audio.setGainBGM(originalVolumeBGM);
			Audio.setGainSFX(originalVolumeSFX);
			game.returnScene();
			return;
		}
		if (apply.hover()) {
			float volume = 20 * (float) Math.log(masterVolume.getValue());
			Options.save(Option.gainBGM, volume);
			Options.save(Option.gainSFX, volume);
			Options.gainBGM = volume;
			Options.gainSFX = volume;
			Notifications.addNotification("Sound Settings Applied", "Your configuration has been successfully applied.");
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
