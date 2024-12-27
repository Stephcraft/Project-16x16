package project_16x16;

import java.awt.event.KeyEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import project_16x16.ui.Notifications;

/**
 * Handle loading/saving of player settings (game options).
 */
public class Options {

	// preferences are stored in the Windows Registry
	private static final Preferences options = Preferences.userNodeForPackage(Options.class);

	/*
	 * TODO might be best to combine the key name (see current enum) with the
	 * default key value and the current/live value into one object. this way,
	 * string values in buttons can update live and one could call .save(), which
	 * would save the new mapping AND set the in-game mapping in one operation!
	 */

	/**
	 * Defines all configurable options in the game
	 */
	public static enum Option {
		// @formatter:off
        // Movement controls
        MOVE_LEFT_KEY,
        MOVE_RIGHT_KEY,
        JUMP_KEY,
        DASH_KEY,

        // Debug and development controls
        FRAME_RATE_LOW_KEY,
        FRAME_RATE_HIGH_KEY,
        FRAME_RATE_DEFAULT_KEY,
        TOGGLE_DEADZONE_KEY,
        TOGGLE_DEBUG_KEY,

        // Camera controls
        CAMERA_TO_MOUSE_KEY,
        CAMERA_TO_PLAYER_KEY,
        CAMERA_SHAKE_KEY,

        // UI controls
        NOTIFY_KEY,
        TOGGLE_FULLSCREEN_KEY,

        // Life system controls
        LIFE_CAP_INCREASE_KEY,
        LIFE_CAP_DECREASE_KEY,
        LIFE_INCREASE_KEY,
        LIFE_DECREASE_KEY,

        // Other settings
        TARGET_FPS,
        SNAP_SIZE,
        DEBUG_MODE,
        GAIN_BGM,
        GAIN_SFX,
        MUTE_BGM,
        MUTE_SFX,

        TEST_KEY,
     // @formatter:on
	}

	/**
	 * Defines default key mappings and their associated actions
	 */
	public static class DefaultKeys {
		// Movement controls
		public static final int MOVE_LEFT = KeyEvent.VK_A;
		public static final int MOVE_RIGHT = KeyEvent.VK_D;
		public static final int JUMP = KeyEvent.VK_SPACE;
		public static final int DASH = KeyEvent.VK_SHIFT;

		// Debug and development controls
		public static final int FRAME_RATE_LOW = KeyEvent.VK_X;
		public static final int FRAME_RATE_HIGH = KeyEvent.VK_Z;
		public static final int FRAME_RATE_DEFAULT = KeyEvent.VK_N;
		public static final int TOGGLE_DEADZONE = KeyEvent.VK_V;
		public static final int TOGGLE_DEBUG = KeyEvent.VK_TAB;

		// Camera controls
		public static final int CAMERA_TO_MOUSE = KeyEvent.VK_C;
		public static final int CAMERA_TO_PLAYER = KeyEvent.VK_F;
		public static final int CAMERA_SHAKE = KeyEvent.VK_G;

		// UI controls
		public static final int NOTIFY = KeyEvent.VK_H;
		public static final int TOGGLE_FULLSCREEN = KeyEvent.VK_F11;

		// Life system controls
		public static final int LIFE_CAP_INCREASE = KeyEvent.VK_P;
		public static final int LIFE_CAP_DECREASE = KeyEvent.VK_O;
		public static final int LIFE_INCREASE = KeyEvent.VK_L;
		public static final int LIFE_DECREASE = KeyEvent.VK_K;
	}

	/**
	 * Game settings with their default values
	 */
	// Movement key bindings
	public static int moveLeftKey = options.getInt(Option.MOVE_LEFT_KEY.toString(), DefaultKeys.MOVE_LEFT);
	public static int moveRightKey = options.getInt(Option.MOVE_RIGHT_KEY.toString(), DefaultKeys.MOVE_RIGHT);
	public static int jumpKey = options.getInt(Option.JUMP_KEY.toString(), DefaultKeys.JUMP);
	public static int dashKey = options.getInt(Option.DASH_KEY.toString(), DefaultKeys.DASH);

