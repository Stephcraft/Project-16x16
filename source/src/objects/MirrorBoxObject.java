package objects;

import processing.core.PApplet;
import projectiles.MagicProjectile;
import projectiles.ProjectileObject;
import projectiles.Swing;
import sidescroller.SideScroller;

public class MirrorBoxObject extends GameObject {
	
	public int direction;
	public boolean rotating;
	
	public boolean activated;
	
	private int lastProjectileUniqueID;
	
	public MirrorBoxObject(SideScroller a) {
		super(a);
		
		direction = 0;
		
		type = "OBJECT";
		id = "MIRROR_BOX";
		
		//Default image
		image = applet.gameGraphics.get("MIRROR_BOX");
		
		width = 64;
		height = 64;
		
		collision = new Collision(applet, 64, 64, 0, 0, true);
		collision.flag = "TRANSPARENT_BULLET";
		applet.collisions.add( collision );
	}
	
	public void display() {
				
		switch(direction) {
			case 0:
				applet.image(image, pos.x-applet.originX, pos.y-applet.originY);
				break;
			case 1:
				applet.pushMatrix();
					applet.translate(pos.x-applet.originX, pos.y-applet.originY);
					applet.rotate(PApplet.radians(90));
					applet.image(image, 0,0);
				applet.popMatrix();
				break;
			case 2:
				applet.pushMatrix();
					applet.translate(pos.x-applet.originX, pos.y-applet.originY);
					applet.scale(-1,-1);
					applet.image(image, 0,0);
				applet.popMatrix();
				break;
			case 3:
				applet.pushMatrix();
					applet.translate(pos.x-applet.originX, pos.y-applet.originY);
					applet.rotate(PApplet.radians(270));
					applet.image(image, 0,0);
				applet.popMatrix();
				break;
		}
	}
	
	public void update() {
		if(rotating) {
			image = animation.animate(applet.frameCount, applet.deltaTime);
		}
		
		collision.pos = pos;
		
		// Change Mirror Box Axis
		changeAxis();
		
		// Reflect incoming projectiles 
		reflectProjectile();
		
		if(animation.ended && rotating) {
			image = applet.gameGraphics.get("MIRROR_BOX");
			rotating = false;
			direction += 1;
			if(direction >= 4) { direction = 0; }
		}
	}
	
	private void changeAxis() {
		for(int i=0; i<applet.player.swings.size(); i++) {
			
			Swing swing = applet.player.swings.get(i);
			
			if(collidesWithSwing(swing)) {
				if(!swing.activated) {
					
					rotating = true;
					
					//Setup Animation
					animation.frames = applet.gameGraphics.getAnimation("MIRROR_BOX::ROTATE");
					animation.loop = false;
					animation.length = 7;
					animation.rate = 1;
					animation.frame = 0;
					animation.start = 0;
					animation.ended = false;
					
					
					swing.activated = true;
				}
			}
		}
	}
	
	private void reflectProjectile() {
		
		// for each projectile in the scene
		for(int i=0; i<applet.projectileObjects.size(); i++) {
			
			ProjectileObject projectile = applet.projectileObjects.get(i);
			
			activated = false;  
			
			// check if the projectile is of type magic
			if(projectile.id.equals("MAGIC")) {
				
				if(collidesWithProjectile(projectile) && !projectile.hit) { 
					
					// get the unique ID of the projectile that collided with the mirror box.
					// this will be helpful in making sure the projectile doesn't get stuck
					// on the way out of the box.
					int projectileUniqueID = projectile.getUniqueID();
					
					// determine whether this is a new collision. if so reflect the projectile as normal.
					// otherwise, do nothing and allow the projectile to continue on its merry way.
					if (lastProjectileUniqueID != projectileUniqueID) {
						
						// update the lastProjectileUniqueID with ID of the one we just encountered
						lastProjectileUniqueID = projectileUniqueID;

						activated = true;
						
						// if this newDirection variable is changed from zero, the projectile has successfully collided
						// with the mirror box, and should be reflected in a certain direction. if it remains
						// at zero then the projectile should collide with the side of the mirror box.
						int newDirection = 0;
						
						// TEMPORARY FIX: these displacement variables adjusts the position of the projectile so that 
						// their collider doesn't overlap with that of an adjacent object, e.g. ground tile. a better
						// fix could be to change the size of the collider for the projectile, as i'm pretty sure
						// we don't need to cover the projectile's trail, only the front.

						
						int xDisplacement = 0; 
						int yDisplacement = 0;
						
						switch(projectile.direction) {
						case RIGHT:
							if (direction == 2) {
								newDirection = DOWN;  
								xDisplacement = -12; 
								yDisplacement = 3;
							}
							else if (direction == 3) {
								newDirection = UP;
								xDisplacement = -12;
								yDisplacement = -3;
							}
							break;
						case LEFT:
							if (direction == 0) {
								newDirection = UP;  
								xDisplacement = 12;
								yDisplacement = -3;
							}
							else if (direction == 1) {
								newDirection = DOWN;
								xDisplacement = 12;
								yDisplacement = 3;
							}
							break;
						case UP:
							if (direction == 1) {
								newDirection = RIGHT;  
								xDisplacement = 12;
								yDisplacement = -3;
							}
							else if (direction == 2) {
								newDirection = LEFT;
								xDisplacement = -12;
								yDisplacement = -3;
							}
							break;
						case DOWN:
							if (direction == 3) {
								newDirection = LEFT;  
								xDisplacement = -12;
								yDisplacement = 3;
							}
							else if (direction == 0) {
								newDirection = RIGHT;
								xDisplacement = 12;
								yDisplacement = 3;
							}
							break;
						}
						// a new direction has not been assigned, must be a hit
						if (newDirection == 0) {
							((MagicProjectile) projectile).hit(collision);
						}
						else {
							// set the position of the projectile to the centre of the mirror box so the projectile
							// doesn't change direction too early + the temporary displacement amount to make sure
							// projectiles don't get stuck to adjacent game objects
							projectile.pos.x = pos.x + xDisplacement;
							projectile.pos.y = pos.y + yDisplacement;
							
							// change the direction to the appropriate new direction
							projectile.direction = newDirection;
						}
					} 
				}
			}
		}

		// TO DO: move this outside of reflectProjectile() - not sure exactly what it does.  
		if(!rotating) {
			if(activated) {
				image = applet.gameGraphics.g(352, 160, 16, 16);
			}
			else {
				image = applet.gameGraphics.get("MIRROR_BOX");
			}
		}
	}
			

	public boolean collidesWithSwing(Swing swing) {
		return (swing.pos.x-applet.originX+swing.width/2 > pos.x-applet.originX-width/2 && swing.pos.x-applet.originX-swing.width/2 < pos.x-applet.originX+width/2) &&
			   (swing.pos.y-applet.originY+swing.height/2 > pos.y-applet.originY-height/2 && swing.pos.y-applet.originY-swing.height/2 < pos.y-applet.originY+height/2);
	}
	
	public boolean collidesWithProjectile(ProjectileObject swing) {
		return (swing.pos.x-applet.originX+swing.width/2 > pos.x-applet.originX-width/2 && swing.pos.x-applet.originX-swing.width/2 < pos.x-applet.originX+width/2) &&
			   (swing.pos.y-applet.originY+swing.height/2 > pos.y-applet.originY-height/2 && swing.pos.y-applet.originY-swing.height/2 < pos.y-applet.originY+height/2);
	}
}
