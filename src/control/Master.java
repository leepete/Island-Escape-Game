package control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import data.GameData;
import data.Load;
import data.Save;
import model.Board;
import model.Player;
import model.enums.Direction;
import model.items.Chest;
import model.items.LargeItem;
import view.GUI;

/**
 * Master connection that sends information about the world state to the client.
 * All of the logic is done within a single Board object which this class handles.
 * Also receives key events from the client to update the world state.
 * @author Darren Hobern 300341002
 */
public final class Master extends Thread {
	private final Board board;
	private final int broadcastClock;
	private final byte uid;
	private final Socket socket;
	private boolean exit;
	private boolean waited = false;
	private boolean sentScoreBoard = false;
	
	private DataOutputStream output;
	private DataInputStream input;
	
	public static final byte ROOM_DATA = 0;
	public static final byte PLAYER_DATA = 1;
	public static final byte TREE_DATA = 2;
	public static final byte WINITEM_DATA = 3;
	public static final byte ENDGAME_DATA = 4;
	public static final byte CHEST_DATA = 5;
	public static final byte CHEST_CLOSE_DATA = 6;
	
	

	public Master(Socket socket, byte uid, int broadcastClock, Board board) {
		this.board = board;
		this.broadcastClock = broadcastClock;
		this.socket = socket;
		this.uid = uid;
	}

	/**
	 * Sends information to, and receives events from the client
	 */
	public void run() {
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			
			exit=false;
			while(!exit) {
				try {
					//If an event has arrived
					receive();
					send();

					Thread.sleep(broadcastClock);
				} catch(InterruptedException e) {}
			}
			socket.close();
		} catch(IOException e) {
			System.err.println("PLAYER " + uid + " DISCONNECTED");
			board.disconnectPlayer(uid);
		}
	}
	
	/**
	 * Receives key events from the client and decodes them,
	 * passing the intended function to the board.
	 */
	private void receive() {
		try {
			Direction dir = Direction.UP;
			if(input.available() != 0) {
				byte key = input.readByte();
				switch(key) {
				case 0: dir = Direction.UP;
						board.getPlayer(uid).movePlayer(dir);
						break;
				case 1: dir = Direction.DOWN;
						board.getPlayer(uid).movePlayer(dir);
						break;
				case 2: dir = Direction.RIGHT;
						board.getPlayer(uid).movePlayer(dir);
						break;
				case 3: dir = Direction.LEFT;
						board.getPlayer(uid).movePlayer(dir);
						break;
				case 4: dir = Direction.CLOCKWISE;
						board.getPlayer(uid).rotateViewDirection(dir);
						break;
				case 5: dir = Direction.ANTICLOCKWISE;
						board.getPlayer(uid).rotateViewDirection(dir);
						break;
				case 6: board.getPlayer(uid).performAction();
						break;
				default:
					if(key >= Slave.INVENTORY_OFFSET && key <= Player.INVEN_SIZE+Chest.INVEN_SIZE+Slave.INVENTORY_OFFSET) {
						key -= Slave.INVENTORY_OFFSET;
						if(key >= 0 && key < Player.INVEN_SIZE) {
							board.getPlayer(uid).placeItem((int)key);
						} else {
							key -= Player.INVEN_SIZE;
							if(key >= 0 && key < Chest.INVEN_SIZE) {
								((Chest)board.getPlayer(uid).getConnection()).transferItem((int)(key), uid);
							} else {
								key -= Chest.INVEN_SIZE;
								board.getPlayer(uid).disconnectLargeItem(true);
							}
						}
						
					} else {
						key -= Slave.MENU_OFFSET;
						if(key >= 0 && key <= GUI.MENU_OPTIONS) {
							if(key >= 0 && key < 3) {
								save(key+1);
							} else if(key >= 3 && key < 6) {
								load(key-2);
							}
						}
					}
				}
			}
		} catch(IOException e) {
			exit = true;
		}
	}
	
	/**
	 * Creates a new Save object which is used to create a .xml file in 
	 * the slot specified by key
	 * 
	 * @param key
	 * @author juddwill
	 */
	private void save(int key) {
		Save s = new Save();
		s.saveFile(board.getRooms(), key);
		System.out.println("Saved to Slot " + (key));
	}
	
	/**
	 * Creates a new Load object which is used to load the game data 
	 * specified by the .xml file at the save slot specified by key.
	 * 
	 * @param key
	 * @author juddwill
	 */
	private void load(int key) {
		Load l = new Load();
		GameData gameData = l.loadFile(new File("./resources/saves/sav" + (key) + ".xml"));
        board.loadRooms(gameData.getRooms());
        board.loadPlayers(gameData.getPlayers());
        board.loadStartRoom(gameData.getRooms()[2][0]);
        board.loadEndRoom(gameData.getRooms()[2][2]);
        System.out.println("Loaded slot " + (key));
        System.out.println("./resources/saves/sav" + (key) + ".xml");
	}

	/**
	 * Sends an update to the client based on what state the game is in.
	 */
	private void send() {
		if(board.state == Board.WAITING) {
			if(!waited) {
				sendRoom();
				sendPlayer();
				waited = true;
			}
			return;
		}
		waited = false;
		if(board.state == Board.PLAYING ) {
			sendRoom();
			sendPlayer();
			checkForConnection();
			return;
		}
		
		if(board.state == Board.GAMEOVER) {
			if(!sentScoreBoard) {
				sendScoreboard();
				sentScoreBoard = true;
			}
			return;
		}
	}
	
	/**
	 * Sends the players' uids and scores to the client
	 */
	private void sendScoreboard() {
		try {
			output.writeByte(ENDGAME_DATA);
			byte[] scoreboard = board.scoreboardToByteArray();
			output.writeInt(scoreboard.length);
			output.write(scoreboard);
			output.flush();
		} catch(IOException e) {
			System.err.println("PLAYER " + uid + " DISCONNECTED");
			exit = true;
		}
	}
	
	/**
	 * Checks the player for a connection to a LargeItem,
	 * if there is a connection we will need to send some data
	 */
	private void checkForConnection() {
		try {
			Player p = board.getPlayer(uid);
			LargeItem lItem = p.getConnection();
			if(lItem == null) {
				if(p.closeChestUi()) {
					output.writeByte(CHEST_CLOSE_DATA);
					output.flush();
				}
				
			} else {
				byte[] dataArray = board.connectionToByteArray(uid);
				output.writeByte(p.getConnection().getDataID()); //Let the client know what type of data this is
				output.writeInt(dataArray.length);
				output.write(dataArray);
				output.flush();
			}
		} catch(IOException e) {
			System.err.println("PLAYER " + uid + " DISCONNECTED");
			exit = true;
		}
	}
	
	/**
	 * Sends the information about the room the client is in to them.
	 */
	private void sendRoom() {
		try {
			output.writeByte(ROOM_DATA); //Let the client know this is the room data
			output.writeByte(Board.state);
			byte[] stateArray = board.toByteArray(uid);
			output.writeInt(stateArray.length);
			output.write(stateArray);
			output.flush();
		} catch(IOException e) {
			System.err.println("PLAYER " + uid + " DISCONNECTED");
			exit = true;
		}
	}
	
	/**
	 * Sends the client their player's information.
	 */
	private void sendPlayer() {
		try {
			output.writeByte(PLAYER_DATA); //Let the client know this is the player data
			byte[] playerArray = board.getPlayer(uid).toByteArray();
			output.writeInt(playerArray.length);
			output.write(playerArray);
			output.flush();
		} catch(IOException e) {
			System.err.println("PLAYER " + uid + " DISCONNECTED");
			exit = true;
		}
	}
	
}
