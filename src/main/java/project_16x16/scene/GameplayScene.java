package project_16x16.scene;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Audio.BGM;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.Tileset;
import project_16x16.Time;
import project_16x16.Utility;
import project_16x16.entities.Player;
import project_16x16.multiplayer.Multiplayer;
import project_16x16.objects.BackgroundObject;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.objects.EditorItem;
import project_16x16.objects.GameObject;
import project_16x16.projectiles.ProjectileObject;
import project_16x16.scene.gameplaymodes.GameplayMode;
import project_16x16.scene.gameplaymodes.ImportGameMode;
import project_16x16.scene.gameplaymodes.InventoryGameMode;
import project_16x16.scene.gameplaymodes.LoadExampleGameMode;
import project_16x16.scene.gameplaymodes.ModifyGameMode;
import project_16x16.scene.gameplaymodes.MoveGameMode;
import project_16x16.scene.gameplaymodes.PlayGameMode;
import project_16x16.scene.gameplaymodes.SaveGameMode;
import project_16x16.scene.gameplaymodes.TestGameMode;
import project_16x16.ui.Tab;
import project_16x16.windows.ImportLevelWindow;
import project_16x16.windows.LoadLevelWindow;
import project_16x16.windows.SaveLevelWindow;

/**
 * Gameplay Scene. Both the level editor and gameplay.
 */
public class GameplayScene extends PScene {

	// Singleplayer
	private boolean isSingleplayer = true; // true by default

	// Multiplayer
	private Multiplayer multiplayer;

	// Mode icon layout (screen space)
	private static final int ICON_X = 42; // centre of the first icon
	private static final int ICON_Y = 36;
	private static final int ICON_PITCH = 48;
	private static final int ICON_SIZE = 36;
	/** Extent of the mode icons, from the top-left corner. */
	private static final int ICONS_RIGHT = 220, ICONS_BOTTOM = 60;

	private static final int EDITOR_ACCENT = 0xFFFFB84D;

	private final String levelString;

	// Graphics Icon
	private PImage iconModify;
	private PImage iconInventory;
	private PImage iconPlay;
	private PImage iconSave;
	private PImage iconModifyActive;
	private PImage iconInventoryActive;
	private PImage iconPlayActive;
	private PImage iconSaveActive;

	public ArrayList<ProjectileObject> projectileObjects; // TODO working?
	public ArrayList<EditableObject> objects;

	// Windows
	private SaveLevelWindow windowSaveLevel;
	private ImportLevelWindow windowImportlevel;
	// private TestWindow window_test;
	private LoadLevelWindow windowLoadLevel;

	// Tabs
	private Tab windowTabs;
	// Each button id corresponds with its string id: ex) load = 0, save = 1, etc.
	String[] tabTexts = new String[] { "load", "save", "import" };

	// Camera Zoom State
	private boolean zoomable = true; // Camera can zoom by default

	// Editor Item
	private EditorItem editorItem;

	private HashMap<GameModes, GameplayMode> modesMap;

	public enum GameModes {
		MODIFY, PLAY, INVENTORY, SAVE, IMPORT, LOADEXAMPLE, MOVE, TEST,
	}

	public GameplayMode currentMode;

	public EditableObject focusedObject = null;

	public boolean edit;

	/**
	 * Set when the game should pause at the end of this frame's UI (so the pause
	 * backdrop captures the frame without debug/notification overlays).
	 */
	private boolean pauseRequested;

	/**
	 * Local Player
	 */
	private Player localPlayer;
	/**
	 * Other Player (multiplayer)
	 */
	private Player onlinePlayer;

	private PVector mouseDown, origPos;

	private SelectionBox selectionBox;

	public GameplayScene(SideScroller sideScroller, String levelString) {
		super(sideScroller);
		this.levelString = levelString;
		setup();
	}

