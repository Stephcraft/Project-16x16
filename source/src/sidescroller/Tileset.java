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

/**
 * <h1>Tileset Class</h1>
 * <p>
 * Tileset is a static class that loads and provides PImages
 * </p>
 */
public class Tileset {

	private static final int TILESETSIZE = 16;
	private static final String TILESHEETPATH = "Assets/Art/graphics-sheet.png";
	private static final String DATAPATH = "Assets/tileData.json";
	private static final int SCALE = 4;
	
	private static HashMap<String, Integer> tileRef = new HashMap<String, Integer>();
	private static ArrayList<PImage> loadedTiles = new ArrayList<PImage>();
	
	private static JSONObject JSONtileData;
	private static JSONArray JSONtiles;
	private static JSONArray JSONanimations;
	
	private static PImage graphicsSheet;
	
	private static SideScroller applet;
	
	/**
	 * Loads all tiles at given JSON path.
	 */
	public static void load(SideScroller app){
		
		applet = app;
		
		// Load Graphics Sheet
		graphicsSheet = applet.loadImage(TILESHEETPATH);
		
		// Load JSON
		JSONtileData = applet.loadJSONObject(DATAPATH);
		JSONtiles = JSONtileData.getJSONArray("tiles");
		JSONanimations = JSONtileData.getJSONArray("animations");
		
		// Load Tiles
		for(int i  = 0; i < JSONtiles.size(); i++)
		{
			JSONObject tile = JSONtiles.getJSONObject(i);
			String name = tile.getString("name");
			tileRef.put(name, i);
			getTile(name);
		}
	}
	
	/**
	* Returns PImage associated with given name. 
	*
	* @param  name   name associated with PImage
	*/
	public static PImage getTile(String name){
		int id = getTileId(name);
		return getTile(id);
	}
	
	/**
	* Returns PImage associated with given id. 
	*
	* @param  id   id associated with PImage
	*/
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
	
	/**
	* Returns PImage found at given location. 
	*
	* @param  x   x position
	* @param  y   y position
	* @param  w   width
	* @param  h   height
	*/
	public static PImage getTile(int x, int y, int w, int h)
	{
		return graphicsSheet.get(x, y, w, h);
	}
		
	/**
	* Returns ArrayList of PImages associated with given name. 
	*
	* @param  name animation name
	*/
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
	
	/**
	* Returns id associated with PImage. 
	*
	* @param  name name associated with PImage
	*/
	public static int getTileId(String name){
		if (tileRef.containsKey(name))
			return tileRef.get(name);
		
		PApplet.println("<Tileset> Error while loading, null string reference to tile ( " + name + " ) >");
		return 0;
	}
	
	/**
	* Returns PImage name. 
	*
	* @param  id id associated with PImage
	*/
	public static String getTileName(int id)
	{
		JSONObject tile = JSONtiles.getJSONObject(id);
		return tile.getString("name");
	}
	
	/**
	* Returns amount of PImages loaded.
	*/
	public static int loadedTilesSize()
	{
		return loadedTiles.size();
	}
	
	/**
	* Returns PImage type. 
	*
	* @param  name name associated with PImage
	*/
	public static String getTileType(String name) {
		JSONObject tile = JSONtiles.getJSONObject(getTileId(name));
		return tile.getString("type", "COLLISION");
	}
	
	/**
	* Returns new scaled PImage. 
	*
	* @param  img Scaled PImage
	* @param  scale Scale amount
	*/
	public static PImage pixelate(PImage img, int scale) {
	    PGraphics pg = applet.createGraphics(img.width * SCALE, img.height * SCALE);
	    pg.noSmooth();
	    pg.beginDraw();
	    pg.clear();
	    pg.image(img, 0, 0, img.width * SCALE, img.height * SCALE);
	    pg.endDraw();
	    return pg.get();
	}
	
	/**
	* Returns ObjectClass of PImage  .
	* TODO: Not sure this should be here
	* 
	* @param  name name associated with PImage
	*/
	public static GameObject getObjectClass(String name) {
		GameObject obj = new GameObject(applet);

		switch (name) {
			case "MAGIC_SOURCE" :
				obj = new MagicSourceObject(applet);
				break;
			case "MIRROR_BOX" :
				obj = new MirrorBoxObject(applet);
				break;
		}

		return obj;
	}
}