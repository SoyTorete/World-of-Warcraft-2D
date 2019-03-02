package wow.server.world;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import wow.server.Player;

/**
 * Holds zone-specific data.
 * @author Xolitude
 * @since December 11, 2018
 */
public class Zone {

	public int ID;
	public String Name;
	public int Race;
	
	private ArrayList<Player> players = new ArrayList<Player>();
	
	public void addPlayer(Player player) {
		Logger.getLogger("server").log(Level.INFO, "Added player {0} to zone {1}:{2}.", new Object[] { player.Name, Name, ID});
		players.add(player);
	}
	
	public void removePlayer(Player player) {
		Logger.getLogger("server").log(Level.INFO, "Removed player {0} from zone {1}:{2}.", new Object[] { player.Name, Name, ID});
		players.remove(player);
	}
	
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> zonePlayers = new ArrayList<Player>();
		for (Player player : players) {
			zonePlayers.add(player);
		}
		return players;
	}
}
