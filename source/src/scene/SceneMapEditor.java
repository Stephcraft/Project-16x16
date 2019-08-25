package scene;

import java.util.ArrayList;
import java.util.Iterator;

import objects.EditorItem;
import objects.CollidableObject;
import objects.EditableObject;
import processing.core.*;
import processing.event.MouseEvent;
import projectiles.ProjectileObject;
import scene.components.WorldViewportEditor;
import sidescroller.GameGraphics.Graphic;
import sidescroller.SideScroller;
import windows.OptionsInterface;
import ui.Anchor;
import ui.ScrollBarVertical;
import windows.SaveLevelWindow;

public class SceneMapEditor extends PScene {

	// Graphics Slots
	private PImage slot;
	private PImage slotEditor;

	// Graphics Icon
	PGraphics icon_eye;
	PGraphics icon_arrow;
	PGraphics icon_inventory;
	PGraphics icon_play;
	PGraphics icon_save;
	PGraphics icon_options;
	PGraphics icon_eyeActive;
	PGraphics icon_arrowActive;
	PGraphics icon_inventoryActive;
	PGraphics icon_playActive;
	PGraphics icon_saveActive;
	PGraphics icon_optionsActive;
	PGraphics icon_leavePlayMode;
	PGraphics icon_leavePlayModeActive;

	// Windows
	public SaveLevelWindow window_saveLevel;
	public OptionsInterface window_options;

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

	// Windows
	private SaveLevelWindow window_saveLevel;

	// Editor Item
	private EditorItem editorItem;

	// Editor Viewport
	public WorldViewportEditor worldViewportEditor;

	// Scroll Bar
	private ScrollBarVertical scrollBar;

	public enum Tools {
		MOVE, MODIFY, INVENTORY, PLAY, SAVE, OPTIONS,
	}

	public static Tools tool;

	private ArrayList<String> inventory;

//	public boolean focusedOnObject; // mutex

	public EditableObject focusedObject = null;

	public boolean edit;

	private int scroll_inventory;

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

		// Get Icon Graphics from graphics-sheet.png

		icon_eye = util.pg(applet.graphicsSheet.get(267, 302, 11, 8), 4);
		icon_arrow = util.pg(applet.graphicsSheet.get(279, 301, 9, 9), 4);
		icon_inventory = util.pg(applet.graphicsSheet.get(289, 301, 9, 9), 4);
		icon_play = util.pg(applet.graphicsSheet.get(298, 301, 9, 9), 4);
		icon_save = util.pg(applet.graphicsSheet.get(307, 301, 9, 9), 4);
		icon_options = util.pg(applet.graphicsSheet.get(327, 302, 8, 8), 4);


		icon_eyeActive = util.pg(applet.graphicsSheet.get(267, 292, 11, 8), 4);
		icon_arrowActive = util.pg(applet.graphicsSheet.get(279, 291, 9, 9), 4);
		icon_inventoryActive = util.pg(applet.graphicsSheet.get(289, 291, 9, 9), 4);
		icon_playActive = util.pg(applet.graphicsSheet.get(298, 291, 9, 9), 4);
		icon_saveActive = util.pg(applet.graphicsSheet.get(307, 291, 9, 9), 4);
		icon_optionsActive = util.pg(applet.graphicsSheet.get(327, 292, 8, 8), 4);
		
		icon_leavePlayMode = util.pg(applet.graphicsSheet.get(306, 321, 9, 9), 4); 
		icon_leavePlayModeActive = util.pg(applet.graphicsSheet.get(306, 311, 9, 9), 4); 

		// Init Window
		window_saveLevel = new SaveLevelWindow(applet);
		window_options = new OptionsInterface(applet);

		// Init ScollBar
		Anchor scrollBarAnchor = new Anchor(applet, -20, 150, 20, 50);
		scrollBarAnchor.anchorOrigin = Anchor.AnchorOrigin.TopRight;
		scrollBarAnchor.stretch = Anchor.Stretch.Vertical;
		scrollBar = new ScrollBarVertical(applet, scrollBarAnchor);

		// Default Scene
		applet.collidableObjects.add(new CollidableObject(applet, "METAL_WALK_MIDDLE:0", 0, 0));

		// Default Tool
		tool = Tools.MODIFY;

