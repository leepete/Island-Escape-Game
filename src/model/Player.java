package model;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import control.Master;
import game.Main;
import model.enums.Direction;
import model.enums.Status;
import model.items.Chest;
import model.items.Item;
import model.items.KeyItem;
import model.items.LargeItem;
import model.items.Torch;
import model.items.TrapItem;
import model.items.Tree;
import model.items.WinItem;
import model.squares.BlankSquare;
import model.squares.DoorSquare;
import model.squares.Square;

/**
 * Player holds all the infomation each player needs for the game. Player sends move direction to the room they are in, as well as
 * calling methods inside the square its on to place and pickup items.
 * @author Nathan Larson 300334702
 */
public class Player {
	public static final int INVEN_SIZE = 6;
	
	private Item[] inventory;
	private Direction viewDirection = Direction.NORTH;
	private Room currentRoom;
	private Point location; //X and Y = Col and Row on board
	private int score;
	private String name;
	private Status status;
	private LargeItem connectedItem = null;
	private int statusDuration = 0;
	
	private boolean closeChest;

	public final byte uid;

	public Player(String name, Room currentRoom, Point spawn, byte uid){
		this.name = name;
		this.status = Status.NORMAL;
		inventory = new Item[INVEN_SIZE];
		this.currentRoom = currentRoom;
		this.location = new Point(spawn);
		this.uid = uid;
	}

	/**
	 * Alternate constructor used when loading. Uses extra arguments to allow recreation
	 * of a player who is part way through playing the game rather than a blank player.
	 * 
	 * @author juddwill
	 */
	public Player(String name, Item[] inventory, Direction viewDirection, Point location, 
			int score, Status status, byte uid, int statusDuration, Item connectedItem) {
		this.name = name;
		this.inventory = inventory;
		this.viewDirection = viewDirection;
		this.currentRoom = null;
		this.location = location;
		this.score = score;
		this.status = status;
		this.uid = uid;
		this.statusDuration = statusDuration;
	}

	/**
	 * Will either pick up an item if the square has one or will attempt to use a door if standing on one
	 */
	public synchronized void performAction(){
		
		Square currentSquare = currentRoom.getSquare(location.x, location.y);
		
		if(currentSquare instanceof DoorSquare){
			((DoorSquare) currentSquare).use(this);
		}else
			pickUpItem();
	}

	/**
	 * Attempts to add an item to the players inventory, will return false if inventory is full otherwise
	 * adds to the first open slot in inventory
	 * @param item to be added
	 * @return true or false depending on if the item was added or not
	 */
	public synchronized boolean addItem(Item item){
		for(int i = 0; i < INVEN_SIZE; i++){
			if(inventory[i] == null){
				inventory[i] = item;
				if(item instanceof Torch && currentRoom.isDark()) currentRoom.updateAllLight();
				if(item instanceof KeyItem) ((KeyItem) item).addToList(uid);
				return true;
			}
		}
		return false;
	}

	/**
	 * Will attempt to place an item on the current square, and if its successful will remove the item from the inventory and return true
	 * @param index the index in the inventory the item is at
	 * @return will return false if item is null or if unable to place
	 */
	public synchronized boolean placeItem(int index){
		Item i = inventory[index];
		
		Square currentSquare = currentRoom.getSquare(location.x, location.y);
		
		if(i instanceof KeyItem){
			if(connectedItem instanceof Chest) return false;
			((KeyItem) i).removeFromList(uid);
			inventory[index] = null;
			return true;
		}
		
		if(connectedItem != null && connectedItem instanceof Chest){
			if(((Chest) connectedItem).addItem(i)) {
				inventory[index] = null;
				if(i instanceof Torch) currentRoom.updateAllLight();
				return true;
			}
			return false;
		}
		
		if(currentSquare instanceof BlankSquare){ //Shouldn't need to cast as player can only stand on blank squares but if more square types are added
			
			if(((BlankSquare) currentSquare).placeItem(i)){ //If it successfully places
				inventory[index] = null;
				if(i instanceof Torch) currentRoom.updateAllLight();
				return true;
			}
			
		}
		return false;
	}

