package tests;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import data.Load;
import data.Save;
import model.Room;
import model.enums.KeyType;
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
import model.items.Tree;
import model.items.WinItem;
import model.squares.BlankSquare;
import model.squares.DoorSquare;
import model.squares.Square;
import model.squares.WallSquare;

public class DataTests {
	
	//ITEM TESTS
	@Test
	public void loadKeyItemNoBL() throws ParserConfigurationException{
		Item item1 = new KeyItem(KeyType.BLACK);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		Item item2 = load.loadItem(itemNode);
		assertEquals(item1, item2);
	}
	@Test
	public void loadKeyItemBL() throws ParserConfigurationException{
		ArrayList<Byte> playerBlackList = new ArrayList<Byte>();
		playerBlackList.add((byte) 1);
		Item item1 = new KeyItem(KeyType.BLACK, playerBlackList);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		Item item2 = load.loadItem(itemNode);
		assertEquals(item1, item2);
	}
	@Test
	public void loadDisorientateTrapUnarmed() throws ParserConfigurationException{
		DisorientateTrap item1 = new DisorientateTrap(false);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		DisorientateTrap item2 = (DisorientateTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadDisorientateTrapArmed() throws ParserConfigurationException{
		DisorientateTrap item1 = new DisorientateTrap(true);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		DisorientateTrap item2 = (DisorientateTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadScoreTrapUnarmed() throws ParserConfigurationException{
		ScoreTrap item1 = new ScoreTrap(false);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		ScoreTrap item2 = (ScoreTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadScoreTrapArmed() throws ParserConfigurationException{
		ScoreTrap item1 = new ScoreTrap(true);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		ScoreTrap item2 = (ScoreTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadTeleportTrapUnarmed() throws ParserConfigurationException{
		TeleportTrap item1 = new TeleportTrap(false);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		TeleportTrap item2 = (TeleportTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadTeleportTrapArmed() throws ParserConfigurationException{
		TeleportTrap item1 = new TeleportTrap(true);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		TeleportTrap item2 = (TeleportTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadStunTrapUnarmed() throws ParserConfigurationException{
		StunTrap item1 = new StunTrap(false);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		StunTrap item2 = (StunTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadStunTrapArmed() throws ParserConfigurationException{
		StunTrap item1 = new StunTrap(true);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		StunTrap item2 = (StunTrap) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadChest() throws ParserConfigurationException{
		Item[] inv = new Item[6];
		Chest item1 = new Chest(inv);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		Chest item2 = (Chest) load.loadItem(itemNode);
		assertEquals(item1.getContainedItems(), item2.getContainedItems());
	}
	@Test
	public void loadChestLarge() throws ParserConfigurationException{
		Item[] inv = new Item[6];
		Chest item1 = new Chest(inv);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveLargeItem(item1, doc);
		Chest item2 = (Chest) load.loadItem(itemNode);
		assertEquals(item1.getContainedItems(), item2.getContainedItems());
	}
	@Test
	public void loadScoreItem() throws ParserConfigurationException{
		ScoreItem item1 = new ScoreItem(50);
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		ScoreItem item2 = (ScoreItem) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadTorch() throws ParserConfigurationException{
		Torch item1 = new Torch();
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		Torch item2 = (Torch) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadTree() throws ParserConfigurationException{
		Tree item1 = new Tree((byte) 3, "hint");
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		Tree item2 = (Tree) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadTreeLarge() throws ParserConfigurationException{
		Tree item1 = new Tree((byte) 3, "hint");
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveLargeItem(item1, doc);
		Tree item2 = (Tree) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadWinItem() throws ParserConfigurationException{
		WinItem item1 = new WinItem("portal");
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		WinItem item2 = (WinItem) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadWinItemLarge() throws ParserConfigurationException{
		WinItem item1 = new WinItem("portal");
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveLargeItem(item1, doc);
		WinItem item2 = (WinItem) load.loadItem(itemNode);
		assertTrue(item1.equals(item2));
	}
	@Test
	public void loadNullItem() throws ParserConfigurationException{
		Item item1 = null;
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveItem(item1, doc);
		Item item2 = load.loadItem(itemNode);
		assertTrue(item1 == item2);
	}
	@Test
	public void loadNullItemLarge() throws ParserConfigurationException{
		LargeItem item1 = null;
		Save save = new Save();
		Load load = new Load();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node itemNode = (Node) save.saveLargeItem(item1, doc);
		LargeItem item2 = (LargeItem) load.loadItem(itemNode);
		assertTrue(item1 == item2);
	}	
	
	public Square[][] genBoard() {
		Square[][] board = new Square[2][2];
		board[0][0] = new BlankSquare(false, (byte) 0);
		board[0][1] = new BlankSquare(true, (byte) 1);
		board[1][0] = new WallSquare(false, (byte) 2);
		board[1][1] = new DoorSquare(false, KeyType.BLUE, 0, 0, false, (byte) 3);
		
		return board;	
	}

}
