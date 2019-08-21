package windows;

import scene.SceneMapEditor;
import sidescroller.PClass;
import sidescroller.SideScroller;
import ui.Button;
import ui.TextInputField;

public class LoadTestWindow extends PClass{
	TextInputField input;
	Button pressLoad;
	Button pressCancel;

	String path = "Assets/Storage/Game/Maps/";

	// Map Editor Scene
	public SceneMapEditor scene;

	public LoadTestWindow(SideScroller a) {
		super(a);

		scene = (SceneMapEditor) a.mapEditor;

		pressLoad = new Button(applet);
		pressLoad.setText("Load Level");
		pressLoad.setPosition(applet.width / 2, applet.height / 2 + 150);

		pressCancel = new Button(applet);
		pressCancel.setText("Cancel");
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);

		input = new TextInputField(applet);
		input.setPosition(applet.width / 2, applet.height / 2);
		input.setWidth(300);
	}

	public void display() {
		// Display Privacy Area
		applet.fill(0, 100);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);
		
		// Display Window
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.width / 2, applet.height / 2, 400, 500);

		// Display Window Title
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text("Load Level", applet.width / 2, applet.height / 2 - 200);

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
		input.update();

		pressLoad.update();
		if (pressLoad.event()) {
			//util.saveLevel(path + input.getText() + ".dat");
			input.setText("");
			scene.tool = SceneMapEditor.Tools.MOVE;
		}

		pressCancel.update();
		if (pressCancel.event()) {
			input.setText("");
			scene.tool = SceneMapEditor.Tools.MOVE;
		}
	}
}