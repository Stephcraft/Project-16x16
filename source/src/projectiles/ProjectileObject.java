package projectiles;

import java.util.ArrayList;

import components.AnimationComponent;
import objects.Collision;
import processing.core.PGraphics;
import processing.core.PVector;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class ProjectileObject extends PClass {
	
	public AnimationComponent animation;
	
	public PVector pos;
	
	public PGraphics image;
	
	public int direction;
	
	public int width;
	public int height;
	
	public int speed;
	
	// Identification - what kind of projectile is this? e.g. "MAGIC"
	public String id;
	
	public boolean hit;
	
	// create a unique identifier for the projectile. this will help to make sure 
	// projectiles are reflected properly when they collide with mirror box.
	// 0 is reserved just in case.
	private static int instanceCounter = 1;
	private final int uniqueID;

	public ProjectileObject(SideScroller a) {
		super(a);
		
		id = "";
		
		animation = new AnimationComponent();
		pos = new PVector(0,0);
		
		// set the uniqueID as the current value of instanceCounter
		uniqueID = instanceCounter;
		// increment the static instance counter with every new instance of the object
		instanceCounter += 1;
	}
	
	public void display() {}
	public void update() {}
	
	public boolean collides(Collision collision) {
		return (pos.x-applet.originX+width/2 > collision.pos.x-applet.originX-collision.width/2 && pos.x-applet.originX-width/2 < collision.pos.x-applet.originX+collision.width/2) &&
			   (pos.y-applet.originY+height/2 > collision.pos.y-applet.originY-collision.height/2 && pos.y-applet.originY-height/2 < collision.pos.y-applet.originY+collision.height/2);
	}
	
	protected ArrayList<PGraphics> getAnimation(String id) {
		return applet.gameGraphics.getAnimation(id);
	}
	
	public int getUniqueID() {
		return uniqueID;
	}
}
