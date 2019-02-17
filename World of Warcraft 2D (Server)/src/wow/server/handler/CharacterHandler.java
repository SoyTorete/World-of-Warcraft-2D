package wow.server.handler;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import logon.CS_CharacterCreate;
import logon.CS_CharacterDelete;
import logon.CS_CharacterList;
import logon.SC_Character;
import logon.SC_CharacterCreate;
import logon.SC_CharacterDelete;
import logon.SC_CharacterList;
import main.APacket;
import wow.server.connection.TemporaryConnection;
import wow.server.manager.DatabaseManager;
import wow.server.manager.DatabaseManager.QueryState;
import wow.server.manager.ZoneManager;

/**
 * Handles character-type requests.
 * @author Xolitude
 * @since February 17, 2019
 */
public class CharacterHandler implements IHandler {
	
	private final int ServerError = -1;
	private final int Exists = 1;
	private final int Ok = 0;

	@Override
	public void handlePacket(Server server, Connection connection, APacket packet) {
		TemporaryConnection temp = (TemporaryConnection) connection;
		if (packet instanceof CS_CharacterCreate) {
			CS_CharacterCreate sub_packet = (CS_CharacterCreate) packet;
			
			String name = sub_packet.Name;
			int raceId = sub_packet.RaceID;
			int userId = temp.UserID;
			int realmId = temp.RealmID;
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			int zoneId = ZoneManager.GetZoneSpawnForRace(raceId);
			
			QueryState state = DatabaseManager.CreateCharacter(name, raceId, userId, realmId, zoneId);
			SC_CharacterCreate packet_fwd = new SC_CharacterCreate();
			switch (state) {
			case Success:
				packet_fwd.Code = Ok;
				Logger.getLogger("server").log(Level.INFO, "{0} created character: (Name:{1};Race:{2})", new Object[] {temp.Username, name, raceId});
				break;
			case Exists:
				packet_fwd.Code = Exists;
				break;
			case ConnectionError:
				packet_fwd.Code = ServerError;
				break;
			}
			temp.sendTCP(packet_fwd);
		} else if (packet instanceof CS_CharacterList) {
			Logger.getLogger("server").log(Level.INFO, "{0} sent the character-list packet.", temp.Username);
			ArrayList<SC_Character> characters = DatabaseManager.GetCharactersForUser(temp.UserID, temp.RealmID);
			SC_CharacterList sub_packet = new SC_CharacterList();
			sub_packet.CharacterList = characters;
			temp.sendTCP(sub_packet);
		} else if (packet instanceof CS_CharacterDelete) {
			String name = ((CS_CharacterDelete)packet).Name;
			DatabaseManager.DeleteCharacter(name, temp.UserID, temp.RealmID);
			Logger.getLogger("server").log(Level.INFO, "{0} deleted character: {1}", new Object[] {temp.Username, name});
			
			temp.sendTCP(new SC_CharacterDelete());
		}
	}
}
