package wow.server.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import wow.server.Account;
import wow.server.GameServer;
import wow.server.GameServerGUI.LogType;
import wow.server.Player;
import wow.server.util.Configuration;

/**
 * Handles reading/writing account data.
 * @author Xolitude
 * @since February 26, 2019
 */
public class AccountManager {
	
	/**
	 * The different levels of security an account can be.
	 * @author Xolitude
	 * @since November 30, 2018
	 */
	public enum AccountLevel {
		Player(0),
		Moderator(1),
		Gamemaster(2),
		Administrator(3);
		
		private int level;
		
		AccountLevel(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
	}
	
	public enum QueueState {
		Error,
		Failed,
		Success
	}
	
	private static ArrayList<Account> Accounts = new ArrayList<Account>();
	
	public static void Initialize() {
		File[] accounts = new File(Configuration.getAccountDataPath()).listFiles();
		if (accounts != null) {
			for (File account : accounts) {
				if (account.isDirectory()) {
					String name = account.getName();
					File accountFile = new File(account.getAbsolutePath()+"/"+name+".data");
					if (accountFile.exists()) {
						LoadAccount(accountFile);
					}
				}
			}
		}
		Logger.getLogger("server").log(Level.INFO, "Loaded {0} accounts.", Accounts.size());
		GameServer.getServerConsole().writeMessage(LogType.Logon, String.format("Loaded %s accounts.", Accounts.size()));
	}
	
	/**
	 * Load an individual account.
	 * @param dataFile
	 */
	private static void LoadAccount(File dataFile) {
		try (DataInputStream reader = new DataInputStream(new FileInputStream(dataFile))) {
			Account account = new Account();
			account.Username = reader.readUTF();
			account.HashedPassword = reader.readUTF();
			account.Salt = reader.readUTF();
			int level = reader.readInt();
			for (AccountLevel security : AccountLevel.values()) {
				if (security.level == level) {
					account.Security = security;
				}
			}
			if (account.Security == null)
				Logger.getLogger("server").log(Level.WARNING, "{0} does not have a security level!");
			
			LoadCharacters(account);
			Accounts.add(account);
		} catch (IOException ex) {
			Logger.getLogger("server").log(Level.SEVERE, "Failed to load account; {0}", ex.getMessage());
		}
	}
	
	/**
	 * Load all characters on a given account.
	 * @param account
	 */
	private static void LoadCharacters(Account account) {
		File characterFolder = new File(Configuration.getAccountDataPath()+"/"+account.Username+"/Characters");
		if (characterFolder.exists()) {
			File[] characters = characterFolder.listFiles();
			if (characters.length > 0) {
				for (File dataFile : characters) {
					LoadCharacter(dataFile, account);
				}
			}
			Logger.getLogger("server").log(Level.INFO, "Loaded {0} characters on account {1}.", new Object[] { account.Characters.size(), account.Username });
		}
	}
	
	/**
	 * Load an individual character.
	 * @param dataFile
	 * @param account
	 */
	private static void LoadCharacter(File dataFile, Account account) {
		try (DataInputStream reader = new DataInputStream(new FileInputStream(dataFile))) {
			Player player = new Player();
			player.Name = reader.readUTF();
			player.ZoneID = reader.readByte();
			player.RaceID = reader.readByte();
			player.RealmID = reader.readByte();
			player.X = reader.readFloat();
			player.Y = reader.readFloat();
			player.Direction = reader.readByte();
			player.Level = reader.readByte();
			player.XP = reader.readByte();
			
			account.Characters.add(player);
		} catch (IOException ex) {
			Logger.getLogger("server").log(Level.SEVERE, "Failed to load character; {0}", ex.getMessage());
		}
	}
	
