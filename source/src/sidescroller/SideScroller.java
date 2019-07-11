package sidescroller;

import processing.core.*;
import processing.event.MouseEvent;
import projectiles.ProjectileObject;

import java.util.ArrayList;

import scene.Camera;
import scene.PScene;
import scene.SceneMapEditor;

import entities.*;
import sidescroller.Util;
import sidescroller.Options;

import objects.BackgroundObject;
import objects.Collision;
import objects.GameObject;
import dm.core.*;

/**
 * <h1>SideScroller Class
 * <p>
 * The SideScroller class is the main class. It extends the processing applet,
 * and is the heart of the game.
 * </p> 
 */
public class SideScroller extends PApplet {

	public boolean LOADED;
	public boolean FAILED;

	public boolean debug;

	public int floor;

	// World Origin Coordinates
	public int originX;
	public int originY;
	public int originTargetX;
	public int originTargetY;

	public int screenX;
	public int screenY;

	public int worldWidth;
	public int worldHeight;
	public PVector worldPosition;

	// Image Resources
	public PImage graphicsSheet;
	public PImage magicSheet;

	// Main Resource
	public GameGraphics gameGraphics;

	// Font Resources
	public PFont font_pixel;

	// Options
	public Options options;

	// Frame Rate
	public float deltaTime;

	// Scenes
	public PScene scene;

	Util util = new Util(this);

	// Player
	public Player player;

	// World Objects
	public ArrayList<Collision> collisions;
	public ArrayList<BackgroundObject> backgroundObjects;
	public ArrayList<GameObject> gameObjects;
	public ArrayList<ProjectileObject> projectileObjects;

	// Events
	ArrayList<Integer> keys;
	public boolean keyPressEvent;
	public boolean keyReleaseEvent;
	public boolean mousePressEvent;
	public boolean mouseReleaseEvent;

	// Camera Variables
	Camera cam;

	/**
	 * controls how processing handles the window
	 */
	@Override
	public void settings() {
		size((int) (1280 * 1.0), (int) (720 * 1.0), JAVA2D); // *1.5 //Changed to 16:9

		noSmooth();
	}

	/**
	 * setup is called once at the beginning of the game. Most variables will be initialized here. 
	 */
	@Override
	public void setup() {

		// Start Graphics
		background(0);

		// Setup Camera
		cam = new Camera(-400, -400, 0, 0, 1, 1, this);
		cam.CameraInit();
		cam.useCamera();

		// Setup modes
		imageMode(CENTER);
		rectMode(CENTER);
		strokeCap(SQUARE);

		// Setup DM
		DM.setup(this);

		// Create Option Class
		options = new Options();

		// Default frameRates
		frameRate(60);

		deltaTime = 1;

		// Create ArrayList
		keys = new ArrayList<Integer>();
		collisions = new ArrayList<Collision>();
		backgroundObjects = new ArrayList<BackgroundObject>();
		gameObjects = new ArrayList<GameObject>();
		projectileObjects = new ArrayList<ProjectileObject>();

		// Create Game Graphics
		gameGraphics = new GameGraphics(this);

		// Debug Option
		debug = true;

		// Set Screen Size
		screenX = width - 400;
		screenY = height - 400;

		worldWidth = width * 2;
		worldHeight = height;
		worldPosition = new PVector(0, 0);

		// Create scene
		scene = new SceneMapEditor(this);

		// Main Load
		thread("load");
	}

	/**
	 * This is where any needed assets will be loaded.
	 */
	public void load() {

		// Load Font
		font_pixel = loadFont("Assets/Font/font-pixel-48.vlw");

		// Apply Text Font
		textFont(font_pixel);

		// Load Graphics Sheet
		graphicsSheet = loadImage("Assets/Art/graphics-sheet.png");
		magicSheet = loadImage("Assets/Art/magic.png");

		// Create All Graphics
		gameGraphics.load();

		// Set Scene
		setScene("MAPEDITOR");

		// Create Player
		player = new Player(this);
		player.load(graphicsSheet);
		player.pos.x = 0;
		player.pos.y = -100;
		// cam.Follow(player);

		// Set Floor
		floor = 400;

		LOADED = true;
	}

	public float fc = 0;
	public float fc2 = 0;

	/**
	 * draw is called once per frame and is the game loop. Any update or displaying
	 * functions should be called here.
	 */
	@Override
	public void draw() {
		if (!LOADED) {
			return;
		}

		// Camera
		cam.useCamera();
		// cam.Move(.5f, 0);

		if (keyPress(80)) {
			cam.zoom(-.005f);
		}

		if (keyPressEvent && keyPress(81)) {
			frameRate(60);
		}
		if (keyPressEvent && keyPress(84)) {
			frameRate(2);
		}

		// Update DeltaTime
		if (frameRate < options.targetFrameRate - 20 && frameRate > options.targetFrameRate + 20) {
			deltaTime = DM.deltaTime;
		} else {
			deltaTime = 1;
		}

		// Handle Draw Scene Method
		scene.draw();

		surface.setTitle("Sardonyx Prealpha - Frame Rate " + (int) frameRate);

		// TODO to be moved
		// Update World Origin
		originX = (int) util.smoothMove(originX, originTargetX, (float) 0.1);
		originY = (int) util.smoothMove(originY, originTargetY, (float) 0.1);

		// Reset Events
		keyPressEvent = false;
		keyReleaseEvent = false;
		mousePressEvent = false;
		mouseReleaseEvent = false;
	}

	/**
	 * keyPressed decides if the key that has been pressed is a valid key.
	 * if it is, it is then added to the keys ArrayList, and the keyPressedEvent flag is set.
	 */
	@Override
	public void keyPressed() {
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i) == keyCode) {
				return;
			}
		}
		keys.add(keyCode);

		keyPressEvent = true;
	}

	/**
	 * keyReleased decides if the key pressed is valid and if it is then removes it from the 
	 * keys ArrayList and keyReleaseEvent flag is set.
	 */
	@Override
	public void keyReleased() {
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i) == keyCode) {
				keys.remove(i);
				break;
			}
		}
		keyReleaseEvent = true;
	}

	/**
	 * sets the mousePressEvent flag.
	 */
	@Override
	public void mousePressed() {
		mousePressEvent = true;
	}

	/**
	 * sets the mouseReleaseEvent flag
	 */
	@Override
	public void mouseReleased() {
		mouseReleaseEvent = true;
	}

	/**
	 * Handles scrolling events.
	 */
	@Override
	public void mouseWheel(MouseEvent event) {

		scene.mouseWheel(event);
	}

	/**
	 * checks if the key pressed was valid, then returns true or false if
	 * the key was accepted. This method is called when determining if a 
	 * key has been pressed.
	 * 
	 * @param k (int) the key that we are determining is valid and has been pressed. 
	 * @return boolean key has or has not been pressed.
	 */
	public boolean keyPress(int k) {
		boolean condition = false; // no it is not.

		// is the key k valid?
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i) == k) {
				condition = true; // yes it is end the loop
				break;
			}
		}
		return condition;// return decision (key has or has not been pressed)
	}

	/**
	 * Sets the scene to be used.
	 * @param s the scene id.
	 */
	public void setScene(String s) {
		PScene.name = s;

		// Run Setup On Scene Change
		scene.setup();
	}

	// Main
	public static void main(String args[]) {
		PApplet.main(new String[]{SideScroller.class.getName()});
	}
}
