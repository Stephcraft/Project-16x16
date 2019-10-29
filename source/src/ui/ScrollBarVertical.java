package ui;

import ui.Anchor.AnchorOrigin;
import processing.core.*;
import processing.event.MouseEvent;
import game.engine.sidescroller.PClass;
import game.engine.sidescroller.Util;

/**
 * Horizontal ScrollBar
 */
public class ScrollBarVertical extends PClass {

	public float barLocation = 0f; // between 0-1
	
	protected Anchor container;
	protected Anchor barAnchor;
	protected boolean barSelected = false;
	
	
	public ScrollBarVertical(Anchor anchor) {
		super();
		setAnchor(anchor);
	}
	
	public void setAnchor(Anchor anchor){
		container = anchor;
		barAnchor = new Anchor(anchor, 0, 0, container.localWidth, container.Height()/5);
		barAnchor.anchorOrigin = AnchorOrigin.TopLeft;
	}
	
	public void display() {
		//Display ScrollBar
		applet.noStroke();
		applet.fill(100, 100);
		applet.rectMode(PApplet.CORNER);
		applet.rect(container.X(), container.Y(), container.Width(), container.Height());
		
		// DisplayLocationBar
		applet.fill(100);
		barAnchor.localY = (int) PApplet.map(barLocation, 0, 1, 0, container.Height() - barAnchor.localHeight);
		applet.rect(barAnchor.X(), barAnchor.Y(), barAnchor.Width(), barAnchor.Height());
	}
	
	public void update() {
		if (applet.isMousePressEvent() && container.hover()) {
			barSelected = true;
		}
		if (applet.isMouseReleaseEvent()) {
			barSelected = false;
		}
		if (barSelected)
		{
			barLocation = (float) PApplet.map(applet.mouseY, container.Y() + container.Height() - (barAnchor.localHeight/2), container.Y() + (barAnchor.localHeight/2), 1, 0);
			barLocation = Util.clamp(barLocation, 0, 1);
		}
	}
	
	public void setBarRatio(float value) {
		barAnchor.localHeight = (int) (value * container.Height());
	}
	
	public void mouseWheel(MouseEvent event) {
		barLocation += event.getCount() * 0.1;
		barLocation = Util.clamp(barLocation, 0, 1);
	}
}