package project_16x16.scene;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.scene.PScene;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.TextInputField;


import java.util.regex.Pattern;

public class MultiplayerClientMenu extends PScene {

    public TextInputField ipInput;
    public Button pressMenu;
    public Button pressConnect;
    
    private SideScroller game;

    private static final Pattern p;
    
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
        ipInput.update();
        ipInput.display();
        pressMenu.manDisplay();
        pressConnect.manDisplay();

    }

	public void update() {
		pressMenu.update();
		if (pressMenu.hover()) {
			game.swapToScene(GameScenes.MAIN_MENU);
		}

		pressConnect.update();
		if (pressConnect.hover()) {
			if (p.matcher(ipInput.getText()).matches()) {
				String ip = ipInput.getText().split(":")[0];
				int port = Integer.valueOf(ipInput.getText().split(":")[1]);
				try {
					Multiplayer m = new Multiplayer(game, ip, port, false);
					((GameplayScene) (GameScenes.GAME.getScene())).setupMultiplayer(m);
					game.swapToScene(GameScenes.GAME);
				} catch (Exception e) {
					Notifications.addNotification("ERROR", "todo"); // TODO
				}
			} else {
				Notifications.addNotification("Invalid IP", "Include IP and port, eg:\n127.0.0.1:8080");
			}

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
