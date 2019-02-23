package wow.state;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import logon.CS_CharacterList;
import wow.WoW;
import wow.gui.GuiButton;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.InputManager;
import wow.manager.NetworkManager;
import wow.manager.NetworkManager.SimplePacketDirection;
import wow.manager.WoWManager;
import wow.manager.WoWManager.RaceType;
import wow.net.RealmCharacter;
import wow.net.connection.AuthConnection.Auth;

/**
 * The character selection-state.
 * @author Xolitude
 * @since December 3, 2018
 */
public class StateCharacterSelect implements IState {
	
	public static final int ID = 1;
	
	private RoundRectangle2D.Double characterSelectionPanel;
	private RoundRectangle2D.Double[] characterPortraits;
	private RoundRectangle2D.Double characterSelector;
	
	private GuiButton enterWorldButton;
	private GuiButton changeRealmButton;
	private GuiButton createCharacterButton;
	private GuiButton deleteCharacterButton;
	private GuiButton backButton;
	
	private int selectedIndex = -1;
	
	// TODO: Fix character selector highlight bug after deletion.
	// TODO: Fix enter world bug.

	@Override
	public void init(DisplayManager display) {
		characterSelectionPanel = new RoundRectangle2D.Double(0, 0, 225, 565, 6, 6);
		characterSelectionPanel.x = display.getWidth() - characterSelectionPanel.getWidth() - 15;
		characterSelectionPanel.y = 15;
		
		// DEBUG: Disable this button until we have a realm-character selected.
		enterWorldButton = new GuiButton("Enter World");
		enterWorldButton.setLocation(display.getWidth() / 2 - enterWorldButton.getWidth() / 2, display.getHeight() - enterWorldButton.getHeight() * 2);
		enterWorldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WoWManager.CharacterInUse = WoWManager.Characters.get(selectedIndex);
				NetworkManager.ConnectToWorld();
			}
		});
		
		changeRealmButton = new GuiButton("Change Realm");
		changeRealmButton.setLocation((int)(characterSelectionPanel.x + (characterSelectionPanel.width / 2 - changeRealmButton.getWidth() / 2)), (int)(characterSelectionPanel.y + (changeRealmButton.getHeight() + 15)));
		changeRealmButton.setEnabled(false);
		
		createCharacterButton = new GuiButton("Create Character");
		createCharacterButton.setLocation((int)(characterSelectionPanel.x + (characterSelectionPanel.width / 2 - createCharacterButton.getWidth() / 2)), (int)(characterSelectionPanel.y + (characterSelectionPanel.height - createCharacterButton.getHeight() - 10)));
		createCharacterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.enterState(StateCharacterCreation.ID);
			}
		});
		
		deleteCharacterButton = new GuiButton("Delete Character");
		deleteCharacterButton.setLocation(createCharacterButton.getX(), enterWorldButton.getY() - deleteCharacterButton.getHeight() * 2);
		deleteCharacterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NetworkManager.SendCharacterDeletionPacket(WoWManager.Characters.get(selectedIndex).Name);
			}
		});
		
		backButton = new GuiButton("Back");
		backButton.setLocation(deleteCharacterButton.getX(), enterWorldButton.getY());
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.enterState(StateMainMenu.ID);
				NetworkManager.AUTH.getClient().close();
				NetworkManager.AUTH = null;
			}
		});
		
		/** Initialize the reference portraits. **/
		characterPortraits = new RoundRectangle2D.Double[7];
		for (int i = 0; i < characterPortraits.length; i++) {
			characterPortraits[i] = new RoundRectangle2D.Double(0, 0, 195, 50, 6, 6);
			if (i == 0) {
				characterPortraits[i].x = characterSelectionPanel.x + characterSelectionPanel.width / 2 - characterPortraits[i].width / 2;
				characterPortraits[i].y = changeRealmButton.getY() + changeRealmButton.getHeight() + 25;
			} else {
				characterPortraits[i].x = characterPortraits[0].x;
				characterPortraits[i].y = characterPortraits[i-1].y + characterPortraits[i-1].height + 10;
			}
		}
		
		characterSelector = new RoundRectangle2D.Double(0, 0, 195, 50, 6, 6);
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		/** Send the character-list packet if we haven't. **/
		boolean requestSent = false;
		if (WoWManager.Characters == null) {
			if (!requestSent) {
				NetworkManager.AUTH.STATUS = Auth.CharacterList;
				NetworkManager.SendSimplePacket(SimplePacketDirection.Auth, new CS_CharacterList());
				requestSent = true;
			}
			return;
		}
		
		if (WoWManager.Characters.size() < 1)
			selectedIndex = -1;
		
		/** If we have any characters, draw the highlighter at the first character by default. **/
		if (WoWManager.Characters.size() > 0) {
			if (characterSelector.x == 0.0 && characterSelector.y == 0.0) {
				characterSelector.x = characterPortraits[0].x;
				characterSelector.y = characterPortraits[0].y;
				selectedIndex = 0;
			}
		}
		
		enterWorldButton.tick(engine, display, delta);
		changeRealmButton.tick(engine, display, delta);
		createCharacterButton.tick(engine, display, delta);
		deleteCharacterButton.tick(engine, display, delta);
		backButton.tick(engine, display, delta);
		
		InputManager input = display.getInput();
		
		if (input.isMouseButtonPressed(InputManager.MOUSE_LEFT)) {
			for (int i = 0; i < characterPortraits.length; i++) {
				RoundRectangle2D.Double r = characterPortraits[i];
				if (r.contains(input.getMousePosition())) {
					if (WoWManager.Characters != null) {
						try {
							if (WoWManager.Characters.get(i) != null) {
								characterSelector.x = r.x;
								characterSelector.y = r.y;
								selectedIndex = i;
							}
						} catch (Exception ex) {}
					}
				}
			}
		}
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setColor(Color.gray);
		Stroke oldStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.draw(characterSelectionPanel);
		graphics.setStroke(oldStroke);
		
		enterWorldButton.render(engine, display, graphics);
		changeRealmButton.render(engine, display, graphics);
		createCharacterButton.render(engine, display, graphics);
		deleteCharacterButton.render(engine, display, graphics);
		backButton.render(engine, display, graphics);
		
		graphics.setFont(display.getGameFont(14f));
		graphics.setColor(new Color(223, 195, 15));		
		GraphicsManager.drawString(WoWManager.RealmName, (int)(characterSelectionPanel.x + (characterSelectionPanel.width / 2 - graphics.getFontMetrics().stringWidth(WoWManager.RealmName) / 2)), changeRealmButton.getY() - graphics.getFontMetrics().getHeight() - 12, graphics);
		
		if (characterSelector.x != 0.0 && characterSelector.y != 0.0 && selectedIndex > -1) {
			graphics.setColor(new Color(238, 208, 18, 95));
			graphics.fill(characterSelector);
			graphics.setColor(Color.yellow);
			graphics.draw(characterSelector);
		}
		
		if (WoWManager.Characters != null) {
			if (WoWManager.Characters.size() > 0) {
				for (int i = 0; i < WoWManager.Characters.size(); i++) {
					RealmCharacter character = WoWManager.Characters.get(i);
					
					graphics.setFont(display.getGameFont(14f));
					graphics.setColor(new Color(223, 195, 15));
					GraphicsManager.drawString(character.Name, (float)characterPortraits[i].x + 5, (float)characterPortraits[i].y, graphics);
					graphics.setFont(display.getGameFont(13f));
					graphics.setColor(Color.white);
					GraphicsManager.drawString("Level 1 Warrior", (float)characterPortraits[i].x + 5, (float)characterPortraits[i].y + graphics.getFontMetrics().getHeight() + 4, graphics);
					graphics.setColor(Color.gray);
					GraphicsManager.drawString(character.Zone.getName(), (float)characterPortraits[i].x + 5, (float)characterPortraits[i].y + (graphics.getFontMetrics().getHeight() * 2 + 8), graphics);
				}
			}
		}
		
		if (selectedIndex > -1) {
			try {
				RaceType race = WoWManager.Characters.get(selectedIndex).Race;
				if (race != null) {
					BufferedImage sprite = race.getSpritesheet().getSubImage(1, 0, 32, 32);
					GraphicsManager.drawImage(sprite, display.getWidth() / 2 - sprite.getWidth() / 2, display.getHeight() / 2 - sprite.getHeight() / 2, 48, 48, graphics);
				}
			} catch (Exception ex) {}
		}
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public void OnStateTransition(DisplayManager display) {
		WoWManager.Characters = null;
		selectedIndex = -1;
	}
}
