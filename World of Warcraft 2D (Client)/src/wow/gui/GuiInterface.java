package wow.gui;

import java.awt.Graphics2D;

import wow.WoW;
import wow.manager.DisplayManager;

/**
 * An interface which most gui's derive from.
 * @author Xolitude
 * @since November 26, 2018
 */
public interface GuiInterface {

	void tick(WoW engine, DisplayManager display, double delta);
	void render(WoW engine, DisplayManager display, Graphics2D graphics);
	void setLocation(int x, int y);
	int getX();
	int getY();
	int getWidth();
	int getHeight();
}
