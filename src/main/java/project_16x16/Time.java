package project_16x16;

/**
 * Global game clock. {@link #tick()} is called once at the start of every frame
 * (see {@link SideScroller#draw()}), so every system sees the same frame time
 * regardless of the frame rate.
 * <p>
 * Conventions:
 * <ul>
 * <li>Timestamps and durations are in <b>milliseconds</b> ({@link #millis()},
 * {@link #deltaMillis()}).</li>
 * <li>Rates (speeds, accelerations, smoothing) are <b>per second</b>, and are
 * integrated with {@link #delta()} (seconds).</li>
 * </ul>
 * <p>
 * The frame delta is clamped to {@link #MAX_DELTA_MILLIS}, so a stall (window
 * drag, level load, GC pause) advances the game by one bounded step rather
 * than teleporting objects. Consequently {@link #millis()} is game time, and
 * may fall behind wall-clock time.
 *
 * @author micycle1
 */
public final class Time {

	/**
	 * Longest frame the game will simulate; longer frames are treated as this long
	 * (i.e. below 10 FPS the game runs in slow motion).
	 */
	static final long MAX_DELTA_MILLIS = 100;
	private static final long MAX_DELTA_NANOS = MAX_DELTA_MILLIS * 1_000_000;

	private static long lastNanos = System.nanoTime();
	private static long gameNanos = 0;
	private static long deltaNanos = 0;
	private static float deltaSeconds = 0;

	private Time() {
	}

	/**
	 * Restarts the clock at zero. Call once loading is complete, so that the time
	 * spent loading isn't counted as the first frame.
	 */
	public static void reset() {
		lastNanos = System.nanoTime();
		gameNanos = 0;
		deltaNanos = 0;
		deltaSeconds = 0;
	}

	/**
	 * Advances the clock by the real time elapsed since the previous tick. Call
	 * exactly once per frame, before any game logic.
	 */
	public static void tick() {
		final long now = System.nanoTime();
		advance(now - lastNanos);
		lastNanos = now;
	}

	static void advance(long elapsedNanos) {
		deltaNanos = Math.max(0, Math.min(elapsedNanos, MAX_DELTA_NANOS));
		deltaSeconds = deltaNanos / 1e9f;
		gameNanos += deltaNanos;
	}

	/**
	 * @return game time elapsed since the clock was {@link #reset()} (ms)
	 */
	public static long millis() {
		return gameNanos / 1_000_000;
	}

	/**
	 * @return duration of the current frame (ms)
	 */
	public static float deltaMillis() {
		return deltaNanos / 1e6f;
	}

	/**
	 * @return duration of the current frame (seconds). Multiply per-second rates by
	 *         this to get the per-frame amount.
	 */
	public static float delta() {
		return deltaSeconds;
	}

	/**
	 * Frame-rate independent replacement for a fixed per-frame lerp amount. Moving
	 * a value towards its target by this amount every frame closes the fraction
	 * {@code 1 - e^(-rate * t)} of the gap after {@code t} seconds, whatever the
	 * frame rate.
	 * <p>
	 * To convert a lerp amount {@code a} tuned at 60 FPS:
	 * {@code rate = -ln(1 - a) * 60}.
	 *
	 * @param rate smoothing rate (per second); higher is snappier
	 * @return lerp amount [0, 1) for this frame
	 */
	public static float smoothing(float rate) {
		return 1 - (float) Math.exp(-rate * deltaSeconds);
	}

	/**
	 * Smoothly moves {@code current} towards {@code target}.
	 *
	 * @param rate smoothing rate (per second)
	 * @see #smoothing(float)
	 */
	public static float damp(float current, float target, float rate) {
		return current + (target - current) * smoothing(rate);
	}

	/**
	 * Number of equal sub-steps to split this frame into, such that no sub-step
	 * exceeds {@code maxStep}. Used to keep physics (collision detection in
	 * particular) stable when the frame rate is low.
	 *
	 * @param maxStep longest allowed step (seconds)
	 * @return number of steps (at least 1); each lasts {@code delta() / steps}
	 */
	public static int substeps(float maxStep) {
		return Math.max(1, (int) Math.ceil(deltaSeconds / maxStep));
	}
}
