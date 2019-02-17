package logon;

import main.APacket;

public class SC_Login extends APacket {
	
	public int Code;

	@Override
	public String toString() {
		return "sc_login";
	}
}
