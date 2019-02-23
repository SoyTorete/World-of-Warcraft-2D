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

import logon.SC_Character;
import wow.server.GameServer;
import wow.server.manager.RealmManager.Realm;
import wow.server.util.Configuration;

/**
 * Handles everything database-related.
 * @author Xolitude
 * @since December 2, 2018
 */
public class DatabaseManager {
	
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
	 * Create a character on an account.
	 * @param name
	 * @param raceId
	 * @param userId
	 * @param realmId
	 */
	public static QueryState CreateCharacter(String name, int raceId, int userId, int realmId, int zoneId) {
		String query = "INSERT INTO user_characters (user_id, realm_id, character_name, x_position, y_position, direction, zone, race) VALUES (?, ?, ?, 0, 0, 0, ?, ?)";
		PreparedStatement statement = null;
		
		if (CharacterExists(name))
			return QueryState.Exists;
		
		createConnection(2);
		
		try {
			statement = characterConnection.prepareStatement(query);
			statement.setInt(1, userId);
			statement.setInt(2, realmId);
			statement.setString(3, name);
			statement.setInt(4, zoneId);
			statement.setInt(5, raceId);
			statement.execute();
			
			return QueryState.Success;
		} catch (SQLException ex) {
			Logger.getLogger("server").log(Level.SEVERE, "A database error occured while trying to create a character: {0}", ex.getMessage());
			return QueryState.ConnectionError;
		} finally {
			closeQuietly(statement);
			closeQuietly();
		}
	}
	
	/**
	 * Does this character exist?
	 * @param name
	 * @return true/false
	 */
	private static boolean CharacterExists(String name) {
		String query = String.format("SELECT * FROM user_characters WHERE character_name='%s'", name);
		Statement statement = null;
		ResultSet set = null;
		
		createConnection(2);
		
		try {
			statement = characterConnection.createStatement();
			set = statement.executeQuery(query);
			
			if (set.next())
				return true;
			else
				return false;
		} catch (SQLException ex) {
			Logger.getLogger("server").log(Level.SEVERE, "Unable to check if character exists: {0}", ex.getMessage());
		} finally {
			closeQuietly(set);
			closeQuietly(statement);
			closeQuietly();
		}
		return true;
	}
	
	/**
	 * Creates an ArrayList of all of the specified user's characters.
	 * @param userId
	 * @param realmId
	 * @return characters
	 */
	public static ArrayList<SC_Character> GetCharactersForUser(int userId, int realmId) {
		ArrayList<SC_Character> characters = new ArrayList<SC_Character>();
		String query = String.format("SELECT character_name, zone, race FROM user_characters WHERE user_id='%s' AND realm_id='%s'", userId, realmId);
		Statement statement = null;
		ResultSet set = null;
		
		createConnection(2);
		
		try {
			statement = characterConnection.createStatement();
			set = statement.executeQuery(query);
			
			while (set.next()) {
				SC_Character character = new SC_Character();
				character.Name = set.getString("character_name");
				character.Zone = set.getInt("zone");
				character.Race = set.getInt("race");
				characters.add(character);
			}
		} catch (SQLException ex) {
			Logger.getLogger("server").log(Level.SEVERE, "Unable to gather characters for userId ({0}): {1}", new Object[] {userId, ex.getMessage()});
		} finally {
			closeQuietly(set);
			closeQuietly(statement);
			closeQuietly();
		}
		return characters;
	}
	
	/**
	 * Deletes the given character from the database.
	 * @param name
	 * @param userId
	 * @param realmId
	 */
	public static void DeleteCharacter(String name, int userId, int realmId) {
		String query = "DELETE FROM user_characters WHERE user_id = ? AND realm_id = ? AND character_name = ?";
		PreparedStatement statement = null;
		
		createConnection(2);
		
		try {
			statement = characterConnection.prepareStatement(query);
			statement.setInt(1, userId);
			statement.setInt(2, realmId);
			statement.setString(3, name);
			statement.execute();
		} catch (SQLException ex) {
			Logger.getLogger("server").log(Level.SEVERE, "Unable to delete character '{0}': {1}", new Object[] {name, ex.getMessage()});
		} finally {
			closeQuietly(statement);
			closeQuietly();
		}
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
