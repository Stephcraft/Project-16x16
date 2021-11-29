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
	public static enum Option {
		moveLeftKey, moveRightKey, jumpKey, dashKey, targetFPS, snapSize, debugMode, gainBGM, gainSFX, muteBGM, muteSFX , testKey;
	}

	public static int moveLeftKey = options.getInt(Option.moveLeftKey.toString(), KeyEvent.VK_A);
	public static int moveRightKey = options.getInt(Option.moveRightKey.toString(), KeyEvent.VK_D);
	public static int jumpKey = options.getInt(Option.jumpKey.toString(), KeyEvent.VK_SPACE);
	public static int dashKey = options.getInt(Option.dashKey.toString(), KeyEvent.VK_SHIFT);

	public static int targetFrameRate = options.getInt(Option.targetFPS.toString(), 60);
	public static int snapSize = options.getInt(Option.snapSize.toString(), 32);
	public static int debugMode = options.getInt(Option.debugMode.toString(), 2);

	public static float gainBGM = options.getFloat(Option.gainBGM.toString(), 0);
	public static float gainSFX = options.getFloat(Option.gainSFX.toString(), 0);
	public static boolean muteBGM = options.getBoolean(Option.muteBGM.toString(), false);
	public static boolean muteSFX = options.getBoolean(Option.muteSFX.toString(), false);

	protected static final int frameRateLow = KeyEvent.VK_X;
	protected static final int frameRateHigh = KeyEvent.VK_Z;
	protected static final int frameRateDefault = KeyEvent.VK_N;
	protected static final int toggleDeadzone = KeyEvent.VK_V;
	protected static final int toggleDebug = KeyEvent.VK_TAB;
	protected static final int cameraToMouse = KeyEvent.VK_C;
	protected static final int cameraToPlayer = KeyEvent.VK_F;
	protected static final int shake = KeyEvent.VK_G;
	protected static final int notify = KeyEvent.VK_H;
	
	public static final int lifeCapInc = KeyEvent.VK_P;
	public static final int lifeCapDec = KeyEvent.VK_O;
	public static final int lifeDec = KeyEvent.VK_K;
	public static final int lifeInc = KeyEvent.VK_L;
	public static final int fullscreen = KeyEvent.VK_F11;

	public static void save(Option Option, float value) {
		options.putFloat(Option.toString(), value);
		try {
			options.flush();
		} catch (BackingStoreException e) {
			Notifications.addNotification("Options Error", "Could not flush user preferences to registry.");
		}
	}

	public static void save(Option Option, int value) {
		options.putInt(Option.toString(), value);
		try {
			options.flush();
		} catch (BackingStoreException e) {
			Notifications.addNotification("Options Error", "Could not flush user preferences to registry.");
		}
	}

	public static void save(Option Option, boolean value) {
		options.putBoolean(Option.toString(), value);
		try {
			options.flush();
		} catch (BackingStoreException e) {
			Notifications.addNotification("Options Error", "Could not flush user preferences to registry.");
		}
	}

}