package wow.state;

import java.awt.Graphics2D;

import wow.WoW;
import wow.manager.DisplayManager;

/**
 * An interface in which all game-states derive.
 * @author Xolitude
 * @since November 25, 2018
 */
public interface IState {

	/**
	 * Initialize all ui, variables, etc, here.
	 * @param display
	 */
	void init(DisplayManager display);
	
	/**
	 * Update (tick) state-specific data.
	 * @param engine
	 * @param display
	 * @param delta
	 */
	void tick(WoW engine, DisplayManager display, double delta);
	
	/**
	 * Render (draw) state-specific data.
	 * @param engine
	 * @param display
	 * @param graphics
	 */
	void render(WoW engine, DisplayManager display, Graphics2D graphics);
	
	/**
	 * Called whenever display.enterState(id) is used.
	 * @param engine
	 * @param display
	 */
	void OnStateTransition(DisplayManager display);
	
	/**
	 * 
	 * @return the state's unique-id.
	 */
	int getId();
}
