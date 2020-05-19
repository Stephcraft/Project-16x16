package project_16x16.windows;

import project_16x16.scene.GameplayScene;
import project_16x16.PClass;
import project_16x16.Main;
import project_16x16.ui.TextInputField;
import project_16x16.ui.Button;

public class SaveLevelWindow extends PClass {

	TextInputField input;
	Button pressSave;
	Button pressCancel;

	final String path = "src/main/resources/Storage/Game/Maps/save/";

	// Map Editor Scene
	public GameplayScene scene;

	public SaveLevelWindow(Main a, GameplayScene scene) {

		super(a);

		this.scene = scene;

		pressSave = new Button(applet);
		pressSave.setText("Save Level");
		pressSave.setPosition(applet.width / 2, applet.height / 2 + 150);

		pressCancel = new Button(applet);
		pressCancel.setText("Cancel");
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);

		input = new TextInputField(applet);
		input.setPosition(applet.width / 2, applet.height / 2);
		input.setWidth(300);
	}

	// Used to toggle the darkened background, use for buttons at the moment
	public void privacyDisplay() {
		// Display Privacy Area
		applet.fill(0, 100);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);
	}

	public void display() {
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
		pressSave.setPosition(applet.width / 2, applet.height / 2 + 150);
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);
		input.setPosition(applet.width / 2, applet.height / 2);

		input.update();

		pressSave.update();
		if (pressSave.event()) {
			scene.saveLevel(path + input.getText() + ".dat");
			input.setText("");
			scene.tool = GameplayScene.Tools.MODIFY;
		}

		pressCancel.update();
		if (pressCancel.event()) {
			input.setText("");
			scene.tool = GameplayScene.Tools.MODIFY;
		}
	}
}
