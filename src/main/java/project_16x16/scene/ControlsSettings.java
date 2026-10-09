package project_16x16.scene;

import static project_16x16.Utility.charToStr;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import project_16x16.Options;
import project_16x16.Options.Option;
import project_16x16.SideScroller;
import project_16x16.ui.Button;
import project_16x16.ui.MenuNav;
import project_16x16.ui.MenuStyle;
import project_16x16.ui.Notifications;

public final class ControlsSettings extends PScene {

	private final SideScroller game;
	private final MenuNav nav;

	private Button changeJumpKey;
	private Button changeDashKey;
	private Button changeMoveLeftKey;
	private Button changeMoveRightKey;

	// Pending (not yet applied) key configuration
	private int newJumpKey;
	private int newDashKey;
	private int newMoveLeftKey;
	private int newMoveRightKey;

	/** The key button currently waiting for a key press, if any. */
	private Button activeButton;

	public ControlsSettings(SideScroller a) {
		super(a);
		game = a;

		final int cx = a.width / 2;
		final int top = 190;
		final int step = 62;
		nav = new MenuNav(a);
		changeJumpKey = nav.button("", cx, top, 520, 52, 28, () -> activeButton = changeJumpKey);
		changeDashKey = nav.button("", cx, top + step, 520, 52, 28, () -> activeButton = changeDashKey);
		changeMoveLeftKey = nav.button("", cx, top + 2 * step, 520, 52, 28, () -> activeButton = changeMoveLeftKey);
		changeMoveRightKey = nav.button("", cx, top + 3 * step, 520, 52, 28, () -> activeButton = changeMoveRightKey);
		nav.button("Reset All", cx, top + 4 * step + 15, 520, 52, 28, this::confirmReset);
		nav.button("Apply", cx, top + 5 * step + 15, 520, 52, 28, this::apply);
		nav.button("Back", cx, top + 6 * step + 15, 520, 52, 28, game::returnScene);
		loadFromOptions();
	}

	private void loadFromOptions() {
		newJumpKey = Options.jumpKey;
		newDashKey = Options.dashKey;
		newMoveLeftKey = Options.moveLeftKey;
		newMoveRightKey = Options.moveRightKey;
		activeButton = null;
		refreshLabels();
	}

	private void refreshLabels() {
		label(changeJumpKey, "Jump", newJumpKey);
		label(changeDashKey, "Dash", newDashKey);
		label(changeMoveLeftKey, "Move Left", newMoveLeftKey);
		label(changeMoveRightKey, "Move Right", newMoveRightKey);
	}

	private void label(Button b, String action, int key) {
		b.setText(action + ":  " + (b == activeButton ? "press a key..." : charToStr(key)));
	}

	private void apply() {
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

	private void confirmReset() {
		ConfirmationMenu confirmReset = new ConfirmationMenu(game, () -> {
			Options.save(Option.JUMP_KEY, Options.DefaultKeys.JUMP);
			Options.save(Option.DASH_KEY, Options.DefaultKeys.DASH);
			Options.save(Option.MOVE_LEFT_KEY, Options.DefaultKeys.MOVE_LEFT);
			Options.save(Option.MOVE_RIGHT_KEY, Options.DefaultKeys.MOVE_RIGHT);
			Options.jumpKey = Options.DefaultKeys.JUMP;
			Options.dashKey = Options.DefaultKeys.DASH;
			Options.moveLeftKey = Options.DefaultKeys.MOVE_LEFT;
			Options.moveRightKey = Options.DefaultKeys.MOVE_RIGHT;
			loadFromOptions();
			Notifications.addNotification("Control Settings Reset", "Reset all control settings to default");
		}, "Reset all controls?");
		game.swapToScene(SideScroller.GameScenes.makeConfirmation(confirmReset));
	}

	@Override
	public void switchTo() {
		loadFromOptions(); // discard any unapplied changes
		super.switchTo();
	}

	@Override
	public void drawUI() {
		MenuStyle.panel(game, "CONTROLS");
		refreshLabels();
		nav.display();
		if (activeButton != null) {
			MenuStyle.caption(game, "Press the new key (Esc to cancel)", game.width / 2f, 150);
		}
	}

	@Override
	void mouseReleased(MouseEvent e) {
		activeButton = null; // clicking elsewhere cancels; a key button click re-sets it below
		nav.mouseReleased();
	}

	@Override
	void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (activeButton != null) {
			if (key != PConstants.ESC) {
				if (activeButton == changeJumpKey) {
					newJumpKey = key;
				} else if (activeButton == changeDashKey) {
					newDashKey = key;
				} else if (activeButton == changeMoveLeftKey) {
					newMoveLeftKey = key;
				} else if (activeButton == changeMoveRightKey) {
					newMoveRightKey = key;
				}
			}
			activeButton = null;
			return;
		}
		if (nav.keyReleased(e)) {
			return;
		}
		if (key == PConstants.ESC) {
			game.returnScene();
		}
	}
}
