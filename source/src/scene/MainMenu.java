package scene;

import sidescroller.SideScroller;

/**
 * TODO The main menu will be scene
 * @author micycle1
 *
 */
public final class MainMenu extends PScene {

	public MainMenu(SideScroller a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
	}

	@Override
	public void drawUI() {
		background(0);
		applet.rect(50, 50, 50, 50);
	}

	@Override
	public void debug() {
		// TODO Auto-generated method stub
	}
}
