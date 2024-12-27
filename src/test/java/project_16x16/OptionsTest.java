package project_16x16;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;

public class OptionsTest {

	@Test
	public void callingSaveWithIntShouldUpdateOptions() {
		int expected = 5;
		Preferences options = Preferences.userNodeForPackage(Options.class);

		Options.save(Options.Option.TEST_KEY, expected);

		assertEquals(expected, options.getInt(Options.Option.TEST_KEY.toString(), 0));
	}

	@Test
	public void callingSaveWithFloatShouldUpdateOptions() {
		float expected = 5.5f;
		Preferences options = Preferences.userNodeForPackage(Options.class);

		Options.save(Options.Option.TEST_KEY, expected);

		assertEquals(expected, options.getFloat(Options.Option.TEST_KEY.toString(), 0));
	}

	@Test
	public void callingSaveWithBooleanShouldUpdateOptions() {
		Preferences options = Preferences.userNodeForPackage(Options.class);

		Options.save(Options.Option.TEST_KEY, true);

		assertEquals(true, options.getBoolean(Options.Option.TEST_KEY.toString(), false));
	}

}
