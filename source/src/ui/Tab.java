package ui;

import sidescroller.PClass;
import sidescroller.SideScroller;

public class Tab extends PClass{
	
	private int tabWidth = 0;
	private int tabCount;
	private Button[] buttons;
	private int activeButton;
	private int prevButton;
	private int buttonDistance = 0;
	private int movingIncrement = 0;
	private double incrementSpeed;
	
	//Basic constructor for tab
	public Tab(SideScroller a, String[] texts, int tabs) {
		super(a);
		tabCount = tabs;
		buttons = new Button[tabCount];
		incrementSpeed = 1;
		movingIncrement = 0;
		for(int i = 0; i < tabCount; i++) {
			buttons[i] = new Button(a);
			buttons[i].setText(texts[i]);
			tabWidth += (buttons[i].getWidth());
			buttons[i].setPosition((applet.width / 2) + (i * 100), (applet.height / 2) - 275);
		}
	}
	
	//Update all buttons the tab contains
	public void update() {
		for(int i = 0; i < tabCount; i++) {
			buttons[i].update();
		}
	}
	
	//Display all buttons the tab contains
	public void display() {
		applet.strokeWeight(5);
		applet.stroke(175, 175, 175);
		applet.fill(0, 100);
		applet.rectMode(CENTER);
		applet.rect((applet.width / 2), (applet.height / 2) - 275, tabWidth, 50);
		for(int i = 0; i < tabCount; i++) {
			buttons[i].display();
		}
		displayActive();
	}
	
	public void moveActive(int index){
		setPrevButton(activeButton);
		setActiveButton(index);
		buttonDistance = buttons[prevButton].getXPos() - buttons[activeButton].getXPos();
	}
	
	public void displayActive() {
		applet.strokeWeight(5);
		applet.stroke(200, 200, 200);
		applet.fill(0, 100);
		if(buttonDistance - movingIncrement == 0) {
			applet.rect(buttons[activeButton].getXPos(), buttons[activeButton].getYPos(), buttons[activeButton].getWidth(), buttons[activeButton].getHeight());
		} else if(buttonDistance - movingIncrement > 0) {
			applet.rect(buttons[prevButton].getXPos() - movingIncrement, buttons[prevButton].getYPos(), buttons[prevButton].getWidth(), buttons[prevButton].getHeight());
			movingIncrement += incrementSpeed;
			setIncrementSpeed(movingIncrement);
			if(movingIncrement > buttonDistance) {
				movingIncrement = 0;
				buttonDistance = 0;
				setIncrementSpeed(1);
			}
		} else if(-buttonDistance - movingIncrement > 0){
			applet.rect(buttons[prevButton].getXPos() + movingIncrement, buttons[prevButton].getYPos(), buttons[prevButton].getWidth(), buttons[prevButton].getHeight());
			movingIncrement += incrementSpeed;
			setIncrementSpeed(movingIncrement);
			if(movingIncrement > -buttonDistance) {
				movingIncrement = 0;
				buttonDistance = 0;
				setIncrementSpeed(1);
			}
		}
	}
	
	//Check hover state of each button
	public boolean hover() {
		for(int i = 0; i < tabCount; i++) {
			if(buttons[i].hover()) {
				return true;
			}
		}
		return false;
	}
	
	//Return a single button: Buttons and their names are in increasing order, so {"load", "save"} would result to load having index 0 and save having index 1
	public Button getButton(int index) {
		return(buttons[index]);
	}
	
	//Set a button as blocked: only call this if blocking non-active buttons
	public void setBlockedButton(int index, boolean block) {
		buttons[index].setBlocked(block);
	}
	
	//Set the button on which the window is currently on
	public void setActiveButton(int index) {
		activeButton = index;
		setBlockedButton(activeButton, true);
	}
	
	public void setPrevButton(int index) {
		prevButton = index;
		setBlockedButton(prevButton, false);
	}
	
	public void setIncrementSpeed(double speed) {
		incrementSpeed = Math.pow(speed, .62);
	}
}
