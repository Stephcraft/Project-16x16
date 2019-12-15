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

import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Tileset.tileType;
import project_16x16.Util;

import project_16x16.ui.Anchor;
import project_16x16.ui.ScrollBarVertical;
import project_16x16.ui.Tab;

import project_16x16.windows.LoadLevelWindow;
import project_16x16.windows.SaveLevelWindow;
import project_16x16.windows.TestWindow;

/**
 * Gameplay Scene. Both the level editor and gameplay.
 */
public class GameplayScene extends PScene {

	//Singleplayer
	private boolean isSingleplayer;

	//Multiplayer
	private Multiplayer host;
	private Multiplayer client;

	private String Ip = "127.0.0.1";
	private int port = 25565;
	private boolean isHost;

	// Graphics Slots
	private PImage slot;
	private PImage slotEditor;

	// Graphics Icon
	private PImage icon_eye;
	private PImage icon_arrow;
	private PImage icon_inventory;
	private PImage icon_play;
	private PImage icon_save;
	private PImage icon_eyeActive;
	private PImage icon_arrowActive;
	private PImage icon_inventoryActive;
	private PImage icon_playActive;
	private PImage icon_saveActive;

	public ArrayList<ProjectileObject> projectileObjects; // TODO working?
	public ArrayList<EditableObject> objects;

	// Windows
	private SaveLevelWindow window_saveLevel;
	private TestWindow window_test;
	private LoadLevelWindow window_loadLevel;

	// Tabs
	private Tab windowTabs;
	// Each button id corresponds with its string id: ex) load = 0, save = 1, etc.
	String tabTexts[] = new String[] { "load", "save", "long name" };

	// Editor Item
	private EditorItem editorItem;

	// Scroll Bar
	private ScrollBarVertical scrollBar;

	public enum Tools {
		MOVE, MODIFY, INVENTORY, PLAY, SAVE, LOADEXAMPLE, TEST,
	}

	public Tools tool;

	private ArrayList<String> inventory;

	public EditableObject focusedObject = null;

	public boolean edit;

	private int scroll_inventory;

	private Player player;

	private PVector mouseDown, origPos;

	private SelectionBox selectionBox;

	public GameplayScene(SideScroller a) {
		super(a);
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
		icon_eye = Tileset.getTile(267, 302, 11, 8, 4);
		icon_arrow = Tileset.getTile(279, 301, 9, 9, 4);
		icon_inventory = Tileset.getTile(289, 301, 9, 9, 4);
		icon_play = Tileset.getTile(298, 301, 9, 9, 4);
		icon_save = Tileset.getTile(307, 301, 9, 9, 4);

		icon_eyeActive = Tileset.getTile(267, 292, 11, 8, 4);
		icon_arrowActive = Tileset.getTile(279, 291, 9, 9, 4);
		icon_inventoryActive = Tileset.getTile(289, 291, 9, 9, 4);
		icon_playActive = Tileset.getTile(298, 291, 9, 9, 4);
		icon_saveActive = Tileset.getTile(307, 291, 9, 9, 4);

		// Init Window
		window_saveLevel = new SaveLevelWindow(applet, this);
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
		player = new Player(applet, this);
		player.pos.set(0, -100); // TODO spawn location

		loadLevel(SideScroller.LEVEL); // TODO change level

		windowTabs = new Tab(applet, tabTexts, 3);
	}

	/**
	 * Draw scene elements that are below (affected by) the camera.
	 */
	public void draw() {
		background(29, 33, 45);

		applet.noStroke();
		applet.fill(29, 33, 45);

		if (tool == Tools.MODIFY) {
			displayGrid();
			if (applet.mousePressEvent && focusedObject != null) {
				focusedObject.updateEdit(); // enforce one item selected at once
			}
		}

		for (EditableObject o : objects) {
			if(o instanceof MagicSourceObject){
				((MagicSourceObject)o).updateEmissionPosition();
			}
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
			case MOVE :
			case INVENTORY :
			case SAVE :
			case LOADEXAMPLE :
			case TEST :
			default :
				break;
		}
		drawPlayer();
	}

	public void setInfo(String IP, int port, boolean isHost) {
		this.Ip = IP;
		this.port = port;
		this.isHost = isHost;
	}

	public void setupMultiplayer(boolean isHost) {
		if (isHost) {
			this.isHost = true;
			host = new Multiplayer(this, this.port);
		} else {
			this.isHost = false;
			client = new Multiplayer(this, this.Ip, this.port);
		}
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
				player.updateEdit();
				player.displayEdit();
				break;
			case PLAY :
				if (isSingleplayer) {
					player.update();
				} else {
					if (isHost) {
						host.writeDataServer((int) player.pos.x, (int) player.pos.y, player.animation.name);
						host.readDataServer();
						player.update();
					} else {
						System.out.println(player);
						client.writeDataClient((int) player.pos.x, (int) player.pos.y, player.animation.name);
						client.readDataClient();
						player.update();
					}
				}
				break;
			case MOVE :
			case INVENTORY :
			case SAVE :
			case LOADEXAMPLE :
			case TEST :
			default :
				break;
		}
		player.display();
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

