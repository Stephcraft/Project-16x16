package project_16x16.scene;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

import project_16x16.entities.Player;

import project_16x16.multiplayer.Multiplayer;
import project_16x16.objects.*;

import processing.core.*;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import project_16x16.projectiles.ProjectileObject;
import project_16x16.Audio;
import project_16x16.Options;
import project_16x16.Main;
import project_16x16.Tileset;
import project_16x16.Util;
import project_16x16.components.Tile;
import project_16x16.components.Tile.TileType;
import project_16x16.Audio.BGM;
import project_16x16.Main.GameScenes;
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

	//Singleplayer
	private boolean isSingleplayer = true; // true by default

	//Multiplayer
	private Multiplayer multiplayer;

	// Graphics Slots
	private PImage slot;
	private PImage slotEditor;
	
	private final String levelString;

	// Graphics Icon
	private PImage icon_modify;
	private PImage icon_inventory;
	private PImage icon_play;
	private PImage icon_save;
	private PImage icon_modfiyActive;
	private PImage icon_inventoryActive;
	private PImage icon_playActive;
	private PImage icon_saveActive;

	public ArrayList<ProjectileObject> projectileObjects; // TODO working?
	public ArrayList<EditableObject> objects;

	// Windows
	private SaveLevelWindow window_saveLevel;
	private ImportLevelWindow window_importlevel;
	//private TestWindow window_test;
	private LoadLevelWindow window_loadLevel;

	// Tabs
	private Tab windowTabs;
	// Each button id corresponds with its string id: ex) load = 0, save = 1, etc.
	String[] tabTexts = new String[] { "load", "save", "import"};

	// Camera Zoom State
	private boolean zoomable = true; // Camera can zoom by default

	// Editor Item
	private EditorItem editorItem;

	// Scroll Bar
	private ScrollBarVertical scrollBar;

	public enum Tools {
		MOVE, MODIFY, INVENTORY, PLAY, SAVE, IMPORT, LOADEXAMPLE, TEST,
	}

	public Tools tool;

	private ArrayList<String> inventory;

	public EditableObject focusedObject = null;

	public boolean edit;

	private int scroll_inventory;

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

	public GameplayScene(Main a, String levelString) {
		super(a);
		this.levelString = levelString;
		setup();
	}

	private void setup() {

		projectileObjects = new ArrayList<>();

		objects = new ArrayList<>();

		// Create Inventory
		inventory = new ArrayList<>();
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
		icon_modify = Tileset.getTile(279, 301, 9, 9, 4);
		icon_inventory = Tileset.getTile(289, 301, 9, 9, 4);
		icon_play = Tileset.getTile(298, 301, 9, 9, 4);
		icon_save = Tileset.getTile(307, 301, 9, 9, 4);

		icon_modfiyActive = Tileset.getTile(279, 291, 9, 9, 4);
		icon_inventoryActive = Tileset.getTile(289, 291, 9, 9, 4);
		icon_playActive = Tileset.getTile(298, 291, 9, 9, 4);
		icon_saveActive = Tileset.getTile(307, 291, 9, 9, 4);

		// Init Window
		window_saveLevel = new SaveLevelWindow(applet, this);
		// Import Window
		window_importlevel = new ImportLevelWindow(applet, this);
//		window_test = new TestWindow(applet);
		window_loadLevel = new LoadLevelWindow(applet, this);

		// Init ScollBar
		Anchor scrollBarAnchor = new Anchor(applet, -20, 102, 20, 50);
		scrollBarAnchor.anchorOrigin = Anchor.AnchorOrigin.TopRight;
		scrollBarAnchor.stretch = Anchor.Stretch.Vertical;
		scrollBar = new ScrollBarVertical(scrollBarAnchor);
		scrollBar.setBarRatio(0.8f);
		
		// Default Tool
		tool = Tools.MODIFY;

		// Init Player
		localPlayer = new Player(applet, this, false);
		localPlayer.pos.set(0, -100); // TODO spawn location

		loadLevel(levelString); // TODO change level

		windowTabs = new Tab(applet, tabTexts, tabTexts.length);
	}

	@Override
	public void switchTo() {
		super.switchTo();
		((PauseMenu) GameScenes.PAUSE_MENU.getScene()).switched = false;
		Audio.play(BGM.TEST1, -10);
	}
	
	/**
	 * Draw scene elements that are below (affected by) the camera.
	 */
	public void draw() {
		background(23, 26, 36);
		
		if (tool == Tools.MODIFY) {
			displayGrid();
			if (applet.mousePressEvent && focusedObject != null) {
				focusedObject.updateEdit(); // enforce one item selected at once
			}
		}

		for (EditableObject o : objects) {
			if (tool == Tools.MODIFY) {
				o.updateEdit();
				o.displayEdit();
			}
			if (tool == Tools.PLAY && o instanceof GameObject) {
				((GameObject) o).update();
			}
			o.display();
		}

		// View Projectiles
		Iterator<ProjectileObject> i = projectileObjects.iterator();
		while (i.hasNext()) {
			ProjectileObject o = i.next();
			if (applet.frameCount - o.spawnTime > 600) {
				i.remove(); // kill projectile after 10s
			} else {
				o.update();
				o.display();
			}
		}

		switch (tool) {
			case MODIFY :
				editorItem.displayDestination();
				break;
			case PLAY :
			case INVENTORY :
			case SAVE :
			case LOADEXAMPLE :
			case TEST :
			default :
				break;
		}
		drawPlayer();
	}
	
