package scene;

import sidescroller.PClass;
import sidescroller.SideScroller;

/**
 * TODO (possibly): methods to be called when activated & de-activated.
 * 
 * @author micycle1
 *
 */
public abstract class PScene extends PClass {

	public PScene(SideScroller a) {
		super(a);
	}

	public abstract void setup();

	public abstract void draw();

	public abstract void drawUI();

	public abstract void debug();
}