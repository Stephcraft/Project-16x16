package project_16x16.scene;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

import com.jogamp.newt.event.InputEvent;

/**
 * @author two-dimensional-squares
 * credit to https://stackoverflow.com/questions/16411823/junit-tests-for-gui-in-java
 * for methods to test Java GUI
 * https://www.geeksforgeeks.org/robot-class-java-awt/
 *
 */
public class KeyBoardMappingTest implements ActionListener{
	KeyboardMappingSettings target = new KeyboardMappingSettings();
	public static void main(String[] args) throws IOException,
    AWTException, InterruptedException, Exception{
		
		Robot robot = new Robot();//we use a robot to test the GUI by simulating on the events and things
		robot.setAutoDelay(200);
		try{Thread.sleep(250);
		}catch(InterruptedException e){
			System.out.print(e.getMessage());
		}
		//unit test #1
		try {
			robot.mouseMove(100, 200); //location of the first menu option
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}catch(Exception e){//if not responding, throw an error
			System.out.print(e.getMessage()); //should clicked on the first menu option for moving up
			
		}
		//unit test #2
		try {
			robot.mouseMove(300, 400); //location of the 2nd menu option
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}catch(Exception e){
			System.out.print(e.getMessage()); //should clicked on the 2nd menu option for moving down
			
		}
		//unit test #3
		try {
					robot.mouseMove(100, 300); //location of the 3rd menu option
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}catch(Exception e){
					System.out.print(e.getMessage()); //should clicked on the 3rd menu option for moving down
					
				}
		//full integration test #1
				try {
					robot.mouseMove(300, 400);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseMove(300, 500);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseMove(900, 900); // the save button
					robot.mousePress(InputEvent.BUTTON1_MASK);
					
		}catch(Exception e){
					System.out.print(e.getMessage());
					
				}
			
				
				//unit test #4
		try {
					robot.mouseMove(300, 400); //location of the 4th menu option
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}catch(Exception e){
					System.out.print(e.getMessage()); //should clicked on the 4th menu option for moving down
					
				}
		//unit test #5
		try {
			robot.mouseMove(300, 400); //location of the 4th menu option
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
}catch(Exception e){
			System.out.print(e.getMessage()); //should clicked on the 4th menu option for moving down
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == target.close_button)
			System.out.println("Close window successfully!");
	}
	
	
	
	
}
