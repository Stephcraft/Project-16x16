package project_16x16;

import java.util.ArrayDeque;
import java.util.HashSet;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PSurface;
import processing.core.PVector;
import processing.event.MouseEvent;
import processing.javafx.PSurfaceFX;
import project_16x16.Options.Option;
import project_16x16.entities.Player;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.scene.AudioSettings;
import project_16x16.scene.ConfirmationMenu;
import project_16x16.scene.ControlsSettings;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;
import project_16x16.scene.GraphicsSettings;
import project_16x16.scene.MainMenu;
import project_16x16.scene.MultiplayerClientMenu;
import project_16x16.scene.MultiplayerHostMenu;
import project_16x16.scene.MultiplayerMenu;
import project_16x16.scene.PScene;
import project_16x16.scene.PauseMenu;
import project_16x16.scene.Settings;
import project_16x16.ui.MenuBackground;
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
	private PVector windowSize = new PVector(1280, 720); // Base window size, before display scaling
	/** Windowed-mode window (content) size, restored when leaving fullscreen. */
	private int windowedWidth = 1280, windowedHeight = 720;
	/**
	 * The game's logical resolution: all drawing and mouse coordinates are in these
	 * units, whatever the window size. Rendering is scaled up (or down) from it to
	 * the window's own resolution -- see {@link #scaleResolution()}.
	 */
	public PVector gameResolution = new PVector(1280, 720);
	/** Window pixels per game-resolution unit. */
	private float renderScale = 1;
	/** Offset (window pixels) of the game area within the window, if letterboxed. */
	private float renderOffsetX = 0, renderOffsetY = 0;
	/** Framerate target/cap; the actual framerate's limit. */
	public static float targetFramerate;
	// Font Resources
	private static PFont font_pixel;

	// Scenes
	private ArrayDeque<GameScenes> sceneHistory;
	/** When the scene last changed (game time, ms). */
	private long sceneSwapTime = 0;
	/** Minimum time between scene changes (ms) -- debounces repeated input. */
	private static final long SCENE_SWAP_DEBOUNCE_MILLIS = 100;
	private static final float SCENE_FADE_MILLIS = 230;

	private static MainMenu menu;
	private static GameplayScene game;
	private static PauseMenu pmenu;
	private static Settings settings;

	private static MultiplayerMenu mMenu;
	private static MultiplayerHostMenu mHostMenu;
	private static MultiplayerClientMenu mClientMenu;

	private static GraphicsSettings graphicsSettings;
	private static AudioSettings audioSettings;
	private static ControlsSettings controlsSettings;

	public enum GameScenes {
		MAIN_MENU(menu), GAME(game), PAUSE_MENU(pmenu), SETTINGS_MENU(settings), MULTIPLAYER_MENU(mMenu), HOST_MENU(mHostMenu), CLIENT_MENU(mClientMenu),
		GRAPHICS_SETTINGS(graphicsSettings), AUDIO_SETTINGS(audioSettings), CONTROLS_SETTINGS(controlsSettings), CONFIRMATION(null);

		PScene scene;

		private GameScenes(PScene scene) {
			this.scene = scene;
		}

		public PScene getScene() {
			return scene;
		}

		// confirmation menus ("are you sure...") can be created ad-hoc, unlike other
		// named scenes like "GAME"
		public static GameScenes makeConfirmation(ConfirmationMenu newScene) {
			GameScenes.CONFIRMATION.scene = newScene;
			return GameScenes.CONFIRMATION;
		}
	}

	// Events
	private HashSet<Integer> keysDown;
	public boolean keyPressEvent; // TODO remove -- override keyPressed() instead
	public boolean keyReleaseEvent; // TODO remove -- override mouseReleased() instead
	public boolean mousePressEvent; // TODO remove -- override mousePressed() instead
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
		stage.setResizable(false); // prevent arbitrary user resize
		stage.setFullScreenExitHint(""); // disable fullscreen toggle hint
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // prevent ESC toggling fullscreen
		scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
		scene.setFill(Color.BLACK); // letterbox bars
		// keep the game scaled to fill the window as its size changes (resize, fullscreen)
		scene.widthProperty().addListener(o -> scaleResolution());
		scene.heightProperty().addListener(o -> scaleResolution());
		stage.fullScreenProperty().addListener((o, wasFullscreen, isFullscreen) -> {
			if (!isFullscreen) { // restore the chosen window size (it may have changed while fullscreen)
				setWindowSize(windowedWidth, windowedHeight);
			}
		});

		if (Options.windowWidth > 0 && Options.windowHeight > 0) {
			setWindowSize(Options.windowWidth, Options.windowHeight);
		} else {
			Screen screen = Screen.getPrimary();
			double scaleX = screen.getOutputScaleX();
			double scaleY = screen.getOutputScaleY();
			double scale = Double.parseDouble(System.getProperty("ui.scale", String.valueOf(Math.min(scaleX, scaleY))));
			changeScale((float) scale);
		}
		if (Options.fullscreen) {
			setFullscreen(true);
		}

		return surface;
	}

	/**
	 * Passes JavaFX window closed call to game.
	 *
	 * @param event
	 */
	private void closeWindowEvent(WindowEvent event) {
		try {
			Audio.exit();
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

		// Load framerate target from user settings (default = 60)
		targetFramerate = Options.targetFrameRate;

		// Setup modes
		imageMode(CENTER);
		rectMode(CENTER);
		strokeCap(SQUARE);

		// create set to manage current key(s) pressed down
		keysDown = new HashSet<>();

		// Main Load
		load();
		Notifications.assignApplet(this);
		MenuBackground.assignApplet(this);
		Audio.assignApplet(this);

		// Create scenes
		sceneHistory = new ArrayDeque<>();
		game = new GameplayScene(this, Constants.DEV_LEVEL);
		menu = new MainMenu(this);
		pmenu = new PauseMenu(this);
		settings = new Settings(this);
		mMenu = new MultiplayerMenu(this);
		mHostMenu = new MultiplayerHostMenu(this);
		mClientMenu = new MultiplayerClientMenu(this);
		graphicsSettings = new GraphicsSettings(this);
		audioSettings = new AudioSettings(this);
		controlsSettings = new ControlsSettings(this);
		swapToScene(GameScenes.MAIN_MENU);

		// Init camera
		camera = new Camera(this);
		camera.setMouseMask(CONTROL);
		camera.setMinZoomScale(Constants.CAMERA_ZOOM_MIN);
		camera.setMaxZoomScale(Constants.CAMERA_ZOOM_MAX);
		camera.setCameraPositionNoLerp(game.getPlayer().position); // start on the player...
		camera.setFollowObject(game.getPlayer()); // ...and track them

		scaleResolution();
		launchIntoMultiplayer(); // multi is conditional on program args

		Time.reset(); // game time starts once loading is complete
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
	 * Use this method or {@link #returnScene()} to change the running game scene to
	 * a named scene.
	 *
	 * @param newScene
	 * @see #returnScene()
	 */
	public void swapToScene(GameScenes newScene) {
		final boolean inSetup = frameCount == 0; // no debounce while setting up
		if (inSetup || Time.millis() - sceneSwapTime > SCENE_SWAP_DEBOUNCE_MILLIS) {
			if (!newScene.equals(sceneHistory.peek())) { // if different
				if (!sceneHistory.isEmpty()) {
					sceneHistory.peek().getScene().switchFrom(); // switch from
				}
				sceneHistory.push(newScene);
				newScene.getScene().switchTo();
				sceneSwapTime = Time.millis();
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
			sceneSwapTime = Time.millis();
		}
	}

	/**
	 * @return true if the current scene belongs to a game in progress (such as the
	 *         pause menu, or settings opened from it), rather than to the main menu
	 */
	public boolean isGameInProgress() {
		for (GameScenes scene : sceneHistory) { // most recent first
			if (scene == GameScenes.MAIN_MENU) {
				return false;
			}
			if (scene == GameScenes.GAME || scene == GameScenes.PAUSE_MENU) {
				return true;
			}
		}
		return false;
	}

	/**
	 * draw is called once per frame and is the game loop. Any update or displaying
	 * functions should be called here.
	 */
	@Override
	public void draw() {
		Time.tick();
		frameRate(targetFramerate);

		// draw in game-resolution units, scaled to the window
		translate(renderOffsetX, renderOffsetY);
		scale(renderScale);
		final GraphicsContext context = canvas.getGraphicsContext2D();
		// keep the pixel-art world crisp when magnified; smooth it when shrunk (else it shimmers)
		context.setImageSmoothing(renderScale * camera.getZoomScale() < 1);
		camera.hook();
		drawBelowCamera();
		camera.release();
		context.setImageSmoothing(true); // UI text is drawn from bitmap glyphs, which need smoothing
		drawAboveCamera();
		drawLetterbox();

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
		if (sceneHistory.isEmpty()) {
			return;
		}
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
		if (sceneHistory.isEmpty()) {
			return;
		}
		sceneHistory.peek().getScene().drawUI();
		drawSceneFade();
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
	 * Blacks out the window outside the game area (when the window's aspect ratio
	 * differs from the game's), covering anything drawn beyond the game's edges.
	 */
	private void drawLetterbox() {
		if (renderOffsetX < 0.5f && renderOffsetY < 0.5f) {
			return;
		}
		pushStyle();
		resetMatrix(); // window pixels
		noStroke();
		fill(0);
		rectMode(CORNER);
		final float gameW = gameResolution.x * renderScale;
		final float gameH = gameResolution.y * renderScale;
		rect(0, 0, renderOffsetX, g.height); // left
		rect(renderOffsetX + gameW, 0, g.width, g.height); // right
		rect(0, 0, g.width, renderOffsetY); // top
		rect(0, renderOffsetY + gameH, g.width, g.height); // bottom
		popStyle();
	}

	/**
	 * Fades in from black after a scene change.
	 */
	private void drawSceneFade() {
		float progress = (Time.millis() - sceneSwapTime) / SCENE_FADE_MILLIS;
		if (progress >= 1 || progress < 0) {
			return;
		}
		pushStyle();
		noStroke();
		fill(0, 255 * (1 - progress));
		rectMode(CORNER);
		rect(0, 0, width, height);
		popStyle();
	}

	/**
	 * Captures what has been drawn so far this frame, at game resolution. Unlike
	 * {@link #get()}, this is the game area only (excluding any letterboxing),
	 * scaled from the window's resolution to game resolution.
	 */
	public PImage captureFrame() {
		final WritableImage snapshot = canvas.snapshot(null, null);
		final int w = Math.min((int) snapshot.getWidth(), Math.round(gameResolution.x * renderScale));
		final int h = Math.min((int) snapshot.getHeight(), Math.round(gameResolution.y * renderScale));
		final PImage frame = createImage(w, h, ARGB);
		frame.loadPixels();
		snapshot.getPixelReader().getPixels(Math.round(renderOffsetX), Math.round(renderOffsetY), w, h, PixelFormat.getIntArgbInstance(), frame.pixels, 0,
				w);
		frame.updatePixels();
		if (w != width || h != height) {
			frame.resize(width, height);
		}
		return frame;
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
	 * FOR GLOBAL KEYS ONLY (dev tools mostly for now...)
	 */
	@Override
	public void keyReleased(processing.event.KeyEvent event) {
		keysDown.remove(event.getKeyCode());
		keyReleaseEvent = true;

		final int keyCode = event.getKeyCode();
		if (keyCode == Options.frameRateHighKey) {
			targetFramerate = 1000;
		} else if (keyCode == Options.frameRateLowKey) {
			targetFramerate = 20;
		} else if (keyCode == Options.frameRateDefaultKey) {
			targetFramerate = 60;
		} else if (keyCode == Options.toggleDeadzoneKey) {
			camera.toggleDeadZone();
		} else if (keyCode == Options.cameraToMouseKey) {
			camera.setCameraPosition(camera.getMouseCoord());
		} else if (keyCode == Options.cameraToPlayerKey) {
			camera.setFollowObject(game.getPlayer());
			camera.setZoomScale(1.0f);
		} else if (keyCode == Options.cameraShakeKey) {
			camera.shake(0.4f);
		} else if (keyCode == Options.notifyKey) {
			Notifications.addNotification("Hello", "World");
		} else if (keyCode == Options.toggleFullscreenKey) {
			Options.fullscreen = !isFullscreen();
			Options.save(Option.FULLSCREEN, Options.fullscreen);
			setFullscreen(Options.fullscreen);
		} else if (keyCode == Options.toggleDebugKey) {
			debug = debug.next();
			Options.save(Option.DEBUG_MODE, debug.ordinal());
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
	 * Converts mouse positions from window pixels to game-resolution units, so
	 * mouseX/mouseY (and the events scenes receive) match what is drawn.
	 */
	@Override
	protected void handleMouseEvent(MouseEvent e) {
		final int x = (int) Math.floor((e.getX() - renderOffsetX) / renderScale);
		final int y = (int) Math.floor((e.getY() - renderOffsetY) / renderScale);
		super.handleMouseEvent(new MouseEvent(e.getNative(), e.getMillis(), e.getAction(), e.getModifiers(), x, y, e.getButton(), e.getCount()));
	}

	/**
	 * Handles scrolling events.
	 */
	@Override
	public void mouseWheel(MouseEvent event) {
		game.mouseWheel(event);
		if (game.isZoomable()) {
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

	public boolean isFullscreen() {
		return stage.isFullScreen();
	}

	/**
	 * Enters or leaves fullscreen mode, once the current frame has finished.
	 */
	public void setFullscreen(boolean fullscreen) {
		afterFrame(() -> stage.setFullScreen(fullscreen));
	}

	/** @return the width of the window in windowed mode (px) */
	public int getWindowWidth() {
		return windowedWidth;
	}

	/** @return the height of the window in windowed mode (px) */
	public int getWindowHeight() {
		return windowedHeight;
	}

	/**
	 * Sets the size of the window's content area in windowed mode, shrinking it
	 * (keeping its aspect ratio) if needed to fit the screen. If the game is
	 * fullscreen, the size applies once it leaves fullscreen. The game is scaled
	 * to fill the window. Takes effect once the current frame has finished.
	 */
	public void setWindowSize(int width, int height) {
		windowedWidth = width;
		windowedHeight = height;
		afterFrame(() -> {
			if (stage.isFullScreen()) {
				return;
			}
			Rectangle2D screen = Screen.getPrimary().getVisualBounds();
			// window decorations (titlebar/borders) are unscaled and unknown until shown
			double decoW = Double.isNaN(stage.getWidth()) ? 0 : stage.getWidth() - scene.getWidth();
			double decoH = Double.isNaN(stage.getHeight()) ? 0 : stage.getHeight() - scene.getHeight();
			double fit = Math.min(1, Math.min((screen.getWidth() - decoW) / width, (screen.getHeight() - decoH) / height));
			stage.setWidth(Math.round(width * fit) + decoW);
			stage.setHeight(Math.round(height * fit) + decoH);
			stage.centerOnScreen();
		});
	}

	/**
	 * Runs a change to the window after the current frame (or once the window is
	 * first shown). Changing the window can process further frames before
	 * returning, and Processing exits if a frame starts within another frame.
	 */
	private void afterFrame(Runnable action) {
		if (stage.isShowing()) {
			Platform.runLater(action);
			return;
		}
		stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				stage.removeEventHandler(WindowEvent.WINDOW_SHOWN, this);
				action.run();
			}
		});
	}

	public void resizeGameResolution(int width, int height) {
		gameResolution = new PVector(width, height);
		scaleResolution();
	}

	/**
	 * Fits the game to the current window: the canvas takes the window's full
	 * resolution, and drawing is scaled from game resolution to fit it (see
	 * {@link #draw()}), preserving the aspect ratio and letterboxing any spare
	 * space. Rendering at the window's resolution (rather than rendering at game
	 * resolution and scaling the image) keeps large windows sharp. Called
	 * automatically whenever the window size changes.
	 */
	private void scaleResolution() {
		if (canvas == null || scene == null || scene.getWidth() <= 0 || scene.getHeight() <= 0) {
			return;
		}
		final int w = (int) Math.round(scene.getWidth());
		final int h = (int) Math.round(scene.getHeight());
		canvas.getTransforms().clear();
		canvas.setTranslateX(0);
		canvas.setTranslateY(0);
		canvas.setWidth(w);
		canvas.setHeight(h);
		// Processing resizes the sketch along with the canvas, but sketch code works
		// in game-resolution units -- only the renderer covers the whole canvas
		g.setSize(w, h);
		width = (int) gameResolution.x;
		height = (int) gameResolution.y;
		renderScale = Math.min(w / gameResolution.x, h / gameResolution.y);
		renderOffsetX = (w - gameResolution.x * renderScale) / 2;
		renderOffsetY = (h - gameResolution.y * renderScale) / 2;
	}

	/**
	 * Sizes the window from the base window size and a display scale factor (akin
	 * to the 'glass.win.uiScale' system property), shrinking it if needed to fit
	 * the screen.
	 */
	private void changeScale(float newScale) {
		Options.uiScale = newScale;
		setWindowSize(Math.round(windowSize.x * newScale), Math.round(windowSize.y * newScale));
	}

	private void displayDebugInfo() {
		pushStyle();
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

		fill(255, 255, 0);
		textAlign(RIGHT, TOP);
		text("[" + round(player.position.x) + ", " + round(player.position.y) + "]", width - ip, lineOffset * 0 + yOffset);
		text("[" + round(velocity.x) + ", " + round(velocity.y) + "]", width - ip, lineOffset * 1 + yOffset);
		text("[" + player.animation.name + "]", width - ip, lineOffset * 2 + yOffset);
		text("[" + round(player.animation.getFrameID()) + " / " + player.animation.getAnimLength() + "]", width - ip, lineOffset * 3 + yOffset);
		text("[" + PApplet.round(camera.getPosition().x) + ", " + PApplet.round(camera.getPosition().y) + "]", width - ip, lineOffset * 4 + yOffset);
		text("[" + String.format("%.2f", camera.getZoomScale()) + "]", width - ip, lineOffset * 5 + yOffset);
		text("[" + round(degrees(camera.getCameraRotation())) + "]", width - ip, lineOffset * 6 + yOffset);
		text("[" + round(camera.getMouseCoord().x) + ", " + round(camera.getMouseCoord().y) + "]", width - ip, lineOffset * 7 + yOffset);
		text("[" + "?" + "]", width - ip, lineOffset * 8 + yOffset); // TODO expose

		text("['" + (char) Options.frameRateHighKey + "']", width - ip, lineOffset * 12 + yOffset);
		text("['" + (char) Options.frameRateLowKey + "']", width - ip, lineOffset * 13 + yOffset);
		text("['" + (char) Options.toggleDeadzoneKey + "']", width - ip, lineOffset * 14 + yOffset);
		text("['" + (char) Options.cameraToMouseKey + "']", width - ip, lineOffset * 15 + yOffset);
		text("['" + (char) Options.cameraToPlayerKey + "']", width - ip, lineOffset * 16 + yOffset);
		text("['" + (char) Options.cameraShakeKey + "']", width - ip, lineOffset * 17 + yOffset);
		text("['" + (char) Options.notifyKey + "']", width - ip, lineOffset * 18 + yOffset);
		text("['" + (char) Options.lifeCapIncreaseKey + "']", width - ip, lineOffset * 19 + yOffset);
		text("['" + (char) Options.lifeCapDecreaseKey + "']", width - ip, lineOffset * 20 + yOffset);
		text("['" + (char) Options.lifeIncreaseKey + "']", width - ip, lineOffset * 21 + yOffset);
		text("['" + (char) Options.lifeDecreaseKey + "']", width - ip, lineOffset * 22 + yOffset);
		text("['F11']", width - ip, lineOffset * 23 + yOffset);
		text("['TAB']", width - ip, lineOffset * 24 + yOffset);

		if (frameRate >= targetFramerate - 0.5f) {
			fill(0, 255, 0);
		} else {
			fill(255, 0, 0);
		}
		text("[" + round(frameRate) + "]", width - ip, lineOffset * 9 + yOffset);
		popStyle();
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
		/*
		 * Set DPI scaling to 1 here, but manually change using changeScale() later so
		 * we have direct control over it.
		 */
		System.setProperty("glass.win.uiScale", "100%");
		PApplet.main(SideScroller.class, args);
	}
}
