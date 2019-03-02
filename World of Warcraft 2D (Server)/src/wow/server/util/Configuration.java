package wow.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
		ACCOUNT_DATA("account_data");
		
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
	
	public static String getAccountDataPath() {
		for (Map.Entry<String, String> keyvalues : settings.entrySet()) {
			if (keyvalues.getKey().equalsIgnoreCase("account_data")) {
				return keyvalues.getValue();
			}
		}
		return "nil";
	}
}
