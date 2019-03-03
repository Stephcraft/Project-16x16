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
		
		//Change Mirror Box Axis
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
		
		//Reflect Magic Projectile
		for(int i=0; i<applet.projectileObjects.size(); i++) {
			ProjectileObject projectile = applet.projectileObjects.get(i);
			
			activated = false;
			if(projectile.id.equals("MAGIC")) {
				if(collidesWithProjectile(projectile) && !projectile.hit) {
					activated = true;
					
					switch(projectile.direction) {
						case RIGHT:
							
							if(direction == 0 || direction == 1) {
									((MagicProjectile) projectile).hit(collision);
							}
							else if(direction == 2) {
								projectile.direction = DOWN;
								projectile.pos.x = pos.x;
							}
							else if(direction == 3) {
								projectile.direction = UP;
								projectile.pos.x = pos.x;
							}
							break;
					}
					
					
				}
			}
			if(!rotating) {
				if(activated) {
					image = applet.gameGraphics.g(352, 160, 16, 16);
				}
				else {
					image = applet.gameGraphics.get("MIRROR_BOX");
				}
			}
		}
		
		if(animation.ended && rotating) {
			image = applet.gameGraphics.get("MIRROR_BOX");
			rotating = false;
			direction += 1;
			if(direction >= 4) { direction = 0; }
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
