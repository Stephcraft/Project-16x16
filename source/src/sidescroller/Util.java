package sidescroller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
import java.util.ArrayList;

import objects.BackgroundObject;
import objects.Collision;
import objects.GameObject;
import processing.core.*;
import processing.data.*;
import scene.PScene;
import scene.SceneMapEditor;

public class Util {
	SideScroller applet;

	public Util(SideScroller a) {
		applet = a;
	}

	public PGraphics pg(PImage img) {
		return pg(img, 1);
	}

	/**
	 * 
	 * @param img
	 * @param scl Scale
	 * @return
	 */
	public PGraphics pg(PImage img, float scl) {
		PGraphics pg = applet.createGraphics((int) (img.width * scl), (int) (img.height * scl));

		pg.noSmooth();
		pg.beginDraw();
		pg.clear();
		pg.image(img, 0, 0, (int) (img.width * scl), (int) (img.height * scl));
		pg.endDraw();

		return pg;
	}
	
	public ArrayList<PGraphics> pg(ArrayList<PImage> imgs, float scl) {
		ArrayList<PGraphics> pgs = new ArrayList<PGraphics>();
		for (PImage img : imgs)
		{
			pgs.add(pg(img, scl));
		}
		return pgs;
	}

	public PGraphics blur(PGraphics img, float b) {
		PGraphics pg = applet.createGraphics(img.width, img.height);

		pg.noSmooth();
		pg.beginDraw();
		pg.clear();
		pg.image(img, 0, 0, img.width, img.height);
		pg.filter(PConstants.BLUR, b);
		pg.endDraw();

		return pg;
	}

