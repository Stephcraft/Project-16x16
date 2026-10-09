package project_16x16.entities;

import processing.core.PVector;
import project_16x16.SideScroller;
import project_16x16.Time;
import project_16x16.scene.GameplayScene;

/**
 * <h1>Enemy type</h1>
 * <p>
 * This class handles a simple-minded enemy which just travels from point A to
 * point B and vice-versa.
 * </p>
 */
public class CleanerRobot extends Enemy {
	private PVector posA, posB;
	private PVector target;

	public CleanerRobot(SideScroller sideScroller, GameplayScene gameplayScene) {
		super(sideScroller, gameplayScene);
	}

	public CleanerRobot(SideScroller sideScroller, GameplayScene gameplayScene, PVector x1, PVector x2) {
		this(sideScroller, gameplayScene);
		posA = x1;
		posB = x2;
		target = posA;
	}

	@Override
	public void update() {
		super.update();

		// arrival radius covers a whole frame's movement, so low frame rates can't overshoot it
		if (getDistance(target, position) < Math.max(10, speedWalk * Time.delta())) {
			if (target == posA) {
				target = posB;
			} else {
				target = posA;
			}
		} else if (position.x > target.x) {
			velocity.x = -speedWalk;
			enemyState.facingDir = LEFT;
		} else if (position.x < target.x) {
			velocity.x = speedWalk;
			enemyState.facingDir = RIGHT;
		}
	}

	private double getDistance(PVector a, PVector b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

}
