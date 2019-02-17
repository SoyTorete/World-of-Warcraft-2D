package wow.server.world;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wow.server.GameServer;
import wow.server.GameServerGUI.LogType;
import wow.server.manager.ZoneManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses each zone in the game upon initialization.
 * @author Xolitude
 * @since December 11, 2018
 */
public class ZoneParser implements Runnable {
	
	public enum State {
		Loading, 
		Finished
	}
	private State state;

	private GameServer server;
	private Thread thread;
	
	private final File mapsFolder = new File("data/maps");
	private ArrayList<File> mapsToParse;
	
	public ZoneParser(GameServer server) {
		this.server = server;
		mapsToParse = new ArrayList<File>();
		for (File file : mapsFolder.listFiles()) {
			if (file.isFile()) {
				mapsToParse.add(file);
			}
		}
	}
	
	public synchronized void start() {
		server.getServerConsole().writeMessage(LogType.Server, String.format("Loading %s zone(s)...", mapsToParse.size()));
				
		thread = new Thread(this, "parser");
		thread.start();
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		state = State.Loading;
		do {
			File map = mapsToParse.get(0);
			Zone zone = new Zone();
			
			Document mapDoc = null;
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				mapDoc = dBuilder.parse(map);
				mapDoc.getDocumentElement().normalize();
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
			NodeList properties = mapDoc.getElementsByTagName("property");
			for (int i = 0; i < properties.getLength(); i++) {
				Node propertyNode = properties.item(i);
				
				if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
					Element propertyElement = (Element) propertyNode;
					
					String name = propertyElement.getAttribute("name");
					switch (name.toLowerCase()) {
					case "zone_id":
						zone.ID = Integer.valueOf(propertyElement.getAttribute("value"));
						break;
					case "zone_name":
						zone.Name = propertyElement.getAttribute("value");
						break;
					case "zone_spawn":
						zone.Race = Integer.valueOf(propertyElement.getAttribute("value"));
						break;
					}
				}
			}
			
			// TODO: (Server) Add game-object parsing.
			
			ZoneManager.AddZone(zone);
			mapsToParse.remove(map);
		} while (mapsToParse.size() > 0);
		long stop = System.currentTimeMillis();
		server.getServerConsole().writeMessage(LogType.Server, String.format("Loaded zones in %sms.", stop - start));
		state = State.Finished;
	}
	
	public State getState() {
		return state;
	}
}
