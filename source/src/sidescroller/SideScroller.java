package sidescroller;

import java.util.ArrayList;
import java.util.HashSet;

import components.AnimationComponent;
import dm.core.DM;

import entities.Player;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import objects.BackgroundObject;
import objects.CollidableObject;
import objects.GameObject;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PSurface;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.javafx.PSurfaceFX;

import projectiles.ProjectileObject;

import scene.PScene;
import scene.SceneMapEditor;
import scene.SceneMapEditor.Tools;

/**
 * <h1>SideScroller Class</h1>
 * <p>
 * The SideScroller class is the main class. It extends the processing applet,
 * and is the heart of the game.
 * </p>
 */
public class SideScroller extends PApplet {

	public static final String LEVEL = "Assets/Storage/Game/Maps/gg-2.dat";
	public static final boolean DEBUG = true;
	public static final boolean SNAP = true; // snap objects to grid when moving; located here for ease of access
	public static int snapSize;

	// Image Resources
	public PImage graphicsSheet;
	public PImage magicSheet;

	// Main Resource
	public GameGraphics gameGraphics;

	// Font Resources
	private PFont font_pixel;

	// Options
	public Options options;

	// Frame Rate
	public float deltaTime;

	// Scenes
	public SceneMapEditor mapEditor;

	Util util = new Util(this);

	// Player
	public Player player;

	// World Objects
	public ArrayList<CollidableObject> collidableObjects;
	public ArrayList<BackgroundObject> backgroundObjects;
	public ArrayList<GameObject> gameObjects;
	public ArrayList<ProjectileObject> projectileObjects;

	// Events
	private HashSet<Integer> keys;
	public boolean keyPressEvent;
	public boolean keyReleaseEvent;
	public boolean mousePressEvent;
	public boolean mouseReleaseEvent;

	// Camera Variables
	public Camera camera;
	private PVector mousePosition;
	

	// Expose JavaFX nodes
	private PSurfaceFX surface;
	private Canvas canvas;
	private Stage stage;

	/**
	 * controls how processing handles the window
	 */
	@Override
	public void settings() {
		size(displayWidth, displayHeight, FX2D);
	}

	/**
	 * Called by Processing after settings().
	 */
	@Override
	protected PSurface initSurface() {
		surface = (PSurfaceFX) super.initSurface();
		canvas = (Canvas) surface.getNative();
		stage = (Stage) canvas.getScene().getWindow();
		return surface;
	}

	/**
	 * The default {@link #noSmooth()} does not work in FX2D mode - we override the
	 * default function with a working technique. Cannot be placed in
	 * {@link #settings()}, like it normally would be.
	 */
<<<<<<< HEAD

=======
	@Override
	public void noSmooth() {
		try {
			canvas.getGraphicsContext2D().setImageSmoothing(false);
		} catch (java.lang.NoSuchMethodError e) {
		}
	}
>>>>>>> 8e06683883b2a9fd2c1131505557caca4d7ef9e6

	/**
	 * setup is called once at the beginning of the game. Most variables will be
	 * initialized here.
	 */
	@Override
	public void setup() {

		snapSize = SNAP ? 32 : 1; // global snap step

		noSmooth();

		// Start Graphics
		background(0);

		// Setup modes
		imageMode(CENTER);
		rectMode(CENTER);
		strokeCap(SQUARE);

		// Setup DM
		DM.setup(this); // what is this?

		AnimationComponent.applet = this;

		// Create Option Class
		options = new Options();

		// Default frameRate
		frameRate(Options.targetFrameRate);

		deltaTime = 1;

		// Create ArrayList
		keys = new HashSet<Integer>();
		collidableObjects = new ArrayList<CollidableObject>();
		backgroundObjects = new ArrayList<BackgroundObject>();
		gameObjects = new ArrayList<GameObject>();
		projectileObjects = new ArrayList<ProjectileObject>();

		// Create Game Graphics
		gameGraphics = new GameGraphics(this);

		// Create scene
		mapEditor = new SceneMapEditor(this);

		// Main Load
		load();

		// Camera
		camera = new Camera(this);
		camera.setMouseMask(CONTROL);
		camera.setMinZoomScale(0.3);
		camera.setMaxZoomScale(3);
		// camera.setScreenDeadZone(new PVector(width * 0.25f, height * 0.25f), new
		// PVector(width * 0.75f, height * 0.75f)); // example
		camera.setWorldDeadZone(new PVector(50, 0), new PVector(width * 0.25f, height * 0.25f)); // example
		camera.setFollowObject(player);
	}

	/**
	 * This is where any needed assets will be loaded.
	 */
	private void load() {

		// Load Font
		font_pixel = loadFont("Assets/Font/font-pixel-48.vlw");

		// Apply Text Font
		textFont(font_pixel);

		// Load Graphics Sheet
		graphicsSheet = loadImage("Assets/Art/graphics-sheet.png");
		magicSheet = loadImage("Assets/Art/magic.png");

		// Load Options
		Options.load();

		// Create All Graphics
		gameGraphics.load();

		// Set Scene
		setScene("MAPEDITOR");

		// Create Player
		player = new Player(this);
		player.load(graphicsSheet);
		player.pos.x = 0; // // TODO set to spawn loc
		player.pos.y = -100; // // TODO set to spawn loc
	}

