package wow.state;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import wow.WoW;
import wow.gui.GuiButton;
import wow.gui.GuiRaceOption;
import wow.gui.GuiTextField;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.InputManager;
import wow.manager.NetworkManager;
import wow.manager.WoWManager.RaceType;

/**
 * The character-creation state.
 * @author Xolitude
 * @since December 5, 2018
 */
public class StateCharacterCreation implements IState {
	
	public static final int ID = 2;
	
	private GuiRaceOptionPanel racePanel;
	private GuiTextField nameTextField;
	private GuiButton acceptCharacterButton;
	private GuiButton backButton;
	
	private BufferedImage allianceBanner;
	private BufferedImage hordeBanner;

	@Override
	public void init(DisplayManager display) {
		racePanel = new GuiRaceOptionPanel(display);
		nameTextField = new GuiTextField(display, 175, 25, 6, 6);
		nameTextField.setLocation(display.getWidth() / 2 - nameTextField.getWidth() / 2, display.getHeight() - nameTextField.getHeight() * 2 - 25);
		
		acceptCharacterButton = new GuiButton("Accept");
		acceptCharacterButton.setLocation(display.getWidth() - acceptCharacterButton.getWidth() - 15, nameTextField.getY());
		acceptCharacterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String name = nameTextField.getText().trim();
				int raceId = racePanel.selectedRace.getId();
				
				NetworkManager.SendCharacterCreationPacket(name, raceId);
			}
		});
		
		backButton = new GuiButton("Back");
		backButton.setLocation(acceptCharacterButton.getX(), acceptCharacterButton.getY() + backButton.getHeight() + 5);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				nameTextField.setFocus(false);
				nameTextField.setText("");
				display.enterState(StateCharacterSelect.ID);
			}
		});
		
		try {
			allianceBanner = ImageIO.read(getClass().getResourceAsStream("/ui/Ally_Banner.png"));
			hordeBanner = ImageIO.read(getClass().getResourceAsStream("/ui/Horde_Banner.png"));
		} catch (IOException e) {}
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		if (!nameTextField.hasFocus())
			nameTextField.setFocus(true);
		racePanel.tick(engine, display, delta);
		acceptCharacterButton.tick(engine, display, delta);
		backButton.tick(engine, display, delta);
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		GraphicsManager.drawImage(allianceBanner, racePanel.humanOption.getX() - 2, racePanel.humanOption.getY() - 2, allianceBanner.getWidth(), allianceBanner.getHeight(), graphics);
		GraphicsManager.drawImage(hordeBanner, racePanel.undeadOption.getX() - 2, racePanel.undeadOption.getY() - 2, hordeBanner.getWidth(), hordeBanner.getHeight(), graphics);
		
		racePanel.render(engine, display, graphics);
				
		/** Get the current selected race and render it's first "idle" image. **/
		RaceType race = racePanel.selectedRace;
		if (race != null) {
			BufferedImage sprite = null;
			if (sprite == null) 
				sprite = race.getSpritesheet().getSubImage(1, 0, 32, 32);
			GraphicsManager.drawImage(sprite, display.getWidth() / 2 - sprite.getWidth() / 2, display.getHeight() / 2 - sprite.getHeight() / 2, 64, 64, graphics);
		}	
		
		graphics.setFont(display.getGameFont(14f));
		graphics.setColor(new Color(223, 195, 15));
		GraphicsManager.drawCenteredString("Name", nameTextField.getX(), nameTextField.getY() - graphics.getFontMetrics().getHeight() - 5, nameTextField.getWidth(), nameTextField.getHeight(), graphics);
		GraphicsManager.drawCenteredString("Alliance", racePanel.humanOption.getX(), racePanel.humanOption.getY() - graphics.getFontMetrics().getHeight() - 15, racePanel.humanOption.getWidth(), racePanel.humanOption.getHeight(), graphics);
		GraphicsManager.drawCenteredString("Horde", racePanel.undeadOption.getX(), racePanel.undeadOption.getY() - graphics.getFontMetrics().getHeight() - 15, racePanel.undeadOption.getWidth(), racePanel.undeadOption.getHeight(), graphics);

		
		nameTextField.render(engine, display, graphics);
		acceptCharacterButton.render(engine, display, graphics);
		backButton.render(engine, display, graphics);
	}

	@Override
	public int getId() {
		return ID;
	}
	
	/**
	 * Handles the race options and selection.
	 * @author Xolitude
	 * @since December 6, 2018
	 */
	private class GuiRaceOptionPanel {
		
		private RoundRectangle2D.Double raceSelectionPanel;
		
		private GuiRaceOption undeadOption;
		private GuiRaceOption humanOption;
		
		private RaceType selectedRace;
		
		public GuiRaceOptionPanel(DisplayManager display) {
			raceSelectionPanel = new RoundRectangle2D.Double(25, 0, 225, 325, 6, 6);
			raceSelectionPanel.y = display.getHeight() / 2 - raceSelectionPanel.height / 2;
			
			undeadOption = new GuiRaceOption(RaceType.Undead);
			undeadOption.setLocation((int)(raceSelectionPanel.x + (raceSelectionPanel.width - 75)), (int)raceSelectionPanel.y + 45);
			undeadOption.isSelected = true;
			
			humanOption = new GuiRaceOption(RaceType.Human);
			humanOption.setLocation((int)(raceSelectionPanel.x + (raceSelectionPanel.width / 4 - humanOption.getWidth() / 2)), (int)raceSelectionPanel.y + 45);
		}
		
		public void tick(WoW engine, DisplayManager display, double delta) {
			undeadOption.tick(engine, display, delta);
			humanOption.tick(engine, display, delta);
			
			tickSelected(engine, display, delta);
		}
		
		/**
		 * A hacky method of selection ¯\_('-')_/¯
		 * @param engine
		 * @param display
		 * @param delta
		 */
		private void tickSelected(WoW engine, DisplayManager display, double delta) {
			InputManager input = display.getInput();
			Point mousePos = input.getMousePosition();
			
			if (input.isMouseButtonPressed(InputManager.MOUSE_LEFT)) {
				if (undeadOption.getBorder().contains(mousePos)) {
					humanOption.isSelected = false;
					
					undeadOption.isSelected = true;
				} else if (humanOption.getBorder().contains(mousePos)) {
					undeadOption.isSelected = false;
					
					humanOption.isSelected = true;
				}
			}
			
			if (humanOption.isSelected)
				selectedRace = humanOption.getRace();
			else if (undeadOption.isSelected)
				selectedRace = undeadOption.getRace();
		}

		public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
			graphics.setColor(Color.gray);
			Stroke oldStroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(raceSelectionPanel);
			graphics.setStroke(oldStroke);
			
			undeadOption.render(engine, display, graphics);
			humanOption.render(engine, display, graphics);
		}
	}

	@Override
	public void OnStateTransition() {		
		nameTextField.setText("");
	}
}
