package project_16x16;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import project_16x16.components.Tile;

public final class Utility {

	private static Main applet;

	/**
	 * The Util class provides static functions, but uses a PApplet (SideScroller)
	 * instance within the static methods. This instance must be assigned in a
	 * static method, here, before anything else uses this class.
	 */
	public static void assignApplet(Main a) {
		applet = a;
	}

	/**
	 * 
	 * @param img
	 * @return
	 * @deprecated
	 */
	public static PImage pg(PImage img) {
		return pg(img, 1);
	}

	/**
	 * Scales a PImage object.
	 * 
	 * @param img
	 * @param scl Scale (probably set to 4).
	 * @deprecated Use {@link Tileset#getTile(int, int, int, int, int) getTile() to
	 *             load and scale game graphics}.
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

	/**
	 * @see {@link BlurUtils#blurImage(PImage, int, int) blurImage()}.
	 */
	public static PImage blur(PImage img, int radius, int iterations) {
		return BlurUtils.blurImage(img, radius, iterations);
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

		pg.noSmooth();
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

	public static float smoothMove(float pos, float target, float speed) {
		return pos + (target - pos) * speed;
	}
	
	/**
	 * Converts (R,G,B) values to integer representation,
	 * compatible with Processing.
	 * @param R Red Value [0-255].
	 * @param G Green Value [0-255].
	 * @param B Blue Value [0-255].
	 * @return Color int.
	 * @see {@link #colorToRGB(int, int, int, float) colorToRGB(int R, int G, int B, float A)}
	 */
	public static int colorToRGB(int R, int G, int B) {
		int out = 255 << 24; // full transparency
		out += R << 16;
		out += G << 8;
		out += B;
		return out;
	}

	/**
	 * Converts (R,G,B,A) values to integer representation,
	 * compatible with Processing.
	 * @param R Red Value [0-255].
	 * @param G Green Value [0-255].
	 * @param B Blue Value [0-255].
	 * @param A Alpha (transparency) [0.0-1.0].
	 * @return Color int.
	 */
	public static int colorToRGB(int R, int G, int B, float A) {
		return new Color(((float) R) / 255, ((float) G) / 255, ((float) B) / 255, A / 255).getRGB();
	}

	/**
	 * Is the <b>screen coordinate</b> of the mouse within a square region, given by
	 * its center coordinate (x,y)?
	 * 
	 * @param x region center pos X
	 * @param y region center pos Y
	 * @param w region width
	 * @param h region height
	 * @return Whether mouse hovers region.
	 * @see #hoverGame(float, float, float, float) hoverGame()
	 */
	public static boolean hoverScreen(float x, float y, float w, float h) {
		return (applet.getMouseCoordScreen().x > x - w / 2 && applet.getMouseCoordScreen().x < x + w / 2
				&& applet.getMouseCoordScreen().y > y - h / 2 && applet.getMouseCoordScreen().y < y + h / 2);
	}

	/**
	 * Is the <b>gameworld coordinate</b> of the mouse within a square region, given
	 * by its center coordinate (x,y)?
	 * 
	 * @param x region center pos X
	 * @param y region center pos Y
	 * @param w region width
	 * @param h region height
	 * @return Whether mouse hovers region.
	 * @see #hoverScreen(float, float, float, float) hoverScreen()
	 */
	public static boolean hoverGame(float x, float y, float w, float h) {
		return (applet.getMouseCoordGame().x > x - w / 2 && applet.getMouseCoordGame().x < x + w / 2
				&& applet.getMouseCoordGame().y > y - h / 2 && applet.getMouseCoordGame().y < y + h / 2);
	}
	
	/**
	 * Determine if a point is within a rectangular region -- PVector params.
	 * @param point PVector position to test.
	 * @param UL Corner one of region.
	 * @param BR Corner two of region (different X & Y).
	 * @return True if point contained in region.
	 */
	public static boolean withinRegion(PVector point, PVector UL, PVector BR) {
		return (point.x >= UL.x && point.y >= UL.y) && (point.x <= BR.x && point.y <= BR.y) // SE
				|| (point.x >= BR.x && point.y >= BR.y) && (point.x <= UL.x && point.y <= UL.y) // NW
				|| (point.x <= UL.x && point.x >= BR.x) && (point.y >= UL.y && point.y <= BR.y) // SW
				|| (point.x <= BR.x && point.x >= UL.x) && (point.y >= BR.y && point.y <= UL.y); // NE
	}
		
	/**
	 * Determine if a point is within a rectangular region -- Float params.
	 * @param pointX X coord of point position to test.
	 * @param pointY X coord of point position to test.
	 * @param ULX X coord of corner #1 (upper left) of region.
	 * @param ULY Y coord of corner #1 (upper left) of region.
	 * @param BRX X coord of corner #2 (bottom right) of region.
	 * @param BRY Y coord of corner #2 (bottom right) of region.
	 * @return True if point contained in region.
	 */
	public static boolean withinRegion(float pointX, float pointY, float ULX, float ULY, float BRX, float BRY) {
		return (pointX >= ULX && pointY >= ULY) && (pointX <= BRX && pointY <= BRY) // SE
				|| (pointX >= BRX && pointY >= BRY) && (pointX <= ULX && pointY <= ULY) // NW
				|| (pointX <= ULX && pointX >= BRX) && (pointY >= ULY && pointY <= BRY) // SW
				|| (pointX <= BRX && pointX >= ULX) && (pointY >= BRY && pointY <= ULY); // NE
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
	
	public static void convertTiledLevel(String filePath, String mapName) {
		JSONArray levelSave = new JSONArray();
		JSONObject main = new JSONObject();
		main.setString("title", "undefined");
		main.setString("creator", "undefined");
		main.setString("version", "alpha 1.0.0");
		levelSave.append(main);
		
		
		JSONObject JSONtileData = applet.loadJSONObject(filePath);
		JSONArray JSONlayers = JSONtileData.getJSONArray("layers");
		
		for(int i = 0; i < JSONlayers.size(); i++) {
			JSONObject layer = JSONlayers.getJSONObject(i);
			int width = layer.getInt("width");
			int height = layer.getInt("height");
			JSONArray data = layer.getJSONArray("data");
			for(int j = 0; j < data.size(); j++) {
				int tileId = data.getInt(j) - 1;
				if (tileId >= 0) {
					Tile tile = Tileset.getTileObject(tileId);
					JSONObject JSONtile = new JSONObject();
					JSONtile.setString("id", tile.getName());
					JSONtile.setString("type", tile.getTileType().toString());
					JSONtile.setInt("x", (j % width) * 60 - (width/2*60));
					JSONtile.setInt("y", (int) (j / width) * 60 - (height/2*60));
					levelSave.append(JSONtile);
				}
			}
			Utility.saveFile("src/main/resources/Storage/Game/Maps/save/" + mapName + ".dat", Utility.encrypt(levelSave.toString()));
		}
	}
}

/**
 * A simple set of software blur utilities for mobile applications.
 * https://gist.github.com/mattdesl/4383372
 * 
 * @author davedes, blur algorithm by Romain Guy
 * @author micycle1 -- PImage integration.
 */
final class BlurUtils {
	/*
	 * Copyright (c) 2007, Romain Guy All rights reserved.
	 * 
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions are met:
	 * 
	 * * Redistributions of source code must retain the above copyright notice, this
	 * list of conditions and the following disclaimer. * Redistributions in binary
	 * form must reproduce the above copyright notice, this list of conditions and
	 * the following disclaimer in the documentation and/or other materials provided
	 * with the distribution. * Neither the name of the TimingFramework project nor
	 * the names of its contributors may be used to endorse or promote products
	 * derived from this software without specific prior written permission.
	 * 
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
	 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
	 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
	 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
	 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
	 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
	 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
	 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
	 * POSSIBILITY OF SUCH DAMAGE.
	 */
	/**
	 * <p>
	 * Blurs the source pixels into the destination pixels. The force of the blur is
	 * specified by the radius which must be greater than 0.
	 * </p>
	 * <p>
	 * The source and destination pixels arrays are expected to be in the RGBA
	 * format.
	 * </p>
	 * 
	 * @param srcPixels the source pixels
	 * @param dstPixels the destination pixels
	 * @param width     the width of the source picture
	 * @param height    the height of the source picture
	 * @param radius    the radius of the blur effect
	 * @author Romain Guy <romain.guy@mac.com>
	 */
	private static void blurPass(int[] srcPixels, int[] dstPixels, int width, int height, int radius) {
		final int windowSize = radius * 2 + 1;
		final int radiusPlusOne = radius + 1;

		int sumRed;
		int sumGreen;
		int sumBlue;
		int sumAlpha;

		int srcIndex = 0;
		int dstIndex;
		int pixel;

		int[] sumLookupTable = new int[256 * windowSize];
		for (int i = 0; i < sumLookupTable.length; i++) {
			sumLookupTable[i] = i / windowSize;
		}

		int[] indexLookupTable = new int[radiusPlusOne];
		if (radius < width) {
			for (int i = 0; i < indexLookupTable.length; i++) {
				indexLookupTable[i] = i;
			}
		} else {
			for (int i = 0; i < width; i++) {
				indexLookupTable[i] = i;
			}
			for (int i = width; i < indexLookupTable.length; i++) {
				indexLookupTable[i] = width - 1;
			}
		}

		for (int y = 0; y < height; y++) {
			sumAlpha = sumRed = sumGreen = sumBlue = 0;
			dstIndex = y;

			pixel = srcPixels[srcIndex];
			sumRed += radiusPlusOne * ((pixel >> 24) & 0xFF);
			sumGreen += radiusPlusOne * ((pixel >> 16) & 0xFF);
			sumBlue += radiusPlusOne * ((pixel >> 8) & 0xFF);
			sumAlpha += radiusPlusOne * (pixel & 0xFF);

			for (int i = 1; i <= radius; i++) {
				pixel = srcPixels[srcIndex + indexLookupTable[i]];
				sumRed += (pixel >> 24) & 0xFF;
				sumGreen += (pixel >> 16) & 0xFF;
				sumBlue += (pixel >> 8) & 0xFF;
				sumAlpha += pixel & 0xFF;
			}

			for (int x = 0; x < width; x++) {
				dstPixels[dstIndex] = sumLookupTable[sumRed] << 24 | sumLookupTable[sumGreen] << 16
						| sumLookupTable[sumBlue] << 8 | sumLookupTable[sumAlpha];
				dstIndex += height;

				int nextPixelIndex = x + radiusPlusOne;
				if (nextPixelIndex >= width) {
					nextPixelIndex = width - 1;
				}

				int previousPixelIndex = x - radius;
				if (previousPixelIndex < 0) {
					previousPixelIndex = 0;
				}

				int nextPixel = srcPixels[srcIndex + nextPixelIndex];
				int previousPixel = srcPixels[srcIndex + previousPixelIndex];

				sumRed += (nextPixel >> 24) & 0xFF;
				sumRed -= (previousPixel >> 24) & 0xFF;

				sumGreen += (nextPixel >> 16) & 0xFF;
				sumGreen -= (previousPixel >> 16) & 0xFF;

				sumBlue += (nextPixel >> 8) & 0xFF;
				sumBlue -= (previousPixel >> 8) & 0xFF;

				sumAlpha += nextPixel & 0xFF;
				sumAlpha -= previousPixel & 0xFF;
			}

			srcIndex += width;
		}
	}

	/**
	 * Blurs (in both horizontal and vertical directions) the specified RGBA image
	 * with the given radius and iterations.
	 * 
	 * @param inputRGBA  the image pixels, in RGBA format
	 * @param width      the width of the image in pixels
	 * @param height     the height of the image in pixels
	 * @param radius     the radius of the blur effect
	 * @param iterations the number of times to perform the blur; i.e. to increase
	 *                   quality
	 * @return the blurred pixels
	 */
	private static int[] blur(int[] inputRGBA, int width, int height, int radius, int iterations) {
		int[] srcPixels = new int[width * height];
		int[] dstPixels = new int[width * height];

		System.arraycopy(inputRGBA, 0, srcPixels, 0, srcPixels.length); // copy input into srcPixels

		for (int i = 0; i < iterations; i++) {
			blurPass(srcPixels, dstPixels, width, height, radius); // horizontal pass
			blurPass(dstPixels, srcPixels, height, width, radius); // vertical pass
		}
		return srcPixels; // the result is now stored in srcPixels due to the 2nd pass
	}

	/**
	 * Blurs the input PImage, producing a copy [the orignal is untouched] (much
	 * faster than using {@link processing.core.PApplet#filter(int) filter(BLUR)}).
	 * 
	 * @param in         source PImage
	 * @param radius     radius of blur effect
	 * @param iterations the number of times to perform the blur; i.e. to increase
	 *                   quality
	 * @return PImage blurred PImage object
	 */
	public static PImage blurImage(PImage in, int radius, int iterations) {
		PImage out = new PImage(in.width, in.height);
		out.loadPixels();
		out.pixels = blur(in.pixels, in.width, in.height, radius, iterations);
		out.updatePixels();
		return out;
	}
}
