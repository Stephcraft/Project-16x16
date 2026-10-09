package project_16x16.scene;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.components.Tile;
import project_16x16.components.Tile.TileType;
import project_16x16.objects.EditorItem;

/**
 * The level editor's tile palette: every placeable tile, grouped by type into
 * tabs, in a panel docked to the bottom third of the screen. Tiles are dragged
 * out of it (as the {@link EditorItem}) into the level. The Favourites tab is a
 * user-curated shortlist: tiles are added to it by dropping them on its tab (or
 * on its grid, while it's open), and removed by right-clicking them.
 */
public class TilePalette extends PClass {

	private enum Category {
		FAVOURITES("Favourites", null), BLOCKS("Blocks", TileType.COLLISION), DECOR("Decor", TileType.BACKGROUND), OBJECTS("Objects", TileType.OBJECT);

		private final String label;
		private final TileType type;

		Category(String label, TileType type) {
			this.label = label;
			this.type = type;
		}
	}

	private static final int MARGIN = 12; // panel inset from the screen edges
	private static final int PADDING = 14; // panel contents inset
	private static final int HEADER_HEIGHT = 44;
	private static final int CELL = 64;
	private static final int CELL_GAP = 6;
	private static final int PITCH = CELL + CELL_GAP;
	private static final int SCROLLBAR_WIDTH = 6;
	private static final int CLOSE_SIZE = 28;

	private static final int PANEL_FILL = 0xFF1D212D;
	private static final int PANEL_STROKE = 0xFF4A5163;
	private static final int CELL_FILL = 0xFF2A3042;
	private static final int ACCENT = 0xFF7CC8FF;

	private static final String[] DEFAULT_FAVOURITES = { "Metal", "Metal_Walk_Left:0", "Metal_Walk_Middle:0", "Metal_Walk_Middle:1",
			"Metal_Walk_Right:0", "XBox" };

	private final EditorItem editorItem;
	private final Runnable onClose;
	private final EnumMap<Category, List<Tile>> tiles = new EnumMap<>(Category.class);

	private Category category = Category.FAVOURITES;
	private int scrollRow;
	private boolean draggingScrollbar;

	// Layout (screen space), recomputed each frame since the resolution can change
	private float left, top, right, bottom;
	private float gridLeft, gridTop;
	private int columns, rows;
	private float favouritesLeft, favouritesRight, tabTop, tabBottom; // Favourites tab (drop target)

	/**
	 * @param onClose called when the palette's close button is clicked
	 */
	public TilePalette(SideScroller sideScroller, EditorItem editorItem, Runnable onClose) {
		super(sideScroller);
		this.editorItem = editorItem;
		this.onClose = onClose;
		for (Category c : Category.values()) {
			if (c != Category.FAVOURITES) {
				tiles.put(c, Tileset.getAllTiles(c.type));
			}
		}
		final List<Tile> favourites = new ArrayList<>();
		for (String name : DEFAULT_FAVOURITES) {
			final Tile tile = findTile(name);
			if (tile != null) {
				favourites.add(tile);
			}
		}
		tiles.put(Category.FAVOURITES, favourites);
	}

