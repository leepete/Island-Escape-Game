package data;

import java.io.File;

import javax.swing.JFileChooser;
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
import model.items.TrapItem;
import model.items.Tree;
import model.items.WinItem;
//import model.items.PointTrap;
import model.squares.BlankSquare;
import model.squares.DoorSquare;
import model.squares.Square;
import model.squares.WallSquare;

/**
 * A Save object contains the necessary logic to save the game. Once constructed
 * saveFile must be called and passed a Room[][] object that represents the game
 * board and an int which indicates which slot to save the file to.
 * 
 * @author William Judd 300262468
 */
public class Save {
	
	public Save() {
		
	}
	/**
	 * Saves a File that represents the Room[][] object rooms. This
	 * can then be loaded back into the necessary data to run the game.
	 * 
	 * @param rooms
	 * 
	 * @author juddwill
	 */
	public void saveFile(Room[][] rooms, int key) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			int x = rooms[0].length;
			int y = rooms.length;
			//sets up root
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("board");
			rootElement.setAttribute("x", String.valueOf(x));
			rootElement.setAttribute("y", String.valueOf(y));
			doc.appendChild(rootElement);
			//iterates through the rooms array and saves each room
			for (int i = 0; i != y; ++i) {
				for (int j = 0; j != x; ++j) {
					System.out.println("Room (" + i + "," + j + ").");
					Element room = saveRoom(rooms[i][j], doc);
					rootElement.appendChild(room);
				}
			}
			//outputs a file to the file specified by key
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer transformer = transFact.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("./resources/saves/sav" + (key) + ".xml"));
			System.out.println("./resources/saves/sav" + (key+1) + ".xml");
			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}


	}

	/**
	 * Returns an Element that can be added to the document that represents
	 * the Item[] inv.
	 * @param inv
	 * @param doc
	 * @return
	 * 
	 * @author juddwill
	 */
	public Element saveInventory(Item[] inv, Document doc) {
		//create inventory node to represent List<Item> and appends to player
		Element inventory = doc.createElement("inventory");
		//loop to add all items
		for (int i = 0; i != 6; ++i) {
			inventory.appendChild(saveItem(inv[i], doc));
		}
		return inventory;	
	}

	/**
	 * Returns an Element that can be added to the document that represents
	 * the item i.
	 * 
	 * @param i
	 * @param doc
	 * @return
	 * 
	 * @author juddwill
	 */
	public Element saveItem(Item i, Document doc) {
		Element item = doc.createElement("item");
		if (i == null) {
			item.setAttribute("itemType", "null");
		} else if (i instanceof KeyItem) {
			item.setAttribute("itemType", "keyItem");
			item.setAttribute("name", i.getName());
			item.setAttribute("type", ((KeyItem) i).getType().toString());
			//save playerBlackList
			Element playerBlacklist = doc.createElement("blacklist");
			item.appendChild(playerBlacklist);
			for (Byte byt : ((KeyItem)i).getBlackList()) {
				Element playerUid = doc.createElement("playerUid");
				playerBlacklist.appendChild(playerUid);
				playerUid.setAttribute("uid", String.valueOf(byt));
			}
		} else if (i instanceof TrapItem) {	//saves Trap item, below ifs decide what type
			if (i instanceof DisorientateTrap) {	
				item.setAttribute("itemType", "disorientateTrap");
				if (((TrapItem) i).isArmed()) {
					item.setAttribute("armed", "true");
				} else {
					item.setAttribute("armed", "false");
				}
			} else if (i instanceof ScoreTrap) {
				item.setAttribute("itemType", "scoreTrap");
				if (((TrapItem) i).isArmed()) {
					item.setAttribute("armed", "true");
				} else {
					item.setAttribute("armed", "false");
				}
			} else if (i instanceof StunTrap) {
				item.setAttribute("itemType", "stunTrap");
				if (((TrapItem) i).isArmed()) {
					item.setAttribute("armed", "true");
				} else {
					item.setAttribute("armed", "false");
				}
			} else if (i instanceof TeleportTrap) {
				item.setAttribute("itemType", "teleportTrap");
				if (((TrapItem) i).isArmed()) {
					item.setAttribute("armed", "true");
				} else {
					item.setAttribute("armed", "false");
				}
			}
		} else if (i instanceof LargeItem) {	//saves LargeItem, either Tree, Chest or WinItem
			if (i instanceof Tree) {
				item.setAttribute("itemType", "tree");
				item.setAttribute("uid", String.valueOf(((Tree) i).getUid()));
				item.setAttribute("hint", ((Tree) i).getHint());
			} else if (i instanceof WinItem) {
				item.setAttribute("itemType", "winItem");
				item.setAttribute("name", ((WinItem) i).getName());
			} else if (i instanceof Chest) {
				item.setAttribute("itemType", "chest");
				item.setAttribute("uid", String.valueOf(((Chest) i).getUid()));
				item.appendChild(saveInventory(((Chest) i).getContainedItems(), doc));
			}
		} else if (i instanceof Torch) {	//saves a Torch item
			item.setAttribute("itemType", "torch");
		} else if (i instanceof ScoreItem) {//saves a ScoreItem
			item.setAttribute("itemType", "score");
			item.setAttribute("points", String.valueOf(((ScoreItem) i).getPoints()));
		}
		return item;
	}
	
	/**
	 * Returns an Element that can be added to the document that represents
	 * the LargeItem i.
	 * 
	 * @param i
	 * @param doc
	 * @return
	 * 
	 * @author juddwill
	 */
	public Element saveLargeItem(LargeItem i, Document doc) {
		Element item = doc.createElement("largeItem");
		if (i == null) {
			item.setAttribute("itemType", "null");
		} else if (i instanceof LargeItem) {
			if (i instanceof Tree) {
				item.setAttribute("itemType", "tree");
				item.setAttribute("uid", String.valueOf(((Tree) i).getUid()));
				item.setAttribute("hint", ((Tree) i).getHint());
			} else if (i instanceof WinItem) {
				item.setAttribute("itemType", "winItem");
				item.setAttribute("name", ((WinItem) i).getName());
			} else if (i instanceof Chest) {
				item.setAttribute("itemType", "chest");
				item.setAttribute("uid", String.valueOf(((Chest) i).getUid()));
				item.appendChild(saveInventory(((Chest) i).getContainedItems(), doc));
			}
		}
		return item;
	}

	/**
	 * Returns an Element that can be saved to the document that represents
	 * the Room r.
	 * 
	 * @param r
	 * @param doc
	 * @return
	 * 
	 * @author juddwill
	 */
	public Element saveRoom(Room r, Document doc) {
		Element room = doc.createElement("room");
		if (r != null) {
			Element board = saveRoomBoard(r.getBoard(), doc);
			room.appendChild(board);
			if (r.isDark()) {
				room.setAttribute("dark", "true");
			} else {
				room.setAttribute("dark", "false");
			}

		} else {
			Element board = doc.createElement("nullRoom");
			room.appendChild(board);
		}

		return room;
	}

	/**
	 * Returns an Element that can be added to the document that represents
	 * the Square[][] b.
	 * @param b
	 * @param doc
	 * @return
	 * 
	 * @author juddwill
	 */
	public Element saveRoomBoard(Square[][] b, Document doc) {
		Element board = doc.createElement("board");
		int x = b[0].length;
		int y = b.length;
		board.setAttribute("x", String.valueOf(x));
		board.setAttribute("y", String.valueOf(y));
		for (int i = 0; i != y; ++i) {
			for (int j = 0; j != x; ++j) {
				if (b[i][j] instanceof DoorSquare) {
					Element dSquare = doc.createElement("square");
					board.appendChild(dSquare);
					dSquare.setAttribute("squareType", "doorSquare");
					//records dark state
					if (((DoorSquare)b[i][j]).isDark()) {
						dSquare.setAttribute("dark", "true");
					} else {
						dSquare.setAttribute("dark", "false");
					}
					//records player if present or null if not
					dSquare.appendChild(savePlayer(((DoorSquare)b[i][j]).getPlayer(), doc));
					//records lock state
					if (((DoorSquare)b[i][j]).isLocked()) {
						dSquare.setAttribute("locked", "true");	
					} else {
						dSquare.setAttribute("locked", "false");
					}
					//records keyType
					dSquare.setAttribute("type", ((DoorSquare)b[i][j]).getKeyType().toString());
					//records uid
					dSquare.setAttribute("uid", String.valueOf(((DoorSquare)b[i][j]).getUid()));
					//records location
					Element location = doc.createElement("location");
					dSquare.appendChild(location);
					location.setAttribute("x", String.valueOf(((DoorSquare)b[i][j]).getLocation().x));
					location.setAttribute("y", String.valueOf(((DoorSquare)b[i][j]).getLocation().y));
					Element whitelist = doc.createElement("whitelist");
					dSquare.appendChild(whitelist);
					for (Byte byt : ((DoorSquare)b[i][j]).getWhitelist()) {
						Element playerUid = doc.createElement("playerUid");
						whitelist.appendChild(playerUid);
						playerUid.setAttribute("uid", String.valueOf(byt));
					}
				} else if (b[i][j] instanceof BlankSquare) {
					Element bSquare = doc.createElement("square");
					board.appendChild(bSquare);
					bSquare.setAttribute("squareType", "blankSquare");
					bSquare.appendChild(saveItem(((BlankSquare)b[i][j]).getItem(), doc));
					//saves dark state
					if (((BlankSquare)b[i][j]).isDark()) {
						bSquare.setAttribute("dark", "true");
					} else {
						bSquare.setAttribute("dark", "false");
					}
					bSquare.setAttribute("uid", String.valueOf(((BlankSquare)b[i][j]).getUid()));
					//checks if player present
					bSquare.appendChild(savePlayer(((BlankSquare)b[i][j]).getPlayer(), doc));
					//checks if item present
					if(((BlankSquare)b[i][j]).getItem() != null) {
						bSquare.appendChild(saveItem(((BlankSquare)b[i][j]).getItem(), doc));
					} 
				} else if (b[i][j] instanceof WallSquare) {
					Element wSquare = doc.createElement("square");
					board.appendChild(wSquare);
					wSquare.setAttribute("squareType", "wallSquare");
					if (((WallSquare)b[i][j]).isDark()) {
						wSquare.setAttribute("dark", "true");
					} else {
						wSquare.setAttribute("dark", "false");
					}
					wSquare.setAttribute("uid", String.valueOf(((WallSquare)b[i][j]).getUid()));
				}
			}
		}
		return board;
	}

	/**
	 * Returns and Element that can be added to the document that represents
	 * the Player p.
	 * 
	 * @param p
	 * @param doc
	 * @return
	 * 
	 * @author juddwill
	 */
	public Element savePlayer(Player p, Document doc) {
		Element player = doc.createElement("player");
		if (p == null) {
			player.setAttribute("exists", "false");
		} else {
			player.setAttribute("exists", "true");
			player.appendChild(saveInventory(p.getInventory(), doc));
			player.setAttribute("viewDirection", p.getDirection().toString());
			int x = (int) p.getLocation().getX();
			int y = (int) p.getLocation().getY();
			player.setAttribute("x", String.valueOf(x));
			player.setAttribute("y", String.valueOf(y));
			player.setAttribute("score", String.valueOf(p.getScore()));
			player.setAttribute("name", p.getName());
			player.setAttribute("status", p.getStatus().toString());
			player.setAttribute("uid", String.valueOf(p.uid));
			player.setAttribute("statusDuration", String.valueOf(p.getStatusDuration()));
			player.appendChild(saveLargeItem(p.getConnection(), doc));
		}
		return player;	
	}
}