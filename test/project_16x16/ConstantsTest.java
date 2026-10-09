package project_16x16;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Constants class.
 */
class ConstantsTest {

    /**
     * Verifies that the constant values are as expected.
     */
    @Test
    void CallingConstantValues_returnsExpectedValues() {
        assertEquals(0.05f, Constants.CAMERA_LERP, 1e-6);
        assertEquals(3.0f, Constants.CAMERA_ZOOM_MAX, 1e-6);
        assertEquals(0.3f, Constants.CAMERA_ZOOM_MIN, 1e-6);
        assertEquals(1f, Constants.GAME_GRAVITY, 1e-6);
        assertEquals("Font/font-pixel-48.vlw", Constants.GAME_FONT);
        assertEquals("Storage/Game/Maps/gg-2.dat", Constants.DEV_LEVEL);
    }

    /**
     * Verifies that the Colors inner class constants are as expected.
     * This test assumes Utility.colorToRGB(29, 33, 45) returns the correct int value.
     */
    @Test
    void CallingColorsMenuGrey_returnsExpectedRGB() {
        int expected = Utility.colorToRGB(29, 33, 45);
        assertEquals(expected, Constants.Colors.MENU_GREY);
    }

    /**
     * Ensures the Constants class cannot be instantiated.
     */
    @Test
    void CallingConstructor_throwsException() throws Exception {
        Constructor<Constants> ctor = Constants.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThrows(InvocationTargetException.class, ctor::newInstance);
    }
}
