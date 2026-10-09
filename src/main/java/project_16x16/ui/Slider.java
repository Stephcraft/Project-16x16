package project_16x16.ui;

import processing.core.PApplet;
import processing.core.PConstants;
import project_16x16.SideScroller;

/**
 * Horizontal slider with a label and percentage read-out. Used for options.
 * Call {@link #press()}, {@link #drag()} and {@link #release()} from the
 * owning scene's mouse handlers.
 */
public final class Slider extends Button {

	private static final int ACCENT = 0xFF7CC8FF;
	private static final int TRACK_WIDTH = 300;
	private static final int LABEL_WIDTH = 190;
	private static final int VALUE_WIDTH = 80;
	private static final float STEP = 0.05f;

	private float value; // between 0 and 1
	private boolean dragging;

	public Slider(SideScroller sideScroller, float defaultValue) {
		super(sideScroller);
		this.value = PApplet.constrain(defaultValue, 0, 1);
		width = LABEL_WIDTH + TRACK_WIDTH + VALUE_WIDTH;
		height = 40;
	}

	public Slider(SideScroller sideScroller) {
		this(sideScroller, 0.5f);
	}

	private float trackLeft() {
		return x - width / 2f + LABEL_WIDTH;
	}

	private void setFromMouse() {
		float mx = applet.getMouseCoordScreen().x;
		value = PApplet.constrain((mx - trackLeft()) / TRACK_WIDTH, 0, 1);
	}

	/** Begin dragging if the mouse is over the slider. */
	public void press() {
		if (hover()) {
			dragging = true;
			setFromMouse();
		}
	}

	/** @return true if the value was changed by dragging. */
	public boolean drag() {
		if (dragging) {
			setFromMouse();
		}
		return dragging;
	}

	public void release() {
		dragging = false;
	}

	/** Nudge the value by a fixed step (keyboard control). */
	public void nudge(int direction) {
		value = PApplet.constrain(Math.round(value / STEP) * STEP + direction * STEP, 0, 1);
	}

	@Override
	public void display() {
		manDisplay();
	}

	@Override
	public void manDisplay() {
		boolean active = hover() || isSelected() || dragging;
		float left = trackLeft();

		applet.textAlign(PConstants.LEFT, PConstants.CENTER);
		applet.textSize(26);
		applet.fill(255, active ? 255 : 200);
		applet.text(getText(), x - width / 2f, y);

		// track
		applet.noStroke();
		applet.fill(47, 54, 73);
		applet.rect(left + TRACK_WIDTH / 2f, y, TRACK_WIDTH, 8, 4);
		// filled portion
		applet.fill(ACCENT);
		if (value > 0) {
			applet.rect(left + TRACK_WIDTH * value / 2f, y, TRACK_WIDTH * value, 8, 4);
		}
		// thumb
		float r = active ? 12 : 9;
		applet.stroke(active ? 255 : ACCENT);
		applet.strokeWeight(3);
		applet.fill(29, 33, 45);
		applet.ellipse(left + TRACK_WIDTH * value, y, r * 2, r * 2);

		applet.textAlign(PConstants.RIGHT, PConstants.CENTER);
		applet.fill(255);
		applet.text(Math.round(value * 100) + "%", x + width / 2f, y);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = PApplet.constrain(value, 0, 1);
	}

	@Override
	public void intW() {
	}

	@Override
	public void intH() {
	}
}