		util.loadLevel(SideScroller.LEVEL); // TODO change level
	}

	/**
	 * Draw scene elements that are below (affected by) the camera.
	 */
	@Override
	public void draw() {
		background(29, 33, 45);

		applet.noStroke();
		applet.fill(29, 33, 45);

		//		applet.rect(applet.worldPosition.x, applet.worldPosition.y, applet.worldWidth,
		//				applet.worldHeight); // todo


		if (tool == Tools.MODIFY) {
			displayGrid();
			if (applet.mousePressEvent && focusedObject != null) {
				focusedObject.updateEdit(); // enforce one item selected at once
			}
		}

		// View Background Objects
		for (int i = 0; i < applet.backgroundObjects.size(); i++) {
			if (tool == Tools.MODIFY) {
				applet.backgroundObjects.get(i).updateEdit();
			}

			applet.backgroundObjects.get(i).display();

			if (applet.backgroundObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				applet.backgroundObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// View Collidable objects
		for (int i = 0; i < applet.collidableObjects.size(); i++) {
			if (tool == Tools.MODIFY) {
				applet.collidableObjects.get(i).updateEdit();
			}

			applet.collidableObjects.get(i).display();

			if (applet.collidableObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				applet.collidableObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// View Game Objects (player-interactable objects)
		for (int i = 0; i < applet.gameObjects.size(); i++) {
			if (tool == Tools.MODIFY) {
				applet.gameObjects.get(i).updateEdit();
			}

			if (tool == Tools.PLAY) {
				applet.gameObjects.get(i).update();
			}

			applet.gameObjects.get(i).display();

			if (SideScroller.DEBUG) {
				applet.strokeWeight(2);
				applet.noFill();
				applet.stroke(255, 190, 200);
				applet.rect(applet.gameObjects.get(i).pos.x, applet.gameObjects.get(i).pos.y,
						applet.gameObjects.get(i).width, applet.gameObjects.get(i).height);
				applet.noStroke();
				applet.fill(255);
				applet.ellipse(applet.gameObjects.get(i).pos.x, applet.gameObjects.get(i).pos.y, 5, 5);
				applet.noFill();
			}

			// Delete
			if (applet.gameObjects.get(i).focus && applet.keyPress(8) && applet.keyPressEvent) {
				applet.gameObjects.remove(i);
				applet.keyPressEvent = false;
			}
		}

		// Editor View
		if (tool == Tools.MODIFY) {
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
		if (tool == Tools.MODIFY) {
			editorItem.displayDestination();
		}

		// View Projectiles
		Iterator<ProjectileObject> i = applet.projectileObjects.iterator();
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
				applet.player.updateEdit();
				applet.player.displayEdit();
				editorItem.displayDestination();
				applet.collidableObjects.forEach(o -> o.displayEdit());
				applet.backgroundObjects.forEach(o -> o.displayEdit());
				applet.gameObjects.forEach(o -> o.displayEdit());
				break;
			case PLAY :
				applet.player.update();
				break;
			case MOVE :
			case INVENTORY :
			case SAVE :
				break;
			default :
				break;
		}
		if (tool == Tools.PLAY) {
			applet.player.updateEdit();
			applet.player.displayEdit();
		}

		applet.player.display();

		// View Viewport Editor
//		worldViewportEditor.updateEditor(); // TODO
//		worldViewportEditor.displayEditor(); // TODO
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
				PImage img = applet.gameGraphics.get(inventory.get(i));
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
		if(tool != Tools.PLAY) {
			if (tool == Tools.MOVE || (util.hover(40, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
				if (util.hover(40, 120, 36, 36) && applet.mousePressEvent) {
					tool = Tools.MOVE;
				}
				image(icon_eyeActive, 40, 120);
			} else {
				image(icon_eye, 40, 120);
			}
			if (tool == Tools.MODIFY || (util.hover(90, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
				if (util.hover(90, 120, 36, 36) && applet.mousePressEvent) {
					tool = Tools.MODIFY;
				}
				image(icon_arrowActive, 90, 120);
			} else {
				image(icon_arrow, 90, 120);
			}
			if (tool == Tools.INVENTORY
					|| (util.hover(90 + 48, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
				if (util.hover(90 + 48, 120, 36, 36) && applet.mousePressEvent) {
					tool = Tools.INVENTORY;
				}
				image(icon_inventoryActive, 90 + 48, 120);
			} else {
				image(icon_inventory, 90 + 48, 120);
			}
			if (tool == Tools.PLAY
					|| (util.hover(90 + 48 * 2, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
				if (util.hover(90 + 48 * 2, 120, 36, 36) && applet.mousePressEvent) {
					tool = Tools.PLAY;
				}
				image(icon_playActive, 90 + 48 * 2, 120);
			} else {
				image(icon_play, 90 + 48 * 2, 120);
			}
			if (tool == Tools.SAVE
					|| (util.hover(90 + 48 * 3, 120, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
				if (util.hover(90 + 48 * 3, 120, 36, 36) && applet.mousePressEvent) {
					tool = Tools.SAVE;
				}
				image(icon_saveActive, 90 + 48 * 3, 120);
			} else {
				image(icon_save, 90 + 48 * 3, 120);
			}
		}else {
			if(tool != Tools.PLAY
					|| (util.hover(80, 850, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
				if (util.hover(80, 850, 36, 36) && applet.mousePressEvent) {
					tool = Tools.MOVE;
				}
				image(icon_leavePlayModeActive, 80, 850);
				applet.text("Leave Play Mode",  172, 820);
			} else {
				image(icon_leavePlayMode, 80, 850);
			}
		}
		if(tool == Tools.OPTIONS
				|| (util.hover(90 + 48 * 30, 850, 36, 36) && tool != Tools.SAVE && tool != Tools.INVENTORY)) {
			if (util.hover(90 + 48 * 30, 850, 36, 36) && applet.mousePressEvent) {
				tool = Tools.OPTIONS;
			}
			image(icon_optionsActive, 90 + 48 * 30, 850);
			applet.text("Option Menu",  1585, 820);
		} else {
			image(icon_options, 90 + 48 * 30, 850);
		}


		// GUI Editor Object
		if (tool == Tools.MODIFY) {
			editorItem.update();
			editorItem.display();
		}

		// Display Inventory
		if (tool == Tools.INVENTORY) {
			displayCreativeInventory();
		}

		// Windows
		if (tool == Tools.SAVE) {
			window_saveLevel.update();
			window_saveLevel.display();
		}

		if(tool == Tools.OPTIONS)
			window_options.display();
		window_options.update();
		window_options.displayOptions();

		// Display player health
		// TODO player health	


		// Move Tool
		if (tool == Tools.MOVE) {
			if (applet.mousePressed) {
				//				applet.originTargetX += applet.pmouseX - applet.getMouseX();
				//				applet.originTargetY += applet.pmouseY - applet.getMouseY();
				//				applet.originX = applet.originTargetX;
				//				applet.originY = applet.originTargetY;
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
				window_saveLevel.update();
				window_saveLevel.display();
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
				if(applet.keyPress(79)) {
					if(tool != Tools.OPTIONS) {
						tool = Tools.OPTIONS;
						editorItem.setMode("CREATE");
						editorItem.focus = false;
					}
					else {
						tool = Tools.MOVE;
					}
				}
			}
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
		for (Graphic g : applet.gameGraphics.graphics.values()) {
			if (index % 6 == 0) { // show 6 items per row
				x = 0;
				y++;
			} else {
				x++;
			}
			applet.image(slotEditor, 20 * 4 / 2 + 10 + x * (20 * 4 + 10), y * (20 * 4 + 10) + scroll_inventory);
			PImage img = g.image;
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
						editorItem.setTile(g.name);
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
			PImage img = applet.gameGraphics.get(inventory.get(i));
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
		applet.strokeWeight(2);
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
			//			applet.line(x * (4 * 16) - (applet.originX % (16 * 4)) - ((4 * 16) / 2), 0,
			//					x * (4 * 16) - (applet.originX % (16 * 4)) - ((4 * 16) / 2), applet.height);
			//			applet.line(0, y * (4 * 16) - (applet.originY % (16 * 4)) - ((4 * 16) / 2), applet.width,
			//					y * (4 * 16) - (applet.originY % (16 * 4)) - ((4 * 16) / 2)); // todo

			applet.line(x * (4 * 16) - (0 % (16 * 4)) - ((4 * 16) / 2), 0,
					x * (4 * 16) - (0 % (16 * 4)) - ((4 * 16) / 2), applet.height);
			applet.line(0, y * (4 * 16) - (0 % (16 * 4)) - ((4 * 16) / 2), applet.width,
					y * (4 * 16) - (0 % (16 * 4)) - ((4 * 16) / 2)); // todo
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
			if (tool == Tools.INVENTORY) {
				scrollBar.mouseWheel(event);
				scroll_inventory = (int) PApplet.map(scrollBar.barLocation, 1, 0,
						-getInventorySize() + applet.height - 8, 0);
			}
		}
	}
}
