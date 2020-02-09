package project_16x16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project_16x16.components.Tile;
import project_16x16.components.Tile.TileType;
import project_16x16.objects.GameObject;
import project_16x16.objects.MagicSourceObject;
import project_16x16.objects.MirrorBoxObject;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;

/**
 * Tileset is a static class that loads and provides PImages.
 */
public class Tileset {

	public static final int TILESETWIDTH = 32;
	public static final int TILESETHEIGHT = 32;
	public static final int TILESETSIZE = 16;
	private static final String TILESHEETPATH = "Art/graphics-sheet.png";
	private static final String DATAPATH = "tileData.json";
	private static final int SCALE = 4;
	
	private static SideScroller applet;
	private static PImage graphicsSheet;
	
	private static int loadedTiles = 0;
	private static HashMap<String, Integer> tileRefs = new HashMap<String, Integer>();
	private static Tile[] tiles;
	
	private static JSONArray JSONanimations;
	
	public static void load(SideScroller app){
		applet = app;
		graphicsSheet = applet.loadImage(TILESHEETPATH);
		tiles = new Tile[TILESETWIDTH * TILESETHEIGHT];
		try {
			loadTiles();
		} catch (Exception e) {
			System.err.println("Could not load tile data. Exiting...");
			System.exit(0);
		}
	}
	
	public static PImage getTile(String name){
		return getTile(getTileId(name));
	}
	
	public static PImage getTile(int Id) {
		if (tiles[Id] != null) {
			return tiles[Id].getPImage();
		} else {
			PApplet.println("<Tileset> Error while loading, null index reference to tile ( " + Id + " ) >");
			return null; // TODO return placeholder?
		}
	}
	
	public static PImage getTile(int x, int y, int w, int h)
	{
		return graphicsSheet.get(x, y, w, h);
	}
	
	/**
	 * Get and scale at once (many graphics are scaled x4).
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param scale
	 * @return
	 * @author micycle1
	 */
	public static PImage getTile(int x, int y, int w, int h, int scale) {
		return Util.resizeImage(graphicsSheet.get(x, y, w, h), scale);
	}
		
	public static String getTileName(int Id)
	{
		if (tiles[Id] != null)
			return tiles[Id].getName();
		
		PApplet.println("<Tileset> Error while loading, null ID reference to tile ( " + Id + " ) >");
		return "";
	}
	
	public static int getTileId(String name){
		if (tileRefs.containsKey(name))
			return tileRefs.get(name);
		
		PApplet.println("<Tileset> Error while loading, null string reference to tile ( " + name + " ) >");
		return 0;
	}
	
//	public static int getTileId(PImage image)
//	{
//		for(int i = 0; i < loadedTiles.size(); i++)
//		{
//			if (loadedTiles.get(i).equals(image))
//				return i;
//		}
//		return -1;
//	}
//	
	public static int getTileCount() {
		return loadedTiles;
	}
	
	public static TileType getTileType(String name) {
		return getTileType(getTileId(name));
	}
	
	public static TileType getTileType(int Id) {
		if (tiles[Id] != null)
			return tiles[Id].getTileType();
		
		PApplet.println("<Tileset> Error while loading, null ID reference to tile ( " + Id + " ) >");
		return null;
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
	
	public static ArrayList<Tile> getAllTiles(TileType type) {
		ArrayList<Tile> tilesArray = new ArrayList<Tile>();
		for(int i = 0; i < tiles.length; i++) {
			if (tiles[i] == null)
				continue;
			TileType tileType = tiles[i].getTileType();
			if (tileType == type)
				tilesArray.add(tiles[i]);
		}
		return tilesArray;
	}
	
	public static ArrayList<Tile> getAllTiles(TileType[] types) {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for(TileType type : types)
			tiles.addAll(getAllTiles(type));
		return tiles;
	}
	
	/**
	 * Returns the class that is associated with the input string (eg.
	 * "MAGIC_SOURCE" --> MagicSourceObject.java). This class object is later used
	 * to create an instance of the class using reflection. This method is used in
	 * loading level info from JSON (ie. constructing Java objects -- map objects --
	 * from their JSON string representation).
	 * 
	 * @author micycle1
	 */
	public static Class<? extends GameObject> getObjectClass(String name) {
		switch (name) {
			case "MAGIC_SOURCE" :
				return MagicSourceObject.class;
			case "MIRROR_BOX" :
				return MirrorBoxObject.class;
			default :
				return GameObject.class;
		}
	}
	
	private static void loadTiles() {
		
		JSONObject JSONtileData = applet.loadJSONObject(DATAPATH);
		JSONArray JSONtiles = JSONtileData.getJSONArray("tiles");
		JSONanimations = JSONtileData.getJSONArray("animations");
		
		for(int i  = 0; i < JSONtiles.size(); i++) {
			JSONObject tile = JSONtiles.getJSONObject(i);
			String name = tile.getString("name");
			int x = tile.getInt("x");
			int y = tile.getInt("y");
			int ID = x + y * TILESETWIDTH;
			PImage image = getTile( x * TILESETSIZE, y * TILESETSIZE, TILESETSIZE, TILESETSIZE);
			
			image = Util.resizeImage(image, SCALE);
			TileType tileType;
			
			String type = JSONtiles.getJSONObject(i).getString("type", "COLLISION");
			switch(type) {
				case "COLLISION":
					tileType = TileType.COLLISION;
				case "BACKGROUND":
					tileType = TileType.BACKGROUND;
				case "OBJECT":
					tileType = TileType.OBJECT;
				case "ENTITY":
					tileType = TileType.ENTITY;
				default:
					tileType = TileType.COLLISION;
			}
			
			Tile newTile = new Tile(ID, name, image, tileType);
			tileRefs.put(name, ID);
			tiles[ID] = newTile;
			loadedTiles++;
		}
	}
}