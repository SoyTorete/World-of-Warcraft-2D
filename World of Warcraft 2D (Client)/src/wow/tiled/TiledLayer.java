package wow.tiled;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TiledLayer {

	public String Name;
	public int Width, Height;
	public ArrayList<Integer> TileGridIDs;
	private Integer[] tileGridArray;
	private int[][] tileGridIdCoordinates;
	
	private BufferedImage image;
	
	public void generateData(TiledLevel map) {
		tileGridArray = new Integer[TileGridIDs.size()];
		tileGridArray = TileGridIDs.toArray(tileGridArray);
		
		tileGridIdCoordinates = new int[map.Width][map.Height];
		for (int x = 0; x < tileGridIdCoordinates.length; x++) {
			for (int y = 0; y < tileGridIdCoordinates[0].length; y++) {
				tileGridIdCoordinates[x][y] = tileGridArray[(x+(y*map.Width))];
			}
		}
		
		image = new BufferedImage(Width * map.tWidth, Height * map.tHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		
		for (int x = 0; x < tileGridIdCoordinates.length; x++) {
			for (int y = 0; y < tileGridIdCoordinates[0].length; y++) {
				Tileset tilesetToUse = null;
				
				tilesetToUse = map.getTilesetContaining(tileGridIdCoordinates[x][y]);
				if (tilesetToUse != null) {
					BufferedImage img = tilesetToUse.getImageFor(tileGridIdCoordinates[x][y]);
					if (img != null) {
						g.drawImage(img, x * 16, y * 16, null);
					}
				}
			}
		}
		g.dispose();
	}
	
	public BufferedImage getLayerImage() {
		return image;
	}
}
