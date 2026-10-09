package project_16x16.ui;

import static net.jafama.FastMath.*;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import net.jafama.DoubleWrapper;
import processing.core.PApplet;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.Time;

/**
 * The animated flow-field particles drawn behind the menus. Particles stream
 * along one of several vector fields, which morph into one another every few
 * seconds. A few purple particles flicker among the cyan ones. In the main menu
 * the cursor gently stirs the particles.
 * <p>
 * The particles are drawn on their own JavaFX canvas, layered behind the game's
 * canvas (which menus clear to transparent rather than paint over). Trails are
 * made by not clearing the particle canvas: each frame it is faded towards the
 * background colour. The layer's opacity and blur are applied by JavaFX when it
 * composites the layers (on the GPU), so particles are faded as a whole --
 * overlapping strokes don't build up -- and the background colour shows exactly
 * where there are none.
 * <p>
 * The field is shared by all menus, so it flows on uninterrupted between them;
 * behind other menus it is drawn defocused (blurred and dimmer) -- see
 * {@link #draw(boolean)}.
 * <p>
 * Flow fields adapted from https://www.openprocessing.org/sketch/762850
 *
 * @author micycle1
 */
public final class MenuBackground {

	/** Particle counts for each density setting ({@link Options#menuParticles}). */
	private static final int[] DENSITY_COUNTS = { 500, 1100, 1800 };
	public static final String[] DENSITY_NAMES = { "Low", "Medium", "High" };

	private static final Color BACKGROUND = Color.rgb(29, 33, 45);

	/**
	 * How quickly particle trails fade into the background (per second); see
	 * {@link Time#smoothing(float)}.
	 */
	private static final float TRAIL_FADE_RATE = 5f;
	/**
	 * Fading by tiny per-frame amounts at high frame rates leaves ghost trails
	 * (8-bit colour can't represent them), so fade at most this often (seconds).
	 */
	private static final float TRAIL_FADE_INTERVAL = 1 / 30f;

	private static final int FUNCTIONS = 12;
	/** Time spent on each flow field before morphing into the next (seconds). */
	private static final float HOLD_SECONDS = 5;
	/** Duration of the morph between consecutive flow fields (seconds). */
	private static final float MORPH_SECONDS = 1.6f;
	private static final float FLOW_SPEED = 3; // flow-field units per second

	/** Defocus transition rate (per second). */
	private static final float FOCUS_RATE = 5;
	/** Particle layer opacity in the main menu. */
	private static final float SHARP_ALPHA = 0.8f;
	/** Particle layer opacity when fully defocused. */
	private static final float DEFOCUS_ALPHA = 0.5f;
	/** Stroke weight multiplier when fully defocused. */
	private static final float DEFOCUS_WEIGHT = 1.8f;
	/** Blur radius when fully defocused (game-resolution px). */
	private static final float DEFOCUS_BLUR = 7;

	/** Cursor stirring: radius (px) and peak speed (px/s). Kept subtle. */
	private static final float STIR_RADIUS = 110;
	private static final float STIR_SPEED = 160;

	/** Fraction of particles that are purple (and flicker). */
	private static final float PURPLE_CHANCE = 0.08f;

	private static SideScroller game;
	private static final List<Particle> particles = new ArrayList<>();
	private static int targetCount;

	/** The particle layer: a canvas over the background colour. */
	private static StackPane layer;
	private static Canvas canvas;
	private static final GaussianBlur blur = new GaussianBlur(0);

	private static int function = 0;
	private static float phaseTime = 0; // seconds since the current field started
	private static float morph = 0; // eased progress of the morph into the next field [0, 1]
	private static float focus = 1;
	private static float trailFadePending = 0; // seconds of fade not yet applied
	private static int lastFrame = -1;

	private static float centerX, centerY;
	private static float scaleX, scaleY;

	// reused between calls to avoid allocation (single render thread)
	private static final double[] SLOPES = new double[2];
	private static final DoubleWrapper COS_RESULT = new DoubleWrapper();

	private MenuBackground() {
	}

	public static void assignApplet(SideScroller s) {
		game = s;
		centerX = game.gameResolution.x / 2;
		centerY = game.gameResolution.y / 2;
		scaleX = game.gameResolution.x / 20;
		scaleY = game.gameResolution.y / 20 * (game.gameResolution.x / game.gameResolution.y);
		setDensity(Options.menuParticles);

		canvas = new Canvas();
		layer = new StackPane(canvas);
		layer.setBackground(new Background(new BackgroundFill(BACKGROUND, null, null)));
		layer.setMouseTransparent(true);
		layer.setVisible(false);
		game.addUnderlay(layer);
	}

