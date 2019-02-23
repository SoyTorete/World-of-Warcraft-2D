package wow.tiled;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tileset {

	public int FirstGridID;
	public int LastGridID;
	public int[] GridIDs;
	public int[][] GridIDCoordinates;
	public int TileWidth;
	public int TileHeight;
	public int TileCount;
	public int Columns;
	public int Rows;
	
	public File ImageSrc;
	public BufferedImage Image;
	public int ImageWidth;
	public int ImageHeight;
	
	public void generateData() {
		try {
			Image = ImageIO.read(ImageSrc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Rows = ImageHeight / TileHeight;
		LastGridID = Columns * Math.abs(ImageHeight/ TileHeight) + FirstGridID - 1; 
		
		GridIDs = new int[TileCount+1];
		int tileGidIndex = FirstGridID;
		for (int i = 0; i < GridIDs.length; i++) {
			GridIDs[i] = tileGidIndex;
			tileGidIndex++;
		}
		
		GridIDCoordinates = new int[Columns][Rows];
		for (int x = 0; x < GridIDCoordinates.length; x++) {
			for (int y = 0; y < GridIDCoordinates[0].length; y++) {
				GridIDCoordinates[x][y] = GridIDs[(x+(y*(ImageWidth / TileWidth)))];
			}
		}
	}
	
	public boolean contains(int gid) {
		for (int i : GridIDs) {
			if (i == gid) {
				return true;
			}
		}
		return false;
	}
	
	public BufferedImage getImageFor(int gid) {
		for (int x = 0; x < GridIDCoordinates.length; x++) {
			for (int y = 0; y < GridIDCoordinates[0].length; y++) {
				if (GridIDCoordinates[x][y] == gid) {
					return Image.getSubimage(x * 16, y * 16, 16, 16);
				}
			}
		}
		return null;
	}
}
