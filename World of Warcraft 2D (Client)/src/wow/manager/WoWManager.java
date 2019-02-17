package wow.manager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import wow.gfx.Spritesheet;
import wow.net.Player;

/**
 * Handles general game-data.
 * @author Xolitude
 * @since December 3, 2018
 */
public class WoWManager {
	
	/**
	 * The games different race-types.
	 * @author Xolitude
	 * @since December 5, 2018
	 */
	public enum RaceType {
		Undead(1, new Spritesheet("/sprites/player/Forsaken.png")),
		Human(2, new Spritesheet("/sprites/player/Human.png"));
		
		private int id;
		private Spritesheet spritesheet;
		
		RaceType(int id, Spritesheet spritesheet) {
			this.id = id;
			this.spritesheet = spritesheet;
		}
		
		public int getId() {
			return id;
		}
		
		public Spritesheet getSpritesheet() {
			return spritesheet;
		}
	}
	
	/**
	 * The games different zones.
	 * @author Xolitude
	 * @since December 14, 2018
	 */
	public enum Zones {
		TrisfalGlades(1, "Trisfal Glades"),
		ElwynnForest(2, "Elwynn Forest");
		
		private int id;
		private String name;
		
		Zones(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/** The player. **/
	public static Player Player;
	
	public static int RealmID;
	public static String RealmName;
	public static int RealmPort;
	
	/** 
	 * Get the name of a zone based on the given id.
	 * @param id
	 * @return the zone name or 'n/a' if one does not exist
	 */
	public static String GetZoneName(int id) {
		for (Zones zone : Zones.values()) {
			if (zone.id == id)
				return zone.name;
		}
		return "n/a";
	}
	
	/**
	 * Get the race-type based on the given id.
	 * @param id
	 * @return the race-type
	 */
	public static RaceType GetRaceType(int id) {
		for (RaceType race : RaceType.values()) {
			if (race.id == id) {
				return race;
			}
		}
		return null;
	}
	
	/**
	 * Create a hash-version of a plain-text password.
	 * @param password
	 * @return hashedPassword
	 */
	public static String SHA256Hash(String password) {
		String hashedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(password.getBytes());
			hashedPassword = GraphicsManager.BytesToHex(hash);
		} catch (NoSuchAlgorithmException e) {}
		return hashedPassword;
	}
}
