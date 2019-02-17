package wow.gfx;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Handles spritesheet data.
 * @author Xolitude
 * @since November 30, 2018
 */
public class Spritesheet {

	private BufferedImage spritesheet;
	
	public Spritesheet(String src) {
		try {
			spritesheet = ImageIO.read(getClass().getResourceAsStream(src));
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to find the spritesheet file.");
		} catch (IOException ex) {
			System.out.println("Unable to read from the given directory:");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Get a sub-image of the spritesheet.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage getSubImage(int x, int y, int width, int height) {
		return spritesheet.getSubimage(x * width, y * height, width, height);
	}
}
