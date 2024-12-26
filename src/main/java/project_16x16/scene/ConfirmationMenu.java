package project_16x16.scene;

import processing.core.PImage;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.Utility;
import project_16x16.ui.Button;

/**
 * Confirmation menus offer the user a chance to confirm or cancel their
 * proposed changes. The design is such that clicking "yes" should apply
 * proposed changes (runs the given Runnable); clicking "no" is functionless,
 * and merely goes back to the previous menu.
 * 
 * @author micycle1
 *
 */
public class ConfirmationMenu extends PScene {

	private final Runnable onConfirm;
	private final String menutext;

	private PImage cache;

	private Button yes;
	private Button no;

	/**
	 * 
	 * @param sideScroller
	 * @param onConfirm    the code that runs if "yes" is clicked
	 * @param menutext
	 */
	public ConfirmationMenu(SideScroller sideScroller, Runnable onConfirm, String menutext) {
		super(sideScroller);
		this.onConfirm = onConfirm;
		this.menutext = menutext;

		yes = new Button(applet);
		yes.setText("Yes!");
		yes.setPosition(sideScroller.width / 2 - 100, sideScroller.height / 2);
		yes.setSize(300, 100);
		yes.setTextSize(40);

		no = new Button(applet);
		no.setText("No!");
		no.setPosition(sideScroller.width / 2 + 100, sideScroller.height / 2);
		no.setSize(300, 100);
		no.setTextSize(40);
	}

	public ConfirmationMenu(SideScroller sideScroller, Runnable onConfirm) {
		this(sideScroller, onConfirm, null);
	}

	@Override
	public void drawUI() {
		applet.image(cache, applet.width / 2, applet.height / 2); // draw cached & blurred game

		applet.textSize(60);
		if (menutext != null) {
			applet.text(menutext, applet.width / 2, 200);
			applet.textSize(30);
			applet.text("Are you sure?", applet.width / 2, 250);
		} else {
			applet.text("Are you sure?", applet.width / 2, 200);
		}

		yes.display();
		no.display();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		yes.update();
		no.update();

		if (yes.hover()) {
			onConfirm.run();
			applet.returnScene();
			return;
		}

		if (no.hover()) {
			applet.returnScene();
			return;
		}

	}

	@Override
	public void switchTo() {
		super.switchTo();
		cache = applet.get(); // when game is paused, cache the game screen.
		cache = Utility.blur(cache, 6, 2); // blur game screen
	}

}