	/**
	 * Create an account.
	 * @param username
	 * @param hash
	 * @param salt
	 */
	public static QueueState CreateAccount(String username, String hash, String salt) {
		File folder = new File(Configuration.getAccountDataPath()+"/"+username);
		if (folder.mkdirs()) {
			File account = new File(folder+"/"+username+".data");
			try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(account))) {
				writer.writeUTF(username);
				writer.writeUTF(hash);
				writer.writeUTF(salt);
				writer.writeInt(AccountLevel.Player.getLevel());
				
				GameServer.getServerConsole().writeMessage(LogType.Logon, "Account created: "+username);
				Account newAccount = new Account();
				newAccount.Username = username;
				newAccount.HashedPassword = hash;
				newAccount.Salt = salt;
				Accounts.add(newAccount);
				return QueueState.Success;
			} catch (IOException ex) {
				GameServer.getServerConsole().writeMessage(LogType.Logon, "Unable to create account. See console log.");
				Logger.getLogger("server").log(Level.SEVERE, "Failed to create account '{0}'; {1}", new Object[] { username, ex.getMessage() });
				return QueueState.Error;
			}
		} else {
			GameServer.getServerConsole().writeMessage(LogType.Logon, "Account '"+username+"' already exists.");
			return QueueState.Failed;
		}
	}
	
	/**
	 * Create a character under the given account.
	 * @param account
	 * @param characterName
	 * @param raceId
	 * @param realmId
	 * @return a state signaling the status of the attempt
	 */
	public static QueueState CreateCharacter(Account account, String characterName, int raceId, int realmId) {
		File accountFolder = new File(Configuration.getAccountDataPath()+"/"+account.Username);
		if (accountFolder.isDirectory() && accountFolder.exists()) {
			File charactersFolder = new File(accountFolder.getAbsolutePath()+"/Characters");
			charactersFolder.mkdir();
			
			characterName = characterName.substring(0, 1).toUpperCase() + characterName.substring(1).toLowerCase();
			File newCharacter = new File(charactersFolder+"/"+characterName+".data");
			if (newCharacter.exists()) {
				Logger.getLogger("server").log(Level.WARNING, "Account {0} tried to create an existing character!", new Object[] { account.Username, characterName });
				return QueueState.Failed;
			}
			
			try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(newCharacter))) {
				int zoneId = ZoneManager.GetZoneSpawnForRace(raceId);
				// DEBUG: Get the spawn position and direction from the map.
				float x = 50;
				float y = 50;
				int direction = 0;
				int level = 1;
				int xp = 0;
				
				writer.writeUTF(characterName);
				writer.writeByte(zoneId);
				writer.writeByte(raceId);
				writer.writeByte(realmId);
				writer.writeFloat(x);
				writer.writeFloat(y);
				writer.writeByte(direction);
				writer.writeByte(level);
				writer.writeByte(xp);
				
				// NOTE: Add the character to the currently loaded account.
				Player player = new Player();
				player.Name = characterName;
				player.ZoneID = zoneId;
				player.RaceID = raceId;
				player.RealmID = realmId;
				player.X = x;
				player.Y = y;
				player.Direction = direction;
				player.Level = level;
				player.XP = xp;
				account.Characters.add(player);
				Logger.getLogger("server").log(Level.INFO, "Character {0} created under account {1}.", new Object[] { characterName, account.Username });
				
				return QueueState.Success;
			} catch (IOException ex) {
				GameServer.getServerConsole().writeMessage(LogType.Logon, "Unable to create character. See console log.");
				Logger.getLogger("server").log(Level.SEVERE, "Failed to create character '{0}'; {1}", new Object[] { characterName, ex.getMessage() });
				return QueueState.Error;
			}
		}
		return QueueState.Failed;
	}
	
	/**
	 * Delete a character.
	 * @param account
	 * @param characterName
	 * @return a state signaling the status of the attempt
	 */
	public static QueueState DeleteCharacter(Account account, String characterName) {
		File accountFolder = new File(Configuration.getAccountDataPath()+"/"+account.Username);
		if (accountFolder.isDirectory() && accountFolder.exists()) {
			File characterFile = new File(accountFolder.getAbsolutePath()+"/Characters/"+characterName+".data");
			if (characterFile.exists()) {
				if (characterFile.delete()) {
					Logger.getLogger("server").log(Level.INFO, "Character {0} under account {0} has been deleted.", new Object[] { characterName, account.Username });
					account.RemoveCharacter(characterName);
					return QueueState.Success;
				} else {
					Logger.getLogger("server").log(Level.WARNING, "Failed to delete character {0}.", characterName);
					return QueueState.Failed;
				}
			} else {
				Logger.getLogger("server").log(Level.WARNING, "Account {0} tried to delete a non-existent character {1}. Client bug?", new Object[] { account.Username, characterName });
				return QueueState.Error;
			}
		}
		return QueueState.Failed;
	}
	
	/**
	 * Does this account exist?
	 * @param username to check for
	 * @return true, otherwise false
	 */
	public static Account Exists(String username) {
		for (Account account : Accounts) {
			if (account.Username.equalsIgnoreCase(username)) {
				return account;
			}
		}
		return null;
	}
}
