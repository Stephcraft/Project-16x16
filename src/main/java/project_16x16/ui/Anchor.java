package project_16x16.ui;

import processing.core.PConstants;
import project_16x16.SideScroller;

public class Anchor {

	public enum AnchorOrigin {
		TopLeft, Top, TopRight, Left, Center, Right, BottomLeft, Bottom, BottomRight,
	}

	public enum Stretch {
		Horizontal, Vertical, InverseHorizontal, InverseVertical, None
	}

	// position respect to anchor type
	public int localX = 0;
	public int localY = 0;

	public int localWidth = 0;
	public int localHeight = 0;

	public AnchorOrigin anchorOrigin = AnchorOrigin.TopLeft;
	public Stretch stretch = Stretch.None;

	private SideScroller applet;
	private Anchor frame = null;

	// TODO: add rectmode support

	public Anchor(Anchor anchor, int x, int y, int width, int height) {
		this(anchor.getPApplet(), x, y, width, height);
		this.frame = anchor;
	}

	public Anchor(SideScroller applet, int x, int y, int width, int height) {
		this.applet = applet;
		this.localX = x;
		this.localY = y;
		this.localWidth = width;
		this.localHeight = height;
	}

	public Anchor copy() {
		Anchor anchor = new Anchor(applet, localX, localY, localWidth, localHeight);

		return anchor;
	}

	// PApplet

	public SideScroller getPApplet() {
		if (hasContainer()) {
			return frame.getPApplet();
		} else {
			return applet;
		}
	}

	public void setPApplet(SideScroller applet) {
		this.applet = applet;
	}

	// Container

	public boolean hasContainer() {
		return frame != null;
	}

	public Anchor getContainer() {
		if (hasContainer()) {
			return frame;
		} else {
			return null;
		}
	}

	public void setContainer(Anchor anchor) {
		frame = anchor;
		applet = anchor.getPApplet();
	}

	// Position

	public int X() {
		int value = 0;
		switch (anchorOrigin) {
			// case Left
			case Left:
			case TopLeft:
			case BottomLeft:
				value = localX + frameGlobalX();
				break;
			// case Right
			case Right:
			case TopRight:
			case BottomRight:
				value = (frameGlobalWidth() + localX) + frameGlobalX();
				break;
			// case Center
			case Center:
				value = (frameGlobalWidth() / 2 + localX) + frameGlobalX();
				break;
			// case Top or Bottom
			default:
				value = localX + frameGlobalX();
				break;
		}
		return value;
	}

	public int Y() {
		int value = 0;
		switch (anchorOrigin) {
			// case TOP
			case Top:
			case TopLeft:
			case TopRight:
				value = localY + frameGlobalY();
				break;
			// case Bottom
			case Bottom:
			case BottomLeft:
			case BottomRight:
				value = (frameGlobalHeight() + localY) + frameGlobalY();
				break;
			// case Center
			case Center:
				value = (frameGlobalHeight() / 2 + localY) + frameGlobalY();
				break;
			// case Left or Right
			default:
				value = localY + frameGlobalY();
				break;
		}
		return value;
	}

	// Stretch

	public int Width() {
		int value = 0;
		switch (stretch) {
			case Horizontal:
				value = frameGlobalWidth() - X();
				break;
			case InverseHorizontal:
				value = X() - frameGlobalWidth();
				;
				break;
			case Vertical:
			case InverseVertical:
				value = localWidth;
				break;
			case None:
				value = localWidth;
				break;
		}
		return value;
	}

	public int Height() {
		int value = 0;
		switch (stretch) {
			case Horizontal:
			case InverseHorizontal:
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

	public int frameGlobalX() {
		if (hasContainer()) {
			return frame.X();
		} else {
			return 0;
		}
	}

	public int frameGlobalY() {
		if (hasContainer()) {
			return frame.Y();
		} else {
			return 0;
		}
	}

	public int frameGlobalWidth() {
		if (hasContainer()) {
			return frame.Width();
		} else {
			return applet.width;
		}
	}

	public int frameGlobalHeight() {
		if (hasContainer()) {
			return frame.Height();
		} else {
			return applet.height;
		}
	}

	// is mouse over anchor
	public boolean hover() {
		return (applet.mouseX > X() && applet.mouseX < X() + Width() && applet.mouseY > Y() && applet.mouseY < Y() + Height());
	}

	public void debugMode() {
		applet.stroke(255, 0, 0);
		applet.noFill();
		applet.rectMode(PConstants.CORNER);
		applet.rect(X(), Y(), Width(), Height());
		applet.rectMode(PConstants.CENTER);
	}
}
