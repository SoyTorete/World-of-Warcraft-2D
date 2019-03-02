package wow.net;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import wow.WoW;
import wow.gfx.Animation;
import wow.manager.DisplayManager;
import wow.manager.GraphicsManager;
import wow.manager.WoWManager.RaceType;

/**
 * The network player.
 * @author Xolitude
 * @since February 23, 2019
 */
public class WorldCharacterMP extends IPlayer {
	
	private boolean isMoving;
	private Rectangle rect;
	private Rectangle namerect;

	public WorldCharacterMP(RaceType race, String name) {
		super(race, name);
	}
	
	@Override
	public void initAnimations() {
		northAnimation = new Animation();
		northAnimation.addFrame(race.getSpritesheet().getSubImage(1, 3, 32, 32), 12);
		northAnimation.addFrame(race.getSpritesheet().getSubImage(2, 3, 32, 32), 12);
		northAnimation.addFrame(race.getSpritesheet().getSubImage(1, 3, 32, 32), 12);
		northAnimation.addFrame(race.getSpritesheet().getSubImage(0, 3, 32, 32), 12);
		
		eastAnimation = new Animation();
		eastAnimation.addFrame(race.getSpritesheet().getSubImage(1, 2, 32, 32), 12);
		eastAnimation.addFrame(race.getSpritesheet().getSubImage(2, 2, 32, 32), 12);
		eastAnimation.addFrame(race.getSpritesheet().getSubImage(1, 2, 32, 32), 12);
		eastAnimation.addFrame(race.getSpritesheet().getSubImage(0, 2, 32, 32), 12);

		southAnimation = new Animation();
		southAnimation.addFrame(race.getSpritesheet().getSubImage(1, 0, 32, 32), 12);
		southAnimation.addFrame(race.getSpritesheet().getSubImage(2, 0, 32, 32), 12);
		southAnimation.addFrame(race.getSpritesheet().getSubImage(1, 0, 32, 32), 12);
		southAnimation.addFrame(race.getSpritesheet().getSubImage(0, 0, 32, 32), 12);

		westAnimation = new Animation();
		westAnimation.addFrame(race.getSpritesheet().getSubImage(1, 1, 32, 32), 12);
		westAnimation.addFrame(race.getSpritesheet().getSubImage(2, 1, 32, 32), 12);
		westAnimation.addFrame(race.getSpritesheet().getSubImage(1, 1, 32, 32), 12);
		westAnimation.addFrame(race.getSpritesheet().getSubImage(0, 1, 32, 32), 12);
	}

	@Override
	public void tick(WoW engine, DisplayManager display, double delta) {
		if (isMoving) {
			if (direction == Direction.North || direction == Direction.North_East || direction == Direction.North_West) {
				northAnimation.tick(delta);
			} else if (direction == Direction.South || direction == Direction.South_East || direction == Direction.South_West) {
				southAnimation.tick(delta);
			} else if (direction == Direction.East) {
				eastAnimation.tick(delta);
			} else {
				westAnimation.tick(delta);
			}
		}
		
		if (rect != null) {
			rect.setLocation((int)x, (int)y);
		}
		if (namerect != null) {
			namerect.setLocation((int)(rect.getX() + rect.getWidth() / 2 - namerect.getWidth() / 2), (int)(rect.getY() - namerect.getHeight()));
		}
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		graphics.setFont(display.getGameFont(12f));
		if (rect == null) {
			rect = new Rectangle(0, 0, 32, 32);
		}
		graphics.setColor(Color.black);
		if (namerect == null) {
			namerect = new Rectangle(0, 0, graphics.getFontMetrics().stringWidth(getName()), graphics.getFontMetrics().getHeight());
		}
		graphics.setColor(new Color(0.0f, 0.0f, 0.0f, 0.45f));
		graphics.fill(namerect);
		graphics.setColor(Color.white);
		GraphicsManager.drawString(getName(), (float)namerect.getX(), (float)namerect.getY(), graphics);
		try {
			if (isMoving) {
				switch (direction) {
				case North:
				case North_East:
				case North_West:
					northAnimation.render(graphics, x, y);
					break;
				case South:
				case South_East:
				case South_West:
					southAnimation.render(graphics, x, y);
					break;
				case East:
					eastAnimation.render(graphics, x, y);
					break;
				case West:
					westAnimation.render(graphics, x, y);
					break;
				}
			} else {
				switch (direction) {
				case North:
				case North_East:
				case North_West:
					northAnimation.stop();
					northAnimation.render(graphics, x, y);
					break;
				case South:
				case South_East:
				case South_West:
					southAnimation.stop();
					southAnimation.render(graphics, x, y);
					break;
				case East:
					eastAnimation.stop();
					eastAnimation.render(graphics, x, y);
					break;
				case West:
					westAnimation.stop();
					westAnimation.render(graphics, x, y);
					break;
				}
			}
		} catch (Exception ex) {}
	}
	
	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	@Override
	public void OnMove() {}
}
