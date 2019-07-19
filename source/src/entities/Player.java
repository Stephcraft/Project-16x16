package entities;

import processing.core.*;
import projectiles.Swing;
import sidescroller.Options;
//import sidescroller.PClass;
import sidescroller.SideScroller;

import java.util.ArrayList;

import components.AnimationComponent;
import dm.core.DM;
import objects.Collision;
import objects.EditableObject;

public class Player extends EditableObject {
	//public PVector pos;
	public float px;
	public float py;
	//public int width;
	//public int height;
	
	public PGraphics image;
	
	PGraphics lifeOn;
	PGraphics lifeOff;
	
	public float gravity;
	
	public int speedX;
	public int speedY;
	public int pSpeedX;
	public int pSpeedY;
	
	public int speedWalk;
	public int speedJump;
	
	public int direction;
	
	public int life;
	public int lifeCapacity;
	
	public boolean flying;
	public boolean pflying;
	
	public boolean attack;
	
	//Player Projectile
	public ArrayList<Swing> swings;
	
	//Animation Component
	public AnimationComponent animation;
	
	//Animation frames
	public ArrayList<PGraphics> anim_squish;
	public ArrayList<PGraphics> anim_idle;
	public ArrayList<PGraphics> anim_walk;
	public ArrayList<PGraphics> anim_attack;
	public ArrayList<PGraphics> anim_shoot;
	
	public Player(SideScroller a) {
		super(a);
		
		pos = new PVector(100,300);
		gravity = 2;

		animation = new AnimationComponent();
		swings = new ArrayList<Swing>();
		
		anim_squish = new ArrayList<PGraphics>();
		anim_idle = new ArrayList<PGraphics>();
		anim_walk = new ArrayList<PGraphics>();
		anim_attack = applet.gameGraphics.getAnimation("PLAYER::WALK"); 
		anim_shoot = new ArrayList<PGraphics>();
		
		animation.length = 7;
		animation.loop = true;
		animation.rate = 6;
		
		//Set life
		lifeCapacity = 3;
		life = lifeCapacity;
		
		speedWalk = 10;
		speedJump = 25; // 20
		
		width = 14*4;
		height = 16*4;
		
		flying = true;
	}
	
	public void load(PImage sheet) {
		image = util.pg(sheet.get(0, 258, 14, 14),4);
		
		lifeOn = util.pg(sheet.get(144, 256, 9, 9),4);
		lifeOff = util.pg(sheet.get(160, 256, 9, 9),4);
		
		anim_walk.add( util.pg(sheet.get(0, 272, 14, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*1+2, 272, 14, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*2+4, 272, 14, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*3+6, 272, 14, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*4+8, 272, 15, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*5+10, 272, 15, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*6+12, 272, 15, 15),4) );
		anim_walk.add( util.pg(sheet.get(14*7+14, 272, 14, 15),4) );
		
		anim_squish.add( util.pg(sheet.get(0, 258, 14, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*1+2, 258, 14, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*2+4, 258, 14, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*3+6, 258, 14, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*4+8, 258, 15, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*5+10, 258, 15, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*6+12, 258, 15, 14),4) );
		anim_squish.add( util.pg(sheet.get(14*7+14, 258, 14, 14),4) );
		
		//anim_attack.add( util.pg() );
		
		animation.frames = anim_walk;
		
		setAnimation("IDLE");
	}
	
