package wow.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import logon.CS_Login;
import logon.SC_Login;
import main.APacket;
import wow.server.Account;
import wow.server.GameServer;
import wow.server.connection.TemporaryConnection;
import wow.server.connection.WorldConnection;
import wow.server.manager.AccountManager;
import wow.server.util.BCrypt;

/**
 * Handles login data from the client.
 * @author Xolitude
 * @since February 4, 2019
 */
public class LoginHandler implements IHandler {
	
	private final int Unk = 10;
	private final int Online = 2;
	private final int Incorrect = 1;
	private final int Ok = 0;

    @Override
    public void handlePacket(Server server, Connection connection, APacket packet) {
    	TemporaryConnection temp = (TemporaryConnection) connection;
        CS_Login sub_packet = (CS_Login)packet;
        String username = sub_packet.Username.toUpperCase();
        String password = sub_packet.Password;
        SC_Login response = new SC_Login();
        
		Logger.getLogger("server").log(Level.INFO, "{0} is logging in.", username);
		Account account = AccountManager.Exists(username);
		if (account == null) {
			response.Code = Unk;
			temp.sendTCP(response);
			temp.close();
			return;
		}
		
		boolean online = GameServer.isAccountOnline(username);
		if (online) {
			response.Code = Online;
			temp.sendTCP(response);
			temp.close();
			return;
		}
		
		String hashCheck = BCrypt.hashpw(password+GameServer.SALT, account.Salt);
		if (account.HashedPassword.equalsIgnoreCase(hashCheck)) {
			temp.Account = account;
			
			response.Code = Ok;
			temp.sendTCP(response);
		} else {
			response.Code = Incorrect;
			temp.sendTCP(response);
			temp.close();
		}
    }
}
