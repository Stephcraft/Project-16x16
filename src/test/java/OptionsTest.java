import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import java.util.prefs.Preferences;
import project_16x16.Options;


public class OptionsTest {

    @Test
    public void CallingSaveWithIntShouldUpdateOptions(){
        int expected = 5;
        Preferences options = Preferences.userNodeForPackage(Options.class);

        Options.save(Options.option.testKey , expected);

        assertEquals(expected , options.getInt(Options.option.testKey.toString(),0));
    }

    @Test
    public void CallingSaveWithFloatShouldUpdateOptions(){
        float expected = 5.5f;
        Preferences options = Preferences.userNodeForPackage(Options.class);

        Options.save(Options.option.testKey , expected);

        assertEquals(expected , options.getFloat(Options.option.testKey.toString(),0));
    }

    @Test
    public void CallingSaveWithBooleanShouldUpdateOptions(){
        Preferences options = Preferences.userNodeForPackage(Options.class);

        Options.save(Options.option.testKey , true);

        assertEquals(true , options.getBoolean(Options.option.testKey.toString(),false));
    }


}
