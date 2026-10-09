package project_16x16.ui;

import processing.core.PConstants;
import project_16x16.SideScroller;
import project_16x16.Utility;

/**
 * A button that opens a list of choices. Add it to a {@link MenuNav} with an
 * action of {@link #toggle()}, call {@link #drawList()} after the rest of the
 * menu (so the list overlays other buttons), and route clicks and keys through
 * {@link #click()} and {@link #key(int)} while {@link #isOpen()}.
 */
public final class Dropdown extends Button {

	private static final int ACCENT = 0xFF7CC8FF;
	private static final int ITEM_HEIGHT = 44;

	private final String label;
	private final String[] options;
	private int selectedIndex;
	private int highlight;
	private boolean open;

	public Dropdown(SideScroller sideScroller, String label, String[] options) {
		super(sideScroller);
		this.label = label;
		this.options = options;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int index) {
		selectedIndex = Math.max(0, Math.min(options.length - 1, index));
		highlight = selectedIndex;
	}

	public boolean isOpen() {
		return open;
	}

	public void toggle() {
		open = !open;
		highlight = selectedIndex;
	}

	@Override
	public void manDisplay() {
		setText(label + ":  " + options[selectedIndex]);
		super.manDisplay();
		// chevron
		float cx = x + width / 2f - 30;
		applet.noStroke();
		applet.fill(255);
		if (open) {
			applet.triangle(cx - 8, y + 4, cx + 8, y + 4, cx, y - 6);
		} else {
			applet.triangle(cx - 8, y - 4, cx + 8, y - 4, cx, y + 6);
		}
	}

	private float itemY(int i) {
		return y + height / 2f + 8 + ITEM_HEIGHT * (i + 0.5f);
	}

	/** Draws the option list on top of everything drawn so far. */
	public void drawList() {
		if (!open) {
			return;
		}
		float top = itemY(0) - ITEM_HEIGHT / 2f;
		applet.rectMode(PConstants.CENTER);
		applet.noStroke();
		applet.fill(0, 90);
		applet.rect(x, top + options.length * ITEM_HEIGHT / 2f + 4, width + 8, options.length * ITEM_HEIGHT + 8, 10);
		applet.strokeWeight(3);
		applet.stroke(ACCENT);
		applet.fill(19, 23, 35);
		applet.rect(x, top + options.length * ITEM_HEIGHT / 2f, width, options.length * ITEM_HEIGHT, 10);

		applet.textAlign(PConstants.CENTER, PConstants.CENTER);
		applet.textSize(26);
		for (int i = 0; i < options.length; i++) {
			if (Utility.hoverScreen(x, itemY(i), width, ITEM_HEIGHT)) {
				highlight = i;
			}
			if (i == highlight) {
				applet.noStroke();
				applet.fill(74, 81, 99);
				applet.rect(x, itemY(i), width - 10, ITEM_HEIGHT - 6, 6);
			}
			applet.fill(i == selectedIndex ? ACCENT : 255);
			applet.text(options[i], x, itemY(i));
		}
	}

	/**
	 * Handles a click while open: picks the option under the mouse (if any) and
	 * closes.
	 *
	 * @return true if the click was consumed (the list was open)
	 */
	public boolean click() {
		if (!open) {
			return false;
		}
		for (int i = 0; i < options.length; i++) {
			if (Utility.hoverScreen(x, itemY(i), width, ITEM_HEIGHT)) {
				selectedIndex = i;
				break;
			}
		}
		open = false;
		return true;
	}

	/**
	 * Handles a key while open.
	 *
	 * @return true if the key was consumed (the list was open)
	 */
	public boolean key(int keyCode) {
		if (!open) {
			return false;
		}
		switch (keyCode) {
			case PConstants.UP:
			case 'W':
				highlight = (highlight + options.length - 1) % options.length;
				break;
			case PConstants.DOWN:
			case 'S':
				highlight = (highlight + 1) % options.length;
				break;
			case PConstants.ENTER:
			case ' ':
				selectedIndex = highlight;
				open = false;
				break;
			case PConstants.ESC:
				open = false;
				break;
			default:
				break;
		}
		return true;
	}
}
