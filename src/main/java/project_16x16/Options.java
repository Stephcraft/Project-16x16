package project_16x16;

import java.util.prefs.Preferences;

/**
 * Handle loading/saving of player settings (game options)
 */
public class Options {
	
	private static final Preferences options = Preferences.userNodeForPackage(Options.class);
	
	public static enum option {
		moveLeftKey, moveRightKey, jumpKey, dashKey, targetFPS, snapSize, debugMode;
	}

	public static int moveLeftKey = options.getInt(option.moveLeftKey.toString(), 65); // 65 = 'A'
	public static int moveRightKey = options.getInt(option.moveRightKey.toString(), 68); // 68 = 'D'
	public static int jumpKey = options.getInt(option.jumpKey.toString(), 32); // 32 = SPACE
	public static int dashKey = options.getInt(option.dashKey.toString(), 16); // 16 = SHIFT
	public static int targetFrameRate = options.getInt(option.targetFPS.toString(), 60);
	public static int snapSize = options.getInt(option.snapSize.toString(), 32);
	public static int debugMode = options.getInt(option.debugMode.toString(), 0); // 0 = OFF
		
	public static void save(option option, int value) {
		options.putInt(option.toString(), value);
	}
	
}