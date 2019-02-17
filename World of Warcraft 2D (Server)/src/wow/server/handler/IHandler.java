package wow.server.handler;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import main.APacket;

/**
 * An interface for packet handlers.
 * @author Xolitude
 * @since February 4, 2018
 */
public interface IHandler {
    void handlePacket(Server server, Connection connection, APacket packet);
}
