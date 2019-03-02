package wow.server.connection;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;

import wow.server.Account;

public class TemporaryConnection extends Connection {

	public Account Account;
	public int RealmID;
	
	public TemporaryConnection() {
		Logger.getLogger("server").log(Level.INFO, "Auth client connected.");
	}
}
