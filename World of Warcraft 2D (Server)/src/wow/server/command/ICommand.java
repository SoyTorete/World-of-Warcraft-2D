package wow.server.command;

import wow.server.manager.AccountManager.AccountLevel;

/**
 * An abstract class for commands.
 * @author Xolitude
 * @since December 2, 2018
 */
public abstract class ICommand {

	private String prefix;
	private AccountLevel level;
	
	public ICommand(String prefix, AccountLevel level) {
		this.prefix = prefix;
		this.level = level;
	}
	
	public abstract void handleCommand(String[] args);
	
	public String getPrefix() {
		return prefix;
	}
	
	public AccountLevel getLevel() {
		return level;
	}
	
	@Override
	public abstract String toString();
}
