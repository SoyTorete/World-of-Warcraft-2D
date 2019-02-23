package wow.net.connection;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import main.APacket;
import main.Network;
import world.CS_WorldConnection;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.net.RealmCharacter;
import wow.net.handler.IHandler;

public class WorldConnection {

	public enum World {
		Waiting, 
		Connecting,
		ConnectingFailed,
		WorldOk,
		CharacterPosition,
		World,
	}
	
	private Client client;
	public static World STATUS = World.Waiting;
	
	public WorldConnection() {
		if (client != null)
			return;
		client = new Client();
		Network.RegisterLib(client.getKryo());
		new Thread("world") {
			public void run() {
				STATUS = World.Connecting;
				client.start();
				try {
					client.connect(5000, "127.0.0.1", WoWManager.RealmPort, WoWManager.RealmPort+1);
					CS_WorldConnection packet = new CS_WorldConnection();
					packet.AccountName = WoWManager.AccountName;
					packet.CharacterName = WoWManager.CharacterInUse.Name;
					client.sendTCP(packet);
				} catch (IOException ex) {
					STATUS = World.ConnectingFailed;
					Logger.getLogger("client").log(Level.WARNING, "{0}", ex.getMessage());
				}
			}
		}.start();
		
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				for (Map.Entry<String, IHandler> set : NetworkManager.HANDLERS.entrySet()) {
					if (set.getKey().equalsIgnoreCase(object.toString())) {
						set.getValue().handlePacket(client, (APacket)object);
					}
				}
			}
		});
	}
	
	public Client getClient() {
		return client;
	}
}
