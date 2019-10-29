package game.engine.sidescroller;

import java.io.File;
import java.util.HashSet;

import components.AnimationComponent;
import dm.core.DM;
import game.entities.Player;

import lombok.Data;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import game.engine.scene.PScene;
import game.engine.scene.GameplayScene;
import game.engine.scene.MainMenu;
import game.engine.scene.PauseMenu;
import game.state.GameState;

/**
 * <h1>SideScroller Class</h1>
 * <p>
 * The SideScroller class is the main class. It extends the processing applet,
 * and is the heart of the game.
 * </p>
 */
@Data
public class SideScroller extends BaseWindow {
	// Camera Variables
	protected Camera camera;

	// Game Dev
	public static final String LEVEL = "Assets/Storage/Game/Maps/gg-2.dat";

	private DebugMode debug = DebugMode.OFF;
	private final boolean SNAP = true; // snap game.objects to grid when moving; located here for ease of access
	private int snapSize;

	// Font Resources
	private static PFont font_pixel;

	// Frame Rate
	private float deltaTime;

	// Scenes
	/**
	 * Use {@link #swapToScene(PScene)} or {@link #returnScene()} to change the
	 * game.engine.scene -- don't reassign this variable directly!
	 */
	private PScene currentScene;
	private PScene previousScene;
	private MainMenu menu;
	private GameplayScene game;
	private PauseMenu pmenu;

	// Events
	private HashSet<Integer> keys;
	private boolean keyPressEvent;
	private boolean keyReleaseEvent;
	private boolean mousePressEvent;
	private boolean mouseReleaseEvent;

	@Override
	public void settings() {
		super.settings();
		Util.assignApplet(this);
	}



	/**
	 * setup is called once at the beginning of the game. Most variables will be
	 * initialized here.
	 */
	@Override
	public void setup() {
		GameState.getInstance().setApplet(this);

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

		// Default frameRate
		frameRate(Options.targetFrameRate);

		deltaTime = 1;

		// Create ArrayList
		keys = new HashSet<Integer>();

		// Main Load
		load();

		// Create game.engine.scene
		game = new GameplayScene();
		game.setup();
		menu = new MainMenu();
		pmenu = new PauseMenu();
		swapToScene(menu);

		// Camera
		camera = new Camera(this);
		camera.setMouseMask(CONTROL);
		camera.setMinZoomScale(0.3);
		camera.setMaxZoomScale(3);

		scaleResolution();
		game.loadLevel(LEVEL);
	}

	/**
	 * This is where any needed assets will be loaded.
	 */
	private void load() {
		Tileset.load(this);
		if (new File("Assets/Font/font-pixel-48.vlw").exists()) {
			font_pixel = loadFont("Assets/Font/font-pixel-48.vlw"); // Load Font
			textFont(font_pixel); // Apply Text Font
		}
		else {
			System.err.println("Could not locate the game font file.");
		}
	}

	/**
	 * 
	 * @param newScene
	 * @see #returnScene()
	 */
	public void swapToScene(PScene newScene) {
		if (currentScene != null) {
			currentScene.switchFrom();
			if (!(newScene.equals(previousScene))) {
				previousScene = currentScene;
			}
		}
		currentScene = newScene;
		currentScene.switchTo();
	}

	/**
	 * Scenes should call this if they are being closed and the intention is to
	 * return to the previous game.engine.scene (for example closing the pause menu and
	 * returning to the game).
	 */
	public void returnScene() {
		if (previousScene != null) {
			swapToScene(previousScene);
		}
	}

	/**
	 * draw is called once per frame and is the game loop. Any update or displaying
	 * functions should be called here.
	 */
	@Override
	public void draw() {
		
		camera.hook();
		drawBelowCamera();
		camera.release();
		drawAboveCamera();

		rectMode(CENTER);

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
	}

	/**
	 * Any Processing drawing enclosed in {@link #drawBelowCamera()} will be
	 * affected (zoomed, panned, rotated) by the camera. Called in {@link #draw()},
	 * before {@link #drawAboveCamera()}.
	 * 
	 * @see #drawAboveCamera()
	 * @see {@link Camera#hook()}
	 */
	private void drawBelowCamera() {
		currentScene.draw(); // Handle Draw Scene Method - draws world, etc.
		if (debug == DebugMode.ALL) {
			currentScene.debug();
			camera.postDebug();
		}
	}

	/**
	 * Any Processing drawing enclosed in {@link #drawAboveCamera()} will not be
	 * affected (zoomed, panned, rotated) by the camera. Called in {@link #draw()},
	 * after {@link #drawBelowCamera()}.
	 * 
	 * @see #drawBelowCamera()
	 * @see {@link Camera#release()}
	 */
	private void drawAboveCamera() {
		currentScene.drawUI();
		if (debug == DebugMode.ALL) {
			camera.post();
			displayDebugInfo();
		}
		if (debug == DebugMode.INFO_ONLY) {
			displayDebugInfo();
		}
	}