	private void setup() {
		projectileObjects = new ArrayList<>();

		objects = new ArrayList<>();

		// Init Editor Components
		editorItem = new EditorItem(applet, this);

		// Get Icon Graphics
		iconModify = Tileset.getTile(279, 301, 9, 9, 4);
		iconInventory = Tileset.getTile(289, 301, 9, 9, 4);
		iconPlay = Tileset.getTile(298, 301, 9, 9, 4);
		iconSave = Tileset.getTile(307, 301, 9, 9, 4);

		iconModifyActive = Tileset.getTile(279, 291, 9, 9, 4);
		iconInventoryActive = Tileset.getTile(289, 291, 9, 9, 4);
		iconPlayActive = Tileset.getTile(298, 291, 9, 9, 4);
		iconSaveActive = Tileset.getTile(307, 291, 9, 9, 4);

		// Init Window
		windowSaveLevel = new SaveLevelWindow(applet, this);
		// Import Window
		windowImportlevel = new ImportLevelWindow(applet, this);
//		window_test = new TestWindow(applet);
		windowLoadLevel = new LoadLevelWindow(applet, this);

		// Init Player
		localPlayer = new Player(applet, this, false);
		localPlayer.position.set(0, -100); // TODO spawn location

		// GameplayModes initialization
		modesMap = new HashMap<>();
		modesMap.put(GameModes.MODIFY, new ModifyGameMode(this, editorItem));
		modesMap.put(GameModes.PLAY, new PlayGameMode(this, localPlayer));
		modesMap.put(GameModes.INVENTORY, new InventoryGameMode(this, editorItem));
		modesMap.put(GameModes.SAVE, new SaveGameMode(this));
		modesMap.put(GameModes.IMPORT, new ImportGameMode(this));
		modesMap.put(GameModes.LOADEXAMPLE, new LoadExampleGameMode(this));
		modesMap.put(GameModes.MOVE, new MoveGameMode(this));
		modesMap.put(GameModes.TEST, new TestGameMode(this));

		currentMode = modesMap.get(GameModes.MODIFY);

		loadLevel(levelString); // TODO change level

		windowTabs = new Tab(applet, tabTexts, tabTexts.length);
	}

	@Override
	public void switchTo() {
		super.switchTo();
		Audio.play(BGM.TEST1);
	}

	/**
	 * Draw scene elements that are below (affected by) the camera.
	 */
	@Override
	public void draw() {
		background(23, 26, 36);

		currentMode.displayWorldEdit();
		for (EditableObject o : objects) {
			currentMode.updateEditableObject(o);
			o.display();
		}

		// View Projectiles
		Iterator<ProjectileObject> i = projectileObjects.iterator();
		while (i.hasNext()) {
			ProjectileObject o = i.next();
			if (Time.millis() - o.spawnTime > ProjectileObject.LIFETIME_MILLIS) {
				i.remove();
			} else {
				o.update();
				o.display();
			}
		}

		currentMode.displayDestination();
		drawPlayer();
	}

	/**
	 * Call when host/connect buttons pressed.
	 *
	 * @param multiplayer multiplayer client
	 */
	public void setupMultiplayer(Multiplayer multiplayer) {
		this.multiplayer = multiplayer;
		onlinePlayer = new Player(applet, (GameplayScene) GameScenes.GAME.getScene(), true);
		isSingleplayer = false;
	}

	public void setSingleplayer(boolean value) {
		this.isSingleplayer = value;
	}

	/**
	 * Draws and updates the player.
	 */
	private void drawPlayer() {
		currentMode.updateLocalPlayer(localPlayer);
		if (!isSingleplayer) {
			JSONObject data = new JSONObject();
			data.setFloat("x", localPlayer.position.x);
			data.setFloat("y", localPlayer.position.y);
			data.setInt("dir", localPlayer.getState().facingDir);
			data.setString("animSequence", localPlayer.animation.name);
			data.setInt("animFrame", localPlayer.animation.getFrameID());
			multiplayer.writeData(data.toString()); // write data to server

			JSONObject other = multiplayer.readData(); // read from server & display other player
			if (other != null) {
				onlinePlayer.position.x = other.getFloat("x");
				onlinePlayer.position.y = other.getFloat("y");
				onlinePlayer.setAnimation(other.getString("animSequence"));
				onlinePlayer.animation.setFrame(other.getInt("animFrame"));
				onlinePlayer.getState().facingDir = other.getInt("dir");
				onlinePlayer.display();
			}
		}
		localPlayer.display();
	}

