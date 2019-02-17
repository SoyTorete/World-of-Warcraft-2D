package wow.net.handler;

import com.esotericsoftware.kryonet.Connection;

import logon.SC_Realmlist;
import main.APacket;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.net.connection.AuthConnection.Auth;

public class RealmHandler implements IHandler {

	@Override
	public void handlePacket(Connection connection, APacket packet) {
		SC_Realmlist sub_packet = (SC_Realmlist)packet;
		
		WoWManager.RealmID = sub_packet.ID;
		WoWManager.RealmName = sub_packet.Name;
		WoWManager.RealmPort = sub_packet.Port;
		
		NetworkManager.AUTH.STATUS = Auth.RealmlistReceived;
	}
}
