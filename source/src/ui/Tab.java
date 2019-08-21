package ui;

import sidescroller.PClass;
import sidescroller.SideScroller;

public class Tab extends PClass{
	private int tabCount;
	private Button[] buttons;
	
	//Basic constructor for tab
	public Tab(SideScroller a) {
		super(a);
		tabCount = 2;
	}
	
	//Update all buttons the tab contains
	public void update() {
		for(int i = 0; i < tabCount; i++) {
			buttons[i].update();
		}
	}
	
	//Display all buttons the tab contains
	public void display() {
		for(int i = 0; i < tabCount; i++) {
			buttons[i].display();
		}
	}
	
	//Create the buttons for the tab
	public void setTab(int tabs, SideScroller a, String[] texts) {
		tabCount = tabs;
		buttons = new Button[tabCount];
		for(int i = 0; i < tabCount; i++) {
			buttons[i] = new Button(a);
			buttons[i].setText(texts[i]);
			//Created buttons are placed to the right of the last button with (hopefully) correct spacing
			buttons[i].setPosition(((applet.width / 2) + (100 * i)) - 50, (applet.height / 2) - 250);
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
	
	//Set a button as blocked
	public void setBlockedButton(int index, boolean block) {
		buttons[index].setBlocked(block);
	}
}
