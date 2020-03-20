package project_16x16.windows;

import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Utility;
import project_16x16.scene.GameplayScene;
import project_16x16.ui.Button;
import project_16x16.ui.TextInputField;

public class ImportLevelWindow extends PClass {

	TextInputField input;
	Button pressImport;
	Button pressCancel;

	final String jsonPath = "src/main/resources/";
	
	// Map Editor Scene
	public GameplayScene scene;
	
	public ImportLevelWindow(SideScroller a, GameplayScene scene) {
		
		super(a);
		
		this.scene = scene;
		
		pressImport = new Button(applet);
		pressImport.setText("Import Level");
		pressImport.setPosition(applet.width / 2, applet.height / 2 + 150);

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
		applet.text("Import Level", applet.width / 2, applet.height / 2 - 200);

		applet.textSize(20);
		applet.textAlign(LEFT, TOP);
		applet.text("Map Name", applet.width / 2 - 150, applet.height / 2 - 40);
		
		applet.textSize(20);
		applet.textAlign(LEFT, TOP);
		applet.text("Map must be inside resource folder", applet.width / 2 - 180, applet.height / 2 + 30);

		// Display Save Input
		input.display();

		// Display Save Press
		pressImport.display();

		// Display Cancel Press
		pressCancel.display();
	}

	public void update() {
		pressImport.setPosition(applet.width / 2, applet.height / 2 + 150);
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);
		input.setPosition(applet.width / 2, applet.height / 2);

		input.update();

		pressImport.update();
		if (pressImport.event()) {
			Utility.convertTiledLevel(jsonPath + input.getText() + ".json", input.getText());
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
