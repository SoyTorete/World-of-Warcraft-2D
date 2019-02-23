package wow.tiled;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class TiledLevel {

	public int Width, Height;
	public int tWidth, tHeight;
	
	private ArrayList<Tileset> tilesets;
	private ArrayList<TiledLayer> layers;
	
	public TiledLevel() {
		tilesets = new ArrayList<Tileset>();
		layers = new ArrayList<TiledLayer>();
	}
	
	public void render(Graphics2D graphics) {
		AffineTransform xform = graphics.getTransform(); // save the original xform.
		for (TiledLayer layer : layers) {
			graphics.scale(2, 2);
			graphics.drawImage(layer.getLayerImage(), null, 0, 0);
			graphics.setTransform(xform);
		}
	}
	
	public void addTileset(Tileset tileset) {
		tileset.generateData();
		tilesets.add(tileset);
	}
	
	public void addLayer(TiledLayer layer) {
		layer.generateData(this);
		layers.add(layer);
	}
	
	public Tileset getTilesetContaining(int gid) {
		for (Tileset tileset : tilesets) {
			if (tileset.contains(gid)) {
				return tileset;
			}
		}
		return null;
	}
}
