package logon;

import main.APacket;

public class CS_Realmlist extends APacket {
	
	public int Code = 10;

	@Override
	public String toString() {
		return "cs_realmlist";
	}
}
