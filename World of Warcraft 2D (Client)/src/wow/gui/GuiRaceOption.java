package wow.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.InputManager;
import wow.manager.WoWManager.RaceType;

/**
 * A class to handle the different race options.
 * @author Xolitude
 * @since December 5, 2018
 */
public class GuiRaceOption implements GuiInterface {
	
	private RaceType race;
	private BufferedImage icon;
	private RoundRectangle2D.Double iconBorder;
	
	private int x, y;
	
	private boolean isHovering = false;
	public boolean isSelected = false;
	private RoundRectangle2D.Double toolTip;
	
	public GuiRaceOption(RaceType race) {
		this.race = race;
		try {
			switch (race) {
			case Undead:
				icon = ImageIO.read(getClass().getResourceAsStream("/sprites/player/forsaken_race_img.png"));
				break;
			case Human:
				icon = ImageIO.read(getClass().getResourceAsStream("/sprites/player/human_race_img.png"));
				break;
			}
		} catch (IOException ex) {}
		iconBorder = new RoundRectangle2D.Double(0, 0, icon.getWidth() + 3, icon.getHeight() + 3, 4, 10);
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		InputManager input = display.getInput();
		
		Point mousePos = input.getMousePosition();
		if (iconBorder.contains(mousePos)) {
			isHovering = true;
		} else {
			isHovering = false;
		}
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		GraphicsManager.drawImage(icon, (int)(iconBorder.x + (iconBorder.width / 2 - icon.getWidth() / 2)) + 1, (int)(iconBorder.y + (iconBorder.height / 2 - icon.getHeight() / 2)) + 1, getWidth(), getHeight(), graphics);
		
		InputManager input = display.getInput();
		Point mousePos = input.getMousePosition();
		
		if (isHovering) {
			graphics.setColor(Color.black);
		} else
			graphics.setColor(Color.gray);
		
		if (isSelected)
			graphics.setColor(Color.black);
		graphics.draw(iconBorder);
		
		if (isHovering) {
			toolTip = new RoundRectangle2D.Double(0, 0, graphics.getFontMetrics().stringWidth(race.name()), graphics.getFontMetrics().getHeight(), 1, 1);
			toolTip.x = mousePos.x;
			toolTip.y = mousePos.y - toolTip.height;
			graphics.setColor(Color.darkGray);
			graphics.fill(toolTip);
			
			graphics.setColor(new Color(223, 195, 15));
			GraphicsManager.drawString(race.name(), (float)toolTip.x, (float)toolTip.y, graphics);
		} else {
			toolTip = null;
		}
	}
	
	/**
	 * @return this option's race
	 */
	public RaceType getRace() {
		return race;
	}
	
	/**
	 * @return this option's border
	 */
	public RoundRectangle2D.Double getBorder() {
		return iconBorder;
	}

	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		iconBorder.x = x;
		iconBorder.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return icon.getWidth();
	}

	@Override
	public int getHeight() {
		return icon.getHeight();
	}
}
