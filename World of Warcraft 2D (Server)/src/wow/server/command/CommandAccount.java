package wow.server.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import wow.server.GameServer;
import wow.server.GameServerGUI.LogType;
import wow.server.manager.AccountManager;
import wow.server.manager.AccountManager.AccountLevel;
import wow.server.util.BCrypt;

/**
 * Handles commands relative to accounts.
 * @author Xolitude
 * @since December 2, 2018
 */
public class CommandAccount extends ICommand {
	
	private final int OK = -1;
	private final int ARGUMENTS = 0;
	
	private LinkedHashMap<String, Method> subCommands = new LinkedHashMap<String, Method>();

	public CommandAccount() {
		super("account", AccountLevel.Administrator);
		
		/** Register sub-commands with a method. **/
		try {
			subCommands.put("create", this.getClass().getDeclaredMethod("handleCreation", String[].class));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleCommand(String[] args) {
		if (args.length == 1) 
			GameServer.getServerConsole().writeMessage(LogType.Server, toString());
		
		if (args.length >= 2) {
			String subCommand = args[1];
			for (Map.Entry<String, Method> set : subCommands.entrySet()) {
				if (set.getKey().equals(subCommand)) {
					Method method = set.getValue();
					try {
						method.invoke(this, (Object)args);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Handle the 'create' sub-command.
	 * @param args
	 */
	private void handleCreation(String[] args) {
		int code = OK; /** Checks for errors. **/
		if (args.length < 4)
			code = ARGUMENTS;
		if (args.length > 4) 
			code = ARGUMENTS;
		
		switch (code) {
		case ARGUMENTS:
			GameServer.getServerConsole().writeMessage(LogType.Server, "Usage: account create [username] [password].");
			return;
		}
		
		String username = args[2].toUpperCase();
		String password = args[3];
		
		String bcrypt_salt = null;
		String bcrypt_hash = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(password.getBytes());
			String shaHashedPassword = BCrypt.BytesToHex(hash);
			
			bcrypt_salt = BCrypt.gensalt(12);
			bcrypt_hash = BCrypt.hashpw(shaHashedPassword+GameServer.SALT, bcrypt_salt);
		} catch (NoSuchAlgorithmException ex) {}
		
		if (bcrypt_salt != null && bcrypt_hash != null)
			AccountManager.CreateAccount(username, bcrypt_hash, bcrypt_salt);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		ArrayList<String> subs = new ArrayList<String>();
		
		for (Map.Entry<String, Method> set : subCommands.entrySet()) {
			subs.add(set.getKey());
		}
		
		for (int i = 0; i < subs.size(); i++) {
			String str = subs.get(i);
			if (i+1 != subs.size())
				buffer.append(str+", ");
			else
				buffer.append(str);
		}
		return "Available sub-commands for account: "+buffer.toString();
	}
}
