package wow.manager;

import java.util.LinkedHashMap;

import logon.CS_CharacterCreate;
import logon.CS_CharacterDelete;
import main.APacket;
import wow.net.connection.AuthConnection;
import wow.net.handler.CharacterHandler;
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
		HANDLERS.put("sc_character_create", new CharacterHandler());
		HANDLERS.put("sc_character_list", new CharacterHandler());
		HANDLERS.put("sc_character_delete", new CharacterHandler());
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
	
	public static void SendCharacterCreationPacket(String name, int raceId) {
		CS_CharacterCreate packet = new CS_CharacterCreate();
		packet.Name = name;
		packet.RaceID = raceId;
		
		AUTH.getClient().sendTCP(packet);
	}
	
	public static void SendCharacterDeletionPacket(String name) {
		CS_CharacterDelete packet = new CS_CharacterDelete();
		packet.Name = name;
		AUTH.getClient().sendTCP(packet);
	}
}
