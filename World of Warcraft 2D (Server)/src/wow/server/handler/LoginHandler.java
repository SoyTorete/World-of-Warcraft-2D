package wow.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import logon.CS_Login;
import logon.SC_Login;
import main.APacket;
import wow.server.GameServer;
import wow.server.connection.TemporaryConnection;
import wow.server.manager.DatabaseManager;
import wow.server.util.BCrypt;

/**
 * Handles login data from the client.
 * @author Xolitude
 * @since February 4, 2019
 */
public class LoginHandler implements IHandler {
	
	private final int Unk = 10;
	private final int Incorrect = 1;
	private final int Ok = 0;

    @Override
    public void handlePacket(Server server, Connection connection, APacket packet) {
    	TemporaryConnection temp = (TemporaryConnection) connection;
        CS_Login sub_packet = (CS_Login)packet;
        String username = sub_packet.Username.toUpperCase();
        String password = sub_packet.Password;
        
		Logger.getLogger("server").log(Level.INFO, "{0} is logging in.", username);
        
        String[] data = DatabaseManager.AccountExists(username);
        SC_Login response = new SC_Login();
        
        if (data == null) { // Send unk.
        	response.Code = Unk;
        	temp.sendTCP(response);
        	temp.close();
        } else {
        	String dbPassword = data[0];
        	String dbSalt = data[1];
        	// wait to pull id.
        	
        	String hashed = BCrypt.hashpw(password+GameServer.SALT, dbSalt);
        	if (dbPassword.equalsIgnoreCase(hashed)) {
        		temp.Username = username;
        		temp.UserID = Integer.valueOf(data[2]);
        		response.Code = Ok;
        		temp.sendTCP(response);
        	} else {
        		response.Code = Incorrect;
        		temp.sendTCP(response);
        		temp.close();
        	}
        }
    }
}
