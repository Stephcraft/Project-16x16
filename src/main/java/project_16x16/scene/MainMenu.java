package project_16x16.scene;

import processing.core.PImage;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.Audio;
import project_16x16.Audio.BGM;
import project_16x16.SideScroller.GameScenes;
import project_16x16.scene.PScene;
import project_16x16.ui.Button;

/**
 * TODO The main menu will be scene
 * 
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	final private int TOTAL_FRAMES = 213;
	final private PImage background[] = new PImage[TOTAL_FRAMES];
	final private String PATH = "src/main/resources/Video/";
	private int currentFrame;

	public Button pressStart;
	public Button pressQuit;
	public Button pressSettings; // TODO add settings menu
	public Button pressMultiplayer;

	private SideScroller game;

	public MainMenu(SideScroller a) {
		super(a);
		game = a;

		currentFrame = 0;
		loadBackground();

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
		Audio.play(BGM.DEFAULT1);
	}

	@Override
	public void drawUI() {
		drawBackground();
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

	private void loadBackground() {
		for (int i = 0; i < background.length; i++) {
			if (i < 10) {
				background[i] = loadImage(PATH + "frame_" + "00" + i + "_delay-0.03s.gif");
			} else if (i < 100) {
				background[i] = loadImage(PATH + "frame_" + "0" + i + "_delay-0.03s.gif");
			} else {
				background[i] = loadImage(PATH + "frame_" + i + "_delay-0.03s.gif");
			}
			background[i].resize(applet.width, applet.height);
		}
	}

	private void drawBackground() {
		if (applet.frameCount % 2 == 0) {
			currentFrame = ((currentFrame + 1) % background.length);
			applet.image(background[currentFrame], applet.width / 2, applet.height / 2);
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		update();
	}
}
