package project_16x16;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import project_16x16.ui.Notifications;

/**
 * Handle loading/saving of player settings (game options)
 */
public class Options {
	
	private static final Preferences options = Preferences.userNodeForPackage(Options.class);
	public  static final String FRAMEREATE_5000 = " Z: FRAMERATE TO 5000 ";
	public static final String FRAMERATE_20 = " X: FRAMERATE TO 20 ";
	public static final String TOG_DEADZONE = " V: TOGGLE DEADZONE ";
	public static final String CAM_TO_MOUSE = " C: CAMERA TO MOUSE ";
	public static final String CAM_TO_PLAYER = " F: HOOK CAM TO PLAYER ";
	public static final String SHAKE_POP_NOTE = " G: SHAKE , POP NOTE ";
	public static final String INC_LIFE_CAP = " P: INCREASE LIFE CAP ";
	public static final String DEC_LIFE_CAP = " O: DECREASE LIFE CAP ";
	public static final String DEC_LIFE = " K: DECREASE LIFE ";
	public static final String INC_LIFE = " L: INCREASE LIFE";
	public static final String FULLSCREEN = " F11: FULLSCREEN ";
	/**
	 * Define the option as an enum, then create the variable.
	 */
	public static enum option {
		moveLeftKey, moveRightKey, jumpKey, dashKey, targetFPS, snapSize, debugMode, gainBGM, gainSFX;
	}

	public static int moveLeftKey = options.getInt(option.moveLeftKey.toString(), 65); // 65 = 'A'
	public static int moveRightKey = options.getInt(option.moveRightKey.toString(), 68); // 68 = 'D'
	public static int jumpKey = options.getInt(option.jumpKey.toString(), 32); // 32 = SPACE
	public static int dashKey = options.getInt(option.dashKey.toString(), 16); // 16 = SHIFT
	
	public static int targetFrameRate = options.getInt(option.targetFPS.toString(), 60);
	public static int snapSize = options.getInt(option.snapSize.toString(), 32);
	public static int debugMode = options.getInt(option.debugMode.toString(), 0); // 0 = OFF
	
	public static float gainBGM = options.getFloat(option.gainBGM.toString(), 0);
	public static float gainSFX = options.getFloat(option.gainSFX.toString(), 0);

	public static void save(option option, int value) {
		options.putInt(option.toString(), value);
		try {
			options.flush();
		} catch (BackingStoreException e) {
			Notifications.addNotification("Options Error", "Could not flush user preferences to registry.");
		}
	}
	
	public static void save(option option, float value) {
		options.putFloat(option.toString(), value);
		try {
			options.flush();
		} catch (BackingStoreException e) {
			Notifications.addNotification("Options Error", "Could not flush user preferences to registry.");
		}
	}

}