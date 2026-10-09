package project_16x16;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeTest {

	private static final long MS = 1_000_000; // nanos

	@BeforeEach
	public void resetClock() {
		Time.reset();
	}

	@Test
	public void advanceShouldAccumulateGameTime() {
		Time.advance(16 * MS);
		Time.advance(17 * MS);

		assertEquals(33, Time.millis());
		assertEquals(17, Time.deltaMillis(), 1e-4);
		assertEquals(0.017f, Time.delta(), 1e-6);
	}

	@Test
	public void longFramesShouldBeClamped() {
		Time.advance(5000 * MS);

		assertEquals(Time.MAX_DELTA_MILLIS, Time.millis());
		assertEquals(Time.MAX_DELTA_MILLIS, Time.deltaMillis(), 1e-4);
	}

	@Test
	public void smoothingShouldBeFrameRateIndependent() {
		final float rate = Constants.CAMERA_SMOOTHING;

		float at60 = 0;
		for (int i = 0; i < 60; i++) {
			Time.advance(1_000_000_000L / 60);
			at60 = Time.damp(at60, 1, rate);
		}
		float at240 = 0;
		for (int i = 0; i < 240; i++) {
			Time.advance(1_000_000_000L / 240);
			at240 = Time.damp(at240, 1, rate);
		}

		assertEquals(1 - Math.exp(-rate), at60, 1e-4); // after one second
		assertEquals(at60, at240, 1e-4);
	}

	@Test
	public void substepsShouldNotExceedMaxStep() {
		Time.advance(0);
		assertEquals(1, Time.substeps(1 / 120f));

		Time.advance(1_000_000_000L / 30);
		final int steps = Time.substeps(1 / 120f);
		assertEquals(4, steps, 1); // float rounding may add one
		assertEquals(true, Time.delta() / steps <= 1 / 120f);
	}
}
