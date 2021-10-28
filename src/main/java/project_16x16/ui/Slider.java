package project_16x16.ui;

import processing.core.PApplet;
import project_16x16.SideScroller;

/**
 * Horizontal slider.
 * Used for options.
 */
public final class Slider extends Button {

	private float value; // between 0 and 1
	private float tmpValue;
    private static final int thumbSize = 10;

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

    /**
     * Updates the slider value.
     * Sets the slider value between 0 and 1.
     */
    @Override
    public void update() {
        tmpValue = PApplet.map(applet.mouseX - getX() + (float) super.getW() / 2, 0, getW(), 0.0f, 1.0f);
        if (hover()) {
            value = tmpValue;
        }
    }

    @Override
    public void display() {
        super.display();

        // display the thumb
        applet.rect(x - (float) width / 2 + width * value, y, thumbSize, height + (float) thumbSize / 4);
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