	/**
	 * draw is called once per frame and is the game loop. Any update or displaying
	 * functions should be called here.
	 */
	@Override
	public void draw() {
		surface.setTitle("Sardonyx Prealpha | " + mapEditor.tool.toString() + " | " + frameCount);

		pushMatrix();
		drawBelowCamera: { // drawn objects enclosed by pushMatrix() and popMatrix() are transformed by the
			// camera.
			camera.update();
			mousePosition = camera.getMouseCoord().copy();
			mapEditor.draw(); // Handle Draw Scene Method - draws player, world, etc.
			//camera.postDebug(); // for development
		}
		popMatrix();

		drawAboveCamera: { // Where HUD etc should be drawn
			mousePosition = new PVector(mouseX, mouseY);
			mapEditor.drawUI();
			player.displayLife();

			if (DEBUG) {
				displayDebugInfo();
			}
		}

		// Update DeltaTime
		if (frameRate < Options.targetFrameRate - 20 && frameRate > Options.targetFrameRate + 20) {
			deltaTime = DM.deltaTime;
		} else {
			deltaTime = 1;
		}

		// Reset Events
		keyPressEvent = false;
		keyReleaseEvent = false;
		mousePressEvent = false;
		mouseReleaseEvent = false;

		rectMode(CENTER);

		if (keys.contains(75)) { // K - for development
			if(SceneMapEditor.tool == Tools.PLAY) {
			camera.rotate(-PI / 60);
			}
		}
		if (keys.contains(76)) { // L - for development
			if(SceneMapEditor.tool == Tools.PLAY) {
			camera.rotate(PI / 60);
			}
		}
	}

