package wow.server;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import wow.server.command.CommandAccount;
import wow.server.command.ICommand;

/**
 * A gui for the server.
 * @author Xolitude
 * @since December 13, 2018
 */
public class GameServerGUI {

	/**
	 * The different log-types.
	 * @author Xolitude
	 * @since December 13, 2018
	 */
	public enum LogType {
		Server,
		Logon,
		World
	}
	
	private JFrame frmWorldOfWarcraft;
	private JTextField textField;
	private JTextArea textArea;
	
	private GameServer server;
	private ArrayList<ICommand> commands;

	/**
	 * Create the application.
	 */
	public GameServerGUI(GameServer server) {
		this.server = server;
		commands = new ArrayList<ICommand>();
		commands.add(new CommandAccount());
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWorldOfWarcraft = new JFrame();
		frmWorldOfWarcraft.setResizable(false);
		frmWorldOfWarcraft.setTitle("World of Warcraft 2D - GameServer");
		frmWorldOfWarcraft.setBounds(100, 100, 476, 288);
		frmWorldOfWarcraft.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWorldOfWarcraft.setLocationRelativeTo(null);
		frmWorldOfWarcraft.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 232, 453, 20);
		frmWorldOfWarcraft.getContentPane().add(textField);
		textField.setColumns(10);
		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					parseCommand(textField.getText());
					textField.setText("");
				}
			}
		});
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		
		JScrollPane scroller = new JScrollPane();
		scroller.setViewportView(textArea);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setBounds(10, 10, 453, 215);
		
		frmWorldOfWarcraft.getContentPane().add(scroller);
		frmWorldOfWarcraft.setVisible(true);
	}
	
	/**
	 * Parse a potential command.
	 * @param cmd
	 */
	private void parseCommand(String cmd) {
		if (cmd.equalsIgnoreCase("stop"))
			server.stop();
		
		for (int i = 0; i < commands.size(); i++) {
			ICommand command = commands.get(i);
			if (cmd.startsWith(command.getPrefix())) {
				command.handleCommand(server, cmd.split(" "));
			}
		}
	}
	
	/**
	 * Write a message to the text-area.
	 * @param logType
	 * @param message
	 */
	public void writeMessage(LogType logType, String message) {
		String type = String.format("[%s]", logType.name());
		textArea.append(type + " " + message + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
