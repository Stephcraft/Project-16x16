package game.objects;

import lombok.Data;
import processing.core.PVector;

@Data
public class BaseObjectData {
	private PVector pos;
	private int width;
	private int height;
	private String id;
}
