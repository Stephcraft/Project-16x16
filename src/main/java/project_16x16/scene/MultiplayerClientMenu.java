package project_16x16.scene;

import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.scene.PScene;
import project_16x16.ui.Button;
import project_16x16.ui.TextInputField;


import java.util.regex.Pattern;

public class MultiplayerClientMenu extends PScene {

    public TextInputField ipInput;
    public Button pressMenu;
    public Button pressConnect;

    Pattern p;

    private SideScroller game;

    public MultiplayerClientMenu(SideScroller a) {
        super(a);
        game = a;

        p = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");

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
    public void draw() {

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
        if(pressMenu.hover()) {
            game.swapToScene(game.menu);
        }

        pressConnect.update();
        // && p.matcher(ipInput.getText()).matches()
        if (pressConnect.hover()) {
           // String[] ip = ipInput.getText().split(":");
            game.game.setupMultiplayer(false);
            game.swapToScene(game.game);
        }
        else {
            //TODO: Add popup that says Invalid IP
        }
    }

    @Override
    void mouseReleased(MouseEvent e) {
        update();
    }
}
