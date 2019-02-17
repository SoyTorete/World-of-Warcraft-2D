package logon;

import main.APacket;

/**
 * C>S character delete.
 * @author Xolitude
 * @since February 17, 2019
 */
public class CS_CharacterDelete extends APacket {
	
	public String Name;

	@Override
	public String toString() {
		return "cs_character_delete";
	}
}
