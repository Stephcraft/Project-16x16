package scene.components;

import processing.core.PApplet;
import processing.core.PVector;
import sidescroller.PClass;
import sidescroller.SideScroller;

public class WorldViewportEditor extends PClass {
	private float sideRight;
	private float sideLeft;
	private float sideUp;
	private float sideDown;

	enum Side {
		RIGHT, LEFT, UP, DOWN, NONE
	}

	private boolean focus;
	private boolean focused;
	private Side focusSide;

	private boolean hover;
	private Side hoverSide;
	
	/**
	 * todo worldWidth and worldHeight can be calculated from the outermost tiles.
	 */
	private int worldWidth = 0; // todo
	private int worldHeight = 0; // todo
	private PVector worldPosition = new PVector(0, 0);

	public WorldViewportEditor(SideScroller a) {
		super(a);

		sideRight = a.width / 2;
		sideLeft = -a.width / 2;
		sideUp = -a.height / 2;
		sideDown = a.height / 2;

		focusSide = Side.NONE;
		hoverSide = Side.NONE;
	}

	public void updateEditor() {

		// Activate Element
		if (applet.mousePressEvent) {

			// Side Up
			if (hover(worldPosition.x, worldPosition.y - worldHeight / 2, worldWidth, 10)) {
				focusSide = Side.UP;
				focus = true;
				focused = true;
			}

			// Side Down
			else if (hover(worldPosition.x, worldPosition.y + worldHeight / 2, worldWidth,
					10)) {
				focusSide = Side.DOWN;
				focus = true;
				focused = true;
			}

			// Side right
			else if (hover(worldPosition.x + worldWidth / 2, worldPosition.y, 10,
					worldHeight)) {
				focusSide = Side.RIGHT;
				focus = true;
				focused = true;
			}

			// Side Left
			else if (hover(worldPosition.x - worldWidth / 2, worldPosition.y, 10,
					worldHeight)) {
				focusSide = Side.LEFT;
				focus = true;
				focused = true;
			} else {
				focused = false;
			}
		}

		// Hover Element
		if (!applet.mousePressed) {

			// Side Up
			if (hover(worldPosition.x, worldPosition.y - worldHeight / 2, worldWidth, 10)) {
				hover = true;
				hoverSide = Side.UP;
			}

			// Side Down
			else if (hover(worldPosition.x, worldPosition.y + worldHeight / 2, worldWidth,
					10)) {
				hover = true;
				hoverSide = Side.DOWN;
			}

			// Side right
			else if (hover(worldPosition.x + worldWidth / 2, worldPosition.y, 10,
					worldHeight)) {
				hover = true;
				hoverSide = Side.RIGHT;
			}

			// Side Left
			else if (hover(worldPosition.x - worldWidth / 2, worldPosition.y, 10,
					worldHeight)) {
				hover = true;
				hoverSide = Side.LEFT;
			} else {
				hover = false;
			}
		} else {
			hover = false;
		}

		// Blur Element
		if (applet.mouseReleaseEvent) {
			focus = false;
			focusSide = Side.NONE;
		}

		// Active Element Actions
		if (focus) {
			switch (focusSide) {
				case UP :
					sideUp = applet.getMouseY();
					break;
				case DOWN :
					sideDown = applet.getMouseY();
					break;
				case LEFT :
					sideLeft = applet.getMouseX();
					break;
				case RIGHT :
					sideRight = applet.getMouseX();
					break;
				default :
					break;
			}

			float h = (sideDown - sideUp);
			float w = (sideRight - sideLeft);
			float y = sideUp + h / 2;
			float x = sideLeft + w / 2;

			int pixelOffsetX = 0;
			int pixelOffsetY = 0;

			if ((PApplet.round((h) / 64) * 64) / 64 % 2 == 0) {
				pixelOffsetY = 32;
			}
			if ((PApplet.round((w) / 64) * 64) / 64 % 2 == 0) {
				pixelOffsetX = 32;
			}

			worldWidth = PApplet.round((w) / 64) * 64; // (int)w
			worldHeight = PApplet.round((h) / 64) * 64;

			worldPosition.x = PApplet.round((x) / 64) * 64 + pixelOffsetX;
			worldPosition.y = PApplet.round((y) / 64) * 64 + pixelOffsetY;
		}

	}

