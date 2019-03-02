package wow.server.handler;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import main.APacket;
import world.CS_Movement;
import world.CS_WorldConnection;
import world.SC_MovementToAll;
import world.SC_MovementUpdate;
import world.SC_PlayerConnect;
import world.SC_Player;
import world.SC_PlayerList;
import world.SC_World;
import world.SC_WorldConnection;
import world.SC_WorldPosition;
import wow.server.GameServer;
import wow.server.Player;
import wow.server.connection.TemporaryConnection;
import wow.server.connection.WorldConnection;
import wow.server.manager.ZoneManager;

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
			handleNewConnection(server, worldConnection, (CS_WorldConnection)packet);
		} else if (packet instanceof CS_Movement) {
			handleMovement(server, worldConnection, (CS_Movement)packet);
		}
	}
	
	private void handleNewConnection(Server server, WorldConnection connection, CS_WorldConnection packet) {
		String accountName = packet.AccountName;
		String characterName = packet.CharacterName;
		
		Connection[] authConnections = GameServer.getAuthServer().getConnections();
		for (Connection c : authConnections) {
			TemporaryConnection tempC = (TemporaryConnection) c;
			if (tempC.Account.Username.equalsIgnoreCase(accountName)) {
				Logger.getLogger("server").log(Level.INFO, "{0} is requesting a world-logon.", tempC.Account.Username);
				connection.Account = tempC.Account;
				connection.Account.RealmID = tempC.RealmID;
				
				for (Player player : connection.Account.Characters) {
					if (player.Name.equalsIgnoreCase(characterName)) {
						connection.Account.OnlinePlayer = player;
					}
				}
				tempC.close(); // Close the auth connection.
			}
		}
		connection.sendTCP(new SC_WorldConnection());
		
		SC_WorldPosition position = new SC_WorldPosition();
		position.X = connection.Account.OnlinePlayer.X;
		position.Y = connection.Account.OnlinePlayer.Y;
		connection.sendTCP(position);
		Logger.getLogger("server").log(Level.INFO, "Sent {0} a spawn packet.", connection.Account.OnlinePlayer.Name);
		
		ArrayList<Player> players = ZoneManager.GetPlayersInZone(connection.Account.OnlinePlayer.ZoneID);
		for (Player player : players) {
			SC_Player player_packet = new SC_Player();
			player_packet.Name = player.Name;
			player_packet.RaceID = player.RaceID;
			player_packet.X = player.X;
			player_packet.Y = player.Y;
			player_packet.Level = player.Level;
			connection.sendTCP(player_packet);
		}
		connection.sendTCP(new SC_PlayerList()); // NOTE: Send player-list complete
		connection.sendTCP(new SC_World()); // NOTE: Send finished signal
		ZoneManager.AddPlayerToZone(connection.Account.OnlinePlayer, connection.Account.OnlinePlayer.ZoneID);
		
		// NOTE: Send new player to all players
		SC_PlayerConnect player_packet = new SC_PlayerConnect();
		player_packet.Name = connection.Account.OnlinePlayer.Name;
		player_packet.RaceID = connection.Account.OnlinePlayer.RaceID;
		player_packet.X = connection.Account.OnlinePlayer.X;
		player_packet.Y = connection.Account.OnlinePlayer.Y;
		player_packet.Level = connection.Account.OnlinePlayer.Level;
		Connection[] connections = server.getConnections();
		for (Connection c : connections) {
			WorldConnection worldC = (WorldConnection) c;
			if (!worldC.Account.Username.equalsIgnoreCase(connection.Account.Username)) {
				if (worldC.Account.OnlinePlayer.ZoneID == connection.Account.OnlinePlayer.ZoneID)
					worldC.sendTCP(player_packet);
			}
		}		
	}
	
	private void handleMovement(Server server, WorldConnection connection, CS_Movement packet) {
		int direction = packet.Direction;
		boolean isMoving = packet.IsMoving;
		Direction dEnum = null;
		
		for (Direction d : Direction.values()) {
			if (d.getDirection() == direction) {
				dEnum = d;
			}
		}
		
		float oX = connection.Account.OnlinePlayer.X;
		float oY = connection.Account.OnlinePlayer.Y;
		
		// DEBUG: Handle character speed client side and fact-check server side?
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
		
		connection.Account.OnlinePlayer.X = oX;
		connection.Account.OnlinePlayer.Y = oY;
		connection.Account.OnlinePlayer.Direction = direction;
		
		// NOTE: Send the player their new position.
		SC_MovementUpdate update = new SC_MovementUpdate();
		update.NewX = oX;
		update.NewY = oY;
		connection.sendUDP(update);
		
		// NOTE: Send movement update to all.
		SC_MovementToAll moveAll = new SC_MovementToAll();
		moveAll.Name = connection.Account.OnlinePlayer.Name;
		moveAll.NewX = connection.Account.OnlinePlayer.X;
		moveAll.NewY = connection.Account.OnlinePlayer.Y;
		moveAll.Direction = connection.Account.OnlinePlayer.Direction;
		moveAll.IsMoving = isMoving;
		
		Connection[] connections = server.getConnections();
		for (Connection c : connections) {
			WorldConnection worldC = (WorldConnection) c;
			if (!worldC.Account.Username.equalsIgnoreCase(connection.Account.Username)) {
				worldC.sendUDP(moveAll);
			}
		}
	}
}
