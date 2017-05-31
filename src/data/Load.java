package data;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.Board;
import model.Player;
import model.Room;
import model.enums.Direction;
import model.enums.KeyType;
import model.enums.Status;
import model.items.Chest;
import model.items.DisorientateTrap;
import model.items.Item;
import model.items.KeyItem;
import model.items.LargeItem;
import model.items.ScoreItem;
import model.items.ScoreTrap;
import model.items.StunTrap;
import model.items.TeleportTrap;
import model.items.Torch;
//import model.items.PointTrap;
import model.items.TrapItem;
import model.items.Tree;
import model.items.WinItem;
import model.squares.BlankSquare;
import model.squares.DoorSquare;
import model.squares.Square;
import model.squares.WallSquare;

/**
 * A Load object contains the necessary logic to load a game. Once constructed
 * loadFile must be called and must be passed a .xml file that the data will be
 * loaded from. 
 * 
 * @author William Judd 300262468
 */
public class Load {
	
	/**
	 * Constructs a new Load object that can be used to load a saved game.
	 * 
	 * @author juddwill
	 */
	public Load() {
		
	}
	
	/**
	 * Returns a GameData object generated from the data within the XMLfile
	 * that is passed in as a parameter. 	
	 * 
	 * @param XMLfile
	 * @return
	 * 
	 * @author juddwill
	 */
	public GameData loadFile(File XMLfile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XMLfile);
			doc.getDocumentElement().normalize();
			//grabs width and height of 2dArray
			int rX = Integer.parseInt((String) doc.getDocumentElement().getAttribute("x"));
			int rY = Integer.parseInt((String) doc.getDocumentElement().getAttribute("y"));
			//makes rooms array
			Room[][] rooms = new Room[rY][rX];
			//initialises list of players
			List<Player> players = new ArrayList<Player>();
			//makes list of all "room" nodes
			NodeList roomsList = doc.getElementsByTagName("room");
			//index of roomNode being processed
			int roomIndex = 0;
			//iterate through all "room" nodes
			for (int i = 0; i != rY; ++i) {
				for (int j = 0; j != rX; ++j) {
					//"room" node being processed
					Node roomNode = roomsList.item(roomIndex);
					//loads the room creating a room object
					Room room = loadRoom(roomNode, j, i);
					//adds to array
					rooms[i][j] = room;
					//increments roomList
					roomIndex++;			
				}
			}
			//updates player and door info by iterating through loaded array
			//connects doors
			rooms = setUpDoorConnections(rooms, 5);
			for (int i = 0; i != rY; ++i) {
				for (int j = 0; j != rX; ++j) {
					//Room being processed
					//check room exists at this position
					if (rooms[i][j] != null) {
						Square[][] squares = rooms[i][j].getBoard();
						for (int t = 0; t != squares.length; ++t) {
							for (int v = 0; v != squares[0].length; ++v) {
								//if player is present adds them to players
								if (squares[t][v] instanceof DoorSquare) {
									if (((DoorSquare)squares[t][v]).isOccupied()) {
										((DoorSquare)squares[t][v]).getPlayer().setRoom(rooms[i][j]);
										Player p = ((DoorSquare)squares[t][v]).getPlayer();
										players.add(((DoorSquare)squares[t][v]).getPlayer());
										rooms[i][j].addPlayer(p);
									}
								//if player is present adds them to players
								} else if (squares[t][v] instanceof BlankSquare) {
									if (((BlankSquare)squares[t][v]).isOccupied()) {
										((BlankSquare)squares[t][v]).getPlayer().setRoom(rooms[i][j]);
										Player p = ((BlankSquare)squares[t][v]).getPlayer();
										players.add(((BlankSquare)squares[t][v]).getPlayer());
										rooms[i][j].addPlayer(p);
									}
									if (((BlankSquare)squares[t][v]).getItem() instanceof TeleportTrap) {
										TeleportTrap tt = (TeleportTrap) ((BlankSquare)squares[t][v]).getItem();
										tt.loadRoom(rooms[2][0]);
										
									}
								}
							}
						}
					}		
				}
			}
			return new GameData(rooms, players);
			
		} catch (Exception e){
			e.printStackTrace();
		}
		//will only happen if exception is thrown in try/catch
		return null;
	}
	
	/**
	 * Iterates through the 2D array of rooms and connects any doors in adjacent rooms together
	 * @param size
	 */
	public Room[][] setUpDoorConnections(Room[][] rooms, int size){
		
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
				if(rooms[col][row] == null) System.out.print("[ ]");
				else System.out.print("[x]");
			}
			System.out.println("");
		}
		return rooms;
	}
	
	/**
	 * Returns a Square objected created from the given Node. Square will
	 * be either a WallSquare, BlankSquare or DoorSquare
	 * 
	 * @param squareNode
	 * @return
	 * 
	 * @author juddwill
	 */
	public Square loadSquare(Node squareNode) {
		Square square = null;
		//sanity check
		if (squareNode.getNodeType() == Node.ELEMENT_NODE) {
			Element squareElement = (Element) squareNode;
			//checks type of square and calls appropriate load method
			if (squareElement.getAttribute("squareType").equals("doorSquare")) {
				square = loadDoorSquare(squareElement);
			} else if (squareElement.getAttribute("squareType").equals("blankSquare")) {
				square = loadBlankSquare(squareElement);
			} else if (squareElement.getAttribute("squareType").equals("wallSquare")) {
				square = loadWallSquare(squareElement);
			}
		}
		return square;
	}
	
	/**
	 * Returns a DoorSquare object created from the given Node.
	 * 
	 * @param squareElement
	 * @return
	 * 
	 * @author juddwill
	 */
	public DoorSquare loadDoorSquare(Element squareElement) {
		//assumes not dark then updates to true if it is dark
		boolean dark = false;
		if (squareElement.getAttribute("dark").equals("true")) {
			dark = true;
		}
		//assumes not locked then updates to true if it is locked
		boolean locked = false;
		if (squareElement.getAttribute("locked").equals("true")) {
			locked = true;
		}
		//loads player if any, remains null if not
		Player player = null;
		NodeList playerNodes = squareElement.getElementsByTagName("player");
		Node playerNode = playerNodes.item(0);
		if (playerNode.getNodeType() == Node.ELEMENT_NODE) {
			player = loadPlayer(playerNode);
		}
		//loads KeyType if any, remains null if not
		String keyType = squareElement.getAttribute("type");
		KeyType type = null;
		switch (keyType) {
		case "GREEN":	
			type = KeyType.GREEN;
			break;
		case "RED":
			type = KeyType.RED;
			break;
		case "BLUE":
			type = KeyType.BLUE;
			break;
		case "BLACK":
			type = KeyType.BLACK;
			break;
		case "SILVER":
			type = KeyType.SILVER;
			break;
		case "GOLD":
			type = KeyType.GOLD;
			break;
		}
		//loads location x component and location y component
		int lX = 0;
		int lY = 0;
		NodeList locationNodes = squareElement.getElementsByTagName("location");
		Node locationNode = locationNodes.item(0);
		if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
			Element locationElement = (Element) locationNode;
			lX = Integer.parseInt((String) locationElement.getAttribute("x"));
			lY = Integer.parseInt((String) locationElement.getAttribute("y"));
			
		}
		//loads square uid
		byte uid = (byte) Integer.parseInt(squareElement.getAttribute("uid"));
		//constructs and load playerWhitelist
		NodeList whitelistNodes = squareElement.getElementsByTagName("whitelist");
		Node whitelistNode = whitelistNodes.item(0);
		ArrayList<Byte> playerWhitelist = null;
		if (whitelistNode.getNodeType() == Node.ELEMENT_NODE) {
			Element whitelistElement = (Element) whitelistNode;
			NodeList playerUidNodes = whitelistElement.getElementsByTagName("playerUid");
			int index = 0; 
			playerWhitelist = new ArrayList<Byte>();
			while (index != playerUidNodes.getLength()) {
				Node playerUidNode = playerUidNodes.item(index);
				if (playerUidNode.getNodeType() == Node.ELEMENT_NODE) {
					Element playerUidElement = (Element) playerUidNode;
					playerWhitelist.add((byte) Integer.parseInt(playerUidElement.getAttribute("uid")));
				}
				index++;
			}
		}
		//creates the square using above loaded information
		DoorSquare square = new DoorSquare(locked, type, lX, lY, dark, player, uid, playerWhitelist);
		return square;
	}
	
	/**
	 * Returns a BlankSquare object created from the given Node.
	 * 
	 * @param squareElement
	 * @return
	 * 
	 * @author juddwill
	 */
	public BlankSquare loadBlankSquare(Element squareElement) {
		Item item = null;
		//makes a list 1 entry long containing the "item" element
		NodeList itemNodes = squareElement.getElementsByTagName("item");
		Node itemNode = itemNodes.item(0);
		if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
			item = loadItem(itemNode);
		}
		//assumes not dark then updates to true if it is
		boolean dark = false;
		if (squareElement.getAttribute("dark").equals("true")) {
			dark = true;
		}
		//checks and parses player
		Player player = null;
		NodeList playerNodes = squareElement.getElementsByTagName("player");
		Node playerNode = playerNodes.item(0);
		if (playerNode.getNodeType() == Node.ELEMENT_NODE) {
			player = loadPlayer(playerNode);
		}
		byte uid = (byte) Integer.parseInt(squareElement.getAttribute("uid"));		
		BlankSquare square = new BlankSquare(dark, item, player, uid);
		return square;
	}
	
	/**
	 * Returns a WallSquare object created from the given Node.
	 * 
	 * @param squareElement
	 * @return
	 * 
	 * @author juddwill
	 */
	public WallSquare loadWallSquare(Element squareElement) {
		String tileType = squareElement.getAttribute("tileType");
		boolean dark = false;
		if (squareElement.getAttribute("dark").equals("true")) {
			dark = true;
		}
		byte uid = (byte) Integer.parseInt(squareElement.getAttribute("uid"));
		//creates a new wall square
		WallSquare square = new WallSquare(dark, uid);
		return square;
	}
	
	/**
	 * Returns a Player object created from the given Node. Player object
	 * can be null if the Node is representative of an element with no 
	 * occupant.
	 * 
	 * @param playerNode
	 * @return
	 * 
	 * @author juddwill
	 */
	public Player loadPlayer(Node playerNode) {
		Player player = null;
		Element playerElement = (Element) playerNode;
		if (playerElement.getAttribute("exists").equals("true")) {
			Item[] inventory = null;
			NodeList inventoryNodes = playerElement.getElementsByTagName("inventory");
			Node inventoryNode = inventoryNodes.item(0);
			if (playerNode.getNodeType() == Node.ELEMENT_NODE) {
				inventory = loadInventory(inventoryNode);
			}
			String viewDirection = playerElement.getAttribute("viewDirection");
			Direction direction = null;
			switch (viewDirection) {
				case "NORTH":
					direction = Direction.NORTH;
					break;
				case "EAST":
					direction = Direction.EAST;
					break;
				case "SOUTH":
					direction = Direction.SOUTH;
					break;
				case "WEST":
					direction = Direction.WEST;
					break;
				case "UP":
					direction = Direction.UP;
					break;
				case "RIGHT":
					direction = Direction.RIGHT;
					break;
				case "DOWN":
					direction = Direction.DOWN;
					break;
				case "LEFT":
					direction = Direction.LEFT;
					break;		
			}
			int x = (int) Integer.parseInt(playerElement.getAttribute("x"));
			int y = (int) Integer.parseInt(playerElement.getAttribute("y"));
			Point location = new Point(x, y);
			int score = Integer.parseInt(playerElement.getAttribute("score"));
			String name = playerElement.getAttribute("name");
			String stat = playerElement.getAttribute("status");
			Status status = null;
			switch (stat) {
				case "NORMAL":
					status = Status.NORMAL;
					break;
				case "STUNNED":
					status = Status.STUNNED;
					break;
				case "DISORIENTATED":
					status = Status.DISORIENTATED;
					break;
			}
			int uid = Integer.parseInt(playerElement.getAttribute("uid"));
			int statusDuration = Integer.parseInt(playerElement.getAttribute("statusDuration"));
			NodeList itemNodes = playerElement.getElementsByTagName("largeItem");
			Node itemNode = itemNodes.item(0);
			Element itemElement = (Element) itemNode;
			LargeItem largeItem = null;
			
			if (itemElement.getAttribute("itemType") != null) {
				largeItem = (LargeItem) loadItem(itemNode);
			}			
			player = new Player(name, inventory, direction, location, 
					score, status, (byte) uid, statusDuration, largeItem);
		}
		
		return player;
	}
	
	/**
	 * Returns an Item[] array that represents an inventory created from
	 * the given Node. Item[] array may have up to 6 Items in it or be 
	 * completely empty as required to match the Node.
	 * 
	 * @param inventoryNode
	 * @return
	 * 
	 * @author juddwill
	 */
	public Item[] loadInventory(Node inventoryNode) {
		Element inventoryElement = (Element) inventoryNode;
		NodeList itemNodes = inventoryElement.getElementsByTagName("item");
		Item[] inventory = new Item[6];
		for (int i =0; i != itemNodes.getLength(); ++i) { 
			inventory[i] = loadItem(itemNodes.item(i));
		}
		return inventory;
	}
	
	/**
	 * Returns an Item object created from the given Node. Item object
	 * can be null if the Node is representative of an empty item slot,
	 * either in inventory or for a square.
	 * 
	 * @param itemNode
	 * @return
	 * 
	 * @author juddwill
	 */
	public Item loadItem(Node itemNode) {
		Item item = null;
		Element itemElement = (Element) itemNode;
		//goes through if else cases to check what type of item it is and update accordingly
		if (itemElement.getAttribute("itemType").equals("keyItem")) {
			String keyType = itemElement.getAttribute("type");
			KeyType key = null;
			switch (keyType) { 
				case "GREEN":	
					key = KeyType.GREEN;
					break;
				case "RED":
					key = KeyType.RED;
					break;
				case "BLUE":
					key = KeyType.BLUE;
					break;
				case "BLACK":
					key = KeyType.BLACK;
					break;
				case "SILVER":
					key = KeyType.SILVER;
					break;
				case "GOLD":
					key = KeyType.GOLD;
					break;
			}
			
			NodeList blacklistNodes = itemElement.getElementsByTagName("blacklist");
			Node blacklistNode = blacklistNodes.item(0);
			ArrayList<Byte> playerBlackList = null;
			if (blacklistNode.getNodeType() == Node.ELEMENT_NODE) {
				Element blacklistElement = (Element) blacklistNode;
				NodeList playerUidNodes = blacklistElement.getElementsByTagName("playerUid");
				int index = 0; 
				playerBlackList = new ArrayList<Byte>();
				while (index != playerUidNodes.getLength()) {
					Node playerUidNode = playerUidNodes.item(index);
					if (playerUidNode.getNodeType() == Node.ELEMENT_NODE) {
						Element playerUidElement = (Element) playerUidNode;
						playerBlackList.add((byte) Integer.parseInt(playerUidElement.getAttribute("uid")));
					}
					index++;
				}
			}
			
			KeyItem keyItem = new KeyItem(key, playerBlackList);
			item = keyItem;
				
		} else if (itemElement.getAttribute("itemType").equals("scoreTrap")) {
			String bool = itemElement.getAttribute("armed");
			boolean armed = false;
			if (bool.equals("true")) {
				armed = true;
			}
			item = new ScoreTrap(armed);
		} else if (itemElement.getAttribute("itemType").equals("disorientateTrap")) {
			String bool = itemElement.getAttribute("armed");
			boolean armed = false;
			if (bool.equals("true")) {
				armed = true;
			}
			item = new DisorientateTrap(armed);
		} else if (itemElement.getAttribute("itemType").equals("teleportTrap")) {
			String bool = itemElement.getAttribute("armed");
			boolean armed = false;
			if (bool.equals("true")) {
				armed = true;
			}
			item = new TeleportTrap(armed);
		} else if (itemElement.getAttribute("itemType").equals("stunTrap")) {
			String bool = itemElement.getAttribute("armed");
			boolean armed = false;
			if (bool.equals("true")) {
				armed = true;
			}
			item = new StunTrap(armed);
		} else if (itemElement.getAttribute("itemType").equals("torch")) {
			item = new Torch();
		} else if (itemElement.getAttribute("itemType").equals("tree")) {
			item = new Tree((byte) Integer.parseInt(itemElement.getAttribute("uid")), itemElement.getAttribute("hint"));
		} else if (itemElement.getAttribute("itemType").equals("winItem")) {
			item = new WinItem(itemElement.getAttribute("name"));
		} else if (itemElement.getAttribute("itemType").equals("chest")) {
			NodeList inventoryNodes = itemElement.getElementsByTagName("inventory");
			Node inventoryNode = inventoryNodes.item(0);
			item = new Chest(loadInventory(inventoryNode));
		} else if (itemElement.getAttribute("itemType").equals("score")) {
			item = new ScoreItem(Integer.parseInt(itemElement.getAttribute("points")));
		}
		return item;
	}
	
	/**
	 * Returns a Room object created from the given Node. Room object
	 * can be null if passed Node represents a spot in the board with 
	 * no room.
	 * 
	 * @param roomNode
	 * @return
	 * 
	 * @author juddwill
	 */
	public Room loadRoom(Node roomNode, int y, int x) {
		Room room = null;
		//sanity check
		if (roomNode.getNodeType() == Node.ELEMENT_NODE) {
			//cast to Element so it can be used more easily
			Element roomElement = (Element) roomNode;
			boolean dark = false;
			if (roomElement.getAttribute("dark").equals("true")) {
				dark = true;
			}
			//makes list of all "board" nodes (will only be 1) then grabs that node
			NodeList boardList = roomElement.getElementsByTagName("board");
			Node boardNode = boardList.item(0);
			//sanity check
			if (boardNode!=null) {
				if (boardNode.getNodeType() == Node.ELEMENT_NODE) {
					//casts "board" node to Element
					Element boardElement = (Element) boardNode;
					//grabs x/y of "board"
					int bX = (int) Integer.parseInt((String) boardElement.getAttribute("x"));
					int bY = (int) Integer.parseInt((String) boardElement.getAttribute("y"));
					//new squares 2d array using x/y from "board"
					Square[][] squares = new Square[bX][bY];
					//makes list of all "square" nodes in the boardNode
					NodeList squaresList = roomElement.getElementsByTagName("square");
					//index of Square being processed
					int squareIndex = 0;
					//iterate through all of the "square" nodes
					for (int t = 0; t != bY; ++t) {
						for (int v = 0; v != bX; ++v) {
							//"square" node being processed
							Node squareNode = squaresList.item(squareIndex);
							squares[t][v] = loadSquare(squareNode);
							//increments squareList
							squareIndex++;
						}
					}		
					room = new Room(squares, dark, new Point(x, y));
				}	
			}	
		}
		return room;
	}
}
