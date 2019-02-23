package wow.state;

import java.awt.Graphics2D;
import java.io.File;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.WoWManager;
import wow.manager.WoWManager.RaceType;
import wow.net.Camera;
import wow.tiled.TiledMap;

/**
 * The game state.
 * @author Xolitude
 * @since December 19, 2018
 */
public class StateGame implements IState {
	
	public static final int ID = 4;
	
	private Camera camera;

	@Override
	public void init(DisplayManager display) {
		camera = new Camera();
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		WoWManager.Player.tick(engine, display, delta);
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		camera.translate(WoWManager.Player.getX(), WoWManager.Player.getY(), graphics);
		WoWManager.Map.level.render(graphics);
		WoWManager.Player.render(engine, display, graphics);
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public void OnStateTransition(DisplayManager display) {
		
	}
}
