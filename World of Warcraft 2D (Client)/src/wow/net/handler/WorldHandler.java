package wow.net.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;

import main.APacket;
import world.SC_MovementToAll;
import world.SC_MovementUpdate;
import world.SC_Player;
import world.SC_PlayerConnect;
import world.SC_PlayerDisconnect;
import world.SC_World;
import world.SC_WorldConnection;
import world.SC_WorldPosition;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.manager.WoWManager.RaceType;
import wow.net.WorldCharacter;
import wow.net.WorldCharacterMP;
import wow.net.connection.AuthConnection.Auth;
import wow.net.connection.WorldConnection.World;

/**
 * Handles world-data.
 * @author Xolitude
 * @since February 22, 2019
 */
public class WorldHandler implements IHandler {
	
	@Override
	public void handlePacket(Connection connection, APacket packet) {
		if (packet instanceof SC_WorldConnection) {
			NetworkManager.AUTH.STATUS = Auth.World;
		} else if (packet instanceof SC_WorldPosition) {
			handleSpawn((SC_WorldPosition)packet);
		} else if (packet instanceof SC_Player) {
			handlePlayerList((SC_Player)packet);
		} else if (packet instanceof SC_World) {
			NetworkManager.WORLD.STATUS = World.World;
		} else if (packet instanceof SC_MovementUpdate) {
			WoWManager.Player.setX(((SC_MovementUpdate)packet).NewX);
			WoWManager.Player.setY(((SC_MovementUpdate)packet).NewY);
		} else if (packet instanceof SC_MovementToAll) {
			handlePlayerPositionUpdate((SC_MovementToAll)packet);
		} else if (packet instanceof SC_PlayerConnect) {
			handleNewConnection((SC_PlayerConnect)packet);
		} else if (packet instanceof SC_PlayerDisconnect) {
			handleDisconnection(((SC_PlayerDisconnect)packet).Name);
		}
	}
	
	private void handleSpawn(SC_WorldPosition packet) {
		WoWManager.Player = new WorldCharacter(WoWManager.CharacterInUse.Race, WoWManager.CharacterInUse.Name);
		WoWManager.Player.spawn(packet.X, packet.Y);
	}
	
	private void handlePlayerList(SC_Player packet) {
		String name = packet.Name;
		int raceId = packet.RaceID;
		float x = packet.X;
		float y = packet.Y;
		int level = packet.Level;
		RaceType raceType = null;
		
		for (RaceType race : RaceType.values()) {
			if (race.getId() == raceId) {
				raceType = race;
			}
		}
		
		Logger.getLogger("client").log(Level.INFO, "Adding new player: {0}", name);
		WorldCharacterMP newPlayer = new WorldCharacterMP(raceType, name);
		newPlayer.setX(x);
		newPlayer.setY(y);
		WoWManager.Players.add(newPlayer);
	}
	
	private void handlePlayerPositionUpdate(SC_MovementToAll packet) {
		for (WorldCharacterMP player : WoWManager.Players) {
			if (player.getName().equalsIgnoreCase(packet.Name)) {
				player.setDirection(packet.Direction);
				player.setX(packet.NewX);
				player.setY(packet.NewY);
				player.setMoving(packet.IsMoving);
			}
		}
	}
	
	private void handleNewConnection(SC_PlayerConnect packet) {
		RaceType raceType = null;
		
		for (RaceType race : RaceType.values()) {
			if (race.getId() == packet.RaceID) {
				raceType = race;
			}
		}
		
		Logger.getLogger("client").log(Level.INFO, "Adding new player to world: {0}", packet.Name);
		WorldCharacterMP newPlayer = new WorldCharacterMP(raceType, packet.Name);
		newPlayer.setX(packet.X);
		newPlayer.setY(packet.Y);
		WoWManager.Players.add(newPlayer);
	}
	
	private void handleDisconnection(String name) {
		WoWManager.Players.removeIf(n -> n.getName().equalsIgnoreCase(name));
		Logger.getLogger("client").log(Level.INFO, "{0} has left the world.", name);
	}
}