	// Debug and development key bindings
	public static int frameRateLowKey = options.getInt(Option.FRAME_RATE_LOW_KEY.toString(), DefaultKeys.FRAME_RATE_LOW);
	public static int frameRateHighKey = options.getInt(Option.FRAME_RATE_HIGH_KEY.toString(), DefaultKeys.FRAME_RATE_HIGH);
	public static int frameRateDefaultKey = options.getInt(Option.FRAME_RATE_DEFAULT_KEY.toString(), DefaultKeys.FRAME_RATE_DEFAULT);
	public static int toggleDeadzoneKey = options.getInt(Option.TOGGLE_DEADZONE_KEY.toString(), DefaultKeys.TOGGLE_DEADZONE);
	public static int toggleDebugKey = options.getInt(Option.TOGGLE_DEBUG_KEY.toString(), DefaultKeys.TOGGLE_DEBUG);

	// Camera key bindings
	public static int cameraToMouseKey = options.getInt(Option.CAMERA_TO_MOUSE_KEY.toString(), DefaultKeys.CAMERA_TO_MOUSE);
	public static int cameraToPlayerKey = options.getInt(Option.CAMERA_TO_PLAYER_KEY.toString(), DefaultKeys.CAMERA_TO_PLAYER);
	public static int cameraShakeKey = options.getInt(Option.CAMERA_SHAKE_KEY.toString(), DefaultKeys.CAMERA_SHAKE);

	// UI key bindings
	public static int notifyKey = options.getInt(Option.NOTIFY_KEY.toString(), DefaultKeys.NOTIFY);
	public static int toggleFullscreenKey = options.getInt(Option.TOGGLE_FULLSCREEN_KEY.toString(), DefaultKeys.TOGGLE_FULLSCREEN);

	// Life system key bindings
	public static int lifeCapIncreaseKey = options.getInt(Option.LIFE_CAP_INCREASE_KEY.toString(), DefaultKeys.LIFE_CAP_INCREASE);
	public static int lifeCapDecreaseKey = options.getInt(Option.LIFE_CAP_DECREASE_KEY.toString(), DefaultKeys.LIFE_CAP_DECREASE);
	public static int lifeIncreaseKey = options.getInt(Option.LIFE_INCREASE_KEY.toString(), DefaultKeys.LIFE_INCREASE);
	public static int lifeDecreaseKey = options.getInt(Option.LIFE_DECREASE_KEY.toString(), DefaultKeys.LIFE_DECREASE);

	// Game settings
	public static int targetFrameRate = options.getInt(Option.TARGET_FPS.toString(), 60);
	public static int snapSize = options.getInt(Option.SNAP_SIZE.toString(), 32);
	public static int debugMode = options.getInt(Option.DEBUG_MODE.toString(), 2);

	// Audio settings
	public static float gainBGM = options.getFloat(Option.GAIN_BGM.toString(), 0);
	public static float gainSFX = options.getFloat(Option.GAIN_SFX.toString(), 0);
	public static boolean muteBGM = options.getBoolean(Option.MUTE_BGM.toString(), false);
	public static boolean muteSFX = options.getBoolean(Option.MUTE_SFX.toString(), false);

	/**
	 * Saves a float value to the preferences
	 */
	public static void save(Option option, float value) {
		options.putFloat(option.toString(), value);
		flushOptions();
	}

	/**
	 * Saves an integer value to the preferences
	 */
	public static void save(Option option, int value) {
		options.putInt(option.toString(), value);
		flushOptions();
	}

	/**
	 * Saves a boolean value to the preferences
	 */
	public static void save(Option option, boolean value) {
		options.putBoolean(option.toString(), value);
		flushOptions();
	}

	/**
	 * Flushes changes to the system's preference store
	 */
	private static void flushOptions() {
		try {
			options.flush();
		} catch (BackingStoreException e) {
			Notifications.addNotification("Options Error", "Could not flush user preferences to registry.");
		}
	}
}