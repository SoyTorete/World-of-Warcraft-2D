package world;

import main.APacket;

/**
 * Send a new connections position to them.
 * @author Xolitude
 * @since February 22, 2019
 */
public class SC_WorldPosition extends APacket {

	public float X;
	public float Y;
	
	@Override
	public String toString() {
		return "sc_world_position";
	}
}
