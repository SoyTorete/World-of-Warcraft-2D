package wow.net.handler;

import com.esotericsoftware.kryonet.Connection;

import logon.CS_Realmlist;
import logon.SC_Login;
import main.APacket;
import wow.manager.NetworkManager;
import wow.manager.NetworkManager.SimplePacketDirection;
import wow.net.connection.AuthConnection.Auth;

public class LoginHandler implements IHandler {
	
	private final int Unk = 10;
	private final int Online = 2;
	private final int Incorrect = 1;
	private final int Ok = 0;

	@Override
	public void handlePacket(Connection connection, APacket object) {
		SC_Login sub_packet = (SC_Login)object;
		int code = sub_packet.Code;
		switch (code) {
		case Unk:
			NetworkManager.AUTH.STATUS = Auth.AuthenticatingUnk;
			break;
		case Online:
			NetworkManager.AUTH.STATUS = Auth.AlreadyOnline;
			break;
		case Incorrect:
			NetworkManager.AUTH.STATUS = Auth.AuthenticatingIncorrect;
			break;
		case Ok:
			NetworkManager.AUTH.STATUS = Auth.AuthenticatingOk;
			NetworkManager.SendSimplePacket(SimplePacketDirection.Auth, new CS_Realmlist());
			break;
		}
	}
}
