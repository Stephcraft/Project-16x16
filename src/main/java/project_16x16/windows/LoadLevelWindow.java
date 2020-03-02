package project_16x16.windows;

import project_16x16.scene.GameplayScene;
import project_16x16.PClass;
import project_16x16.Main;
import project_16x16.Util;
import project_16x16.objects.BackgroundObject;
import project_16x16.objects.CollidableObject;
import project_16x16.ui.List;
import project_16x16.ui.Notifications;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class LoadLevelWindow extends PClass {

	ArrayList<CollidableObject> collidableObjects;
	ArrayList<BackgroundObject> backgroundObjects;
	final String path = "src/main/resources/Storage/Game/Maps/save/";
	String picked;
	// Map editor Scene
	public GameplayScene scene;
	public List list;
	File f;

	public LoadLevelWindow(Main a, GameplayScene scene) {

		super(a);
		collidableObjects = new ArrayList<>();
		backgroundObjects = new ArrayList<>();
		picked = "";
		this.scene = scene;
		f = new File(path);
		f.mkdirs();
		File[] files = f.listFiles(pathname -> {
			String name = pathname.getName().toLowerCase();
			return name.endsWith(".dat") && pathname.isFile();
		});

		assert files != null;
		list = new List(a, Arrays.stream(files).map(File::getName).toArray(String[]::new), 30);
		list.setSizeH(200);
		list.setPosition(applet.width / 2 + 400, 325);
		list.setConfirmButton("Confirm", applet.width / 2 + 400, 500);
		list.setCancelButton("Cancel", applet.width / 2 + 400, 550);
	}

	public void display() {
		// Display Privacy Area
		applet.fill(0, 100);
		applet.noStroke();
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);

		// Display Window
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.width / 2, applet.height / 2, applet.width, applet.height);
		applet.stroke(255, 255, 255);
		applet.rect(500, applet.height / 2, 800, 600);

		// Display Window Title
		applet.pushMatrix();
		applet.fill(255);
		applet.textSize(30);
		applet.textAlign(CENTER, CENTER);
		applet.text("Load Level", applet.width / 2 + 400, applet.height / 2 - 200);
		applet.popMatrix();
		// Display Load Press
		list.display();
		showLevelPreviewWindow();
	}

	public void update() {
		list.update();
		confirmButton();
		cancelButton();
	}

	public void confirmButton() {
		if (!list.getElement().isEmpty() && !picked.equals(list.getElement())) {
			picked = list.getElement();
			buildLevelPreviewWindow(path + list.getElement());
		}
		if (list.getConfirmPress() && !list.getElement().isEmpty()) {
			scene.loadLevel(path + list.getElement());
			Notifications.addNotification("Level Loaded", "Loaded "+ list.getElement() + ".");
			list.resetElement();
			scene.tool = GameplayScene.Tools.MODIFY;
		} else if (list.getConfirmPress() && list.getElement().isEmpty())
			scene.tool = GameplayScene.Tools.MODIFY;
	}

	public void cancelButton() {
		if (list.getCancelPress()) {
			scene.tool = GameplayScene.Tools.MODIFY;
			list.resetElement();
		}
	}

	public void buildLevelPreviewWindow(String save) {
		String[] script = applet.loadStrings(save);
		if (script == null) {
			return;
		}

		String scriptD = Util.decrypt(PApplet.join(script, "\n")); // decrypt save data
		JSONArray data = JSONArray.parse(scriptD); // Parse JSON

		if (data == null) {
			System.err.println("Failed to parse level data to JSON. File is probably corrupt.");
			return;
		}
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxX = Integer.MIN_VALUE;
		collidableObjects.clear();
		backgroundObjects.clear();
		// check for boundaries
		for (int i = 0; i < data.size(); i++) {
			JSONObject item = data.getJSONObject(i);
			String type = item.getString("type");
			if (type == null) {
				continue;
			}
			maxX = Math.max(maxX, item.getInt("x"));
			minX = Math.min(minX, item.getInt("x"));
			maxY = Math.max(maxY, item.getInt("y"));
			minY = Math.min(minY, item.getInt("y"));
		}
		// Create Level
		for (int i = 0; i < data.size(); i++) {
			JSONObject item = data.getJSONObject(i);
			String type = item.getString("type");
			if (type == null) {
				continue;
			}
			switch (type) { // Read Main
			case "COLLISION":
				CollidableObject collision = new CollidableObject(applet, scene);
				try {
					collision.setGraphic(item.getString("id"));
				} catch (Exception e) {
					collision.width = 64;
					collision.height = 64;
				}
				collision.setImageWidth(20);
				collision.setImageHeight(20);
				collision.pos.x = PApplet.map(item.getInt("x"), minX + 100, maxX - 100, 300, 600);
				collision.pos.y = PApplet.map(item.getInt("y"), minY + 100, maxY - 100, 300, 600);

				collidableObjects.add(collision); // SideScrollerend To Level
				break;
			case "BACKGROUND":
				BackgroundObject backgroundObject = new BackgroundObject(applet, scene);
				backgroundObject.setGraphic(item.getString("id"));
				backgroundObject.setImageWidth(20);
				backgroundObject.setImageHeight(20);
				backgroundObject.pos.x = PApplet.map(item.getInt("x"), minX + 100, maxX - 100, 300, 600);
				backgroundObject.pos.y = PApplet.map(item.getInt("y"), minY + 100, maxY - 100, 300, 600);
				backgroundObjects.add(backgroundObject); // SideScrollerend To Level
				break;
			default:
				break;
			}
		}
	}

	public void showLevelPreviewWindow() {
		for (CollidableObject collidableObject : collidableObjects) {
			collidableObject.display();
		}
		for (BackgroundObject backgroundObject : backgroundObjects) {
			backgroundObject.display();
		}
	}

}
