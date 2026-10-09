package project_16x16.ui;

import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Time;

public class Tab extends PClass {

	private int tabCount;
	private Button[] buttons;
	private int activeButton;
	private int prevButton;
	private int buttonDistance = 0;
	/** Time since the active-tab highlight started sliding (ms). */
	private float slideElapsed = 0;

	/**
	 * The highlight slides with increasing speed: after t seconds it has travelled
	 * (SLIDE_RATE * t)^SLIDE_EXPONENT px.
	 */
	private static final float SLIDE_RATE = 22.8f;
	private static final double SLIDE_EXPONENT = 1 / 0.38;

	// Basic constructor for tab
	public Tab(SideScroller sideScroller, String[] texts, int tabs) {
		super(sideScroller);
		tabCount = tabs;
		buttons = new Button[tabCount];
		for (int i = 0; i < tabCount; i++) {
			buttons[i] = new Button(sideScroller);
			buttons[i].setText(texts[i]);
			if (i == 0) {
				buttons[i].setPosition((applet.width / 2) - 155, (applet.height / 2) - 265);
			} else {
				buttons[i].setPosition(buttons[i - 1].getX() + ((buttons[i - 1].getW() + buttons[i].getW()) / 2), (applet.height / 2) - 265);
			}
		}
	}

	// Update all buttons the tab contains
	public void update() {
		for (int j = 0; j < tabCount; j++) {
			if (j == 0) {
				buttons[j].setPosition((applet.width / 2) - 155, (applet.height / 2) - 265);
			} else {
				buttons[j].setPosition(buttons[j - 1].getX() + ((buttons[j - 1].getW() + buttons[j].getW()) / 2), (applet.height / 2) - 265);
			}
		}
		for (int i = 0; i < tabCount; i++) {
			buttons[i].update();
		}
	}

	// Display all buttons the tab contains
	public void display() {
		for (int i = 0; i < tabCount; i++) {
			buttons[i].display();
		}
		displayInactive();
		displayActive();
	}

	// Move active button to selected
	public void moveActive(int index) {
		setPrevButton(activeButton);
		setActiveButton(index);
		buttonDistance = buttons[prevButton].getX() - buttons[activeButton].getX();
		slideElapsed = 0;
	}

	// Display inactive thick button edges without changing the actual stroke normal
	// buttons use
	public void displayInactive() {
		applet.strokeWeight(8);
		applet.stroke(47, 54, 73);
		applet.fill(0, 150);
		for (int i = 0; i < tabCount; i++) {
			if (activeButton != i) {
				applet.rectMode(CENTER);
				applet.rect(buttons[i].getX(), buttons[i].getY(), buttons[i].getW(), buttons[i].getH());
			}
		}
	}

	// Selection animation between buttons and active button edge
	public void displayActive() {
		applet.strokeWeight(8);
		applet.stroke(255, 255, 255);
		applet.fill(0, 0);
		applet.rectMode(CENTER);
		Button from = buttons[activeButton];
		float offset = 0;
		if (buttonDistance != 0) {
			slideElapsed += Time.deltaMillis();
			final float travelled = (float) Math.pow(SLIDE_RATE * slideElapsed / 1000, SLIDE_EXPONENT);
			if (travelled < Math.abs(buttonDistance)) {
				from = buttons[prevButton];
				offset = -Math.signum(buttonDistance) * travelled;
			} else {
				buttonDistance = 0; // arrived
			}
		}
		applet.rect(from.getX() + offset, from.getY(), from.getW(), from.getH());
	}

	// Check hover state of each button
	public boolean hover() {
		for (int i = 0; i < tabCount; i++) {
			if (buttons[i].hover()) {
				return true;
			}
		}
		return false;
	}

	// Return a single button: Buttons and their names are in increasing order, so
	// {"load", "save"} would result to load having index 0 and save having index 1
	public Button getButton(int index) {
		return (buttons[index]);
	}

	// Set a button as blocked: only call this if blocking non-active buttons
	public void setBlockedButton(int index, boolean block) {
		buttons[index].setBlocked(block);
	}

	// Set the button on which the window is currently on
	public void setActiveButton(int index) {
		activeButton = index;
		setBlockedButton(activeButton, true);
	}

	// Set previous active button
	public void setPrevButton(int index) {
		prevButton = index;
		setBlockedButton(prevButton, false);
	}

	// Get current active button
	public int getActiveButton() {
		return activeButton;
	}

	// Get previous active button
	public int getPrevButton() {
		return prevButton;
	}
}
