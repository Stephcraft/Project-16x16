package sidescroller;

import java.util.ArrayList;
import java.util.HashSet;

import dm.core.DM;

import entities.Player;

import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import objects.BackgroundObject;
import objects.Collision;
import objects.GameObject;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.javafx.PSurfaceFX;

import projectiles.ProjectileObject;

import scene.PScene;
import scene.SceneMapEditor;

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
	public SceneMapEditor mapEditor;

	Util util = new Util(this);

	// Player
	public Player player;

	// World Objects
	public ArrayList<Collision> collisions;
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

	/**
	 * controls how processing handles the window
	 */
	@Override
	public void settings() {
		size(1280, 720, FX2D);
	}

	/**
	 * The default {@link #noSmooth()} does not work in FX2D mode - we override the
	 * default function with a working technique. Cannot be placed in
	 * {@link #settings()}, like it normally would be.
	 */
	@Override
	public void noSmooth() {
		final PSurfaceFX FXSurface = (PSurfaceFX) surface;
		final Canvas canvas = (Canvas) FXSurface.getNative();
		canvas.getGraphicsContext2D().setImageSmoothing(false);
	}

	/**
	 * setup is called once at the beginning of the game. Most variables will be
	 * initialized here.
	 */
	@Override
	public void setup() {

		noSmooth();

		// Start Graphics
		background(0);

		// Setup modes
		imageMode(CENTER);
		rectMode(CENTER);
		strokeCap(SQUARE);

		// Setup DM
		DM.setup(this); // what is this?

		// Create Option Class
		options = new Options();

		// Default frameRate
		frameRate(60);

		deltaTime = 1;

		// Create ArrayList
		keys = new HashSet<Integer>();
		collisions = new ArrayList<Collision>();
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
//		camera.setScreenDeadZone(new PVector(width * 0.25f, height * 0.25f), new PVector(width * 0.75f, height * 0.75f)); // example
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

		// Create All Graphics
		gameGraphics.load();

		// Set Scene
		setScene("MAPEDITOR");

		// Create Player
		player = new Player(this);
		player.load(graphicsSheet);
		player.pos.x = 0;
		player.pos.y = -100;
	}

	public float fc = 0;
	public float fc2 = 0;

	/**
	 * draw is called once per frame and is the game loop. Any update or displaying
	 * functions should be called here.
	 */
	@Override
	public void draw() {
		surface.setTitle("Sardonyx Prealpha - Frame Rate " + (int) frameRate);

		pushMatrix();
		drawBelowCamera: { // drawn objects enclosed by pushMatrix() and popMatrix() are transformed by the
							// camera.
			camera.update();
			mousePosition = camera.getMouseCoord().copy();
			mapEditor.draw(); // Handle Draw Scene Method - draws player, world, etc.
			camera.postDebug(); // for development
		}
		popMatrix();

		drawAboveCamera: { // Where HUD etc should be drawn
			mousePosition = new PVector(mouseX, mouseY);
			mapEditor.drawUI();

			if (DEBUG) {
				fill(255, 0, 0);
				textSize(20);
				textAlign(RIGHT, CENTER);

				int lineOffset = 15;
				text("X: " + player.pos.x + "Y: " + player.pos.y, width, 5);
				text("SX: " + player.speedX + " SY: " + player.speedY, width, 5 + lineOffset * 1);
				text("anim: " + player.animation.name, width, 5 + lineOffset * 2);
				text("f: " + player.animation.frame + " ends: " + player.animation.length, width, 5 + lineOffset * 3);
				text("fly: " + player.flying + " att: " + player.attack + " dash: " + player.dashing, width,
						5 + lineOffset * 4);
				text("Camera Pos: " + camera.getCameraPosition(), width, 5 + lineOffset * 5);
				text("Camera Zoom: " + String.format("%.2f", camera.getZoomScale()), width, 5 + lineOffset * 6);
				text("World Mouse: " + round(camera.getMouseCoord().x) + ", " + round(camera.getMouseCoord().y), width,
						5 + lineOffset * 7);
			}
		}

		// Update DeltaTime
		if (frameRate < options.targetFrameRate - 20 && frameRate > options.targetFrameRate + 20) {
			deltaTime = DM.deltaTime;
		} else {
			deltaTime = 1;
		}

		// Reset Events
		keyPressEvent = false;
		keyReleaseEvent = false;
		mousePressEvent = false;
		mouseReleaseEvent = false;

		if (keys.contains(75)) { // K - for development
			camera.rotate(-PI / 60);
		}
		if (keys.contains(76)) { // L - for development
			camera.rotate(PI / 60);
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
	 */
	@Override
	public void keyReleased(KeyEvent event) {
		keys.remove(event.getKeyCode());
		keyReleaseEvent = true;

		switch (event.getKey()) { // must be caps
		case 'Z':
			frameRate(2000);
			break;
		case 'X':
			frameRate(10);
			break;
		case 'V':
			camera.toggleDeadZone(); // for development
			break;
		case 'C':
			camera.setCameraPosition(camera.getMouseCoord()); // for development
			break;
		case 'F':
			camera.setFollowObject(player); // for development
			camera.setZoomScale(1.0f); // for development
			break;
		case 'G':
			camera.shake(0.4f); // for development
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
				break;
			case 27: // ESC - Pause menu here
				if (looping) {
					noLoop();
				} else {
					loop();
				}
				break;
			default:
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

	@Override
	public void exit() {
		// super.exit(); // commented-out - prevents ESC from closing game
	}

	// Main
	public static void main(String args[]) {
		PApplet.main(new String[] { SideScroller.class.getName() });
	}
}
