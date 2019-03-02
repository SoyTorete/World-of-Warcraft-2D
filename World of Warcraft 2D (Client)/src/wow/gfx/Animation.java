package wow.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import wow.manager.GraphicsManager;
import wow.manager.WoWManager;

/**
 * Handles sprite animation.
 * @author Xolitude
 * @since November 28, 2018
 */
public class Animation {

	private ArrayList<Frame> frames;
	private Frame currentFrame;
	private int frameIndex = 0;
	
	private int tickCounter = 0;
	
	public Animation() {
		frames = new ArrayList<Frame>();
	}
	
	/**
	 * Adds a new frame to this animation.
	 * @param img
	 * @param duration
	 */
	public void addFrame(BufferedImage img, int duration) {
		Frame newFrame = new Frame(img, duration);
		frames.add(newFrame);
	}
	
	/**
	 * Stop the animation and reset it.
	 */
	public void stop() {
		currentFrame = frames.get(0);
	}
	
	/**
	 * Tick the animation.
	 * @param delta
	 */
	public void tick(double delta) {
		if (currentFrame == null)
			currentFrame = frames.get(0);
		tickCounter += 1 * delta; /* Update based on the delta to help avoid lag with a constant number. */
		if (tickCounter >= currentFrame.frameDuration) {
			if (frameIndex >= frames.size()-1) {
				frameIndex = 0; /* Restart the animation if we've reached the end of the list of frames. */
			} else
				frameIndex++;
			tickCounter = 0;
			currentFrame = frames.get(frameIndex);
		}
	}

	/**
	 * Render the current frame of the animation.
	 * @param graphics
	 */
	public void render(Graphics2D graphics, float x, float y) {
		GraphicsManager.drawImage(currentFrame.frameImage, x, y, graphics);
	}
	
	/**
	 * Holds data for each individual frame of the animation.
	 * @author Xolitude
	 * @since November 29, 2018
	 */
	private class Frame {
		
		private BufferedImage frameImage;
		private int frameDuration;
		
		public Frame(BufferedImage frameImage, int frameDuration) {
			this.frameImage = frameImage;
			this.frameDuration = frameDuration;
		}
	}
}
