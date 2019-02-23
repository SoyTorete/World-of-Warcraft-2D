package world;

import main.APacket;

/**
 * Used to tell the server we're going to attempt to connect to the world.
 * @author Xolitude
 * @since February 20, 2019
 */
public class CS_WorldConnection extends APacket {
	
	public String AccountName;
	public String CharacterName;

	@Override
	public String toString() {
		return "cs_world_connection";
	}
}
