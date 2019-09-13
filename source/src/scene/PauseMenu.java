/**
 * 
 */
package scene;

import sidescroller.SideScroller;
import ui.Button;
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
	public void draw() {

	}

	@Override
	public void drawUI() {
		background(29, 33, 45);
		pressResume.manDisplay();
		pressSettings.manDisplay();
		pressMenu.manDisplay();
	}

	@Override
	public void debug() {
	}
	
	public void update() {
		pressResume.update();
		if(pressResume.event()) {
			game.swapScene(game.game);
			game.debug = SideScroller.debugType.ALL;
		}
		
		pressSettings.update();
		if(pressSettings.event()) {
			
		}
		
		pressMenu.update();
		if(pressMenu.event()) {
			game.swapScene(game.menu);
			game.debug = SideScroller.debugType.OFF;
		}
	}
	
    @Override
    void mouseReleased(MouseEvent e) {
    	update();
    }
}
