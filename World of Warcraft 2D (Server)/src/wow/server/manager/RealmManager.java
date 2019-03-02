package wow.server.manager;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import wow.server.GameServer;
import wow.server.GameServerGUI.LogType;
import wow.server.util.Configuration;

/**
 * Stores realm information.
 * @author Xolitude
 * @since December 3, 2018
 */
public class RealmManager {
	
	private static ArrayList<Realm> realms;
	
	/**
	 * Initialize the manager.
	 * @param server
	 */
	public static void Initialize(GameServer server) {
		realms = new ArrayList<Realm>();
		realms.add(new Realm(1, "PTR a0.8.0", 6771));
		
		if (realms.size() == 0) {
			Logger.getLogger("server").log(Level.SEVERE, "No realms were found!");
			server.stop();
		}
		
		for (Realm realm : realms) {
			GameServer.getServerConsole().writeMessage(LogType.Logon, String.format("Added realm: %s:%s", realm.name, realm.port));
			Logger.getLogger("server").log(Level.INFO, "Added realm: {0}:{1}", new Object[] {realm.name, String.valueOf(realm.port)});
		}
	}
	
	/**
	 * @return realms
	 */
	public static ArrayList<Realm> GetRealms() {
		return realms;
	}
	
	/**
	 * Stores realm-specific information.
	 * @author Xolitude
	 * @since December 3, 2018
	 */
	public static class Realm {
		private int id;
		private String name;
		private int port;
		
		public Realm(int id, String name, int port) {
			this.id = id;
			this.name = name;
			this.port = port;
		}

		/**
		 * @return id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return port
		 */
		public int getPort() {
			return port;
		}
	}
}
