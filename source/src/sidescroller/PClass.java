package sidescroller;

import processing.core.*;
import sidescroller.Util;

public class PClass {
	public SideScroller applet;
	public Util util;
	
	public int CENTER;
	public int CORNER;
	
	public final int UP = PApplet.UP;
	public final int DOWN = PApplet.DOWN;
	public final int LEFT = PApplet.LEFT;
	public final int RIGHT = PApplet.RIGHT;
	public final int TOP;
	public final int KEY_W = 87;
	public final int KEY_A = 65;
	public final int KEY_S = 83;
	public final int KEY_D = 68;
	
	public PClass(SideScroller a) {
		applet = a;
		util = new Util(a);
		
		CENTER = PApplet.CENTER;
		CORNER = PApplet.CORNER;
		
		//UP = PApplet.UP;
		//DOWN = PApplet.DOWN;
		//LEFT = PApplet.LEFT;
		//RIGHT = PApplet.RIGHT;
		
		TOP = PApplet.TOP;
	}
	
	public void println(String msg) {
		PApplet.println(msg);
	}
	
	public void background(int r, int g, int b) {
		applet.background(r,g,b);
	}
	public void background(int g) {
		applet.background(g);
	}
	public void image(PGraphics img, float x, float y) {
		applet.image(img, x, y);
	}
	public PImage loadImage(String src) {
		return applet.loadImage(src);
	}
	
	public float round(float n) {
		return PApplet.round(n);
	}
}
