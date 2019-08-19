package windows;

import scene.SceneMapEditor;
import sidescroller.PClass;
import sidescroller.SideScroller;
import ui.Button;
import ui.TextInputField;

public class LoadLevelWindow extends PClass{
	
	private Button pressLoad;
	private Button pressCancel;
	TextInputField input;
	private String path = "Assets/Storage/Game/Maps/";
	
	public SceneMapEditor scene;
	
	public LoadLevelWindow(SideScroller a) {
		super(a);
		
		scene = (SceneMapEditor) a.mapEditor;
		pressLoad = new Button(applet);
		pressLoad.setText("Load");
		pressLoad.setPosition(applet.width / 2, applet.height / 2 + 150);
		
		pressCancel = new Button(applet);
		pressCancel.setText("Cancel");
		pressCancel.setPosition(applet.width / 2, applet.height / 2 + 200);
		
		input = new TextInputField(applet);
		input.setPosition(applet.width / 2, applet.height / 2);
		input.setWidth(300);
	}
	
	public void display() {
		applet.fill(0, 100);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);
		
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.width / 2, applet.height / 2, 400, 500);
		
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text("Load Level", applet.width / 2, applet.height / 2 - 200);
		
		applet.textSize(20);
		applet.textAlign(LEFT, TOP);
		applet.text("Level Name :", applet.width / 2 - 150, applet.height / 2 - 40);
		
		input.display();
		
		pressLoad.display();
		
		pressCancel.display();
	}
	
	public void update() {
		pressLoad.update();
		input.update();
		if(pressLoad.event()) {
			SideScroller.LEVEL = path + input.getText();
			util.loadLevel(SideScroller.LEVEL);
			SideScroller.setRunning(true);
			scene.tool = SceneMapEditor.Tools.PLAY;
		}
		pressCancel.update();
		if (pressCancel.event()) {
			SideScroller.setRunning(true);
			scene.tool = SceneMapEditor.Tools.PLAY;
		}
	}
}
