package gui;

import processing.core.PApplet;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class PInput extends PClass {
	private int width;
	private int height;
	private int x;
	private int y;
	
	private String text;
	
	private boolean focus;
	
	public PInput(SideScroller a) {
		super(a);
		
		width = 200;
		height = 30;
		x = 0;
		y = 0;
		
		text = "";
	}
	
	public void display() {
		
		//Display Focus Box
		if(focus) {
			applet.noStroke();
			applet.fill(74,81,99,100);
			applet.rect(x,y,width+10,height+10);
		}
		
		//Display Box
		applet.strokeWeight(4);
		applet.stroke(74,81,99);
		applet.fill(0);
		applet.rect(x,y,width,height);
		
		//Display text
		applet.fill(255);
		applet.textSize(20);
		applet.textAlign(LEFT,CENTER);
		applet.text(text,x-width/2+8,y);
		
		//Display Cursor
		if(focus) {
			applet.fill(255,PApplet.map(PApplet.sin(applet.frameCount*(float)0.1),0,1,100,255));
			applet.text("_",x-width/2+8+applet.textWidth(text),y);
		}
	}
	
	public void update() {
		
		//Focus Event
		if(hover()) {
			if(applet.mousePressEvent) {
				focus = true;
			}
		}
		else {
			if(applet.mousePressEvent) {
				focus = false;
			}
		}
		
		//Typing
		if(focus) {
			applet.textSize(20);
			if(applet.keyPressEvent) {
				if(applet.key != '\u0008') {
					if(applet.textWidth(text) < width-20) {
						if(applet.key != '\uFFFF' && applet.key != '\n') {
							text += applet.key;
						}
					}
				}
				else {
					if(text.length() > 0) {
						text = text.substring(0, text.length()-1);
					}
				}
			}
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
