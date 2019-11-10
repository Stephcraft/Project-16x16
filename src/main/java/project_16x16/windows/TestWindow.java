package project_16x16.windows;

import project_16x16.scene.GameplayScene;
import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.TextInputField;
//CAN BE IGNORED: ONLY A TESTING WINDOW
public class TestWindow extends PClass{
	TextInputField input;
	Button pressLoad;
	Button pressCancel;

	String path = "Storage/Game/Maps/";

	// Map Editor Scene
	public GameplayScene scene;

	public TestWindow(SideScroller a) {
		super(a);

		scene = a.game;

		pressLoad = new Button(applet);
		pressLoad.setText("Test Window");
		pressLoad.setPosition(applet.width / 2, applet.height / 2 + 150);

		pressCancel = new Button(applet);
		pressCancel.setText("Cancel");
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);

		input = new TextInputField(applet);
		input.setPosition(applet.width / 2, applet.height / 2);
		input.setWidth(300);
	}

	//Used to toggle the darkened background, use for buttons at the moment
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
		applet.text("Test Window", applet.width / 2, applet.height / 2 - 200);

		applet.textSize(20);
		applet.textAlign(LEFT, TOP);
		applet.text("Level Name :", applet.width / 2 - 150, applet.height / 2 - 40);

		// Display Save Input
		input.display();

		// Display Save Press
		pressLoad.display();

		// Display Cancel Press
		pressCancel.display();
	}

	public void update() {
		pressLoad.setPosition(applet.width / 2, applet.height / 2 + 150);
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);
		input.setPosition(applet.width / 2, applet.height / 2);
		input.update();

		pressLoad.update();
		if (pressLoad.event()) {
			//util.saveLevel(path + input.getText() + ".dat");
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