	/**
	 * @param level 0 = low, 1 = medium, 2 = high (see {@link #DENSITY_NAMES})
	 */
	public static void setDensity(int level) {
		targetCount = DENSITY_COUNTS[Math.max(0, Math.min(DENSITY_COUNTS.length - 1, level))];
		while (particles.size() > targetCount) {
			particles.remove(particles.size() - 1);
		}
		while (particles.size() < targetCount) {
			particles.add(new Particle());
		}
	}

	/**
	 * Advances and draws the particles, and clears the game canvas so they show
	 * through it. Call at the start of a menu's {@code drawUI()}, before drawing
	 * anything else; extra calls in the same frame are ignored.
	 *
	 * @param sharp true for the main menu (crisp, bright and gently stirred by
	 *              the cursor); false to draw the field defocused behind another menu
	 */
	public static void draw(boolean sharp) {
		if (game.frameCount == lastFrame) {
			return;
		}
		lastFrame = game.frameCount;
		game.clearCanvas();
		layer.setVisible(true);

		final GraphicsContext gc = canvas.getGraphicsContext2D();
		if (canvas.getWidth() != game.g.width || canvas.getHeight() != game.g.height) {
			canvas.setWidth(game.g.width); // match the window's resolution
			canvas.setHeight(game.g.height);
			gc.setFill(BACKGROUND);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}

		final float dt = Time.delta();
		focus = Time.damp(focus, sharp ? 1 : 0, FOCUS_RATE);
		fadeTrails(gc, dt);
		advanceFlow(dt);

		final float s = game.getRenderScale();
		gc.setTransform(s, 0, 0, s, game.getRenderOffsetX(), game.getRenderOffsetY()); // game-resolution units
		gc.setLineCap(StrokeLineCap.ROUND);
		final float weightScale = (float) PApplet.lerp(DEFOCUS_WEIGHT, 1, focus);
		final float stir = game.focused ? focus : 0;

		for (Particle p : particles) {
			p.update(dt, stir);
			final float x = toScreenX(p.x);
			final float y = toScreenY(p.y);
			if (p.age > p.lifespan || !(x > -100 && x < game.gameResolution.x + 100 && y > -100 && y < game.gameResolution.y + 100)) {
				p.spawn(); // also catches NaN positions, which some fields produce
				continue;
			}

			float life = Math.min(1, Math.min(p.age / 0.5f, (p.lifespan - p.age) / 0.8f));
			if (p.flickerRate > 0) { // brief bright flashes
				final float wave = 0.5f + 0.5f * (float) sin(p.age * p.flickerRate + p.flickerPhase);
				life *= 0.2f + 0.8f * wave * wave * wave;
			}
			gc.setGlobalAlpha(life);
			gc.setStroke(p.color);
			gc.setLineWidth(p.size * weightScale);
			gc.strokeLine(p.lastX, p.lastY, x, y);

			p.lastX = x;
			p.lastY = y;
		}
		gc.setGlobalAlpha(1);

		// composited by JavaFX
		canvas.setOpacity(PApplet.lerp(DEFOCUS_ALPHA, SHARP_ALPHA, focus));
		final double radius = (1 - focus) * DEFOCUS_BLUR * s;
		if (radius < 0.5) {
			canvas.setEffect(null);
		} else {
			blur.setRadius(Math.min(63, radius));
			canvas.setEffect(blur);
		}
	}

	/**
	 * Hides the particle layer if it wasn't drawn this frame. Call at the end of
	 * every frame.
	 */
	public static void endFrame() {
		if (layer != null && lastFrame != game.frameCount) {
			layer.setVisible(false);
		}
	}

	/** Fades what was drawn previously towards the background colour. */
	private static void fadeTrails(GraphicsContext gc, float dt) {
		trailFadePending += dt;
		if (trailFadePending >= TRAIL_FADE_INTERVAL) {
			gc.setTransform(1, 0, 0, 1, 0, 0);
			gc.setGlobalAlpha(1 - Math.exp(-TRAIL_FADE_RATE * trailFadePending));
			gc.setFill(BACKGROUND);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.setGlobalAlpha(1);
			trailFadePending = 0;
		}
	}

	/** Steps the flow-field cycle. */
	private static void advanceFlow(float dt) {
		phaseTime += dt;
		morph = 0;
		if (phaseTime > HOLD_SECONDS) {
			final float m = Math.min(1, (phaseTime - HOLD_SECONDS) / MORPH_SECONDS);
			morph = m * m * (3 - 2 * m); // smoothstep
			if (m >= 1) {
				function = nextFunction();
				phaseTime = 0;
				morph = 0;
			}
		}

	}

	private static int nextFunction() {
		return (function + 1) % FUNCTIONS;
	}

