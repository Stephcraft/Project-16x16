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
import processing.data.JSONArray;

public class Tileset {

	private static final int TILESETSIZE = 16;
	private static final String DATAPATH = "Assets/tileData.json";
	private static final int SCALE = 4;
	
	private static HashMap<String, Integer> tileRef = new HashMap<String, Integer>();
	private static ArrayList<PImage> loadedTiles = new ArrayList<PImage>();
	
	private static JSONArray JSONtiles;
	private static JSONArray JSONanimations;
	
	private static SideScroller applet;
	
	public static void load(SideScroller app){
		// load JSON
		applet = app;
		JSONObject JSONtileData;
		JSONtileData = applet.loadJSONObject(DATAPATH);
		JSONtiles = JSONtileData.getJSONArray("tiles");
		JSONanimations = JSONtileData.getJSONArray("animations");
		// load tiles
		for(int i  = 0; i < JSONtiles.size(); i++)
		{
			JSONObject tile = JSONtiles.getJSONObject(i);
			String key = tile.getString("name");
			tileRef.put(key, i);
			getTile(key);
		}
	}
	
	public static PImage getTile(int id){
		if (loadedTiles.size() > id)
			return loadedTiles.get(id);
		
		JSONObject tile = JSONtiles.getJSONObject(id);
		int x = (int) (tile.getFloat("x") * TILESETSIZE);
		int y = (int) (tile.getFloat("y") * TILESETSIZE);
		int w = tile.getInt("w");
		int h = tile.getInt("h");
		PImage image = getTile(x, y, w, h);
		image = pixelate(image, SCALE);

		loadedTiles.add(image);
		return image;
	}
	
	public static PImage getTile(String name){
		int id = getTileId(name);
		return getTile(id);
	}
	
	public static PImage getTile(int x, int y, int w, int h)
	{
		return applet.graphicsSheet.get(x, y, w, h);
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
	
	public static GameObject getObjectClass(String id) {
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
	
	public static int getTileId(String key){
		if (tileRef.containsKey(key))
			return tileRef.get(key);
		
		PApplet.println("<Tileset> Error while loading, null string reference to tile ( " + key + " ) >");
		return 0;
	}
	
	public static String getTileType(String name) {
		int id = getTileId(name);
		JSONObject tile = JSONtiles.getJSONObject(id);
		String type = tile.getString("type", "COLLISION");
		return type;
	}
	
	public static int loadedTilesSize()
	{
		return loadedTiles.size();
	}
	
	public static String getTileName(int id)
	{
		JSONObject tile = JSONtiles.getJSONObject(id);
		return tile.getString("name");
	}
	
	public static PImage pixelate(PImage img, int scale) {
	    PGraphics pg = applet.createGraphics(img.width * SCALE, img.height * SCALE);
	    pg.noSmooth();
	    pg.beginDraw();
	    pg.clear();
	    pg.image(img, 0, 0, img.width * SCALE, img.height * SCALE);
	    pg.endDraw();
	    return pg.get();
	}
}