package project_16x16.scene.gameplaymodes;

import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import project_16x16.objects.EditorItem;
import project_16x16.scene.GameplayScene;
import project_16x16.scene.GameplayScene.GameModes;
import project_16x16.scene.TilePalette;

/**
 * Level editing with the tile palette open. The level can still be edited
 * around the palette.
 */
public class InventoryGameMode extends ModifyGameMode {

	private final TilePalette palette;

	public InventoryGameMode(GameplayScene gameplayScene, EditorItem editorItem) {
		super(gameplayScene, editorItem);
		palette = new TilePalette(gameplayScene.applet, editorItem, () -> gameplayScene.changeMode(GameModes.MODIFY));
	}

	@Override
	public GameModes getModeType() {
		return GameModes.INVENTORY;
	}

	@Override
	public boolean isOverUI(PVector screen) {
		return palette.contains(screen);
	}

	@Override
	public void updateGUIButton(int x, int y, PImage activeIcon, PImage inactiveIcon, GameModes mode, boolean isHighlighted) {
		if (mode == GameModes.INVENTORY && isHighlighted && scene.applet.mousePressEvent) {
			scene.changeMode(GameModes.MODIFY); // the palette's icon toggles it
		}
		super.updateGUIButton(x, y, activeIcon, inactiveIcon, mode, isHighlighted);
	}

	@Override
	public boolean dropTile(String tileName, PVector screen) {
		return palette.drop(tileName, screen);
	}

	@Override
	public void updateGUI() {
		palette.display();
		super.updateGUI(); // dragged item is drawn over the palette
	}

	@Override
	public void mouseWheelEvent(MouseEvent event) {
		if (palette.contains(scene.applet.getMouseCoordScreen())) {
			palette.mouseWheel(event);
		}
	}
}
