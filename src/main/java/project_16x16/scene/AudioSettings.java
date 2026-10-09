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
	private final Slider volumeBGM;
	private final Slider volumeSFX;

	private float originalVolumeBGM;
	private float originalVolumeSFX;

	public AudioSettings(SideScroller a) {
		super(a);
		game = a;

		final int cx = a.width / 2;
		nav = new MenuNav(a);
		volumeBGM = new Slider(a, 1);
		volumeBGM.setText("Music");
		volumeBGM.setPosition(cx, 270);
		nav.add(volumeBGM, () -> {
		});

		volumeSFX = new Slider(a, 1);
		volumeSFX.setText("Effects");
		volumeSFX.setPosition(cx, 350);
		nav.add(volumeSFX, () -> {
		});

		nav.button("Apply", cx, 470, 360, 70, 32, this::apply);
		nav.button("Back", cx, 560, 360, 70, 32, this::cancel);
	}

	private static float sliderToGain(float v) {
		return v <= 0.001f ? MIN_GAIN : Math.max(MIN_GAIN, 20 * (float) Math.log10(v));
	}

	private static float gainToSlider(float gain) {
		return gain <= MIN_GAIN ? 0 : (float) Math.pow(10, gain / 20);
	}

	private void previewGain() {
		Audio.setGainBGM(sliderToGain(volumeBGM.getValue()));
		Audio.setGainSFX(sliderToGain(volumeSFX.getValue()));
	}

	private void apply() {
		float volBGM = sliderToGain(volumeBGM.getValue());
		float volSFX = sliderToGain(volumeSFX.getValue());
		Options.save(Option.GAIN_BGM, volBGM);
		Options.save(Option.GAIN_SFX, volSFX);
		Options.gainBGM = volBGM;
		Options.gainSFX = volSFX;
		Notifications.addNotification("Sound Settings Applied", "Your configuration has been successfully applied.");
		game.returnScene();
	}

	/** Reverts any previewed volume changes and leaves. */
	private void cancel() {
		Audio.setGainBGM(originalVolumeBGM);
		Audio.setGainSFX(originalVolumeSFX);
		game.returnScene();
	}

	@Override
	public void switchTo() {
		originalVolumeBGM = Options.gainBGM;
		originalVolumeSFX = Options.gainSFX;
		volumeBGM.setValue(gainToSlider(originalVolumeBGM));
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
		volumeBGM.press();
		volumeSFX.press();
		previewGain();
	}

	@Override
	void mouseDragged(MouseEvent e) {
		volumeBGM.drag();
		volumeSFX.drag();
		previewGain();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		volumeBGM.release();
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
