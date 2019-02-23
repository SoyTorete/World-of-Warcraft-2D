package logon;

import java.util.ArrayList;

import main.APacket;

/**
 * S>C character list. Sent with a list of SC_Character objects.
 * @author Xolitude
 * @since February 17, 2019
 */
public class SC_CharacterList extends APacket {
	
	public ArrayList<SC_Character> CharacterList;

	@Override
	public String toString() {
		return "sc_character_list";
	}
}
