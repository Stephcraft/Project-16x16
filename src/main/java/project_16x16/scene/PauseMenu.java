/**
 * 
 */
package project_16x16.scene;

import project_16x16.SideScroller;
import project_16x16.Util;
import project_16x16.SideScroller.GameScenes;
import project_16x16.ui.Button;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * @author Quillbert182
 *
 */
public class PauseMenu extends PScene {

	public Button pressResume;
	public Button pressMenu;	//Retruns to main menu
	public Button pressSettings; //TODO add settings menu
	
	private SideScroller game;
	private PImage cache;
	
	protected boolean switched = false;
	
	public PauseMenu(SideScroller a) {
		super(a);
		game = a;
		
		pressResume = new Button(a);
		pressSettings = new Button(a);
		pressMenu = new Button(a);
		
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
	public void switchTo() {
		super.switchTo();
		if (!switched) {
			cache = applet.get(); // when game is paused, cache the game screen.
			cache = Util.blur(cache, 3, 2); // blur game screen
		}
		switched = true;
	}

	@Override
	public void drawUI() {
		applet.image(cache, applet.width/2, applet.height/2); // draw cached & blurred game
		pressResume.manDisplay();
		pressSettings.manDisplay();
		pressMenu.manDisplay();
	}

	private void update() {
		pressResume.update();
		if(pressResume.hover()) {
			game.returnScene();
		}
		
		pressSettings.update();
		if(pressSettings.hover()) {
			game.swapToScene(GameScenes.SETTINGS_MENU);
		}
		
		pressMenu.update();
		if(pressMenu.hover()) {
			game.swapToScene(GameScenes.MAIN_MENU);
		}
	}
	
    @Override
    void mouseReleased(MouseEvent e) {
    	update();
    }
    
	@Override
	void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 8 : // BACKSPACE
			case PConstants.ESC : // Pause
				game.returnScene();
				break;
			default :
				break;
		}
	}
}
