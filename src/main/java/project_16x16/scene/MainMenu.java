package project_16x16.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jafama.DoubleWrapper;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Audio.BGM;
import project_16x16.Constants;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.Utility;
import project_16x16.ui.Button;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;

import static net.jafama.FastMath.*;

/**
 *
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	public Button pressStart;
	public Button pressQuit;
	public Button pressSettings;
	public Button pressMultiplayer;

	private SideScroller game;
	private final MenuNav nav;

	private PGraphics background;

	public MainMenu(SideScroller a) {
		super(a);
		game = a;

		background = game.createGraphics((int) game.gameResolution.x, (int) game.gameResolution.x);
		background.noSmooth();
		Particles.assignApplet(a);
		Particles.populate(1250);

		pressStart = new Button(a);
		pressMultiplayer = new Button(a);
		pressQuit = new Button(a);
		pressSettings = new Button(a);

		pressStart.setText("Start Game");
		pressStart.setPosition(applet.width / 2, applet.height / 2 - 150);
		pressStart.setSize(360, 90);
		pressStart.setTextSize(40);

		pressMultiplayer.setText("Multiplayer");
		pressMultiplayer.setPosition(applet.width / 2, applet.height / 2 - 30);
		pressMultiplayer.setSize(360, 90);
		pressMultiplayer.setTextSize(40);

		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width / 2, applet.height / 2 + 90);
		pressSettings.setSize(360, 90);
		pressSettings.setTextSize(40);

		pressQuit.setText("Quit Game");
		pressQuit.setPosition(applet.width / 2, applet.height / 2 + 210);
		pressQuit.setSize(360, 90);
		pressQuit.setTextSize(40);

		nav = new MenuNav(a);
		nav.add(pressStart, () -> {
			((GameplayScene) GameScenes.GAME.getScene()).setSingleplayer(true);
			game.swapToScene(GameScenes.GAME);
		});
		nav.add(pressMultiplayer, () -> game.swapToScene(GameScenes.MULTIPLAYER_MENU));
		nav.add(pressSettings, () -> game.swapToScene(GameScenes.SETTINGS_MENU));
		nav.add(pressQuit, () -> System.exit(0));
	}

	@Override
	public void switchTo() {
		super.switchTo();
		Audio.play(BGM.TEST4);
	}

	@Override
	public void drawUI() {
		game.fill(Constants.Colors.MENU_GREY, 40);
		game.noStroke();
		game.rectMode(CORNER);
		game.rect(0, 0, game.gameResolution.x, game.gameResolution.y);
		game.rectMode(CENTER);
		Particles.run();

		MenuStyle.title(game, "PROJECT 16x16", game.height / 2f - 300);
		nav.display();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (nav.keyReleased(e)) {
			return;
		}
		switch (e.getKeyCode()) {
			case 8: // BACKSPACE
			case PConstants.ESC: // Pause
				game.returnScene();
				break;
			default:
				break;
		}
	}

	/**
	 * Adapted from https://www.openprocessing.org/sketch/762850
	 *
	 * @author micycle1
	 *
	 */
	private static class Particles {

		private static SideScroller game;

		private static List<Particle> particles;

		private static int function = 0;
		private static int centerX, centerY;
		private static int scaleX, scaleY;

		// reused between calls to avoid allocation (single render thread)
		private static final double[] SLOPES = new double[2];
		private static final DoubleWrapper COS_RESULT = new DoubleWrapper();

		private static long timeAccumulator = 0;
		private static final int TRANSITION_INTERVAL = 5000; // 5000 milliseconds = 5 seconds

		static void assignApplet(SideScroller s) {
			particles = new ArrayList<>();
			game = s;
			centerX = (int) (game.gameResolution.x / 2);
			centerY = (int) (game.gameResolution.y / 2);
			scaleX = (int) (game.gameResolution.x / 20);
			scaleY = (int) (game.gameResolution.y / 20 * (game.gameResolution.x / game.gameResolution.y));
		}

		static void populate(int n) {
			for (int i = 0; i < n; i++) {
				float x = getXPos(game.random(0, game.gameResolution.x));
				float y = getYPos(game.random(0, game.gameResolution.y));
				int color = Utility.colorToRGB((int) game.random(0, 50), (int) game.random(150, 255), (int) game.random(150, 255));
				Particle p = new Particle(x, y, (int) game.random(2, 8), color);
				particles.add(p);
			}
		}

		static void run() {
			if (timeAccumulator >= TRANSITION_INTERVAL) {
				function++;
				function %= 12; // cycle movement function
				timeAccumulator %= TRANSITION_INTERVAL;
			}

			int repopulate = 0;
			for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext();) {
				Particle p = iterator.next();
				p.update(3 / SideScroller.targetFramerate);
				float x = getXPrint(p.x);
				float y = getYPrint(p.y);

				game.stroke(p.color);
				game.strokeWeight(p.size);
				game.line(PApplet.lerp(x, p.lastX, 0.15f), PApplet.lerp(y, p.lastY, 0.15f), p.lastX, p.lastY);

				p.lastX = x;
				p.lastY = y;
				if (!Utility.withinRegion(p.lastX, p.lastY, -100, -100, game.gameResolution.x + 100, game.gameResolution.y + 100)) {
					iterator.remove();
					repopulate++;
				}
			}
			populate(repopulate);

			timeAccumulator += 1000 / game.frameRate;
		}

		/**
		 * Computes the flow-field slope at (x, y) for the current function.
		 *
		 * @param out       out[0] = slope X, out[1] = slope Y
		 * @param cosResult reusable holder for sinAndCos()
		 */
		private static void getSlopes(float x, float y, double[] out, DoubleWrapper cosResult) {
			double sx = 1;
			double sy = 1;

			switch (function) {
				case 0 -> {
					sx = cos(y);
					sy = sin(x);
				}
				case 1 -> {
					sx = cos(y * 5) * x * 0.3;
					sy = sin(x * 5) * y * 0.3;
				}
				case 2 -> sy = cos(x * y);
				case 3 -> sy = sin(x) * cos(y);
				case 4 -> sy = cos(x) * y * y;
				case 5 -> sy = log(abs(x)) * log(abs(y));
				case 6 -> sy = tan(x) * cos(y * y);
				case 7 -> { // orbit
					sx = sin(y * 0.1) * 3;
					sy = -sin(x * 0.1) * 3;
				}
				case 8 -> { // two orbits
					sx = y / 3;
					sy = (x - x * x * x) * 0.01;
				}
				case 9 -> {
					sx = -y;
					sy = -sin(x);
				}
				case 10 -> {
					sx = -1.5 * y;
					sy = -y - sin(1.5 * x) + 0.75;
				}
				case 11 -> {
					double sinX = sinAndCos(x, cosResult);
					double cosX = cosResult.value;

					double sinY = sinAndCos(y, cosResult);
					double cosY = cosResult.value;

					sx = sinY * cosX;
					sy = sinX * cosY;
				}
				default -> {
				}
			}

			out[0] = sx;
			out[1] = sy;
		}

		private static float getXPos(float x) {
			return (x - centerX) / scaleX;
		}

		private static float getYPos(float y) {
			return (y - centerY) / scaleY;
		}

		private static float getXPrint(float x) {
			return (scaleX * x + centerX);
		}

		private static float getYPrint(float y) {
			return (scaleY * y + centerY);
		}

		static class Particle {

			private float x, y;
			private float lastX, lastY;
			private final int size;
			private final int color;
			private final float direction;

			public Particle(float x, float y, int size, int color) {
				this.x = x;
				this.y = y;
				this.color = color;
				this.size = size;
				this.lastX = getXPrint(x);
				this.lastY = getYPrint(y);
				this.direction = (game.random(0.1f, 1) * (game.random(1) > 0.5f ? 1 : -1));

			}

			void update(float step) {
				getSlopes(x, y, SLOPES, COS_RESULT);
				x += direction * (float) SLOPES[0] * step;
				y += direction * (float) SLOPES[1] * step;
			}
		}
	}
}
