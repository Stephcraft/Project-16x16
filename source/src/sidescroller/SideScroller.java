package sidescroller;

import processing.core.*; 
import processing.event.MouseEvent;
import projectiles.ProjectileObject;

import java.util.ArrayList;

import scene.PScene;
import scene.SceneMapEditor;

import entities.*;
import sidescroller.Util;
import sidescroller.Options;

import objects.BackgroundObject;
import objects.Collision;
import objects.GameObject;
import dm.core.*;

public class SideScroller extends PApplet {
	
	public boolean LOADED;
	public boolean FAILED;
	
	public boolean debug;
	
	public int floor;
	
	//World Origin Coordinates
	public int originX;
	public int originY;
	public int originTargetX;
	public int originTargetY;
	
	public int screenX;
	public int screenY;
	
	public int worldWidth;
	public int worldHeight;
	public PVector worldPosition;
	
	//Image Resources
	public PImage graphicsSheet;
	public PImage magicSheet;
	
	//Main Resource
	public GameGraphics gameGraphics;
	
	//Font Resources
	public PFont font_pixel;
	
	//Options
	public Options options;
	
	//Frame Rate
	public float deltaTime;
	
	//public String scene;
	
	//Scenes
	public PScene scene;
	//public SceneMapEditor sceneMapEditor;
	
	Util util = new Util(this);
	
	//Player
	public Player player;

	//World Objects
	public ArrayList<Collision> collisions;
	public ArrayList<BackgroundObject> backgroundObjects;
	public ArrayList<GameObject> gameObjects;
	public ArrayList<ProjectileObject> projectileObjects;
	//public ArrayList<EntitiesObject> entitiesObjects;
	
	//Events
	ArrayList<Integer> keys;
	public boolean keyPressEvent;
	public boolean keyReleaseEvent;
	public boolean mousePressEvent;
	public boolean mouseReleaseEvent;
	
	public void settings() {
		//fullScreen();
		size((int)(1280*1.0),(int)(720*1.0)); // *1.5 //Changed to 16:9
		noSmooth();
	}
	
	public void setup() {
		
		//Start Graphics
		background(0);
		/*
		fill(255);
		textAlign(CENTER,CENTER);
		textSize(50);
		text("Loading...", width/2, height/2);
		*/
		
		//Setup modes
		imageMode(CENTER);
		rectMode(CENTER);
		strokeCap(SQUARE);
		
		//Setup DM
		DM.setup(this);
		
		//Create Option Class
		options = new Options();
		
		//Default frameRates
		frameRate(60);
		
		deltaTime = 1;
		
		//Create ArrayList
		keys = new ArrayList<Integer>();
		collisions = new ArrayList<Collision>();
		backgroundObjects = new ArrayList<BackgroundObject>();
		gameObjects = new ArrayList<GameObject>();
		projectileObjects = new ArrayList<ProjectileObject>();
		
		//Create Game Graphics
		gameGraphics = new GameGraphics(this);
		
		//Debug Option
		debug = true;
		
		//Set Screen Size
		screenX = width-400;
		screenY = height-400;
		
		worldWidth = width*2;
		worldHeight = height;
		worldPosition = new PVector(0,0);
		
		//Create scene
		//sceneMapEditor = new SceneMapEditor(this);
		scene = new SceneMapEditor(this);
		
		//Main Load
		thread("load");
	}
	
	public void load() {
		
		//TEST
		//press = new Press(this);
		//press.setPosition(width/2, height/2);
		
		//Load Font
		font_pixel = loadFont("Assets/Font/font-pixel-48.vlw");
		
		//Apply Text Font
		textFont(font_pixel);
		
		//Load Graphics Sheet
		graphicsSheet = loadImage("Assets/Art/graphics-sheet.png");
		magicSheet = loadImage("Assets/Art/magic.png");
		
		//Create All Graphics
		gameGraphics.load();
		
		//Set Scene
		setScene("MAPEDITOR");
		
		//Create Player
		player = new Player(this);
		player.load(graphicsSheet);
		player.pos.x = 0;
		player.pos.y = -100;
		
		//Set Floor
		floor = 400;
		
		LOADED = true;
	}
	
