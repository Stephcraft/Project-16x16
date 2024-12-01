package project_16x16;

import java.util.ArrayDeque;
import java.util.HashSet;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCombination;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PSurface;
import processing.core.PVector;
import processing.event.MouseEvent;
import processing.javafx.PSurfaceFX;
import project_16x16.Options.Option;
import project_16x16.components.AnimationComponent;
import project_16x16.entities.Player;
import project_16x16.factory.AudioFactory;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.scene.AudioSettings;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;
import project_16x16.scene.MainMenu;
import project_16x16.scene.MultiplayerClientMenu;
import project_16x16.scene.MultiplayerHostMenu;
import project_16x16.scene.MultiplayerMenu;
import project_16x16.scene.PScene;
import project_16x16.scene.PauseMenu;
import project_16x16.scene.Settings;
import project_16x16.ui.Notifications;

/**
 * <h1>SideScroller Class</h1>
 * <p>
 * The SideScroller class is the main class. It extends the processing applet,
 * and is the heart of the game.
 * </p>
 */
public class SideScroller extends PApplet {

	// Game Dev
	public static final String LEVEL = "Storage/Game/Maps/tiledMap.dat";

	public enum DebugType {
		OFF, ALL, INFO_ONLY;

		private static DebugType[] vals = values();

		public DebugType next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}

