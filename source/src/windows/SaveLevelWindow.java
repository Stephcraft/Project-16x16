package windows;

import scene.GameplayScene;
import state.GameState;
import sidescroller.PClass;
import sidescroller.SideScroller;
import ui.TextInputField;
import ui.Button;


public class SaveLevelWindow extends PClass {

	TextInputField input;
	Button pressSave;
	Button pressCancel;

	String path = "Assets/Storage/Game/Maps/";

	// Map Editor Scene
	public GameplayScene scene;

	public SaveLevelWindow(GameplayScene scene) {
		
		super();

		this.scene = scene; 

		pressSave = new Button();
		pressSave.setText("Save Level");
		pressSave.setPosition((int)(GameState.getInstance().getWindowSize().x / 2), (int)(GameState.getInstance().getWindowSize().y / 2 + 150));

		pressCancel = new Button();
		pressCancel.setText("Cancel");
		pressCancel.setPosition((int)(GameState.getInstance().getWindowSize().x / 2), (int)(GameState.getInstance().getWindowSize().y / 2 + 200));

		input = new TextInputField();
		input.setPosition((int)(GameState.getInstance().getWindowSize().x / 2), (int)(GameState.getInstance().getWindowSize().y / 2));
		input.setWidth(300);
	}

	//Used to toggle the darkened background, use for buttons at the moment
	public void privacyDisplay() {
		SideScroller applet = GameState.getInstance().getApplet();
		// Display Privacy Area
		applet.fill(0, 100);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);
	}
	
	public void display() {
		SideScroller applet = GameState.getInstance().getApplet();
		// Display Window
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.width / 2, applet.height / 2, 400, 500);

		// Display Window Title
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text("Save Level", applet.width / 2, applet.height / 2 - 200);

		applet.textSize(20);
		applet.textAlign(LEFT, TOP);
		applet.text("Level Name :", applet.width / 2 - 150, applet.height / 2 - 40);

		// Display Save Input
		input.display();

		// Display Save Press
		pressSave.display();

		// Display Cancel Press
		pressCancel.display();
	}

	public void update() {
		pressSave.setPosition((int)(GameState.getInstance().getWindowSize().x / 2), (int)(GameState.getInstance().getWindowSize().y / 2 + 150));
		pressCancel.setPosition((int)(GameState.getInstance().getWindowSize().x / 2), (int)(GameState.getInstance().getWindowSize().y / 2 + 200));
		input.setPosition((int)(GameState.getInstance().getWindowSize().x / 2), (int)(GameState.getInstance().getWindowSize().y / 2));
		
		input.update();

		pressSave.update();
		if (pressSave.event()) {
			scene.saveLevel(path + input.getText() + ".dat");
			input.setText("");
			scene.tool = GameplayScene.Tools.MOVE;
		}

		pressCancel.update();
		if (pressCancel.event()) {
			input.setText("");
			scene.tool = GameplayScene.Tools.MOVE;
		}
	}
}