	/**
	 * Computes the flow-field slope at (x, y) for the given function.
	 *
	 * @param out       out[0] = slope X, out[1] = slope Y
	 * @param cosResult reusable holder for sinAndCos()
	 */
	private static void getSlopes(int function, float x, float y, double[] out, DoubleWrapper cosResult) {
		double sx = 1;
		double sy = 1;

		switch (function) {
			case 0 -> {
				sx = cos(y);
				sy = sin(x);
			}
			case 1 -> {
				sx = cos(y * 5) * x * 0.3;
				sy = sin(x * 5) * y * 0.3;
			}
			case 2 -> sy = cos(x * y);
			case 3 -> sy = sin(x) * cos(y);
			case 4 -> sy = cos(x) * y * y;
			case 5 -> sy = log(abs(x)) * log(abs(y));
			case 6 -> sy = tan(x) * cos(y * y);
			case 7 -> { // orbit
				sx = sin(y * 0.1) * 3;
				sy = -sin(x * 0.1) * 3;
			}
			case 8 -> { // two orbits
				sx = y / 3;
				sy = (x - x * x * x) * 0.01;
			}
			case 9 -> {
				sx = -y;
				sy = -sin(x);
			}
			case 10 -> {
				sx = -1.5 * y;
				sy = -y - sin(1.5 * x) + 0.75;
			}
			case 11 -> {
				double sinX = sinAndCos(x, cosResult);
				double cosX = cosResult.value;

				double sinY = sinAndCos(y, cosResult);
				double cosY = cosResult.value;

				sx = sinY * cosX;
				sy = sinX * cosY;
			}
			default -> {
			}
		}

		out[0] = sx;
		out[1] = sy;
	}

	private static float toFieldX(float x) {
		return (x - centerX) / scaleX;
	}

	private static float toFieldY(float y) {
		return (y - centerY) / scaleY;
	}

	private static float toScreenX(float x) {
		return scaleX * x + centerX;
	}

	private static float toScreenY(float y) {
		return scaleY * y + centerY;
	}

	private static final class Particle {

		/** Position, in flow-field units. */
		private float x, y;
		/** Last drawn position, in screen units. */
		private float lastX, lastY;
		private int size;
		/** Signed speed multiplier: some particles run against the flow. */
		private float direction;
		private Color color;
		/** Flicker speed (radians/s); 0 for particles that don't flicker. */
		private float flickerRate, flickerPhase;
		private float age, lifespan; // seconds

		Particle() {
			spawn();
		}

		/** (Re)starts this particle at a random position. */
		void spawn() {
			x = toFieldX(game.random(0, game.gameResolution.x));
			y = toFieldY(game.random(0, game.gameResolution.y));
			lastX = toScreenX(x);
			lastY = toScreenY(y);
			size = (int) game.random(2, 7);
			direction = game.random(0.1f, 1) * (game.random(1) > 0.5f ? 1 : -1);
			if (game.random(1) < PURPLE_CHANCE) {
				color = Color.rgb((int) game.random(150, 210), (int) game.random(50, 110), (int) game.random(220, 255));
				flickerRate = game.random(5, 12);
				flickerPhase = game.random(PApplet.TWO_PI);
			} else {
				color = Color.rgb((int) game.random(0, 50), (int) game.random(150, 255), (int) game.random(150, 255));
				flickerRate = 0;
			}
			age = 0;
			lifespan = game.random(3, 10);
		}

		/**
		 * @param stir strength of the cursor's influence [0, 1]
		 */
		void update(float dt, float stir) {
			age += dt;
			getSlopes(function, x, y, SLOPES, COS_RESULT);
			double sx = SLOPES[0];
			double sy = SLOPES[1];
			if (morph > 0) {
				getSlopes(nextFunction(), x, y, SLOPES, COS_RESULT);
				sx += (SLOPES[0] - sx) * morph;
				sy += (SLOPES[1] - sy) * morph;
			}
			final float step = direction * FLOW_SPEED * dt;
			x += (float) sx * step;
			y += (float) sy * step;

			if (stir > 0) { // a gentle swirl around the cursor
				final float dx = toScreenX(x) - game.mouseX;
				final float dy = toScreenY(y) - game.mouseY;
				final float d2 = dx * dx + dy * dy;
				if (d2 < STIR_RADIUS * STIR_RADIUS && d2 > 1) {
					final float d = (float) sqrt(d2);
					final float falloff = 1 - d / STIR_RADIUS;
					final float push = STIR_SPEED * stir * falloff * falloff * dt / d;
					x += (-dy + 0.2f * dx) * push / scaleX;
					y += (dx + 0.2f * dy) * push / scaleY;
				}
			}
		}
	}
}
