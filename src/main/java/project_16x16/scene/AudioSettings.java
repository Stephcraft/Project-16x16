package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;
import project_16x16.ui.Notifications;
import project_16x16.ui.Slider;

/**
 *
 * @author micycle1
 *
 */

public final class AudioSettings extends PScene {

	/** Gain (dB) used for a slider value of zero (effectively silent). */
	private static final float MIN_GAIN = -80;

	private final SideScroller game;
	private final MenuNav nav;
	private final Slider volumeMenuBGM;
	private final Slider volumeGameBGM;
	private final Slider volumeSFX;

	private float originalVolumeMenuBGM;
	private float originalVolumeGameBGM;
	private float originalVolumeSFX;

	public AudioSettings(SideScroller a) {
		super(a);
		game = a;

		final int cx = a.width / 2;
		nav = new MenuNav(a);
		volumeMenuBGM = slider("Menu Music", cx, 230);
		volumeGameBGM = slider("Game Music", cx, 305);
		volumeSFX = slider("Effects", cx, 380);

		nav.button("Apply", cx, 500, 360, 70, 32, this::apply);
		nav.button("Back", cx, 590, 360, 70, 32, this::cancel);
	}

	private Slider slider(String text, int x, int y) {
		Slider s = new Slider(game, 1);
		s.setText(text);
		s.setPosition(x, y);
		nav.add(s, () -> {
		});
		return s;
	}

	private static float sliderToGain(float v) {
		return v <= 0.001f ? MIN_GAIN : Math.max(MIN_GAIN, 20 * (float) Math.log10(v));
	}

	private static float gainToSlider(float gain) {
		return gain <= MIN_GAIN ? 0 : (float) Math.pow(10, gain / 20);
	}

	private void previewGain() {
		Audio.setGainMenuBGM(sliderToGain(volumeMenuBGM.getValue()));
		Audio.setGainGameBGM(sliderToGain(volumeGameBGM.getValue()));
		Audio.setGainSFX(sliderToGain(volumeSFX.getValue()));
	}

	private void apply() {
		float volMenuBGM = sliderToGain(volumeMenuBGM.getValue());
		float volGameBGM = sliderToGain(volumeGameBGM.getValue());
		float volSFX = sliderToGain(volumeSFX.getValue());
		Options.save(Option.GAIN_MENU_BGM, volMenuBGM);
		Options.save(Option.GAIN_GAME_BGM, volGameBGM);
		Options.save(Option.GAIN_SFX, volSFX);
		Options.gainMenuBGM = volMenuBGM;
		Options.gainGameBGM = volGameBGM;
		Options.gainSFX = volSFX;
		Notifications.addNotification("Sound Settings Applied", "Your configuration has been successfully applied.");
		game.returnScene();
	}

	/** Reverts any previewed volume changes and leaves. */
	private void cancel() {
		Audio.setGainMenuBGM(originalVolumeMenuBGM);
		Audio.setGainGameBGM(originalVolumeGameBGM);
		Audio.setGainSFX(originalVolumeSFX);
		game.returnScene();
	}

	@Override
	public void switchTo() {
		originalVolumeMenuBGM = Options.gainMenuBGM;
		originalVolumeGameBGM = Options.gainGameBGM;
		originalVolumeSFX = Options.gainSFX;
		volumeMenuBGM.setValue(gainToSlider(originalVolumeMenuBGM));
		volumeGameBGM.setValue(gainToSlider(originalVolumeGameBGM));
		volumeSFX.setValue(gainToSlider(originalVolumeSFX));
		super.switchTo();
	}

	@Override
	public void drawUI() {
		MenuStyle.panel(game, "AUDIO");
		nav.display();
	}

	@Override
	void mousePressed(MouseEvent e) {
		volumeMenuBGM.press();
		volumeGameBGM.press();
		volumeSFX.press();
		previewGain();
	}

	@Override
	void mouseDragged(MouseEvent e) {
		volumeMenuBGM.drag();
		volumeGameBGM.drag();
		volumeSFX.drag();
		previewGain();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		volumeMenuBGM.release();
		volumeGameBGM.release();
		volumeSFX.release();
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == PConstants.LEFT || e.getKeyCode() == PConstants.RIGHT) {
			if (nav.selectedButton() instanceof Slider) {
				((Slider) nav.selectedButton()).nudge(e.getKeyCode() == PConstants.LEFT ? -1 : 1);
				previewGain();
			}
			return;
		}
		if (nav.keyReleased(e)) {
			return;
		}
		if (e.getKeyCode() == PConstants.ESC) {
			cancel();
		}
	}

}