	/**
	 * Draw scene elements that are above the camera.
	 */
	@Override
	public void drawUI() {
		if (currentMode.getModeType() != GameModes.PLAY) {
			displayEditorFrame();
		}

		// GUI Icons
		displayModeIcon(0, iconModifyActive, iconModify, GameModes.MODIFY);
		displayModeIcon(1, iconInventoryActive, iconInventory, GameModes.INVENTORY);
		displayModeIcon(2, iconPlayActive, iconPlay, GameModes.PLAY);
		displayModeIcon(3, iconSaveActive, iconSave, GameModes.SAVE);

		currentMode.updateGUI();
		if (selectionBox != null) {
			selectionBox.draw();
		}

		if (pauseRequested) {
			pauseRequested = false;
			((PauseMenu) GameScenes.PAUSE_MENU.getScene()).setBackdrop(applet.captureFrame());
			applet.swapToScene(GameScenes.PAUSE_MENU);
		}
	}

	private void displayModeIcon(int index, PImage activeIcon, PImage inactiveIcon, GameModes mode) {
		final int x = ICON_X + index * ICON_PITCH;
		currentMode.updateGUIButton(x, ICON_Y, activeIcon, inactiveIcon, mode, Utility.hoverScreen(x, ICON_Y, ICON_SIZE, ICON_SIZE));
	}

	/**
	 * Frames the screen and labels it, so it's obvious the level editor (rather
	 * than the game) is running.
	 */
	private void displayEditorFrame() {
		applet.pushStyle();
		applet.rectMode(CORNER);
		applet.noFill();
		applet.stroke(EDITOR_ACCENT);
		applet.strokeWeight(4);
		applet.rect(2, 2, applet.width - 4, applet.height - 4);

		final String label = "EDIT MODE";
		final String hint = "press 3 to play";
		applet.textSize(22);
		final float labelWidth = applet.textWidth(label);
		applet.textSize(18);
		final float w = labelWidth + applet.textWidth(hint) + 48;
		final float h = 34;
		final float x = Math.max(applet.width / 2f - w / 2, ICONS_RIGHT + 16); // clear of the icons
		applet.noStroke();
		applet.fill(EDITOR_ACCENT);
		applet.rect(x, 0, w, h, 0, 0, 8, 8);
		applet.textAlign(LEFT, CENTER);
		applet.fill(29, 33, 45);
		applet.textSize(22);
		applet.text(label, x + 16, h / 2 - 3);
		applet.fill(29, 33, 45, 170);
		applet.textSize(18);
		applet.text(hint, x + 32 + labelWidth, h / 2 - 3);
		applet.popStyle();
	}

	/**
	 * Display boundaries of all world objects.
	 */
	@Override
	public void debug() {
		if (pauseRequested) {
			return; // keep debug outlines out of the pause backdrop
		}
		for (EditableObject o : objects) {
			o.debug();
		}
		for (ProjectileObject o : projectileObjects) {
			o.debug();
		}
	}

	public Player getPlayer() {
		return localPlayer;
	}

	/**
	 * Close server/client connections.
	 */
	public void exit() {
		if (!isSingleplayer) {
			multiplayer.exit();
		}
	}

	private void displayGrid() {// world edit grid
		applet.strokeWeight(1);
		applet.stroke(0, 155, 155);
		final int xOffset = 32; // to align with rectMode(CENTER)
		final int yOffset = 32; // to align with rectMode(CENTER)
		final int l = 6400;
		for (int i = -l; i < l; i += SideScroller.snapSize) {
			applet.line(-l, i + yOffset, l, i + yOffset); // horizontal
			applet.line(i + xOffset, -l, i + xOffset, l); // vertical
		}
	}

	public boolean isZoomable() {
		return zoomable && !isOverUI(applet.getMouseCoordScreen());
	}

	@Override
	void mousePressed(MouseEvent e) {
		origPos = applet.camera.getPosition(); // used for camera panning
		mouseDown = applet.getMouseCoordScreen();
		switch (e.getButton()) {
			case LEFT:
				boolean overAny = false;
				for (EditableObject o : objects) {
					if (o.isFocused()) {
						o.focus(); // refocus multi-select objects (edit offset)
					}
					if (o.mouseHover()) {
						o.focus();
						overAny = true;
					}
				}
				if (!overAny) { // if not over any, deselect all
					objects.forEach(o -> o.unFocus());
				}
				break;
			case RIGHT:
				if (currentMode.allowsWorldEditing() && !isOverUI(mouseDown)) {
					selectionBox = new SelectionBox(mouseDown);
				}
				break;
			default:
				break;
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
			case LEFT:
				if (currentMode.allowsWorldEditing()) {
					endDrag();
				}
				break;
			case RIGHT:
				selectionBox = null;
				break;
			default:
				break;
		}
	}

