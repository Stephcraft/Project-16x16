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

public class EditorItem extends PClass {

	private PVector position;
	public boolean focus;

	private PImage image;

	public String id;
	public TileType type;

	private String mode;

	private GameplayScene gameplayScene;

	public EditorItem(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller);

		this.gameplayScene = gameplayScene;

		setTile("BOX");
		setMode("CREATE");

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
				if (mode.equals("CREATE")) {
					// transform from screen mouse pos to game pos
					// Create new instance from dragged icon

					PVector realPos = applet.camera.getDispToCoord(new PVector(Utility.roundToNearest(applet.getMouseCoordScreen().x, SideScroller.snapSize),
							Utility.roundToNearest(applet.getMouseCoordScreen().y, SideScroller.snapSize)));
					EditableObject c = null;
					switch (type) {
						case COLLISION:
							c = new CollidableObject(applet, gameplayScene, id, 0, 0);
							c.position.set(realPos);
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
			}
		}
	}

	public void displayDestination() {
		if (focus) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(round((applet.getMouseCoordGame().x) / 4) * 4, round((applet.getMouseCoordGame().y) / 4) * 4, image.width, image.height);
		}
	}

	public void setMode(String m) {
		mode = m;
	}

	public void setTile(String name) {
		image = Tileset.getTile(name);
		type = Tileset.getTileType(name);

		id = name;
	}
}
