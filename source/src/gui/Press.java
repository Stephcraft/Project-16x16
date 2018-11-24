package gui;

import sidescroller.PClass;
import sidescroller.SideScroller;

public class Press extends PClass {
	
	private int width;
	private int height;
	private String text;
	private int x;
	private int y;
	
	private boolean focus;
	private boolean press;
	
	public boolean blocked;

	public Press(SideScroller a) {
		super(a);
		
		text = "Press me";
		width = 0;
		height = 0;
		x = 0;
		y = 0;
	}
	
	public void display() {
		applet.strokeWeight(4);
		if(focus) {
			applet.stroke(74,81,99);
			applet.fill(47,54,73);
		}
		else {
			applet.stroke(47,54,73);
			applet.fill(74,81,99);
		}
		applet.rect(x,y,width,height);
		
		applet.fill(255);
		applet.textAlign(CENTER,CENTER);
		applet.textSize(20);
		applet.text(text, x, y);
		width = (int)applet.textWidth(text)+40;
		height = 30;
	}
	
	public void update() {
		press = false;
		if(applet.mousePressEvent) {
			focus = hover();
		}
		if(applet.mouseReleaseEvent) {
			if(hover()) {
				press = true;
			}
			focus = false;
		}
	}
	
	public boolean hover() {
		return(applet.mouseX > x-width/2 && applet.mouseX < x+width/2 && applet.mouseY > y-height/2 && applet.mouseY < y+height/2);
	}
	
	public void setText(String txt) {
		text = txt;
	}
	
	public void setPosition(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	public boolean event() {
		return press;
	}
}
