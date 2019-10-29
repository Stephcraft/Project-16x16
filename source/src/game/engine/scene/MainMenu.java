package game.engine.scene;

import processing.event.MouseEvent;
import game.engine.sidescroller.DebugMode;
import ui.Button;

/**
 * TODO The main menu will be game.engine.scene
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	public Button pressStart;
	public Button pressQuit;
	public Button pressSettings; // TODO add game.state menu
	
	public MainMenu() {
		super();
		
		pressStart = new Button();
		pressQuit = new Button();
		pressSettings = new Button();
		
		pressStart.setText("Start Game");
		pressStart.setPosition(applet.width/2, applet.height/2-150);
		pressStart.setSize(300, 100);
		pressStart.setTextSize(40);
		
		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width/2, applet.height/2);
		pressSettings.setSize(300, 100);
		pressSettings.setTextSize(40);
		
		pressQuit.setText("Quit Game");
		pressQuit.setPosition(applet.width/2, applet.height/2 + 150);
		pressQuit.setSize(300, 100);
		pressQuit.setTextSize(40);
	}
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawUI() {
		background(29, 33, 45);
		pressStart.manDisplay();
		pressSettings.manDisplay();
		pressQuit.manDisplay();
	}

	private void update() {
		pressStart.update();
		if(pressStart.hover()) {
			applet.swapToScene(applet.getGame());
			applet.setDebug(DebugMode.ALL);
		}
		
		pressSettings.update();
		if(pressSettings.hover()) {
			
		}
		
		pressQuit.update();
		if(pressQuit.hover()) {
			System.exit(0);
		}
	}
	
    @Override
    void mouseReleased(MouseEvent e) {
    	update();
    }
}
