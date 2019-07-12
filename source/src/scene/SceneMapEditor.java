package scene;

import java.util.ArrayList;

import objects.EditorItem;
import objects.Collision;

import processing.core.*;
import processing.event.MouseEvent;
import scene.components.WorldViewportEditor;
import sidescroller.SideScroller;
import windows.SaveLevelWindow;

public class SceneMapEditor extends PScene {

	// Graphics Slots
	PGraphics slot;
	PGraphics slotEditor;

	// Graphics Icon
	PGraphics icon_eye;
	PGraphics icon_arrow;
	PGraphics icon_inventory;
	PGraphics icon_play;
	PGraphics icon_save;
	PGraphics icon_eyeActive;
	PGraphics icon_arrowActive;
	PGraphics icon_inventoryActive;
	PGraphics icon_playActive;
	PGraphics icon_saveActive;

	// Windows
	public SaveLevelWindow window_saveLevel;

	// Editor Item
	public EditorItem editorItem;

	// Editor Viewport
	public WorldViewportEditor worldViewportEditor;

	public String tool;
	public ArrayList<String> inventory;

	public boolean focusedOnObject;

	public boolean edit;

	int scroll_inventory;

	public SceneMapEditor(SideScroller a) {
		super(a);
	}

	@Override
	public void setup() {

		// Create Inventory
		inventory = new ArrayList<String>();
		inventory.add("WEED_WALK_MIDDLE:0");
		inventory.add("WEED_WALK_MIDDLE:1");
		inventory.add("WEED_WALK_MIDDLE:2");
		inventory.add("WEED_WALK_MIDDLE:3");
		inventory.add("WEED_WALK_MIDDLE:4");
		inventory.add("WEED_WALK_MIDDLE:5");

		// Init Editor Components
		editorItem = new EditorItem(applet);
		worldViewportEditor = new WorldViewportEditor(applet);

		// Get Slots Graphics
		slot = util.pg(applet.graphicsSheet.get(289, 256, 20, 21), 4);
		slotEditor = util.pg(applet.graphicsSheet.get(310, 256, 20, 21), 4);

		// Get Icon Graphics
		icon_eye = util.pg(applet.graphicsSheet.get(267, 302, 11, 8), 4);
		icon_arrow = util.pg(applet.graphicsSheet.get(279, 301, 9, 9), 4);
		icon_inventory = util.pg(applet.graphicsSheet.get(289, 301, 9, 9), 4);
		icon_play = util.pg(applet.graphicsSheet.get(298, 301, 9, 9), 4);
		icon_save = util.pg(applet.graphicsSheet.get(307, 301, 9, 9), 4);

		icon_eyeActive = util.pg(applet.graphicsSheet.get(267, 292, 11, 8), 4);
		icon_arrowActive = util.pg(applet.graphicsSheet.get(279, 291, 9, 9), 4);
		icon_inventoryActive = util.pg(applet.graphicsSheet.get(289, 291, 9, 9), 4);
		icon_playActive = util.pg(applet.graphicsSheet.get(298, 291, 9, 9), 4);
		icon_saveActive = util.pg(applet.graphicsSheet.get(307, 291, 9, 9), 4);

		// Init Window
		window_saveLevel = new SaveLevelWindow(applet);

		// Default Camera Position
		applet.originTargetX = -applet.width / 2;
		applet.originTargetY = -applet.height / 2;
		applet.originX = applet.originTargetX;
		applet.originY = applet.originTargetY;

		// Default Scene
		applet.collisions.add(new Collision(applet, "METAL_WALK_MIDDLE:0", 0, 0));

		// Default Tool
		tool = "MODIFY";

		util.loadLevel("Assets/Storage/Game/Maps/level-1.dat");
	}

