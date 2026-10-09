package project_16x16.scene;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Dropdown;
import project_16x16.ui.MenuBackground;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;
import project_16x16.ui.Notifications;

public final class GraphicsSettings extends PScene {

	private static final int[] FPS_OPTIONS = { 30, 60, 90, 120, 144, 165, 240 };
	/** Windowed-mode sizes offered (16:9, like the game), if they fit the screen. */
	private static final int[][] WINDOW_SIZES = { { 960, 540 }, { 1280, 720 }, { 1600, 900 }, { 1920, 1080 }, { 2560, 1440 }, { 3200, 1800 },
			{ 3840, 2160 } };

	private final SideScroller game;
	private final MenuNav nav;
	private final Dropdown displayMode;
	private final Dropdown windowSize;
	private final Dropdown fps;
	private final Dropdown particles;
	private final Dropdown[] dropdowns;
	/** The window sizes offered by {@link #windowSize}. */
	private final List<int[]> windowSizes = new ArrayList<>();

	public GraphicsSettings(SideScroller a) {
		super(a);
		game = a;

		Rectangle2D screen = Screen.getPrimary().getBounds();
		for (int[] size : WINDOW_SIZES) {
			if (windowSizes.isEmpty() || (size[0] <= screen.getWidth() && size[1] <= screen.getHeight())) {
				windowSizes.add(size);
			}
		}
		String[] sizeLabels = new String[windowSizes.size()];
		for (int i = 0; i < sizeLabels.length; i++) {
			sizeLabels[i] = windowSizes.get(i)[0] + " x " + windowSizes.get(i)[1];
		}
		String[] fpsLabels = new String[FPS_OPTIONS.length];
		for (int i = 0; i < fpsLabels.length; i++) {
			fpsLabels[i] = FPS_OPTIONS[i] + " FPS";
		}

		final int cx = a.width / 2;
		nav = new MenuNav(a);
		displayMode = dropdown("Display", new String[] { "Windowed", "Fullscreen" }, cx, 195);
		windowSize = dropdown("Window Size", sizeLabels, cx, 270);
		fps = dropdown("Frame Rate", fpsLabels, cx, 345);
		particles = dropdown("Menu Particles", MenuBackground.DENSITY_NAMES, cx, 420);
		dropdowns = new Dropdown[] { displayMode, windowSize, fps, particles };

		nav.button("Apply", cx, 525, 360, 66, 32, this::apply);
		nav.button("Back", cx, 610, 360, 66, 32, game::returnScene);
	}

	private Dropdown dropdown(String label, String[] options, int x, int y) {
		Dropdown d = new Dropdown(game, label, options);
		d.setPosition(x, y);
		d.setSize(600, 56);
		d.setTextSize(28);
		nav.add(d, d::toggle);
		return d;
	}

	private void apply() {
		int value = FPS_OPTIONS[fps.getSelectedIndex()];
		Options.save(Option.TARGET_FPS, value);
		Options.targetFrameRate = value;
		SideScroller.targetFramerate = value;

		int[] size = windowSizes.get(windowSize.getSelectedIndex());
		Options.windowWidth = size[0];
		Options.windowHeight = size[1];
		Options.save(Option.WINDOW_WIDTH, size[0]);
		Options.save(Option.WINDOW_HEIGHT, size[1]);
		if (size[0] != game.getWindowWidth() || size[1] != game.getWindowHeight()) {
			game.setWindowSize(size[0], size[1]); // if fullscreen, applies on returning to windowed mode
		}

		Options.fullscreen = displayMode.getSelectedIndex() == 1;
		Options.save(Option.FULLSCREEN, Options.fullscreen);
		game.setFullscreen(Options.fullscreen);

		Options.menuParticles = particles.getSelectedIndex();
		Options.save(Option.MENU_PARTICLES, Options.menuParticles);
		MenuBackground.setDensity(Options.menuParticles);

		Notifications.addNotification("Graphics Settings Applied", "Your configuration has been successfully applied.");
		game.returnScene();
	}

	@Override
	public void switchTo() {
		displayMode.setSelectedIndex(game.isFullscreen() ? 1 : 0);

		// select the options closest to the current window size and frame rate
		int best = 0;
		for (int i = 0; i < windowSizes.size(); i++) {
			if (Math.abs(windowSizes.get(i)[0] - game.getWindowWidth()) < Math.abs(windowSizes.get(best)[0] - game.getWindowWidth())) {
				best = i;
			}
		}
		windowSize.setSelectedIndex(best);

		best = 0;
		for (int i = 0; i < FPS_OPTIONS.length; i++) {
			if (Math.abs(FPS_OPTIONS[i] - SideScroller.targetFramerate) < Math.abs(FPS_OPTIONS[best] - SideScroller.targetFramerate)) {
				best = i;
			}
		}
		fps.setSelectedIndex(best);

		particles.setSelectedIndex(Options.menuParticles);
		super.switchTo();
	}

	/** @return the dropdown whose list is open, or null */
	private Dropdown openDropdown() {
		for (Dropdown d : dropdowns) {
			if (d.isOpen()) {
				return d;
			}
		}
		return null;
	}

	@Override
	public void drawUI() {
		MenuStyle.panel(game, "GRAPHICS");
		nav.display();
		Dropdown open = openDropdown();
		if (open != null) {
			open.drawList(); // overlays the buttons below
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		Dropdown open = openDropdown();
		if (open != null) {
			open.click();
			return;
		}
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		Dropdown open = openDropdown();
		if (open != null) {
			open.key(e.getKeyCode());
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
