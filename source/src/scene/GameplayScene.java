package scene;

import java.util.ArrayList;
import java.util.Iterator;

import entities.Player;
import objects.EditorItem;
import objects.GameObject;
import objects.BackgroundObject;
import objects.CollidableObject;
import objects.EditableObject;
import processing.core.*;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import projectiles.ProjectileObject;
import scene.components.WorldViewportEditor;
import sidescroller.SideScroller;
import sidescroller.Tileset;
import sidescroller.Tileset.tileType;
import sidescroller.Util;
import ui.Anchor;
import ui.ScrollBarVertical;
import ui.Tab;
import windows.LoadLevelWindow;
import windows.SaveLevelWindow;
import windows.TestWindow;

/**
 * Gameplay Scene. Both the level editor and gameplay.
 */
public class GameplayScene extends PScene {

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

	// Game World Objects
	public ArrayList<CollidableObject> collidableObjects;
	public ArrayList<BackgroundObject> backgroundObjects;
	public ArrayList<GameObject> gameObjects;
	public ArrayList<ProjectileObject> projectileObjects;

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

	// Editor Viewport
	public WorldViewportEditor worldViewportEditor;

	// Scroll Bar
	private ScrollBarVertical scrollBar;

	public enum Tools {
		MOVE, MODIFY, INVENTORY, PLAY, SAVE, LOADEXAMPLE, TEST,
	}

	public Tools tool;

	private ArrayList<String> inventory;

//	public boolean focusedOnObject; // mutex

	public EditableObject focusedObject = null;

	public boolean edit;

	private int scroll_inventory;

	private Player player;

	public GameplayScene(SideScroller a) {
		super(a);
	}