	@Override
	public void draw() {
		background(29, 33, 45);

		applet.noStroke();
		applet.fill(29, 33, 45);
		applet.rect(applet.worldPosition.x - applet.originX, applet.worldPosition.y - applet.originY, applet.worldWidth,
				applet.worldHeight);

		displayGrid();

		boolean objectFocus = false;

		// View Background Objects
		for (int i = 0; i < applet.backgroundObjects.size(); i++) {
			if (tool == "MODIFY") {
				applet.backgroundObjects.get(i).updateEdit();
			}

			applet.backgroundObjects.get(i).display();

			if (applet.backgroundObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				applet.backgroundObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// View Collisions
		for (int i = 0; i < applet.collisions.size(); i++) {
			if (tool == "MODIFY") {
				applet.collisions.get(i).updateEdit();
				if (applet.collisions.get(i).focus) {
					objectFocus = true;
				}
			}

			applet.collisions.get(i).display();

			if (applet.collisions.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				applet.collisions.remove(i);
				applet.keyPressEvent = false;
			}
		}
		if (tool == "MODIFY") {
			if (!objectFocus) {
				focusedOnObject = false;

				// Loop for new Selection
				for (int i = 0; i < applet.collisions.size(); i++) {
					applet.collisions.get(i).updateEdit();
				}
			}
		}

		// View Game Objects
		for (int i = 0; i < applet.gameObjects.size(); i++) {
			if (tool == "MODIFY") {
				applet.gameObjects.get(i).updateEdit();
				if (applet.gameObjects.get(i).focus) {
					objectFocus = true;
				}
			}

			if (tool == "PLAY") {
				applet.gameObjects.get(i).update();
			}

			applet.gameObjects.get(i).display();

			// Delete
			if (applet.gameObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				applet.gameObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// View Projectiles
		for (int i = 0; i < applet.projectileObjects.size(); i++) {
			applet.projectileObjects.get(i).update();
			applet.projectileObjects.get(i).display();
		}

		// View Player
		if (tool == "PLAY") {
			applet.player.update();
			applet.player.display();
		} else {
			applet.player.display();
		}
		if (tool == "MODIFY") {
			applet.player.updateEdit();
			applet.player.displayEdit();
		}

		// Update World Origin
		if (tool == "PLAY") {
			applet.originTargetX = (int) util.clamp(applet.originTargetX,
					applet.worldPosition.x - applet.worldWidth / 2,
					applet.worldPosition.x + applet.worldWidth / 2 - applet.width);
			applet.originTargetY = (int) util.clamp(applet.originTargetY,
					applet.worldPosition.y - applet.worldHeight / 2,
					applet.worldPosition.y + applet.worldHeight / 2 - applet.height);

			applet.originX = (int) util.clamp(applet.originX, applet.worldPosition.x - applet.worldWidth / 2,
					applet.worldPosition.x + applet.worldWidth / 2 - applet.width);
			applet.originY = (int) util.clamp(applet.originY, applet.worldPosition.y - applet.worldHeight / 2,
					applet.worldPosition.y + applet.worldHeight / 2 - applet.height);
		}
	}

	public void drawUI() {
		// View Viewport Editor
		worldViewportEditor.updateEditor();
		worldViewportEditor.displayEditor();

		// Editor View
		if (tool == "MODIFY") {
			for (int i = 0; i < applet.collisions.size(); i++) {
				applet.collisions.get(i).displayEdit();
			}
			for (int i = 0; i < applet.backgroundObjects.size(); i++) {
				applet.backgroundObjects.get(i).displayEdit();
			}
			for (int i = 0; i < applet.gameObjects.size(); i++) {
				applet.gameObjects.get(i).displayEdit();
			}
		}

		// Editor Object Destination
		if (tool == "MODIFY") {
			editorItem.displayDestination();
		}

		// GUI Slots
		if (tool != "INVENTORY") {
			for (int i = 0; i < 6; i++) {
				// Display Slot
				image(slot, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10);

				// Display Item
				PGraphics img = applet.gameGraphics.get(inventory.get(i));
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
						editorItem.type = applet.gameGraphics.getType(inventory.get(i));
					}
				}
			}
		}

		// GUI Icons
		if (tool == "MOVE" || (util.hover(40, 120, 36, 36) && tool != "SAVE" && tool != "INVENTORY")) {
			if (util.hover(40, 120, 36, 36) && applet.mousePressEvent) {
				tool = "MOVE";
			}
			image(icon_eyeActive, 40, 120);
		} else {
			image(icon_eye, 40, 120);
		}
		if (tool == "MODIFY" || (util.hover(90, 120, 36, 36) && tool != "SAVE" && tool != "INVENTORY")) {
			if (util.hover(90, 120, 36, 36) && applet.mousePressEvent) {
				tool = "MODIFY";
			}
			image(icon_arrowActive, 90, 120);
		} else {
			image(icon_arrow, 90, 120);
		}
		if (tool == "INVENTORY" || (util.hover(90 + 48, 120, 36, 36) && tool != "SAVE" && tool != "INVENTORY")) {
			if (util.hover(90 + 48, 120, 36, 36) && applet.mousePressEvent) {
				tool = "INVENTORY";
			}
			image(icon_inventoryActive, 90 + 48, 120);
		} else {
			image(icon_inventory, 90 + 48, 120);
		}
		if (tool == "PLAY" || (util.hover(90 + 48 * 2, 120, 36, 36) && tool != "SAVE" && tool != "INVENTORY")) {
			if (util.hover(90 + 48 * 2, 120, 36, 36) && applet.mousePressEvent) {
				tool = "PLAY";
			}
			image(icon_playActive, 90 + 48 * 2, 120);
		} else {
			image(icon_play, 90 + 48 * 2, 120);
		}
		if (tool == "SAVE" || (util.hover(90 + 48 * 3, 120, 36, 36) && tool != "SAVE" && tool != "INVENTORY")) {
			if (util.hover(90 + 48 * 3, 120, 36, 36) && applet.mousePressEvent) {
				tool = "SAVE";
			}
			image(icon_saveActive, 90 + 48 * 3, 120);
		} else {
			image(icon_save, 90 + 48 * 3, 120);
		}

		// GUI Editor Object
		if (tool == "MODIFY") {
			editorItem.update();
			editorItem.display();
		}

		// Display Inventory
		if (tool == "INVENTORY") {
			displayCreativeInventory();
		}

		// Windows
		if (tool == "SAVE") {
			window_saveLevel.update();
			window_saveLevel.display();
		}

		// Move Tool
		if (tool == "MOVE") {
			if (applet.mousePressed) {
				applet.originTargetX += applet.pmouseX - applet.getMouseX();
				applet.originTargetY += applet.pmouseY - applet.getMouseY();
				applet.originX = applet.originTargetX;
				applet.originY = applet.originTargetY;
			}
		}

		// Change tool;
		if (tool != "SAVE") {
			if (applet.keyPressEvent) {
				if (applet.keyPress(49)) {
					tool = "MOVE";
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(50)) {
					tool = "MODIFY";
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(51)) {
					tool = "INVENTORY";
					editorItem.setMode("ITEM");
					editorItem.focus = false;
					scroll_inventory = 0;
				}
				if (applet.keyPress(52)) {
					tool = "PLAY";
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(53)) {
					tool = "SAVE";
					editorItem.setMode("CREATE");
					editorItem.focus = false;
				}
				if (applet.keyPress(69)) {
					if (tool == "INVENTORY") {
						tool = "MOVE";
						editorItem.setMode("CREATE");
						editorItem.focus = false;
					} else {
						tool = "INVENTORY";
						editorItem.setMode("ITEM");
						editorItem.focus = false;
						scroll_inventory = 0;
					}
				}
			}
		}
	}
	
	public void displayCreativeInventory() {// complete creative inventory

		// Display Background
		applet.stroke(50);
		applet.fill(0, 100);
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);

		// Display Editor Mode Items
		int x = 0;
		int y = 1;
		for (int i = 0; i < applet.gameGraphics.graphics.size(); i++) {
			if (i % 6 == 0) {
				x = 0;
				y++;
			} else {
				x++;
			}
			applet.image(slotEditor, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scroll_inventory);

			PGraphics img = applet.gameGraphics.graphics.get(i).image;
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
						editorItem.setTile(applet.gameGraphics.graphics.get(i).name);
					}
				}
			}
		}

		// Display Scroll Bar
		applet.noStroke();
		applet.fill(100, 100);
		applet.rect(applet.width - 10, applet.height / 2, 20, applet.height);
		applet.fill(100);
		applet.rect(applet.width - 10,
				PApplet.map(scroll_inventory, -getInventorySize() + applet.height - 8, 0, applet.height - 25, 125), 20,
				50);

		// Display Top Bar
		applet.noStroke();
		applet.fill(29, 33, 45);
		applet.rect(applet.width / 2, 50, applet.width, 100);

		// Display Line Separator
		applet.strokeWeight(4);
		applet.stroke(74, 81, 99);
		applet.line(0, 100, applet.width, 100);

		// Display Inventory Slots
		for (int i = 0; i < 6; i++) {
			// Display Slot
			image(slot, 20 * 4 / 2 + 10 + i * (20 * 4 + 10), 20 * 4 / 2 + 10);

			// Display Item
			PGraphics img = applet.gameGraphics.get(inventory.get(i));
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

	public void displayGrid() {// world grid
		applet.strokeWeight(1);
		applet.stroke(50);
		int x = 0;
		int y = 0;
		int l = applet.width / (16 * 4) * applet.height / (16 * 4);
		for (int i = 0; i < l; i++) {

			x++;
			if (i % applet.height / (16 * 4) == 0) {
				y++;
				x = 0;
			}
			applet.line(x * (4 * 16) - (applet.originX % (16 * 4)) - ((4 * 16) / 2), 0,
					x * (4 * 16) - (applet.originX % (16 * 4)) - ((4 * 16) / 2), applet.height);
			applet.line(0, y * (4 * 16) - (applet.originY % (16 * 4)) - ((4 * 16) / 2), applet.width,
					y * (4 * 16) - (applet.originY % (16 * 4)) - ((4 * 16) / 2));
		}
	}

	public float getInventorySize() {
		int y = 1;

		for (int i = 0; i < applet.gameGraphics.graphics.size(); i++) {
			if (i % 6 == 0) {
				y++;
			} else {
			}
		}
		return 20 * 4 + 10 + y * (20 * 4 + 10);
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		if (event.isShiftDown()) {
		} else {
			if (tool == "INVENTORY") {
				scroll_inventory -= event.getCount() * 10;
				scroll_inventory = (int) util.clamp(scroll_inventory, -getInventorySize() + applet.height - 8, 0);
			}
		}
	}
}
