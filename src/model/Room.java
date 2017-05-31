package model;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.enums.Direction;
import model.enums.KeyType;
import model.items.*;
import model.squares.*;
import view.GUI;

/**
 * A room is represented by a 2D array of Square. The room is responsible for moving players and setting what squares are dark or lit.
 * It knows who's in the room, where its entrances are and its name and description.
 * @author Nathan Larson 300334702
 */
public class Room {

	private Square[][] board;
	private List<Player> occupants;
	private DoorSquare[] entrances; //[0] North entrance, [1] East entrance, [2] South entrance, [3] West entrance
	private boolean dark;
	private Point position; //In relation to the rooms[][] array

	public final int width;
	public final int height;

	private DataOutputStream dout;

	public Room(Square[][] board, boolean dark, Point pos){
		this.board = board;
		this.dark = dark;
		this.position = pos;
		width = board.length;
		height = board[0].length;

		occupants = new ArrayList<Player>();
	}

	public void addPlayer(Player player) {
		occupants.add(player);
	}

	/**
	 * Iterates over the board and finds all doors and adds them to the entrances array in their correct position
	 */
	public void setUpDoors(){
		
		this.entrances = new DoorSquare[4];

		//Find the entrances on the board
		for(int col = 0; col < width; col++){
			for(int row = 0; row < height; row++){
				if( (col != 0 && col != board.length-1) && (row != 0 && row != height-1) ) continue; //doors must be on the edge of map
				if(board[col][row] instanceof DoorSquare){
					if(row == 0) entrances[0] = (DoorSquare) board[col][row];                 //Door is on northern edge
					if(col == width-1) entrances[1] = (DoorSquare) board[col][row]; //Door is on eastern edge
					if(row == height-1) entrances[2] = (DoorSquare) board[col][row];    //Door is on southern edge
					if(col == 0) entrances[3] = (DoorSquare) board[col][row];                 //Door is on western edge
				}
			}
		}
	}
	
	/**
	 * removes a player from the occupants list of the room
	 * returns false is the player is not in the list
	 */
	public boolean remove(Player p){
		if(occupants.contains(p)){
			occupants.remove(p);
			return true;
		}
		return false;
	}

	public boolean isDark() {
		return dark;
	}

	/**
	 * Picks a random space on the board and moves the player there if able to 
	 * otherwise recalls the method until a space is found
	 */
	public void teleportPlayer(Player p){
		Point location = p.getLocation();
		int col = (int) (Math.random()*(board.length-2)+1);
		int row = (int) (Math.random()*(board[0].length-2)+1);
		if(board[col][row].canMoveTo()){
			((BlankSquare) board[col][row]).moveTo(p);
			occupants.add(p);
			p.setRoom(this);
			location.x = col;
			location.y = row;
		}else
			teleportPlayer(p);
	}

	/**
	 * Moves a player in a certain direction, assumes the movement is valid
	 * @param p player to be moved
	 * @param dir direction to move in
	 */
	public void movePlayer(Player p, Direction dir) {
		Point location = p.getLocation();
		switch(dir){
		case UP:
			((BlankSquare) board[location.x][location.y]).removeOccupant();
			location.y -= 1;
			((BlankSquare) board[location.x][location.y]).moveTo(p);
			break;
		case RIGHT:
			((BlankSquare) board[location.x][location.y]).removeOccupant();
			location.x += 1;
			((BlankSquare) board[location.x][location.y]).moveTo(p);
			break;
		case DOWN:
			((BlankSquare) board[location.x][location.y]).removeOccupant();
			location.y += 1;
			((BlankSquare) board[location.x][location.y]).moveTo(p);
			break;
		case LEFT:
			((BlankSquare) board[location.x][location.y]).removeOccupant();
			location.x -= 1;
			((BlankSquare) board[location.x][location.y]).moveTo(p);
			break;
		}
		if(dark) updateAllLight();
	}

	/**
	 * wrapper method for updateLight, calls it for each player in room
	 */
	public void updateAllLight(){
		
		removeLight();
		
		for(Player p : occupants){
			updateLight(p); //Update all players light
		}
	}
	
	/**
	 * Sets the squares around the player to be lit, using the Torch.RADIUS constant
	 * @param location of the player to update light for
	 * @param dir Direction that the player moved, null if no movement
	 */
	public void updateLight(Player p){
		
		Point location = p.getLocation();
		int lightRadius = Torch.RADIUS;
		
		if(!p.hasItem(new Torch())) return;

		for(int col = location.x-lightRadius; col <= location.x+lightRadius; col++){ //Loop around the players area to light it up
			for(int row = location.y-lightRadius; row <= location.y+lightRadius; row++){
				if(col >= 0 && col < board.length && row >= 0 && row < board[0].length){ //Make sure point is on board
						if((col == location.x-lightRadius && (row == location.y-lightRadius || row ==location.y+lightRadius))
								|| (col == location.x+lightRadius && (row == location.y-lightRadius || row ==location.y+lightRadius))){ //If point is on the corner of the lit area, make it dark to give a more circular feel
							if(board[col][row].isDark()) board[col][row].setDark(true);
						}else
							board[col][row].setDark(false);
				}
			}
		}
	}

