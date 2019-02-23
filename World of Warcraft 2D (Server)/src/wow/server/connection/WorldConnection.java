package wow.server.connection;

import com.esotericsoftware.kryonet.Connection;

/**
 * The world connection per client.
 * @author Xolitude
 * @since February 22, 2019
 */
public class WorldConnection extends Connection{

	public String Username;
	public int UserID;
	public int RealmID;
	public int X, Y;
	public String Character;
}
