package scene;

import processing.event.MouseEvent;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class PScene extends PClass {
	
	public static String name;
	
	public PScene(SideScroller a) {
		super(a);
		
		name = "";
	}
	
	public void setup() {}
	public void draw() {}
	
	public void mouseWheel(MouseEvent event) {}
	public void mouseMoved() {}
}
