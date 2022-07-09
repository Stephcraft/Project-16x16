import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.core.PVector;
import project_16x16.Tileset;
import project_16x16.components.Tile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class TileTest {

    private Tile tile;
    private PImage image;


    @BeforeEach
    void setup()  {
        image = mock(PImage.class);
        tile = new Tile(1, "tile", image, Tile.TileType.BACKGROUND);
    }

    @Test
    void constructorTest() {
            assertNotNull(tile);
    }
    @Test
    void getIDTest() {
        assertEquals(tile.getID(), 1);
    }
    @Test
    void getNameTest() {
        assertEquals(tile.getName(), "tile");
    }
    @Test
    void getPImageTest() {
        assertEquals(tile.getPImage(), image);
    }
    @Test
    void getTileTypeTest() {
        assertEquals(tile.getTileType(), Tile.TileType.BACKGROUND);
    }
    @Test
    void getPositionTest() {
        PVector vector = new PVector(Tileset.TILESETWIDTH, 0); // as ID = 1
        assertEquals(tile.getPosition(), vector);
    }
}
