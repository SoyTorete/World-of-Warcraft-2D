package wow.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;

/**
 * A basic notification.
 * @author Xolitude
 * @since November 30, 2018
 */
public class GuiNotificationBasic implements GuiInterface {
	
	private String str;
	private Rectangle2D.Double box;
	
	private int x, y;
	
	public GuiNotificationBasic(String str) {
		this.str = str;
		this.box = new Rectangle2D.Double(0, 0, 450, 120);
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(14f));
		
		graphics.setColor(new Color(0, 0, 0, 235));
		graphics.fill(box);
		graphics.setColor(Color.gray);
		graphics.draw(box);
		
		graphics.setColor(new Color(223, 195, 15));
		GraphicsManager.drawCenteredString(str, x, y, getWidth(), getHeight(), graphics);
	}

	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		box.x = x;
		box.y = y;
	}
	
	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return (int)box.getWidth();
	}

	@Override
	public int getHeight() {
		return (int)box.getHeight();
	}
}
