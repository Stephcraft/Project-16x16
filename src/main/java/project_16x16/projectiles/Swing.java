package project_16x16.projectiles;

import project_16x16.components.AnimationComponent;
import processing.core.PVector;
import project_16x16.scene.GameplayScene;
import project_16x16.SideScroller;
import project_16x16.Tileset;

public class Swing extends ProjectileObject { // PClass

	private AnimationComponent animation;

	public boolean activated;

	public Swing(SideScroller sideScroller, GameplayScene gameplayScene, int x, int y, int dir) {
		super(sideScroller, gameplayScene);
		animation = new AnimationComponent();

		direction = dir;

		switch (direction) {
			case LEFT :
				position = new PVector(x - 60, y);
				break;
			case RIGHT :
				position = new PVector(x + 60, y);
				break;
		}

		width = 28 * 4;
		height = 9 * 4;

		// Setup Animation
		animation.changeAnimation(Tileset.getAnimation("Swing"), false, 4);
		update();
	}

	@Override
	public void display() {
		applet.pushMatrix();
		applet.translate(position.x, position.y);
		if (direction == LEFT) {
			applet.scale(-1, 1);
		}
		applet.image(image, 0, 0);
		applet.popMatrix();
	}

	@Override
	public void update() {
		image = animation.animate();

		if (animation.ended) {
			gameplayScene.getPlayer().swings.remove(this);
		}
	}
}
