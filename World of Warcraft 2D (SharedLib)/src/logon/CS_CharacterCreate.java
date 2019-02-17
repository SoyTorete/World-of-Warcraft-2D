package logon;

import main.APacket;

/**
 * Client>Server character creation.
 * @author Xolitude
 * @since February 17, 2019
 */
public class CS_CharacterCreate extends APacket {

	public String Name;
	public int RaceID;
	
	@Override
	public String toString() {
		return "cs_character_create";
	}
}
