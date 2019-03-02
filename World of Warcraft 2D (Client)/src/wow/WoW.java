package wow;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.SwingUtilities;

import wow.manager.DisplayManager;
import wow.manager.NotificationManager;
import wow.state.StateCharacterCreation;
import wow.state.StateCharacterSelect;
import wow.state.StateGame;
import wow.state.StateLoading;
import wow.state.StateMainMenu;

/**
 * The main class; The "engine."
 * @author Xolitude
 * @since November 25, 2018
 */
public class WoW implements Runnable {

	protected Canvas canvas;
	protected DisplayManager display;
	
	private boolean isRunning = false;
	private Thread thread;
	
	protected final int FPS = 60;
	private int renderedFps;
	
	protected double delta;
		
	/**
	 * Initialize the display and start the engine.
	 */
	private void init() {
		canvas = new Canvas();
		display = new DisplayManager();
		display.create(1280, 720, canvas, this);
		display.addState(new StateMainMenu());
		display.addState(new StateCharacterSelect());
		display.addState(new StateCharacterCreation());
		display.addState(new StateLoading());
		display.addState(new StateGame());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.initialize();
			}
		});
		
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%4$s: %5$s [%1$tc]%n");
		
		start();
	}
	
	public synchronized void start() {
		isRunning = true;
		
		thread = new Thread(this, display.getTitle()+"_thread");
		thread.start();
	}
	
	/**
	 * Credits to vanZeben, Notch.
	 * @see https://github.com/vanZeben
	 */
	@Override
	public void run() {		
		long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / FPS;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = false;

            while (delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                renderedFps = frames;
                frames = 0;
                ticks = 0;
            }
        }
	}
	
	/**
	 * Run game ticks.
	 */
	private void tick() {
		display.getInput().poll();
		
		/** Make sure we've initialized states before trying to tick. **/
		if (display.haveStatesInitialized())
			display.getActiveState().tick(this, display, delta);
	}
	
	/**
	 * Run game renders.
	 */
	private void render() {
		BufferStrategy bs = canvas.getBufferStrategy();
		if (bs == null) {
			canvas.createBufferStrategy(3);
			return;
		}
		
		Graphics2D graphics = (Graphics2D)bs.getDrawGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, display.getWidth(), display.getHeight());
		
		/** Make sure we've initialized states before trying to render. **/
		if (display.haveStatesInitialized())
			display.getActiveState().render(this, display, graphics);
		
		/** Update notifications relative to the current state. **/
		NotificationManager.Run(display.getActiveState().getId(), this, display, graphics);
				
		graphics.dispose();
		bs.show();
	}
	
	/**
	 * Get the current frames-per-second.
	 * @return renderedFps
	 */
	public int getFps() {
		return renderedFps;
	}
	
	public static void main(String[] args) {
		new WoW().init();
	}
}
