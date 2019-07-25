package ui;

import processing.core.*;
import ui.Anchor;
import ui.Anchor.AnchorOrigin;
import ui.ScrollBar;

public class ScrollBar {

	private Anchor anchor;
	private Anchor barAnchor;
	
	private PApplet app;
	public float barLocation = 0f; // between 0-1
	
	public ScrollBar(Anchor anchor) {
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
	
	public void draw()
	{
		//Display ScrollBar
		app.noStroke();
		app.fill(100, 100);
		app.rectMode(anchor.rectMode);
		app.rect(anchor.globalX(), anchor.globalY(), anchor.globalWidth(), anchor.globalHeight());
		
		// DisplayLocationBar
		app.fill(100);
		int location = (int) PApplet.map(barLocation, 0, 1, barAnchor.globalY(), anchor.globalY() + anchor.globalHeight() - barAnchor.globalHeight());
		app.rect(barAnchor.globalX(), location, anchor.globalWidth(), anchor.localHeight);
	}
	
	public int getPosX()
	{
		return anchor.globalX();
	}
	
	public int getPosY()
	{
		return anchor.globalY();
	}
	
	public int getLength()
	{
		// return longest length
		int value = anchor.globalWidth();
		if (anchor.globalHeight() > value) value = anchor.globalHeight();
		return value;
	}
}
