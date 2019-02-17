package wow.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;

/**
 * A confirmation-notification.
 * @author Xolitude
 * @since November 30, 2018
 */
public class GuiNotificationConfirmation implements GuiInterface {

	private String str;
	private Rectangle2D.Double box;
	private GuiButton button;
	
	private int x, y;
	
	public GuiNotificationConfirmation(String str) {
		this.str = str;
		this.box = new Rectangle2D.Double(0, 0, 450, 120);
		this.button = new GuiButton("Okay");
	}
	
	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		button.tick(engine, display, delta);
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(14f));
		graphics.setColor(new Color(0, 0, 0, 235));
		graphics.fill(box);
		graphics.setColor(Color.gray);
		graphics.draw(box);
		
		graphics.setColor(new Color(223, 195, 15));
		if (str.contains("\n")) {
			String[] strSplit = str.split("\n");
			for (int i = 0; i < strSplit.length; i++) {
				String s = strSplit[i];
				if (i == 0)
					GraphicsManager.drawCenteredString(s, x, y - graphics.getFontMetrics().getAscent(), getWidth(), getHeight(), graphics);
				else
					GraphicsManager.drawCenteredString(s, x, y + (i * graphics.getFontMetrics().getAscent()) - graphics.getFontMetrics().getAscent(), getWidth(), getHeight(), graphics);
			}
		} else
			GraphicsManager.drawCenteredString(str, x, y - graphics.getFontMetrics().getAscent(), getWidth(), getHeight(), graphics);
		
		button.render(engine, display, graphics);
	}

	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		box.x = x;
		box.y = y;
		button.setLocation((int)box.x + getWidth() / 2 - button.getWidth() / 2, (int)box.y + getHeight() - button.getHeight() - 12);
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
	
	/**
	 * Get the notification's button.
	 * @return button
	 */
	public GuiButton getButton() {
		return button;
	}
}
