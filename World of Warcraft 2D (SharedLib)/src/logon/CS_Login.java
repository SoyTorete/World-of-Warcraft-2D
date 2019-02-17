package logon;

import main.APacket;

/**
 * Client>Server logon.
 * @author Xolitude
 * @since February 4, 2019
 */
public class CS_Login extends APacket {

	public String Username;
	public String Password;

	@Override
	public String toString() {
		return "cs_login";
	}
}