	/**
	 * If there's an item on the current square and the inventory isn't full will pick up the item on the square
	 */
	public synchronized void pickUpItem(){
		Item item = ((BlankSquare) currentRoom.getSquare(location.x, location.y)).getItem();
		if(item != null){
			
			if(item instanceof KeyItem)
				if(((KeyItem) item).checkIfBlackListed(uid)) return;
			
			if(addItem(item) && !(item instanceof KeyItem)) //If inventory isn't full
				((BlankSquare) currentRoom.getSquare(location.x, location.y)).setItem(null);
		}
	}

	/**
	 * Depending on the current view direction will adjust the players movement to still be up down left or right
	 * @param moveDir the inputed direction
	 * @return the direction that is changed in terms of the view
	 */
	private synchronized Direction changeForView(Direction moveDir){
		int newDir = moveDir.ordinal() - viewDirection.ordinal(); //int represents direction, 0- north, 1 east, 2 south and 3 - west
		newDir %= 4;
		return Direction.values()[newDir+4];
	}
	
	/**
	 * Disconnects from a LargeItem and closes the ui of a chest if its
	 * closing because of movement.
	 * @param closed
	 * @author Darren Hobern 300341002
	 */
	public synchronized void disconnectLargeItem(boolean closed) {
		if(connectedItem != null) {
			if(!closed && connectedItem instanceof Chest) {
				closeChest = true;
			}
			connectedItem.disconnect(this);
			connectedItem = null;
		}
	}
	
	/**
	 * If able to move, updates the moveDirection in terms of view and status and then moves the player
	 * @param moveDir inputed move direction
	 */
	public synchronized void movePlayer(Direction moveDir){
		disconnectLargeItem(false);
		if(status == Status.STUNNED) return;
		if(status == Status.DISORIENTATED){
			connectedItem = null;
			moveRandom();
			return;
		}
		else{
			moveDir = changeForView(moveDir);
			if(canMove(moveDir)){
				connectedItem = null;
				currentRoom.movePlayer(this, moveDir);
			}
		}
	}

	/**
	 * Checks if the player can move in the given direction
	 * @return true if the move is valid
	 */
	private synchronized boolean canMove(Direction moveDir){
		int moveY = 0;
		int moveX = 0;
		if(moveDir == Direction.UP) moveY = -1;
		else if(moveDir == Direction.DOWN)  moveY = 1;
		else if(moveDir == Direction.RIGHT) moveX = 1;
		else if(moveDir == Direction.LEFT) moveX = -1;
		Square squareToMoveTo = currentRoom.getSquare(location.x+moveX, location.y+moveY);
		if(squareToMoveTo != null){
			boolean canMove = squareToMoveTo.canMoveTo();
			if(!canMove) useLargeItem(squareToMoveTo);
			return canMove;
		}
		return false;
	}
	
	public boolean closeChestUi() {
		boolean close = closeChest;
		return close;
	}
	
	public void resetCloseChest() {
		closeChest = false;
	}

	/**
	 * If the squareToMoveTo has a LargeItem on it, use it
	 * @param squareToMoveTo the square that was trying to be moved to by the player
	 * @throws IOException 
	 */
	private synchronized void useLargeItem(Square squareToMoveTo) {
			if(squareToMoveTo instanceof BlankSquare){
			Item item = ((BlankSquare) squareToMoveTo).getItem();
				if(item instanceof LargeItem) {
					connectedItem = ((LargeItem) item).connect(this);
				} 
			}
	}

	/**
	 * Moves the player in a random direction.
	 */
	private synchronized void moveRandom(){
		ArrayList<Direction> validMoves = new ArrayList<Direction>();
		if(canMove(Direction.UP))
			validMoves.add(Direction.UP);
		if(canMove(Direction.RIGHT))
			validMoves.add(Direction.RIGHT);
		if(canMove(Direction.DOWN))
			validMoves.add(Direction.DOWN);
		if(canMove(Direction.LEFT))
			validMoves.add(Direction.LEFT);
		if(validMoves.size() != 0){
			int move = (int) Math.floor(Math.random()*validMoves.size());
			currentRoom.movePlayer(this, validMoves.get(move));
		}
	}

