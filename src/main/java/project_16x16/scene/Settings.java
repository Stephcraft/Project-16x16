package project_16x16.scene;

import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.NumberInputField;

/**
 * WIP
 * @author micycle1
 *
 */
public class Settings extends PScene {

	private Button quit;
	private Button apply;
	private Button pressGraphicsOptions;
	private Button pressSoundOptions;
	private Button pressControlsOptions;
	private Button pressMiscOptions;

	private NumberInputField windowSizeX;
	
	private SideScroller game;

	public Settings(SideScroller a) {
		super(a);
		game = a;

		quit = new Button(a);
		quit.setText("Quit");
		quit.setPosition(100, 100);

		apply = new Button(applet);
		apply.setText("Apply");
		apply.setPosition(100, 140);

		pressGraphicsOptions = new Button(applet);
		pressGraphicsOptions.setText("Configure Graphics");
		pressGraphicsOptions.setPosition(100, 180);

		pressSoundOptions = new Button(applet);
		pressSoundOptions.setText("Configure Audio and Volume");
		pressSoundOptions.setPosition(100, 220);

		pressControlsOptions = new Button(applet);
		pressControlsOptions.setText("Configure Controls");
		pressControlsOptions.setPosition(100, 260);
		
		windowSizeX = new NumberInputField(a);
		windowSizeX.setPosition(300, 300);
	}

	@Override
	public void drawUI() {
		background(19, 23, 35);
		
//		// Display Window
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.gameResolution.x / 2, applet.gameResolution.y / 2, applet.gameResolution.x * 0.66f - 8,
				applet.gameResolution.y - 8);

//		// Display Window Title
		applet.fill(255);
		applet.textSize(60);
		applet.textAlign(CENTER, TOP);
		applet.text("Options", applet.gameResolution.x / 2, 20);

		quit.display();
		apply.display();
		pressGraphicsOptions.display();
		pressSoundOptions.display();
		pressControlsOptions.display();

		int OptionCoordinate = -330;

//		displayOptionWindow();
		OptionText("Graphics Options");
		applet.text("Resolution:", applet.displayWidth / 2 - 270, applet.displayHeight / 2 + OptionCoordinate);
		applet.text("Aspect Ratio:", applet.displayWidth / 2 - 257, applet.displayHeight / 2 + OptionCoordinate + 50);
		applet.text("Display Mode:", applet.displayWidth / 2 - 257, applet.displayHeight / 2 + OptionCoordinate + 120);
		
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
		if (quit.hover()) {
			game.returnScene();
			return;
		}
		if (apply.hover()) {
			game.resizeWindow(windowSizeX.getValue(), 720); // TODO change
			return;
		}
	}

	private void displayOptionWindow() { // The same windows is displayed for each setting.
		applet.fill(29, 33, 45);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, 600, applet.height / 2 + 300);
	}

	private void OptionText(String toDisplay) { // Display the text at the bottom
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text(toDisplay, applet.width / 2, applet.displayHeight / 2 + 400);
	}

}
