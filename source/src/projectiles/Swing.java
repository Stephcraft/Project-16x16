package projectiles;

import components.AnimationComponent;
import processing.core.PVector;
import scene.GameplayScene;
import sidescroller.SideScroller;
import sidescroller.Tileset;

public class Swing extends ProjectileObject { // PClass

	private AnimationComponent animation;

	public boolean activated;

	public Swing(SideScroller a, GameplayScene g, int x, int y, int dir) {
		super(a, g);
		animation = new AnimationComponent();

		direction = dir;

		switch (direction) {
			case LEFT :
				pos = new PVector(x - 60, y);
				break;
			case RIGHT :
				pos = new PVector(x + 60, y);
				break;
		}

		width = 28 * 4;
		height = 9 * 4;

		// Setup Animation
		animation.changeAnimation(Tileset.getAnimation("Swing"), false, 4);
	}

	@Override
	public void display() {
		try {
			switch (direction) {
				case LEFT :
					applet.pushMatrix();
					applet.translate(pos.x, pos.y);
					applet.scale(-1, 1);
					applet.image(image, 0, 0);
					applet.popMatrix();
					break;
				case RIGHT :
					applet.image(image, pos.x, pos.y);
					break;
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void update() {
		image = animation.animate();

		if (animation.ended) {
			gameScene.getPlayer().swings.remove(this);
		}
	}
}
