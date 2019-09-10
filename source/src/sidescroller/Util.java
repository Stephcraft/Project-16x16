package sidescroller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import objects.BackgroundObject;
import objects.CollidableObject;
import objects.GameObject;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

import scene.PScene;
import scene.GameplayScene;

public final class Util {

	private static SideScroller applet;

	/**
	 * The Util class provides static functions, but uses a PApplet (SideScroller)
	 * instance within the static methods. This instance must be assigned in a
	 * static method, here, before anything else uses this class.
	 */
	public static void assignApplet(SideScroller a) {
		applet = a;
	}

	public static PImage pg(PImage img) {
		return pg(img, 1);
	}

	/**
	 * Note: Graphics in Project-16x16 are generally scaled by x4 at load, so you
	 * probably want to call this method with scl=4, if you're loading game graphics
	 * with it.
	 * 
	 * @param img
	 * @param scl Scale (probably set to 4).
	 * @return
	 */
	public static PImage pg(PImage img, float scl) {
		PGraphics pg = applet.createGraphics((int) (img.width * scl), (int) (img.height * scl));
		pg.noSmooth();
		pg.beginDraw();
		pg.image(img, 0, 0, (int) (img.width * scl), (int) (img.height * scl));
		pg.endDraw();
		return pg.get();
	}

	public static PImage blur(PImage img, float b) {
		PGraphics pg = applet.createGraphics(img.width, img.height);

		pg.noSmooth();
		pg.beginDraw();
		pg.clear();
		pg.image(img, 0, 0, img.width, img.height);
		pg.filter(PConstants.BLUR, b);
		pg.endDraw();

		return pg.get();
	}

	public static PImage warp(PImage source, float waveAmplitude, float numWaves) {
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
		return pg(destination).get();
	}

	public static PImage scale(PImage pBuffer, int scaling) {
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
		return pg(tempImage).get();
	}

	public static PImage resizeImage(PImage img, float scl) {
		PGraphics pg = applet.createGraphics((int) (img.width * scl), (int) (img.height * scl));

		pg.beginDraw();
		pg.clear();
		pg.scale(scl, scl);
		pg.image(img, 0, 0);
		pg.endDraw();

		return pg;
	}

	public static PImage rotateImage(PImage img, float angle) {
		PGraphics pg = applet.createGraphics((int) (img.width * 1.5), (int) (img.height * 1.5));

		pg.beginDraw();
		pg.clear();
		pg.imageMode(PApplet.CENTER);
		pg.translate(pg.width / 2, pg.height / 2);
		pg.rotate(angle);
		pg.image(img, 0, 0);
		pg.endDraw();

		return pg;
	}

	public static float clamp(float val, float min, float max) {
		if (val < min) {
			val = min;
		}
		if (val > max) {
			val = max;
		}

		return val;
	}

	public static float clampMin(float val, float min) {
		if (val < min) {
			val = min;
		}

		return val;
	}

	public static float smoothMove(float pos, float target, float speed) {
		return pos + (target - pos) * speed;
	}

	/**
	 * Is the mouse within a square region, given by its center coordinate (x,y).
	 * 
	 * @param x region center pos X
	 * @param y region center pos Y
	 * @param w region width
	 * @param h region height
	 * @return Whether mouse hovers region.
	 */
	public static boolean hover(float x, float y, float w, float h) {
		return (applet.getMouseX() > x - w / 2 && applet.getMouseX() < x + w / 2 && applet.getMouseY() > y - h / 2
				&& applet.getMouseY() < y + h / 2);
	}

	/**
	 * Rounds n to the nearest x.
	 * 
	 * @param n number to round
	 * @param x
	 */
	public static float roundToNearest(float n, float x) {
		return Math.round(n / x) * x;
	}

	/**
	 * Round a float to 'n' decimal places.
	 * 
	 * @param n number to round
	 * @param d number of decimal places
	 * @return rounded float
	 */
	public static float roundToNPlaces(float n, int d) {
		return Float.parseFloat(String.format("%." + d + "f", n));
	}

	/**
	 * Are two PVectors with range of each other? Faster than using
	 * [{@link PApplet#dist(float, float, float, float) dist()} < range] since this
	 * doesn't use square-roots.
	 * 
	 * @param a    PVector 1
	 * @param b    PVector 2
	 * @param dist Range to check
	 * @return true if the PVectors are within range of each other.
	 */
	public static boolean fastInRange(PVector a, PVector b, int dist) {
		return ((b.x - a.x) * (b.x - a.x)) + ((b.y - a.y) * (b.y - a.y)) < (dist * dist);
	}

	/**
	 * Calculates the angle between two PVectors where: East = 0; North = -1/2PI;
	 * West = -PI; South = -3/2PI | 1/2PI
	 * 
	 * @param tail PVector Coordinate 1.
	 * @param head PVector Coordinate 2.
	 * @return float θ in radians.
	 */
	public static float angleBetween(PVector tail, PVector head) {
		float a = PApplet.atan2(tail.y - head.y, tail.x - head.x);
		if (a < 0) {
			a += PConstants.TWO_PI;
		}
		return a;
	}

	/**
	 * Writes data to file.
	 * 
	 * @param path    File location.
	 * @param content File contents
	 */
	public static void saveFile(String path, String content) {
		try {
			OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);
			o.write(content);
			o.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean fileExists(String src) {
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

	/**
	 * Encrypts str (JSON) for output.
	 * 
	 * @param str
	 * @return
	 */
	public static String encrypt(String str) {
		String output = "";
		for (int i = 0; i < str.length(); i++) {
			int k = PApplet.parseInt(str.charAt(i));
			k = (k * 8) - 115; // Encrypt Key
			output += PApplet.parseChar(k);
		}
		return output;

	}

	/**
	 * Decrypt input to str (JSON).
	 * 
	 * @param str
	 * @return
	 */
	public static String decrypt(String str) {
		String output = "";
		for (int i = 0; i < str.length(); i++) {
			int k = PApplet.parseInt(str.charAt(i));
			k = (k + 115) / 8; // Encrypt Key
			output += PApplet.parseChar(k);
		}
		return output.replaceAll("" + PApplet.parseChar(8202), "\n").replaceAll("" + PApplet.parseChar(8201), "\t");
	}
}
