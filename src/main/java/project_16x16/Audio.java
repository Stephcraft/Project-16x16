package project_16x16;

import java.util.HashMap;

import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;

/**
 * Provides static methods for playing game audio: background music and sound
 * effects.
 * 
 * @author micycle1
 *
 */
public final class Audio {

	private static final String BGM_PATH = "Audio/BGM/";
	private static final String SFX_PATH = "Audio/SFX/";

	private static Minim minim;

	/**
	 * Background music, which are referenced as enums.
	 */
	public enum BGM {
		DEFAULT("first_theme.mp3"), // TODO
		DEFAULT1("cyberSynthwave.mp3"); // TODO

		private String filename;

		private BGM(String filename) {
			this.filename = filename;
		}

		public String getPath() {
			return BGM_PATH + filename;
		}
	}

	/**
	 * Sound effects, which are referenced as enums.
	 */
	public enum SFX {
		JUMP("jump.wav");

		private String filename;

		private SFX(String filename) {
			this.filename = filename;
		}

		public String getPath() {
			return SFX_PATH + filename;
		}
	}

	private static final HashMap<SFX, AudioSample> SFX_MAP = new HashMap<>(); // could load from json
	private static final HashMap<BGM, AudioPlayer> BGM_MAP = new HashMap<>(); // could load from json

	/**
	 * Call during setup to instantiate a connection to Minim (the audio backend).
	 * 
	 * @param s
	 */
	public static void assignApplet(SideScroller s) {
		minim = new Minim(s);

		for (SFX sfx : SFX.values()) {
			SFX_MAP.put(sfx, minim.loadSample(sfx.getPath()));
		}
		for (BGM bgm : BGM.values()) {
			BGM_MAP.put(bgm, minim.loadFile(bgm.getPath()));
		}

		setGainBGM(Options.gainBGM);
		setGainSFX(Options.gainSFX);
	}

	/**
	 * Play a sound effect once. Can be called again before the sound finishes
	 * playing.
	 * 
	 * @param sound sound effect name
	 * @see #play(SFX, float)
	 * @see ddf.minim.AudioSample#trigger()
	 */
	public static void play(SFX sound) {
		SFX_MAP.get(sound).trigger();
	}

	/**
	 * Plays a sound effect once (does not loop). Can be called again before the
	 * sound finishes playing. The sound effect gain is specified with this method.
	 * 
	 * @param sound sound effect name
	 * @param gain  gain, in decibels (where negative is quieter). Default = 0.
	 * @see #play(SFX)
	 * @see ddf.minim.AudioSample#trigger()
	 */
	public static void play(SFX sound, float gain) {
		SFX_MAP.get(sound).setGain(gain);
		SFX_MAP.get(sound).trigger();
	}

	/**
	 * Plays a background music track. Only one BGM track can play at once -- this
	 * method stops any existing BGM track. BGM tracks loop by default.
	 * 
	 * @param sound BGM track
	 * @see #play(BGM, float)
	 */
	public static void play(BGM sound) {
		for (AudioPlayer bgm : BGM_MAP.values()) {
			if (bgm.isPlaying()) {
				bgm.pause();
				bgm.rewind();
			}
		}
		BGM_MAP.get(sound).loop();
	}

	/**
	 * Plays a background music track at a specific gain (volume). Only one BGM
	 * track can play at once -- this method stops any existing BGM track. BGM
	 * tracks loop by default.
	 * 
	 * @param sound BGM track
	 * @param gain  gain, in decibels (where negative is quieter). Default = 0.
	 * @see #play(BGM)
	 */
	public static void play(BGM sound, float gain) {
		BGM_MAP.get(sound).setGain(gain);
		play(sound);
	}

	/**
	 * Sets gain (volume) for background music.
	 * 
	 * @param gain gain, in decibels (where negative is quieter). Default = 0.
	 */
	public static void setGainBGM(float gain) {
		BGM_MAP.values().forEach(sound -> sound.setGain(gain));
	}

	/**
	 * Sets gain (volume) for sound effects.
	 * 
	 * @param gain gain, in decibels (where negative is quieter). Default = 0.
	 */
	public static void setGainSFX(float gain) {
		SFX_MAP.values().forEach(sound -> sound.setGain(gain));
	}

	/**
	 * Global mute.
	 */
	public static void mute() {
		SFX_MAP.values().forEach(sound -> sound.mute());
		BGM_MAP.values().forEach(sound -> sound.mute());
	}

	/**
	 * Global unmute.
	 */
	public static void unMute() {
		SFX_MAP.values().forEach(sound -> sound.unmute());
		BGM_MAP.values().forEach(sound -> sound.unmute());
	}

	/**
	 * Stops Minim and releases all audio resources. Call when application is
	 * closed.
	 * 
	 * @see Minim#stop()
	 */
	public static void exit() {
		minim.stop();
	}

}
