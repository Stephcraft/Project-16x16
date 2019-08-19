package ui;

import sidescroller.PClass;
import sidescroller.SideScroller;

public class Tab extends PClass{
	
	private int x;
	private int y;
	private int width;
	private int height;
	private String text;
	private boolean focus;
	private PClass[] windowList; //array of windows to switch between
	
	public Tab(SideScroller a, String txt) {
		super(a);
		
		x = 0;
		y = 0;
		width = 50;
		height = 50;
		text = txt;
	}
	
	public void display() {
		if(focus) {
			applet.noStroke();
			applet.fill(74, 81, 99, 100);
			applet.rect(x, y, width + 10, height + 10);
		}
		
		applet.strokeWeight(4);
		applet.stroke(74, 81, 99);
		applet.fill(0);
		applet.rect(x, y, width, height);
		
		applet.fill(255);
		applet.textSize(20);
		applet.textAlign(LEFT, CENTER);
		applet.text(text, x - width / 2 + 8, y);
	}
	
	public void update() {
		if (hover()) {
			if (applet.mousePressEvent) {
				focus = true;
			}
		} else {
			if (applet.mousePressEvent) {
				focus = false;
			}
		}
	}
	
	public boolean hover() {
		return (applet.getMouseX() > x - width / 2 && applet.getMouseX() < x + width / 2 && applet.getMouseY() > y - height / 2
				&& applet.getMouseY() < y + height / 2);
	}
	
	public void setText(String txt) {
		text = txt;
	}
	
	public void setPosition(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	public void set(int _x, int _y, int w, int h) {
		x = _x;
		y = _y;
		width = w;
		height = h;
	}
	
	public void setWidth(int w) {
		width = w;
	}
	
	public String getText() {
		return text;
	}
}