	@Override
	public void setup() {

		// Init Game World Objects Arrays
		collidableObjects = new ArrayList<CollidableObject>();
		backgroundObjects = new ArrayList<BackgroundObject>();
		gameObjects = new ArrayList<GameObject>();
		projectileObjects = new ArrayList<ProjectileObject>();

		// Create Inventory
		inventory = new ArrayList<String>();
		inventory.add("WEED_WALK_MIDDLE:0");
		inventory.add("WEED_WALK_MIDDLE:1");
		inventory.add("WEED_WALK_MIDDLE:2");
		inventory.add("WEED_WALK_MIDDLE:3");
		inventory.add("WEED_WALK_MIDDLE:4");
		inventory.add("WEED_WALK_MIDDLE:5");

		// Init Editor Components
		editorItem = new EditorItem(applet, this);
		worldViewportEditor = new WorldViewportEditor(applet);

		// Get Slots Graphics
		slot = Util.pg(SideScroller.graphicsSheet.get(289, 256, 20, 21), 4);
		slotEditor = Util.pg(SideScroller.graphicsSheet.get(310, 256, 20, 21), 4);

		// Get Icon Graphics
		icon_eye = Util.pg(SideScroller.graphicsSheet.get(267, 302, 11, 8), 4);
		icon_arrow = Util.pg(SideScroller.graphicsSheet.get(279, 301, 9, 9), 4);
		icon_inventory = Util.pg(SideScroller.graphicsSheet.get(289, 301, 9, 9), 4);
		icon_play = Util.pg(SideScroller.graphicsSheet.get(298, 301, 9, 9), 4);
		icon_save = Util.pg(SideScroller.graphicsSheet.get(307, 301, 9, 9), 4);

		icon_eyeActive = Util.pg(SideScroller.graphicsSheet.get(267, 292, 11, 8), 4);
		icon_arrowActive = Util.pg(SideScroller.graphicsSheet.get(279, 291, 9, 9), 4);
		icon_inventoryActive = Util.pg(SideScroller.graphicsSheet.get(289, 291, 9, 9), 4);
		icon_playActive = Util.pg(SideScroller.graphicsSheet.get(298, 291, 9, 9), 4);
		icon_saveActive = Util.pg(SideScroller.graphicsSheet.get(307, 291, 9, 9), 4);

		// Init Window
		window_saveLevel = new SaveLevelWindow(applet, this);
		window_test = new TestWindow(applet);
		window_loadLevel = new LoadLevelWindow(applet, this);

		// Init ScollBar
		Anchor scrollBarAnchor = new Anchor(applet, -20, 102, 20, 50);
		scrollBarAnchor.anchorOrigin = Anchor.AnchorOrigin.TopRight;
		scrollBarAnchor.stretch = Anchor.Stretch.Vertical;
		scrollBar = new ScrollBarVertical(scrollBarAnchor);
		scrollBar.setBarRatio(0.8f);

		// Default Scene
		collidableObjects.add(new CollidableObject(applet, this, "METAL_WALK_MIDDLE:0", 0, 0));

		// Default Tool
		tool = Tools.MODIFY;

		// Init Player
		player = new Player(applet, this);
		player.load(SideScroller.graphicsSheet);
		player.pos.x = 0; // // TODO set to spawn loc
		player.pos.y = -100; // // TODO set to spawn loc

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

		// View Background Objects
		for (int i = 0; i < backgroundObjects.size(); i++) {
			if (tool == Tools.MODIFY) {
				backgroundObjects.get(i).updateEdit();
			}

			backgroundObjects.get(i).display();

			if (backgroundObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				backgroundObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// View Collidable objects
		for (int i = 0; i < collidableObjects.size(); i++) {
			if (tool == Tools.MODIFY) {
				collidableObjects.get(i).updateEdit();
			}

			collidableObjects.get(i).display();

			if (collidableObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				collidableObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// View Game Objects (player-interactable objects)
		for (int i = 0; i < gameObjects.size(); i++) {
			if (tool == Tools.MODIFY) {
				gameObjects.get(i).updateEdit();
			}

			if (tool == Tools.PLAY) {
				gameObjects.get(i).update();
			}

			gameObjects.get(i).display();

			// Delete
			if (gameObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				gameObjects.remove(i);
				applet.keyPressEvent = false;
			}
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
				collidableObjects.forEach(o -> o.displayEdit());
				backgroundObjects.forEach(o -> o.displayEdit());
				gameObjects.forEach(o -> o.displayEdit());
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
				player.update();
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
					if (applet.getMouseX() > x - (20 * 4) / 2 && applet.getMouseX() < x + (20 * 4) / 2
							&& applet.getMouseY() > y - (20 * 4) / 2 && applet.getMouseY() < y + (20 * 4) / 2) {
						editorItem.focus = true;
						editorItem.setTile(inventory.get(i));
						editorItem.type = Tileset.getTileType(inventory.get(i));
					}
				}
			}
		}

		// GUI Icons
		if (tool == Tools.MOVE || (Util.hover(40, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hover(40, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.MOVE;
			}
			image(icon_eyeActive, 40, 120);
		} else {
			image(icon_eye, 40, 120);
		}
		if (tool == Tools.MODIFY || (Util.hover(90, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hover(90, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.MODIFY;
			}
			image(icon_arrowActive, 90, 120);
		} else {
			image(icon_arrow, 90, 120);
		}
		if (tool == Tools.INVENTORY
				|| (Util.hover(90 + 48, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hover(90 + 48, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.INVENTORY;
			}
			image(icon_inventoryActive, 90 + 48, 120);
		} else {
			image(icon_inventory, 90 + 48, 120);
		}
		if (tool == Tools.PLAY
				|| (Util.hover(90 + 48 * 2, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hover(90 + 48 * 2, 120, 36, 36) && applet.mousePressEvent) {
				tool = Tools.PLAY;
			}
			image(icon_playActive, 90 + 48 * 2, 120);
		} else {
			image(icon_play, 90 + 48 * 2, 120);
		}
		if (tool == Tools.SAVE
				|| (Util.hover(90 + 48 * 3, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (Util.hover(90 + 48 * 3, 120, 36, 36) && applet.mousePressEvent) {
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

		// Change tool;
		if (tool != Tools.SAVE) {
			if (applet.keyPressEvent) {
				if (applet.keyPress(49)) {
					tool = Tools.MOVE;
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(50)) {
					tool = Tools.MODIFY;
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(51)) {
					tool = Tools.INVENTORY;
					editorItem.setMode("CREATE");
					editorItem.focus = false;
					scroll_inventory = 0;
				}
				if (applet.keyPress(52)) {
					tool = Tools.PLAY;
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(53)) {
					tool = Tools.SAVE;
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(69)) {
					if (tool == Tools.INVENTORY) {
						tool = Tools.MOVE;
						editorItem.setMode("CREATE");
						editorItem.focus = false;
					} else {
						tool = Tools.INVENTORY;
						editorItem.setMode("ITEM");
						editorItem.focus = false;
						scroll_inventory = 0;
					}
				}
			}
		}
	}

	/**
	 * Display boundaries of all world objects.
	 */
	public void debug() {
		applet.strokeWeight(2);
		applet.noFill();

		applet.stroke(50, 255, 120);
		backgroundObjects.forEach(o -> applet.rect(o.pos.x, o.pos.y, o.width, o.height));

		applet.stroke(255, 190, 200);
		gameObjects.forEach(o -> applet.rect(o.pos.x, o.pos.y, o.width, o.height));

		applet.stroke(50, 120, 255);
		collidableObjects.forEach(o -> applet.rect(o.pos.x, o.pos.y, o.width, o.height));

		applet.noStroke();
		applet.fill(255);
		collidableObjects.forEach(o -> applet.ellipse(o.pos.x, o.pos.y, 5, 5));
		gameObjects.forEach(o -> applet.ellipse(o.pos.x, o.pos.y, 5, 5));
		backgroundObjects.forEach(o -> applet.ellipse(o.pos.x, o.pos.y, 5, 5));
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
				if (applet.getMouseY() > 100) {
					if (applet.getMouseX() > xx - (20 * 4) / 2 && applet.getMouseX() < xx + (20 * 4) / 2
							&& applet.getMouseY() > yy - (20 * 4) / 2 && applet.getMouseY() < yy + (20 * 4) / 2) {
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
				if (editorItem.focus && applet.getMouseX() > xx - (20 * 4) / 2 && applet.getMouseX() < xx + (20 * 4) / 2
						&& applet.getMouseY() > yy - (20 * 4) / 2 && applet.getMouseY() < yy + (20 * 4) / 2) {
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

	/**
	 * Saves the level (background, game and collideable objects), encrypting the
	 * output.
	 * 
	 * @param path Save location path.
	 */
	public void saveLevel(String path) {
		JSONArray data = new JSONArray();

		// MAIN
		JSONObject main = new JSONObject();
		main.setString("title", "undefined");
		main.setString("creator", "undefined");
		main.setString("version", "alpha 1.0.0");

		// Add Main
		data.append(main);

		// Add Collisions
		for (int i = 0; i < collidableObjects.size(); i++) {
			JSONObject item = new JSONObject();
			item.setString("id", collidableObjects.get(i).id);
			item.setString("type", "COLLISION");
			item.setInt("x", (int) collidableObjects.get(i).pos.x);
			item.setInt("y", (int) collidableObjects.get(i).pos.y);
			data.append(item);
		}

		// Add Background Objects
		for (int i = 0; i < backgroundObjects.size(); i++) {
			JSONObject item = new JSONObject();
			item.setString("id", backgroundObjects.get(i).id);
			item.setString("type", "BACKGROUND");
			item.setInt("x", (int) backgroundObjects.get(i).pos.x);
			item.setInt("y", (int) backgroundObjects.get(i).pos.y);
			data.append(item);
		}

		// Add Game Objects
		for (int i = 0; i < gameObjects.size(); i++) {
			collidableObjects.remove(gameObjects.get(i).collision);

			JSONObject item = new JSONObject();
			item.setString("id", gameObjects.get(i).id);
			item.setString("type", "OBJECT");
			item.setInt("x", (int) gameObjects.get(i).pos.x);
			item.setInt("y", (int) gameObjects.get(i).pos.y);
			data.append(item);
		}

		// Save Level
		Util.saveFile(path, Util.encrypt(data.toString()));
	}

	public void loadLevel(String path) { // TODO save camera position/settings.
		String[] script = applet.loadStrings(path);
		String scriptD = Util.decrypt(PApplet.join(script, "\n"));

		// Parse JSON
		JSONArray data = JSONArray.parse(scriptD);

		// Clear Object Arrays
		collidableObjects.clear();
		backgroundObjects.clear();

		// Create Level
		for (int i = 0; i < data.size(); i++) {
			JSONObject item = data.getJSONObject(i);

			String type = item.getString("type");

			// Read Main
			if (i == 0) {
				if (applet.currentScene instanceof GameplayScene) {
					((GameplayScene) applet.currentScene).worldViewportEditor.setSize();
				}
			} else {
				switch (type) {
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

						// Append To Level
						collidableObjects.add(collision);
						break;
					case "BACKGROUND" :
						BackgroundObject backgroundObject = new BackgroundObject(applet, this);
						backgroundObject.setGraphic(item.getString("id"));
						backgroundObject.pos.x = item.getInt("x");
						backgroundObject.pos.y = item.getInt("y");

						// Append To Level
						backgroundObjects.add(backgroundObject);
						break;
					case "OBJECT" :
						GameObject gameObject = Tileset.getObjectClass(item.getString("id"));
						gameObject.pos.x = item.getInt("x");
						gameObject.pos.y = item.getInt("y");

						// Append To Level
						gameObjects.add(gameObject);
						break;
				}
			}
		}
	}
}
