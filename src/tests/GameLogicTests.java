package tests;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.List;

import org.junit.Test;

import model.Player;
import model.Room;
import model.enums.Direction;
import model.enums.KeyType;
import model.enums.Status;
import model.items.Chest;
import model.items.DisorientateTrap;
import model.items.Item;
import model.items.KeyItem;
import model.items.ScoreTrap;
import model.items.StunTrap;
import model.items.TeleportTrap;
import model.items.Torch;
import model.items.ScoreItem;
import model.items.TrapItem;
import model.items.Tree;
import model.squares.BlankSquare;
import model.squares.DoorSquare;
import model.squares.Square;
import model.squares.WallSquare;
import view.Renderer;

public class GameLogicTests {

	@Test
	public void playerMoveUp(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(1,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(1, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		p.movePlayer(Direction.UP);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 4)).isOccupied(), true);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).isOccupied(), false);
		assertEquals(p.getLocation().x == 1 && p.getLocation().y == 4, true);
	}
	
	@Test
	public void playerInvalidMove(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(1,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(1, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		p.movePlayer(Direction.LEFT);
		p.movePlayer(Direction.LEFT);
		p.movePlayer(Direction.LEFT);
		assertEquals(((BlankSquare) rooms[0].getSquare(0, 5)).isOccupied(), true);
		assertEquals(p.getLocation().x == 0 && p.getLocation().y == 5, true);
	}

	@Test
	public void playerMoveAround01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(1,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(1, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.DOWN);
		p.movePlayer(Direction.LEFT);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).isOccupied(), false);
		assertEquals(((BlankSquare) rooms[0].getSquare(2, 6)).isOccupied(), true);
		assertEquals(p.getLocation().x == 2 && p.getLocation().y == 6, true);
	}

	@Test
	/**
	 * Movement is blocked by a tree
	 */
	public void playerMoveAround02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(1,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(1, 5)).moveTo(p);
		((BlankSquare) rooms[0].getSquare(2, 5)).setItem(new Tree((byte) 3));
		rooms[0].addPlayer(p);
		p.movePlayer(Direction.RIGHT);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).isOccupied(), true);
		assertEquals(((BlankSquare) rooms[0].getSquare(2, 5)).isOccupied(), false);
		assertEquals(p.getLocation().x == 1 && p.getLocation().y == 5, true);
	}
	
	@Test
	/**
	 * Movement around a dark room
	 */
	public void playerMoveAround03(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[1], new Point(1,5), (byte) 0);
		((BlankSquare) rooms[1].getSquare(1, 5)).moveTo(p);
		assertEquals(((BlankSquare) rooms[1].getSquare(1, 5)).isOccupied(), true);

		rooms[1].addPlayer(p);
		p.movePlayer(Direction.RIGHT);
		assertEquals(((BlankSquare) rooms[1].getSquare(1, 5)).isOccupied(), false);
		assertEquals(((BlankSquare) rooms[1].getSquare(2, 5)).isOccupied(), true);
		assertEquals(p.getLocation().x == 2 && p.getLocation().y == 5, true);
	}

	@Test
	/**
	 * Try to open a locked door, should do nothing
	 */
	public void useLockedDoor(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		((DoorSquare) rooms[0].getSquare(0, 5)).use(p);
		assertEquals(((BlankSquare) rooms[0].getSquare(0, 5)).isOccupied(), true);
		assertEquals(p.getLocation().x == 0 && p.getLocation().y == 5, true);
	}

	@Test
	/**
	 * Attempt to unlock a door without the key, should do nothing
	 */
	public void unlockDoor01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		((DoorSquare) rooms[0].getSquare(0, 5)).unlock(p);
		assertEquals(((DoorSquare) rooms[0].getSquare(0, 5)).isLocked(), true);
	}

	@Test
	/**
	 * Attempt to unlock a door with the key, should work
	 */
	public void unlockDoor02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		p.addItem(new KeyItem(KeyType.BLUE));
		((DoorSquare) rooms[0].getSquare(0, 5)).unlock(p);
		assertEquals(((DoorSquare) rooms[0].getSquare(0, 5)).getWhitelist().contains(p.uid), true);
		assertEquals(p.hasItem(new KeyItem(KeyType.BLUE)), false);
	}

	@Test
	/**
	 * Attempt to unlock a door with the wrong key, shouldn't work
	 */
	public void unlockDoor03(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		p.addItem(new KeyItem(KeyType.GREEN));
		((DoorSquare) rooms[0].getSquare(0, 5)).unlock(p);
		assertEquals(((DoorSquare) rooms[0].getSquare(0, 5)).getWhitelist().contains(p), false);
		assertEquals(p.hasItem(new KeyItem(KeyType.GREEN)), true);
	}

	@Test
	/**
	 * Unlock a door with the key and use it to get to the other room
	 */
	public void unlockDoorAndUse01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		p.addItem(new KeyItem(KeyType.BLUE));

		((DoorSquare) rooms[0].getSquare(0, 5)).unlock(p);

		assertEquals(((DoorSquare) rooms[0].getSquare(0, 5)).getWhitelist().contains(p.uid), true);
		assertEquals(p.hasItem(new KeyItem(KeyType.BLUE)), false);

		((DoorSquare) rooms[0].getSquare(0, 5)).use(p);

		assertEquals(((BlankSquare) rooms[1].getSquare(14, 5)).isOccupied(), true);
		assertEquals(((BlankSquare) rooms[0].getSquare(0, 5)).isOccupied(), false);
		assertEquals(rooms[0].getOccupants().isEmpty(), true);
		assertEquals(rooms[1].getOccupants().isEmpty(), false);
		assertEquals(p.getLocation().x == 14 && p.getLocation().y == 5, true);
	}

	@Test
	/**
	 * Unlock the door with a key and use it to get to the other room and them come back into the original room
	 */
	public void unlockDoorAndUse02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		KeyItem k = new KeyItem(KeyType.BLUE);
		p.addItem(k);

		assertEquals(p.hasItem(k), true);

		((DoorSquare) rooms[0].getSquare(0, 5)).unlock(p);


		assertEquals(((DoorSquare) rooms[0].getSquare(0, 5)).getWhitelist().contains(p.uid), true);
		assertEquals(p.hasItem(k), false);

		((DoorSquare) rooms[0].getSquare(0, 5)).use(p);
		((DoorSquare) rooms[1].getSquare(14, 5)).use(p);

		assertEquals(((BlankSquare) rooms[0].getSquare(0, 5)).isOccupied(), true);
		assertEquals(((BlankSquare) rooms[1].getSquare(14, 5)).isOccupied(), false);
		assertEquals(rooms[0].getOccupants().isEmpty(), false);
		assertEquals(rooms[1].getOccupants().isEmpty(), true);
		assertEquals(p.getLocation().x == 0 && p.getLocation().y == 5, true);
	}

	@Test
	/**
	 * Pick up a key after moving onto its square, but make sure its still there
	 */
	public void pickupItem01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		KeyItem k = new KeyItem(KeyType.BLUE);
		((BlankSquare) rooms[0].getSquare(1, 5)).placeItem(k);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, false);

		p.movePlayer(Direction.RIGHT);
		assertEquals(p.hasItem(k), false);
		
		p.pickUpItem();
		
		assertEquals(p.getInventory()[1] == null, true);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == k, true);
		assertEquals(p.hasItem(k), true);
	
		p.pickUpItem(); //Shouldn't be able to pick it up again
		assertEquals(p.getInventory()[1] == null, true);
		
	}

	@Test
	/**
	 * Move over a score pickup and auto pick it up and add the score to the player
	 */
	public void pickupItem02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		ScoreItem s = new ScoreItem(50);
		((BlankSquare) rooms[0].getSquare(1, 5)).placeItem(s);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, false);

		p.movePlayer(Direction.RIGHT);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, true);
		assertEquals(p.getScore() == 50, true);
	}

	@Test
	/**
	 * Move over and pickup a Trap item
	 */
	public void pickupItem03(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		TrapItem t = new DisorientateTrap();
		((BlankSquare) rooms[0].getSquare(1, 5)).setItem(t);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == t, true);

		p.movePlayer(Direction.RIGHT);
		p.pickUpItem();

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, true);
		assertEquals(p.hasItem(t), true);

		p.movePlayer(Direction.RIGHT);
		p.placeItem(0);

		assertEquals(((BlankSquare) rooms[0].getSquare(2, 5)).getItem() == t, true);
		assertEquals(p.hasItem(t), false);
	}

	@Test
	/**
	 * Trigger a Disorientate trap that causes random movement
	 */
	public void triggerTrap01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		TrapItem t = new DisorientateTrap();
		((BlankSquare) rooms[0].getSquare(1, 5)).placeItem(t);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == t, true);

		p.movePlayer(Direction.RIGHT);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, true);
		assertEquals(p.getStatus() == Status.DISORIENTATED, true);

		p.movePlayer(Direction.RIGHT);
		assertEquals(p.getLocation().x == 1 && p.getLocation().y == 5, false);

	}

	@Test
	/**
	 * Trigger a Score trap that causes the play to lose points
	 */
	public void triggerTrap02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		TrapItem t = new ScoreTrap();
		((BlankSquare) rooms[0].getSquare(1, 5)).placeItem(t);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == t, true);

		p.addScore(125);
		assertEquals(p.getScore() == 125, true);

		p.movePlayer(Direction.RIGHT);
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, true);	
		assertEquals(p.getScore() == 0, true);		
	}
	
	@Test
	/**
	 * Trigger a Teleport trap that causes the player go back to the start room
	 */
	public void triggerTrap04(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		TrapItem t = new TeleportTrap(rooms[1]);
		((BlankSquare) rooms[0].getSquare(1, 5)).placeItem(t);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == t, true);
		assertEquals(p.getRoom() == rooms[0], true);
		
		p.movePlayer(Direction.RIGHT);
		
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, true);	
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getPlayer() == p, false);	
		assertEquals(p.getRoom() == rooms[1], true);	
		
	}
	
	@Test
	/**
	 * Trigger a Stun trap that causes the player to be unable to move
	 */
	public void triggerTrap03(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[0], new Point(0,5), (byte) 0);
		((BlankSquare) rooms[0].getSquare(0, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		TrapItem t = new StunTrap();
		((BlankSquare) rooms[0].getSquare(1, 5)).placeItem(t);

		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == t, true);
		
		p.movePlayer(Direction.RIGHT);
		
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getItem() == null, true);	
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getPlayer() == p, true);	
		
		p.movePlayer(Direction.RIGHT);
		
		assertEquals(((BlankSquare) rooms[0].getSquare(1, 5)).getPlayer() == p, true);	
		
	}

	@Test
	/**
	 * Pick up and use a torch in a dark room, area around player should light up
	 * Make sure test is centered in room with no walls around as I removed the instanceof and bounds checks
	 */
	public void useTorch01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[1], new Point(4,5), (byte) 0);
		((BlankSquare) rooms[1].getSquare(4, 5)).moveTo(p);
		rooms[1].addPlayer(p);
		Torch t = new Torch();
		((BlankSquare) rooms[1].getSquare(5, 5)).placeItem(t);

		assertEquals(((BlankSquare) rooms[1].getSquare(5, 5)).getItem() == t, true);
		for(int col = p.getLocation().x-Torch.RADIUS; col <= p.getLocation().x+Torch.RADIUS; col++){
			for(int row = p.getLocation().y-Torch.RADIUS; row <= p.getLocation().y+Torch.RADIUS; row++){
				if(col != p.getLocation().x && row != p.getLocation().y) 
					assertEquals(((BlankSquare) rooms[1].getSquare(col, row)).isDark(), true);	
			}
		}

		p.movePlayer(Direction.RIGHT);
		p.pickUpItem();

		System.out.println(((BlankSquare) rooms[1].getSquare(5, 5)).getItem());
		assertEquals(((BlankSquare) rooms[1].getSquare(4, 5)).isOccupied(), false);
		assertEquals(((BlankSquare) rooms[1].getSquare(5, 5)).isOccupied(), true);
		assertEquals(((BlankSquare) rooms[1].getSquare(5, 5)).getItem() == t, false);
		assertEquals(p.hasItem(t), true);
		for(int col = p.getLocation().x-Torch.RADIUS; col <= p.getLocation().x+Torch.RADIUS; col++){
			for(int row = p.getLocation().y-Torch.RADIUS; row <= p.getLocation().y+Torch.RADIUS; row++){
				if((col == p.getLocation().x-Torch.RADIUS && (row == p.getLocation().y-Torch.RADIUS || row ==p.getLocation().y+Torch.RADIUS))
						|| (col == p.getLocation().x+Torch.RADIUS && (row == p.getLocation().y-Torch.RADIUS || row ==p.getLocation().y+Torch.RADIUS))){ //If point is on the corner of the lit area, make it dark to give a more circular feel
					assertEquals(((BlankSquare) rooms[1].getSquare(col, row)).isDark(), true);	
				}else
					assertEquals(((BlankSquare) rooms[1].getSquare(col, row)).isDark(), false);	
				
			}
		}
	}
	
	@Test
	/**
	 * Pick up and use a torch in a dark room, area around player should light up
	 * Testing for out of bounds and class casting errors
	 */
	public void useTorch02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Guy", rooms[1], new Point(4,5), (byte) 0);
		((BlankSquare) rooms[1].getSquare(4, 5)).moveTo(p);
		rooms[1].addPlayer(p);
		Torch t = new Torch();
		((BlankSquare) rooms[1].getSquare(5, 5)).placeItem(t);

		assertEquals(((BlankSquare) rooms[1].getSquare(5, 5)).getItem() == t, true);

		p.movePlayer(Direction.RIGHT);
		p.pickUpItem();

		assertEquals(((BlankSquare) rooms[1].getSquare(5, 5)).getItem() == t, false);
		assertEquals(p.hasItem(t), true);
		
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.RIGHT);
		p.movePlayer(Direction.RIGHT);
	}
	
	@Test
	/**
	 * Add an item to a container
	 */
	public void addToContainer(){
		Room[] rooms = setUpRooms();

		Chest con = new Chest();
		((BlankSquare) rooms[0].getSquare(5, 5)).placeItem(con);

		assertEquals(((BlankSquare) rooms[0].getSquare(5, 5)).getItem() == con, true);

		Item item = new DisorientateTrap();
		

		assertEquals(con.addItem(item), true);
		assertEquals(con.hasItem(item), true);
	}
	
	@Test
	/**
	 * add an item to the chest through the player, then remove an item from a container by index so the player gets it back
	 */
	public void removeFromContainer01(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Test", rooms[0], new Point(4,5), (byte)0);

		Chest con = new Chest();
		((BlankSquare) rooms[0].getSquare(5, 5)).placeItem(con);
		
		((BlankSquare) rooms[0].getSquare(4, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		
		p.movePlayer(Direction.RIGHT);

		Torch item = new Torch();
		
		p.addItem(item);
		p.placeItem(0);
		
		assertEquals(p.getConnection() == con, true);
		assertEquals(con.hasItem(item), true);
		assertEquals(p.hasItem(item), false);
		
		con.transferItem(0, p.uid);
		
		assertEquals(con.hasItem(item), false);
		assertEquals(p.hasItem(item), true);
	}
	
	@Test
	/**
	 * Try removing from a chest when the player inventory is full
	 */
	public void removeFromContainer02(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Test", rooms[0], new Point(4,5), (byte)0);

		Chest con = new Chest();
		((BlankSquare) rooms[0].getSquare(5, 5)).placeItem(con);
		
		((BlankSquare) rooms[0].getSquare(4, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		
		p.movePlayer(Direction.RIGHT);

		TrapItem item = new StunTrap();
		
		p.addItem(item);
		p.placeItem(0);
		
		for(int i = 0; i < Player.INVEN_SIZE; i++){
			p.addItem(new Torch());
		}
		
		
		assertEquals(p.getConnection() == con, true);
		assertEquals(con.hasItem(item), true);
		assertEquals(p.hasItem(item), false);
		assertEquals(p.getInventory()[Player.INVEN_SIZE-1] != null, true);
		
		con.transferItem(0, p.uid);
		
		assertEquals(con.hasItem(item), true);
		assertEquals(p.hasItem(item), false);
	}
	
	@Test
	/**
	 * The player should store the tree after moving into it
	 */
	public void ConnectToTree(){
		Room[] rooms = setUpRooms();
		Player p = new Player("Test", rooms[0], new Point(4,5), (byte)0);
		
		((BlankSquare) rooms[0].getSquare(4, 5)).moveTo(p);
		rooms[0].addPlayer(p);
		
		assertEquals(((BlankSquare) rooms[0].getSquare(4, 5)).getPlayer() == p, true);	
		
		Tree tree = new Tree((byte)0);

		((BlankSquare) rooms[0].getBoard()[5][5]).setItem(tree);
		
		p.movePlayer(Direction.RIGHT);
		
		assertEquals(p.getConnection() == tree, true);
		assertEquals(((BlankSquare) rooms[0].getSquare(4, 5)).getPlayer() == p, true);	
		
	}

	/**
	 * Set up two blank rooms with a wall on the edges connected to each other via a door
	 */
	public Room[] setUpRooms(){
		Square[][] board = new Square[15][10];
		Square[][] board2 = new Square[15][10];
		for (int y = 0; y != 10; ++y){
			for (int x = 0; x != 15; ++x) {
				if(x == 0 || y == 0 || x == 14 || y == 9){
					board[x][y] = new WallSquare(false, (byte) 9);
					board2[x][y] = new WallSquare(true, (byte) 9);
				}
				else{
					board[x][y] = new BlankSquare(false, (byte) 1);
					board2[x][y] = new BlankSquare(true, (byte) 1);
				}
			}
		}
		Room r = new Room(board, false, new Point(0,0));
		Room r2 = new Room(board2, true, new Point(0,1));
		DoorSquare d = new DoorSquare(true, KeyType.BLUE,0,5, false, (byte) 5);
		DoorSquare d2 = new DoorSquare(true, KeyType.BLUE,14,5, true, (byte) 5);
		d.setConnectedDoor(d2);
		d2.setConnectedDoor(d);
		d.setRoom(r);
		d2.setRoom(r2);
		board[0][5] = d;
		board2[14][5] = d2;
		Room[] rooms = {r,r2};
		return rooms;
	}

}