	/**
	 * keyPressed decides if the key that has been pressed is a valid key. if it is,
	 * it is then added to the keys ArrayList, and the keyPressedEvent flag is set.
	 * 
	 * FOR GLOBAL KEYS ONLY
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		keys.add(event.getKeyCode());
		keyPressEvent = true;
	}

	/**
	 * keyReleased decides if the key pressed is valid and if it is then removes it
	 * from the keys ArrayList and keyReleaseEvent flag is set.
	 * 
	 * FOR GLOBAL KEYS ONLY
	 */
	@Override
	public void keyReleased(KeyEvent event) {
		keys.remove(event.getKeyCode());
		keyReleaseEvent = true;

		switch (event.getKeyCode()) { // must be ALL-CAPS
			case 'Z' :
				frameRate(5000);
				break;
			case 'X' :
				frameRate(20);
				break;
			case 'V' :
				camera.toggleDeadZone(); // for development
				break;
			case 'C' :
				camera.setCameraPosition(camera.getMouseCoord()); // for development
				break;
			case 'F' :
				camera.setFollowObject(game.getPlayer()); // for development
				camera.setZoomScale(1.0f); // for development
				break;
			case 'G' :
				camera.shake(0.4f); // for development
				break;
			case 122 : // F11
				noLoop();
				stage.setFullScreen(!stage.isFullScreen());
				super.scaleResolution();
				loop();
				break;
			case 27 : // ESC - Pause menu here
				swapToScene(currentScene == pmenu ? game : pmenu);
				debug = currentScene == pmenu ? DebugMode.OFF : DebugMode.ALL;
				break;
			case 9 : // TAB
				debug = debug.next();
				break;
			default :
				break;

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
		game.mouseWheel(event);
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
	 * Any object that is transformed by the camera (ie. not HUD elements) and uses
	 * mouse position in any manner should use this method to access the mouse
	 * coordinate (ie. where the mouse is in the game world). Such game.objects should
	 * variable.
	 * 
	 * @return Mouse Coordinate [Game World]
	 * @see {@link org.gicentre.utils.move.ZoomPan#getMouseCoord() getMouseCoord()}
	 * @see #getMouseCoordScreen()
	 */
	public PVector getMouseCoordGame() {
		return camera.getMouseCoord();
	}

	/**
	 * Objects that use the screen mouse coordinate (most UI game.objects) to determine
	 * interaction should use this method to get a PVector of the mouse coord, or
	 * refer to the PApplet mouseX and mouseY variables.
	 * 
	 * @return Mouse Coordinate [Screen]
	 * @see #getMouseCoordGame()
	 */
	public PVector getMouseCoordScreen() {
		return new PVector(mouseX, mouseY);
	}



	private void displayDebugInfo() {
		final int lineOffset = 12; // vertical offset
		final int yOffset = 1;
		final int labelPadding = 225; // label -x offset (from screen width)
		final int ip = 1; // infoPadding -xoffset (from screen width)
		final Player player = game.getPlayer();
		fill(0, 50);
		noStroke();
		rectMode(CORNER);
		rect(width - labelPadding, 0, labelPadding, yOffset + lineOffset * 11);
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
		text("[" + round(player.getBaseObjectData().getPos().x) + ", " + round(player.getBaseObjectData().getPos().y) + "]", width - ip, lineOffset * 0 + yOffset);
		text("[" + player.speedX + ", " + player.speedY + "]", width - ip, lineOffset * 1 + yOffset);
		text("[" + player.animation.name + "]", width - ip, lineOffset * 2 + yOffset);
		text("[" + round(player.animation.getFrame()) + " / " + player.animation.getAnimLength() + "]", width - ip,
				lineOffset * 3 + yOffset);
		text("[" + (player.flying ? "FLY" : player.attack ? "ATT" : "DASH") + "]", width - ip,
				lineOffset * 4 + yOffset);
		text("[" + PApplet.round(camera.getPosition().x) + ", " + PApplet.round(camera.getPosition().y) + "]",
				width - ip, lineOffset * 5 + yOffset);
		text("[" + String.format("%.2f", camera.getZoomScale()) + "]", width - ip, lineOffset * 6 + yOffset);
		text("[" + round(degrees(camera.getCameraRotation())) + "]", width - ip, lineOffset * 7 + yOffset);
		text("[" + round(camera.getMouseCoord().x) + ", " + round(camera.getMouseCoord().y) + "]", width - ip,
				lineOffset * 8 + yOffset);
//		text("[" + projectileObjects.size() + "]", width - ip, lineOffset * 9 + yOffset); TODO expose
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
