package ui;

import ui.Anchor;
import ui.Anchor.AnchorOrigin;
import ui.ScrollBar;
import processing.core.*;
import processing.event.MouseEvent;
import sidescroller.PClass;
import sidescroller.SideScroller;

/**
 * Horizontal ScrollBar
 */
public class ScrollBar extends PClass {

	public float barLocation = 0f; // between 0-1
	
	private PApplet app;
	
	private Anchor anchor;
	private Anchor barAnchor;
	private boolean barSelected = false;
	
	
	public ScrollBar(SideScroller a, Anchor anchor) {
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
		app.rect(anchor.globalX(), anchor.globalY(), anchor.globalWidth(), anchor.globalHeight());
		
		// DisplayLocationBar
		app.fill(100);
		barAnchor.localY = (int) PApplet.map(barLocation, 0, 1, 0, anchor.globalHeight() - barAnchor.localHeight);
		app.rect(barAnchor.globalX(), barAnchor.globalY(), anchor.globalWidth(), anchor.localHeight);
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
			barLocation = (float) PApplet.map(applet.mouseY, anchor.globalY() + anchor.globalHeight() - (barAnchor.localHeight/2), anchor.globalY() + (barAnchor.localHeight/2), 1, 0);
			barLocation = util.clamp(barLocation, 0, 1);
		}
	}
	
	public void mouseWheel(MouseEvent event) {
		barLocation += event.getCount() * 0.1;
		barLocation = util.clamp(barLocation, 0, 1);
	}
}
