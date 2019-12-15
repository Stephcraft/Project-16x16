package project_16x16.scene;

import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.ui.Button;

public class MultiplayerMenu extends PScene {

    public Button pressHost;
    public Button pressClient;
    public Button pressMenu;

    private SideScroller game;

    public MultiplayerMenu(SideScroller a) {
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
    public void draw() {

    }

    @Override
    public void drawUI() {
        background(29, 33, 45);
        pressHost.manDisplay();
        pressClient.manDisplay();
        pressMenu.manDisplay();
    }

    public void update() {
        pressHost.update();
        if(pressHost.hover()) {
            game.swapToScene(game.mHostMenu);
        }

        pressClient.update();
        if(pressClient.hover()) {
            game.swapToScene(game.mClientMenu);
        }

        pressMenu.update();
        if(pressMenu.hover()) {
            game.swapToScene(game.menu);
        }
    }

    @Override
    void mouseReleased(MouseEvent e) {
        update();
    }
}
