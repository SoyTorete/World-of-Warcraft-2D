package wow.net;

import java.awt.Graphics2D;

import wow.WoW;
import wow.gfx.Animation;
import wow.manager.DisplayManager;
import wow.manager.WoWManager.RaceType;
import wow.manager.WoWManager.Zones;

/**
 * An extend-able player class for both local and mp.
 * @author Xolitude
 * @since December 15, 2018
 */
public abstract class IPlayer {
	
	public enum Direction {
		North(0),
		South(1),
		East(2),
		West(3),
		North_East(4),
		South_East(5),
		South_West(6),
		North_West(7);
		
		private int id;
		
		Direction(int id) {
			this.id = id;
		}
		
		public int getDirection() {
			return id;
		}
	}
	
	protected RaceType race;
	private String name;
	
	protected Animation northAnimation;
	protected Animation eastAnimation;
	protected Animation southAnimation;
	protected Animation westAnimation;

	protected float x, y;
	
	protected boolean isMovingUp, isMovingDown, isMovingLeft, isMovingRight;
	protected Direction direction = Direction.North;
	
	protected Zones zone;

	public IPlayer(RaceType race, String name) {
		this.race = race;
		this.name = name;
		
		initAnimations();
	}
	
	/**
	 * Create the animations.
	 */
	public abstract void initAnimations();
	
	public abstract void tick(WoW engine, DisplayManager display, double delta);
	public abstract void render(WoW engine, DisplayManager display, Graphics2D graphics);
	
	// DEBUG: Testing purposes.
	public abstract void OnMove();
	
	public void spawn(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setDirection(int direction) {
		for (Direction d : Direction.values()) {
			if (d.id == direction) {
				this.direction = d;
			}
		}
	}
	
	public void setZone(int zone) {
		for (Zones z : Zones.values()) {
			if (z.getId() == zone) {
				this.zone = z;
			}
		}
	}
	
	/** Begin player-movement variables. **/
	public void setMovingUp(boolean isMovingUp) {
		if (!isMovingDown)
			this.isMovingUp = isMovingUp;
	}

	public void setMovingDown(boolean isMovingDown) {
		if (!isMovingUp)
			this.isMovingDown = isMovingDown;
	}

	public void setMovingLeft(boolean isMovingLeft) {
		if (!isMovingRight)
			this.isMovingLeft = isMovingLeft;
	}

	public void setMovingRight(boolean isMovingRight) {
		if (!isMovingLeft)
			this.isMovingRight = isMovingRight;
	}
	
	public void setX(float newX) {
		x = newX;
	}
	
	public void setY(float newY) {
		y = newY;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	/** End player-movement variables. **/
	
	/**
	 * Get the race-type for our player.
	 * @return race
	 */
	public RaceType getRaceType() {
		return race;
	}
	
	/**
	 * Get the name of our player.
	 * @return name
	 */
	public String getName() {
		return name;
	}
}
