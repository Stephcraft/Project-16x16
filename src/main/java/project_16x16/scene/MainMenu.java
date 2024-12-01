package project_16x16.scene;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio.BGM;
import project_16x16.Constants;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.Utility;
import project_16x16.factory.AudioFactory;
import project_16x16.ui.Button;

/**
 * 
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	public Button pressStart;
	public Button pressQuit;
	public Button pressSettings; // TODO add settings menu
	public Button pressMultiplayer;

	private SideScroller game;

	private PGraphics background;

	public MainMenu(SideScroller a) {
		super(a);
		game = a;

		background = game.createGraphics((int) game.gameResolution.x, (int) game.gameResolution.x);
		background.noSmooth();
		Particles.assignApplet(a);
		Particles.populate(1000);

		pressStart = new Button(a);
		pressMultiplayer = new Button(a);
		pressQuit = new Button(a);
		pressSettings = new Button(a);

		pressStart.setText("Start Game");
		pressStart.setPosition(applet.width / 2, applet.height / 2 - 240);
		pressStart.setSize(300, 100);
		pressStart.setTextSize(40);

		pressMultiplayer.setText("Multiplayer");
		pressMultiplayer.setPosition(applet.width / 2, applet.height / 2 - 80);
		pressMultiplayer.setSize(300, 100);
		pressMultiplayer.setTextSize(40);

		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width / 2, applet.height / 2 + 80);
		pressSettings.setSize(300, 100);
		pressSettings.setTextSize(40);

		pressQuit.setText("Quit Game");
		pressQuit.setPosition(applet.width / 2, applet.height / 2 + 240);
		pressQuit.setSize(300, 100);
		pressQuit.setTextSize(40);
	}

	@Override
	public void switchTo() {
		super.switchTo();
		AudioFactory.getInstance().play(BGM.TEST3);
	}

	@Override
	public void drawUI() {
		game.fill(Constants.Colors.MENU_GREY, 40);
		game.rectMode(CORNER);
		game.noStroke();
		game.rect(0, 0, game.gameResolution.x, game.gameResolution.y);
		Particles.run();
		game.rectMode(CENTER);

		pressStart.manDisplay();
		pressMultiplayer.manDisplay();
		pressSettings.manDisplay();
		pressQuit.manDisplay();
	}

	private void update() {
		pressStart.update();
		if (pressStart.hover()) {
			((GameplayScene) GameScenes.GAME.getScene()).setSingleplayer(true);
			game.swapToScene(GameScenes.GAME);
		}

		pressMultiplayer.update();
		if (pressMultiplayer.hover()) {
			game.swapToScene(GameScenes.MULTIPLAYER_MENU);
		}

		pressSettings.update();
		if (pressSettings.hover()) {
			game.swapToScene(GameScenes.SETTINGS_MENU);
		}

		pressQuit.update();
		if (pressQuit.hover()) {
			System.exit(0);
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		update();
	}

	@Override
	void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 8 : // BACKSPACE
			case PConstants.ESC : // Pause
				game.returnScene();
				break;
			default :
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

		private static ArrayList<Particle> particles;

		private static int function = 0;
		private static int centerX, centerY;
		private static int scaleX, scaleY;

		private static final float STEP = 0.05f;
		private static final int TRANSITION_TIME = 360;

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
				particles.add(new Particle(getXPos(game.random(0, game.gameResolution.x)),
						getYPos(game.random(0, game.gameResolution.y)), (int) game.random(2, 8), Utility.colorToRGB(
								(int) game.random(0, 50), (int) game.random(150, 255), (int) game.random(150, 255))));
			}
		}

		static void run() {

			if (game.frameCount % TRANSITION_TIME == 0) {
				function++;
				function %= 12;
			}

			int repopulate = 0;
			for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext();) {
				Particle p = iterator.next();
				float x = (float) getSlopeX(p.x, p.y);
				float y = (float) getSlopeY(p.x, p.y);
				p.x += p.direction * x * STEP;
				p.y += p.direction * y * STEP;

				x = getXPrint(p.x);
				y = getYPrint(p.y);

				if (game.frameCount - p.start > 1) {
					game.stroke(p.color);
					game.strokeWeight(p.size);
					game.line(PApplet.lerp(x, p.lastX, 0.15f), PApplet.lerp(y, p.lastY, 0.15f), p.lastX, p.lastY);
				}
				p.lastX = x;
				p.lastY = y;
				if (!Utility.withinRegion(p.lastX, p.lastY, -100, -100, game.gameResolution.x + 100,
						game.gameResolution.y + 100)) {
					iterator.remove();
					repopulate++;
				}
			}
			populate(repopulate);
		}

		private static double getSlopeX(float x, float y) {
			switch (function) {
				// @formatter:off
				case 0: return Math.cos(y);
				case 1: return Math.cos(y*5)*x*0.3;
				case 2: 
				case 3: 
				case 4: 
				case 5: 
				case 6: return 1;
				case 7: return Math.sin(y*0.1)*3; //orbit
				case 8: return y/3; //two orbits
				case 9: return -y;		
				case 10: return -1.5*y;
				case 11: return Math.sin(y)*Math.cos(x);
				default : return 1;
				// @formatter:on
			}
		}

		private static double getSlopeY(float x, float y) {
			switch (function) {
				// @formatter:off
				case 0: return Math.sin(x);
				case 1: return Math.sin(x*5)*y*0.3;
				case 2: return Math.cos(x*y);
				case 3: return Math.sin(x)*Math.cos(y);
				case 4: return Math.cos(x)*y*y;
				case 5: return Math.log(Math.abs(x))*Math.log(Math.abs(y));
				case 6: return Math.tan(x)*Math.cos(y*y);
				case 7: return -Math.sin(x*0.1)*3; //orbit
				case 8: return (x-x*x*x)*0.01; //two orbits
				case 9: return -Math.sin(x);
				case 10: return -y-Math.sin(1.5*x) + .75;
				case 11: return Math.sin(x)*Math.cos(y);
				default : return 1;
				// @formatter:on
			}
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
			private final int start;

			public Particle(float x, float y, int size, int color) {
				this.x = x;
				this.y = y;
				this.color = color;
				this.size = size;
				this.lastX = x;
				this.lastY = y;
				this.direction = (game.random(0.1f, 1) * (game.random(1) > 0.5f ? 1 : -1));
				start = game.frameCount;
			}
		}
	}
}
