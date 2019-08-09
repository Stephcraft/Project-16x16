package objects;

import projectiles.MagicProjectile;
import projectiles.Swing;
import sidescroller.SideScroller;

/**
 * Extends {@link GameObject}.
 */
public class MagicSourceObject extends GameObject {

	public MagicSourceObject(SideScroller a) {
		super(a);

		type = "OBJECT";
		id = "MAGIC_SOURCE";

		// Default image
		image = applet.gameGraphics.get("MAGIC_SOURCE");

		// Setup Animation		
		animation.changeAnimation(applet.gameGraphics.ga(applet.magicSheet, 0, 0, 16, 16, 80), true, 6);

		width = 48;
		height = 48;

		pos.y = -80;
	}

	@Override
	public void display() {
		applet.image(image, pos.x, pos.y);
	}

	@Override
	public void update() {
		image = animation.animate();

		// Create new Magic Projectiles
		for (int i = 0; i < applet.player.swings.size(); i++) {
			Swing swing = applet.player.swings.get(i);

			if (collidesWithSwing(swing)) {
				if (!swing.activated) {
					applet.projectileObjects
							.add(new MagicProjectile(applet, (int) pos.x, (int) pos.y, swing.direction));

					swing.activated = true;
				}
			}
		}
	}

	public boolean collidesWithSwing(Swing swing) {
		return (swing.pos.x + swing.width / 2 > pos.x - width / 2
				&& swing.pos.x - swing.width / 2 < pos.x + width / 2)
				&& (swing.pos.y + swing.height / 2 > pos.y - height / 2
						&& swing.pos.y - swing.height / 2 < pos.y + height / 2);
	}

	public boolean collidesWithPlayer() {
		return (applet.player.pos.x + applet.player.width / 2 > pos.x - width / 2
				&& applet.player.pos.x - applet.player.width / 2 < pos.x + width / 2)
				&& (applet.player.pos.y + applet.player.height / 2 > pos.y
						- height / 2
						&& applet.player.pos.y - applet.player.height / 2 < pos.y
								+ height / 2);
	}
}
