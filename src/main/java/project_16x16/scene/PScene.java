package project_16x16.scene;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

import project_16x16.PClass;
import project_16x16.SideScroller;

/**
 * Scenes are a way of encapsulating different game states/views (such as menu
 * vs playable game).
 * 
 * @author micycle1
 *
 */
public abstract class PScene extends PClass {

	/**
	 * Keep track of whether the scene has ever registered methods with PApplet --
	 * this is needed because calling
	 * {@link processing.core.PApplet#unregisterMethod(String, Object)
	 * unregisterMethod()} will crash the PApplet (even with try-catch) if methods
	 * have not been previously registered.
	 */
	private boolean registered = false;

	public PScene(SideScroller a) {
		super(a);
	}

	/**
	 * Code here is affected by camera.
	 */
	public void draw() {
	}

	/**
	 * Code here is unaffected by camera.
	 */
	public void drawUI() {
	}

	/**
	 * Code here called if dev debug mode is ON (affected by camera).
	 */
	public void debug() {
	}

	/**
	 * Called when scene is made active (set-up). If you @Override this method,
	 * ensure you still call super.switchTo().
	 */
	public void switchTo() {
		applet.registerMethod("keyEvent", this);
		applet.registerMethod("mouseEvent", this);
		registered = true;
	}

	/**
	 * Called when the scene was active but another scene made active (tidy-up). If
	 * you @Override this method, ensure you still call super.switchFrom().
	 */
	public void switchFrom() {
		if (registered) {
			applet.unregisterMethod("keyEvent", this);
			applet.unregisterMethod("mouseEvent", this);
		}
	}

	/**
	 * This method is <b>Public</b> only to enable binding to a parent PApplet.
	 * <p>
	 * You can <b>ignore this method</b> since the parent sketch will call it
	 * automatically when it detects a mouse event (provided register() has been
	 * called).
	 */
	public final void mouseEvent(MouseEvent e) {
		switch (e.getAction()) {
			case MouseEvent.PRESS :
				mousePressed(e);
				break;
			case MouseEvent.RELEASE :
				mouseReleased(e);
				break;
			case MouseEvent.CLICK :
				mouseClicked(e);
				break;
			case MouseEvent.WHEEL :
				mouseWheel(e);
				break;
			case MouseEvent.DRAG :
				mouseDragged(e);
				break;
			default :
				break;
		}
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>PRESS</b>
	 * {@link processing.event.MouseEvent MouseEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when the mouse is
	 * <b>pressed</b>.
	 */
	void mousePressed(MouseEvent e) {
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>RELEASE</b>
	 * {@link processing.event.MouseEvent MouseEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when the mouse is
	 * <b>released</b>.
	 */
	void mouseReleased(MouseEvent e) {
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>CLICK</b>
	 * {@link processing.event.MouseEvent MouseEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when the mouse is
	 * <b>clicked</b>. (a press and release in quick succession).
	 */
	void mouseClicked(MouseEvent e) {
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>WHEEL</b>
	 * {@link processing.event.MouseEvent MouseEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when the mouse wheel is
	 * <b>scrolled</b>.
	 * <p>
	 * Use the getCount() method of the MouseEvent e parameter to get the scroll
	 * direction.
	 */
	void mouseWheel(MouseEvent e) {
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>DRAG</b>
	 * {@link processing.event.MouseEvent MouseEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when the mouse is
	 * <b>dragged</b>.
	 */
	void mouseDragged(MouseEvent e) {
	}

	/**
	 * This method is <b>Public</b> only to enable binding to a parent PApplet.
	 * <p>
	 * You can <b>ignore this method</b> since the parent sketch will call it
	 * automatically when it detects a key event (provided register() has been
	 * called).
	 */
	public final void keyEvent(KeyEvent e) {
		switch (e.getAction()) {
			case KeyEvent.PRESS :
				keyPressed(e);
				break;
			case KeyEvent.RELEASE :
				keyReleased(e);
				break;
			default :
				break;
		}
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>PRESS</b>
	 * {@link processing.event.KeyEvent KeyEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when a key is
	 * <b>pressed</b>.
	 */
	void keyPressed(KeyEvent e) {
	}

	/**
	 * Called automatically when the parent PApplet issues a <b>RELEASE</b>
	 * {@link processing.event.KeyEvent KeyEvent}.
	 * <p>
	 * Therefore write any code here that should be executed when a key is
	 * <b>released</b>.
	 */
	void keyReleased(KeyEvent e) {
	}
}