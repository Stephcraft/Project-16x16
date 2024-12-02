package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Constants;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.factory.AudioFactory;
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
	private Slider volumeBGM;
	private Slider volumeSFX;

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

		volumeBGM = new Slider(game, 0.75f);
		volumeBGM.setText("BGM");
		volumeBGM.setPosition(a.width / 2, 300);

		volumeSFX = new Slider(game, 0.75f);
		volumeSFX.setText("SFX");
		volumeSFX.setPosition(a.width / 2, 350);
		
	}

	@Override
	public void switchTo() {
		originalVolumeBGM = Options.gainBGM;
		originalVolumeSFX = Options.gainSFX;
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
		volumeBGM.display();
		volumeSFX.display();
		
	}

	@Override
	void mouseDragged(MouseEvent e) {
		volumeBGM.update();
		volumeSFX.update();
		float volBGM = 20 * (float) Math.log(volumeBGM.getValue());
		float volSFX = 20 * (float) Math.log(volumeSFX.getValue());
		AudioFactory.getInstance().setGainBGM(volBGM);
		AudioFactory.getInstance().setGainSFX(volSFX);
	}

	@Override
	void mouseReleased(MouseEvent e) {
		apply.update();
		quit.update();

		if (quit.hover()) {
			// revert sound changes if menu is quit
			AudioFactory.getInstance().setGainBGM(originalVolumeBGM);
			AudioFactory.getInstance().setGainSFX(originalVolumeSFX);
			game.returnScene();
			return;
		}
		if (apply.hover()) {
			float volBGM = 20 * (float) Math.log(volumeBGM.getValue());
			float volSFX = 20 * (float) Math.log(volumeSFX.getValue());
			Options.save(Option.gainBGM, volBGM);
			Options.save(Option.gainSFX, volSFX);
			Options.gainBGM = volBGM;
			Options.gainSFX = volSFX;
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