	/**
	 * keyPressed decides if the key that has been pressed is a valid key. if it is,
	 * it is then added to the keys ArrayList, and the keyPressedEvent flag is set.
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		keys.add(event.getKeyCode());
		keyPressEvent = true;
	}

	/**
	 * keyReleased decides if the key pressed is valid and if it is then removes it
	 * from the keys ArrayList and keyReleaseEvent flag is set.
	 * Here for development
	 */
	@Override
	public void keyReleased(KeyEvent event) {
		keys.remove(event.getKeyCode());
		keyReleaseEvent = true;
<<<<<<< HEAD
		
		switch (event.getKey()) { // must be caps
		case 'X':
			frameRate(10); //slow down
			break;
		case 'R':
			frameRate(60); //reset framerate
			break;
		case 'V':
			if(SceneMapEditor.tool == Tools.PLAY) {
			camera.toggleDeadZone();
			}// for development
			break;
		case 'C':
			if(SceneMapEditor.tool == Tools.PLAY) {
			camera.setCameraPosition(camera.getMouseCoord()); // for development
			}
			break;
		case 'F':
			if(SceneMapEditor.tool == Tools.PLAY) {
			camera.setFollowObject(player); // for development
			camera.setZoomScale(1.0f); // for development
			}
			break;
		case 'G':
			if(SceneMapEditor.tool == Tools.PLAY) {
			camera.shake(0.4f); // for development
			}
			break;
		case 'H':
			if(SceneMapEditor.tool == Tools.PLAY && player.lifeCapacity <= 10) {
			player.lifeCapacity += 1;
			}
			break;
		case 'I':
			if(SceneMapEditor.tool == Tools.PLAY) {
			player.lifeCapacity -= 1;
			}
			break;	
		default:
			switch (event.getKeyCode()) { // non-character keys
			case 122: // F11
				noLoop();
				final PSurfaceFX FXSurface = (PSurfaceFX) surface;
				final Canvas canvas = (Canvas) FXSurface.getNative();
				final Stage stage = (Stage) canvas.getScene().getWindow();
				stage.setFullScreen(!stage.isFullScreen());
				loop();
=======

		switch (event.getKey()) { // must be ALL-CAPS
			case 'Z' :
				frameRate(2000);
>>>>>>> 8e06683883b2a9fd2c1131505557caca4d7ef9e6
				break;
			case 'X' :
				frameRate(10);
				break;
			case 'V' :
				camera.toggleDeadZone(); // for development
				break;
			case 'C' :
				camera.setCameraPosition(camera.getMouseCoord()); // for development
				break;
			case 'F' :
				camera.setFollowObject(player); // for development
				camera.setZoomScale(1.0f); // for development
				break;
			case 'G' :
				camera.shake(0.4f); // for development
				break;
			default :
				switch (event.getKeyCode()) { // non-character keys
					case 122 : // F11
						noLoop();
						stage.setFullScreen(!stage.isFullScreen());
						loop();
						break;
					case 27 : // ESC - Pause menu here
						if (looping) {
							noLoop();
						} else {
							loop();
						}
						break;
					default :
						break;
				}
		}
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
		mapEditor.mouseWheel(event);
		if (event.getAmount() == -1.0) { // for development
			camera.zoomIn(0.02f);
		} else {
			camera.zoomOut(0.02f);
		}
	}

	/**
	 * checks if the key pressed was valid, then returns true or false if the key
	 * was accepted. This method is called when determining if a key has been
	 * pressed.
	 * 
	 * @param k (int) the key that we are determining is valid and has been pressed.
	 * @return boolean key has or has not been pressed.
	 */
	public boolean keyPress(int k) {
		return keys.contains(k);
	}

	/**
	 * Sets the scene to be used.
	 * 
	 * @param s the scene id.
	 */
	private void setScene(String s) {
		PScene.name = s;

		// Run Setup On Scene Change
		mapEditor.setup();
	}

	/**
	 * Any object that is transformed by the camera (ie. not HUD elements) and uses
	 * mouse position in any manner should use this method to access the <b>mouse
	 * X</b> coordinate (ie. where the mouse is in the game world). Such objects
	 * should not reference the PApplet's {@link processing.core.PApplet.mouseX
	 * mouseX} variable.
	 * 
	 * @return mouseX coordinate (accounting for camera displacement).
	 * @see {@link #getMouseY()}
	 * @see {@link org.gicentre.utils.move.ZoomPan#getMouseCoord() getMouseCoord()}
	 */
	public int getMouseX() {
		return (int) mousePosition.x;
	}

	/**
	 * Any object that is transformed by the camera (ie. not HUD elements) and uses
	 * mouse position in any manner should use this method to access the <b>mouse
	 * Y</b> coordinate (ie. where the mouse is in the game world). Such objects
	 * should not reference the PApplet's {@link processing.core.PApplet.mouseX
	 * mouseY} variable.
	 * 
	 * @return mouseY coordinate (accounting for camera displacement).
	 * @see {@link #getMouseX()}
	 * @see {@link org.gicentre.utils.move.ZoomPan#getMouseCoord() getMouseCoord()}
	 */
	public int getMouseY() {
		return (int) mousePosition.y;
	}

	private void displayDebugInfo() {
		final int lineOffset = 12; // vertical offset
		final int yOffset = 1;
		final int labelPadding = 225; // label -x offset (from screen width)
		final int ip = 1; // infoPadding -xoffset (from screen width)
		fill(0, 50);
		noStroke();
		rectMode(CORNER);
		rect(width - labelPadding, 0, labelPadding, yOffset + lineOffset * 10);
		fill(255, 0, 0);
		textSize(18);

		textAlign(LEFT, TOP);
		text("Player Pos:", width - labelPadding, lineOffset * 0 + yOffset);
		text("Player Speed:", width - labelPadding, lineOffset * 1 + yOffset);
		text("Anim #:", width - labelPadding, lineOffset * 2 + yOffset);
		text("Anim Frame:", width - labelPadding, lineOffset * 3 + yOffset);
		text("Player Status:", width - labelPadding, lineOffset * 4 + yOffset);
		text("Camera Pos:", width - labelPadding, lineOffset * 5 + yOffset);
		text("Camera Zoom:", width - labelPadding, lineOffset * 6 + yOffset);
		text("Camera Rot:", width - labelPadding, lineOffset * 7 + yOffset);
		text("World Mouse:", width - labelPadding, lineOffset * 8 + yOffset);
		text("Projectiles:", width - labelPadding, lineOffset * 9 + yOffset);
		text("Framerate:", width - labelPadding, lineOffset * 10 + yOffset);

		textAlign(RIGHT, TOP);
		text("[" + round(player.pos.x) + ", " + round(player.pos.y) + "]", width - ip, lineOffset * 0 + yOffset);
		text("[" + player.speedX + ", " + player.speedY + "]", width - ip, lineOffset * 1 + yOffset);
		text("[" + player.animation.name + "]", width - ip, lineOffset * 2 + yOffset);
		text("[" + round(player.animation.getFrame()) + " / " + player.animation.getAnimLength() + "]", width - ip,
				lineOffset * 3 + yOffset);
		text("[" + (player.flying ? "FLY" : player.attack ? "ATT" : "DASH") + "]", width - ip,
				lineOffset * 4 + yOffset);
		text("[" + camera.getCameraPosition() + "]", width - ip, lineOffset * 5 + yOffset);
		text("[" + String.format("%.2f", camera.getZoomScale()) + "]", width - ip, lineOffset * 6 + yOffset);
		text("[" + round(degrees(camera.getCameraRotation())) + "]", width - ip, lineOffset * 7 + yOffset);
		text("[" + round(camera.getMouseCoord().x) + ", " + round(camera.getMouseCoord().y) + "]", width - ip,
				lineOffset * 8 + yOffset);
		text("[" + projectileObjects.size() + "]", width - ip, lineOffset * 9 + yOffset);
		if (frameRate >= 59.5) {
			fill(0, 255, 0);
		}
		text("[" + round(frameRate) + "]", width - ip, lineOffset * 10 + yOffset);
	}

	@Override
	public void exit() {
		// super.exit(); // commented-out - prevents ESC from closing game
	}

	// Main
	public static void main(String args[]) {
		PApplet.main(new String[] { SideScroller.class.getName() });
	}
}
