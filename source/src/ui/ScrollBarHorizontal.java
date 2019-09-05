package ui;

import ui.Anchor;
import ui.Anchor.AnchorOrigin;
import processing.core.*;
import processing.event.MouseEvent;
import sidescroller.PClass;
import sidescroller.SideScroller;

/**
 * Horizontal ScrollBar
 */
public class ScrollBarHorizontal extends PClass {

	public float barLocation = 0f; // between 0-1
	
	protected PApplet app;
	
	protected Anchor anchor;
	protected Anchor barAnchor;
	protected boolean barSelected = false;
	
	
	public ScrollBarHorizontal(SideScroller a, Anchor anchor) {
		super(a);
		setAnchor(anchor);
	}
	
	public void setAnchor(Anchor anchor)
	{
		this.anchor = anchor;
		this.app = anchor.getPApplet();
		
		barAnchor = new Anchor(anchor, 0, 0, anchor.localWidth, anchor.localHeight);
		barAnchor.setContainer(anchor);
		barAnchor.anchorOrigin = AnchorOrigin.TopLeft;
	}
	
	public void display()
	{
		//Display ScrollBar
		app.noStroke();
		app.fill(100, 100);
		app.rectMode(anchor.rectMode);
		app.rect(anchor.X(), anchor.Y(), anchor.Width(), anchor.Height());
		
		// DisplayLocationBar
		app.fill(100);
		barAnchor.localX = (int) PApplet.map(barLocation, 0, 1, 0, anchor.Width() - barAnchor.localWidth);
		app.rect(barAnchor.X(), barAnchor.Y(), anchor.localWidth, anchor.Height());
	}
	
	public void update() {
		if (applet.mousePressEvent && anchor.hover()) {
			barSelected = true;
		}
		if (applet.mouseReleaseEvent) {
			barSelected = false;
		}
		if (barSelected)
		{
			barLocation = (float) PApplet.map(applet.mouseX, anchor.X() + anchor.Width() - (barAnchor.localWidth/2), anchor.X() + (barAnchor.localWidth/2), 1, 0);
			barLocation = util.clamp(barLocation, 0, 1);
		}
	}
	
	public void mouseWheel(MouseEvent event) {
		barLocation += event.getCount() * 0.1;
		barLocation = util.clamp(barLocation, 0, 1);
	}
}