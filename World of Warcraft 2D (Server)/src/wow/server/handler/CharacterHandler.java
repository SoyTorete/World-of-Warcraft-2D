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
import wow.server.Player;
import wow.server.connection.TemporaryConnection;
import wow.server.manager.AccountManager;
import wow.server.manager.AccountManager.QueueState;

/**
 * Handles character-type requests.
 * @author Xolitude
 * @since February 17, 2019
 */
public class CharacterHandler implements IHandler {
	
	private final int ServerError = -1;
	private final int Exists = 1;
	private final int Ok = 0;
	
	private void handleCharacterCreate(TemporaryConnection connection, CS_CharacterCreate packet) {
		String name = packet.Name;
		int raceId = packet.RaceID;
		int realmId = connection.RealmID;
		
		QueueState state = AccountManager.CreateCharacter(connection.Account, name, raceId, realmId);
		SC_CharacterCreate packet_fwd = new SC_CharacterCreate();
		switch (state) {
		case Success:
			packet_fwd.Code = Ok;
			break;
		case Failed:
			packet_fwd.Code = Exists;
			break;
		case Error:
			packet_fwd.Code = ServerError;
			break;
		}
		connection.sendTCP(packet_fwd);
	}
	
	private void handleCharacterList(TemporaryConnection connection) {
		Logger.getLogger("server").log(Level.INFO, "{0} sent the character-list packet.", connection.Account.Username);
		
		ArrayList<SC_Character> characters = new ArrayList<SC_Character>();
		for (Player player : connection.Account.Characters) {
			if (player.RealmID == connection.RealmID) {
				SC_Character character = new SC_Character();
				character.Name = player.Name;
				character.Zone = player.ZoneID;
				character.Race = player.RaceID;
				characters.add(character);
			}
		}
		SC_CharacterList packet = new SC_CharacterList();
		packet.CharacterList = characters;
		connection.sendTCP(packet);
	}
	
	private void handleCharacterDelete(TemporaryConnection connection, String characterName) {
		// TODO: handle delete state.
		AccountManager.DeleteCharacter(connection.Account, characterName);
		connection.sendTCP(new SC_CharacterDelete());
	}

	@Override
	public void handlePacket(Server server, Connection connection, APacket packet) {
		TemporaryConnection temp = (TemporaryConnection) connection;
		if (packet instanceof CS_CharacterCreate) {
			handleCharacterCreate(temp, (CS_CharacterCreate)packet);
		} else if (packet instanceof CS_CharacterList) {
			handleCharacterList(temp);
		} else if (packet instanceof CS_CharacterDelete) {
			handleCharacterDelete(temp, ((CS_CharacterDelete)packet).Name);
		}
	}
}
