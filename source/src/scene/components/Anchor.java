package scene.components;

import processing.core.PApplet;

public class Anchor {
	
	public enum AnchorX
	{
		Left, Center, Right
	}
	public enum AnchorY
	{
		Top, Center, Bottom
	}
	public enum Scale
	{
		Horizontal, Vertical, None
	}
	
	public int posX = 0;
	public int posY = 0;
	
	public int width = 0;
	public int height = 0;
	
	public AnchorX anchorX = AnchorX.Center;
	public AnchorY anchorY = AnchorY.Center;
	public Scale scale = Scale.None;
	public int rectMode = PApplet.CORNER;
	
	public PApplet app;				// reference for anchor
	private Anchor container = null;	// TODO: if set overrides references from Applet
	
	public Anchor(PApplet applet, int posX, int posY, int width, int height)
	{
		this.app = applet;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
	}
	
	public Anchor copy()
	{
		Anchor anchor = new Anchor(app, posX, posY, width, height);
		anchor.anchorX = anchorX;
		anchor.anchorY = anchorY;
		anchor.scale = scale;
		
		return anchor;
	}
	
	public int getPosX()
	{
		int value = 0;
		switch(anchorX)
		{
			case Left:
				value = posX;
				break;
			case Right:
				value = app.width + posX;
				break;
			case Center:
				value = app.width/2 + posX;
				break;
		}
		return value;
	}
	
	public int getPosY()
	{
		int value = 0;
		switch(anchorY)
		{
			case Top:
				value = posY;
				break;
			case Bottom:
				value = app.height + posY;
				break;
			case Center:
				value = app.height/2 + posY;
				break;
		}
		return value;
	}
	
	public int getWidth()
	{
		int value = 0;
		switch(scale)
		{
			case Horizontal:
				value = app.width - getPosX();
				break;
			case Vertical:
				value = width;
				break;
			case None:
				value = width;
				break;
		}
		return value;
	}
	
	public int getHeight()
	{
		int value = 0;
		switch(scale)
		{
			case Horizontal:
				value = height;
				break;
			case Vertical:
				value = app.height - getPosY();
				break;
			case None:
				value = height;
				break;
		}
		return value;
	}
}
