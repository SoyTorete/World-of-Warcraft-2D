package wow.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import wow.WoW;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.InputManager;

/**
 * A custom TextField class. Credits to the Slick-engine for the inspiration.
 * @author Xolitude
 * @since November 27, 2018
 * @see https://github.com/ariejan/slick2d/blob/master/src/org/newdawn/slick/gui/TextField.java
 */
public class GuiTextField implements GuiInterface, KeyListener {
	
	private Color borderColor;
	private Color backgroundColor;
	private Color foregroundColor;
	
	private int x, y;
	private int width, height;
	private RoundRectangle2D.Double bounds;
	private int cursorPosition = 0;
	
	private boolean hasFocus = false;
	private boolean isEnabled = true;
	
	private char echoChar;
	private String text = "", passwordText = "";
	
	public GuiTextField(DisplayManager display, int width, int height, int arcW, int arcH) {
		display.addKeyListener(this);
		this.width = width;
		this.height = height;
		bounds = new RoundRectangle2D.Double(0, 0, width, height, arcW, arcH);
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(14f));
		
		/** Set the background color of the textfield if it is not null. **/
		if (backgroundColor != null)
			graphics.setColor(backgroundColor);
		else
			graphics.setColor(Color.black);
		graphics.fill(bounds);
		
		/** Get the cursor position relative to the width of the current text. **/
		int localCursorPosition = graphics.getFontMetrics().stringWidth(text.substring(0, cursorPosition));
		int translateX = 0;
		
		/** Set a translation only if the cursor is greater than the width of the textfield. **/
		if (localCursorPosition > width - 1) {
			translateX = width - localCursorPosition - graphics.getFontMetrics().stringWidth("|");
		}
		
		/** Set the graphics clip to this textfield's size and draw it's text. **/
		Rectangle oldClip = (Rectangle) graphics.getClip();
		graphics.setClip(bounds);
		graphics.translate(translateX + 2, 0);
		if (foregroundColor != null)
			graphics.setColor(foregroundColor);
		else
			graphics.setColor(Color.white);
		GraphicsManager.drawString(text, x, y + 4, graphics);
		if (hasFocus)
			GraphicsManager.drawString("|", x+localCursorPosition, y + 4, graphics);
		
		graphics.translate(-translateX - 2, 0);
		graphics.setClip(oldClip);
		
		/** Set the border color of the textfield if it is not null. **/
		if (borderColor != null)
			graphics.setColor(borderColor);
		else
			graphics.setColor(Color.gray);
		graphics.draw(bounds);
	}

	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		bounds.x = x;
		bounds.y = y;
	}
	
	/**
	 * Set the border color of this textfield.
	 * @param borderColor
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	
	/**
	 * Set the background color of this textfield.
	 * @param backgroundColor
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	/**
	 * Set the foreground (text) color of this textfield.
	 * @param foregroundColor
	 */
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	/**
	 * Set this textfield's focus.
	 * @param hasFocus
	 */
	public void setFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}
	
	/**
	 * Set an echo character; i.e a password character to "hide" the text.
	 * @param echoChar
	 */
	public void setEchoChar(char echoChar) {
		this.echoChar = echoChar;
	}
	
	/**
	 * Does this textfield have focus?
	 * @return hasFocus
	 */
	public boolean hasFocus() {
		return hasFocus;
	}
	
	/**
	 * Should we enable this component?
	 * @param isEnabled
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	/**
	 * Set the text of this textfield.
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		if (cursorPosition > text.length())
			cursorPosition = text.length();
		if (echoChar != 0) 
			this.passwordText = text;
	}
	
	/**
	 * Get the plain text of either the password text or regular text.
	 * @return this TextField's text
	 */
	public String getText() {
		if (echoChar != 0) {
			return passwordText.trim();
		} else
			return text.trim();
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
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (hasFocus && isEnabled) {
			int keyCode = e.getKeyCode();
			
			/** These display a "?" symbol. **/
			if (keyCode == KeyEvent.VK_TAB || keyCode == KeyEvent.VK_CAPS_LOCK 
					|| keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_ENTER 
					|| keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT)
				return;
			
			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				if (cursorPosition > 0 && text.length() > 0) {
					/** Remove characters from the main text. **/
					text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
					
					/** Remove characters from the passwordText as well if there is some to remove. **/
					if (passwordText.length() > 0) {
						passwordText = passwordText.substring(0, cursorPosition - 1) + passwordText.substring(cursorPosition);
					}
					cursorPosition--;
				}
				return;
			}
			
			/** If we are using an echo character, append the text with that character but store the real text in a field. **/
			if (echoChar != 0) {
				text += echoChar;
				passwordText += e.getKeyChar();
			} else
				text += e.getKeyChar();
			cursorPosition++;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}
