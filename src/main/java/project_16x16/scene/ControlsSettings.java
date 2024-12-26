package project_16x16.scene;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Audio;
import project_16x16.Constants;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;
import project_16x16.ui.Slider;

public final class ControlsSettings extends PScene {

	private SideScroller game;

	private Button quit;
	private Button apply;
	private Button changeJumpKey;
	private Button changeDashKey;
	private Button changeMoveLeftKey;
	private Button changeMoveRightKey;

	// Stores key configurations
	private int newJumpKey;
	private int newDashKey;
	private int newMoveLeftKey;
	private int newMoveRightKey;

	private int originalJumpKey;
	private int originalDashKey;
	private int originalMoveLeftKey;
	private int originalMoveRightKey;

	private Button activeButton;

	public ControlsSettings(SideScroller a) {
		super(a);
		game = a;

		apply = new Button(applet);
		apply.setText("Apply");
		apply.setPosition(a.width / 2, 500);

		quit = new Button(a);
		quit.setText("Quit");
		quit.setPosition(a.width / 2, 600);
		
		changeJumpKey = new Button(a);
		changeJumpKey.setText("Change Jump Key: " + PApplet.str(Options.jumpKey));
		changeJumpKey.setPosition(a.width / 2, 150);

		changeDashKey = new Button(a);
		changeDashKey.setText("Change Dash Key: " + PApplet.str(Options.dashKey));
		changeDashKey.setPosition(a.width / 2, 200);

		changeMoveLeftKey = new Button(a);
		changeMoveLeftKey.setText("Change Move Left Key: " + PApplet.str(Options.moveLeftKey));
		changeMoveLeftKey.setPosition(a.width / 2, 250);

		changeMoveRightKey = new Button(a);
		changeMoveRightKey.setText("Change Move Right Key: " + PApplet.str(Options.moveRightKey));
		changeMoveRightKey.setPosition(a.width / 2, 300);

		newJumpKey = Options.jumpKey;
		newDashKey = Options.dashKey;
		newMoveLeftKey = Options.moveLeftKey;
		newMoveRightKey = Options.moveRightKey;

		activeButton = null;
	}

	@Override
	public void switchTo() {
		originalJumpKey = Options.jumpKey;
		originalDashKey = Options.dashKey;
		originalMoveLeftKey = Options.moveLeftKey;
		originalMoveRightKey = Options.moveRightKey;
		super.switchTo();
	}

	@Override
	public void drawUI() {
		displayWindow();
		apply.display();
		quit.display();

		changeJumpKey.display();
		changeDashKey.display();
		changeMoveLeftKey.display();
		changeMoveRightKey.display();

		if (activeButton != null) {
			game.fill(255, 0, 0);
			game.textAlign(PConstants.CENTER);
			game.text("Press a key to change the control!", game.width / 2, 100);
		}
	}
	
	private void displayWindow() {
		background(19, 23, 35);
		applet.fill(29, 33, 45);
		applet.stroke(47, 54, 73);
		applet.strokeWeight(8);
		applet.rect(applet.gameResolution.x / 2, applet.gameResolution.y / 2, applet.gameResolution.x * 0.66f - 8,
				applet.gameResolution.y - 8);
	}

	@Override
	void mouseReleased(MouseEvent e) {
		apply.update();
		quit.update();
		changeJumpKey.update();
		changeDashKey.update();
		changeMoveLeftKey.update();
		changeMoveRightKey.update();

		if (quit.hover()) {
			newJumpKey = originalJumpKey;
			newDashKey = originalDashKey;
			newMoveLeftKey = originalMoveLeftKey;
			newMoveRightKey = originalMoveRightKey;
			changeJumpKey.setText("Change Jump Key: " + PApplet.str(originalJumpKey));
			changeDashKey.setText("Change Dash Key: " + PApplet.str(originalDashKey));
			changeMoveLeftKey.setText("Change Move Left Key: " + PApplet.str(originalMoveLeftKey));
			changeMoveRightKey.setText("Change Move Right Key: " + PApplet.str(originalMoveRightKey));

			game.returnScene();
			return;
		}
		if (apply.hover()) {
			Options.save(Option.jumpKey, newJumpKey);
			Options.save(Option.dashKey, newDashKey);
			Options.save(Option.moveLeftKey, newMoveLeftKey);
			Options.save(Option.moveRightKey, newMoveRightKey);
			Options.jumpKey = newJumpKey;
			Options.dashKey = newDashKey;
			Options.moveLeftKey = newMoveLeftKey;
			Options.moveRightKey = newMoveRightKey;

			Notifications.addNotification("Controls Settings Applied", "Your configuration has been successfully applied.");
			game.returnScene();
		}

		if (changeJumpKey.hover()) {
			activeButton = changeJumpKey;
		} else if (changeDashKey.hover()) {
			activeButton = changeDashKey;
		} else if (changeMoveLeftKey.hover()) {
			activeButton = changeMoveLeftKey;
		} else if (changeMoveRightKey.hover()) {
			activeButton = changeMoveRightKey;
		} else {
			activeButton = null;
		}
	}

	@Override
	void keyReleased(KeyEvent e) {
		if (activeButton != null) {
			int key = e.getKeyCode();
			if (activeButton == changeJumpKey) {
				newJumpKey = key;
				changeJumpKey.setText("Change Jump Key: " + PApplet.str(key));
			} else if (activeButton == changeDashKey) {
				newDashKey = key;
				changeDashKey.setText("Change Dash Key: " + PApplet.str(key));
			} else if (activeButton == changeMoveLeftKey) {
				newMoveLeftKey = key;
				changeMoveLeftKey.setText("Change Move Left Key: " + PApplet.str(key));
			} else if (activeButton == changeMoveRightKey) {
				newMoveRightKey = key;
				changeMoveRightKey.setText("Change Move Right Key: " + PApplet.str(key));
			}
			activeButton = null;
		} else if (e.getKeyCode() == PConstants.ESC) {
			game.returnScene();
		}
	}
}
