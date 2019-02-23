package wow.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.esotericsoftware.kryonet.Client;

import wow.WoW;
import wow.gui.GuiButton;
import wow.gui.GuiTextField;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.InputManager;
import wow.manager.NetworkManager;
import wow.manager.WoWManager;

/**
 * The main-menu state.
 * @author Xolitude
 * @since November 25, 2018
 */
public class StateMainMenu implements IState {
	
	public static final int ID = 0;
	
	private BufferedImage background;
	
	private final String copyright1 = "Copyright 2018-2019 Xolitude.";
	private final String copyright2 = "Copyright 2004-2019 Blizzard Entertainment. All Rights Reserved.";
	
	private GuiButton loginButton;
	private GuiButton quitButton;
	
	private GuiTextField usernameTextField;
	private GuiTextField passwordTextField;

	@Override
	public void init(DisplayManager display) {
		try {
			background = ImageIO.read(getClass().getResourceAsStream("/ui/bg_0.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		loginButton = new GuiButton("Login");
		loginButton.setLocation(display.getWidth() / 2 - loginButton.getWidth() / 2, 500);
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				usernameTextField.setFocus(false);
				passwordTextField.setFocus(false);
				String username = usernameTextField.getText().trim();
				String password = passwordTextField.getText().trim();
				usernameTextField.setText("");
				passwordTextField.setText("");

				WoWManager.AccountName = username;
				NetworkManager.ConnectToAuth(username, password);
			}
		});
		
		quitButton = new GuiButton("Quit");
		quitButton.setLocation(display.getWidth() - quitButton.getWidth() - 25, display.getHeight() - quitButton.getHeight() - 45);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.exit();
			}
		});
		
		usernameTextField = new GuiTextField(display, 150, 25, 6, 6);
		usernameTextField.setBackgroundColor(new Color(0, 0, 0, 225));
		usernameTextField.setBorderColor(Color.gray);
		usernameTextField.setLocation(display.getWidth() / 2 - usernameTextField.getWidth() / 2, 340);
		usernameTextField.setFocus(true);
		
		passwordTextField = new GuiTextField(display, 150, 25, 6, 6);
		passwordTextField.setBackgroundColor(new Color(0, 0, 0, 225));
		passwordTextField.setBorderColor(Color.gray);
		passwordTextField.setLocation(display.getWidth() / 2 - passwordTextField.getWidth() / 2, 440);
		passwordTextField.setEchoChar('*');
		
		NetworkManager.Initialize();
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		tickInput(display, delta);
		tickLoginUI(engine, display, delta);
		tickOtherUI(engine, display, delta);
	}
	
	private void tickInput(DisplayManager display, double delta) {
		if (!usernameTextField.hasFocus() && !passwordTextField.hasFocus())
			usernameTextField.setFocus(true);
		
		InputManager input = display.getInput();
		
		if (input.isKeyPressed(KeyEvent.VK_TAB)) {
			if (usernameTextField.hasFocus()) {
				usernameTextField.setFocus(false);
				passwordTextField.setFocus(true);
			} else if (passwordTextField.hasFocus()) {
				passwordTextField.setFocus(false);
				usernameTextField.setFocus(true);
			}
		}
		
		/** Only make controls usable if we aren't on any other connection-state. **/
		setControlUsability(NetworkManager.AUTH == null);
	}
	
	private void tickLoginUI(WoW engine, DisplayManager display, double delta) {
		loginButton.tick(engine, display, delta);
		usernameTextField.tick(engine, display, delta);
		passwordTextField.tick(engine, display, delta);
	}
	
	private void tickOtherUI(WoW engine, DisplayManager display, double delta) {
		quitButton.tick(engine, display, delta);
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		GraphicsManager.drawImage(background, 0, 0, display.getWidth(), display.getHeight(), graphics);
		
		renderLoginUI(engine, display, graphics);
		renderOtherUI(engine, display, graphics);
	}
	
	private void renderLoginUI(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(14f));
		graphics.setColor(Color.BLACK);
		GraphicsManager.drawString("Account Name", display.getWidth() / 2 - graphics.getFontMetrics().stringWidth("Account Name") / 2 + 2, 302, graphics);
		graphics.setColor(new Color(223, 195, 15));
		GraphicsManager.drawString("Account Name", display.getWidth() / 2 - graphics.getFontMetrics().stringWidth("Account Name") / 2, 300, graphics);
		
		graphics.setColor(Color.BLACK);
		GraphicsManager.drawString("Account Password", display.getWidth() / 2 - graphics.getFontMetrics().stringWidth("Account Password") / 2 + 2, 402, graphics);
		graphics.setColor(new Color(223, 195, 15));
		GraphicsManager.drawString("Account Password", display.getWidth() / 2 - graphics.getFontMetrics().stringWidth("Account Password") / 2, 400, graphics);
		
		usernameTextField.render(engine, display, graphics);
		passwordTextField.render(engine, display, graphics);
		
		loginButton.render(engine, display, graphics);
	}
	
	private void renderOtherUI(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(14f));
		graphics.setColor(new Color(223, 195, 15));
		GraphicsManager.drawString(display.getVersion(), 0, display.getHeight() - graphics.getFontMetrics().getHeight() * 2, graphics);
		GraphicsManager.drawString("February 17 2019", 0, display.getHeight() - graphics.getFontMetrics().getHeight(), graphics);
		GraphicsManager.drawString(copyright1, display.getWidth() / 2 - graphics.getFontMetrics().stringWidth(copyright1) / 2, display.getHeight() - graphics.getFontMetrics().getHeight() * 2, graphics);
		GraphicsManager.drawString(copyright2, display.getWidth() / 2 - graphics.getFontMetrics().stringWidth(copyright2) / 2, display.getHeight() - graphics.getFontMetrics().getHeight(), graphics);
		
		quitButton.render(engine, display, graphics);
	}
	
	/**
	 * Should the controls be enabled?
	 * @param isEnabled
	 */
	private void setControlUsability(boolean isEnabled) {
		loginButton.setEnabled(isEnabled);
		quitButton.setEnabled(isEnabled);
		usernameTextField.setEnabled(isEnabled);
		passwordTextField.setEnabled(isEnabled);
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public void OnStateTransition(DisplayManager display) {		
	}
}
