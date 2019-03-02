package wow.server;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import main.APacket;
import main.Network;
import world.SC_PlayerDisconnect;
import wow.server.GameServerGUI.LogType;
import wow.server.connection.TemporaryConnection;
import wow.server.connection.WorldConnection;
import wow.server.handler.CharacterHandler;
import wow.server.handler.IHandler;
import wow.server.handler.LoginHandler;
import wow.server.handler.RealmHandler;
import wow.server.handler.WorldHandler;
import wow.server.manager.AccountManager;
import wow.server.manager.RealmManager;
import wow.server.manager.ZoneManager;
import wow.server.util.Configuration;
import wow.server.world.ZoneParser.State;

/**
 * The main server handler.
 * @author Xolitude
 * @since November 30, 2018
 */
public class GameServer {
	
	/** The salt to be used in authentication. **/
	public static final String SALT = "wow2d_a8.0.0";
	
	/**
	 * The different race-types.
	 * @author Xolitude
	 * @since December 12, 2018
	 */
	public enum RaceTypes {
		Undead(1),
		Human(2);
		
		private int id;
		
		RaceTypes(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private static GameServerGUI console;
	
	private static Server auth;
	private static Server world;

	private LinkedHashMap<String, IHandler> handlers;
	
	public GameServer() throws IOException {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%4$s: %5$s [%1$tc]%n");
		
		console = new GameServerGUI(this);
		handlers = new LinkedHashMap<String, IHandler>();
		handlers.put("cs_login", new LoginHandler());
		handlers.put("cs_realmlist", new RealmHandler());
		handlers.put("cs_character_create", new CharacterHandler());
		handlers.put("cs_character_list", new CharacterHandler());
		handlers.put("cs_character_delete", new CharacterHandler());
		handlers.put("cs_world_connection", new WorldHandler());
		handlers.put("cs_movement", new WorldHandler());
		
		Configuration.Initialize();
		AccountManager.Initialize();
		/** Give the server time to load the zones. **/
		ZoneManager.Initialize(this);
		do {} while (ZoneManager.GetParseState() == State.Loading);
		
		auth = new Server() {
			protected Connection newConnection() {
				return new TemporaryConnection();
			}
		};
		auth.start();
		auth.bind(Configuration.getAuthenticationPort());
		console.writeMessage(LogType.Logon, String.format("AuthServer started on port %s.", Configuration.getAuthenticationPort()));
		Network.RegisterLib(auth.getKryo());
		auth.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				for (Map.Entry<String, IHandler> set : handlers.entrySet()) {
					if (set.getKey().equalsIgnoreCase(object.toString())) {
						set.getValue().handlePacket(auth, connection, (APacket)object);
					}
				}
			}
			
			public void disconnected(Connection connection) {
				String username = "Auth client";
				TemporaryConnection temp = (TemporaryConnection)connection;
				if (temp.Account != null) {
					username = temp.Account.Username;
				}
				Logger.getLogger("server").log(Level.INFO, "{0} got disconnected from the authentication server.", username);
			}
		});

		RealmManager.Initialize(this);
		world = new Server() {
			protected Connection newConnection() {
				return new WorldConnection();
			}
		};
		world.start();
		world.bind(RealmManager.GetRealms().get(0).getPort(), RealmManager.GetRealms().get(0).getPort()+1);
		Network.RegisterLib(world.getKryo());
		world.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				for (Map.Entry<String, IHandler> set : handlers.entrySet()) {
					if (set.getKey().equalsIgnoreCase(object.toString())) {
						set.getValue().handlePacket(world, connection, (APacket)object);
					}
				}
			}
			
			// TODO: Save player data, send to all other players, etc,.
			public void disconnected(Connection connection) {
				WorldConnection worldConnection = (WorldConnection) connection;
				
				SC_PlayerDisconnect disconnect = new SC_PlayerDisconnect();
				disconnect.Name = worldConnection.Account.OnlinePlayer.Name;
				for (Connection c : world.getConnections()) {
					WorldConnection wC = (WorldConnection)c;
					if (!wC.Account.Username.equalsIgnoreCase(worldConnection.Account.Username)) {
						wC.sendTCP(disconnect);
					}
				}
				ZoneManager.RemovePlayerFromZone(worldConnection.Account.OnlinePlayer);
			}
		});		
		
		if (auth != null && world != null) {
			Logger.getLogger("server").log(Level.INFO, "AuthServer started on port: {0}", String.valueOf(Configuration.getAuthenticationPort()));
			Logger.getLogger("server").log(Level.INFO, "WorldServer started on ports: {0}:{1}", new Object[] {String.valueOf(RealmManager.GetRealms().get(0).getPort()), String.valueOf(RealmManager.GetRealms().get(0).getPort()+1)});
		} else 
			stop();
	}
	
	/**
	 * Is this account already logged in?
	 * @param username
	 * @return true, otherwise false
	 */
	public static boolean isAccountOnline(String username) {
		// NOTE: Check temporary (auth) connections first.
		for (Connection c : auth.getConnections()) {
			TemporaryConnection temp = (TemporaryConnection)c;
			if (temp.Account != null) {
				if (temp.Account.Username.equalsIgnoreCase(username)) {
					return true;
				}
			}
		}
		
		// NOTE: Check world connections next.
		for (Connection c : world.getConnections()) {
			WorldConnection worldC = (WorldConnection)c;
			if (worldC.Account.Username.equalsIgnoreCase(username)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Stop the server and exit cleanly.
	 */
	public synchronized void stop() {
		console.writeMessage(LogType.Server, "Shutting down...");
		Logger.getLogger("server").log(Level.WARNING, "Shutting down...");
		try {
			world.close();
			auth.close();
		} catch (Exception e) {}
		finally {
			System.exit(0);
		}
	}
	
	/**
	 * Get the game-server's gui.
	 * @return gui
	 */
	public static GameServerGUI getServerConsole() {
		return console;
	}
	
	/**
	 * @return auth
	 */
	public static Server getAuthServer() {
		return auth;
	}
	
	/**
	 * @return world
	 */
	public static Server getWorldServer() {
		return world;
	}
	
	public static void main(String[] args) {
		try {
			new GameServer();
		} catch (IOException e) {
			System.out.println("Unable to start the gameserver: ");
			e.printStackTrace();
		}
	}
}
