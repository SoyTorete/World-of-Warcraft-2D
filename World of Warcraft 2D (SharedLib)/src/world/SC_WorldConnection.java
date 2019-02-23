package world;

import main.APacket;

/**
 * Basically just an "ok" response.
 * @author Xolitude
 * @since February 20, 2019
 */
public class SC_WorldConnection extends APacket {

	@Override
	public String toString() {
		return "sc_world_connection";
	}
}
