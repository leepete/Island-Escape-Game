package model.squares;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Player;
import model.Room;
import model.enums.KeyType;
import model.items.Item;
import model.items.KeyItem;

/**
 * DoorSquare extends BlankSquare and is responsible for moving player between rooms. Doors know where they are and what room they are in. 
 * Doors contain the door they are connected to, and use it to move players to another room.
 * @author Nathan Larson 300334702
 */
public class DoorSquare extends BlankSquare implements Square {
	
	private Room currentRoom;
	private DoorSquare connectedDoor;
	private ArrayList<Byte> playerWhitelist = new ArrayList<Byte>();
	private boolean locked;
	private KeyType type;
	private Point location;

	/**
	 * DoorSquare constructor
	 * @param locked does the door need to be unlocked before its usable
	 * @param type key of matching type can unlock this door
	 * @param x column door is in
	 * @param y row door is in
	 * @param dark whether or not the square is dark or lit
	 */
	public DoorSquare(boolean locked, KeyType type, int x, int y, boolean dark, byte uid){
		super(dark, (byte) (uid+type.ordinal()));
		this.locked = locked;
		this.type = type;
		this.location = new Point(x,y);
	}
	
	/**
	 * Alternate constructor used when loading to create a square that may already be
	 * occupied by a player and/or an item
	 * 
	 * @param dark
	 * @param item
	 * @param occupant
	 * @param uid
	 * @author juddwill
	 */
	public DoorSquare(boolean locked, KeyType type, int x, int y, boolean dark, Player player, byte uid, ArrayList<Byte> playerWhitelist){
		super(dark, null, player, uid);
		this.locked = locked;
		this.type = type;
		this.location = new Point(x,y);
		this.playerWhitelist = playerWhitelist;
	}

	/**
	 * If the inventory contains a key to unlock the door, use it and remove the key from the inventory
	 * @param inventory the players inventory to search through
	 */
	public boolean unlock(Player player){
		Item[] inventory = player.getInventory();
		if(!locked) return false;
		for(int i = 0; i < Player.INVEN_SIZE; i++){
			if(inventory[i] instanceof KeyItem){
				if(((KeyItem) inventory[i]).getType() == type){
					playerWhitelist.add(player.uid);
					connectedDoor.getWhitelist().add(player.uid);
					inventory[i] = null;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Move the player through the door, updating light if its dark
	 */
	public void use(Player player){
		if(locked && !playerWhitelist.contains(player.uid)) unlock(player);
		if(playerWhitelist.contains(player.uid) || !locked){
			
			currentRoom.remove(player);
			currentRoom.updateAllLight();
			
			connectedDoor.moveTo(player);
			connectedDoor.addToRoom(player);
			
			player.setLocation(connectedDoor.getLocation().x, connectedDoor.getLocation().y );
			
			if(connectedDoor.getRoom().isDark()) 
				connectedDoor.getRoom().updateAllLight();
			
			occupant = null;
		}
	}
	
	@Override
	public void moveTo(Player p){
		super.moveTo(p);
	}
	
	public void addToRoom(Player p){
		p.setRoom(currentRoom);
		currentRoom.addPlayer(p);
	}

	@Override
	public boolean placeItem(Item i) {
		// Can't place item on a door square
		return false;
	}
	
	public DoorSquare connectedDoor(){
		return this.connectedDoor;
	}
	
	public ArrayList<Byte> getWhitelist(){
		return playerWhitelist;
	}
	
	public boolean isLocked(){
		return locked;
	}

	/**
	 * Either locks or unlocks the door, and its connected door. 
	 * @param locked true for locking the door or false for unlocking
	 */
	public void setLocked(boolean locked){
		this.locked = locked;
		if(connectedDoor.isLocked() != locked)
			connectedDoor.setLocked(locked);
	}
	
	public Room getRoom(){
		return currentRoom;
	}
	
	public void setRoom(Room r){
		this.currentRoom = r;
	}
	
	public Point getLocation(){
		return location;
	}

	public void setConnectedDoor(DoorSquare d){
		this.connectedDoor = d;
	}
	
	public DoorSquare getConnectedDoor() {
		return connectedDoor;
	}
	
	public KeyType getKeyType() {
		return type;
	}
	
	@Override
	public byte getUid() {
		return uid;
	}

}