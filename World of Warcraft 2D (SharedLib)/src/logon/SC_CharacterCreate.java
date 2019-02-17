package logon;

import main.APacket;

/**
 * S>C character creation.
 * @author Xolitude
 * @since February 17, 2019
 */
public class SC_CharacterCreate extends APacket {

	public int Code;
	
	@Override
	public String toString() {
		return "sc_character_create";
	}
}
