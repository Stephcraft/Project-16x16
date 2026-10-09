package project_16x16.ui;

import processing.core.PConstants;
import processing.core.PImage;
import project_16x16.SideScroller;

/**
 * Shared drawing helpers so that all menus look consistent.
 */
public final class MenuStyle {

	private static final int ACCENT = 0xFF7CC8FF;

	/** The (blurred) game frame shown behind menus opened from a paused game. */
	private static PImage gameBackdrop;

	private MenuStyle() {
	}

	/** Draws a large centred title with a drop shadow and accent underline. */
	public static void title(SideScroller a, String text, float y) {
		a.textAlign(PConstants.CENTER, PConstants.CENTER);
		a.textSize(80);
		a.fill(0, 120);
		a.text(text, a.width / 2f + 3, y + 4);
		a.fill(255);
		a.text(text, a.width / 2f, y);
		float w = Math.min(a.textWidth(text), 600);
		a.noStroke();
		a.fill(ACCENT);
		a.rectMode(PConstants.CENTER);
		a.rect(a.width / 2f, y + 56, w * 0.6f, 4, 2);
	}

	/** Draws a small centred caption. */
	public static void caption(SideScroller a, String text, float x, float y) {
		a.textAlign(PConstants.CENTER, PConstants.CENTER);
		a.textSize(24);
		a.fill(255, 150);
		a.text(text, x, y);
	}

	/** Darkens whatever is behind the menu. */
	public static void dim(SideScroller a, int alpha) {
		a.noStroke();
		a.fill(0, alpha);
		a.rectMode(PConstants.CORNER);
		a.rect(0, 0, a.width, a.height);
		a.rectMode(PConstants.CENTER);
	}

	/** Sets the game frame shown behind menus while a game is paused. */
	public static void setGameBackdrop(PImage frame) {
		gameBackdrop = frame;
	}

	/**
	 * Draws what goes behind a menu: the paused game (dimmed) while a game is in
	 * progress, otherwise the defocused menu particles.
	 */
	public static void backdrop(SideScroller a) {
		if (gameBackdrop != null && a.isGameInProgress()) {
			a.image(gameBackdrop, a.width / 2f, a.height / 2f, a.width, a.height);
			dim(a, 110);
		} else {
			MenuBackground.draw(false);
		}
	}

	/**
	 * Draws the framed panel used behind settings-style menus, with a title. The
	 * panel is translucent, so the {@link #backdrop(SideScroller) backdrop} shows
	 * through.
	 */
	public static void panel(SideScroller a, String title) {
		backdrop(a);
		a.rectMode(PConstants.CENTER);
		a.fill(29, 33, 45, 205);
		a.stroke(47, 54, 73);
		a.strokeWeight(8);
		a.rect(a.width / 2f, a.height / 2f, a.width * 0.66f - 8, a.height - 8, 12);
		title(a, title, 90);
	}
}
