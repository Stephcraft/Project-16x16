package project_16x16.scene;

import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.scene.PScene;
import project_16x16.ui.Button;
import project_16x16.ui.TextInputField;


import java.util.regex.Pattern;

public class MultiplayerClientMenu extends PScene {

    public TextInputField ipInput;
    public Button pressMenu;
    public Button pressConnect;
    
    private SideScroller game;

    private static final Pattern p;
    
    private static final String IP = "127.0.0.1"; // TODO hardcoded
    private static final int port = 25565; // TODO hardcoded
    
	static {
		p = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)"
				+ "{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");
	}

    public MultiplayerClientMenu(SideScroller a) {
        super(a);
        game = a;
        
        pressMenu = new Button(a);
        pressConnect = new Button(a);
        ipInput = new TextInputField(a);


        ipInput.setPosition(applet.width/2, applet.height/2-240);
        ipInput.setWidth(300);

        pressConnect.setText("Connect");
        pressConnect.setPosition(applet.width/2, applet.height/2-80);
        pressConnect.setTextSize(40);
        pressConnect.setSize(300,100);

        pressMenu.setText("Back to menu");
        pressMenu.setPosition(applet.width/2, applet.height/2+240);
        pressMenu.setTextSize(40);
        pressMenu.setSize(300,100);
    }
    
    @Override
    public void drawUI() {
        background(29, 33, 45);
        ipInput.display();
        pressMenu.manDisplay();
        pressConnect.manDisplay();

    }

	public void update() {
		ipInput.update();

		pressMenu.update();
		if (pressMenu.hover()) {
			game.swapToScene(GameScenes.MAIN_MENU);
		}

		pressConnect.update();
		// && p.matcher(ipInput.getText()).matches()
		if (pressConnect.hover()) {
			// String[] ip = ipInput.getText().split(":");
			try {
				Multiplayer m = new Multiplayer(game, IP, port, false);
				((GameplayScene) (GameScenes.GAME.getScene())).setupMultiplayer(m);
				game.swapToScene(GameScenes.GAME);
			} catch (Exception e) {
				// TODO: UI MESSAGE
			}
		}
	}

    @Override
    void mouseReleased(MouseEvent e) {
        update();
    }
}
