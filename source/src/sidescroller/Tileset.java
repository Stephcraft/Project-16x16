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

public class Tileset extends PClass {

	private final int TILESETSIZE = 16;
	private final String DATAPATH = "Assets/tileData.json";
	
	private HashMap<String, Integer> tileRef = new HashMap<String, Integer>();
	private ArrayList<PImage> loadedTiles = new ArrayList<PImage>();
	
	private JSONArray JSONtiles;
	private JSONArray JSONanimations;
	
	public Tileset(SideScroller a) {
		super(a);
	}
	
	public void load(){
		// load JSON
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
	
	public PImage getTile(int id){
		if (loadedTiles.size() > id)
			return loadedTiles.get(id);
		
		JSONObject tile = JSONtiles.getJSONObject(id);
		int x = (int) (tile.getFloat("x") * TILESETSIZE);
		int y = (int) (tile.getFloat("y") * TILESETSIZE);
		int w = tile.getInt("w");
		int h = tile.getInt("h");
		PImage image = getTile(x, y, w, h);
		loadedTiles.add(image);
		return image;
	}
	
	public PImage getTile(String name){
		int id = getTileId(name);
		return getTile(id);
	}
	
	public PImage getTile(int x, int y, int w, int h)
	{
		return applet.graphicsSheet.get(x, y, w, h);
	}
		
	public ArrayList<PImage> getAnimation(String name){
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
	
	public PGraphics getTileGraphic(String name, float scale) {
		PImage image = getTile(name);
		return util.pg(image, scale);
	}
	
	public ArrayList<PGraphics> getAnimationGraphic(String name, float scale)
	{
		return util.pg(getAnimation(name), 4);
	}
	
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
	
	public int getTileId(String key){
		if (tileRef.containsKey(key))
			return tileRef.get(key);
		
		PApplet.println("<Tileset> Error while loading, null string reference to tile ( " + key + " ) >");
		return 0;
	}
	
	public String getTileType(String name) {
		int id = getTileId(name);
		JSONObject tile = JSONtiles.getJSONObject(id);
		String type = tile.getString("type", "COLLISION");
		return type;
	}
	
	public int loadedTilesSize()
	{
		return loadedTiles.size();
	}
	
	public String getTileName(int id)
	{
		JSONObject tile = JSONtiles.getJSONObject(id);
		return tile.getString("name");
	}
}