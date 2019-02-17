package wow.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles the server-configuration file.
 * @author Xolitude
 * @since December 2, 2018
 */
public class Configuration {

	private static final File file = new File("data/server.conf");
	private static LinkedHashMap<String, String> settings;
	
	public enum Keys {
		AUTH_PORT("auth_port"),
		MOTD("motd"),
		DB_USER("db_user"),
		DB_PASS("db_pass"),
		DB_AUTH("db_auth"),
		DB_WORLD("db_world"),
		DB_CHAR("db_char"),
		DB_HOST("db_host"),
		DB_PORT("db_port"),
		USE_SSL("use_ssl");
		
		private String name;
		
		Keys(String name) {
			this.name = name;
		}
	}
	
	/**
	 * Initialize and load the configuration.
	 */
	public static void Initialize() {
		if (settings == null) {
			settings = new LinkedHashMap<String, String>();
			load();
		}		
	}
	
	/**
	 * Load each individual setting if we have a definition for it.
	 */
	private static void load() {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if (!line.contains("="))
					continue;
				String[] split = line.split("=");
				String key = split[0];
				String value = split[1];
				
				for (Keys k : Keys.values()) {
					if (k.name.equalsIgnoreCase(key)) {
						settings.put(k.name, value);
					}
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
	}
	
	/**
	 * 
	 * @return the authentication port.
	 */
	public static int getAuthenticationPort() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("auth_port")) {
				return Integer.valueOf(keyvalues.getValue());
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @return the server's message of the day.
	 */
	public static String getMessageOfTheDay() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("motd")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the username to use for the database.
	 */
	public static String getDatabaseUsername() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_user")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the password to use for the database.
	 */
	public static String getDatabasePassword() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_pass")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the name of the authentication database.
	 */
	public static String getAuthDatabase() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_auth")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the name of the character database.
	 */
	public static String getCharacterDatabase() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_char")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the name of the world database.
	 */
	public static String getWorldDatabase() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_world")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the host of the database.
	 */
	public static String getDatabaseHost() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_host")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
	
	/**
	 * 
	 * @return the port of the database.
	 */
	public static int getDatabasePort() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("db_port")) {
				return Integer.valueOf(keyvalues.getValue());
			}
		}
		return -1;
	}
	
	/**
	 * Should we use SSL?
	 * @return true, otherwise false.
	 */
	public static String shouldUseSSL() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("use_ssl")) {
				return keyvalues.getValue();
			}
		}
		return "false";
	}
}
