/**
 * 
 */
package game.engine.scene;

import game.engine.sidescroller.DebugMode;
import game.engine.sidescroller.Util;
import ui.Button;
import processing.core.PImage;
import processing.event.MouseEvent;

/**
 * @author Quillbert182
 *
 */
public class PauseMenu extends PScene {

	public Button pressResume;
	public Button pressMenu;	//Retruns to main menu
	public Button pressSettings; //TODO add game.state menu

	private PImage cache;
	
	public PauseMenu() {
		super();
		
		pressResume = new Button();
		pressSettings = new Button();
		pressMenu = new Button();
		
		pressResume.setText("Resume Game");
		pressResume.setPosition(applet.width/2, applet.height/2-150);
		pressResume.setTextSize(40);
		pressResume.setSize(300,100);
		
		pressSettings.setText("Settings");
		pressSettings.setPosition(applet.width/2, applet.height/2);
		pressSettings.setTextSize(40);
		pressSettings.setSize(300,100);
		
		pressMenu.setText("Main Menu");
		pressMenu.setPosition(applet.width/2, applet.height/2+150);
		pressMenu.setTextSize(40);
		pressMenu.setSize(300,100);
	}

	@Override
	public void draw() {

	}
	
	@Override
	public void switchTo() {
		super.switchTo();
		cache = applet.get(); // when game is paused, cache the game screen.
		cache = Util.blur(cache, 3, 2); // blur game screen
	}

	@Override
	public void drawUI() {
		applet.image(cache, applet.width/2, applet.height/2); // draw cached & blurred game
		pressResume.manDisplay();
		pressSettings.manDisplay();
		pressMenu.manDisplay();
	}

	@Override
	public void debug() {
	}
	
	public void update() {
		pressResume.update();
		if(pressResume.hover()) {
			applet.returnScene();
			applet.setDebug(DebugMode.ALL);
		}
		
		pressSettings.update();
		if(pressSettings.hover()) {
			
		}
		
		pressMenu.update();
		if(pressMenu.hover()) {
			applet.swapToScene(applet.getMenu());
			applet.setDebug(DebugMode.OFF);
		}
	}
	
    @Override
    void mouseReleased(MouseEvent e) {
    	update();
    }
}