	@Override
	void mouseDragged(MouseEvent e) {
		currentMode.mouseDraggedEvent(e, origPos, mouseDown);
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		if (event.isShiftDown()) {
		} else {
			currentMode.mouseWheelEvent(event);
		}
	}

	@Override
	protected void keyReleased(processing.event.KeyEvent e) {
		final int keyCode = e.getKeyCode();
		if (keyCode == PConstants.ESC) {
			if (currentMode.getModeType() == GameModes.INVENTORY) {
				changeMode(GameModes.MODIFY); // close the palette
			} else {
				pauseRequested = true;
			}
		} else if (keyCode == Options.lifeCapIncreaseKey) {
			localPlayer.lifeCapacity++;
		} else if (keyCode == Options.lifeCapDecreaseKey) {
			localPlayer.lifeCapacity--;
		} else if (keyCode == Options.lifeIncreaseKey) {
			localPlayer.life++;
		} else if (keyCode == Options.lifeDecreaseKey) {
			localPlayer.life--;
		}

		currentMode.keyReleasedEvent(e);
	}

	public void switchModeOnKeyEvent(processing.event.KeyEvent event) {
		editorItem.focus = false;
		switch (event.getKeyCode()) {
			case 49: // 1
				changeMode(GameModes.MODIFY);
				break;
			case 50: // 2
			case 69: // 'e'
				if (currentMode.getModeType() == GameModes.INVENTORY) {
					changeMode(GameModes.MODIFY);
				} else {
					changeMode(GameModes.INVENTORY);
				}
				break;
			case 51: // 3
				changeMode(GameModes.PLAY);
				break;
			case 52: // 4
				changeMode(GameModes.SAVE);
				break;
			case 54: // 6
				changeMode(GameModes.IMPORT);
				break;
			case 8: // BACKSPACE
			case 46: // DEL
				objects.stream().filter(EditableObject::isFocused).toList().forEach(this::removeObject);
				break;
			default:
				break;
		}
	}

	/**
	 * Saves the level (background, game and collideable objects), encrypting the
	 * output.
	 *
	 * @param path Save location path.
	 */
	public void saveLevel(String path) {
		JSONArray data = new JSONArray();

		JSONObject main = new JSONObject();
		main.setString("title", "undefined");
		main.setString("creator", "undefined");
		main.setString("version", "alpha 1.0.0");
		data.append(main); // Add Main
		for (EditableObject o : objects) {
			if (!(o instanceof ProjectileObject)) {
				data.append(o.exportToJSON());
			}
		}
		Utility.saveFile(path, Utility.encrypt(data.toString()));
	}

