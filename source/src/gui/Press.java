package gui;

import sidescroller.PClass;
import sidescroller.SideScroller;

/**
 * The Press Class extends PClass
 * A button for the player to click
 */
public class Press extends PClass {
	
	private int width;
	private int height;
	private String text;
	private int x;
	private int y;
	
	private boolean focus;
	private boolean press;
	
	public boolean blocked;

	/**
	 * Constructor for Press
	 * @param  a Reference to Game
	 */
	public Press(SideScroller a) {
		super(a);
		
		text = "Press me";
		width = 0;
		height = 0;
		x = 0;
		y = 0;
	}
	
	/**
	 * Determins what to display for the button
	 */
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
	
	/**
	 * Updates the button
	 */
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
	
	/**
	 * Determins if the mouse is over the button
	 * @return response as a boolean
	 */
	public boolean hover() {
		return(applet.mouseX > x-width/2 && applet.mouseX < x+width/2 && applet.mouseY > y-height/2 && applet.mouseY < y+height/2);
	}
	
	/**
	 * Sets the text for the button
	 * @param txt the new text for the button
	 */
	public void setText(String txt) {
		text = txt;
	}
	
	/**
	 * Sets the position for the button
	 * @param _x the new x component
	 * @param _y the new y component
	 */
	public void setPosition(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	/**
	 * Determin if the button is pressed
	 * @return response as a boolean
	 */
	public boolean event() {
		return press;
	}
}