	public PGraphics warp(PGraphics source, float waveAmplitude, float numWaves) {
		int w = source.width, h = source.height;
		PImage destination = applet.createImage(w, h, PConstants.ARGB);
		source.loadPixels();
		destination.loadPixels();

		float yToPhase = 2 * PConstants.PI * numWaves / h; // conversion factor from y values to radians.

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int newX, newY;
				newX = PApplet.parseInt(x + waveAmplitude * PApplet.sin(y * yToPhase));
				newY = y;
				int c;
				if (newX >= w || newX < 0 || newY >= h || newY < 0) {
					c = applet.color(0, 0, 0, 0);
				} else {
					c = source.pixels[newY * w + newX];
				}
				destination.pixels[y * w + x] = c;
			}
		}
		return pg(destination);
	}

	public PGraphics scale(PGraphics pBuffer, int scaling) {
		PImage originalImage = pBuffer;
		PImage tempImage = applet.createImage(PApplet.parseInt(originalImage.width * scaling),
				PApplet.parseInt(originalImage.height * scaling), PConstants.ARGB);
		tempImage.loadPixels();
		originalImage.loadPixels();
		for (int i = 0; i < originalImage.pixels.length; i++) {
			tempImage.pixels[i * scaling] = originalImage.pixels[i];
			tempImage.pixels[i * scaling + 1] = originalImage.pixels[i];
			tempImage.pixels[i * scaling + originalImage.width] = originalImage.pixels[i];
			tempImage.pixels[i * scaling + originalImage.width + 1] = originalImage.pixels[i];
		}
		tempImage.updatePixels();
		return pg(tempImage);
	}

	public float clamp(float val, float min, float max) {
		if (val < min) {
			val = min;
		}
		if (val > max) {
			val = max;
		}

		return val;
	}

	public float clampMin(float val, float min) {
		if (val < min) {
			val = min;
		}

		return val;
	}

	public float smoothMove(float pos, float target, float speed) {
		return pos + (target - pos) * speed;
	}

	public boolean hover(float x, float y, float w, float h) {
		return (applet.getMouseX() > x - w / 2 && applet.getMouseX() < x + w / 2 && applet.getMouseY() > y - h / 2
				&& applet.getMouseY() < y + h / 2);
	}

	public static void saveFile(String src, String content) {
		FileWriter fw;
		try {
			fw = new FileWriter(src);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean fileExists(String src) {
		boolean condition = false;
		try {
			String[] file = applet.loadStrings(src);
			if (file[0].length() == 0) {
			}
			condition = true;
		} catch (Exception e) {
			condition = false;
		}
		return condition;
	}

	// Game
	public void loadLevel(String src) { // todo save camera position/settings.
		String[] script = applet.loadStrings(src);
		String scriptD = decrypt(PApplet.join(script, "\n"));

		// Parse JSON
		JSONArray data = JSONArray.parse(scriptD);

		// Debug
		// PApplet.println( scriptD );

		// Clear Object Arrays
		applet.collisions.clear();
		applet.backgroundObjects.clear();

		// Create Level
		for (int i = 0; i < data.size(); i++) {
			JSONObject item = data.getJSONObject(i);

			String type = item.getString("type");

			// Read Main
			if (i == 0) {
				JSONArray d = item.getJSONArray("scene-dimension");
//				applet.worldPosition.x = d.getInt(0); // TODO
//				applet.worldPosition.y = d.getInt(1); // TODO
//				applet.worldPosition.x = 0; // TODO
//				applet.worldPosition.y = 0; // TODO
//				applet.worldWidth = d.getInt(2); TODO
//				applet.worldHeight = d.getInt(3); TODO
				if (PScene.name == "MAPEDITOR") {
					((SceneMapEditor) applet.mapEditor).worldViewportEditor.setSize();
				}
			} else {
				switch (type) {
					case "COLLISION" :
						Collision collision = new Collision(applet);
						try {
							collision.setGraphic(item.getString("id"));
						} catch (Exception e) {
							collision.width = 64;
							collision.height = 64;
						}
						collision.pos.x = item.getInt("x");
						collision.pos.y = item.getInt("y");

						// Append To Level
						applet.collisions.add(collision);
						break;
					case "BACKGROUND" :
						BackgroundObject backgroundObject = new BackgroundObject(applet);
						backgroundObject.setGraphic(item.getString("id"));
						backgroundObject.pos.x = item.getInt("x");
						backgroundObject.pos.y = item.getInt("y");

						// Append To Level
						applet.backgroundObjects.add(backgroundObject);
						break;
					case "OBJECT" :
						GameObject gameObject = Tileset.getObjectClass(item.getString("id"));
						gameObject.pos.x = item.getInt("x");
						gameObject.pos.y = item.getInt("y");

						// Append To Level
						applet.gameObjects.add(gameObject);
						break;
				}
			}
		}
	}

	public void saveLevel(String src) {
		JSONArray data = new JSONArray();

		// MAIN
		JSONObject main = new JSONObject();
		main.setString("title", "undefined");
		main.setString("creator", "undefined");
		main.setString("version", "alpha 1.0.0");

		JSONArray dimension = new JSONArray();
//		dimension.setInt(0, (int) applet.worldPosition.x); // todo
//		dimension.setInt(1, (int) applet.worldPosition.y); // todo
		dimension.setInt(0, (int) 0); // todo
		dimension.setInt(1, (int) 0); // todo
//		dimension.setInt(2, applet.worldWidth); todo
//		dimension.setInt(3, applet.worldHeight); todo
		dimension.setInt(2, 0); // todo
		dimension.setInt(3, 0); // todo

		main.setJSONArray("scene-dimension", dimension);

		// Add Main
		data.append(main);

		// Add Collisions
		for (int i = 0; i < applet.collisions.size(); i++) {
			JSONObject item = new JSONObject();
			item.setString("id", applet.collisions.get(i).id);
			item.setString("type", "COLLISION");
			item.setInt("x", (int) applet.collisions.get(i).pos.x);
			item.setInt("y", (int) applet.collisions.get(i).pos.y);
			data.append(item);
		}

		// Add Background Objects
		for (int i = 0; i < applet.backgroundObjects.size(); i++) {
			JSONObject item = new JSONObject();
			item.setString("id", applet.backgroundObjects.get(i).id);
			item.setString("type", "BACKGROUND");
			item.setInt("x", (int) applet.backgroundObjects.get(i).pos.x);
			item.setInt("y", (int) applet.backgroundObjects.get(i).pos.y);
			data.append(item);
		}

		// Add Game Objects
		for (int i = 0; i < applet.gameObjects.size(); i++) {
			applet.collisions.remove(applet.gameObjects.get(i).collision);

			JSONObject item = new JSONObject();
			item.setString("id", applet.gameObjects.get(i).id);
			item.setString("type", "OBJECT");
			item.setInt("x", (int) applet.gameObjects.get(i).pos.x);
			item.setInt("y", (int) applet.gameObjects.get(i).pos.y);
			data.append(item);
		}

		// Save Level
		saveFile(src, encrypt(data.toString()));
	}

	public static String encrypt(String str) {
		String output = "";

		for (int i = 0; i < str.length(); i++) {
			int k = PApplet.parseInt(str.charAt(i));

			// Encrypt Key
			k = (k * 8) - 115;

			char c = PApplet.parseChar(k);

			output += c;
		}
		return output;
	}

	public static String decrypt(String str) {
		String output = "";

		for (int i = 0; i < str.length(); i++) {
			int k = PApplet.parseInt(str.charAt(i));

			// Encrypt Key
			k = (k + 115) / 8;

			char c = PApplet.parseChar(k);

			output += c;
		}
		return output.replaceAll("" + PApplet.parseChar(8202), "\n").replaceAll("" + PApplet.parseChar(8201), "\t");
	}
}
