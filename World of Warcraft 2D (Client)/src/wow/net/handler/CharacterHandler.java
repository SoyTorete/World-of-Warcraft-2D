package wow.net.handler;

import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;

import logon.CS_CharacterList;
import logon.SC_Character;
import logon.SC_CharacterCreate;
import logon.SC_CharacterDelete;
import logon.SC_CharacterList;
import main.APacket;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.manager.NetworkManager.SimplePacketDirection;
import wow.manager.WoWManager.RaceType;
import wow.manager.WoWManager.Zones;
import wow.net.RealmCharacter;
import wow.net.connection.AuthConnection.Auth;

/**
 * Handles character data from the server.
 * @author Xolitude
 * @since Fenruary 17, 2019
 */
public class CharacterHandler implements IHandler {

	private final int ServerError = -1;
	private final int Exists = 1;
	private final int Ok = 0;
	
	@Override
	public void handlePacket(Connection connection, APacket packet) {
		if (packet instanceof SC_CharacterCreate) {
			int code = ((SC_CharacterCreate)packet).Code;
			
			switch (code) {
			case ServerError:
				NetworkManager.AUTH.STATUS = Auth.CharacterCreateServerError;
				break;
			case Exists:
				NetworkManager.AUTH.STATUS = Auth.CharacterCreateExists;
				break;
			case Ok:
				NetworkManager.AUTH.STATUS = Auth.CharacterCreateOk;
				break;
			}
		} else if (packet instanceof SC_CharacterList) {
			ArrayList<SC_Character> characters = ((SC_CharacterList)packet).CharacterList;
			WoWManager.Characters = new ArrayList<RealmCharacter>();
			if (characters.size() > 0) {
				for (SC_Character c : characters) {
					RealmCharacter realmCharacter = new RealmCharacter();
					realmCharacter.Name = c.Name;
					
					for (Zones z : WoWManager.Zones.values()) {
						if (c.Zone == z.getId()) {
							realmCharacter.Zone = z;
						}
					}
					
					for (RaceType r : WoWManager.RaceType.values()) {
						if (c.Race == r.getId()) {
							realmCharacter.Race = r;
						}
					}
					WoWManager.Characters.add(realmCharacter);
				}
			}
			NetworkManager.AUTH.STATUS = Auth.Waiting;
		} else if (packet instanceof SC_CharacterDelete) {
			NetworkManager.AUTH.STATUS = Auth.CharacterList;
			NetworkManager.SendSimplePacket(SimplePacketDirection.Auth, new CS_CharacterList());
		}
	}
}
