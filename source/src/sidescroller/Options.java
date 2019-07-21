package sidescroller;

import org.json.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Options {
	
	private static final String SAVELOCATION = "Assets/options.json";
	
	public static int moveLeftKey = 37;		//A = 65	LeftArrow = 37
	public static int moveRightKey = 39;	//D = 68	RightArrow = 39
	public static int jumpKey = 38;			//W = 87	UpArrow = 38
	public static int attackKey = 40;		//S = 83    DownArrow = 40
	public static int dashKey = 16;			//Shift = 16
	public static int targetFrameRate = 60;

	@SuppressWarnings("unchecked")
	public static void load()
	{
		JSONParser parser = new JSONParser();
		
		try
		{
			Object obj = parser.parse(new FileReader(SAVELOCATION));
			JSONObject jsonObject = (JSONObject) obj;
			
			moveLeftKey = (int) (long) jsonObject.getOrDefault("moveLeftKey", Long.valueOf(moveLeftKey));
			moveRightKey = (int) (long) jsonObject.getOrDefault("moveRightKey", Long.valueOf(moveRightKey));
			jumpKey = (int) (long) jsonObject.getOrDefault("jumpKey", Long.valueOf(jumpKey));
			attackKey = (int) (long) jsonObject.getOrDefault("attackKey", Long.valueOf(attackKey));
			dashKey = (int) (long) jsonObject.getOrDefault("dashKey", Long.valueOf(dashKey));
			targetFrameRate = (int) (long) jsonObject.getOrDefault("targetFrameRate", Long.valueOf(targetFrameRate));
			
		} catch (IOException e)
		{
			
		} catch (ParseException e)
		{
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void save()
	{
		JSONObject save = new JSONObject();
		
		save.put("moveLeftKey", moveLeftKey);
		save.put("moveRightKey", moveRightKey);
		save.put("jumpKey", jumpKey);
		save.put("attackKey", attackKey);
		save.put("dashKey", dashKey);
		save.put("targetFrameRate", targetFrameRate);
		
		try (FileWriter file = new FileWriter(SAVELOCATION))
		{
			file.write(save.toJSONString());
			file.flush();
		} catch (IOException e)
		{
			
		}
	}
}