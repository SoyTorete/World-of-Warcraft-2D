package wow.server.manager;

import java.util.ArrayList;

import wow.server.GameServer;
import wow.server.world.Zone;
import wow.server.world.ZoneParser;
import wow.server.world.ZoneParser.State;

/**
 * Handles the different zones for the game.
 * @author Xolitude
 * @since December 11, 2018
 */
public class ZoneManager {
	
	private static ZoneParser parser;
	private static ArrayList<Zone> zones;
	
	public static void Initialize(GameServer server) {
		if (parser == null) {
			zones = new ArrayList<Zone>();
			parser = new ZoneParser(server);
			parser.start();
		}
	}
	
	/**
	 * Adds a zone to this manager.
	 * @param zone
	 */
	public static void AddZone(Zone zone) {
		zones.add(zone);
	}
	
	/**
	 * @return the current state of the parser.
	 */
	public static State GetParseState() {
		return parser.getState();
	}
	
	/**
	 * Get the zone-spawn for a specific race.
	 * @param race
	 * @return the zone id
	 */
	public static int GetZoneSpawnForRace(int race) {
		for (Zone zone : zones) {
			if (zone.Race == race) {
				return zone.ID;
			}
		}
		return -1;
	}
}
