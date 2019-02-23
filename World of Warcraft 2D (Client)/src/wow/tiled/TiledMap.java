package wow.tiled;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TiledMap implements Runnable {

	public enum ParseState {
		Waiting(0.0f),
		MapData(0.3f),
		Tilesets(0.6f),
		Layers(0.9f),
		Finished(1.0f);
		
		private float percent;
		
		ParseState(float percent) {
			this.percent = percent;
		}
		
		public float getPercent() {
			return percent;
		}
	}
	
	private Thread thread;
	public ParseState state = ParseState.Waiting;
	
	private File file;
	public TiledLevel level;
	
	public TiledMap(File file) {
		this.file = file;
	}
	
	public void start() {
		if (thread != null)
			return;
		thread = new Thread(this, "map_parser");
		thread.start();
		
		state = ParseState.MapData;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		if (file != null) {
			Document doc = null;
			level = new TiledLevel();
			
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
			level.Width = Integer.valueOf(doc.getDocumentElement().getAttribute("width"));
			level.Height = Integer.valueOf(doc.getDocumentElement().getAttribute("height"));
			level.tWidth = Integer.valueOf(doc.getDocumentElement().getAttribute("tilewidth"));
			level.tHeight = Integer.valueOf(doc.getDocumentElement().getAttribute("tileheight"));
			
			/*****************************************
			 *****************************************/
			
			state = ParseState.Tilesets;
			Logger.getLogger("client").log(Level.INFO, "Reading tilesets.");
			
			NodeList tilesetsList = doc.getElementsByTagName("tileset");
			for (int i = 0; i < tilesetsList.getLength(); i++) {
				Tileset tileset = new Tileset();
				
				Node tilesetNode = tilesetsList.item(i);
				if (tilesetNode.getNodeType() == Node.ELEMENT_NODE) {
					Element tilesetElement = (Element) tilesetNode;
					tileset.FirstGridID = Integer.valueOf(tilesetElement.getAttribute("firstgid"));
					tileset.TileWidth = level.tWidth;
					tileset.TileHeight = level.tHeight;
					tileset.TileCount = Integer.valueOf(tilesetElement.getAttribute("tilecount"));
					tileset.Columns = Integer.valueOf(tilesetElement.getAttribute("columns"));
				}
				Node tilesetImageData = tilesetNode.getChildNodes().item(1);
				if (tilesetImageData.getNodeType() == Node.ELEMENT_NODE) {
					Element tilesetImageDataElement = (Element) tilesetImageData;
					
					try {
						tileset.ImageSrc = new File(getClass().getResource("/maps/"+tilesetImageDataElement.getAttribute("source")).toURI());
					} catch (URISyntaxException ex) {
						Logger.getLogger("client").log(Level.SEVERE, "Unable to read tileset image source: {0}", ex.getMessage());
					}
					tileset.ImageWidth = Integer.valueOf(tilesetImageDataElement.getAttribute("width"));
					tileset.ImageHeight = Integer.valueOf(tilesetImageDataElement.getAttribute("height"));
				}
				level.addTileset(tileset);
			}
			
			/*****************************************
			 *****************************************/
			
			state = ParseState.Layers;
			Logger.getLogger("client").log(Level.INFO, "Reading layers.");
			
			NodeList layerList = doc.getElementsByTagName("layer");
			for (int i = 0; i < layerList.getLength(); i++) {
				TiledLayer layer = new TiledLayer();
				ArrayList<Integer> tileGridIds = new ArrayList<Integer>();
				
				Node layerNode = layerList.item(i);
				if (layerNode.getNodeType() == Node.ELEMENT_NODE) {
					Element layerElement = (Element) layerNode;
					
					layer.Name = layerElement.getAttribute("name");
					layer.Width = Integer.valueOf(layerElement.getAttribute("width"));
					layer.Height = Integer.valueOf(layerElement.getAttribute("height"));
					
					NodeList layerTiles = layerElement.getElementsByTagName("tile");
					for (int ii = 0; ii < layerTiles.getLength(); ii++) {
						Node layerTileNode = layerTiles.item(ii);
						
						if (layerTileNode.getNodeType() == Node.ELEMENT_NODE) {
							Element layerTileElement = (Element) layerTileNode;
							tileGridIds.add(Integer.valueOf(layerTileElement.getAttribute("gid")));
						}
					}
					layer.TileGridIDs = tileGridIds;
				}
				level.addLayer(layer);
			}
		}
		state = ParseState.Finished;
		long stop = System.currentTimeMillis();
		Logger.getLogger("client").log(Level.INFO, "Finished loading in: {0}ms", stop-start);
	}
}