		// GUI Icons
		if (tool == Tools.MOVE
				|| (Util.hoverScreen(40, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(40, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.MOVE;
			}
			image(icon_eyeActive, 40, 120);
		} else {
			image(icon_eye, 40, 120);
		}
		if (tool == Tools.MODIFY
				|| (Util.hoverScreen(90, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(90, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.MODIFY;
			}
			image(icon_arrowActive, 90, 120);
		} else {
			image(icon_arrow, 90, 120);
		}
		if (tool == Tools.INVENTORY
				|| (Util.hoverScreen(90 + 48, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(90 + 48, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.INVENTORY;
			}
			image(icon_inventoryActive, 90 + 48, 120);
		} else {
			image(icon_inventory, 90 + 48, 120);
		}
		if (tool == Tools.PLAY
				|| (Util.hoverScreen(90 + 48 * 2, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(90 + 48 * 2, 120, 36, 36) && applet.mousePressEvent) {
				applet.camera.setFollowObject(player);
				tool = Tools.PLAY;
			}
			image(icon_playActive, 90 + 48 * 2, 120);
		} else {
			image(icon_play, 90 + 48 * 2, 120);
		}
		if (tool == Tools.SAVE
				|| (Util.hoverScreen(90 + 48 * 3, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hoverScreen(90 + 48 * 3, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.SAVE;
			}
			image(icon_saveActive, 90 + 48 * 3, 120);
		} else {
			image(icon_save, 90 + 48 * 3, 120);
		}

		switch (tool) {
			case INVENTORY :
				displayCreativeInventory();
				break;
			case MODIFY :
				editorItem.update();
				editorItem.display();
				break;
			case MOVE :
				break;
			case PLAY :
				player.displayLife();
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
				} else if (windowTabs.getButton(2).event()) {
					windowTabs.moveActive(2);
					tool = Tools.TEST;
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
				} else if (windowTabs.getButton(2).event()) {
					windowTabs.moveActive(2);
					tool = Tools.TEST;
				}
				break;
			case TEST :
				if (windowTabs.getActiveButton() != 2) {
					windowTabs.moveActive(2);
				}
				window_test.privacyDisplay();
				windowTabs.update();
				windowTabs.display();
				window_test.update();
				window_test.display();
				if (windowTabs.getButton(0).event()) {
					windowTabs.moveActive(0);
					tool = Tools.LOADEXAMPLE;
				} else if (windowTabs.getButton(1).event()) {
					windowTabs.moveActive(1);
					tool = Tools.SAVE;
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
		applet.strokeWeight(2);
		applet.noFill();

		for (EditableObject o : objects) {
			o.debug();
			applet.noStroke();
			applet.fill(255);
			applet.ellipse(o.pos.x, o.pos.y, 5, 5);
		}
	}

	public Player getPlayer() {
		return player;
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
		tileType[] tiles = { tileType.COLLISION, tileType.BACKGROUND, tileType.OBJECT };
		ArrayList<PImage> inventoryTiles = Tileset.getAllTiles(tiles);
		for (PImage img : inventoryTiles) {
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
						editorItem.setTile(Tileset.getTileName(Tileset.getTileId(img)));
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
		for (int i = -l; i < l; i += 64) {
			applet.line(-l, i + yOffset, l, i + yOffset); // horizontal
			applet.line(i + xOffset, -l, i + xOffset, l); // vertical
		}
	}

	private float getInventorySize() {
		int y = 1;

		tileType[] tiles = { tileType.COLLISION, tileType.BACKGROUND, tileType.OBJECT };
		ArrayList<PImage> inventoryTiles = Tileset.getAllTiles(tiles);
		for (int i = 0; i < inventoryTiles.size(); i++) {
			if (i % 6 == 0) {
				y++;
			} else {
			}
		}
		return 20 * 4 + 10 + y * (20 * 4 + 10);
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
					objects.forEach(o -> o.unFocus());
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
		if (event.isShiftDown()) {
		} else {
			if (tool == Tools.INVENTORY) {
				scrollBar.mouseWheel(event);
				scroll_inventory = (int) PApplet.map(scrollBar.barLocation, 1, 0,
						-getInventorySize() + applet.height - 8, 0);
			}
		}
	}

	@Override
	protected void keyReleased(processing.event.KeyEvent e) {
		if (tool != Tools.SAVE) { // Change tool
			editorItem.setMode("CREATE");
			editorItem.focus = false;
			switch (e.getKeyCode()) {
				case 49 : // 1
					tool = Tools.MOVE;
					break;
				case 50 : // 2
					tool = Tools.MODIFY;
					break;
				case 51 : // 3
					tool = Tools.INVENTORY;
					scroll_inventory = 0;
					break;
				case 52 : // 4
					tool = Tools.PLAY;
					applet.camera.setFollowObject(player);
					break;
				case 53 : // 5
					tool = Tools.SAVE;
					break;
				case 69 : // 'e' TODO remove?
					if (tool == Tools.INVENTORY) {
						tool = Tools.MOVE;
					} else {
						tool = Tools.INVENTORY;
						editorItem.setMode("ITEM");
						scroll_inventory = 0;
					}
					break;
				case 8: // BACKSPACE
				case 46 : // DEL
					for (Iterator<EditableObject> iterator = objects.iterator(); iterator.hasNext();) {
						EditableObject o = (EditableObject) iterator.next();
						if (o.isFocused()) {
							iterator.remove();
						}
					}
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
