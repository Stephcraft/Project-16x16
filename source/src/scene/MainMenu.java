package scene;

import sidescroller.SideScroller;
import ui.Button;

/**
 * TODO The main menu will be scene
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	Button pressStart;
	Button pressQuit;
	Button pressSettings; // Doesn't yet do anything
	
	public MainMenu(SideScroller a) {
		super(a);
		// TODO Auto-generated constructor stub
		pressStart = new Button(a);
		pressQuit = new Button(a);
		pressSettings = new Button(a);
		
		pressStart.setText("Start Game");
		pressStart.setPosition(applet.width/2, applet.height/2-150);
		pressStart.setSize(300, 100);
		
		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width/2, applet.height/2);
		pressSettings.setSize(300, 100);
		
		pressQuit.setText("Quit Game");
		pressQuit.setPosition(applet.width/2, applet.height/2 + 150);
		pressQuit.setSize(300, 100);
	}

	@Override
	public void draw() {
	}

	@Override
	public void drawUI() {
		background(0);
		//applet.rect(50, 50, 50, 50);
		pressStart.manDisplay();
		pressSettings.manDisplay();
		pressQuit.manDisplay();
	}

	@Override
	public void debug() {
		// TODO Auto-generated method stub
	}
}
