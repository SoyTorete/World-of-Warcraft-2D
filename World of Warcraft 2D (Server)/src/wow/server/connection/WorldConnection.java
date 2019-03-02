package wow.server.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;

import wow.server.Account;

/**
 * The world connection per client.
 * @author Xolitude
 * @since February 22, 2019
 */
public class WorldConnection extends Connection {
	
	public Account Account;
	
	public WorldConnection() {
		Logger.getLogger("server").log(Level.INFO, "World client connected.");
	}
}
