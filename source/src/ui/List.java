package ui;

import processing.core.PApplet;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class List extends PClass {

	final int COVER_H = 30;
	int x, y;
	int w, h;
	int scrollPass;
	String getElement;
	Button confirm;
	Button cancel;
	Button elements[];
	ScrollBarVertical scrollBar;
	int elementOffSet;

	public List(SideScroller a, String ar[], int elementOffset) {
		super();
		x = 0;
		y = 0;
		w = 0;
		h = 0;
		getElement = "";
		elements = new Button[ar.length];
		initalizeElements();
		refreshElements(ar);
		this.elementOffSet = elementOffset;
	}

	// If new elements are present can use it to recreate them
	private void initalizeElements() {
		for (int i = 0; i < elements.length; i++)
			elements[i] = new Button();
	}

	public void refreshElements(String ar[]) {
		int getLargestW = 0;
		for (int i = 0; i < ar.length; i++) {
			elements[i].setText(ar[i]);
			elements[i].setColorsNotPress(applet.color(0), applet.color(0));
			elements[i].setTextColorNotPressed(applet.color(255));
			elements[i].setTextColorPressed(applet.color(0));
			elements[i].setColorsPress(applet.color(0), applet.color(255));
			elements[i].intW();
			getLargestW = Math.max(getLargestW, elements[i].getW());
		}
		setSizeW(getLargestW / 2);
	}

	public void display() {
		displayListArea();
		displayElements();
		confirm.display();
		cancel.display();
		setCover();
		applet.pushMatrix();
		scrollBar.display();
		scrollBar.update();
		applet.popMatrix();
		scrollPass = (int) PApplet.map(scrollBar.barLocation, 1, 0, elements.length*30, 0);
	}

	private void displayListArea() {
		applet.noStroke();
		applet.fill(0);
		applet.rect(x, y, w, h);
	}

	private void loadScrollBar() {
		Anchor scrollBarAnchor = new Anchor(applet, x+w/2, y-h/2-COVER_H/2, 20, h+COVER_H);
		scrollBar = new ScrollBarVertical(scrollBarAnchor);
		scrollBar.setBarRatio(0.5f);
	}

	public void update() {
		confirm.update();
		cancel.update();
		for (int i = 0; i < elements.length; i++) {
			elements[i].updateOnPress();
			// requires the !getConfirm as there is an event glitch when confiriming your choice
			//within the list
			if (elements[i].event() && !getConfirmPress()) {
				getElement = elements[i].getText();
				System.out.println(getElement);
			}
		}
	}

	public void displayElements() {
		for (int i = 0; i < elements.length; i++) {
			elements[i].setPosition((x - w / 2 ) + (elements[i].getW() / 2),
					(y - h / 2 + 30) + (i * elementOffSet) - scrollPass);
			if (inListBox(elements[i].getY() + elements[i].getH() / 2, elements[i].getY() - elements[i].getH() / 2))
				elements[i].display();
		}
	}

	public void setConfirmButton(String text, int x, int y) {
		confirm = new Button();
		confirm.setText(text);
		confirm.setPosition(x, y);
	}

	public void setCancelButton(String text, int x, int y) {
		cancel = new Button();
		cancel.setText(text);
		cancel.setPosition(x, y);
	}

	// For covering the disappereance of elements so the list looks more smooth...
	public void setCover() {
		applet.fill(89, 89, 89);
		applet.noStroke();
		applet.rect(x, y + h / 2, w, COVER_H);// Hardcoded as height of buttons doesn't change only width
		applet.rect(x, y - h / 2, w, COVER_H);
	}

	public boolean inListBox(int elementPosY, int elementPosY2) {
		return elementPosY <= y + h / 2 && elementPosY2 >= y - h / 2;
	}

	// Always use after the constructor
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		loadScrollBar();
	}

	// Always use after the constructor
	public void setSizeW(int w) {
		this.w = w;
	}

	public void setSizeH(int h) {
		this.h = h;
	}

	public boolean getConfirmPress() {
		return confirm.event();
	}

	public boolean getCancelPress() {
		return cancel.event();
	}

	public String getElement() {
		return getElement;
	}

	public void resetElement() {
		getElement = "";
	}
}
