package scene;

import dm.ui.Input;
import processing.event.MouseEvent;
import sidescroller.SideScroller;
import ui.Button;
import ui.TextInputField;

import java.util.regex.Pattern;

public class MultiplayerHostMenu extends PScene {

    public TextInputField ipInput;
    public Button pressMenu;
    public Button pressHost;
    Pattern p;

    private SideScroller game;

    public MultiplayerHostMenu(SideScroller a) {
        super(a);
        game = a;

        p = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$");


        pressMenu = new Button(a);
        ipInput = new TextInputField(a);
        pressHost = new Button(a);

        ipInput.setPosition(applet.width/2, applet.height/2-240);
        ipInput.setWidth(300);

        pressHost.setText("Host");
        pressHost.setPosition(applet.width/2, applet.height/2-80);
        pressHost.setTextSize(40);
        pressHost.setSize(300,100);

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
        pressHost.manDisplay();
    }

    public void update() {

        pressMenu.update();
        if(pressMenu.hover()) {
            game.swapToScene(game.menu);
        }

        ipInput.update();
        if (pressHost.hover() && p.matcher(ipInput.getText()).matches()) {
            String[] ip = ipInput.getText().split(":");
            game.swapToScene(game.game);
            game.game.setInfo(ip[0], Integer.parseInt(ip[1]), true);
        }

    }

    @Override
    void mouseReleased(MouseEvent e) {
        update();
    }
}
