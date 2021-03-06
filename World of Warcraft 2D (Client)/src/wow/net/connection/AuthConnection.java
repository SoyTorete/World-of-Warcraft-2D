package wow.net.connection;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import logon.CS_Login;
import main.APacket;
import main.Network;
import world.SC_WorldConnection;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.net.handler.IHandler;

/**
 * Handles the auth connection.
 * @author Xolitude
 * @since February 4, 2019
 */
public class AuthConnection {
	
	public enum Auth {
		Waiting,
		Disconnected,
		Credentials,
		Connecting,
		ConnectingFailed,
		Authenticating,
		AuthenticatingUnk,
		AuthenticatingIncorrect,
		AuthenticatingOk,
		AlreadyOnline,
		RealmlistReceived,
		CharacterCreateOk,
		CharacterCreateExists,
		CharacterCreateServerError,
		CharacterList,
		World
	}
	
	private Client client;
	public static Auth STATUS = Auth.Waiting;

	public AuthConnection(String username, String password) {
		if (username.isEmpty() || password.isEmpty()) {
			STATUS = Auth.Credentials;
			return;
		}
		if (client != null)
			return;
		client = new Client();
		Network.RegisterLib(client.getKryo());
		new Thread("auth") {
			public void run() {
				STATUS = Auth.Connecting;
				client.start();
				try {
					client.connect(5000,"127.0.0.1", 6770);
					STATUS = Auth.Authenticating;
					CS_Login packet = new CS_Login();
					packet.Username = username;
					packet.Password = WoWManager.SHA256Hash(password);
					client.sendTCP(packet);
				} catch (IOException ex) {
					STATUS = Auth.ConnectingFailed;
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
			
			public void disconnected(Connection connection) {
				connection.close();
			}
		});
	}
	
	public Client getClient() {
		return client;
	}
}
