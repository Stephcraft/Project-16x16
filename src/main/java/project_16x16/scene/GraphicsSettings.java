package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Dropdown;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;
import project_16x16.ui.Notifications;

public final class GraphicsSettings extends PScene {

	private static final int[] FPS_OPTIONS = { 30, 60, 90, 120, 144, 165, 240 };

	private final SideScroller game;
	private final MenuNav nav;
	private final Dropdown fps;

	public GraphicsSettings(SideScroller a) {
		super(a);
		game = a;

		final int cx = a.width / 2;
		String[] labels = new String[FPS_OPTIONS.length];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = FPS_OPTIONS[i] + " FPS";
		}
		fps = new Dropdown(a, "Frame Rate", labels);
		fps.setPosition(cx, 250);
		fps.setSize(520, 60);
		fps.setTextSize(30);

		nav = new MenuNav(a);
		nav.add(fps, fps::toggle);
		nav.button("Apply", cx, 470, 360, 70, 32, this::apply);
		nav.button("Back", cx, 560, 360, 70, 32, game::returnScene);
	}

	private void apply() {
		int value = FPS_OPTIONS[fps.getSelectedIndex()];
		Options.save(Option.TARGET_FPS, value);
		Options.targetFrameRate = value;
		SideScroller.targetFramerate = value;
		Notifications.addNotification("Graphics Settings Applied", "Your configuration has been successfully applied.");
		game.returnScene();
	}

	@Override
	public void switchTo() {
		// select the option closest to the current frame rate
		int best = 0;
		for (int i = 0; i < FPS_OPTIONS.length; i++) {
			if (Math.abs(FPS_OPTIONS[i] - SideScroller.targetFramerate) < Math.abs(FPS_OPTIONS[best] - SideScroller.targetFramerate)) {
				best = i;
			}
		}
		fps.setSelectedIndex(best);
		super.switchTo();
	}

	@Override
	public void drawUI() {
		MenuStyle.panel(game, "GRAPHICS");
		nav.display();
		fps.drawList(); // overlays the buttons below
	}

	@Override
	void mouseReleased(MouseEvent e) {
		if (fps.click()) {
			return;
		}
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (fps.key(e.getKeyCode())) {
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
