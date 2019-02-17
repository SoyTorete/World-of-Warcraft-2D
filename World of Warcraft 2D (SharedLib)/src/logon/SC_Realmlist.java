package logon;

import main.APacket;

public class SC_Realmlist extends APacket {
	
	public int ID;
	public String Name;
	public int Port;

	@Override
	public String toString() {
		return "sc_realmlist";
	}
}
