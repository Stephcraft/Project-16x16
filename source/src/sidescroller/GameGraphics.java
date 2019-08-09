package sidescroller;

import java.util.ArrayList;
import java.util.HashMap;

import objects.GameObject;
import objects.MagicSourceObject;
import objects.MirrorBoxObject;

import processing.core.*;

/**
 * Packages graphics from the {@link SideScroller#graphicsSheet sprite sheet}
 * into sprites associated with an identifier.
 */
public class GameGraphics extends PClass {

	public HashMap<String, Graphic> graphics;
	public HashMap<String, ArrayList<PImage>> animations;

	public enum graphicsType {
		COLLISION, BACKGROUND, OBJECT, ENTITY;
	}

	public GameGraphics(SideScroller a) {
		super(a);
		graphics = new HashMap<String, Graphic>();
		animations = new HashMap<String, ArrayList<PImage>>();
	}

	// Main Function To Load Graphics
	public void load() {
		// Collisions Graphics
		gc("BOX", 360, 32, 16, 16);

		gc("METAL", 336, 16, 16, 16);
		gc("METAL_MIDDLE", 320, 0, 16, 16);
		gc("METAL_RIGHT", 304, 0, 16, 16);
		gc("METAL_LEFT", 336, 0, 16, 16);

		gc("METAL_WALK_MIDDLE:0", 208, 0, 16, 16);
		gc("METAL_WALK_MIDDLE:1", 224, 0, 16, 16);
		gc("METAL_WALK_RIGHT", 240, 0, 16, 16);
		gc("METAL_WALK_LEFT", 192, 0, 16, 16);

		gc("METAL_DUST_WALK_MIDDLE:0", 208, 0, 16, 19);
		gc("METAL_DUST_WALK_MIDDLE:1", 224, 0, 16, 19);
		gc("METAL_DUST_WALK_RIGHT", 240, 0, 16, 19);
		gc("METAL_DUST_WALK_LEFT", 192, 0, 16, 19);

		gc("PLATFORM_WALK:0", 240, 32, 16, 6);
		gc("PLATFORM_WALK:1", 312, 64, 16, 6);
		gc("PLATFORM_WALK_MIDDLE", 288, 0, 16, 6);
		gc("PLATFORM_WALK_RIGHT", 240, 48, 16, 6);
		gc("PLATFORM_WALK_LEFT", 192, 48, 16, 6);

		gc("CORNER_LEFT:0", 384, 32, 16, 16);
		gc("CORNER_LEFT:1", 384, 48, 16, 16);
		gc("CORNER_LEFT:2", 384, 64, 16, 16);
		gc("CORNER_LEFT:3", 368, 48, 16, 16);

		gc("CORNER_RIGHT:0", 400, 32, 16, 16);
		gc("CORNER_RIGHT:1", 400, 48, 16, 16);
		gc("CORNER_RIGHT:2", 400, 64, 16, 16);
		gc("CORNER_RIGHT:3", 352, 48, 16, 16);

		gc("WALL_RIGHT", 272, 0, 5, 16);
		gc("WALL_LEFT", 267, 0, 5, 16);

		gc("WEED_WALK_LEFT:0", 0, 16, 16, 16);
		gc("WEED_WALK_LEFT:1", 32, 16, 16, 16);
		gc("WEED_WALK_LEFT:2", 64, 16, 16, 16);
		gc("WEED_WALK_LEFT:3", 48, 32, 16, 16);

		gc("WEED_WALK_RIGHT:0", 16, 16, 16, 16);
		gc("WEED_WALK_RIGHT:1", 48, 16, 16, 16);
		gc("WEED_WALK_RIGHT:2", 80, 16, 16, 16);
		gc("WEED_WALK_RIGHT:3", 64, 32, 16, 16);

		gc("WEED_WALK_MIDDLE:0", 96, 16, 16, 16);
		gc("WEED_WALK_MIDDLE:1", 112, 16, 16, 16);
		gc("WEED_WALK_MIDDLE:2", 128, 16, 16, 16);
		gc("WEED_WALK_MIDDLE:3", 144, 16, 16, 16);
		gc("WEED_WALK_MIDDLE:4", 160, 16, 16, 16);
		gc("WEED_WALK_MIDDLE:5", 176, 16, 16, 16);

		gc("DOOR_RIGHT", 112, 96, 48, 32);

		// Background Graphics
		gb("CHAIN:0", 130, 0, 12, 16);
		gb("CHAIN:1", 402, 0, 13, 16);

		gb("CHAIN_END:0", 354, 7, 12, 6);
		gb("CHAIN_END:1", 354, 0, 12, 6);

		gb("HOOK_CHAIN", 6, 48, 3, 16);
		gb("HOOK:0", 6, 65, 4, 7);
		gb("HOOK:1", 6, 81, 4, 7);

		gb("SIGN:1", 256, 64, 16, 16);
		gb("SIGN:2", 256, 80, 16, 16);
		gb("SIGN:3", 256, 96, 16, 16);
		gb("SIGN:4", 288, 64, 16, 16);
		gb("SIGN:5", 288, 80, 16, 16);

		gb("BIG_TUBE:0", 224, 112, 16, 16);
		gb("BIG_TUBE:1", 224, 128, 16, 16);
		gb("BIG_TUBE:2", 224, 144, 16, 16);
		gb("BIG_TUBE:3", 224, 161, 16, 16);
		gb("BIG_TUBE:4", 224, 176, 16, 16);
		gb("BIG_TUBE:5", 224, 192, 16, 16);
		gb("BIG_TUBE:6", 208, 144, 16, 16);
		gb("BIG_TUBE:7", 192, 144, 16, 16);
		gb("BIG_TUBE:8", 176, 144, 16, 16);

		// Objects
		go("MAGIC_SOURCE", 0, 496, 16, 16);
		go("MIRROR_BOX", 336, 160, 16, 16);

		// Animations
		ga("PLAYER::WALK", 0, 272, 16, 16, 8);
		ga("PLAYER::ATTACK", 0, 304, 16, 16, 4);
		ga("PLAYER::IDLE", 0, 256, 16, 16, 3);
		ga("PLAYER::SQUISH", 48, 256, 16, 16, 5);

		ga("MAGIC::IDLE", 0, 447, 11, 9, 10);
		// ga("MAGIC::MOVE", 0,471, 40,28, 9);
		ga("MAGIC::MOVE", 0, 476, 40, 15, 9);
		ga("MIRROR_BOX::ROTATE", 392, 168, 20, 22, 7, "Y");
		// ga("PLAYER::ATTACK", 392,168, 20,23, 7, "Y");
	}

