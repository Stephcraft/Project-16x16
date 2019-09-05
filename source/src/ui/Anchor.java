package ui;

import processing.core.PApplet;

public class Anchor {
	
	public enum AnchorOrigin
	{
		TopLeft,	Top, 	TopRight,
		Left, 		Center, Right,
		BottomLeft, Bottom, BottomRight,
	}
	
	public enum Stretch
	{
		Horizontal, Vertical,  InverseHorizontal, InverseVertical, None
	}
	
	// position respect to anchor type
	public int localX = 0; 
	public int localY = 0;
	
	public int localWidth = 0;
	public int localHeight = 0;
	
	public AnchorOrigin anchorOrigin = AnchorOrigin.Center;
	public Stretch stretch = Stretch.None;
	public int rectMode = PApplet.CORNER;
	
	private PApplet app;
	private Anchor frame = null;
	
	//TODO: add rectmode support
	
	public Anchor(Anchor anchor, int x, int y, int width, int height)
	{
		this(anchor.getPApplet(), x, y, width, height);
		this.frame = anchor;
	}
	
	public Anchor(PApplet applet, int x, int y, int width, int height)
	{
		this.app = applet;
		this.localX = x;
		this.localY = y;
		this.localWidth = width;
		this.localHeight = height;
	}
	
	public Anchor copy()
	{
		Anchor anchor = new Anchor(app, localX, localY, localWidth, localHeight);
		
		return anchor;
	}
	
	// PApplet
	
	public PApplet getPApplet()
	{
		if (hasContainer())	return frame.getPApplet();
		else				return app;
	}
	
	public void setPApplet(PApplet app)
	{
		this.app = app;
	}
	
	
	// Container
	
	public boolean hasContainer()
	{
		return frame != null;
	}
	
	public Anchor getContainer()
	{
		if (hasContainer()) return frame;
		else 				return null;
	}
	
	public void setContainer(Anchor anchor)
	{
		frame = anchor;
		app = anchor.getPApplet();
	}
	
	// Position
	
	public int X()
	{
		int value = 0;
		switch(anchorOrigin)
		{
			// case Left
			case Left:  case TopLeft:  case BottomLeft:
				value = localX + frameGlobalX();
				break;
			// case Right
			case Right: case TopRight: case BottomRight:
				value = (frameGlobalWidth() + localX) + frameGlobalX();
				break;
			// case Center
			case Center:
				value = (frameGlobalWidth()/2 + localX) + frameGlobalX();
				break;
			// case Top or Bottom
			default:
				value = localX + frameGlobalX();
				break;
		}
		return value;
	}
	
	public int Y()
	{
		int value = 0;
		switch(anchorOrigin)
		{
			// case TOP
			case Top:    case TopLeft:    case TopRight:
				value = localY + frameGlobalY();
				break;
			// case Bottom
			case Bottom: case BottomLeft: case BottomRight:
				value = (frameGlobalHeight() + localY) + frameGlobalY();
				break;
			// case Center
			case Center:
				value = (frameGlobalHeight()/2 + localY) + frameGlobalY();
				break;
			// case Left or Right
			default:
				value = localY + frameGlobalY();
				break;
		}
		return value;
	}
	
	// Stretch
	
	public int Width()
	{
		int value = 0;
		switch(stretch)
		{
			case Horizontal:
				value = frameGlobalWidth() - X();
				break;
			case InverseHorizontal:
				value = X() - frameGlobalWidth();;
				break;
			case Vertical: case InverseVertical:
				value = localWidth;
				break;
			case None:
				value = localWidth;
				break;
		}
		return value;
	}
	
	public int Height()
	{
		int value = 0;
		switch(stretch)
		{
			case Horizontal: case InverseHorizontal:
				value = localHeight;
				break;
			case Vertical:
				value = frameGlobalHeight() - Y();
				break;
			case InverseVertical:
				value = Y() - frameGlobalHeight();
				break;
			case None:
				value = localHeight;
				break;
		}
		return value;
	}
	
	// Container Variables
	
	public int frameGlobalX()
	{
		if (hasContainer()) return frame.X();
		else 				return 0;
	}
	
	public int frameGlobalY()
	{
		if (hasContainer()) return frame.Y();
		else 				return 0;
	}
	
	public int frameGlobalWidth()
	{
		if (hasContainer()) return frame.Width();
		else 				return app.width;
	}
	
	public int frameGlobalHeight()
	{
		if (hasContainer()) return frame.Height();
		else				return app.height;
	}
	
	// is mouse over anchor
	public boolean hover() {
		return(app.mouseX > X() && app.mouseX < X() + Width() 
		    && app.mouseY > Y() && app.mouseY < Y() + Height());
	}
	}
}
