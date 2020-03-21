package project_16x16.ui;

import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.CENTER;

import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import project_16x16.SideScroller;
import project_16x16.Utility;

/**
 * Handles both drawing and logic of notifications.
 * 
 * @author micycle1
 */
public class Notifications {

	private static final LinkedList<Notifications> notifications = new LinkedList<>();
	private static PImage background;
	private static PVector positionTarget;

	private static final int notificationWidth = 275, notificationHeight = 125, notificationTextPadding = 10,
			notificationLifetime = 240, notificationLifetimeFast = 150, notificationLifetimeVeryFast = 60;

	private final PVector position = new PVector(game.width - notificationWidth, game.height);
	private final String title, message;
	private int lifetime, startTime, alpha = 255;

	private static SideScroller game;

	public static void assignApplet(SideScroller s) {
		game = s;
		positionTarget = new PVector(game.gameResolution.x - notificationWidth,
				game.gameResolution.y - notificationHeight);
		background = game.createImage(notificationWidth, notificationHeight, PApplet.ARGB);
		for (int i = 0; i < background.pixels.length; i++) {
			float a = PApplet.map(i, 0, background.pixels.length, 255, 0);
			background.pixels[i] = Utility.colorToRGB(50, 100, 150, a);
		}
	}

	/**
	 * Private method; creates GUI notifications internally.
	 * <p>
	 * Call the static method {@link #addNotification(String, String)
	 * addNotification} to create notifications.
	 * 
	 * @param title
	 * @param message
	 */
	private Notifications(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public static void clear() {
		notifications.clear();
	}

	public static void addNotification(String title, String message) {
		notifications.add(new Notifications(title, message));
	}

	/**
	 * Call this within game loop.
	 */
	public static void run() {
		if (!(notifications.isEmpty())) {
			notifications.getFirst().draw();
		}
	}

	public static void stageResized() {
		positionTarget = new PVector(game.width - notificationWidth, game.height - notificationHeight);
	}

	/**
	 * Called on one notification
	 */
	private void draw() {
		if (startTime == 0) {
			game.tint(255, 255);
			startTime = game.frameCount;
			if (notifications.size() > 2) {
				if (notifications.size() < 6) {
					lifetime = notificationLifetimeFast;
				} else {
					lifetime = notificationLifetimeVeryFast;
				}
			} else {
				lifetime = notificationLifetime;
			}
		}
		if (position.y > positionTarget.y) {
			position.y -= 10;
		}
		if ((game.frameCount - startTime) >= lifetime) {
			game.tint(255, alpha);
			alpha -= 10;
			if (alpha < 0) {
				notifications.removeFirst();
			}
		}
		
		game.imageMode(CORNER);
		game.rectMode(CORNER);
		game.textAlign(PApplet.LEFT, PApplet.TOP);
		
		game.fill(255, 255, 255, alpha); // white title
		game.image(background, position.x, position.y);
		game.textSize(18);
		// @formatter:off
		game.text("Notification: " + title,
				position.x + notificationTextPadding,
				position.y + notificationTextPadding,
				notificationWidth - notificationTextPadding, 
				notificationHeight - notificationTextPadding);
		game.textSize(15);
		game.textLeading(14);
		game.fill(255, 255, 255, alpha); // black message
		game.text(message,
				position.x + notificationTextPadding,
				position.y + notificationTextPadding + 32,
				notificationWidth - notificationTextPadding, 
				notificationHeight - notificationTextPadding);
		 // @formatter:on
		game.noTint();
		game.imageMode(CENTER);
	}
}