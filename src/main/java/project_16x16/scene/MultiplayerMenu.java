package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Constants;
import project_16x16.Main;
import project_16x16.Main.GameScenes;
import project_16x16.ui.Button;

public class MultiplayerMenu extends PScene {

    public Button pressHost;
    public Button pressClient;
    public Button pressMenu;

    private Main game;

    public MultiplayerMenu(Main a) {
        super(a);
        game = a;

        pressHost = new Button(a);
        pressClient = new Button(a);
        pressMenu = new Button(a);

        pressHost.setText("Host a game");
        pressHost.setPosition(applet.width/2, applet.height/2-240);
        pressHost.setTextSize(40);
        pressHost.setSize(300,100);

        pressClient.setText("Connect to a game");
        pressClient.setPosition(applet.width/2, applet.height/2-80);
        pressClient.setTextSize(40);
        pressClient.setSize(400,100);

        pressMenu.setText("Back to menu");
        pressMenu.setPosition(applet.width/2, applet.height/2+240);
        pressMenu.setTextSize(40);
        pressMenu.setSize(300,100);
    }

    @Override
    public void drawUI() {
        background(Constants.Colors.MENU_GREY);
        pressHost.manDisplay();
        pressClient.manDisplay();
        pressMenu.manDisplay();
    }

    public void update() {
        pressHost.update();
        if(pressHost.hover()) {
            game.swapToScene(GameScenes.HOST_MENU);
        }

        pressClient.update();
        if(pressClient.hover()) {
            game.swapToScene(GameScenes.CLIENT_MENU);
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
			case PConstants.ESC : // Pause
				game.returnScene();
				break;
			default :
				break;
		}
	}
}
