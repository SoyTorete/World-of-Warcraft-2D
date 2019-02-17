package logon;

import main.APacket;

/**
 * C>S character list request.
 * @author Xolitude
 * @since February 17, 2019
 */
public class CS_CharacterList extends APacket {

	@Override
	public String toString() {
		return "cs_character_list";
	}
}
