package wow.server.world;

/**
 * Only used for positional data.
 * @author Xolitude
 * @since December 19, 2018
 */
public class Vector2 {

	private float x;
	private float y;
	private int direction;
	private int zone;
	
	public Vector2(float x, float y, int direction, int zone) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.zone = zone;
	}
	
	public Vector2() {}
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setZone(int zone) {
		this.zone = zone;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public Vector2 getLocation() {
		return new Vector2(x, y, direction, zone);
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public int getZone() {
		return zone;
	}
}
