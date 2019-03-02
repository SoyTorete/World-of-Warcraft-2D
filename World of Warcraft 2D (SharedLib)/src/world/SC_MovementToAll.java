package world;

import main.APacket;

/**
 * Send new movement to all players.
 * @author Xolitude
 * @since February 23, 2019
 */
public class SC_MovementToAll extends APacket {

	public String Name;
	public float NewX, NewY;
	public int Direction;
	public boolean IsMoving;
	
	@Override
	public String toString() {
		return "sc_movement_toall";
	}
}
