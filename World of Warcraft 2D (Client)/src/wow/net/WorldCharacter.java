package wow.net;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import wow.WoW;
import wow.gfx.Animation;
import wow.manager.DisplayManager;
import wow.manager.InputManager;
import wow.manager.NetworkManager;
import wow.manager.WoWManager.RaceType;
import wow.net.IPlayer.Direction;

public class WorldCharacter extends IPlayer {

	private PlayerController controller;
	private boolean hasSentIdle = false;

	public WorldCharacter(RaceType race, String name) {
		super(race, name);
		controller = new PlayerController(this);
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
		controller.tick(display.getInput());
		
		if (isMovingUp)
			direction = Direction.North;
		
		if (isMovingRight) {
			direction = Direction.East;
			eastAnimation.tick(delta);
		}
		
		if (isMovingUp && isMovingRight)
			direction = Direction.North_East;
		
		if (isMovingUp || isMovingUp && isMovingRight || isMovingUp && isMovingLeft)
			northAnimation.tick(delta);
		
		if (isMovingDown)
			direction = Direction.South;
		
		if (isMovingDown && isMovingRight)
			direction = Direction.South_East;
		
		if (isMovingLeft) {
			direction = Direction.West;
			westAnimation.tick(delta);
		}
		
		if (isMovingUp && isMovingLeft) 
			direction = Direction.North_West;
		
		if (isMovingDown && isMovingLeft)
			direction = Direction.South_West;
		
		if (isMovingDown || isMovingDown && isMovingRight || isMovingDown && isMovingLeft)
			southAnimation.tick(delta);
		
		OnMove();
	}
	
	@Override
	public void OnMove() {
		if (isMovingUp || isMovingDown || isMovingLeft || isMovingRight) {
			hasSentIdle = false;
			NetworkManager.SendMovement(direction.getDirection(), true);
		} else {
			if (!hasSentIdle) {
				NetworkManager.SendMovement(direction.getDirection(), false);
				hasSentIdle = true;
			}
		}
	}

	@Override
	public void render(WoW engine, DisplayManager display, Graphics2D graphics) {
		if (isMovingUp) {
			northAnimation.render(graphics, x, y);
		} else if (isMovingDown) {
			southAnimation.render(graphics, x, y);
		} else if (isMovingLeft) {
			westAnimation.render(graphics, x, y);
		} else if (isMovingRight) {
			eastAnimation.render(graphics, x, y);
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
	
	/**
	 * Handles player-specific input.
	 * @author Xolitude
	 * @since December 15, 2018
	 */
	private static class PlayerController {
		
		public enum Key {
			Move_Up(KeyEvent.VK_W, 'w'),
			Move_Down(KeyEvent.VK_S, 's'),
			Move_Left(KeyEvent.VK_A, 'a'),
			Move_Right(KeyEvent.VK_D, 'd');
			
			private int keyCode;
			private char key;
			private boolean isDown;
			
			Key(int keyCode, char key) {
				this.keyCode = keyCode;
				this.key = key;
			}
		}
		
		private WorldCharacter player;
		
		public PlayerController(WorldCharacter player) {
			this.player = player;
		}
		
		public void tick(InputManager input) {
			for (Key key : Key.values()) {
				if (input.isKeyDown(key.keyCode)) {
					key.isDown = true;
				} else {
					key.isDown = false;
				}
			}
			
			for (Key key : Key.values()) {
				if (key.isDown) {
					switch (key.key) {
					case 'w':
						player.setMovingUp(true);
						break;
					case 's':
						player.setMovingDown(true);
						break;
					case 'a':
						player.setMovingLeft(true);
						break;
					case 'd':
						player.setMovingRight(true);
						break;
					}
				} else {
					switch (key.key) {
					case 'w':
						player.setMovingUp(false);
						break;
					case 's':
						player.setMovingDown(false);
						break;
					case 'a':
						player.setMovingLeft(false);
						break;
					case 'd':
						player.setMovingRight(false);
						break;
					}
				}
			}
		}
	}
}
