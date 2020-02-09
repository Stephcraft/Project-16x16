package project_16x16.components;

import processing.core.PImage;
import processing.core.PVector;
import project_16x16.Tileset;

public class Tile {

	private int ID;
	private String name;
	private PImage pImage;
	private TileType tileType;
	
	public enum TileType {
		COLLISION, BACKGROUND, OBJECT, ENTITY;
	}
	
	public Tile(int ID, String name, PImage pImage, TileType tileType) {
		this.ID = ID;
		this.name = name;
		this.pImage = pImage;
		this.tileType = tileType;
	}
	
	public int getID() { return ID; }
	public String getName() { return name; }
	public PImage getPImage() { return pImage; }
	public TileType getTileType() { return tileType; }
	public PVector getPosition() { return new PVector(Tileset.TILESETWIDTH/ID, Tileset.TILESETWIDTH % ID); }
}
