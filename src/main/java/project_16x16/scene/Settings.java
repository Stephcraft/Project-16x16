package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.NumberInputField;

/**
 * WIP - the settings menu
 * 
 * @author micycle1
 *
 */
public class Settings extends PScene {
	// Window
	private final int WINDOW_X_POS = (int) applet.gameResolution.x / 2;
	private final int WINDOW_Y_POS = (int) applet.gameResolution.y / 2;
	// Buttons
	private final int GRAPHICS_Y_OFFSET = 10;
	private final int SOUND_Y_OFFSET = 50;
	private final int CONTROL_Y_OFFSET = 90;
	private final int WINDOWSIZE_Y_OFFSET = 130;
	private final int QUIT_Y_OFFSET = 320;
	private final int APPLY_Y_OFFSET = 280;
	// Text Display

	private Button quit;
	private Button apply;
	private Button pressGraphicsOptions;
	private Button pressSoundOptions;
	private Button pressControlsOptions;
	private Button pressMiscOptions;

	private NumberInputField windowSizeX;

	private SideScroller game;

	public Settings(SideScroller sideScroller) {
		super(sideScroller);
		game = sideScroller;

		quit = new Button(sideScroller);
		quit.setText("Quit");
		quit.setPosition(WINDOW_X_POS, WINDOW_Y_POS + QUIT_Y_OFFSET);

		apply = new Button(applet);
		apply.setText("Apply");
		apply.setPosition(WINDOW_X_POS, WINDOW_Y_POS + APPLY_Y_OFFSET);

		pressGraphicsOptions = new Button(applet);
		pressGraphicsOptions.setText("Configure Graphics");
		pressGraphicsOptions.setPosition(WINDOW_X_POS, WINDOW_Y_POS + GRAPHICS_Y_OFFSET);

		pressSoundOptions = new Button(applet);
		pressSoundOptions.setText("Configure Audio and Volume");
		pressSoundOptions.setPosition(WINDOW_X_POS, WINDOW_Y_POS + SOUND_Y_OFFSET);

		pressControlsOptions = new Button(applet);
		pressControlsOptions.setText("Configure Controls");
		pressControlsOptions.setPosition(WINDOW_X_POS, WINDOW_Y_POS + CONTROL_Y_OFFSET);

		windowSizeX = new NumberInputField(sideScroller);
		windowSizeX.setPosition(WINDOW_X_POS, WINDOW_Y_POS + WINDOWSIZE_Y_OFFSET);
	}

	@Override
	public void drawUI() {
		displayWindow();
//		// Display Window Title
		applet.fill(255);
		applet.textSize(60);
		applet.textAlign(CENTER, TOP);
		applet.text("Settings", applet.gameResolution.x / 2, 20);

		quit.display();
		apply.display();
		pressGraphicsOptions.display();
		pressSoundOptions.display();
		pressControlsOptions.display();

		OptionText("Graphics Options:", -WINDOW_X_POS / 2 + 50, -WINDOW_Y_POS / 2);
		OptionText(dynamicPadding("Resolution -", 22), -WINDOW_X_POS / 2 + 50, -WINDOW_Y_POS / 2 + 50);
		OptionText(dynamicPadding("Aspect Ratio -", 20), -WINDOW_X_POS / 2 + 50, -WINDOW_Y_POS / 2 + 70);
		OptionText(dynamicPadding("Display Mode -", 20), -WINDOW_X_POS / 2 + 50, -WINDOW_Y_POS / 2 + 90);

		windowSizeX.update();
		windowSizeX.display();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		update();
	}

	private void update() {
		quit.update();
		apply.update();
		pressGraphicsOptions.update();
		pressSoundOptions.update();
		pressControlsOptions.update();
		if (quit.hover()) {
			game.returnScene();
			return;
		}
		if (apply.hover()) {
			// game.resizeWindow(windowSizeX.getValue(), 720); // TODO change
			Notifications.addNotification("Options Applied", "Your configuration has been successfully applied.");
			return;
		}
		if (pressGraphicsOptions.hover()) {
			game.swapToScene(GameScenes.GRAPHICS_SETTINGS);
		}
		if (pressSoundOptions.hover()) {
			game.swapToScene(GameScenes.AUDIO_SETTINGS);
		}
		if (pressControlsOptions.hover()) {
			game.swapToScene(GameScenes.CONTROLS_SETTINGS);
		}
	}

	private void displayWindow() {
		background(19, 23, 35);
		// Display Window
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(WINDOW_X_POS, WINDOW_Y_POS, applet.gameResolution.x * 0.66f - 8, applet.gameResolution.y - 8);
	}

	private void OptionText(String toDisplay, int x, int y) { // Display the text at the bottom
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text(toDisplay, WINDOW_X_POS + x, WINDOW_Y_POS + y);
	}

	private String dynamicPadding(String textToPad, int charLimit) {
		int paddingAmount = Math.abs(textToPad.length() - charLimit);
		String textPad = textToPad;
		for (int i = 0; i < paddingAmount; i++) {
			textPad = " " + textPad;
		}
		return textPad;
	}

	@Override
	void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case PConstants.ESC: // Pause
				game.returnScene();
				break;
			default:
				break;
		}
	}

}