	public float fc = 0;
	public float fc2 = 0;
	
	public void draw() {
		if(!LOADED) { return; }
		
//		if(keyPressEvent && keyPress(82)) { frameRate(120); }
		if(keyPressEvent && keyPress(81)) { frameRate(60); }
//		if(keyPressEvent && keyPress(87)) { frameRate(30); }
		if(keyPressEvent && keyPress(84)) { frameRate(2); }
		
		//if(!(PApplet.parseInt(fc + DM.deltaTime) > PApplet.parseInt(fc))) {
			//PApplet.println((fc + DM.deltaTime) - (fc)); //PApplet.parseInt
			//PApplet.println( "dt : " + DM.deltaTime );
		//}
		
		//PApplet.println( "dt : " + DM.deltaTime );
		//PApplet.println( "dtr : " + DM.deltaTimeRaw * 30 );
		
		//fc += DM.deltaTime;
		
		//PApplet.println( "-----------------------" );
		//PApplet.println("test 1 : " + (PApplet.parseInt(fc) > PApplet.parseInt(fc - DM.deltaTime)));
		//PApplet.println( "Real FrameCount : " + fc );
		//PApplet.println( "Nomral FrameCount : " + frameCount );
		
		//Update DeltaTime
		if(frameRate < options.targetFrameRate-20 && frameRate > options.targetFrameRate+20) {
			deltaTime = DM.deltaTime;
		}
		else {
			deltaTime = 1;
		}
		
		//Handle Draw Scene Method
		scene.draw();
		
		//PApplet.println( DM.deltaTime );
		
		// TODO remove this
		//Post FX
		//noStroke();
		//fill(24,28,41, 200);
		//rect(width/2,height/2,width,height);
		
		//fill(255);
		//text("Java Game 16x16", mouseX,mouseY);
		
		
		/*
		background(24,28,41);
		
		for(int i=0; i<backgroundObjects.size(); i++) {
			backgroundObjects.get(i).display();
		}
		for(int i=0; i<collisions.size(); i++) {
			collisions.get(i).update();
			collisions.get(i).display();
		}
		
		player.display();
		player.update();
		
		//GUI
		player.displayLife();
		*/
		//noFill();
		//stroke(25);
		//rect(width/2,height/2, screenX,screenY);
		
		
		surface.setTitle("Sardonyx Prealpha - Frame Rate " + (int)frameRate);
		
		//TODO to be moved
		//Update World Origin
		originX = (int)util.smoothMove(originX, originTargetX, (float) 0.1);
		originY = (int)util.smoothMove(originY, originTargetY, (float) 0.1);
		
		//Reset Events
		keyPressEvent = false;
		keyReleaseEvent = false;
		mousePressEvent = false;
		mouseReleaseEvent = false;
	}
	
	public void keyPressed() {
		for(int i=0; i<keys.size(); i++) {
			if(keys.get(i) == keyCode) {
				return;
			}
		}
		keys.add(keyCode);
		
		keyPressEvent = true;
	}
	
	public void keyReleased() {
		for(int i=0; i<keys.size(); i++) {
			if(keys.get(i) == keyCode) {
				keys.remove(i);
				break;
			}
		}
		keyReleaseEvent = true;
	}
	
	public void mousePressed() {
		mousePressEvent = true;
	}
	
	public void mouseReleased() {
		mouseReleaseEvent = true;
	}
	
	public void mouseWheel(MouseEvent event) {
		
		//Run mouseWheel Event
		scene.mouseWheel(event);
	}
	
	public boolean keyPress(int k) {
		boolean condition = false;
		for(int i=0; i<keys.size(); i++) {
			if(keys.get(i) == k) {
				condition = true;
				break;
			}
		}
		return condition;
	}
	
	//Change Scene
	public void setScene(String s) {
		PScene.name = s;
		
		//Run Setup On Scene Change
		scene.setup();
	}
	
	//Main
	public static void main(String args[]) {
		PApplet.main(new String[] { SideScroller.class.getName() });
	} 
}
