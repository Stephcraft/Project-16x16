/**
 * 
 */
package project_16x16;

import static project_16x16.Util.colorToRGB;

/**
 * A place to storing global constants & defaults, to referenced by multiple
 * classes. Things used in code; store options {@link Options}.
 * 
 * @author micycle1
 *
 */
public final class Constants {

	public static final float CAMERA_LERP = 0.05f;
	public static final float CAMERA_ZOOM_MAX = 0.3f;
	public static final float CAMERA_ZOOM_MIN = 0.3f;
	public static final float GAME_GRAVITY = 1;
	
	public static final String GAME_FONT = "Font/font-pixel-48.vlw";
	
	/**
	 * Default level to load during development.
	 */
	public static final String DEV_LEVEL = "Storage/Game/Maps/gg-2.dat";

	/**
	 * Colours
	 * @author micycle1
	 *
	 */
	public static class Colors {
		
		public static final int MENU_GREY = colorToRGB(29, 33, 45);
		
	}

}
