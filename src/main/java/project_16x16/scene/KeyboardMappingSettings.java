/**
 * 
 */
package project_16x16.scene;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javafx.scene.text.Font;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Constants;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.Slider;


/**
 * @author two-dimensional-squares
 * credit to https://www.youtube.com/watch?v=Kmgo00avvEw&ab_channel=BroCode
 * for wonderful Java GUI tutorial
 *
 */
public class KeyboardMappingSettings extends JFrame implements ActionListener {
	JButton close_button; //click to exit the keyboard mapping setting
	
	
	KeyboardMappingSettings(){ //constructor
		//everything related to the close button that closes the keyboard setting window
		close_button = new JButton();
		close_button.setBounds(800, 900, 100, 50); //appear in lower right cornoer
		close_button.addActionListener(this);
		close_button.setText("close");
		close_button.setHorizontalAlignment(JButton.RIGHT);
		close_button.setVerticalAlignment(JButton.BOTTOM);
		close_button.setForeground(Color.red);
		close_button.setFocusable(false);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setSize(1000, 800);
		this.setVisible(true);
		this.add(close_button);
		
		//use GUI menubar to set two options for each operation
		JMenuBar menu = new JMenuBar();
		JMenu upMenu = new JMenu("Move Up");
		JMenu downMenu = new JMenu("Move Down");
		JMenu leftMenu = new JMenu("Move Left");
		JMenu rightMenu = new JMenu("Move Right");
		JMenu shootMenu = new JMenu("Shoot");
		
		menu.add(upMenu);
		menu.add(downMenu);
		menu.add(leftMenu);
		menu.add(rightMenu);
		menu.add(shootMenu);
		
		JMenuItem up1 = new JMenuItem("upArrow Key");
		JMenuItem up2 = new JMenuItem("W");
		upMenu.add(up1);
		upMenu.add(up2);
		
		JMenuItem down1 = new JMenuItem("downArrow Key");
		JMenuItem down2 = new JMenuItem("S");
		downMenu.add(down1);
		downMenu.add(down2);
		
		JMenuItem left1 = new JMenuItem("leftArrow Key");
		JMenuItem left2 = new JMenuItem("A");
		leftMenu.add(left1);
		leftMenu.add(left2);
		
		JMenuItem right1 = new JMenuItem("rightArrow Key");
		JMenuItem right2 = new JMenuItem("D");
		rightMenu.add(right1);
		rightMenu.add(right2);
		
		JMenuItem shoot1 = new JMenuItem("left Mouse click");
		JMenuItem shoot2 = new JMenuItem("Enter");
		shootMenu.add(shoot1);
		shootMenu.add(shoot2);
		
		this.setJMenuBar(menu);
		
		
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == close_button) {
			this.dispose();
		}
		
		
	}
	
}
