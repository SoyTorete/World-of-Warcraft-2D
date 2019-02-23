package wow.manager;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import wow.WoW;
import wow.gui.GuiNotificationBasic;
import wow.gui.GuiNotificationConfirmation;
import wow.net.connection.AuthConnection.Auth;
import wow.state.StateCharacterCreation;
import wow.state.StateCharacterSelect;
import wow.state.StateLoading;
import wow.state.StateMainMenu;

/**
 * Handles network notifications.
 * @author Xolitude
 * @since November 30, 2018
 */
public class NotificationManager {

	private static GuiNotificationBasic notification;
	private static GuiNotificationConfirmation notificationConfirmation;
	
	/**
	 * Run notification handling for the specified state.
	 * @param stateId
	 * @param engine
	 * @param display
	 * @param graphics
	 */
	public static void Run(int stateId, WoW engine, DisplayManager display, Graphics2D graphics) {
		switch (stateId) {
		case StateMainMenu.ID:
			RunMenu(engine, display, graphics);
			break;
		case StateCharacterCreation.ID:
			RunCreation(engine, display, graphics);
			break;
		case StateCharacterSelect.ID:
			RunSelect(engine, display, graphics);
			break;
		}
	}

	private static void RunMenu(WoW engine, DisplayManager display, Graphics2D graphics) {
		if (NetworkManager.AUTH != null) {
			switch (NetworkManager.AUTH.STATUS) {
			case Connecting:
				if (notification != null)
					notification = null;
				notification = new GuiNotificationBasic("Connecting...");
				notification.setLocation(display.getWidth() / 2 - notification.getWidth() / 2, display.getHeight() / 2 - notification.getHeight() / 2);
				break;
			case ConnectingFailed:
				if (notificationConfirmation != null)
					notificationConfirmation = null;
				notification = null;
				notificationConfirmation = new GuiNotificationConfirmation("Failed to connect.");
				notificationConfirmation.setLocation(display.getWidth() / 2 - notificationConfirmation.getWidth() / 2, display.getHeight() / 2 - notificationConfirmation.getHeight() / 2);
				notificationConfirmation.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						NetworkManager.AUTH = null;
					}
				});
				break;
			case Authenticating:
				if (notification != null)
					notification = null;
				notification = new GuiNotificationBasic("Authenticating...");
				notification.setLocation(display.getWidth() / 2 - notification.getWidth() / 2, display.getHeight() / 2 - notification.getHeight() / 2);
				break;
			case AuthenticatingUnk:
				if (notificationConfirmation != null)
					notificationConfirmation = null;
				notification = null;
				notificationConfirmation = new GuiNotificationConfirmation("Unable to verify this account.\nPlease try again or contact a developer.");
				notificationConfirmation.setLocation(display.getWidth() / 2 - notificationConfirmation.getWidth() / 2, display.getHeight() / 2 - notificationConfirmation.getHeight() / 2);
				notificationConfirmation.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						NetworkManager.AUTH = null;
					}
				});
				break;
			case AuthenticatingIncorrect:
				if (notificationConfirmation != null)
					notificationConfirmation = null;
				notification = null;
				notificationConfirmation = new GuiNotificationConfirmation("Invalid username/password combination.\nPlease try again or contact a developer.");
				notificationConfirmation.setLocation(display.getWidth() / 2 - notificationConfirmation.getWidth() / 2, display.getHeight() / 2 - notificationConfirmation.getHeight() / 2);
				notificationConfirmation.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						NetworkManager.AUTH = null;
					}
				});
				break;
			case AuthenticatingOk:
				if (notification != null)
					notification = null;
				notificationConfirmation = null;
				notification = new GuiNotificationBasic("Retrieving realm...");
				notification.setLocation(display.getWidth() / 2 - notification.getWidth() / 2, display.getHeight() / 2 - notification.getHeight() / 2);
				break;
			case RealmlistReceived:
				display.enterState(StateCharacterSelect.ID);
				break;
			case Credentials:
				if (notificationConfirmation != null)
					notificationConfirmation = null;
				notification = null;
				notificationConfirmation = new GuiNotificationConfirmation("One or more credentials are missing.\nPlease try again.");
				notificationConfirmation.setLocation(display.getWidth() / 2 - notificationConfirmation.getWidth() / 2, display.getHeight() / 2 - notificationConfirmation.getHeight() / 2);
				notificationConfirmation.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						NetworkManager.AUTH = null;
					}
				});
				break;
			case Waiting:
				break;
			}
			if (notification != null) {
				notification.render(engine, display, graphics);
			} else if (notificationConfirmation != null) {
				notificationConfirmation.tick(engine, display, 0);
				notificationConfirmation.render(engine, display, graphics);
			}
		}
	}
	
	private static void RunCreation(WoW engine, DisplayManager display, Graphics2D graphics) {
		if (NetworkManager.AUTH != null) {
			switch (NetworkManager.AUTH.STATUS) {
			case CharacterCreateServerError:
				if (notificationConfirmation != null)
					notificationConfirmation = null;
				notificationConfirmation = new GuiNotificationConfirmation("Server error occured.");
				notificationConfirmation.setLocation(display.getWidth() / 2 - notificationConfirmation.getWidth() / 2, display.getHeight() / 2 - notificationConfirmation.getHeight() / 2);
				notificationConfirmation.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						NetworkManager.AUTH.STATUS = Auth.Waiting;
					}
				});
				break;
			case CharacterCreateExists:
				if (notificationConfirmation != null)
					notificationConfirmation = null;
				notificationConfirmation = new GuiNotificationConfirmation("Unable to create character.\nPlease try again.");
				notificationConfirmation.setLocation(display.getWidth() / 2 - notificationConfirmation.getWidth() / 2, display.getHeight() / 2 - notificationConfirmation.getHeight() / 2);
				notificationConfirmation.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						NetworkManager.AUTH.STATUS = Auth.Waiting;
					}
				});
				break;
			case CharacterCreateOk:
				NetworkManager.AUTH.STATUS = Auth.Waiting;
				display.enterState(StateCharacterSelect.ID);
				break;
			case Waiting:
				notificationConfirmation = null;
				break;
			}
			if (notificationConfirmation != null) {
				notificationConfirmation.tick(engine, display, 0);
				notificationConfirmation.render(engine, display, graphics);
			}
		}
	}
	
	private static void RunSelect(WoW engine, DisplayManager display, Graphics2D graphics) {
		if (NetworkManager.AUTH != null) {
			switch (NetworkManager.AUTH.STATUS) {
			case CharacterList:
				notification = new GuiNotificationBasic("Retrieving character list...");
				notification.setLocation(display.getWidth() / 2 - notification.getWidth() / 2, display.getHeight() / 2 - notification.getHeight() / 2);
				break;
			case Waiting:
				notification = null;
				break;
			}
			if (notification != null) {
				notification.render(engine, display, graphics);
			}
		}
		
		if (NetworkManager.WORLD != null) {
			switch (NetworkManager.WORLD.STATUS) {
			case WorldOk:
				display.enterState(StateLoading.ID);
				break;
			case Waiting:
				break;
			}
		}
	}
}
