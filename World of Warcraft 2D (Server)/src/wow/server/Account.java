package wow.server;

import java.util.ArrayList;

import wow.server.manager.AccountManager.AccountLevel;

/**
 * Handles account-specific data.
 * @author Xolitude
 * @since February 26, 2019
 */
public class Account {

	public String Username;
	public String HashedPassword;
	public String Salt;
	public AccountLevel Security;
	public ArrayList<Player> Characters = new ArrayList<Player>();
	public Player OnlinePlayer;
	public int RealmID;
	
	public void RemoveCharacter(String name) {
		Characters.removeIf(n -> n.Name.equalsIgnoreCase(name));
	}
}