/**
 * Call when host/connect buttons pressed.
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
		switch (tool) {
			case MODIFY :
				localPlayer.updateEdit();
				localPlayer.displayEdit();
				break;
			case PLAY :
				localPlayer.update();
				break;
			case INVENTORY :
			case SAVE :
			case LOADEXAMPLE :
			case TEST :
			default :
				break;
		}
		if (!isSingleplayer) {
			JSONObject data = new JSONObject();
			data.setFloat("x", localPlayer.pos.x);
			data.setFloat("y", localPlayer.pos.y);
			data.setInt("dir", localPlayer.getState().facingDir);
			data.setString("animSequence", localPlayer.animation.name);
			data.setInt("animFrame", localPlayer.animation.getFrameID());
			multiplayer.writeData(data.toString()); // write data to server

			JSONObject other = multiplayer.readData(); // read from server & display other player
			if (other != null) {
				onlinePlayer.pos.x = other.getFloat("x");
				onlinePlayer.pos.y = other.getFloat("y");
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

		// 6 GUI Slots
		if (tool != Tools.INVENTORY) {
			for (int i = 0; i < 6; i++) {
				// Display Slot
				image(slot, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10);

				// Display Item
				PImage img = Tileset.getTile(inventory.get(i));
				applet.image(img, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10, img.width * (float) 0.5,
						img.height * (float) 0.5);

				// Focus Event
				if (applet.mousePressEvent) {
					float x = 20 * 4 / 2 + 10 + i * (20 * 4 + 10);
					float y = 20 * 4 / 2 + 10;
					if (applet.getMouseCoordScreen().x > x - (20 * 4) / 2
							&& applet.getMouseCoordScreen().x < x + (20 * 4) / 2
							&& applet.getMouseCoordScreen().y > y - (20 * 4) / 2
							&& applet.getMouseCoordScreen().y < y + (20 * 4) / 2) {
						editorItem.focus = true;
						editorItem.setTile(inventory.get(i));
						editorItem.type = Tileset.getTileType(inventory.get(i));
					}
				}
			}
		}

		int xAnchor = 42;
		int offset = 48;
		// GUI Icons
		if (tool == Tools.MODIFY
				|| (Util.hoverScreen(xAnchor, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(xAnchor, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.MODIFY;
			}
			image(icon_modfiyActive, xAnchor, 120);
		} else {
			image(icon_modify, xAnchor, 120);
		}
		if (tool == Tools.INVENTORY
				|| (Util.hoverScreen(xAnchor + offset, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(xAnchor + offset, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.INVENTORY;
			}
			image(icon_inventoryActive, xAnchor + offset, 120);
		} else {
			image(icon_inventory, xAnchor + offset, 120);
		}
		if (tool == Tools.PLAY || (Util.hoverScreen(xAnchor + offset * 2, 120, 36, 36) && tool != Tools.SAVE
				&& tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(xAnchor + offset * 2, 120, 36, 36) && applet.mousePressEvent) {
				applet.camera.setFollowObject(localPlayer);
				tool = Tools.PLAY;
			}
			image(icon_playActive, xAnchor + offset * 2, 120);
		} else {
			image(icon_play, xAnchor + offset * 2, 120);
		}
		if (tool == Tools.SAVE || (Util.hoverScreen(xAnchor + offset * 3, 120, 36, 36) && tool != Tools.SAVE
				&& tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(xAnchor + offset * 3, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.SAVE;
			}
			image(icon_saveActive, xAnchor + offset * 3, 120);
		} else {
			image(icon_save, xAnchor + offset * 3, 120);
		}

		switch (tool) {
			case INVENTORY :
				displayCreativeInventory();
				zoomable = false;
				break;
			case MODIFY :
				editorItem.update();
				editorItem.display();
				zoomable = true;
				break;
			case PLAY :
				localPlayer.displayLife();
				zoomable = true;
				break;
			case SAVE :
				// Save , Load
				// The if statement below should be used in each window that includes a tab.
				// switch the number to the id of the button it's checking for
				if (windowTabs.getActiveButton() != 1) {
					windowTabs.moveActive(1);
				}
				window_saveLevel.privacyDisplay();
				windowTabs.update();
				windowTabs.display();
				window_saveLevel.update();
				window_saveLevel.display();
				// This is an example of how to switch windows when another tab button is
				// pressed.
				if (windowTabs.getButton(0).event()) {
					windowTabs.moveActive(0);
					tool = Tools.LOADEXAMPLE;
				}
				if (windowTabs.getButton(2).event()) {
					windowTabs.moveActive(2);
					tool = Tools.IMPORT;
				}
				zoomable = false;
				break;
			case IMPORT :
				// Import Level
				if (windowTabs.getActiveButton() != 2) {
					windowTabs.moveActive(2);
				}
				window_importlevel.privacyDisplay();
				windowTabs.update();
				windowTabs.display();
				window_importlevel.update();
				window_importlevel.display();
				
				if (windowTabs.getButton(0).event()) {
					windowTabs.moveActive(0);
					tool = Tools.LOADEXAMPLE;
				}
				if (windowTabs.getButton(1).event()) {
					windowTabs.moveActive(1);
					tool = Tools.SAVE;
				}
				break;
			case LOADEXAMPLE :
				if (windowTabs.getActiveButton() != 0) {
					windowTabs.moveActive(0);
				}
				windowTabs.update();
				windowTabs.display();
				window_loadLevel.display();
				window_loadLevel.update();
				if (windowTabs.getButton(1).event()) {
					windowTabs.moveActive(1);
					tool = Tools.SAVE;
				}
				if (windowTabs.getButton(2).event()) {
					windowTabs.moveActive(2);
					tool = Tools.IMPORT;
				}
				break;
			default :
				break;
		}
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

	private void displayCreativeInventory() {// complete creative inventory

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
			} else {
				x++;
			}
			applet.image(slotEditor, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scroll_inventory);
			if (img.width > 20 * 4 || img.height > 20 * 4) {
				applet.image(img, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scroll_inventory,
						img.width / 4, img.height / 4);
			} else {
				applet.image(img, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scroll_inventory,
						img.width / 2, img.height / 2);
			}

			// Grab Item
			if (applet.mousePressEvent) {
				float xx = 20 * 4 / 2 + 10 + x * (20 * 4 + 10);
				float yy = y * (20 * 4 + 10) + scroll_inventory;
				if (applet.getMouseCoordScreen().y > 100) {
					if (applet.getMouseCoordScreen().x > xx - (20 * 4) / 2
							&& applet.getMouseCoordScreen().x < xx + (20 * 4) / 2
							&& applet.getMouseCoordScreen().y > yy - (20 * 4) / 2
							&& applet.getMouseCoordScreen().y < yy + (20 * 4) / 2) {
						editorItem.focus = true;
						editorItem.setTile(tile.getName());
					}
				}
			}
			index++;
		}

		// Display ScrollBar
		scrollBar.display();
		scrollBar.update();
		scroll_inventory = (int) PApplet.map(scrollBar.barLocation, 1, 0, -getInventorySize() + applet.height - 8, 0);

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
			applet.image(img, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10, img.width * (float) 0.5,
					img.height * (float) 0.5);

			// Focus Event
			if (applet.mouseReleaseEvent) {
				float xx = 20 * 4 / 2 + 10 + i * (20 * 4 + 10);
				float yy = 20 * 4 / 2 + 10;
				if (editorItem.focus && applet.getMouseCoordScreen().x > xx - (20 * 4) / 2
						&& applet.getMouseCoordScreen().x < xx + (20 * 4) / 2
						&& applet.getMouseCoordScreen().y > yy - (20 * 4) / 2
						&& applet.getMouseCoordScreen().y < yy + (20 * 4) / 2) {
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
		for (int i = -l; i < l; i += Main.snapSize) {
			applet.line(-l, i + yOffset, l, i + yOffset); // horizontal
			applet.line(i + xOffset, -l, i + xOffset, l); // vertical
		}
	}

	private float getInventorySize() {
		int y = 1;

		TileType[] tiles = { TileType.COLLISION, TileType.BACKGROUND, TileType.OBJECT };
		ArrayList<Tile> inventoryTiles = Tileset.getAllTiles(tiles);
		for (int i = 0; i < inventoryTiles.size(); i++) {
			if (i % 6 == 0) y++;
		}
		return 20 * 4 + 10 + y * (20 * 4 + 10);
	}

	public boolean isZoomable() {
		return zoomable;
	}

	@Override
	void mousePressed(MouseEvent e) {
		origPos = applet.camera.getPosition(); // used for camera panning
		mouseDown = applet.getMouseCoordScreen();
		switch (e.getButton()) {
			case LEFT :
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
					objects.forEach(EditableObject::unFocus);
				}
				break;
			case RIGHT :
				if (tool == Tools.MODIFY) {
					selectionBox = new SelectionBox(mouseDown);
				}
				break;
			default :
				break;
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
			case LEFT :
				break;
			case RIGHT :
				selectionBox = null;
				break;
			default :
				break;
		}
	}

	@Override
	void mouseDragged(MouseEvent e) {
		if (e.getButton() == PConstants.CENTER && tool == Tools.MODIFY) { // pan on MMB; TODO fix when zoom != 1.00
			applet.camera.setCameraPositionNoLerp(
					PVector.add(origPos, PVector.sub(mouseDown, applet.getMouseCoordScreen())));
		}
	}

	public void mouseWheel(MouseEvent event) {
		if (!event.isShiftDown()) {
			if (tool == Tools.INVENTORY) {
				scrollBar.mouseWheel(event);
				scroll_inventory = (int) PApplet.map(scrollBar.barLocation, 1, 0,
						-getInventorySize() + applet.height - 8, 0);
			}
		}
	}

	@Override
	protected void keyReleased(processing.event.KeyEvent e) {
		switch (e.getKeyCode()) { // Global gameplay hotkeys
			case PConstants.ESC : // Pause
				applet.swapToScene(GameScenes.PAUSE_MENU);
				break;
			case Options.lifeCapInc :
				localPlayer.lifeCapacity++;
				break;
			case Options.lifeCapDec :
				localPlayer.lifeCapacity--;
				break;
			case Options.lifeInc :
				localPlayer.life++;
				break;
			case Options.lifeDec :
				localPlayer.life--;
				break;
			default :
				break;
		}
		
		if (tool != Tools.SAVE) { // Change tool
			editorItem.setMode("CREATE");
			editorItem.focus = false;
			switch (e.getKeyCode()) {
				case 49 : // 1
					tool = Tools.MODIFY;
					break;
				case 50 : // 2
					tool = Tools.INVENTORY;
					scroll_inventory = 0;
					break;
				case 51 : // 3
					tool = Tools.PLAY;
					applet.camera.setFollowObject(localPlayer);
					break;
				case 52 : // 4
					tool = Tools.SAVE;
					break;
				case 54 : // 6
					tool = Tools.IMPORT;
					break;
				case 69 : // 'e' TODO remove?
					if (tool != Tools.INVENTORY) {
						tool = Tools.INVENTORY;
						editorItem.setMode("ITEM");
						scroll_inventory = 0;
					}
					break;
				case 8: // BACKSPACE
				case 46 : // DEL
					objects.removeIf(EditableObject::isFocused);
					break;
				default :
					break;
			}
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
		Util.saveFile(path, Util.encrypt(data.toString()));
	}

	public void loadLevel(String path) { // TODO save camera position/settings.

		String[] script = applet.loadStrings(path);
		if (script == null) {
			return;
		}

		String scriptD = Util.decrypt(PApplet.join(script, "\n")); // decrypt save data
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
				case "COLLISION" :
					CollidableObject collision = new CollidableObject(applet, this);
					try {
						collision.setGraphic(item.getString("id"));
					} catch (Exception e) {
						collision.width = 64;
						collision.height = 64;
					}
					collision.pos.x = item.getInt("x");
					collision.pos.y = item.getInt("y");

					objects.add(collision); // SideScrollerend To Level
					break;
				case "BACKGROUND" :
					BackgroundObject backgroundObject = new BackgroundObject(applet, this);
					backgroundObject.setGraphic(item.getString("id"));
					backgroundObject.pos.x = item.getInt("x");
					backgroundObject.pos.y = item.getInt("y");

					objects.add(backgroundObject); // SideScrollerend To Level
					break;
				case "OBJECT" :
					try {
						Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(item.getString("id"));
						Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
						GameObject gameObject = (GameObject) ctor.newInstance(new Object[] { applet, this });
						gameObject.pos.x = item.getInt("x");
						gameObject.pos.y = item.getInt("y");

						objects.add(gameObject); // SideScrollerend To Level
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default :
					break;
			}
		}
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
				if (Util.withinRegion(o.pos, startPosGame, applet.getMouseCoordGame())) {
					o.focus();
				} else {
					o.unFocus();
				}
			}
		}
	}

}
