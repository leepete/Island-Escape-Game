package model;

import java.util.ArrayList;
import java.util.List;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import model.enums.Direction;
import model.enums.KeyType;
import model.enums.Status;
import model.squares.BlankSquare;
import model.squares.DoorSquare;
import model.squares.Square;
import model.squares.WallSquare;
import view.GUI;
import view.Renderer;
import model.enums.*;
import model.items.*;
import model.squares.*;

/**
 * Represents the board of rooms with a 2D array. The board also generates rooms and the overall map for the game.
 *
 * @author Nathan Larson 300334702
 */
public class Board {

	private List<Player> players;

	private Room[][] rooms;
	private Room startRoom;		
	private Room endRoom;		
	public static final int MAX_ROOM_SIZE = 10;
	public static final int MIN_ROOM_SIZE = 7;
	public static final int START_ROOM_COLUMN = 2;
	public static final int START_ROOM_ROW = 0;

	private Point[] spawnPoints = {new Point(1,1), new Point(2,1), new Point(3,1), new Point(4,1)};
	private int nextSpawnPoint = 0;
	private ByteArrayOutputStream bout;
	private DataOutputStream dout;

	public static byte state = 0; //State of the game
	public static final byte WAITING = 0;
	public static final byte PLAYING = 1;
	public static final byte GAMEOVER = 2;
	public static final byte SAVING = 3;
	public static final byte LOADING = 4;


	private static byte uid = 0;

	public Board(){
		players = new ArrayList<Player>();

		generateMap(5);

		//Create and set starting chset down
		Chest chest = new Chest();
		((BlankSquare) startRoom.getSquare(2, 4)).setItem(chest);

	}

	/**
	 * Generates a new Map size*size rooms
	 * @param size, width and height of the map
	 */
	private void generateMap(int size){
		rooms = new Room[size][size];
		ArrayList<Room> roomsTraversal = new ArrayList<Room>();
		
		//Generate the start room and end room first
		rooms[START_ROOM_COLUMN][START_ROOM_ROW] = generateRoom(false, 6,2,0);
		startRoom = rooms[START_ROOM_COLUMN][START_ROOM_ROW];
		
		rooms[2][2] = generateRoom(false, 5,2,2);
		endRoom = rooms[2][2];

		//Go through the rooms array and generate a new room at every position except the start and end rooms
		for(int col = 0; col < size; col++){
			for(int row = 0; row < size; row++){
				if((row == 0 || row == 2) && col == 2 ){
					continue;
				}
				rooms[col][row] = generateRoom(col != 0 && row != 0 && col != size-1 && row != size-1, getRandomRoomSize(),col,row);
			}
		}
		
		//A traversal of the map possible for players to take
		//Will determine what rooms keys are added to
		roomsTraversal.add(rooms[1][0]);
		roomsTraversal.add(rooms[3][0]);
		roomsTraversal.add(rooms[4][0]);
		roomsTraversal.add(rooms[0][0]);
		roomsTraversal.add(rooms[0][1]);
		roomsTraversal.add(rooms[0][2]);
		roomsTraversal.add(rooms[0][3]);
		roomsTraversal.add(rooms[4][1]);
		roomsTraversal.add(rooms[4][2]);
		roomsTraversal.add(rooms[4][3]);
		roomsTraversal.add(rooms[0][4]);
		roomsTraversal.add(rooms[4][4]);
		roomsTraversal.add(rooms[1][4]);
		roomsTraversal.add(rooms[3][4]);
		roomsTraversal.add(rooms[2][4]);
		roomsTraversal.add(rooms[2][3]);
		roomsTraversal.add(rooms[1][3]);
		roomsTraversal.add(rooms[3][3]);
		roomsTraversal.add(rooms[1][2]);
		roomsTraversal.add(rooms[3][2]);
		roomsTraversal.add(rooms[1][1]);
		roomsTraversal.add(rooms[3][1]);
		roomsTraversal.add(rooms[2][1]); 
		roomsTraversal.add(endRoom);

		setUpDoors(size); //Add doors to the map
		setUpDoorConnections(size); //Add connections between doors
		addTorchesToMap(size); //Add four torches to the map

		KeyItem goldKey = new KeyItem(KeyType.GOLD); //Create gold key to get through the last door
		int caveRoomCol= (int) Math.floor((Math.random()*3));
		int caveRoomRow= (int) Math.floor((Math.random()*2));
		
		if(caveRoomCol == 1 && caveRoomRow == 1) caveRoomCol++; //So gold key doesn't spawn in middle room where its impossible to get
		
		addItemToRoom(rooms[1+caveRoomCol][1+caveRoomRow].getBoard(), goldKey);
		
		startRoom.getEntrances()[3].setLocked(false);
		((BlankSquare) endRoom.getBoard()[2][2]).setItem(new WinItem("The Totem of Victory!"));
		
		addKeysToMap(roomsTraversal);
		
	}

