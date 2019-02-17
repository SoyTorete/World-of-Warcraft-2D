package wow.net.handler;

import com.esotericsoftware.kryonet.Connection;

import main.APacket;

/**
 * An interface for handling packets.
 * @author Xolitude
 * @since February 4, 2019
 */
public interface IHandler {

	void handlePacket(Connection connection, APacket packet);
}