	public void display() {
		
		//Display Swing Projectiles
		for(int i=0; i<swings.size(); i++) {
			swings.get(i).display();
		}
		
		if(direction == LEFT) {
			applet.pushMatrix();
				applet.translate(pos.x-applet.originX, pos.y-applet.originY);
				applet.scale(-1,1);
				applet.image(image, 0, 0);
			applet.popMatrix();
		}
		else {
			applet.image(image, pos.x-applet.originX, pos.y-applet.originY);
		}
		
		if(applet.debug) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(pos.x, pos.y, width, height);
		}
	}
	
	public void update() {
		image = animation.animate(applet.frameCount, applet.deltaTime);
		
		//speedY *= DM.deltaTime;
		speedY += gravity * applet.deltaTime;
		//speedY += 100 * DM.deltaTimeRaw;
		
		//PApplet.println( "Delta Time : " + DM.deltaTime );
		//PApplet.println( "Var : " + gravity );
		//PApplet.println( "Sum : " + (gravity * DM.deltaTime) );
		//PApplet.println( " ------------------------------ " );
		
		//Save Previous State
		pSpeedX = speedX;
		pSpeedY = speedY;
		
		px = pos.x;
		py = pos.y;
		
		pflying = flying;
		
		//Move on the x axis
		if(applet.keyPress(Options.moveRightKey)) {
			if(applet.keyPressEvent && !attack) {
				setAnimation("WALK");
			}
			speedX = (int)(speedWalk * applet.deltaTime);
			direction = RIGHT;
		}
		else if(applet.keyPress(Options.moveLeftKey)) {
			if(applet.keyPressEvent && !attack) {
				setAnimation("WALK");
			}
			speedX = (int)(-speedWalk * applet.deltaTime);
			direction = LEFT;
		}
		else {
			speedX = 0;
		}
		
		//Idle Animation
		if(speedX == 0 && applet.keyReleaseEvent && !attack) {
			setAnimation("IDLE");
		}
		
		//Move on the y axis
		if(applet.keyPress(Options.jumpKey) && applet.keyPressEvent && !flying) { //&& speedY == 0 && !flying
			speedY -= (int)(speedJump * applet.deltaTime);
			flying = true;
		}
		
		//Attack
		if(applet.key == ' ' && applet.keyPressEvent && !attack) {
			attack = true;
			setAnimation("ATTACK");
			
			//Create Swing Projectile
			swings.add( new Swing(applet, (int)pos.x, (int)pos.y, direction) );
		}
		
		//End Attack
		if(animation.name == "ATTACK" && animation.ended) {
			attack = false;
			setAnimation("IDLE");
		}
		
		//float fx = pos.x + speedX;
		//float fy = pos.y + speedY;
		
		//boolean collides = false;
		
		if(applet.debug) {
			applet.noFill();
			applet.stroke(255,0,0);
			applet.strokeWeight(1);
			applet.ellipse(pos.x-applet.originX, pos.y-applet.originY, 400, 400);
		}
		
		//All Collision Global Check
		for(int i=0; i<applet.collisions.size(); i++) {
			Collision collision = applet.collisions.get(i);
			
			//In Player Range
			if(PApplet.dist(pos.x, pos.y, collision.pos.x, collision.pos.y) < 200) {
			
				if(applet.debug) {
					applet.rect(collision.pos.x-applet.originX, collision.pos.y-applet.originY, 20, 20);
				}
				if(collides(collision)) {
					if(px-applet.originX+width/2 < collision.pos.x+collision.width/2-applet.originX) {
						pos.x = collision.pos.x-collision.width/2 - width/2;
					}
					else if(px-applet.originX-width/2 > collision.pos.x-collision.width/2-applet.originX) { //+collision.width/2
						pos.x = collision.pos.x+collision.width/2 + width/2;
					}
					
				} 
				if(collidesFuturX(collision)) {
					if(px-applet.originX < collision.pos.x-applet.originX) {
						//pos.x = collision.pos.x-collision.width/2 - width/2;
						speedX = 0;
					}
					else if(px-applet.originX > collision.pos.x-applet.originX) {
						//pos.x = collision.pos.x+collision.width/2 + width/2;
						speedX = 0;
					}
				}
				if(collidesFuturY(collision)) {
					if(py-applet.originY+height/2 < collision.pos.y-applet.originY) {
						pos.y = collision.pos.y-collision.height/2 - height/2;
						speedY = 0;
						flying = false;
					}
					else if(pos.y-applet.originY > collision.pos.y-applet.originY) {
						pos.y = collision.pos.y+collision.height/2 + height/2;
						speedY = 0;
					}
				}
			}
		}
		
		if(flying && !attack) {
			setAnimation("FALL");
		}
		
		//On Ground Event
		if(!flying && pflying && !attack) {
			setAnimation("SQUISH");
		}
		
		if(animation.name == "SQUISH" && speedX != 0 && !attack) { //animation.ended  (applet.keyPress(LEFT) || applet.keyPress(RIGHT)
			setAnimation("WALK");
		}
		
		//speedX *= DM.deltaTime;
		//speedY *= DM.deltaTime;
		
		//Apply transformation
		pos.x += speedX;
		pos.y += speedY;
		
		//Apply World Transformation
		if(pos.x-applet.originX-width/2 < applet.width/2-applet.screenX/2) {
			applet.originTargetX -= PApplet.abs(speedX);
		}
		else if(pos.x-applet.originX+width/2 > applet.width/2+applet.screenX/2) {
			applet.originTargetX += PApplet.abs(speedX);
		}
		if(pos.y-applet.originY-height/2 < applet.height/2-applet.screenY/2) {
			applet.originTargetY -= PApplet.abs(speedY);
		}
		else if(pos.y-applet.originY+height/2 > applet.height/2+applet.screenY/2) {
			applet.originTargetY += PApplet.abs(speedY);
		}
		
		//Apply gravity
		//speedY += gravity * DM.deltaTime;
		
		//Update Swing Projectiles
		for(int i=0; i<swings.size(); i++) {
			swings.get(i).update();
		}
	}
	
	public void displayLife() {
		for(int i=0; i<lifeCapacity; i++) {
			if(i <= life) {
				applet.image(lifeOn, 30 + i*50, 30);
			}
			else {
				applet.image(lifeOff, 30 + i*50, 30);
			}
		}
	}
	
	public boolean collides(Collision collision) {
		return (pos.x-applet.originX+width/2 > collision.pos.x-applet.originX-collision.width/2 && pos.x-applet.originX-width/2 < collision.pos.x-applet.originX+collision.width/2) &&
			   (pos.y-applet.originY+height/2 > collision.pos.y-applet.originY-collision.height/2 && pos.y-applet.originY-height/2 < collision.pos.y-applet.originY+collision.height/2);
	}
	
	public boolean collidesEqual(Collision collision) {
		return (pos.x-applet.originX+width/2 >= collision.pos.x-applet.originX-collision.width/2 && pos.x-applet.originX-width/2 <= collision.pos.x-applet.originX+collision.width/2) &&
			   (pos.y-applet.originY+height/2 >= collision.pos.y-applet.originY-collision.height/2 && pos.y-applet.originY-height/2 <= collision.pos.y-applet.originY+collision.height/2);
	}
	
	public boolean collidesFutur(Collision collision) {
		return (pos.x-applet.originX+speedX+width/2 > collision.pos.x-applet.originX-collision.width/2 && pos.x-applet.originX+speedX-width/2 < collision.pos.x-applet.originX+collision.width/2) &&
			   (pos.y-applet.originY+speedY+height/2 > collision.pos.y-applet.originY-collision.height/2 && pos.y-applet.originY+speedY-height/2 < collision.pos.y-applet.originY+collision.height/2);
	}
	
	public boolean collidesFuturX(Collision collision) {
		return (pos.x-applet.originX+speedX+width/2 > collision.pos.x-applet.originX-collision.width/2 && pos.x-applet.originX+speedX-width/2 < collision.pos.x-applet.originX+collision.width/2) &&
			   (pos.y-applet.originY+0+height/2 > collision.pos.y-applet.originY-collision.height/2 && pos.y-applet.originY+0-height/2 < collision.pos.y-applet.originY+collision.height/2);
	}
	
	public boolean collidesFuturY(Collision collision) {
		return (pos.x-applet.originX+0+width/2 > collision.pos.x-applet.originX-collision.width/2 && pos.x-applet.originX+0-width/2 < collision.pos.x-applet.originX+collision.width/2) &&
			   (pos.y-applet.originY+speedY+height/2 > collision.pos.y-applet.originY-collision.height/2 && pos.y-applet.originY+speedY-height/2 < collision.pos.y-applet.originY+collision.height/2);
	}
	
	public void setAnimation(String anim) {
		switch(anim) {
			case "WALK":
				animation.frames = getAnimation("PLAYER::WALK"); //anim_walk;
				animation.loop = true;
				animation.length = 7;
				animation.rate = 6; //6
				animation.frame = 0;
				animation.start = 0;
				break;
			case "IDLE":
				animation.frames = getAnimation("PLAYER::IDLE"); //anim_squish;
				animation.loop = true;
				animation.length = 3;
				animation.rate = 20;
				animation.frame = 0;
				animation.start = 0;
				break;
			case "SQUISH":
				animation.frames = getAnimation("PLAYER::SQUISH"); //anim_squish;
				animation.loop = false;
				animation.length = 5; //7
				animation.rate = 4;
				animation.frame = 0; //3
				animation.start = 0; //3
				break;
			case "FALL":
				animation.frames = anim_squish;
				animation.loop = false;
				animation.length = 0;
				animation.rate = 6;
				animation.frame = 0;
				animation.start = 0;
				break;
			case "ATTACK":
				animation.frames = getAnimation("PLAYER::ATTACK");
				animation.loop = false;
				animation.length = 3;
				animation.rate = 4;
				animation.frame = 0;
				animation.start = 0;
				break;
		}
		animation.ended = false;
		animation.pName = animation.name;
		animation.name = anim;
	}
	
	private ArrayList<PGraphics> getAnimation(String id) {
		return applet.gameGraphics.getAnimation(id);
	}
}



