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
		image = applet.tileset.getTileGraphic("MAGIC_SOURCE", 4);

		// Setup Animation
		animation.frames = applet.tileset.getAnimationGraphic("MAGIC::IDLE", 4);
		animation.loop = true;
		animation.length = 9;
		animation.rate = 6;
		animation.frame = 0;
		animation.start = 0;

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
		image = animation.animate(applet.frameCount, applet.deltaTime);

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
