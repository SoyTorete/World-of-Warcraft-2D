package wow.manager;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 * Handles some graphics operations.
 * @author Xolitude
 * @since November 26, 2018
 */
public class GraphicsManager {

	/**
	 * Draws a string to the screen.
	 * @param str
	 * @param x
	 * @param y
	 * @param graphics
	 */
	public static void drawString(String str, float x, float y, Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawString(str, x, graphics.getFontMetrics().getAscent() + y);
	}
	
	/**
	 * Draws a string centered around the given bounds.
	 * @param str
	 * @param boundsX
	 * @param boundsY
	 * @param graphics
	 */
	public static void drawCenteredString(String str, int x, int y, int boundsX, int boundsY, Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawString(str, x + (boundsX / 2 - graphics.getFontMetrics().stringWidth(str) / 2), y + graphics.getFontMetrics().getAscent() + (boundsY / 2 - graphics.getFontMetrics().getHeight() / 2));
	}
	
	/**
	 * Draws an image at the given position with the given width and height.
	 * @param img
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param graphics
	 */
	public static void drawImage(Image img, float x, float y, int width, int height, Graphics2D graphics) {
		graphics.drawImage(img, (int)x, (int)y, width, height, null);
	}
	
	/**
	 * Get a hex-string value from a byte array.
	 * @param ref
	 * @return hex-string
	 */
	public static String BytesToHex(byte[] ref) {
		StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < ref.length; i++) {
	    String hex = Integer.toHexString(0xff & ref[i]);
	    if(hex.length() == 1) hexString.append('0');
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
}
