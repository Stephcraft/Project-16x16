package project_16x16;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;

class OptionsTest {

	@Test
	void callingSaveWithIntShouldUpdateOptions() {
		int expected = 5;
		Preferences options = Preferences.userNodeForPackage(Options.class);

		Options.save(Options.Option.testKey, expected);

		assertEquals(expected, options.getInt(Options.Option.testKey.toString(), 0));
	}

	@Test
	void callingSaveWithFloatShouldUpdateOptions() {
		float expected = 5.5f;
		Preferences options = Preferences.userNodeForPackage(Options.class);

		Options.save(Options.Option.testKey, expected);

		assertEquals(expected, options.getFloat(Options.Option.testKey.toString(), 0));
	}

	@Test
	void callingSaveWithBooleanShouldUpdateOptions() {
		Preferences options = Preferences.userNodeForPackage(Options.class);

		Options.save(Options.Option.testKey, true);

		assertEquals(true, options.getBoolean(Options.Option.testKey.toString(), false));
	}

}
