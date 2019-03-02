package world;

import main.APacket;

/**
 * Movement packet.
 * @author Xolitude
 * @since February 22, 2019
 */
public class CS_Movement extends APacket {

	public int Direction;
	public boolean IsMoving;
	
	@Override
	public String toString() {
		return "cs_movement";
	}
}
