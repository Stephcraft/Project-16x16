package project_16x16.scene;

import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.SideScroller;
import project_16x16.Utility;
import project_16x16.ui.Button;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;

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
	private final MenuNav nav;

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
		yes.setText("Yes");
		yes.setPosition(sideScroller.width / 2 - 190, sideScroller.height / 2 + 40);
		yes.setSize(340, 90);
		yes.setTextSize(40);

		no = new Button(applet);
		no.setText("No");
		no.setPosition(sideScroller.width / 2 + 190, sideScroller.height / 2 + 40);
		no.setSize(340, 90);
		no.setTextSize(40);

		nav = new MenuNav(sideScroller);
		nav.add(yes, () -> {
			onConfirm.run();
			applet.returnScene();
		});
		nav.add(no, () -> applet.returnScene());
	}

	public ConfirmationMenu(SideScroller sideScroller, Runnable onConfirm) {
		this(sideScroller, onConfirm, null);
	}

	@Override
	public void drawUI() {
		applet.image(cache, applet.width / 2, applet.height / 2); // draw cached & blurred game

		MenuStyle.dim(applet, 110);
		MenuStyle.title(applet, menutext != null ? menutext : "Are you sure?", applet.height / 2f - 130);
		if (menutext != null) {
			MenuStyle.caption(applet, "Are you sure?", applet.width / 2f, applet.height / 2f - 40);
		}
		nav.display();
	}

	@Override
	void mouseReleased(MouseEvent e) {
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (!nav.keyReleased(e) && e.getKeyCode() == PConstants.ESC) {
			applet.returnScene();
		}
	}

	@Override
	public void switchTo() {
		super.switchTo();
		cache = applet.captureFrame(); // when game is paused, cache the game screen.
		cache = Utility.blur(cache, 6, 2); // blur game screen
	}

}
