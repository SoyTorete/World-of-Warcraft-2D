package wow.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import main.APacket;
import world.CS_Movement;
import world.CS_WorldConnection;
import world.SC_MovementUpdate;
import world.SC_World;
import world.SC_WorldConnection;
import world.SC_WorldPosition;
import wow.server.GameServer;
import wow.server.connection.TemporaryConnection;
import wow.server.connection.WorldConnection;

/**
 * Handles initial world request/connection.
 * @author Xolitude
 * @since February 20, 2019
 */
public class WorldHandler implements IHandler {
	
	public enum Direction {
		North(0),
		South(1),
		East(2),
		West(3),
		North_East(4),
		South_East(5),
		South_West(6),
		North_West(7);
		
		private int id;
		
		Direction(int id) {
			this.id = id;
		}
		
		public int getDirection() {
			return id;
		}
	}

	@Override
	public void handlePacket(Server server, Connection connection, APacket packet) {
		WorldConnection worldConnection = (WorldConnection) connection;
		if (packet instanceof CS_WorldConnection) {
			CS_WorldConnection sub_packet = (CS_WorldConnection) packet;
			String accountName = sub_packet.AccountName;
			String characterName = sub_packet.CharacterName;
			
			Connection[] authConnections = GameServer.getAuthServer().getConnections();
			for (Connection c : authConnections) {
				TemporaryConnection tempC = (TemporaryConnection) c;
				if (tempC.Username.equalsIgnoreCase(accountName)) {
					Logger.getLogger("server").log(Level.INFO, "{0} is requesting a world-logon.", tempC.Username);
					worldConnection.Username = tempC.Username;
					worldConnection.UserID = tempC.UserID;
					worldConnection.RealmID = tempC.RealmID;
					tempC.close(); // Close the auth connection.
					
					// DEBUG: Get character position and other data from database.
					worldConnection.X = 50;
					worldConnection.Y = 50;
					worldConnection.Character = characterName;					
				}
			}
			
			// NOTE: Send "ok"
			worldConnection.sendTCP(new SC_WorldConnection());
			
			// NOTE: Send position
			SC_WorldPosition position = new SC_WorldPosition();
			position.X = worldConnection.X;
			position.Y = worldConnection.Y;
			worldConnection.sendTCP(position);
			Logger.getLogger("server").log(Level.INFO, "Sent {0} a spawn packet.", worldConnection.Username);
			
			// NOTE: Send finished signal
			worldConnection.sendTCP(new SC_World());
			
		} else if (packet instanceof CS_Movement) {
			int direction = ((CS_Movement)packet).Direction;
			Direction dEnum = null;
			
			for (Direction d : Direction.values()) {
				if (d.getDirection() == direction) {
					dEnum = d;
				}
			}
			
			int oX = worldConnection.X;
			int oY = worldConnection.Y;
			
			switch (dEnum) {
			case North:
				oY -= 1f;
				break;
			case South:
				oY += 1f;
				break;
			case East:
				oX += 1f;
				break;
			case West:
				oX -= 1f;
				break;
			case North_East:
				oY -= 1f;
				oX += 1f;
				break;
			case North_West:
				oY -= 1f;
				oX -= 1f;
				break;
			case South_East:
				oY += 1f;
				oX += 1f;
				break;
			case South_West:
				oY += 1f;
				oX -= 1f;
				break;
			}
			
			worldConnection.X = oX;
			worldConnection.Y = oY;
			
			SC_MovementUpdate update = new SC_MovementUpdate();
			update.NewX = oX;
			update.NewY = oY;
			
			worldConnection.sendUDP(update);
		}
	}
}
