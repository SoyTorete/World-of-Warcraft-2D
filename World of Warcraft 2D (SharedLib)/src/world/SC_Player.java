package world;

import main.APacket;

/**
 * Player packet.
 * @author Xolitude
 * @since February 23, 2019
 */
public class SC_Player extends APacket {

	public String Name;
	public int RaceID;
	public float X, Y;
	public int Level;
	
	@Override
	public String toString() {
		return "sc_player";
	}
}