/*
 
 if(py-applet.originY+height/2 < collision.pos.y-applet.originY) {
						//if(!(speedY < 0 && flying)) {
							//pos.y = collision.pos.y-collision.height/2 - height/2;
						//}
						//speedY = 0;
						//flying = false;
					}
					else if(py-applet.originY > collision.pos.y-applet.originY) {
						//pos.y = collision.pos.y+collision.height/2 + height/2;
						
					}
					//else if(pos.y-applet.originY-height/2 < collision.pos.y-applet.originY) {
						//pos.y = collision.pos.y+collision.height/2 + height/2;
					//}
					//if(pos.y-applet.originY-height/2 > collision.pos.y-applet.originY) {
						//pos.y = collision.pos.y+collision.height/2 + height/2;
						
					//}
 
 else if(py-applet.originY+height/2 < collision.pos.y-applet.originY) {
					//pos.y = collision.pos.y-collision.height/2 - height/2;
					//speedX = 0;
					//flying = false;
					//speedY = 0;
				}
				
				else if(py-applet.originY > collision.pos.y-applet.originY) {
					pos.y = collision.pos.y+collision.height/2 + height/2;
				}
				
				else if(pos.y-applet.originY > collision.pos.y-applet.originY) {
					//pos.y = collision.pos.y+collision.height/2 + height/2;
					
				}
				
				//if(!collidesFutur(collision)) {
				//	flying = true;
				//}
 
 */
