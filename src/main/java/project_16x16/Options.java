package project_16x16;

import java.awt.event.KeyEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import project_16x16.ui.Notifications;

/**
 * Handle loading/saving of player settings (game options)
 */
public class Options {
	
	private static final Preferences options = Preferences.userNodeForPackage(Options.class);
	
	/**
	 * Define the option as an enum, then create the variable.
	 */
	public static enum option {
		moveLeftKey, moveRightKey, jumpKey, dashKey, targetFPS, snapSize, debugMode, gainBGM, gainSFX;
	}
	
	public static int moveLeftKey = options.getInt(option.moveLeftKey.toString(), KeyEvent.VK_A);
	public static int moveRightKey = options.getInt(option.moveRightKey.toString(), KeyEvent.VK_D); // 68 = 'D'
	public static int jumpKey = options.getInt(option.jumpKey.toString(), KeyEvent.VK_SPACE); // 32 = SPACE
	public static int dashKey = options.getInt(option.dashKey.toString(), KeyEvent.VK_SHIFT); // 16 = SHIFT
	
	public static int targetFrameRate = options.getInt(option.targetFPS.toString(), 60);
	public static int snapSize = options.getInt(option.snapSize.toString(), 32);
	public static int debugMode = options.getInt(option.debugMode.toString(), 2);
	
	public static float gainBGM = options.getFloat(option.gainBGM.toString(), 0);
	public static float gainSFX = options.getFloat(option.gainSFX.toString(), 0);
	
	protected static final int frameRateLow = KeyEvent.VK_X;
	protected static final int frameRateHigh = KeyEvent.VK_Z;
	protected static final int frameRateDefault = KeyEvent.VK_N;
	protected static final int toggleDeadzone = KeyEvent.VK_V;
	protected static final int toggleDebug = KeyEvent.VK_TAB;
	protected static final int cameraToMouse = KeyEvent.VK_C;
	protected static final int cameraToPlayer = KeyEvent.VK_F;
	protected static final int shake = KeyEvent.VK_G;
	protected static final int notify = KeyEvent.VK_H;
	protected static final int lifeCapInc = KeyEvent.VK_P;
	protected static final int lifeCapDec = KeyEvent.VK_O;
	protected static final int lifeDec = KeyEvent.VK_K;
	protected static final int lifeInc = KeyEvent.VK_L;
	protected static final int fullscreen = KeyEvent.VK_F11;

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