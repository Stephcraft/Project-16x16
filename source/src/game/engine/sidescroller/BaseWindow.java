package game.engine.sidescroller;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCombination;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import processing.core.PApplet;
import processing.core.PSurface;
import processing.core.PVector;
import processing.javafx.PSurfaceFX;
import game.state.GameState;


// this class is for infomation about the base window
public abstract class BaseWindow extends PApplet {
	// Expose JavaFX nodes
	/**
	 * Processing's JavaFX surface. Extends and wraps a JavaFX {@link #canvas}.
	 */
	private PSurfaceFX surface;
	/**
	 * JavaFX Canvas - an image that can be drawn on using a set of graphics
	 * commands. A node of the {@link #scene}.
	 */
	private Canvas canvas;
	/**
	 * JavaFX Scene. Embedded in the {@link #stage} - the container for all other
	 * content (JavaFX nodes).
	 */
	protected Scene scene;
	/**
	 * JavaFX Stage - the top level JavaFX container (titlebar, etc.).
	 */
	protected Stage stage;

	/**
	 * controls how processing handles the window
	 */

	@Override
	public void settings() {
		PVector windowSize = GameState.getInstance().getWindowSize();
		size((int) windowSize.x, (int) windowSize.y, FX2D);
	}

	/**
	 * Called by Processing after game.state().
	 */
	@Override
	protected PSurface initSurface() {
		surface = (PSurfaceFX) super.initSurface();
		canvas = (Canvas) surface.getNative();
		canvas.widthProperty().unbind(); // used for scaling
		canvas.heightProperty().unbind(); // used for scaling
		scene = canvas.getScene();
		stage = (Stage) canvas.getScene().getWindow();
		stage.setTitle("Project-16x16");
		stage.setResizable(false); // prevent abitrary user resize
		stage.setFullScreenExitHint(""); // disable fullscreen toggle hint
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // prevent ESC toggling fullscreen
		return surface;
	}

	/**
	 * The default {@link #noSmooth()} does not work in FX2D mode - we override the
	 * default function with a working technique. Cannot be placed in
	 * {@link #settings()}, like it normally would be.
	 */
	@Override
	public void noSmooth() {
		try {
			canvas.getGraphicsContext2D().setImageSmoothing(false);
		} catch (java.lang.NoSuchMethodError e) {
		}
	}

	/**
	 * Scales the game rendering (as defined by gameResolution) to fill the current
	 * stage size. <b>Should be called whenever stage size or game resolution is
	 * changed</b> - currently called only when toggling fullscreen mode.
	 */
	protected void scaleResolution() {
		PVector gameResolution = GameState.getInstance().getGameResolution();
		canvas.getTransforms().clear();
		canvas.setTranslateX(-scene.getWidth() / 2 + gameResolution.x / 2); // recenters after scale
		canvas.setTranslateY(-scene.getHeight() / 2 + gameResolution.y / 2); // recenters after scale
		if (!(scene.getWidth() == gameResolution.x && scene.getHeight() == gameResolution.y)) {
			canvas.setWidth(gameResolution.x);
			canvas.setHeight(gameResolution.y);
			width = (int) gameResolution.x;
			height = (int) gameResolution.y;
			final double scaleX = scene.getWidth() / gameResolution.x;
			final double scaleY = scene.getHeight() / gameResolution.y;
			canvas.getTransforms().setAll(new Scale(scaleX, scaleY)); // scale canvas
		}
	}
}
