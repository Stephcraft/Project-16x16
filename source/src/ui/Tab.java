package ui;

import sidescroller.PClass;
import sidescroller.SideScroller;

public class Tab extends PClass{
	
	private int tabWidth = 400;
	private int tabCount;
	private Button[] buttons;
	private int activeButton = 1;
	private int prevButton = 1;
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
			if(i == 0) {
				buttons[i].setPosition((applet.width / 2) - 150, (applet.height / 2) - 275);
			} else {
				buttons[i].setPosition(buttons[i - 1].getXPos() + ((buttons[i - 1].getWidth() + buttons[i].getWidth()) / 2) + 5, (applet.height / 2) - 275);
			}
		}
	}
	
	//Update all buttons the tab contains
	public void update() {
		for(int j = 0; j < tabCount; j++) {
			if(j == 0) {
				buttons[j].setPosition((applet.width / 2) - 150, (applet.height / 2) - 275);
			} else {
				buttons[j].setPosition(buttons[j - 1].getXPos() + ((buttons[j - 1].getWidth() + buttons[j].getWidth()) / 2) + 5, (applet.height / 2) - 275);
			}
		}
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
	
	//Move active button to selected
	public void moveActive(int index){
		setPrevButton(activeButton);
		setActiveButton(index);
		buttonDistance = buttons[prevButton].getXPos() - buttons[activeButton].getXPos();
	}
	
	//Selection animation between buttons
	public void displayActive() {
		applet.strokeWeight(5);
		applet.stroke(200, 200, 200);
		applet.fill(0, 100);
		if(buttonDistance == movingIncrement) {
			applet.rectMode(CENTER);
			applet.rect(buttons[activeButton].getXPos(), buttons[activeButton].getYPos(), buttons[activeButton].getWidth(), buttons[activeButton].getHeight());
			movingIncrement = 0;
			buttonDistance = 0;
			setIncrementSpeed(1);
		} else if(buttonDistance >  movingIncrement) {
			applet.rectMode(CENTER);
			applet.rect(buttons[prevButton].getXPos() - movingIncrement, buttons[prevButton].getYPos(), buttons[prevButton].getWidth(), buttons[prevButton].getHeight());
			movingIncrement += incrementSpeed;
			setIncrementSpeed(movingIncrement);
			if(movingIncrement > buttonDistance) {
				movingIncrement = 0;
				buttonDistance = 0;
				setIncrementSpeed(1);
			}
		} else if(buttonDistance < movingIncrement){
			applet.rectMode(CENTER);
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
	
	//Set previous active button
	public void setPrevButton(int index) {
		prevButton = index;
		setBlockedButton(prevButton, false);
	}
	
	//Set button's moving speed
	public void setIncrementSpeed(int speed) {
		incrementSpeed = (int) Math.pow(speed, .62);
	}
	
	//Get current active button
	public int getActiveButton() {
		return activeButton;
	}
	
	//Get previous active button
	public int getPrevButton() {
		return prevButton;
	}
}
