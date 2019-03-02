package wow.manager;

import java.io.File;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import wow.gfx.Animation;
import wow.gfx.Spritesheet;
import wow.net.RealmCharacter;
import wow.net.WorldCharacter;
import wow.net.WorldCharacterMP;
import wow.tiled.TiledMap;

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
		
		private Animation northAnimation;
		private Animation eastAnimation;
		private Animation southAnimation;
		private Animation westAnimation;
		
		RaceType(int id, Spritesheet spritesheet) {
			this.id = id;
			this.spritesheet = spritesheet;
			
			northAnimation = new Animation();
			northAnimation.addFrame(spritesheet.getSubImage(1, 3, 32, 32), 12);
			northAnimation.addFrame(spritesheet.getSubImage(2, 3, 32, 32), 12);
			northAnimation.addFrame(spritesheet.getSubImage(1, 3, 32, 32), 12);
			northAnimation.addFrame(spritesheet.getSubImage(0, 3, 32, 32), 12);
			
			eastAnimation = new Animation();
			eastAnimation.addFrame(spritesheet.getSubImage(1, 2, 32, 32), 12);
			eastAnimation.addFrame(spritesheet.getSubImage(2, 2, 32, 32), 12);
			eastAnimation.addFrame(spritesheet.getSubImage(1, 2, 32, 32), 12);
			eastAnimation.addFrame(spritesheet.getSubImage(0, 2, 32, 32), 12);

			southAnimation = new Animation();
			southAnimation.addFrame(spritesheet.getSubImage(1, 0, 32, 32), 12);
			southAnimation.addFrame(spritesheet.getSubImage(2, 0, 32, 32), 12);
			southAnimation.addFrame(spritesheet.getSubImage(1, 0, 32, 32), 12);
			southAnimation.addFrame(spritesheet.getSubImage(0, 0, 32, 32), 12);

			westAnimation = new Animation();
			westAnimation.addFrame(spritesheet.getSubImage(1, 1, 32, 32), 12);
			westAnimation.addFrame(spritesheet.getSubImage(2, 1, 32, 32), 12);
			westAnimation.addFrame(spritesheet.getSubImage(1, 1, 32, 32), 12);
			westAnimation.addFrame(spritesheet.getSubImage(0, 1, 32, 32), 12);
		}
		
		public int getId() {
			return id;
		}
		
		public Spritesheet getSpritesheet() {
			return spritesheet;
		}

		public Animation getNorthAnimation() {
			return northAnimation;
		}

		public Animation getEastAnimation() {
			return eastAnimation;
		}

		public Animation getSouthAnimation() {
			return southAnimation;
		}

		public Animation getWestAnimation() {
			return westAnimation;
		}
	}
	
	/**
	 * The games different zones.
	 * @author Xolitude
	 * @since December 14, 2018
	 */
	// DEBUG: Sub-zones need to be added.
	public enum Zones {
		TrisfalGlades(1, "Trisfal Glades", "/maps/trisfal_glades.tmx"),
		ElwynnForest(2, "Elwynn Forest", "/maps/elwynn_forest.tmx");
		
		private int id;
		private String name;
		private String path;
		
		Zones(int id, String name, String path) {
			this.id = id;
			this.name = name;
			this.path = path;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public File getFile() {
			try {
				return new File(getClass().getResource(path).toURI());
			} catch (URISyntaxException ex) {
				Logger.getLogger("client").log(Level.SEVERE, "Unable to read zone file: {0}", ex.getMessage());
			}
			return null;
		}
	}
	
	/** The player. **/
	public static ArrayList<RealmCharacter> Characters;
	public static RealmCharacter CharacterInUse;
	public static WorldCharacter Player;
	public static ArrayList<WorldCharacterMP> Players = new ArrayList<WorldCharacterMP>();
	
	public static int RealmID;
	public static String RealmName;
	public static int RealmPort;
	
	public static String AccountName;
	
	public static TiledMap Map;	
	
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
