package project_16x16.ui;

import processing.core.PApplet;
import processing.event.MouseEvent;
import project_16x16.SideScroller;

/**
 * Horizontal slider -- used for options.
 * @author micycle1
 *
 */
public final class Slider extends Button {
	
	/**
	 * width
	 * val min/max
	 * pos
	 * temp val (on apply)
	 */
	private float value;
	private float tmpValue;

	public Slider(SideScroller a, float defaultValue) {
		super(a);
		this.value = defaultValue;
		tmpValue = this.value;
	}
	public Slider(SideScroller a) {
		super(a);
		this.value = 0.5f;
		this.tmpValue = this.value;
	}

	public void update(processing.event.MouseEvent e) {
		// TODO override this method
		tmpValue = PApplet.map(applet.mouseX - getX() + (float) super.getW() / 2, 0, getW(), 0.0f, 1.0f);
		if(hover()) {
			value = tmpValue;
		}

	}


	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public void intW() {
		width = (int) applet.textWidth(getText()) + 160;
	}
}
