package project_16x16.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.Tileset;

class TileTest {

	private Tile tile;
	private PImage image;

	@BeforeEach
	void setup() {
		image = mock(PImage.class);
		tile = new Tile(1, "tile", image, Tile.TileType.BACKGROUND);
	}

	@Test
	void callingConstructor_shouldNotFail() {
		assertNotNull(tile);
	}

	@Test
	void callingGetId_shouldReturnExpected() {
		assertEquals(1, tile.getId());
	}

	@Test
	void callingGetName_shouldReturnExpected() {
		assertEquals("tile", tile.getName());
	}

	@Test
	void callingGetPImage_shouldReturnExpected() {
		assertEquals(image, tile.getPImage());
	}

	@Test
	void callingGetTileType_shouldReturnExpected() {
		assertEquals(Tile.TileType.BACKGROUND, tile.getTileType());
	}

	@Test
	void callingGetPosition_shouldReturnExpected() {
		PVector vector = new PVector(Tileset.TILESETWIDTH, 0);
		assertEquals(vector, tile.getPosition());
	}
}
