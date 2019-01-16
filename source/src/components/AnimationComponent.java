package components;

import java.util.ArrayList;

import dm.core.DM;
import processing.core.PApplet;
import processing.core.PGraphics;

public class AnimationComponent {
	public ArrayList<PGraphics> frames;
	
	public float frame;
	public int length;
	
	public boolean loop;
	
	public int sx;
	public int sy;
	public int swidth;
	public int sheight;
	public int cellCount;
	
	public int rate;
	public int start;
	
	public int count;
	
	public String name;
	public String pName;
	
	public boolean ended;
	
	public PGraphics image;
	
	public PGraphics animate(int frameCount, float dt) {
		try {
			image = frames.get((int)frame);
		} catch(Exception e) {
			//PApplet.println("E : " + frame);
		}
		
		if((int)frame >= length) {
			if(loop) {
				frame = start;
			}
			else {
				frame = length;
				ended = true;
			}
		}
		else {
			if(frameCount % rate == 0) {
				frame += dt;
			}
		}
		return image;
	}
	
	public void extendAnimation(int newLength) {
		length=newLength;
	}
	
	public int remainingFrames() {
		return (int)(length-frame);
	}
}
