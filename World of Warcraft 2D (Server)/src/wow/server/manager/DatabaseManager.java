package wow.server.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import wow.server.GameServer;
import wow.server.manager.RealmManager.Realm;
import wow.server.util.Configuration;
import wow.server.world.Vector2;

/**
 * Handles everything database-related.
 * @author Xolitude
 * @since December 2, 2018
 */
public class DatabaseManager {

	// TODO: (Server) Send "Server Error" message to the client if connections wont open, etc.
	// TODO: (Server) Use Database Pooling?
	
	private static GameServer gameServer;
	
	public enum QueryState {
		Success,
		Exists,
		ConnectionError
	}

	private static Connection authenticationConnection;
	private static Connection worldConnection;
	private static Connection characterConnection;
	
	private static Properties Properties;

	private static String authenticationURL = "jdbc:mysql://%s:%s/%s";
	private static String worldURL = "jdbc:mysql://%s:%s/%s";
	private static String characterURL = "jdbc:mysql://%s:%s/%s";

	static {
		Properties = new Properties();
		Properties.setProperty("User", Configuration.getDatabaseUsername());
		Properties.setProperty("Password", Configuration.getDatabasePassword());
		Properties.setProperty("useSSL", Configuration.shouldUseSSL());
		
		authenticationURL = String.format(authenticationURL, Configuration.getDatabaseHost(), Configuration.getDatabasePort(), Configuration.getAuthDatabase());
		worldURL = String.format(worldURL, Configuration.getDatabaseHost(), Configuration.getDatabasePort(), Configuration.getWorldDatabase());
		characterURL = String.format(characterURL, Configuration.getDatabaseHost(), Configuration.getDatabasePort(), Configuration.getCharacterDatabase());
	}
	
	/**
	 * Test database connectivity.
	 */
	public static void Initialize(GameServer server) {
		gameServer = server;
		createConnection(0);
		createConnection(1);
		createConnection(2);
	}
	
	/**
	 * Attempt to create an account with the given name, password and salt.
	 * @param username
	 * @param hashedPassword
	 * @param salt
	 * @return a status
	 */
	public static QueryState CreateAccount(String username, String hashedPassword, String salt) {
		String query = "INSERT INTO auth_users (username, password, salt, user_level) VALUES (?, ?, ?, 0)";
		PreparedStatement statement = null;
		
		if (AccountExists(username) != null)
			return QueryState.Exists;
		
		createConnection(0);
		
		try {
			statement = authenticationConnection.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, hashedPassword);
			statement.setString(3, salt);
			statement.execute();
			
			return QueryState.Success;
		} catch (SQLException ex) {
			return QueryState.ConnectionError;
		} finally {
			closeQuietly(statement);
			closeQuietly();
		}
	}
	
	/**
	 * Does this account exist?
	 * @param username
	 * @return account data if so, otherwise return null
	 */
	public static String[] AccountExists(String username) {
		String query = String.format("SELECT user_id, password, salt FROM auth_users WHERE username='%s'", username);
		String[] data = null;
		Statement statement = null;
		ResultSet set = null;
		
		createConnection(0);
		
		try {			
			statement = authenticationConnection.createStatement();
			set = statement.executeQuery(query);
			
			if (set.next()) {
				data = new String[] { set.getString("password"), set.getString("salt"), String.valueOf(set.getInt("user_id")) };
				return data;
			} else 
				return null;
		} catch (SQLException ex) {
			System.err.println(String.format("Unable to check if account exists: %s", ex.getMessage()));
		} finally {
			closeQuietly(set);
			closeQuietly(statement);
			closeQuietly();
		}
		return null;
	}
	
	/**
	 * Fetch all realms from the database.
	 * @return a list of realms.
	 */
	public static ArrayList<Realm> FetchRealms() {
		ArrayList<Realm> realms = new ArrayList<Realm>();
		String query = "SELECT * FROM auth_realms";
		Statement statement = null;
		ResultSet set = null;
		
		createConnection(0);
		
		try {
			statement = authenticationConnection.createStatement();
			set = statement.executeQuery(query);
			
			while (set.next()) {
				int id = set.getInt("realm_id");
				String name = set.getString("realm_name");
				int port = set.getInt("realm_port");
				realms.add(new Realm(id, name, port));
			}
		} catch (SQLException ex) {
			System.err.println(String.format("Unable to fetch the realms: %s", ex.getMessage()));
		} finally {
			closeQuietly(set);
			closeQuietly(statement);
			closeQuietly();
		}
		return realms;
	}
	
	/**
	 * Create and open a connection.
	 * @param type
	 * @throws SQLException 
	 */
	private static void createConnection(int type) {
		try {
			switch (type) {
			case 0: // Auth
				authenticationConnection = DriverManager.getConnection(authenticationURL, Properties);
				break;
			case 1: // World
				worldConnection = DriverManager.getConnection(worldURL, Properties);
				break;
			case 2: // Character
				characterConnection = DriverManager.getConnection(characterURL, Properties);
				break;
			}
		} catch (SQLException ex) {
			System.err.println(String.format("Unable to open an SQL connection: %s", ex.getMessage()));
			gameServer.stop();
		}
	}
	
	/**
	 * Closes the 'resultSet' object quietly.
	 * @param resultSet
	 */
	private static void closeQuietly(ResultSet resultSet) {
		if (resultSet != null)
			try { resultSet.close(); } catch (Exception e) {}
	}
	
	/**
	 * Closes the 'sqlStatement' object quietly.
	 * @param sqlStatement
	 */
	private static void closeQuietly(Statement sqlStatement) {
		if (sqlStatement != null)
			try { sqlStatement.close(); } catch (Exception e) {}
	}
	
	/**
	 * Closes the connection objects quietly.
	 */
	private static void closeQuietly() {
		if (authenticationConnection != null) 
			try { authenticationConnection.close(); } catch (Exception e) {}
		if (worldConnection != null)
			try { worldConnection.close(); } catch (Exception e) {}
		if (characterConnection != null) 
			try { characterConnection.close(); } catch (Exception e) {}
	}
}
