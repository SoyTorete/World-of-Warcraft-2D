package wow.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.InputManager;

/**
 * Handles button rendering/ticks.
 * @author Xolitude
 * @since November 26, 2018
 */
public class GuiButton implements GuiInterface {

	private BufferedImage enabled;
	private BufferedImage disabled;
	
	private String text;
	private int x, y;
	
	private Rectangle bounds;
	
	private boolean hovering = false;
	private boolean isEnabled = true;
	
	private ActionListener actionListener;
	
	public GuiButton(String text) {
		try {
			enabled = ImageIO.read(getClass().getResourceAsStream("/ui/wow_button.png"));
			disabled = ImageIO.read(getClass().getResourceAsStream("/ui/wow_button_disabled.png"));
		} catch (FileNotFoundException ex) {
			System.err.println("Could not find the button image specified.");
		} catch (IOException ex) {
			System.err.println("Unable to read the button image path: "+ex.getMessage());
		}
		
		this.text = text;
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		InputManager input = display.getInput();
		
		if (isEnabled) {
			if (bounds.contains(input.getMousePosition())) {
				hovering = true;
				if (input.isMouseButtonPressed(InputManager.MOUSE_LEFT)) {
					fireAction();
				}
			} else hovering = false;
		}
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(12f));
		if (isEnabled)
			GraphicsManager.drawImage(enabled, x, y, getWidth(), getHeight(), graphics);
		else
			GraphicsManager.drawImage(disabled, x, y, getWidth(), getHeight(), graphics);

		if (isEnabled) {
			if (hovering)
				graphics.setColor(Color.WHITE);
			else
				graphics.setColor(new Color(223, 195, 15));
		} else
			graphics.setColor(Color.gray);
		GraphicsManager.drawCenteredString(text, x, y, getWidth(), getHeight(), graphics);
	}
	
	/**
	 * Add an ActionListener to this button.
	 * @param actionListener
	 */
	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
	
	/**
	 * Fire the action given to the ActionListener.
	 */
	private void fireAction() {
		try {
			ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
			actionListener.actionPerformed(ae);
		} catch (NullPointerException ex) {
			System.err.println("This button does not have an action.");
		}
	}
	
	/**
	 * Should we enable this component?
	 * @param isEnabled
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		
		bounds = new Rectangle(x, y, getWidth(), getHeight());
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
		return enabled.getWidth();
	}

	@Override
	public int getHeight() {
		return enabled.getHeight();
	}
}
