package wow.manager;

import java.util.LinkedHashMap;

import main.APacket;
import wow.net.connection.AuthConnection;
import wow.net.handler.IHandler;
import wow.net.handler.LoginHandler;
import wow.net.handler.RealmHandler;

/**
 * Handles net-data.
 * @author Xolitude
 * @since February 4, 2019
 */
public class NetworkManager {
	
	public enum SimplePacketDirection {
		Auth,
		World
	}

	public static AuthConnection AUTH;
	public static LinkedHashMap<String, IHandler> HANDLERS;
	
	public static void Initialize() {
		HANDLERS = new LinkedHashMap<String, IHandler>();
		HANDLERS.put("sc_login", new LoginHandler());
		HANDLERS.put("sc_realmlist", new RealmHandler());
	}
	
	public static void ConnectToAuth(String username, String password) {
		AUTH = new AuthConnection(username, password);
	}
	
	public static void SendSimplePacket(SimplePacketDirection direction, APacket packet) {
		switch (direction) {
		case Auth:
			AUTH.getClient().sendTCP(packet);
			break;
		case World:
			break;
		}
	}
}
