package sidescroller;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;

public class Tileset extends PClass {

	private final String DATAPATH = "Assets/tileData.json";
	
	private PImage tileset;
	
	private JSONObject tileData;
	private JSONArray tiles;
	private JSONArray animations;
	
	public Tileset(SideScroller a) {
		super(a);
		//setTestJson();
		// load JSON
		tileData = applet.loadJSONObject(DATAPATH);
		tiles = tileData.getJSONArray("tiles");
		animations = tileData.getJSONArray("animations");
		// load tile set
		tileset = applet.loadImage("Assets/Art/graphics-sheet.png");
	}
	
	public PImage getTile(int id)
	{
		JSONObject tile = tiles.getJSONObject(id);
		int x = tile.getInt("x") * 16;
		int y = tile.getInt("y") * 16;
		return tileset.get(x, y, 16, 16);
	}
	
	public PImage getTile(String name)
	{
		for(int i  = 0; i < tiles.size(); i++)
		{
			JSONObject tile = tiles.getJSONObject(i);
			
			if (tile.getString("name").contentEquals(name))
			{
				return getTile(i);
			}
		}
		
		PApplet.println("tile does not exist");
		return null;
	}
	
	public ArrayList<PImage> getAnimation(int id)
	{
		JSONObject animation = animations.getJSONObject(id);
		ArrayList<PImage> frames = new ArrayList<PImage>();
		JSONArray tileRefs = animation.getJSONArray("tileRefs");
		
		for(int i = 0; i < tileRefs.size(); i++)
		{
			JSONObject tileRef = tileRefs.getJSONObject(i);
			frames.add(getTile(tileRef.getInt("id", 0)));
		}

		return frames;
	}
	
	public ArrayList<PImage> getAnimation(String name)
	{
		for(int i = 0; i < animations.size(); i++)
		{
			JSONObject animation = animations.getJSONObject(i);
			if (animation.getString("name").contentEquals(name))
			{
				return getAnimation(i);
			}
		}
		
		return null;
	}
	
//test method creates JSON template
private void setTestJson()
{
	int size = 32;
	tileData = new JSONObject();
	JSONArray tiles = new JSONArray();
	
	for(int i = 0; i < 2; i++)
	{
		JSONObject tile = new JSONObject();
		
		int x = i%size;
		int y = i/size;
		
		tile.setInt("id", i);
		tile.setString("name", "new Tile");
		tile.setInt("x", x);
		tile.setInt("y", y);
		tile.setInt("width", 1);
		tile.setInt("height", 1);
		
		tiles.setJSONObject(i, tile);
	}
	
	tileData.setJSONArray("tiles", tiles);
	
	JSONArray animations = new JSONArray();
	
	for(int i = 0; i < 2; i++)
	{
		JSONObject animation = new JSONObject();
		
		animation.setInt("id", i);
		animation.setString("name", "new animation");

		JSONArray tileRefs = new JSONArray();
		
		for(int k = 0; k < 2; k++)
		{
			JSONObject tileRef = new JSONObject();
			tileRef.setInt("id", k);
			tileRefs.setJSONObject(k, tileRef);
		}
		
		animation.setJSONArray("tileRefs", tileRefs);
		
		animations.setJSONObject(i, animation);
	}
	
	tileData.setJSONArray("animations", animations);
	
	applet.saveJSONObject(tileData, DATAPATH);
}
}