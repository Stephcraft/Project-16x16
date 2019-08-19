package sidescroller;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @deprecated (seemingly)
 */
public class Local {

	public static PGraphics createEditorGrid(PApplet applet) {
		PGraphics pg = applet.createGraphics((int) (applet.width * 1.5), (int) (applet.height * 1.5));

		int w = (int) (applet.width * 1.5);
		int h = (int) (applet.height * 1.5);

		pg.beginDraw();
		pg.background(0);
		pg.noFill();
		pg.strokeWeight(1);
		pg.stroke(255);

		for (int x = 0; x < w / 2; x++) {
			for (int y = 0; y < h / 2; y++) {
			}
		}
		pg.endDraw();

		return pg;
	}
}
