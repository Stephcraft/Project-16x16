package project_16x16.objects;

import java.lang.reflect.Constructor;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.PClass;
import project_16x16.SideScroller;
import project_16x16.Tileset;
import project_16x16.Utility;
import project_16x16.components.Tile.TileType;
import project_16x16.scene.GameplayScene;

/**
 * A tile being dragged in the level editor (from the tile palette). Dropping it
 * in the level places it there (unless it would overlap a solid object);
 * dropping it on editor UI hands it to that UI (eg. the palette's favourites).
 */
public class EditorItem extends PClass {

	private PVector position;
	public boolean focus;

	private PImage image;

	public String id;
	public TileType type;

	private GameplayScene gameplayScene;

	public EditorItem(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller);

		this.gameplayScene = gameplayScene;

		setTile("BOX");

		position = new PVector(0, 0);
	}

	public void display() {
		if (focus) {
			applet.image(image, position.x, position.y, image.width * (float) 0.5, image.height * (float) 0.5);
		}
	}

	public void update() {
		if (focus) {
			position = applet.getMouseCoordScreen();
			if (applet.mouseReleaseEvent) {
				focus = false;
				drop();
			}
		}
	}

	private void drop() {
		final PVector mouse = applet.getMouseCoordScreen();
		if (gameplayScene.currentMode.dropTile(id, mouse) || gameplayScene.isOverUI(mouse) || !canPlace()) {
			return; // cancelled
		}

		final PVector realPos = placement();
		EditableObject c = null;
		switch (type) {
			case COLLISION:
				c = new CollidableObject(applet, gameplayScene, id, 0, 0);
				break;
			case BACKGROUND:
				c = new BackgroundObject(applet, gameplayScene, id, 0, 0);
				break;
			case OBJECT:
				try {
					Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(id);
					Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
					c = (GameObject) ctor.newInstance(new Object[] { applet, gameplayScene });
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
		if (c != null) {
			c.position.set(realPos);
			c.focus();
			gameplayScene.objects.add(c);
		}
	}

	/**
	 * @return the (grid-snapped) world position the item would be placed at
	 */
	private PVector placement() {
		final PVector mouse = applet.getMouseCoordGame();
		return new PVector(Utility.roundToNearest(mouse.x, SideScroller.snapSize), Utility.roundToNearest(mouse.y, SideScroller.snapSize));
	}

	/**
	 * Solid items can't be placed over other solid objects.
	 */
	private boolean canPlace() {
		if (type == TileType.BACKGROUND) {
			return true;
		}
		final PVector p = placement();
		return gameplayScene.isAreaFree(p.x, p.y, image.width, image.height, o -> false);
	}

	/**
	 * Outlines where the item would be placed: red if it can't be placed there.
	 */
	public void displayDestination() {
		if (focus && !gameplayScene.isOverUI(applet.getMouseCoordScreen())) {
			final PVector p = placement();
			applet.strokeWeight(2);
			if (canPlace()) {
				applet.stroke(0, 255, 200);
			} else {
				applet.stroke(255, 60, 60);
			}
			applet.noFill();
			applet.rect(p.x, p.y, image.width, image.height);
		}
	}

	public void setTile(String name) {
		image = Tileset.getTile(name);
		type = Tileset.getTileType(name);

		id = name;
	}
}