	// Get PImage From ID
	public PImage get(String id) {
		return graphics.get(id).image;
	}

	// Get Type From ID
	public graphicsType getType(String id) {
		return graphics.get(id).type;
	}

	// Get Animation PImage ArrayList
	public ArrayList<PImage> getAnimation(String id) {
		return animations.get(id);
	}

	// Get Object Class From ID
	public GameObject getObjectClass(String id) {
		GameObject obj = new GameObject(applet);

		switch (id) {
			case "MAGIC_SOURCE" :
				obj = new MagicSourceObject(applet);
				break;
			case "MIRROR_BOX" :
				obj = new MirrorBoxObject(applet);
				break;
		}

		return obj;
	}

	// Graphics Collision
	private void gc(String name, int x, int y, int w, int h) {
		graphics.put(name, new Graphic(name, graphicsType.COLLISION, x, y, w, h, 4));
	}

	// Graphics Background
	private void gb(String name, int x, int y, int w, int h) {
		graphics.put(name, new Graphic(name, graphicsType.BACKGROUND, x, y, w, h, 4));
	}

	// Graphics Entity
	private void go(String name, int x, int y, int w, int h) {
		graphics.put(name, new Graphic(name, graphicsType.OBJECT, x, y, w, h, 4));
	}

	// Graphics Entity - UNUSED
	private void ge(String name, int x, int y, int w, int h) {
		graphics.put(name, new Graphic(name, graphicsType.ENTITY, x, y, w, h, 4));
	}

	// Graphics Animation
	private void ga(String n, int sx, int sy, int w, int h, int length) {
		ArrayList<PImage> frames = new ArrayList<PImage>();

		for (int i = 0; i < length; i++) {
			frames.add(util.pg(applet.graphicsSheet.get(sx + (w * i), sy, w, h), 4));
		}
		animations.put(n, frames);
	}

	// Graphics Animation Y
	private void ga(String n, int sx, int sy, int w, int h, int length, String mode) {
		ArrayList<PImage> frames = new ArrayList<PImage>();
		for (int i = 0; i < length; i++) {
			frames.add(util.pg(applet.graphicsSheet.get(sx, sy + (h * i), w, h), 4));
		}
		animations.put(n, frames);
	}

	public ArrayList<PImage> ga(PImage src, int sx, int sy, int w, int h, int length) {
		ArrayList<PImage> frames = new ArrayList<PImage>();

		for (int i = 0; i < length; i++) {
			frames.add(util.pg(src.get(sx + (w * i), sy, w, h), 4));
		}

		return frames;
	}

	public PImage g(int x, int y, int w, int h) {
		return util.pg(applet.graphicsSheet.get(x, y, w, h), 4);
	}

	public PImage g(int x, int y, int w, int h, float s) {
		return util.pg(applet.graphicsSheet.get(x, y, w, h), s);
	}

	public PImage g(PImage src, int x, int y, int w, int h, float s) {
		return util.pg(src.get(x, y, w, h), s);
	}

	public class Graphic {
		public String name;
		public graphicsType type;
		public PImage image;

		public Graphic(String name, graphicsType type, int x, int y, int w, int h, float s) {
			this.name = name;
			this.type = type;
			image = util.pg(applet.graphicsSheet.get(x, y, w, h), s);
		}
	}
}
