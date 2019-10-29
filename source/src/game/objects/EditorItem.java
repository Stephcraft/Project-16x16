package game.objects;

import java.lang.reflect.Constructor;

import processing.core.*;
import game.engine.scene.GameplayScene;
import game.engine.sidescroller.PClass;
import game.engine.sidescroller.Tileset;
import game.engine.sidescroller.Tileset.tileType;
import game.engine.sidescroller.Util;

public class EditorItem extends PClass {

	private PVector pos;
	public boolean focus;

	private PImage image;

	public String id;
	public tileType type;

	private String mode;

	private GameplayScene gameplayScene;

	public EditorItem() {
		super();

		this.gameplayScene = applet.getGame();
		
		setTile("BOX");
		setMode("CREATE");

		pos = new PVector(0, 0);
	}

	public void display() {
		if (focus) {
			applet.image(image, pos.x, pos.y, image.width * (float) 0.5, image.height * (float) 0.5);
		}
	}

	public void update() {

		if (focus) {
			pos = applet.getMouseCoordScreen();

			if (applet.isMouseReleaseEvent()) {
				focus = false;

				if (mode.equals("CREATE")) {
					// transform from screen mouse pos to game pos
					// Create new instance from dragged icon

					PVector realPos = applet.getCamera().getDispToCoord(
							new PVector(Util.roundToNearest(applet.getMouseCoordScreen().x, applet.getSnapSize()),
									Util.roundToNearest(applet.getMouseCoordScreen().y, applet.getSnapSize())));
					switch (type) {
						case COLLISION :
							CollidableObject c = new CollidableObject(id, 0, 0);
							c.getBaseObjectData().getPos().x = realPos.x;
							c.getBaseObjectData().getPos().y = realPos.y;
							c.focus();
							gameplayScene.collidableObjects.add(c);
							break;
						case BACKGROUND :
							BackgroundObject bObject = new BackgroundObject(id, 0, 0);
							bObject.getBaseObjectData().getPos().x = realPos.x;
							bObject.getBaseObjectData().getPos().y = realPos.y;
							bObject.focus();
							gameplayScene.backgroundObjects.add(bObject);
							break;
						case OBJECT :
							try {
								Class<? extends GameObject> gameObjectClass = Tileset.getObjectClass(id);
								Constructor<?> ctor = gameObjectClass.getDeclaredConstructors()[0];
								GameObject obj = (GameObject) ctor.newInstance(new Object[] {  });
								obj.focus();
								obj.getBaseObjectData().getPos().x = realPos.x;
								obj.getBaseObjectData().getPos().y = realPos.y;
								gameplayScene.gameObjects.add(obj);
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
		}
	}

	public void displayDestination() {
		if (focus) {
			applet.strokeWeight(1);
			applet.stroke(0, 255, 200);
			applet.noFill();
			applet.rect(round((applet.getMouseCoordGame().x) / 4) * 4, round((applet.getMouseCoordGame().y) / 4) * 4, image.width,
					image.height);
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
