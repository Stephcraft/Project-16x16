package project_16x16.entities;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import project_16x16.Audio;
import project_16x16.Options;
import project_16x16.SideScroller;
import project_16x16.SideScroller.debugType;
import project_16x16.Tileset;
import project_16x16.Utility;
import project_16x16.Audio.SFX;
import project_16x16.components.AnimationComponent;
import project_16x16.objects.CollidableObject;
import project_16x16.objects.EditableObject;
import project_16x16.projectiles.Swing;
import project_16x16.scene.GameplayScene;

public class CleanerRobot extends Enemy {
	
	/**
	 * <h1>Enemy type</h1>
	 * <p>
	 * This class handles a simple-minded enemy which just travels from point A
	 * to point B and vice-versa.
	 * </p>
	 */
	
	private PVector posA, posB;
	private PVector target;
	
	public CleanerRobot(SideScroller a, GameplayScene g) {
		super(a, g);
	}
	
	public CleanerRobot(SideScroller a, GameplayScene g, PVector x1, PVector x2) {
		this(a, g);
		posA = x1;
		posB = x2;
		target = posA;
	}
	
	public void update() {
		super.update();
		velocity.set(velocity.x, velocity.y + gravity);
		if(getDistance(target, pos) < 10) {
			if(target == posA) {
				target = posB;
			}else {
				target = posA;
			}
		}
		else if(pos.x > target.x) {
			velocity.x = -speedWalk;
			enemyState.facingDir = LEFT;
		} else if (pos.x < target.x) {
			velocity.x = speedWalk;
			enemyState.facingDir = RIGHT;
		}

	}
	
	private double getDistance(PVector A, PVector B) {
		return Math.sqrt(Math.pow(A.x-B.x, 2) + Math.pow(A.y-B.y, 2));
	}
}
