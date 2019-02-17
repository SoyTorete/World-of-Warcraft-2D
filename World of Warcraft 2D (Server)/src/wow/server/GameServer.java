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
import wow.server.GameServerGUI.LogType;
import wow.server.connection.TemporaryConnection;
import wow.server.handler.IHandler;
import wow.server.handler.LoginHandler;
import wow.server.handler.RealmHandler;
import wow.server.manager.DatabaseManager;
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
	
	// TODO: (Server) Error checking for character creation, logging in, etc,.
	
	/** The salt to be used in authentication. **/
	public static String SALT = "wow2d_a8.0.0";
	
	/**
	 * The different levels of security an account can be.
	 * @author Xolitude
	 * @since November 30, 2018
	 */
	public enum AccountLevel {
		Player(0),
		Moderator(1),
		Gamemaster(2),
		Administrator(3);
		
		private int level;
		
		AccountLevel(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
	}
	
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
	
	private GameServerGUI console;
	
	private Server auth;
	private Server world;

	private LinkedHashMap<String, IHandler> handlers;
	
	public GameServer() throws IOException {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%4$s: %5$s [%1$tc]%n");
		
		console = new GameServerGUI(this);
		handlers = new LinkedHashMap<String, IHandler>();
		handlers.put("cs_login", new LoginHandler());
		handlers.put("cs_realmlist", new RealmHandler());
		
		/** Give the server time to load the zones. **/
		ZoneManager.Initialize(this);
		do {} while (ZoneManager.GetParseState() == State.Loading);
		
		Configuration.Initialize();
		DatabaseManager.Initialize(this);
		
		auth = new Server() {
			protected Connection newConnection() {
				Logger.getLogger("server").log(Level.INFO, "Auth client connected.");
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
		});
		auth.addListener(new Listener() {
			public void disconnected(Connection connection) {
				String username = "Auth client";
				TemporaryConnection temp = (TemporaryConnection)connection;
				if (temp.Username != null && !temp.Username.isEmpty())
					username = temp.Username;
				Logger.getLogger("server").log(Level.INFO, "{0} got disconnected.", username);
			}
		});

		RealmManager.Initialize(this);
		world = new Server();
		world.start();
		world.bind(RealmManager.GetRealms().get(0).getPort(), RealmManager.GetRealms().get(0).getPort()+1);
		Network.RegisterLib(world.getKryo());
		
		if (auth != null && world != null) {
			Logger.getLogger("server").log(Level.INFO, "AuthServer started on port: {0}", String.valueOf(Configuration.getAuthenticationPort()));
			Logger.getLogger("server").log(Level.INFO, "WorldServer started on ports: {0}:{1}", new Object[] {String.valueOf(RealmManager.GetRealms().get(0).getPort()), String.valueOf(RealmManager.GetRealms().get(0).getPort()+1)});
		} else 
			stop();
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
	public GameServerGUI getServerConsole() {
		return console;
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