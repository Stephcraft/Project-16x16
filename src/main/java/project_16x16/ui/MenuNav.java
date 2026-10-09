package project_16x16.ui;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.event.KeyEvent;
import project_16x16.Audio;
import project_16x16.Audio.SFX;
import project_16x16.SideScroller;

/**
 * Groups the buttons of a menu, drawing them and wiring each to an action.
 * Supports mouse clicks and keyboard navigation (up/down or W/S to move,
 * enter/space to activate).
 *
 * @author micycle1
 *
 */
public final class MenuNav {

	private final SideScroller applet;
	private final List<Button> buttons = new ArrayList<>();
	private final List<Runnable> actions = new ArrayList<>();
	private int selected = -1;
	private int lastMouseX, lastMouseY;
	private boolean letterKeys = true;
	private int lastHoverSoundSelection = -1;

	public MenuNav(SideScroller applet) {
		this.applet = applet;
	}

	public MenuNav add(Button button, Runnable action) {
		buttons.add(button);
		actions.add(action);
		return this;
	}

	/**
	 * Creates, positions and adds a standard menu button.
	 *
	 * @return the created button
	 */
	public Button button(String text, int x, int y, int w, int h, int textSize, Runnable action) {
		Button b = new Button(applet);
		b.setText(text);
		b.setPosition(x, y);
		b.setSize(w, h);
		b.setTextSize(textSize);
		add(b, action);
		return b;
	}

	/**
	 * Letter keys (W, S, space) navigate by default; disable them for menus that
	 * contain text fields.
	 */
	public MenuNav setLetterKeys(boolean enabled) {
		letterKeys = enabled;
		return this;
	}

	/** @return the keyboard/mouse-selected button, or null. */
	public Button selectedButton() {
		return selected >= 0 ? buttons.get(selected) : null;
	}

	private void activate(int i) {
		Audio.play(SFX.UI_CLICK);
		actions.get(i).run();
	}

	/**
	 * Draws all buttons. The mouse takes over the selection only when it moves,
	 * so keyboard navigation isn't overridden by a stationary cursor.
	 */
	public void display() {
		boolean mouseMoved = applet.mouseX != lastMouseX || applet.mouseY != lastMouseY;
		lastMouseX = applet.mouseX;
		lastMouseY = applet.mouseY;
		for (int i = 0; i < buttons.size(); i++) {
			Button b = buttons.get(i);
			if (mouseMoved) {
				if (b.hover()) {
					selected = i;
				} else if (selected == i) {
					selected = -1;
				}
			}
			b.setSelected(i == selected);
			b.manDisplay();
		}
		if (selected != lastHoverSoundSelection) {
			if (selected >= 0) {
				Audio.play(SFX.UI_HOVER);
			}
			lastHoverSoundSelection = selected;
		}
	}

	/** Runs the action of the button under the mouse, if any. */
	public void mouseReleased() {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).hover()) {
				activate(i);
				return;
			}
		}
	}

	/** @return true if the key was used for navigation/activation. */
	public boolean keyReleased(KeyEvent e) {
		int n = buttons.size();
		if (n == 0) {
			return false;
		}
		switch (e.getKeyCode()) {
			case PConstants.UP:
			case 'W':
				if (!letterKeys && e.getKeyCode() == 'W') {
					return false;
				}
				selected = selected <= 0 ? n - 1 : selected - 1;
				return true;
			case PConstants.DOWN:
			case 'S':
				if (!letterKeys && e.getKeyCode() == 'S') {
					return false;
				}
				selected = (selected + 1) % n;
				return true;
			case PConstants.ENTER:
			case ' ':
				if (e.getKeyCode() == ' ' && !letterKeys) {
					return false;
				}
				if (selected >= 0) {
					activate(selected);
				}
				return true;
			default:
				return false;
		}
	}
}
