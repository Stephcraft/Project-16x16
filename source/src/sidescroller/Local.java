package sidescroller;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Local {
	
	public static PGraphics createEditorGrid(PApplet applet) {
		PGraphics pg = applet.createGraphics((int)(applet.width*1.5), (int)(applet.height*1.5));
		
		int w = (int)(applet.width*1.5);
		int h = (int)(applet.height*1.5);
		
		pg.beginDraw();
			pg.background(0);
			pg.noFill();
			pg.strokeWeight(1);
			pg.stroke(255);
			
			for(int x=0; x<w/2; x++) { //(16*4)
				for(int y=0; y<h/2; y++) {
					//applet.line(x*(4*16), 0, x*(4*16), h);
					//applet.line(0, y*(4*16), w, y*(4*16));
				}
			}
		pg.endDraw();
		
		return pg;
	}
}
