package project_16x16.scene;

import static project_16x16.Utility.charToStr;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.Notifications;

public final class ControlsSettings extends PScene {

	private SideScroller game;

	private Button quit;
	private Button apply;
	private Button reset;
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

		initButtons();

		newJumpKey = Options.jumpKey;
		newDashKey = Options.dashKey;
		newMoveLeftKey = Options.moveLeftKey;
		newMoveRightKey = Options.moveRightKey;
	}

	void initButtons() {
		apply = new Button(applet);
		apply.setText("Apply");
		apply.setPosition(applet.width / 2, 500);

		quit = new Button(applet);
		quit.setText("Quit");
		quit.setPosition(applet.width / 2, 600);

		changeJumpKey = new Button(applet);
		changeJumpKey.setText("Change Jump Key: " + charToStr(Options.jumpKey));
		changeJumpKey.setPosition(applet.width / 2, 150);

		changeDashKey = new Button(applet);
		changeDashKey.setText("Change Dash Key: " + charToStr(Options.dashKey));
		changeDashKey.setPosition(applet.width / 2, 200);

		changeMoveLeftKey = new Button(applet);
		changeMoveLeftKey.setText("Change Move Left Key: " + charToStr(Options.moveLeftKey));
		changeMoveLeftKey.setPosition(applet.width / 2, 250);

		changeMoveRightKey = new Button(applet);
		changeMoveRightKey.setText("Change Move Right Key: " + charToStr(Options.moveRightKey));
		changeMoveRightKey.setPosition(applet.width / 2, 300);

		reset = new Button(applet);
		reset.setText("Reset All");
		reset.setPosition(applet.width / 2, 400);

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
		reset.display();

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
		reset.update();
		changeJumpKey.update();
		changeDashKey.update();
		changeMoveLeftKey.update();
		changeMoveRightKey.update();

		if (quit.hover()) {
			newJumpKey = originalJumpKey;
			newDashKey = originalDashKey;
			newMoveLeftKey = originalMoveLeftKey;
			newMoveRightKey = originalMoveRightKey;
			changeJumpKey.setText("Change Jump Key: " + charToStr(originalJumpKey));
			changeDashKey.setText("Change Dash Key: " + charToStr(originalDashKey));
			changeMoveLeftKey.setText("Change Move Left Key: " + charToStr(originalMoveLeftKey));
			changeMoveRightKey.setText("Change Move Right Key: " + charToStr(originalMoveRightKey));

			game.returnScene();
			return;
		}
		if (apply.hover()) {
			Options.save(Option.JUMP_KEY, newJumpKey);
			Options.save(Option.DASH_KEY, newDashKey);
			Options.save(Option.MOVE_LEFT_KEY, newMoveLeftKey);
			Options.save(Option.MOVE_RIGHT_KEY, newMoveRightKey);
			Options.jumpKey = newJumpKey;
			Options.dashKey = newDashKey;
			Options.moveLeftKey = newMoveLeftKey;
			Options.moveRightKey = newMoveRightKey;

			Notifications.addNotification("Controls Settings Applied", "Your configuration has been successfully applied.");
			game.returnScene();
		}
		if (reset.hover()) {
			ConfirmationMenu confirmReset = new ConfirmationMenu(game, () -> {
				Options.save(Option.JUMP_KEY, Options.DefaultKeys.JUMP);
				Options.save(Option.DASH_KEY, Options.DefaultKeys.DASH);
				Options.save(Option.MOVE_LEFT_KEY, Options.DefaultKeys.MOVE_LEFT);
				Options.save(Option.MOVE_RIGHT_KEY, Options.DefaultKeys.MOVE_RIGHT);
				Options.jumpKey = Options.DefaultKeys.JUMP;
				Options.dashKey = Options.DefaultKeys.DASH;
				Options.moveLeftKey = Options.DefaultKeys.MOVE_LEFT;
				Options.moveRightKey = Options.DefaultKeys.MOVE_RIGHT;
				initButtons(); // remake buttons with new labels
				Notifications.addNotification("Control Settings Reset", "Reset all control settings to default");
			});
			var c = SideScroller.GameScenes.makeConfirmation(confirmReset);
			game.swapToScene(c);
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
				changeJumpKey.setText("Change Jump Key: " + charToStr(key));
			} else if (activeButton == changeDashKey) {
				newDashKey = key;
				changeDashKey.setText("Change Dash Key: " + charToStr(key));
			} else if (activeButton == changeMoveLeftKey) {
				newMoveLeftKey = key;
				changeMoveLeftKey.setText("Change Move Left Key: " + charToStr(key));
			} else if (activeButton == changeMoveRightKey) {
				newMoveRightKey = key;
				changeMoveRightKey.setText("Change Move Right Key: " + charToStr(key));
			}
			activeButton = null;
		} else if (e.getKeyCode() == PConstants.ESC) {
			game.returnScene();
		}
	}
}
