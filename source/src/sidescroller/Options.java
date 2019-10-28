package sidescroller;

import processing.core.PApplet;
import processing.data.JSONObject;

public class Options {
	
	private static final String SAVEPATH = "Assets/options.json";
	
	public static int moveLeftKey = 37;		//A = 65	LeftArrow = 37
	public static int moveRightKey = 39;	//D = 68	RightArrow = 39
	public static int jumpKey = 38;			//W = 87	UpArrow = 38
	public static int attackKey = 40;		//S = 83    DownArrow = 40
	public static int dashKey = 16;			//Shift = 16
	public static int targetFrameRate = 60;

	public static void load()
	{
		PApplet app = new PApplet();
		app.sketchPath(SAVEPATH);
		JSONObject json;
		
		try
		{
			json = app.loadJSONObject(SAVEPATH);
			
			moveLeftKey = json.getInt("moveLeftKey", moveLeftKey);
			moveRightKey = json.getInt("moveRightKey", moveRightKey);
			jumpKey = json.getInt("jumpKey", jumpKey);
			attackKey = json.getInt("attackKey", attackKey);
			dashKey = json.getInt("dashKey", dashKey);
			targetFrameRate = json.getInt("targetFrameRate", targetFrameRate);
		} catch (Exception e) {
			save();
		}
	}
	
	public static void save()
	{
		PApplet app = new PApplet();
		app.sketchPath(SAVEPATH);
		
		JSONObject json = new JSONObject();
		
		json.setInt("moveLeftKey", moveLeftKey);
		json.setInt("moveRightKey", moveRightKey);
		json.setInt("jumpKey", jumpKey);
		json.setInt("attackKey", attackKey);
		json.setInt("dashKey", dashKey);
		json.setInt("targetFrameRate", targetFrameRate);
		
		app.saveJSONObject(json, SAVEPATH);
	}
}