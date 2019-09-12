package sidescroller;

import java.util.ArrayList;
import java.util.HashMap;

import objects.GameObject;
import objects.MagicSourceObject;
import objects.MirrorBoxObject;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;
import scene.GameplayScene;
import processing.data.JSONArray;

/**
 * Tileset is a static class that loads and provides PImages
 * TODO deprecate SideScroller.graphicsSheet and uses thereof.
 */
public class Tileset {

	private static final int TILESETSIZE = 16;
	private static final String TILESHEETPATH = "Assets/Art/graphics-sheet.png";
	private static final String DATAPATH = "Assets/tileData.json";
	private static final int SCALE = 4;
	
	private static SideScroller applet;
	private static PImage graphicsSheet;
	
	private static HashMap<String, Integer> tileRef = new HashMap<String, Integer>();
	private static ArrayList<PImage> loadedTiles = new ArrayList<PImage>();
	
	private static JSONObject JSONtileData;
	private static JSONArray JSONtiles;
	private static JSONArray JSONanimations;
	
	public enum tileType {
		COLLISION, BACKGROUND, OBJECT, ENTITY;
	}
	
	public static void load(SideScroller app){
		applet = app;
		graphicsSheet = applet.loadImage(TILESHEETPATH);
		loadJSON();
		loadTiles();
	}
	
	public static PImage getTile(String name){
		return getTile(getTileId(name));
	}
	
	public static PImage getTile(int index) {
		if (loadedTiles.size() > index) {
			return loadedTiles.get(index);
		} else {
			PApplet.println("<Tileset> Error while loading, null index reference to tile ( " + index + " ) >");
			return null; // TODO return placeholder?
		}
	}
	
	public static PImage getTile(int x, int y, int w, int h)
	{
		return graphicsSheet.get(x, y, w, h);
	}
		
	public static String getTileName(int id)
	{
		JSONObject tile = JSONtiles.getJSONObject(id);
		return tile.getString("name");
	}
	
	public static int getTileId(String name){
		if (tileRef.containsKey(name))
			return tileRef.get(name);
		
		PApplet.println("<Tileset> Error while loading, null string reference to tile ( "+name+" ) >");
		return 0;
	}
	
	public static int getTileId(PImage image)
	{
		for(int i = 0; i < loadedTiles.size(); i++)
		{
			if (loadedTiles.get(i).equals(image))
				return i;
		}
		return -1;
	}
	
	public static int getTileCount() {
		return loadedTiles.size();
	}
	
	public static tileType getTileType(String name) {
		return getTileType(getTileId(name));
	}
	
	public static tileType getTileType(int index) {
		String type = JSONtiles.getJSONObject(index).getString("type", "COLLISION");
		switch(type) {
			case "COLLISION":
				return tileType.COLLISION;
			case "BACKGROUND":
				return tileType.BACKGROUND;
			case "OBJECT":
				return tileType.OBJECT;
			case "ENTITY":
				return tileType.ENTITY;
			default:
				return null;
		}
	}
	
	public static ArrayList<PImage> getAnimation(String name){
		for(int i = 0; i < JSONanimations.size(); i++)
		{
			JSONObject animation = JSONanimations.getJSONObject(i);
			if (animation.getString("name").contentEquals(name))
			{
				ArrayList<PImage> tiles = new ArrayList<PImage>();
				JSONArray tileRefs = animation.getJSONArray("tileRef");
				
				for(int k = 0; k < tileRefs.size(); k++)
				{
					JSONObject tileRef = tileRefs.getJSONObject(k);
					tiles.add(getTile(tileRef.getString("name")));
				}

				return tiles;
			}
		}
		PApplet.println("<Tileset> Error while loading, null string reference to animation ( " + name + " ) >");
		return null;
	}
	
	public static ArrayList<PImage> getAllTiles(tileType type) {
		ArrayList<PImage> images = new ArrayList<PImage>();
		for(int i = 0; i < getTileCount(); i++) {
			tileType tileType = getTileType(i);
			if (tileType == type)
				images.add(loadedTiles.get(i));
		}
		return images;
	}
	
	public static ArrayList<PImage> getAllTiles(tileType[] types) {
		ArrayList<PImage> images = new ArrayList<PImage>();
		for(tileType type : types)
			images.addAll(getAllTiles(type));
		return images;
	}
	
	public static GameObject getObjectClass(String name) { // TODO unsafe casts
		if (name.equals("MAGIC_SOURCE"))
			return new MagicSourceObject(applet, (GameplayScene) applet.currentScene);
		if (name.equals("MIRROR_BOX"))
			return new MirrorBoxObject(applet, (GameplayScene) applet.currentScene);
		return new GameObject(applet, (GameplayScene) applet.currentScene);
	}
	
	private static void loadJSON() {
		JSONtileData = applet.loadJSONObject(DATAPATH);
		JSONtiles = JSONtileData.getJSONArray("tiles");
		JSONanimations = JSONtileData.getJSONArray("animations");
	}
	
	private static void loadTiles() {
		for(int i  = 0; i < JSONtiles.size(); i++) {
			JSONObject tile = JSONtiles.getJSONObject(i);
			String name = tile.getString("name");
			
			PImage image = getTile((int) (tile.getFloat("x") * TILESETSIZE),
							       (int) (tile.getFloat("y") * TILESETSIZE),
								   tile.getInt("w"), tile.getInt("h"));
			
			image = pixelate(image, SCALE);
			
			tileRef.put(name, i);
			loadedTiles.add(image);
		}
	}
	
	private static PImage pixelate(PImage img, int scale) {
	    PGraphics pg = applet.createGraphics(img.width * SCALE, img.height * SCALE);
	    pg.noSmooth();
	    pg.beginDraw();
	    pg.clear();
	    pg.image(img, 0, 0, img.width * SCALE, img.height * SCALE);
	    pg.endDraw();
	    return pg.get();
	}

}