	private Tile findTile(String name) {
		for (Category c : Category.values()) {
			if (c != Category.FAVOURITES) {
				for (Tile tile : tiles.get(c)) {
					if (tile.getName().equals(name)) {
						return tile;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Takes a tile dropped from a drag: one dropped on the Favourites tab (or its
	 * grid, while open) is added to the favourites.
	 *
	 * @return whether the palette took the tile
	 */
	public boolean drop(String tileName, PVector screen) {
		final boolean onTab = within(screen, favouritesLeft, tabTop, favouritesRight, tabBottom);
		final boolean onGrid = category == Category.FAVOURITES && contains(screen) && screen.y > top + HEADER_HEIGHT;
		if (!onTab && !onGrid) {
			return false;
		}
		final List<Tile> favourites = tiles.get(Category.FAVOURITES);
		final Tile tile = findTile(tileName);
		if (tile != null && !favourites.contains(tile)) {
			favourites.add(tile);
		}
		return true;
	}

	/**
	 * @return whether the screen position is over the palette
	 */
	public boolean contains(PVector screen) {
		layout();
		return screen.x >= left && screen.x <= right && screen.y >= top && screen.y <= bottom;
	}

	public void mouseWheel(MouseEvent event) {
		scrollRow += event.getCount();
	}

	/**
	 * Draws the palette and handles its mouse input.
	 */
	public void display() {
		layout();
		final PVector mouse = applet.getMouseCoordScreen();

		applet.pushStyle();
		applet.rectMode(PConstants.CORNERS);
		applet.imageMode(PConstants.CENTER);

		// Panel
		applet.strokeWeight(4);
		applet.stroke(PANEL_STROKE);
		applet.fill(PANEL_FILL);
		applet.rect(left, top, right, bottom, 10);

		displayHeader(mouse);
		final Tile hovered = displayGrid(mouse);

		if (hovered != null && !editorItem.focus) {
			displayTooltip(hovered.getName(), mouse);
		}
		applet.popStyle();
	}

	private void layout() {
		left = MARGIN;
		right = applet.width - MARGIN;
		bottom = applet.height - MARGIN;
		top = applet.height * 2 / 3f;

		gridLeft = left + PADDING;
		gridTop = top + HEADER_HEIGHT + 10;
		final float gridWidth = right - PADDING - SCROLLBAR_WIDTH - 10 - gridLeft;
		columns = Math.max(1, (int) ((gridWidth + CELL_GAP) / PITCH));
		rows = Math.max(1, (int) ((bottom - PADDING - gridTop + CELL_GAP) / PITCH));
	}

	/**
	 * Category tabs, usage hint and close button.
	 */
	private void displayHeader(PVector mouse) {
		tabTop = top + 8;
		tabBottom = top + HEADER_HEIGHT - 4;
		applet.textSize(22);
		applet.textAlign(PConstants.CENTER, PConstants.CENTER);
		float x = left + PADDING;
		for (Category c : Category.values()) {
			final String label = c.label + "  " + tiles.get(c).size();
			final float w = applet.textWidth(label) + 28;
			final boolean hover = within(mouse, x, tabTop, x + w, tabBottom);
			if (hover && applet.mousePressEvent && c != category) {
				category = c;
				scrollRow = 0;
			}
			applet.noStroke();
			applet.fill(c == category ? 0xFF323A50 : hover ? 0xFF282E40 : PANEL_FILL);
			if (c == Category.FAVOURITES) {
				favouritesLeft = x;
				favouritesRight = x + w;
				if (editorItem.focus) { // a tile is being dragged: show this tab accepts it
					applet.stroke(ACCENT);
					applet.strokeWeight(2);
					if (hover) {
						applet.fill(0xFF2E4A63);
					}
				}
			}
			applet.rect(x, tabTop, x + w, tabBottom, 6);
			if (c == category) {
				applet.fill(ACCENT);
				applet.rect(x + 8, tabBottom - 3, x + w - 8, tabBottom);
			}
			applet.fill(c == category ? 255 : 160);
			applet.text(label, x + w / 2, (tabTop + tabBottom) / 2 - 2);
			x += w + 6;
		}

		// Close button
		final float cx = right - PADDING - CLOSE_SIZE / 2f;
		final float cy = (tabTop + tabBottom) / 2;
		final boolean closeHover = within(mouse, cx - CLOSE_SIZE / 2f, cy - CLOSE_SIZE / 2f, cx + CLOSE_SIZE / 2f, cy + CLOSE_SIZE / 2f);
		applet.noStroke();
		applet.fill(closeHover ? 0xFF8C3A3A : 0xFF323A50);
		applet.rect(cx - CLOSE_SIZE / 2f, cy - CLOSE_SIZE / 2f, cx + CLOSE_SIZE / 2f, cy + CLOSE_SIZE / 2f, 6);
		applet.stroke(255, closeHover ? 255 : 190);
		applet.strokeWeight(3);
		final float r = CLOSE_SIZE / 2f - 9;
		applet.line(cx - r, cy - r, cx + r, cy + r);
		applet.line(cx - r, cy + r, cx + r, cy - r);
		if (closeHover && applet.mousePressEvent) {
			onClose.run();
		}

		// Usage hint (only if there's room)
		applet.textSize(18);
		applet.textAlign(PConstants.RIGHT, PConstants.CENTER);
		final String hint = category == Category.FAVOURITES ? "drop tiles on this tab to add  -  right-click to remove"
				: "drag a tile into the level, or onto Favourites";
		if (cx - CLOSE_SIZE - applet.textWidth(hint) > x + 12) {
			applet.fill(255, 110);
			applet.text(hint, cx - CLOSE_SIZE, cy - 2);
		}

		applet.stroke(PANEL_STROKE);
		applet.strokeWeight(2);
		applet.line(left + PADDING, top + HEADER_HEIGHT + 2, right - PADDING, top + HEADER_HEIGHT + 2);
	}

	/**
	 * Tiles of the current category, and the scrollbar.
	 *
	 * @return the tile under the mouse, if any
	 */
	private Tile displayGrid(PVector mouse) {
		final List<Tile> list = tiles.get(category);
		final int totalRows = (list.size() + columns - 1) / columns;
		final int maxScroll = Math.max(0, totalRows - rows);
		updateScrollbar(mouse, totalRows, maxScroll);
		scrollRow = PApplet.constrain(scrollRow, 0, maxScroll);

		Tile hovered = null;
		Tile removed = null;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				final int index = (scrollRow + r) * columns + c;
				if (index >= list.size()) {
					break;
				}
				final Tile tile = list.get(index);
				final float x = gridLeft + c * PITCH;
				final float y = gridTop + r * PITCH;
				final boolean hover = within(mouse, x, y, x + CELL, y + CELL);
				final boolean held = editorItem.focus && tile.getName().equals(editorItem.id);

				applet.fill(CELL_FILL);
				if (hover || held) {
					applet.stroke(ACCENT);
					applet.strokeWeight(2);
				} else {
					applet.noStroke();
				}
				applet.rect(x, y, x + CELL, y + CELL, 6);
				drawTile(tile.getPImage(), x + CELL / 2f, y + CELL / 2f);

				if (hover) {
					hovered = tile;
					if (applet.mousePressEvent && applet.mouseButton == PConstants.LEFT) { // pick up
						editorItem.setTile(tile.getName());
						editorItem.focus = true;
					} else if (applet.mousePressEvent && applet.mouseButton == PConstants.RIGHT && category == Category.FAVOURITES) {
						removed = tile;
					}
				}
			}
		}
		if (removed != null) {
			list.remove(removed);
		}
		if (list.isEmpty()) {
			applet.textSize(20);
			applet.textAlign(PConstants.LEFT, PConstants.TOP);
			applet.fill(255, 110);
			applet.text("No favourites yet: drag tiles from the other tabs onto the Favourites tab.", gridLeft, gridTop + 8);
		}
		return hovered;
	}

	private void updateScrollbar(PVector mouse, int totalRows, int maxScroll) {
		if (maxScroll == 0) {
			draggingScrollbar = false;
			return;
		}
		final float x = right - PADDING - SCROLLBAR_WIDTH;
		final float trackTop = gridTop;
		final float trackBottom = gridTop + rows * PITCH - CELL_GAP;
		final float thumbHeight = Math.max(20, (trackBottom - trackTop) * rows / totalRows);

		if (applet.mousePressEvent && within(mouse, x - 8, trackTop, x + SCROLLBAR_WIDTH + 8, trackBottom)) {
			draggingScrollbar = true;
		}
		if (!applet.mousePressed) {
			draggingScrollbar = false;
		}
		if (draggingScrollbar) {
			final float t = PApplet.map(mouse.y, trackTop + thumbHeight / 2, trackBottom - thumbHeight / 2, 0, 1);
			scrollRow = Math.round(PApplet.constrain(t, 0, 1) * maxScroll);
		}
		scrollRow = PApplet.constrain(scrollRow, 0, maxScroll);

		final float thumbTop = trackTop + (trackBottom - trackTop - thumbHeight) * scrollRow / maxScroll;
		applet.noStroke();
		applet.fill(0xFF2A3042);
		applet.rect(x, trackTop, x + SCROLLBAR_WIDTH, trackBottom, 3);
		applet.fill(draggingScrollbar ? ACCENT : 0xFF6A7390);
		applet.rect(x, thumbTop, x + SCROLLBAR_WIDTH, thumbTop + thumbHeight, 3);
	}

	/**
	 * Draws a tile scaled to fit its cell. Tiles are stored at 4x, so scaling in
	 * quarter steps keeps their pixels square.
	 */
	private void drawTile(PImage image, float x, float y) {
		final float size = Math.max(image.width, image.height);
		final float fit = CELL - 12;
		float scale = 0.75f;
		while (scale > 0.25f && size * scale > fit) {
			scale -= 0.25f;
		}
		if (size * scale > fit) {
			scale = fit / size;
		}
		applet.image(image, x, y, image.width * scale, image.height * scale);
	}

	/**
	 * Drawn last, so it's above every tile.
	 */
	private void displayTooltip(String text, PVector mouse) {
		applet.textSize(18);
		applet.textAlign(PConstants.LEFT, PConstants.CENTER);
		final float w = applet.textWidth(text) + 16;
		final float h = 28;
		final float x = Math.min(mouse.x + 14, applet.width - w - 4);
		final float y = mouse.y - h - 8; // above the cursor
		applet.fill(12, 14, 20, 235);
		applet.stroke(PANEL_STROKE);
		applet.strokeWeight(2);
		applet.rect(x, y, x + w, y + h, 4);
		applet.fill(255);
		applet.text(text, x + 8, y + h / 2 - 2);
	}

	private static boolean within(PVector p, float x1, float y1, float x2, float y2) {
		return p.x >= x1 && p.x <= x2 && p.y >= y1 && p.y <= y2;
	}
}
