package windows;

import java.util.ArrayList;
import java.util.HashSet;

import processing.event.KeyEvent;
import scene.SceneMapEditor;
import sidescroller.PClass;
import sidescroller.SideScroller;
import ui.Button;
import ui.TextInputField;

public class OptionsInterface extends PClass {

	//These are the buttons used for making the interface
	Button pressQuit;
	Button pressApply;
	Button pressGraphicsOptions;
	Button pressSoundOptions;
	Button pressControlsOptions;
	Button pressMiscOptions;

	public SceneMapEditor scene;
	public Options opts = Options.MENU; // Options list
	public TextInputField input; // The player will enter new controls in these fields

	String[] toDisplayControls = {"Left:", "Right:", "Up:", "Down:"}; // avoid copy pasting things

	public OptionsInterface(SideScroller a) {
		super(a);	

		scene = (SceneMapEditor) a.mapEditor;
		
		// Setup Buttons positions and names

		pressQuit = new Button(applet);
		pressQuit.setText("Quit");
		pressQuit.setPosition(applet.displayHeight / 2 + 680, applet.displayHeight / 2 +410);

		pressApply = new Button(applet);
		pressApply.setText("Apply");
		pressApply.setPosition(pressQuit.getXPosition() -647, pressQuit.getYPosition());

		pressGraphicsOptions = new Button(applet);
		pressGraphicsOptions.setText("Configure Graphics");
		pressGraphicsOptions.setPosition(applet.displayWidth / 2, applet.displayHeight / 2 - 300);

		pressSoundOptions = new Button(applet);
		pressSoundOptions.setText("Configure Audio and Volume");
		pressSoundOptions.setPosition(pressGraphicsOptions.getXPosition(), pressGraphicsOptions.getYPosition() + 100);

		pressControlsOptions = new Button(applet);
		pressControlsOptions.setText("Configure Controls");
		pressControlsOptions.setPosition(pressGraphicsOptions.getXPosition(), pressGraphicsOptions.getYPosition() + 200);
	}

	//Used to detect the button on which you press and then display the right window
	public enum Options {
		GRAPHICS, SOUND, CONTROLS, MISC, MENU;
	}

	public void display() {


		// Display Privacy Area (background)
		applet.fill(0, 100);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);

		//Display Window
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.width / 2, applet.height / 2, 800, applet.height);

		// Display Window Title
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text("Options", applet.width / 2, applet.displayHeight / 2 - 400);

		pressApply.display();

		pressQuit.display();

		pressGraphicsOptions.display();

		pressControlsOptions.display();

		pressSoundOptions.display();
		
	}

	public void update() { 

		if(opts == Options.MENU ) {

			pressControlsOptions.update();
			if(pressControlsOptions.event()) {
				opts = Options.CONTROLS;
			}

			pressSoundOptions.update();
			if(pressControlsOptions.event()) {
				opts = Options.SOUND;
			}

			pressGraphicsOptions.update();
			if(pressGraphicsOptions.event()) {
				opts = Options.GRAPHICS;
			}
		}
		pressApply.update();
		if(pressApply.event()) {
			opts = Options.MENU;
		}

		pressQuit.update();
		if(pressQuit.event()) {
			scene.tool = SceneMapEditor.Tools.MOVE;
			opts = Options.MENU;
		}
	}

	public void displayOptions() {
		
		int OptionCoordinate = -330; // This is used to set the option to the same coordinate 
		
		if(opts == Options.GRAPHICS) {
			displayOptionWindow();
			OptionText("Graphics Options");
			applet.text("Resolution:", applet.displayWidth / 2 - 270 , applet.displayHeight / 2 + OptionCoordinate );
			applet.text("Aspect Ratio:", applet.displayWidth / 2 - 257 , applet.displayHeight / 2 + OptionCoordinate + 50 );
			applet.text("Display Mode:", applet.displayWidth / 2 - 257 , applet.displayHeight / 2 + OptionCoordinate + 120 );
		}
		
		if(opts == Options.SOUND) {
			displayOptionWindow();
			OptionText("Sound and volume");
			applet.text("Music volume:", applet.displayWidth / 2 - 270 , applet.displayHeight / 2 + OptionCoordinate );
			applet.text("Sounds volume:", applet.displayWidth / 2 - 265 , applet.displayHeight / 2 + OptionCoordinate + 50 );
		}

		if(opts == Options.CONTROLS) { //done

			displayOptionWindow();
			OptionText("Key Bindings");
		

			for(int j = 0; j < 4; j++) {
				applet.text(toDisplayControls[j], applet.displayWidth / 2 - 330 , applet.displayHeight / 2 + OptionCoordinate );
				OptionCoordinate += 50;
			}
			OptionCoordinate = -330;
			

			for(int i = 0; i < 4; i++) { //Enter the key corresponding to the control in the TextInputField
				input = new TextInputField(applet);
				input.setPosition(applet.displayWidth / 2 - 250, applet.displayHeight / 2 + OptionCoordinate);
				input.setWidth(30);
				input.display();
				OptionCoordinate += 50;
				input.update();
			}	
		}
	}

	public void displayOptionWindow() { //The same windows is displayed for each setting.
		applet.fill(29, 33, 45);
		applet.noStroke();
		applet.rect(applet.width / 2 , applet.height / 2, 600, applet.height/ 2 +300);
	}

	public void OptionText(String toDisplay) { //Display the text at the bottom
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text(toDisplay, applet.width / 2, applet.displayHeight / 2 + 400);
	}
}
