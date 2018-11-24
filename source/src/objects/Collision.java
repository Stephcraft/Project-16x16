package objects;

import processing.core.*;
import sidescroller.SideScroller;

public class Collision extends EditableObject {
	
	public PImage image;
	
	public PGraphics editArrowX;
	public PGraphics editArrowY;
	public PGraphics editArrowXActive;
	public PGraphics editArrowYActive;
	
	public String flag;
	
	//int editOffsetX;
	//int editOffsetY;
	
	public float pixelOffsetX = 0;
	public float pixelOffsetY = 0;
	
	public Collision(SideScroller a) {
		super(a);
		
		flag = "";
		type = "COLLISION";
		pos = new PVector(0,0);
	}
	
	public Collision(SideScroller a, int w, int h, int x, int y, boolean ch) {
		this(a, w, h, x, y);
		
		child = ch;
	}
	
	public Collision(SideScroller a, int w, int h, int x, int y) {
		this(a);
		
		//Get From Game Graphics Image
		pos = new PVector(round(x/4)*4,round(y/4)*4);
		width = w;
		height = h;
	}
	
	public Collision(SideScroller a, String t, int x, int y) {
		this(a);
		
		//Get From Game Graphics Image
		setGraphic(t);
		pos = new PVector(round(x/4)*4,round(y/4)*4);
	}
	
	public void display() {
		
		if(height/4 % 2 != 0) {
			pixelOffsetY = 2;
		}
		if(width/4 % 2 != 0) {
			pixelOffsetX = 2;
		}
		
		if(id == null) {
			if(applet.debug) {
				applet.noFill();
				applet.strokeWeight(1);
				applet.stroke(0,255,200);
				applet.rect(pos.x+pixelOffsetX-applet.originX, pos.y+pixelOffsetY-applet.originY, width, height);
			}
		}
		else {
			applet.image(image, pos.x+pixelOffsetX-applet.originX, pos.y+pixelOffsetY-applet.originY);
		}
	}
	
	public void setGraphic(String _id) {
		image = applet.gameGraphics.get(_id);
		id = _id;
		width = image.width;
		height = image.height;
	}
	
	/*
	public boolean collides(Player player) {
		return (player.pos.x+player.width/2 > pos.x-width/2 && player.pos.x-player.width/2 < pos.x+width/2) &&
			   (player.pos.y+player.height/2 > pos.y-height/2 && player.pos.y-player.height/2 < pos.y+height/2);
	}
	*/
	
	//	TODO remove it ?
	/*
	private static Collision duplicate(final Collision c) {
        return new Collision(c.applet, c.type, (int)c.pos.x, (int)c.pos.y);
    }
	*/
}
