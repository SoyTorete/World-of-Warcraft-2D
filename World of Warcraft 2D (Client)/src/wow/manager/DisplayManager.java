package wow.manager;

import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import wow.WoW;
import wow.state.IState;

/**
 * Handles everything related to the display.
 * @author Xolitude
 * @since November 25, 2018
 */
public class DisplayManager {

	private final String title = "World of Warcraft 2D";
	private final String version = "v0.9.0a (development)";
	
	private JFrame frame;
	private Canvas canvas;
	
	private ArrayList<IState> states = new ArrayList<IState>();
	private IState activeState;
	private int intializedStates = 0;
	
	private InputManager input;
	
	private Font font;
	
	public void create(int width, int height, Canvas canvas, WoW engine) {
		if (frame == null) {
			final Dimension size = new Dimension(width, height);
			this.canvas = canvas;
			canvas.setMinimumSize(size);
			canvas.setMaximumSize(size);
			canvas.setPreferredSize(size);
			
			this.frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new CardLayout());
			frame.add(canvas);
			frame.pack();
			
			try {
				frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/ui/icon.png")));
			} catch (IOException e) {
				System.err.println("Unable to read the icon file.");
			}
			
			try {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				font = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("ui/util/font.otf"));
				ge.registerFont(font);
			} catch (IllegalArgumentException ex) {
				System.err.println("IllegalArugment while creating a font: "+ex.getMessage());
			} catch (FontFormatException ex) {
				System.err.println("FontFormat exception while creating a font:"+ex.getMessage());
			} catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Unable to read the font file.");
			}
			
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			
			input = new InputManager();
			canvas.addKeyListener(input);
			canvas.addMouseListener(input);
			canvas.addMouseMotionListener(input);
			canvas.setFocusTraversalKeysEnabled(false);
			canvas.requestFocus();
			
			frame.setVisible(true);
		}
	}
	
	/**
	 * Adds a listener to the display's canvas.
	 * - This is a generic method.
	 * @param listener
	 */
	public void addKeyListener(KeyListener listener) {
		canvas.addKeyListener(listener);
	}
	
	/**
	 * Add a game-state to this display.
	 * @param state
	 */
	public void addState(IState state) {
		for (IState s : states) {
			if (s.getId() == state.getId()) {
				System.err.println("Unable to add game-state: already exists.");
				return;
			}
		}
		states.add(state);
	}
	
	/**
	 * Begin to render/update the state with the given id.
	 * @param id
	 */
	public void enterState(int id) {
		for (IState state : states) {
			if (state.getId() == id) {
				activeState = state;
			}
			state.OnStateTransition(this);
		}
		if (activeState == null || activeState.getId() != id)
			System.err.println("Unable to enter game-state with id: "+id+" - missing state.");
	}
	
	/**
	 * Initialize states in a new thread.
	 */
	public void initialize() {
		do {
			for (IState state : states) {
				state.init(this);
				intializedStates++;
			}
		} while (intializedStates != states.size());
	}
	
	/**
	 * Have we initialized all states?
	 * @return true, otherwise false.
	 */
	public boolean haveStatesInitialized() {
		return intializedStates == states.size();
	}
	
	/**
	 * Get the current active state.
	 * @return activeState
	 */
	public IState getActiveState() {
		try {
			if (activeState == null)
				activeState = states.get(0);
		} catch (IndexOutOfBoundsException ex) {
			System.err.println("States list is empty.");
		}
		return activeState;
	}
	
	/**
	 * Get the game's title.
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Get the game's version.
	 * @return version
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Get the width of the display.
	 * @return width
	 */
	public int getWidth() {
		return canvas.getWidth();
	}
	
	/**
	 * Get the height of the display.
	 * @return height
	 */
	public int getHeight() {
		return canvas.getHeight();
	}
	
	/**
	 * Get the input manager.
	 * @return input
	 */
	public InputManager getInput() {
		return input;
	}
	
	/**
	 * Get the display's main font.
	 * @param size
	 * @return font
	 */
	public Font getGameFont(float size) {
		if (size > 0.0f)
			font = font.deriveFont(size);
		return font;
	}
	
	/**
	 * Close the display.
	 */
	public void exit() {
		System.exit(0);
	}
}
