package project_16x16.ui;

import java.util.Arrays;
import java.util.HashSet;

import project_16x16.SideScroller;
import project_16x16.Util;

/**
 * Adapted from TextInputField class.
 * 
 * @author micycle1
 *
 */
public class NumberInputField extends TextInputField {

	private static final HashSet<Character> keys = new HashSet<Character>(
			Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));

	/**
	 * Constructor for PInput
	 * 
	 * @param a This a reference to the game //TODO: having variable names that are
	 *          just letters can be confusing to new contributors
	 */
	public NumberInputField(SideScroller a) {
		super(a);
	}
	
	/**
	 * Updates the window based on different player input
	 */
	@Override
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
				if (applet.key == '\u0008') { // backspace
					if (text.length() > 0) {
						text = text.substring(0, text.length() - 1);
					}
				} else if (keys.contains(applet.key)) {
					if (applet.textWidth(text) < width - 20) {
						if (applet.key != '\uFFFF' && applet.key != '\n') {
							text += applet.key;
						}
					}
				}
			}
		}
	}


	/**
	 * @return Value of this number input
	 */
	public int getValue() {
		int returnVal;
		try {
			returnVal = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			returnVal = 0;
		}
		return returnVal;
	}
}