	public void displayEditor() {

		applet.noStroke();
		applet.fill(200, 100);

//		// TOP todo uncomment+fix or discard
//		applet.quad(0, 0, 0, worldPosition.y - worldHeight / 2, applet.width,
//				worldPosition.y - worldHeight / 2, applet.width, 0);
//
//		// BOTTOM
//		applet.quad(0, applet.height, 0, worldPosition.y + worldHeight / 2, applet.width,
//				worldPosition.y + worldHeight / 2, applet.width, applet.height);
//
//		// RIGHT
//		applet.quad(worldPosition.x + worldWidth / 2, worldPosition.y - worldHeight / 2,
//				applet.width, worldPosition.y - worldHeight / 2, applet.width,
//				worldPosition.y + worldHeight / 2, worldPosition.x + worldWidth / 2,
//				worldPosition.y + worldHeight / 2);
//
//		// LEFT
//		applet.quad(worldPosition.x - worldWidth / 2, worldPosition.y - worldHeight / 2, 0,
//				worldPosition.y - worldHeight / 2, 0, worldPosition.y + worldHeight / 2,
//				worldPosition.x - worldWidth / 2, worldPosition.y + worldHeight / 2);

		applet.noFill();
		applet.strokeWeight(1);

		if (focused) {
			applet.stroke(0, 255, 200);
		} else {
			applet.stroke(200, 200, 200);
		}

		applet.rect(worldPosition.x, worldPosition.y, worldWidth, worldHeight);

		if (focus || hover) {
			Side side = focus ? focusSide : hoverSide;

			switch (side) {
				case UP :
					if (hover) {
						applet.stroke(56, 255, 228);
					} else {
						applet.stroke(0, 246, 255);
					}

					// Line
					applet.line(worldPosition.x - worldWidth / 2,
							worldPosition.y - worldHeight / 2,
							worldPosition.x + worldWidth / 2,
							worldPosition.y - worldHeight / 2);
					// Arrow
					applet.line(worldPosition.x, worldPosition.y - worldHeight / 2,
							worldPosition.x, worldPosition.y - worldHeight / 2 - 50);
					applet.line(worldPosition.x, worldPosition.y - worldHeight / 2 - 50,
							worldPosition.x - 20, worldPosition.y - worldHeight / 2 - 50 + 20);
					applet.line(worldPosition.x, worldPosition.y - worldHeight / 2 - 50,
							worldPosition.x + 20, worldPosition.y - worldHeight / 2 - 50 + 20);
					break;

				case DOWN :
					if (hover) {
						applet.stroke(56, 255, 228);
					} else {
						applet.stroke(0, 246, 255);
					}

					// Line
					applet.line(worldPosition.x - worldWidth / 2,
							worldPosition.y + worldHeight / 2,
							worldPosition.x + worldWidth / 2,
							worldPosition.y + worldHeight / 2);

					// Arrow
					applet.line(worldPosition.x, worldPosition.y + worldHeight / 2,
							worldPosition.x, worldPosition.y + worldHeight / 2 + 50);
					applet.line(worldPosition.x, worldPosition.y + worldHeight / 2 + 50,
							worldPosition.x - 20, worldPosition.y + worldHeight / 2 + 50 - 20);
					applet.line(worldPosition.x, worldPosition.y + worldHeight / 2 + 50,
							worldPosition.x + 20, worldPosition.y + worldHeight / 2 + 50 - 20);
					break;

				case LEFT :
					if (hover) {
						applet.stroke(56, 255, 228);
					} else {
						applet.stroke(0, 246, 255);
					}

					// Line
					applet.line(worldPosition.x - worldWidth / 2,
							worldPosition.y - worldHeight / 2,
							worldPosition.x - worldWidth / 2,
							worldPosition.y + worldHeight / 2);

					// Arrow
					applet.line(worldPosition.x - worldWidth / 2, worldPosition.y,
							worldPosition.x - worldWidth / 2 - 50, worldPosition.y);
					applet.line(worldPosition.x - worldWidth / 2 - 50, worldPosition.y,
							worldPosition.x - worldWidth / 2 - 50 + 20, worldPosition.y - 20);
					applet.line(worldPosition.x - worldWidth / 2 - 50, worldPosition.y,
							worldPosition.x - worldWidth / 2 - 50 + 20, worldPosition.y + 20);
					break;
				case RIGHT :
					if (hover) {
						applet.stroke(56, 255, 228);
					} else {
						applet.stroke(0, 246, 255);
					}

					// Line
					applet.line(worldPosition.x + worldWidth / 2,
							worldPosition.y - worldHeight / 2,
							worldPosition.x + worldWidth / 2,
							worldPosition.y + worldHeight / 2);

					// Arrow
					applet.line(worldPosition.x + worldWidth / 2, worldPosition.y,
							worldPosition.x + worldWidth / 2 + 50, worldPosition.y);
					applet.line(worldPosition.x + worldWidth / 2 + 50, worldPosition.y,
							worldPosition.x + worldWidth / 2 + 50 - 20, worldPosition.y - 20);
					applet.line(worldPosition.x + worldWidth / 2 + 50, worldPosition.y,
							worldPosition.x + worldWidth / 2 + 50 - 20, worldPosition.y + 20);
					break;
				default :
					break;
			}
		}
	}

	public void setSize() {
		sideRight = worldPosition.x + worldWidth / 2;
		sideLeft = worldPosition.x - worldWidth / 2;
		sideUp = worldPosition.y - worldHeight / 2;
		sideDown = worldPosition.y + worldHeight / 2;
	}

	private boolean hover(float x, float y, float w, float h) {
		return (applet.getMouseX() > x - w / 2 && applet.getMouseX() < x + w / 2 && applet.getMouseY() > y - h / 2
				&& applet.getMouseY() < y + h / 2);
	}
}
