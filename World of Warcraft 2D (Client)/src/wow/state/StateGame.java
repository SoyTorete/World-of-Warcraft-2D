package wow.state;

import java.awt.Graphics2D;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.WoWManager;
import wow.net.Player;

/**
 * The game state.
 * @author Xolitude
 * @since December 19, 2018
 */
public class StateGame implements IState {
	
	public static final int ID = 3;

	@Override
	public void init(DisplayManager display) {
		
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		WoWManager.Player.tick(engine, display, delta);
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		WoWManager.Player.render(engine, display, graphics);
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public void OnStateTransition() {		
	}
}