		public static DebugType get(int value) {
			return values()[value];
		}
	}

	public DebugType debug = DebugType.get(Options.debugMode);

	public static final boolean SNAP = true; // snap objects to grid when moving; TODO move to options
	public static int snapSize;

	// Game Rendering
	private PVector windowSize = new PVector(1280, 720); // Game window size -- to be set via options
	public PVector gameResolution = new PVector(1280, 720); // Game rendering resolution -- to be set
															// via options
	// Font Resources
	private static PFont font_pixel;

	// Scenes	
	private ArrayDeque<GameScenes> sceneHistory;
	private int sceneSwapTime = 0;

	private static MainMenu menu;
	private static GameplayScene game;
	private static PauseMenu pmenu;
	private static Settings settings;
	private static MultiplayerMenu mMenu;
	private static MultiplayerHostMenu mHostMenu;
	private static MultiplayerClientMenu mClientMenu;
	private static AudioSettings audioSettings;

	public enum GameScenes {
		MAIN_MENU(menu), GAME(game), PAUSE_MENU(pmenu), SETTINGS_MENU(settings), MULTIPLAYER_MENU(mMenu),
		HOST_MENU(mHostMenu), CLIENT_MENU(mClientMenu), AUDIO_SETTINGS(audioSettings);

		PScene scene;

		private GameScenes(PScene scene) {
			this.scene = scene;
		}

		public PScene getScene() {
			return scene;
		}
	}

	// Events
	private HashSet<Integer> keysDown;
	public boolean keyPressEvent; //     TODO remove -- override keyPressed() instead
	public boolean keyReleaseEvent; //   TODO remove -- override mouseReleased() instead
	public boolean mousePressEvent; //   TODO remove -- override mousePressed() instead
	public boolean mouseReleaseEvent; // TODO remove -- override mouseReleased() instead

	// Camera Variables
	public Camera camera; // TODO encapsulate in gamePlayScene

	// Expose JavaFX nodes
	/**
	 * Processing's JavaFX surface. Extends and wraps a JavaFX {@link #canvas}.
	 */
	private PSurfaceFX surface;
	/**
	 * JavaFX Canvas - an image that can be drawn on using a set of graphics
	 * commands. A node of the {@link #scene}.
	 */
	private Canvas canvas;
	/**
	 * JavaFX Scene. Embedded in the {@link #stage} - the container for all other
	 * content (JavaFX nodes).
	 */
	protected Scene scene;
	/**
	 * JavaFX Stage - the top level JavaFX container (titlebar, etc.).
	 */
	private Stage stage;

	/**
	 * controls how processing handles the window
	 */
	@Override
	public void settings() {
		Utility.assignApplet(this);
		size((int) windowSize.x, (int) windowSize.y, FX2D);
	}

	/**
	 * Called by Processing after settings().
	 */
	@Override
	protected PSurface initSurface() {
		surface = (PSurfaceFX) super.initSurface();
		canvas = (Canvas) surface.getNative();
		canvas.widthProperty().unbind(); // used for scaling
		canvas.heightProperty().unbind(); // used for scaling
		scene = canvas.getScene();
		stage = (Stage) scene.getWindow();
		stage.setTitle("Project-16x16");
		stage.setResizable(false); // prevent abitrary user resize
		stage.setFullScreenExitHint(""); // disable fullscreen toggle hint
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // prevent ESC toggling fullscreen
		scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
		return surface;
	}

	/**
	 * Passes JavaFX window closed call to game.
	 * 
	 * @param event
	 */
	private void closeWindowEvent(WindowEvent event) {
		try {
			AudioFactory.getInstance().exit();
			game.exit();
		} finally {
			stage.close();
		}
	}

	/**
	 * setup is called once at the beginning of the game. Most variables will be
	 * initialized here.
	 */
	@Override
	public void setup() {

		snapSize = SNAP ? Options.snapSize : 1; // global snap step

		// Set frame rate limit
		frameRate(Options.targetFrameRate);
		
		// Setup modes
		imageMode(CENTER);
		rectMode(CENTER);
		strokeCap(SQUARE);

		// Create ArrayList
		keysDown = new HashSet<Integer>();

		// Main Load
		load();
		AnimationComponent.assignApplet(this);
		Notifications.assignApplet(this);
		AudioFactory.getInstance().assignApplet(this);

		// Create scene
		sceneHistory = new ArrayDeque<>();
		game = new GameplayScene(this, Constants.DEV_LEVEL);
		menu = new MainMenu(this);
		pmenu = new PauseMenu(this);
		settings = new Settings(this);
		mMenu = new MultiplayerMenu(this);
		mHostMenu = new MultiplayerHostMenu(this);
		mClientMenu = new MultiplayerClientMenu(this);
		audioSettings = new AudioSettings(this);
		swapToScene(GameScenes.MAIN_MENU);

		// Camera
		camera = new Camera(this);
		camera.setMouseMask(CONTROL);
		camera.setMinZoomScale(Constants.CAMERA_ZOOM_MIN);
		camera.setMaxZoomScale(Constants.CAMERA_ZOOM_MAX);

		scaleResolution();
		launchIntoMultiplayer();
	}

	/**
	 * This is where any needed assets will be loaded.
	 */
	private void load() {
		Tileset.load(this);
		surface.setIcon(Tileset.getAnimation("PLAYER::IDLE").get(0));
		font_pixel = loadFont(Constants.GAME_FONT); // Load Font
		textFont(font_pixel); // SideScrollerly Text Font
	}

	/**
	 * Use this method or {@link #returnScene()} to change the running game scene.
	 * 
	 * @param newScene
	 * @see #returnScene()
	 */
	public void swapToScene(GameScenes newScene) {
		if (frameCount - sceneSwapTime > 6 || frameCount == 0) {
			if (!newScene.equals(sceneHistory.peek())) { // if different
				if (!sceneHistory.isEmpty()) {
					sceneHistory.peek().getScene().switchFrom(); // switch from
				}
				sceneHistory.push(newScene);
				newScene.getScene().switchTo();
				sceneSwapTime = frameCount;
			}
		}
	}

	/**
	 * Scenes should call this if they are being closed and the intention is to
	 * return to the previous scene (for example closing the pause menu and
	 * returning to the game).
	 */
	public void returnScene() {
		if (sceneHistory.size() > 1) {
			sceneHistory.pop().getScene().switchFrom();
			sceneHistory.peek().getScene().switchTo();
			sceneSwapTime = frameCount;
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
		sceneHistory.peek().getScene().draw(); // Handle Draw Scene Method - draws world, etc.
		if (debug == DebugType.ALL) {
			sceneHistory.peek().getScene().debug();
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
		sceneHistory.peek().getScene().drawUI();
		Notifications.run();
		if (debug == DebugType.ALL) {
			camera.post();
			displayDebugInfo();
		}
		if (debug == DebugType.INFO_ONLY) {
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
	public void keyPressed(processing.event.KeyEvent event) {
		keysDown.add(event.getKeyCode());
		keyPressEvent = true;
	}

	/**
	 * keyReleased decides if the key pressed is valid and if it is then removes it
	 * from the keys ArrayList and keyReleaseEvent flag is set.
	 * 
	 * FOR GLOBAL KEYS ONLY
	 */
	@Override
	public void keyReleased(processing.event.KeyEvent event) {
		keysDown.remove(event.getKeyCode());
		keyReleaseEvent = true;

		switch (event.getKeyCode()) {
			case Options.frameRateHigh :
				frameRate(5000);
				break;
			case Options.frameRateLow :
				frameRate(20);
				break;
			case Options.frameRateDefault :
				frameRate(Options.frameRateDefault);
				break;
			case Options.toggleDeadzone :
				camera.toggleDeadZone();
				break;
			case Options.cameraToMouse :
				camera.setCameraPosition(camera.getMouseCoord());
				break;
			case Options.cameraToPlayer :
				camera.setFollowObject(game.getPlayer());
				camera.setZoomScale(1.0f);
				break;
			case Options.shake :
				camera.shake(0.4f);
				break;
			case Options.notify :
				Notifications.addNotification("Hello", "World");
				break;
			case Options.fullscreen :
				noLoop();
				stage.setFullScreen(!stage.isFullScreen());
				scaleResolution();
				loop();
				break;
			case Options.toggleDebug :
				debug = debug.next();
				Options.save(Option.debugMode, debug.ordinal());
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
		if(game.isZoomable()) {
			if (event.getCount() == -1) { // for development
				camera.zoomIn(0.02f);
			} else {
				camera.zoomOut(0.02f);
			}
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
	public boolean isKeyDown(int k) {
		return keysDown.contains(k);
	}

	/**
	 * Any object that is transformed by the camera (ie. not HUD elements) and uses
	 * mouse position in any manner should use this method to access the mouse
	 * coordinate (ie. where the mouse is in the game world). Such objects should
	 * not reference the PApplet's {@link processing.core.PApplet.mouseX mouseY}
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
	 * Objects that use the screen mouse coordinate (most UI objects) to determine
	 * interaction should use this method to get a PVector of the mouse coord, or
	 * refer to the PApplet mouseX and mouseY variables.
	 * 
	 * @return Mouse Coordinate [Screen]
	 * @see #getMouseCoordGame()
	 */
	public PVector getMouseCoordScreen() {
		return new PVector(mouseX, mouseY);
	}

	public void resizeWindow(int width, int height) {
		windowSize = new PVector(width, height);
		gameResolution = windowSize.copy();
		stage.setWidth(width); // sceneWidth is not bound, so doesn't change
		stage.setHeight(height);
//		stage.setScene(new Scene(new StackPane(canvas), width, height)); // TODO
	}

	public void resizeGameResolution(int width, int height) {
		gameResolution = new PVector(width, height);
		scaleResolution();
	}

	/**
	 * Scales the game rendering (as defined by gameResolution) to fill the current
	 * stage size. <b>Should be called whenever stage size or game resolution is
	 * changed</b> - currently called only when toggling fullscreen mode.
	 */
	private void scaleResolution() {
		canvas.getTransforms().clear();
		canvas.setTranslateX(-scene.getWidth() / 2 + gameResolution.x / 2); // recenters after scale
		canvas.setTranslateY(-scene.getHeight() / 2 + gameResolution.y / 2); // recenters after scale
		if (!(scene.getWidth() == gameResolution.x && scene.getHeight() == gameResolution.y)) {
			canvas.setWidth(gameResolution.x);
			canvas.setHeight(gameResolution.y);
			width = (int) gameResolution.x;
			height = (int) gameResolution.y;
			final double scaleX = scene.getWidth() / gameResolution.x;
			final double scaleY = scene.getHeight() / gameResolution.y;
			canvas.getTransforms().setAll(new Scale(scaleX, scaleY)); // scale canvas
		}
	}

	private void displayDebugInfo() {
		final int lineOffset = 12; // vertical offset
		final int yOffset = 1;
		final int labelPadding = 225; // label -x offset (from screen width)
		final int ip = 1; // infoPadding -xoffset (from screen width)
		final Player player = game.getPlayer();
		PVector velocity = player.getVelocity();
		fill(0, 50);
		noStroke();
		rectMode(CORNER);
		rect(width - labelPadding, 0, labelPadding, yOffset + lineOffset * 25);
		textSize(18);

		textAlign(LEFT, TOP);
		
		fill(255, 0, 0);
		text("Player Pos:", width - labelPadding, lineOffset * 0 + yOffset);
		text("Player Speed:", width - labelPadding, lineOffset * 1 + yOffset);
		text("Anim #:", width - labelPadding, lineOffset * 2 + yOffset);
		text("Anim Frame:", width - labelPadding, lineOffset * 3 + yOffset);
		text("Camera Pos:", width - labelPadding, lineOffset * 4 + yOffset);
		text("Camera Zoom:", width - labelPadding, lineOffset * 5 + yOffset);
		text("Camera Rot:", width - labelPadding, lineOffset * 6 + yOffset);
		text("World Mouse:", width - labelPadding, lineOffset * 7 + yOffset);
		text("Projectiles:", width - labelPadding, lineOffset * 8 + yOffset);
		text("Framerate:", width - labelPadding, lineOffset * 9 + yOffset);
		
		fill(55, 155, 255);
		text("Framerate HIGH:", width - labelPadding, lineOffset * 12 + yOffset);
		text("Framerate LOW:", width - labelPadding, lineOffset * 13 + yOffset);
		text("Toggle Deadzone:", width - labelPadding, lineOffset * 14 + yOffset);
		text("Camera->Mouse:", width - labelPadding, lineOffset * 15 + yOffset);
		text("Camera->Player:", width - labelPadding, lineOffset * 16 + yOffset);
		text("Shake Camera:", width - labelPadding, lineOffset * 17 + yOffset);
		text("Notification:", width - labelPadding, lineOffset * 18 + yOffset);
		text("Life cap++:", width - labelPadding, lineOffset * 19 + yOffset);
		text("Life cap--:", width - labelPadding, lineOffset * 20 + yOffset);
		text("Life++:", width - labelPadding, lineOffset * 21 + yOffset);
		text("Life--:", width - labelPadding, lineOffset * 22 + yOffset);
		text("Fullscreen:", width - labelPadding, lineOffset * 23 + yOffset);
		text("Toggle Debug:", width - labelPadding, lineOffset * 24 + yOffset);

		fill(255,255,0);
		textAlign(RIGHT, TOP);
		text("[" + round(player.position.x) + ", " + round(player.position.y) + "]", width - ip, lineOffset * 0 + yOffset);
		text("[" + round(velocity.x) + ", " + round(velocity.y) + "]", width - ip, lineOffset * 1 + yOffset);
		text("[" + player.animation.name + "]", width - ip, lineOffset * 2 + yOffset);
		text("[" + round(player.animation.getFrameID()) + " / " + player.animation.getAnimLength() + "]", width - ip,
				lineOffset * 3 + yOffset);
		text("[" + PApplet.round(camera.getPosition().x) + ", " + PApplet.round(camera.getPosition().y) + "]",
				width - ip, lineOffset * 4 + yOffset);
		text("[" + String.format("%.2f", camera.getZoomScale()) + "]", width - ip, lineOffset * 5 + yOffset);
		text("[" + round(degrees(camera.getCameraRotation())) + "]", width - ip, lineOffset * 6 + yOffset);
		text("[" + round(camera.getMouseCoord().x) + ", " + round(camera.getMouseCoord().y) + "]", width - ip,
				lineOffset * 7 + yOffset);
		text("[" + "?" + "]", width - ip, lineOffset * 8 + yOffset); // TODO expose
		
		text("['" + (char) Options.frameRateHigh + "']", width - ip, lineOffset * 12 + yOffset);
		text("['" + (char) Options.frameRateLow + "']", width - ip, lineOffset * 13 + yOffset);
		text("['" + (char) Options.toggleDeadzone + "']", width - ip, lineOffset * 14 + yOffset);
		text("['" + (char) Options.cameraToMouse + "']", width - ip, lineOffset * 15 + yOffset);
		text("['" + (char) Options.cameraToPlayer + "']", width - ip, lineOffset * 16 + yOffset);
		text("['" + (char) Options.shake + "']", width - ip, lineOffset * 17 + yOffset);
		text("['" + (char) Options.notify + "']", width - ip, lineOffset * 18 + yOffset);
		text("['" + (char) Options.lifeCapInc + "']", width - ip, lineOffset * 19 + yOffset);
		text("['" + (char) Options.lifeCapDec + "']", width - ip, lineOffset * 20 + yOffset);
		text("['" + (char) Options.lifeInc + "']", width - ip, lineOffset * 21 + yOffset);
		text("['" + (char) Options.lifeDec + "']", width - ip, lineOffset * 22 + yOffset);
		text("['F11']", width - ip, lineOffset * 23 + yOffset);
		text("['TAB']", width - ip, lineOffset * 24 + yOffset);

		if (frameRate >= 59.5) {
			fill(0, 255, 0);
		}
		else {
			fill(255, 0, 0);
		}
		text("[" + round(frameRate) + "]", width - ip, lineOffset * 9 + yOffset);
	}

	/**
	 * Launch into multiplayer mode instantly bases upon program args. Used in
	 * development to test & debug multiplayer more quickly.
	 */
	private void launchIntoMultiplayer() {
		if (args != null) {
			Multiplayer m;
			if (args[0].equals("host")) {
				try {
					m = new Multiplayer(this, true);
					((GameplayScene) GameScenes.GAME.getScene()).setupMultiplayer(m);
					swapToScene(GameScenes.GAME);
					((GameplayScene) GameScenes.GAME.getScene()).changeMode(GameModes.PLAY);
					stage.setTitle("host");
					System.out.println("~HOST~");
				} catch (Exception e) {
				}
			}
			if (args[0].equals("client")) {
				System.out.println("client path");
				try {
					m = new Multiplayer(this, false);
					((GameplayScene) (GameScenes.GAME.getScene())).setupMultiplayer(m);
					swapToScene(GameScenes.GAME);
					((GameplayScene) GameScenes.GAME.getScene()).changeMode(GameModes.PLAY);
					stage.setTitle("client");
					System.out.println("~CLIENT~");
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public void exit() {
//		super.exit(); // commented-out - prevents ESC from closing game
	}

	// Main
	public static void main(String args[]) {
		PApplet.main(SideScroller.class, args);
	}
}
