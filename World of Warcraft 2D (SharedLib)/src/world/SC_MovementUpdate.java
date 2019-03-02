package world;

import main.APacket;

/**
 * Send back to the client.
 * @author Xolitude
 * @since February 22, 2019
 */
public class SC_MovementUpdate extends APacket {
	
	public float NewX;
	public float NewY;

	@Override
	public String toString() {
		return "sc_movement_update";
	}
}