	/**
	 * Returns true if the player has the passed item
	 * @param item
	 * @return
	 */
	public synchronized boolean hasItem(Item item){
		for(int i = 0; i < INVEN_SIZE; i++){
			if(item.equals(inventory[i])) return true;
		}
		return false;
	}
	
	/**
	 * Rotates the players view direction in the passed direction
	 * @param dir
	 */
	public synchronized void rotateViewDirection(Direction dir) {
		if(dir == Direction.CLOCKWISE) {
			if(viewDirection == Direction.NORTH) {
				viewDirection = Direction.EAST;
			} else if(viewDirection == Direction.EAST) {
				viewDirection = Direction.SOUTH;
			} else if(viewDirection == Direction.SOUTH) {
				viewDirection = Direction.WEST;
			} else if(viewDirection == Direction.WEST) {
				viewDirection = Direction.NORTH;
			}
			return;
		} else if(dir == Direction.ANTICLOCKWISE) {
			if(viewDirection == Direction.NORTH) {
				viewDirection = Direction.WEST;
			} else if(viewDirection == Direction.WEST) {
				viewDirection = Direction.SOUTH;
			} else if(viewDirection == Direction.SOUTH) {
				viewDirection = Direction.EAST;
			} else if(viewDirection == Direction.EAST) {
				viewDirection = Direction.NORTH;
			}
			return;
		}
		throw new IllegalArgumentException("Expecting Clockwise or Anticlockwise direction");
	}
	
	/**
	 * Updates the time remaining on a status effect.
	 * @author Darren Hobern 300341002
	 */
	public synchronized void tick() {
		if(statusDuration > 0) {
			statusDuration--;
		}
		//If the status duration has run out then return to normal :)
		if(statusDuration <= 0 && status != Status.NORMAL) {
			status = Status.NORMAL;
		}
	}
	
	/**
	 * Returns a byte array representing the visual information of the player,
	 * score, status, room, inventory
	 * @return
	 * @throws IOException
	 * @author Darren Hobern 300341002
	 */
	public synchronized byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		byte itUid;
		//Score
		dout.writeInt(score);
		//Status
		byte s = (byte) status.ordinal();
		dout.writeByte(s);
		//Room position
		dout.writeByte((byte)currentRoom.getPosition().x);
		dout.writeByte((byte)currentRoom.getPosition().y);
		//Inventory
		for(int i = 0; i < inventory.length; i++) {
			if(inventory[i] == null) {
				itUid = (byte) 0;
			} else {
				itUid = inventory[i].getUid();
			}
			dout.writeByte(itUid);
		}
		dout.flush();
		return bout.toByteArray();
	}

	/**
	 * Adds points to the players score.
	 * @param points
	 * @author Darren Hobern 300341002
	 */
	public synchronized void addScore(int points){
		score += points;
		if(score < 0) score = 0;
	}

	public int getScore(){
		return score;
	}

	public String getName() {
		return name;
	}

	public void setLocation(int x, int y){
		location.setLocation(x, y);
	}
	
	public Point getLocation(){
		return location;
	}

	public Item[] getInventory(){
		return inventory;
	}

	public Direction getDirection(){
		return viewDirection;
	}

	public void setRoom(Room r){
		this.currentRoom = r;
		//master.sendLocation(currentRoom);
	}

	public Room getRoom() {
		return currentRoom;
	}

	/**
	 * Sets the status effect of the player
	 * @param status
	 * @author Darren Hobern 300341002
	 */
	public synchronized void setStatus(Status status) {
		this.status = status;
		if(status == Status.DISORIENTATED) {
			statusDuration = (3*1000/Main.DEFAULT_CLOCK_PERIOD);
		}
		else if(status == Status.STUNNED) {
			statusDuration = (1*1000/Main.DEFAULT_CLOCK_PERIOD);
		}
	}
	
	public LargeItem getConnection(){
		return connectedItem;
	}

	public Status getStatus() {
		return status;
	}
	
	public int getStatusDuration() {
		return statusDuration;
	}

}