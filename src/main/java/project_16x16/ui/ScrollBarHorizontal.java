package project_16x16.ui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;
import project_16x16.PClass;
import project_16x16.Utility;
import project_16x16.ui.Anchor.AnchorOrigin;

/**
 * Horizontal ScrollBar
 */
public class ScrollBarHorizontal extends PClass {

	private float barLocation = 0f; // between 0-1

	protected Anchor container;
	protected Anchor barAnchor;
	protected boolean barSelected = false;

	public ScrollBarHorizontal(Anchor anchor) {
		super(anchor.getPApplet());
		setAnchor(anchor);
	}

	public void setAnchor(Anchor anchor) {
		container = anchor;

		barAnchor = new Anchor(anchor, 0, 0, container.Width() / 5, container.localHeight);
		barAnchor.anchorOrigin = AnchorOrigin.TopLeft;
	}

	public void display() {
		// Display ScrollBar
		applet.noStroke();
		applet.fill(100, 100);
		applet.rectMode(PConstants.CORNER);
		applet.rect(container.X(), container.Y(), container.Width(), container.Height());

		// DisplayLocationBar
		applet.fill(100);
		barAnchor.localX = (int) PApplet.map(barLocation, 0, 1, 0, container.Width() - barAnchor.localWidth);
		applet.rect(barAnchor.X(), barAnchor.Y(), barAnchor.Width(), barAnchor.Height());
	}

	public void update() {
		if (applet.mousePressEvent && container.hover()) {
			barSelected = true;
		}
		if (applet.mouseReleaseEvent) {
			barSelected = false;
		}
		if (barSelected) {
			barLocation = PApplet.map(applet.mouseX, container.X() + container.Width() - (barAnchor.localWidth / 2), container.X() + (barAnchor.localWidth / 2),
					1, 0);
			barLocation = Utility.clamp(barLocation, 0, 1);
		}
	}

	public void setBarRatio(float value) {
		barAnchor.localWidth = (int) (value * container.Width());
	}

	public void mouseWheel(MouseEvent event) {
		barLocation += event.getCount() * 0.1f;
		barLocation = Utility.clamp(barLocation, 0, 1);
	}
}