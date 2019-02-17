package wow.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import logon.SC_Realmlist;
import main.APacket;
import wow.server.connection.TemporaryConnection;
import wow.server.manager.RealmManager;

public class RealmHandler implements IHandler {

	@Override
	public void handlePacket(Server server, Connection connection, APacket packet) {
		TemporaryConnection temp = (TemporaryConnection)connection;
		Logger.getLogger("server").log(Level.INFO, "{0} sent the realmlist packet.", temp.Username);
		
		SC_Realmlist sub_packet = new SC_Realmlist();
		sub_packet.ID = RealmManager.GetRealms().get(0).getId();
		sub_packet.Name = RealmManager.GetRealms().get(0).getName();
		sub_packet.Port = RealmManager.GetRealms().get(0).getPort();
		
		temp.RealmID = sub_packet.ID;
		temp.sendTCP(sub_packet);
	}
}
