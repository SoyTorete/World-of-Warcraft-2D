package wow.server.manager;

import java.util.ArrayList;

import wow.server.GameServer;
import wow.server.Player;
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
	 * Adds a player to a zone.
	 * @param player
	 * @param zoneId
	 */
	public static void AddPlayerToZone(Player player, int zoneId) {
		for (Zone zone : zones) {
			if (zone.ID == zoneId) {
				zone.addPlayer(player);
			}
		}
	}
	
	/**
	 * Remove a player from their zone.
	 * @param player
	 */
	public static void RemovePlayerFromZone(Player player) {
		for (Zone zone : zones) {
			if (zone.ID == player.ZoneID) {
				zone.removePlayer(player);
			}
		}
	}
	
	public static ArrayList<Player> GetPlayersInZone(int zoneId) {
		for (Zone zone : zones) {
			if (zone.ID == zoneId) {
				return zone.getPlayers();
			}
		}
		return null;
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
