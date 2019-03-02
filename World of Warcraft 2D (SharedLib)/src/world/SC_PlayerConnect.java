package world;

import main.APacket;

/**
 * New player to all players.
 * @author Xolitude
 * @since February 23, 2019
 */
public class SC_PlayerConnect extends APacket {
	
	public String Name;
	public int RaceID;
	public float X, Y;
	public int Level;

	@Override
	public String toString() {
		return "sc_player_connect";
	}
}
