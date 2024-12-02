package project_16x16.scene;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import project_16x16.Audio.BGM;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
import project_16x16.Tileset;
import project_16x16.Utility;
import project_16x16.components.Tile;
import project_16x16.components.Tile.TileType;
import project_16x16.entities.Player;
import project_16x16.factory.AudioFactory;
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
import project_16x16.ui.Anchor;
import project_16x16.ui.ScrollBarVertical;
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

	// Graphics Slots
	private PImage slot;
	private PImage slotEditor;

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

	// Scroll Bar
	private ScrollBarVertical scrollBar;

	private HashMap<GameModes, GameplayMode> modesMap;

	public enum GameModes {
		MODIFY, PLAY, INVENTORY, SAVE, IMPORT, LOADEXAMPLE, MOVE, TEST,
	}

	public GameplayMode currentMode;

	private ArrayList<String> inventory;

	public EditableObject focusedObject = null;

	public boolean edit;

	private int scrollInventory;

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
		projectileObjects = new ArrayList<ProjectileObject>();

		objects = new ArrayList<EditableObject>();

		// Create Inventory
		inventory = new ArrayList<String>();
		inventory.add("Metal");
		inventory.add("Metal_Walk_Left:0");
		inventory.add("Metal_Walk_Middle:0");
		inventory.add("Metal_Walk_Middle:1");
		inventory.add("Metal_Walk_Right:0");
		inventory.add("XBox");

		// Init Editor Components
		editorItem = new EditorItem(applet, this);

		// Get Slots Graphics
		slot = Tileset.getTile(289, 256, 20, 21, 4);
		slotEditor = Tileset.getTile(310, 256, 20, 21, 4);

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

		// Init ScollBar
		Anchor scrollBarAnchor = new Anchor(applet, -20, 102, 20, 50);
		scrollBarAnchor.anchorOrigin = Anchor.AnchorOrigin.TopRight;
		scrollBarAnchor.stretch = Anchor.Stretch.Vertical;
		scrollBar = new ScrollBarVertical(scrollBarAnchor);
		scrollBar.setBarRatio(getBarRatio(getTotalInventoryItems() / 6, 50, 3f));

		// Init Player
		localPlayer = new Player(applet, this, false);
		localPlayer.position.set(0, -100); // TODO spawn location

		// GameplayModes initialization
		modesMap = new HashMap<>();
		modesMap.put(GameModes.MODIFY, new ModifyGameMode(this, editorItem));
		modesMap.put(GameModes.PLAY, new PlayGameMode(this, localPlayer));
		modesMap.put(GameModes.INVENTORY, new InventoryGameMode(this));
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
		((PauseMenu) GameScenes.PAUSE_MENU.getScene()).switched = false;
		AudioFactory.getInstance().play(BGM.TEST1);
	}

	/**
	 * Draw scene elements that are below (affected by) the camera.
	 */
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
			if (applet.frameCount - o.spawnTime > 600) {
				i.remove(); // kill projectile after 10s
			}
			else {
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
	public void drawUI() {
		currentMode.displayGUISlots();

		int xAnchor = 42;
		int offset = 48;
		// GUI Icons
		currentMode.updateGUIButton(xAnchor, iconModifyActive, iconModify, GameModes.MODIFY, Utility.hoverScreen(xAnchor, 120, 36, 36));
		currentMode.updateGUIButton(xAnchor + offset, iconInventoryActive, iconInventory, GameModes.INVENTORY, Utility.hoverScreen(xAnchor + offset, 120, 36, 36));
		currentMode.updateGUIButton(xAnchor + offset * 2, iconPlayActive, iconPlay, GameModes.PLAY, Utility.hoverScreen(xAnchor + offset * 2, 120, 36, 36));
		currentMode.updateGUIButton(xAnchor + offset * 3, iconSaveActive, iconSave, GameModes.SAVE, Utility.hoverScreen(xAnchor + offset * 3, 120, 36, 36));

		currentMode.updateGUI();
		if (selectionBox != null) {
			selectionBox.draw();
		}
	}

	/**
	 * Display boundaries of all world objects.
	 */
	public void debug() {
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

	public void displayCreativeInventory() {
		// complete creative inventory

		// Display Background
		applet.stroke(50);
		applet.fill(0, 100);
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);

		// Display Editor Mode Items
		int x = 0;
		int y = 1;
		int index = 0;
		TileType[] tiles = { TileType.COLLISION, TileType.BACKGROUND, TileType.OBJECT };
		ArrayList<Tile> inventoryTiles = Tileset.getAllTiles(tiles);
		for (Tile tile : inventoryTiles) {
			PImage img = tile.getPImage();
			if (index % 6 == 0) { // show 6 items per row
				x = 0;
				y++;
			}
			else {
				x++;
			}
			applet.image(slotEditor, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scrollInventory);
			if (img.width > 20 * 4 || img.height > 20 * 4) {
				applet.image(img, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scrollInventory, img.width / 4, img.height / 4);
			}
			else {
				applet.image(img, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scrollInventory, img.width / 2, img.height / 2);
			}

			// Detect hover over item
			float xx = 20 * 4 / 2 + 10 + x * (20 * 4 + 10);
			float yy = y * (20 * 4 + 10) + scrollInventory;
			if (applet.getMouseCoordScreen().y > 100) {
				if (applet.getMouseCoordScreen().x > xx - (20 * 4) / 2 && applet.getMouseCoordScreen().x < xx + (20 * 4) / 2 && applet.getMouseCoordScreen().y > yy - (20 * 4) / 2 && applet.getMouseCoordScreen().y < yy + (20 * 4) / 2) {
					// Grab Item
					if (applet.mousePressEvent) {
						editorItem.focus = true;
						editorItem.setTile(tile.getName());
					}
					// Display item name
					applet.textSize(20);
					applet.fill(255);
					applet.text(tile.getName(), applet.mouseX, applet.mouseY);
				}
			}
			index++;
		}

		// Display ScrollBar
		scrollBar.display();
		scrollBar.update();
		scrollInventory = (int) PApplet.map(scrollBar.barLocation, 1, 0, -getInventorySize() + applet.height - 8, 0);

		// Display Top Bar TODO
//		applet.noStroke();
//		applet.fill(29, 33, 45);
//		applet.rect(applet.width / 2, 50, applet.width, 100);

		// Display Line Separator
		applet.strokeWeight(4);
		applet.stroke(74, 81, 99);
		applet.line(0, 100, applet.width, 100);

		// Display Inventory Slots
		for (int i = 0; i < 6; i++) {
			// Display Slot
			image(slot, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10);

			// Display Item
			PImage img = Tileset.getTile(inventory.get(i));
			applet.image(img, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10, img.width * (float) 0.5, img.height * (float) 0.5);

			// Focus Event
			if (applet.mouseReleaseEvent) {
				float xx = 20 * 4 / 2 + 10 + i * (20 * 4 + 10);
				float yy = 20 * 4 / 2 + 10;
				if (editorItem.focus && applet.getMouseCoordScreen().x > xx - (20 * 4) / 2 && applet.getMouseCoordScreen().x < xx + (20 * 4) / 2 && applet.getMouseCoordScreen().y > yy - (20 * 4) / 2 && applet.getMouseCoordScreen().y < yy + (20 * 4) / 2) {
					editorItem.focus = false;
					inventory.set(i, editorItem.id);
				}
			}
		}

		// Display Editor Object
		editorItem.update();
		editorItem.display();
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

	private float getInventorySize() {
		int y = 1;

		TileType[] tiles = { TileType.COLLISION, TileType.BACKGROUND, TileType.OBJECT };
		ArrayList<Tile> inventoryTiles = Tileset.getAllTiles(tiles);
		for (int i = 0; i < inventoryTiles.size(); i++) {
			if (i % 6 == 0) {
				y++;
			}
			else {
			}
		}
		return 20 * 4 + 10 + y * (20 * 4 + 10);
	}

	public boolean isZoomable() {
		return zoomable;
	}

	/**
	 * Lists the amount of items currently on the inventory. Can be refactored in
	 * the future to support the actual gameplay inventory.
	 * 
	 * @return amount of items currently on the inventory
	 */
	private int getTotalInventoryItems() {
		TileType[] tiles = { TileType.COLLISION, TileType.BACKGROUND, TileType.OBJECT };
		return Tileset.getAllTiles(tiles).size();
	}

	/**
	 * Calculates the ratio for a scrollbar.
	 * 
	 * @param bodyToScroll   Approximate relative size of the scrollable body
	 * @param containerSize  Size of the bar container
	 * @param sizeMultiplier Multiplier for the relative size of the bar
	 * @return Bar ratio for that specific bar
	 */
	private float getBarRatio(float bodyToScroll, int containerSize, float sizeMultiplier) {
		return bodyToScroll / (containerSize * sizeMultiplier);
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
				if (currentMode.getModeType().equals(GameModes.MODIFY)) { // As the SelectionBox class is private, this
																			// has to remain as a type-check and cannot
																			// delegate to the currentMode
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

	public void mouseWheel(MouseEvent event) {
		if (event.isShiftDown()) {
		}
		else {
			currentMode.mouseWheelEvent(event);
		}
	}

	@Override
	protected void keyReleased(processing.event.KeyEvent e) {
		switch (e.getKeyCode()) { // Global gameplay hotkeys
			case PConstants.ESC: // Pause
				applet.swapToScene(GameScenes.PAUSE_MENU);
				break;
			case Options.lifeCapInc:
				localPlayer.lifeCapacity++;
				break;
			case Options.lifeCapDec:
				localPlayer.lifeCapacity--;
				break;
			case Options.lifeInc:
				localPlayer.life++;
				break;
			case Options.lifeDec:
				localPlayer.life--;
				break;
			default:
				break;
		}

		currentMode.keyReleasedEvent(e);
	}

	public void switchModeOnKeyEvent(processing.event.KeyEvent event) {
		editorItem.setMode("CREATE");
		editorItem.focus = false;
		switch (event.getKeyCode()) {
			case 49: // 1
				changeMode(GameModes.MODIFY);
				break;
			case 50: // 2
				changeMode(GameModes.INVENTORY);
				scrollInventory = 0;
				break;
			case 51: // 3
				changeMode(GameModes.PLAY);
				applet.camera.setFollowObject(localPlayer);
				break;
			case 52: // 4
				changeMode(GameModes.SAVE);
				break;
			case 54: // 6
				changeMode(GameModes.IMPORT);
				break;
			case 69: // 'e' TODO remove?
				if (currentMode.getModeType().equals(GameModes.INVENTORY)) {
				}
				else {
					changeMode(GameModes.INVENTORY);
					editorItem.setMode("ITEM");
					scrollInventory = 0;
				}
				break;
			case 8: // BACKSPACE
			case 46: // DEL
				for (Iterator<EditableObject> iterator = objects.iterator(); iterator.hasNext();) {
					EditableObject o = (EditableObject) iterator.next();
					if (o.isFocused()) {
						iterator.remove();
					}
				}
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
					}
					catch (Exception e) {
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
					}
					catch (Exception e) {
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

	public void displayGUISlots() {
		for (int i = 0; i < 6; i++) {
			// Display Slot
			image(slot, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10);

			// Display Item
			PImage img = Tileset.getTile(inventory.get(i));
			applet.image(img, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10, img.width * (float) 0.5, img.height * (float) 0.5);

			// Focus Event
			if (applet.mousePressEvent) {
				float x = 20 * 4 / 2 + 10 + i * (20 * 4 + 10);
				float y = 20 * 4 / 2 + 10;
				if (applet.getMouseCoordScreen().x > x - (20 * 4) / 2 && applet.getMouseCoordScreen().x < x + (20 * 4) / 2 && applet.getMouseCoordScreen().y > y - (20 * 4) / 2 && applet.getMouseCoordScreen().y < y + (20 * 4) / 2) {
					editorItem.focus = true;
					editorItem.setTile(inventory.get(i));
					editorItem.type = Tileset.getTileType(inventory.get(i));
				}
			}
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

	public void scrollInventoryBar(MouseEvent event) {
		scrollBar.mouseWheel(event);
		scrollInventory = (int) PApplet.map(scrollBar.barLocation, 1, 0, -getInventorySize() + applet.height - 8, 0);
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
				}
				else {
					o.unFocus();
				}
			}
		}
	}

}