	/**
	 * Returns a random int between MIN_ROOM_SIZE and MAX_ROOM_SIZE
	 */
	public int getRandomRoomSize(){
		double random = Math.floor((Math.random()*(MAX_ROOM_SIZE-MIN_ROOM_SIZE))+MIN_ROOM_SIZE);
		return (int) random;
	}

	/**
	 * Iterates over every room to find any null spots in the room and fill them in with a doorsquares
	 * @param size of the 2D rooms array
	 */
	public void setUpDoors(int size){
		for(int row = 0; row < size; row++){
			for(int col = 0; col < size; col++){
				
				Square[][] board = rooms[col][row].getBoard();
				
				for(int roomRow = 0; roomRow < board.length; roomRow++){
					for(int roomCol = 0; roomCol < board[0].length; roomCol++){
						
						if(roomCol != 0 && roomCol != board.length-1 && roomRow != 0 && roomRow != board[0].length-1) continue; //Only need edges of the room
						if(board[roomCol][roomRow] == null){
							
							KeyType type = getRandomKeyType();
							
							if(rooms[col][row] == endRoom) type = KeyType.GOLD;

							board[roomCol][roomRow] = new DoorSquare(rooms[col][row] != startRoom, type, roomCol, roomRow, rooms[col][row].isDark(), (byte) 9);
							
							if(roomRow == 0){ //North Door
								
								Square[][] connectedRoomBoard = rooms[col][row-1].getBoard();
								
								for(int column = 0; column < connectedRoomBoard.length; column++){
									if(connectedRoomBoard[column][connectedRoomBoard[0].length-1] == null){
										connectedRoomBoard[column][connectedRoomBoard[0].length-1] = new DoorSquare(rooms[col][row] != startRoom, type, column, connectedRoomBoard[0].length-1, rooms[col][row-1].isDark(), (byte) 9);
									}
								}
							}
							
							else if(roomRow == board[0].length-1){ //South Door
								
								Square[][] connectedRoomBoard = rooms[col][row+1].getBoard();
								for(int column = 0; column < connectedRoomBoard.length; column++){
									if(connectedRoomBoard[column][0] == null){
										connectedRoomBoard[column][0] = new DoorSquare(rooms[col][row] != startRoom, type, column, 0, rooms[col][row+1].isDark(), (byte) 9);
									}
								}
								
							}
							
							else if(roomCol == 0){ //West Door
								
								Square[][] connectedRoomBoard = rooms[col-1][row].getBoard();
								for(int r = 0; r < connectedRoomBoard[0].length; r++){
									if(connectedRoomBoard[connectedRoomBoard.length-1][r] == null){
										connectedRoomBoard[connectedRoomBoard.length-1][r] = new DoorSquare(rooms[col][row] != startRoom, type, connectedRoomBoard.length-1, r, rooms[col-1][row].isDark(), (byte) 9);
									}
								}
							}
							
							else if(roomCol == board.length-1){ //East Door
								
								Square[][] connectedRoomBoard = rooms[col+1][row].getBoard();
								for(int r = 0; r < connectedRoomBoard[0].length; r++){
									if(connectedRoomBoard[0][r] == null){
										connectedRoomBoard[0][r] = new DoorSquare(rooms[col][row] != startRoom, type, 0, r, rooms[col+1][row].isDark(), (byte) 9);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Iterates through the 2D array of rooms and connects any doors in adjacent rooms together
	 * @param size
	 */
	public void setUpDoorConnections(int size){
		
		for(int row = 0; row < size; row++){
			for(int col = 0; col < size; col++){
				rooms[col][row].setUpDoors();
			}
		}

		for(int row = 0; row < size; row++){
			for(int col = 0; col < size; col++){
				//Set up door connections so you can travel between rooms
				if(rooms[col][row] != null){
					
					
					
					DoorSquare[] entrances = rooms[col][row].getEntrances();
					if(entrances[0] != null){ //Has a north door
						entrances[0].setRoom(rooms[col][row]);
						entrances[0].setConnectedDoor(rooms[col][row-1].getEntrances()[2]);
					}
					if(entrances[1] != null){ //Has an east door
						entrances[1].setRoom(rooms[col][row]);
						entrances[1].setConnectedDoor(rooms[col+1][row].getEntrances()[3]);
					}
					if(entrances[2] != null){ //Has a south door
						entrances[2].setRoom(rooms[col][row]);
						entrances[2].setConnectedDoor(rooms[col][row+1].getEntrances()[0]);
					}
					if(entrances[3] != null){ //Has a west door
						entrances[3].setRoom(rooms[col][row]);
						entrances[3].setConnectedDoor(rooms[col-1][row].getEntrances()[1]);
					}
				}	
			}
		}
	}

	/**
	 * Creates and returns a new blank Room with or without doors
	 * @param size of the room
     * @param posX/posY position of the room on the roomsBoard
	 */
	private Room generateRoom(boolean dark, int size, int posX,int posY){
		Square[][] board = new Square[size][size];
		
		int tileTexture = 0;
		
		if(dark){
			tileTexture = 4; //Cave textures if its a dark room
		}else if(posY == 0){
			tileTexture = 0; //Beach textures if its a room at the top of the map
		}else{
			tileTexture = 2; //Forest texture for all other rooms
		}
		
		for(int col = 0; col < size; col++){
			for(int row = 0; row < size; row++){
				
				if( row == 0 || row == size-1 || col == 0 || col == size-1){
					board[col][row] = new WallSquare(dark, (byte) (21+(tileTexture/2)));
				}
				else{
					board[col][row] = new BlankSquare(dark,(byte) (1+tileTexture));
				}
			}
		}
		if(size > 6)
			generateWalls(board, dark, tileTexture/2+21);
		
		//Doors always on the same walls in rooms for every player though, so map layout doesn't change
		if( ((posX == 0 || posX == 4) && posY != 0) || ((posX == 1 || posX== 3) && (posY == 2 || posY == 3)) || (posX == 2 && (posY == 4 || posY == 3)) ){
			int xPos = 1 + (int) (Math.random()*(size-2));
			board[xPos][0] = null;
			board[xPos][1] = new BlankSquare(dark,(byte) (1+tileTexture));
		}
		if( ((posY == 0 || posY == 4) && posX != 4) || ((posY == 1 || posY == 3) && (posX == 1 || posX == 2)) ){
			int yPos = 1 +(int) (Math.random()*(size-2));
			board[size-1][yPos] = null;
			board[size-2][yPos] = new BlankSquare(dark,(byte) (1+tileTexture));
		}
		if( ((posX == 0 || posX == 4) && posY != 4) || ((posX == 1 || posX== 3) && (posY == 2 || posY == 1)) 
				|| (posX == 2 && (posY == 2 || posY == 3)) ){
			int xPos = 1 + (int) (Math.random()*(size-2));
			board[xPos][size-1] = null;
			board[xPos][size-2] = new BlankSquare(dark,(byte) (1+tileTexture));
		}
		if(((posY == 0 || posY == 4) && posX != 0) || ((posY == 1 || posY == 3) && (posX == 3 || posX == 2)) ){
			int yPos = 1 +(int) (Math.random()*(size-2));
			board[0][yPos] = null;
			board[1][yPos] = new BlankSquare(dark,(byte) (1+tileTexture));
		}

		checkBlockedOff(board,dark, tileTexture+1);
		if(size > 6)
			addItems(board, dark, tileTexture);

		return new Room(board, dark, new Point(posX,posY));
	}

	/**
	 * Iterates over the roomsTraversal list and adds keys to a random previously visited room, so that keys will always
	 * be accessible  
	 * @param roomsTraversal traversal of the rooms that represents how the player would move through the map
	 */
	public void addKeysToMap(ArrayList<Room> roomsTraversal){

		for(int i = 0; i < roomsTraversal.size(); i++){
			Room currentRoom = roomsTraversal.get(i);

			for(int j = 0; j < 4; j++){
				DoorSquare door = currentRoom.getEntrances()[j];
				if(door != null && door.isLocked() 
						&& roomsTraversal.indexOf(door.getConnectedDoor().getRoom()) > roomsTraversal.indexOf(currentRoom)){ //If the connected room hasn't been visited yet 
					int index = (int) (Math.random()*(i-1));
					if(door.getKeyType() == KeyType.GOLD) continue;
					KeyItem key = new KeyItem(door.getKeyType());
					addItemToRoom(roomsTraversal.get(index).getBoard(), key);
				}
			}
		}
	}
	
	/**
	 * Adds four torches to the map in random outer rooms
	 */
	public void addTorchesToMap(int size){
		int col = (int)(Math.random()*size);
		int row = (int)(Math.random()*size);
		//Add torches to 4 rooms opposite each other on the outer ring of the map
		addItemToRoom(rooms[0][row].getBoard(), new Torch());
		addItemToRoom(rooms[size-1][row].getBoard(), new Torch());
		addItemToRoom(rooms[col][0].getBoard(), new Torch());
		addItemToRoom(rooms[col][size-1].getBoard(), new Torch());

	}

	/**
	 * Takes a board and adds an item to a empty square on the board
	 * @param board to have the item placed on
	 * @param item to be added to board
	 */
	public void addItemToRoom(Square[][] board, Item item){
		int col;
		int row;
		
		while(true){
				
			col = (int) (Math.random()*(board.length-2)) +1;
			row = (int) (Math.random()*(board[0].length-2)) +1;
			
			if(board[col][row] instanceof BlankSquare && ((BlankSquare) board[col][row]).getItem() == null){
				((BlankSquare) board[col][row]).setItem(item);
				break;
			}
		}
	}

	/**
	 * Adds randomly picked items to the board 
	 * @param board
	 */
	public void addItems(Square[][] board, boolean dark, int tileTexture){
		int col;
		int row;
		//Create Score
		while(true){
			ScoreItem scorePickup = new ScoreItem(50);
			col = (int) (Math.random()*(board.length-2)) +1;
			row = (int) (Math.random()*(board[0].length-2)) +1;
			if(board[col][row] instanceof BlankSquare && ((BlankSquare) board[col][row]).getItem() == null){
				((BlankSquare) board[col][row]).setItem(scorePickup);
				break;
			}
		}
		//Create Trap
		TrapItem trap = null;
		int trapPicker = (int) (Math.floor((Math.random()*4)));
		switch(trapPicker){
		case 0:
			trap = new StunTrap();
			break;
		case 1:
			trap = new TeleportTrap(startRoom);
			break;
		case 2:
			trap = new DisorientateTrap();
			break;
		case 3:
			trap = new ScoreTrap();
			break;
		}
		
		while(true){
			col = (int) (Math.random()*(board.length-2)) +1;
			row = (int) (Math.random()*(board[0].length-2)) +1;
			if(board[col][row] instanceof BlankSquare && ((BlankSquare) board[col][row]).getItem() == null){
				((BlankSquare) board[col][row]).setItem(trap);
				break;
			}
		}

		//Create Tree
		for(int i = 0; i < 15; i++){ //Will place a random amount of trees up to 15
					Tree tree = new Tree((byte) (11+tileTexture/2));
					col = (int) (Math.random()*(board.length-2)) +1;
					row = (int) (Math.random()*(board[0].length-2)) +1;
					if(board[col][row] instanceof WallSquare){
						board[col][row] = new BlankSquare(dark,(byte) (1+tileTexture));
						((BlankSquare) board[col][row]).setItem(tree);
						continue;
					}
				}
		 
	}

	/**
	 * Adds so variety in placement to the walls of the room
	 * @param board
	 */
	public void generateWalls(Square[][] board, boolean dark, int tileType){
		for(int col = 2; col < board.length-2; col++){
			for(int row = 2; row < board[0].length-2; row++){
				double random = Math.random();
				if(random > 0.5){
					board[col][row] = new WallSquare(dark, (byte) tileType);
				}
			}
		}
	}

	/**
	 * Returns a random KeyType excluding the GOLD type
	 */
	public KeyType getRandomKeyType(){
		int random = (int) (Math.random()*(KeyType.values().length-1));
		return KeyType.values()[random];
	}

	/**
	 * Checks over a room to make sure it has no blocked off areas
	 * @param board
	 * @param dark
	 */
	public void checkBlockedOff(Square[][] board, boolean dark, int tileType){
		for(int col = 2; col < board.length-2; col++){
			for(int row = 2; row < board[0].length-2; row++){
				if(board[col][row] instanceof BlankSquare){
					//If its surrounded with walls, turn them all to blank squares
					if(board[col+1][row] instanceof WallSquare && board[col][row+1] instanceof WallSquare
							&& board[col-1][row] instanceof WallSquare && board[col][row-1] instanceof WallSquare){
						board[col+1][row] = new BlankSquare(dark,(byte) tileType);
						board[col-1][row] = new BlankSquare(dark,(byte) tileType);
						board[col][row-1] = new BlankSquare(dark,(byte) tileType);
						board[col][row+1] = new BlankSquare(dark,(byte) tileType);
						checkBlockedOff(board, dark, tileType);
					}
					int wallCount = 0;
					Point blank = null;
					if(board[col+1][row] instanceof WallSquare){
						wallCount++;
					}else blank = new Point(col+1, row);
					
					if(board[col-1][row] instanceof WallSquare){
						wallCount++;
					}else blank = new Point(col-1, row);
					
					if(board[col][row+1] instanceof WallSquare){
						wallCount++;
					}else blank = new Point(col, row+1);
					
					if(board[col][row-1] instanceof WallSquare){
						wallCount++;
					}else blank = new Point(col, row-1);
					
					//If the the blanksquare has 3 walls around it and
					//If the blank square next to this one is followed by a wall, it means its most likely a dead end and so get rid of the walls around the blanksquare
					if(wallCount == 3){
						blank.x -= col;
						blank.y -= row;
						if(board[col+blank.x][row+blank.y] instanceof WallSquare){
							board[col+blank.x+1][row+blank.y] = new BlankSquare(dark,(byte) tileType);
							board[col+blank.x-1][row+blank.y] = new BlankSquare(dark,(byte) tileType);
							board[col+blank.x][row+blank.y-1] = new BlankSquare(dark,(byte) tileType);
							board[col+blank.x][row+blank.y+1] = new BlankSquare(dark,(byte) tileType);
							checkBlockedOff(board, dark, tileType);
						}
					}
					
				}
			}
		}
	}

	/**
	 * Trigger for updates to the game logic, mostly used to keep track of durations for status effects
	 * @author Darren Hobern 300341002
	 */
	public synchronized void clockTick() {
		if(state == PLAYING) {
			for(Player p : players) {
				p.tick();
			}
			WinItem.tick();
		}
	}

	/**
	 * Gets the player with the given uid
	 * throws IllegalArgumentException if no players have the uid passed
	 * @param uid
	 * @return Player with the corresponding uid
	 * @author Darren Hobern 300341002
	 */
	public synchronized Player getPlayer(int uid) {
		
		for(Player p : players) {
			if(p.uid == uid) {
				return p;
			}
		}
		throw new IllegalArgumentException("Invalid player UID");
	}

	/**
	 * Registers a new player, giving the player a unique ID
	 * and creating the player object to be used in the world
	 * @return the unique id for this player
	 * @author Darren Hobern 300341002
	 */
	public synchronized byte registerPlayer() {
		Point spawn = spawnPoints[nextSpawnPoint];
		nextSpawnPoint = (nextSpawnPoint+1) % spawnPoints.length;

		Player player = new Player(String.format("Player %d", ++uid), startRoom, spawn, uid);
		players.add(player);
		((BlankSquare) startRoom.getSquare(spawn.x,spawn.y)).moveTo(player);
		startRoom.addPlayer(player);
		return uid;
	}

	/**
	 * Disconnects the player with the matching UID
	 * Throws IllegalArgumentException if no player is found
	 * @param uid of the player to be disconnected
	 * @author Darren Hobern 300341002
	 */
	public synchronized void disconnectPlayer(int uid) {
		Player p = getPlayer(uid);
		if(p != null) {
			p.setStatus(Status.STUNNED);
			players.remove(uid-1);
			return;
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Converts the information to be passed to the client from
	 * the player's connection to a LargeItem to a byte array
	 * @param uid of the player
	 * @return byte[]
	 * @author Darren Hobern 300341002
	 */
	public synchronized byte[] connectionToByteArray(byte uid) throws IOException {
		LargeItem item = getPlayer(uid).getConnection();
		return item.toByteArray();
	}
	
	public synchronized byte[] scoreboardToByteArray() throws IOException {
		bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(bout);
		
		dout.writeByte(players.size());
		for(Player p : players) {
			dout.writeByte(p.uid);
			dout.writeInt(p.getScore());
		}
		dout.flush();
		return bout.toByteArray();
	}

	/**
	 * Converts the current room the given player is in into a byte
	 * array, so that it may be sent to the client of that player.
	 * @param uid of the player
	 * @return byte[] representing the room
	 * @throws IOException
	 * @author Darren Hobern 300341002
	 */
	public synchronized byte[] toByteArray(byte uid) throws IOException {
		bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(bout);
		
		//First find out which player we are
		Player player = getPlayer(uid);
		//Then find out what room we're in
		Room room = player.getRoom();
		//Next write the width and height of the room
		dout.writeByte(room.width);
		dout.writeByte(room.height);
		//Finally iterate over the room
		dout.write(room.toByteArray(player.getDirection(), uid));

		dout.flush();

		return bout.toByteArray();

	}

	//used for save/load
	public Room[][] getRooms() {
		return rooms;
	}
	//used for save/load
	public void loadRooms(Room[][] rooms) {
		this.rooms = rooms;
		this.startRoom = rooms[START_ROOM_COLUMN][START_ROOM_ROW];
		this.endRoom = rooms[2][2];
	}
	//used for save/load
	public List<Player> getPlayers() {
		return players;
	}
	//used for save/load
	public void loadPlayers(List<Player> players) {
		this.players = players;
	}
	//used for save/load
	public void loadStartRoom(Room r) {
		startRoom = r;
	}
	//used for save/load
	public void loadEndRoom(Room r) {
		endRoom = r;
	}

	public void setState(byte state) {
		Board.state = state;
	}

	/**
	 * Stuns all the players in the game, 
	 * used to ensure a fair start to the game.
	 * @author Darren Hobern 300341002
	 */
	public void stunPlayers() {
		for(Player p : players) {
			p.setStatus(Status.STUNNED);
		}
		
	}

}