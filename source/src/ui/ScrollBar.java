package ui;

import processing.core.*;
import processing.event.MouseEvent;
import ui.Anchor;
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
		app = anchor.app;
		
		barAnchor = anchor.copy();
		barAnchor.scale = Anchor.Scale.None;
	}
	
	public void draw()
	{
		//Display ScrollBar
		app.noStroke();
		app.fill(100, 100);
		app.rectMode(anchor.rectMode);
		app.rect(anchor.getPosX(), anchor.getPosY(), anchor.getWidth(), anchor.getHeight());
		
		// DisplayLocationBar
		app.fill(100);
		int location = (int) PApplet.map(barLocation, 0, 1, barAnchor.posY, app.height - barAnchor.getHeight());
		app.rect(barAnchor.getPosX(), location, barAnchor.getWidth(), barAnchor.getHeight());
	}
}
