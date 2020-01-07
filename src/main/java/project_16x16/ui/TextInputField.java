package project_16x16.ui;

import processing.core.PApplet;
import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Util;

/**
 * The PInput Class extends PClass
 * An input field 
 */
public class TextInputField extends PClass {
	
	protected int width;
	protected int height;
	protected int x;
	protected int y;

	protected String text;

	protected boolean focus;

	/**
	 * Constructor for PInput
	 * @param  a This a reference to the game //TODO: having variable names that are just letters can be confusing to new contributors
	 */
	public TextInputField(SideScroller a) {
		super(a);

		width = 200;
		height = 30;
		x = 0;
		y = 0;

		text = "";
	}

	/**
	 * Display for the PInput
	 */
	public void display() {

		// Display Focus Box
		if (focus) {
			applet.noStroke();
			applet.fill(74, 81, 99, 100);
			applet.rect(x, y, width + 10, height + 10);
		}

		// Display Box
		applet.strokeWeight(4);
		applet.stroke(74, 81, 99);
		applet.fill(0);
		applet.rect(x, y, width, height);

		// Display text
		applet.fill(255);
		applet.textSize(20);
		applet.textAlign(LEFT, CENTER);
		applet.text(text, x - width / 2 + 8, y);

		// Display Cursor
		if (focus) {
			applet.fill(255, PApplet.map(PApplet.sin(applet.frameCount * (float) 0.1), 0, 1, 100, 255));
			applet.text("_", x - width / 2 + 8 + applet.textWidth(text), y);
		}
	}

	/**
	 * Updates the window based on different player input
	 */
	public void update() {

		// Focus Event
		if (Util.hoverScreen(x, y, width, height)) {
			if (applet.mousePressEvent) {
				focus = true;
			}
		} else {
			if (applet.mousePressEvent) {
				focus = false;
			}
		}

		// Typing
		if (focus) {
			applet.textSize(20);
			if (applet.keyPressEvent) {
				if (applet.key != '\u0008') {
					if (applet.textWidth(text) < width - 20) {
						if (applet.key != '\uFFFF' && applet.key != '\n') {
							text += applet.key;
						}
					}
				} else {
					if (text.length() > 0) {
						text = text.substring(0, text.length() - 1);
					}
				}
			}
		}
	}

	/**
	 * Changes the object variable text to txt
	 * @param txt what to update text to as String
	 */
	public void setText(String txt) {
		text = txt;
	}

	/**
	 * Changes the position of the window
	 * @param _x the new x component
	 * @param _y the new y component
	 */
	public void setPosition(int _x, int _y) {
		x = _x;
		y = _y;
	}

	/**
	 * changes size and position
	 * @param _x new x component
	 * @param _y new y component
	 * @param w  new width
	 * @param h  new height
	 */
	public void set(int _x, int _y, int w, int h) {
		x = _x;
		y = _y;
		width = w;
		height = h;
	}

	/**
	 * Changes the width 
	 * @param w new width
	 */
	public void setWidth(int w) {
		width = w;
	}

	/**
	 * gets the object variable text
	 * @return text as String
	 */
	public String getText() {
		return text;
	}
}
