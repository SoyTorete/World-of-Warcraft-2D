package wow.manager;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handles user-input.
 * @author Xolitude
 * @since November 25, 2018
 * @see https://www.gamedev.net/articles/programming/general-and-gameplay-programming/java-games-keyboard-and-mouse-r2439/
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {

	/** Begin KeyListener variables. **/
	private final int KEY_COUNT = 256;
	
	private enum KeyState {
		RELEASED,
		PRESSED,
		ONCE
	}
	
	private boolean[] currentKeys = null;
	private KeyState[] keys = null;
	/** End KeyListener variables. **/
	
	/** Begin MouseListener variables. **/
	private final int BUTTON_COUNT = 3;
	
	public static final int MOUSE_LEFT = 1;
	public static final int MOUSE_MIDDLE = 2;
	public static final int MOUSE_RIGHT = 3;
	
	private enum MouseState {
		RELEASED,
		PRESSED,
		ONCE
	}
	
	private Point polledMousePosition;
	private Point currentMousePosition;
	
	private boolean[] state = null;
	private MouseState[] poll = null;
	/** End MouseListener variables. **/ 	
	
	public InputManager() {
		currentKeys = new boolean[KEY_COUNT];
		keys = new KeyState[KEY_COUNT];
		for(int i = 0; i < KEY_COUNT; i++) {
			keys[i] = KeyState.RELEASED;
		}
		
		polledMousePosition = new Point(0, 0);
		currentMousePosition = new Point(0, 0);
		
		state = new boolean[BUTTON_COUNT];
		poll = new MouseState[BUTTON_COUNT];
		for (int i = 0; i < BUTTON_COUNT; i++) {
			poll[i] = MouseState.RELEASED;
		}
	}
	
	/**
	 * Poll the keys every frame or "tick".
	 */
	public synchronized void poll() {
		for (int i = 0; i < KEY_COUNT; i++) {
			if (currentKeys[i]) {
				if (keys[i] == KeyState.RELEASED)
					keys[i] = KeyState.ONCE;
				else
					keys[i] = KeyState.PRESSED;
			} else {
				keys[i] = KeyState.RELEASED;
			}
		}
		
		polledMousePosition = new Point(currentMousePosition);
		for (int i = 0; i < BUTTON_COUNT; i++) {
			if (state[i]) {
				if (poll[i] == MouseState.RELEASED)
					poll[i] = MouseState.ONCE;
				else
					poll[i] = MouseState.PRESSED;
			} else {
				poll[i] = MouseState.RELEASED;
			}
		}
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode >= 0 && keyCode < KEY_COUNT) {
			currentKeys[keyCode] = true;
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode >= 0 && keyCode < KEY_COUNT) {
			currentKeys[keyCode] = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	@Override
	public void mouseDragged(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public synchronized void mousePressed(MouseEvent e) {
		state[e.getButton()-1] = true;
	}
	
	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		state[e.getButton()-1] = false;
	}
	
	@Override
	public synchronized void mouseMoved(MouseEvent e) {
		currentMousePosition = e.getPoint();
	}
	
	/**
	 * Is the given key being held down?
	 * @param keyCode
	 * @return true, otherwise false
	 */
	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] == KeyState.ONCE || keys[keyCode] == KeyState.PRESSED;
	}
	
	/**
	 * Has the given key been pressed once?
	 * @param keyCode
	 * @return true, otherwise false
	 */
	public boolean isKeyPressed(int keyCode) {
	    return keys[keyCode] == KeyState.ONCE;
	}
	
	/**
	 * Has the given mouse button been pressed once?
	 * @param button
	 * @return true, otherwise false
	 */
	public boolean isMouseButtonPressed(int button) {
		return poll[button-1] == MouseState.ONCE;
	}
	
	/**
	 * Is the given mouse button being held down?
	 * @param button
	 * @return true, otherwise false
	 */
	public boolean isMouseButtonDown(int button) {
		return poll[button-1] == MouseState.ONCE || poll[button-1] == MouseState.PRESSED;
	}
	
	/**
	 * Get the mouse' position.
	 * @return mousePosition
	 */
	public Point getMousePosition() {
		return polledMousePosition;
	}
}