	public void loadLevel(String path) {
		// TODO save camera position/settings.

		String[] script = applet.loadStrings(path);
		if (script == null) {
			return;
		}

		String scriptD = Utility.decrypt(PApplet.join(script, "\n")); // decrypt save data
		JSONArray data = JSONArray.parse(scriptD); // Parse JSON
		if (data == null) {
			System.err.println("Failed to parse level data to JSON. File is probably corrupt.");
			return;
		}

		// Clear Object Arrays
		objects.clear(); // TODO reset method

		// Create Level
		for (int i = 0; i < data.size(); i++) {
			JSONObject item = data.getJSONObject(i);

			String type = item.getString("type");
			if (type == null) {
				continue;
			}
			switch (type) { // Read Main
				case "COLLISION":
					CollidableObject collision = new CollidableObject(applet, this);
					try {
						collision.setGraphic(item.getString("id"));
					} catch (Exception e) {
						collision.width = 64;
						collision.height = 64;
					}
					collision.position.x = item.getInt("x");
					collision.position.y = item.getInt("y");

					objects.add(collision); // SideScrollerend To Level
					break;
				case "BACKGROUND":
					BackgroundObject backgroundObject = new BackgroundObject(applet, this);
					backgroundObject.setGraphic(item.getString("id"));
					backgroundObject.position.x = item.getInt("x");
					backgroundObject.position.y = item.getInt("y");

					objects.add(backgroundObject); // SideScrollerend To Level
					break;
				case "OBJECT":
					try {
						Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(item.getString("id"));
						Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
						GameObject gameObject = (GameObject) ctor.newInstance(new Object[] { applet, this });
						gameObject.position.x = item.getInt("x");
						gameObject.position.y = item.getInt("y");

						objects.add(gameObject); // SideScrollerend To Level
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	}

	public void displayWorldEdit() {
		displayGrid();
		if (applet.mousePressEvent && focusedObject != null) {
			focusedObject.updateEdit(); // enforce one item selected at once
		}
	}

	/**
	 * @return whether the screen position is over editor/game UI (rather than the
	 *         level)
	 */
	public boolean isOverUI(PVector screen) {
		return (screen.x < ICONS_RIGHT && screen.y < ICONS_BOTTOM) || currentMode.isOverUI(screen);
	}

	/**
	 * Solid objects (blocks and game objects) can't overlap each other in the
	 * editor. Child collision boxes are skipped: they share their parent's
	 * bounds.
	 */
	private static boolean isSolid(EditableObject o) {
		return (o instanceof CollidableObject || o instanceof GameObject) && !o.child;
	}

	/**
	 * Whether a solid object with the given (world space, centred) bounds would
	 * overlap no solid objects (besides those ignored). Touching edges don't count
	 * as overlapping.
	 */
	public boolean isAreaFree(float x, float y, float w, float h, Predicate<EditableObject> ignore) {
		for (EditableObject o : objects) {
			if (isSolid(o) && !ignore.test(o) && Math.abs(x - o.position.x) * 2 < w + o.width && Math.abs(y - o.position.y) * 2 < h + o.height) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return whether the (solid) object overlaps a solid object that isn't
	 *         selected along with it
	 */
	public boolean isBlocked(EditableObject o) {
		return isSolid(o) && !isAreaFree(o.position.x, o.position.y, o.width, o.height, EditableObject::isFocused);
	}

	/**
	 * Ends a drag of the selected objects. If any of them was dropped over another
	 * solid object, the whole selection returns to where the drag started
	 * (objects duplicated during the drag are discarded).
	 */
	private void endDrag() {
		final List<EditableObject> dragged = objects.stream().filter(o -> o.isFocused() && !o.child).toList();
		if (dragged.stream().noneMatch(this::isBlocked)) {
			return;
		}
		for (EditableObject o : dragged) {
			if (o.dragOrigin == null) {
				removeObject(o);
			} else {
				o.position.set(o.dragOrigin);
			}
		}
	}

	/**
	 * Removes an object from the level, along with its collision box (if any).
	 */
	private void removeObject(EditableObject o) {
		objects.remove(o);
		if (o instanceof GameObject g && g.collision != null) {
			objects.remove(g.collision);
		}
	}

	public void changeMode(GameModes mode) {
		currentMode = modesMap.get(mode);
		currentMode.enter();
	}

	public void setZoomable(boolean value) {
		zoomable = value;
	}

	public Tab getWindowTabs() {
		return windowTabs;
	}

	public SaveLevelWindow getWindowSaveLevel() {
		return windowSaveLevel;
	}

	public ImportLevelWindow getWindowImportLevel() {
		return windowImportlevel;
	}

	public LoadLevelWindow getWindowLoadLevel() {
		return windowLoadLevel;
	}

	/**
	 *
	 * @author micycle1
	 *
	 */
	private class SelectionBox {

		private final PVector startPosScreen, startPosGame;

		private SelectionBox(PVector startPos) {
			startPosScreen = startPos;
			startPosGame = applet.camera.getDispToCoord(startPosScreen);
		}

		private void draw() {
			PVector endPos = applet.getMouseCoordScreen();
			applet.stroke(255, 20, 147);
			applet.strokeWeight(3);
			applet.line(startPosScreen.x, startPosScreen.y, startPosScreen.x, endPos.y);
			applet.line(startPosScreen.x, startPosScreen.y, endPos.x, startPosScreen.y);
			applet.line(endPos.x, startPosScreen.y, endPos.x, endPos.y);
			applet.line(startPosScreen.x, endPos.y, endPos.x, endPos.y);
			for (EditableObject o : objects) {
				if (Utility.withinRegion(o.position, startPosGame, applet.getMouseCoordGame())) {
					o.focus();
				} else {
					o.unFocus();
				}
			}
		}
	}

}
