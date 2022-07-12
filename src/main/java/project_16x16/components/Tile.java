package project_16x16.components;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.Tileset;

public class Tile {

	private int id;
	private String name;
	private PImage pImage;
	private TileType tileType;

	public enum TileType {
		COLLISION, BACKGROUND, OBJECT, ENTITY;
	}

	public Tile(int id, String name, PImage pImage, TileType tileType) {
		this.id = id;
		this.name = name;
		this.pImage = pImage;
		this.tileType = tileType;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public PImage getPImage() {
		return pImage;
	}

	public TileType getTileType() {
		return tileType;
	}

	public PVector getPosition() {
		return new PVector(Tileset.TILESETWIDTH / id, Tileset.TILESETWIDTH % id);
	}

}
