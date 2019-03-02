package wow.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.net.RealmCharacter;
import wow.net.connection.WorldConnection.World;
import wow.tiled.TiledMap;
import wow.tiled.TiledMap.ParseState;

/**
 * Loads the map/zone and awaits for the rest of the server packets.
 * @author Xolitude
 * @since February 22, 2019
 */
public class StateLoading implements IState {

	public static int ID = 3;
	
	private Rectangle progressBar;
	
	@Override
	public void init(DisplayManager display) {
		progressBar = new Rectangle(0, 0, 400, 25);
		progressBar.x = (int) (display.getWidth() / 2 - progressBar.getWidth() / 2);
		progressBar.y = (int) (display.getHeight() / 2 - progressBar.getHeight() / 2);
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		if (WoWManager.Map != null)
			WoWManager.Map.start();
		
		if (WoWManager.Map.state == ParseState.Finished) {
			if (NetworkManager.WORLD.STATUS == World.World) {
				if (WoWManager.Player.animationsInitialized()) {
					display.enterState(StateGame.ID);
				}
			}
		}
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setColor(Color.blue);
		graphics.fillRect(progressBar.x, progressBar.y, (int) (progressBar.width * WoWManager.Map.state.getPercent()), progressBar.height);
	}

	@Override
	public void OnStateTransition(DisplayManager display) {
		if (WoWManager.Map == null) {
			if (display.getActiveState() == this) {
				WoWManager.Map = new TiledMap(WoWManager.CharacterInUse.Zone.getFile());
			}
		}
	}

	@Override
	public int getId() {
		return ID;
	}
}
