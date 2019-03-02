package world;

import main.APacket;

/**
 * Player disconnect packet.
 * @author Xolitude
 * @since March 1, 2019
 */
public class SC_PlayerDisconnect extends APacket {
	
	public String Name;

	@Override
	public String toString() {
		return "sc_player_disconnect";
	}
}
