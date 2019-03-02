package wow.net;

import java.awt.Graphics2D;

import wow.WoW;
import wow.gfx.Animation;
import wow.manager.DisplayManager;
import wow.manager.WoWManager.RaceType;
import wow.net.IPlayer.Direction;

/**
 * The network player.
 * @author Xolitude
 * @since February 23, 2019
 */
public class WorldCharacterMP extends IPlayer {
	
	private boolean isMoving;

	public WorldCharacterMP(RaceType race, String name) {
		super(race, name);
		
		northAnimation = race.getNorthAnimation();
		eastAnimation = race.getEastAnimation();
		southAnimation = race.getSouthAnimation();
		westAnimation = race.getWestAnimation();
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
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
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
	}
	
	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	@Override
	public void OnMove() {}
}
