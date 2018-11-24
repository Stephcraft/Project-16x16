package projectiles;

import components.AnimationComponent;
import processing.core.PVector;
import sidescroller.SideScroller;

public class Swing extends ProjectileObject { //PClass
	
	private AnimationComponent animation;
	
	//public PVector pos;
	
	//public PGraphics image;
	//public int direction;
	
	//public int width;
	//public int height;
	
	public boolean activated;
	
	public Swing(SideScroller a, int x, int y, int dir) {
		super(a);
		animation = new AnimationComponent();
		
		direction = dir;
		
		switch(direction) {
			case LEFT:
				pos = new PVector(x-60,y);
				break;
			case RIGHT:
				pos = new PVector(x+60,y);
				break;
		}
		
		width = 28*4;
		height = 9*4;
		
		//Setup Animation
		animation.frames = applet.gameGraphics.ga(applet.graphicsSheet, 65,292, 28,9, 4);
		animation.loop = false;
		animation.length = 3;
		animation.rate = 4; // 6
		animation.frame = 0;
		animation.start = 0;
	}
	
	public void display() {
		try {
			switch(direction) {
				case LEFT:
					applet.pushMatrix();
						applet.translate(pos.x-applet.originX, pos.y-applet.originY);
						applet.scale(-1, 1);
						applet.image(image, 0, 0);
					applet.popMatrix();
				break;
				case RIGHT:
					applet.image(image, pos.x-applet.originX, pos.y-applet.originY);
					break;
			}
		} catch(Exception e) {}
	}
	
	public void update() {
		image = animation.animate(applet.frameCount, applet.deltaTime);
		
		if(animation.ended) {
			applet.player.swings.remove(this);
		}
	}
}
