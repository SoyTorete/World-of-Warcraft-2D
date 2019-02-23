package wow.net.handler;

import com.esotericsoftware.kryonet.Connection;

import main.APacket;
import world.SC_MovementUpdate;
import world.SC_World;
import world.SC_WorldConnection;
import world.SC_WorldPosition;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;
import wow.net.WorldCharacter;
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
			NetworkManager.WORLD.STATUS = World.WorldOk;
		} else if (packet instanceof SC_WorldPosition) {
			SC_WorldPosition position = (SC_WorldPosition) packet;
			WoWManager.Player = new WorldCharacter(WoWManager.CharacterInUse.Race, WoWManager.CharacterInUse.Name);
			WoWManager.Player.spawn(position.X, position.Y);
		} else if (packet instanceof SC_World) {
			NetworkManager.WORLD.STATUS = World.World;
		} else if (packet instanceof SC_MovementUpdate) {
			WoWManager.Player.setX(((SC_MovementUpdate)packet).NewX);
			WoWManager.Player.setY(((SC_MovementUpdate)packet).NewY);
		}
	}
}