	/**
	 * Removes all light from around the location, returns if the room issn't dark
	 * @param location to have light removed from around it
	 */
	public void removeLight(){
		
		if(!dark) return;
		
		for(int col = 0; col < board.length; col++){
			for(int row = 0; row < board[0].length; row++){
						board[col][row].setDark(true);
			}
		}
	}

	/**
	 * Returns a list of the players currently in the room
	 * @return a List of Player
	 */
	public List<Player> getOccupants() {
		return occupants;
	}

	/**
	 * Returns an array which represents the doors in the room
	 * 0 represents the north door, 1 is east, 2 is south and 3 is west
	 */
	public DoorSquare[] getEntrances(){
		return entrances;
	}

	/**
	 * Returns the square at position x,y as long as its a valid position on the board, otherwise returns null
	 */
	public Square getSquare(int x, int y){
		if(x < board.length && y < board[0].length && x >= 0 && y >= 0)
			return board[x][y];
		else
			return null;
	}

	/**
	 * Returns the board which is a 2D array of Square
	 */
	public Square[][] getBoard() {
		return board;
	}
	
	public Point getPosition(){
		return position;
	}

	/**
	 * Converts this room into a byte array holding all
	 * the visual information required by the renderer.
	 * @return
	 * @throws IOException
	 * @author Peter Lee 300343606
	 */
	public synchronized byte[] toByteArray(Direction dir, byte uid) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(bout);
		int colStart, colEnd, rowStart, rowEnd, it;
		if(dir == Direction.NORTH) {
			for(int c = width-1; c >= 0; c--) {
				for(int r = 0; r < height; r++) {
					toBytes(r, c, uid);
				}
			}
		} else if (dir == Direction.SOUTH) {
			for(int c = 0; c < width; c++) {
				for(int r = height-1; r >= 0; r--) {
					toBytes(r, c, uid);
				}
			}
		} else if (dir == Direction.EAST) {
			for(int r = 0; r < height; r++) {
				for(int c = 0; c < width; c++) {
					toBytes(r, c, uid);
				}
			}
		} else if(dir == Direction.WEST) {
			for(int r = height-1; r >= 0; r--) {
				for(int c = width-1; c >= 0; c--){

					toBytes(r, c, uid);
				}
			}
		}
		dout.flush();

		return bout.toByteArray();
	}

	/**
	 * Converts the square at the passed positions to a byte array
	 * @param r, row
	 * @param c, column
	 * @param plUid, player UID
	 * @throws IOException
	 * @author Darren Hobern 300341002
	 */
	private synchronized void toBytes(int r, int c, byte plUid) throws IOException {
		Square sqr = board[c][r];
		boolean blacklisted = false;
		//Get the type of square
		byte uid = sqr.getUid();
		if(sqr.isDark()) {
			byte offset = GUI.GROUND_DARK_OFFSET;
			if(sqr instanceof DoorSquare) {
				offset = GUI.DOOR_DARK_OFFSET;
			} else if(sqr instanceof WallSquare) {
				offset = GUI.WALL_DARK_OFFSET;
			}
			uid += offset;
		}
		dout.writeByte(uid);
		//If the square is dark
		dout.writeBoolean(sqr.isDark());
		//If the square is a blank square, it might have items/players on it
		if(sqr instanceof BlankSquare) {
			Item item = ((BlankSquare)sqr).getItem();
			if(item != null) {
				uid = item.getUid();
				if(item instanceof TrapItem) {
					if(((TrapItem)item).isArmed()) {
						uid += GUI.ARMED_OFFSET;
					}
				}
				if(item instanceof KeyItem) {
					blacklisted = ((KeyItem)item).checkIfBlackListed(plUid);
				} else {
					blacklisted = false;
				}
			}
			else {
				blacklisted = false;
				uid = 0;
			}
			dout.writeBoolean(blacklisted);
			dout.writeByte(uid);
			
			Player player = ((BlankSquare)sqr).getPlayer();
			if(player != null) {
				uid = player.uid;
			} else {
				uid = 0;
			}
			dout.writeByte(uid);
		} else if(sqr instanceof DoorSquare) {
			//Can't be blacklisted
			dout.writeBoolean(false);
			//If its a door square there are no items
			dout.writeByte(0); //item uid
			Player player = ((BlankSquare)sqr).getPlayer();
			if(player != null) {
				dout.writeByte(player.uid);
			} else {
				dout.writeByte(0);
			}
		} else { //Must be a wall square, which has neither players nor items
			dout.writeBoolean(false);
			dout.writeByte(0);
			dout.writeByte(0);
		}
	}

}