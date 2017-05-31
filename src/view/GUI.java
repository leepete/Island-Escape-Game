package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import control.Slave;
import model.Player;
import model.items.Chest;

/**
 * GUI holds the main JFrame which houses all other user interface components
 *
 * @author Darren Hobern 300341002
 *
 */

public class GUI{
	
	private JFrame frame = new JFrame("Island Escape");
	private JMenuBar menuBar;
	private JMenu menu;
	private JTextArea scoreJTA;
	private JMenu save, load;
	private JMenuItem save1, save2, save3, load1, load2, load3;
	private Info info;
	private Renderer render; 
	private MiniMap minimap;
	private Inventory inventory;
	private ChestFrame chestFrame;

	private byte[] data;
	private ByteArrayInputStream bin;
	private DataInputStream din;

	//Offsets for the UID types
	public static final byte ARMED_OFFSET = 4;
	public static final byte MENU_OPTIONS = 6;
	public static final byte CHEST_OFFSET = (byte)Player.INVEN_SIZE;
	public static final byte GROUND_DARK_OFFSET = 2;
	public static final byte DOOR_DARK_OFFSET = 6;
	public static final byte WALL_DARK_OFFSET = 1;

	//Maps for UID -> File location, item map also holds the item description
	public final Map<Byte, String> squareIDMap;
	public final Map<Byte, String[]> itemIDMap; 
	public final Map<Byte, String> playerIDMap;
	public final Map<Byte, String> overlayIDMap;

	//Strings for parent folder of file locations
	private String sqFolder = "./resources/images/tiles/landscape/";
	private String itFolder = "./resources/images/tiles/items/";
	private String plFolder = "./resources/images/tiles/players/";
	private String ovFolder = "./resources/images/tiles/overlays/";

	private Slave client;

	
	public GUI(int uid, Slave s){	
		squareIDMap = new HashMap<Byte, String>(initSqMap());
		itemIDMap = new HashMap<Byte, String[]>(initItMap());
		playerIDMap = new HashMap<Byte, String>(initPlMap());
		overlayIDMap = new HashMap<Byte, String>(initOvlyMap());
		this.client = s;
		initialise();
		render.addKeyListener(s);
	}

	/**
	 * Initialises the components of the user interface
	 * @param keyListeners
	 * @author Connor Lindsay
	 */
	private void initialise() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600, 900);
		frame.setVisible(true);
		menuBar = new JMenuBar();
		menu = new JMenu("Game");

		save = new JMenu("Save");
		save1 = new JMenuItem("Save slot 1");
		save1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SAVING SLOT 1");
				client.menu((byte) 0);
			}
		});
		save2 = new JMenuItem("Save slot 2");
		save2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SAVING SLOT 2");
				client.menu((byte) 1);
			}
		});
		save3 = new JMenuItem("Save slot 3");
		save3.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SAVING SLOT 3");
				client.menu((byte) 2);
			}
		});

		load = new JMenu("Load");
		load1 = new JMenuItem("Load slot 1");
		load1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("LOADING SLOT 1");
				client.menu((byte) 3);
			}
		});
		load2 = new JMenuItem("Load slot 2");
		load2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("LOADING SLOT 2");
				client.menu((byte) 4);
			}
		});
		load3 = new JMenuItem("Load slot 3");
		load3.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("LOADING SLOT 3");
				client.menu((byte) 5);
			}
		});
		save.add(save1);
		save.add(save2);
		save.add(save3);
		load.add(load1);
		load.add(load2);
		load.add(load3);
		menu.add(save);
		menu.add(load);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);

		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());

		JPanel left = new JPanel();
		JPanel right = new JPanel();

		left.setLayout(new BorderLayout());
		right.setLayout(new BorderLayout());

		render = new Renderer(this); 
		left.add(render, BorderLayout.CENTER);

		JPanel invPanel = new JPanel();
		invPanel.setLayout(new BorderLayout());

		JPanel lBuffer = new JPanel();
		JPanel rBuffer = new JPanel();
		inventory = new Inventory(this);

		Dimension bufferSize = new Dimension(300,100);
		lBuffer.setPreferredSize(bufferSize);
		rBuffer.setPreferredSize(bufferSize);

		invPanel.add(lBuffer, BorderLayout.LINE_START);
		invPanel.add(rBuffer, BorderLayout.LINE_END);

		invPanel.add(inventory, BorderLayout.CENTER);
		left.add(invPanel,BorderLayout.PAGE_END);

		scoreJTA = new JTextArea("SCORE: ",4, 1);
		scoreJTA.setBackground(new Color(238,238,238));
		scoreJTA.setEditable(false);
		scoreJTA.setFont(new Font("Helvetica", Font.BOLD, 30));
		right.add(scoreJTA);

		minimap = new MiniMap();
		right.add(minimap, BorderLayout.PAGE_START);

		info = new Info();
		right.add(info,BorderLayout.PAGE_END);

		pane.add(left, BorderLayout.LINE_START);
		pane.add(right, BorderLayout.LINE_END);

		frame.setResizable(false);
		frame.setLocationRelativeTo(null); //centre's the frame on the middle of the screen
		frame.setVisible(true); //no more needing to re-run the app when it doesnt show
	}

	/**
	 * Redraws the frame and renderer.
	 */
	public void redraw() {
		frame.repaint();
		render.repaint();
	}


	/**
	 * Updates the score of the player
	 * @param score
	 */
	private void updateScore(int score) {
		scoreJTA.setText("SCORE: " + score);
	}


	/**
	 * Calls the appropriate decode method based off what type of input it is receiving
	 * the current room to the client.
	 * @param data
	 * @param inType 
	 * @throws IOException
	 * 
	 */
	public void decodeBytes(byte[] data, byte inType) throws IOException {
		switch(inType) {
		case 0: render.decodeRoom(data);
		break;
		case 1: decodePlayer(data);
				break;
		case 2: 
		case 3: decodeHint(data);
				break;
		case 4: decodeScoreboard(data);
				break;
		case 5: decodeChest(data);
				break;
				

		}
	}

	/**
	 * Updates the minimap to show which room the player is in.
	 * @param column of the room the player is in.
	 * @param row of the room the player is in.
	 * 
	 */
	public synchronized void updateMinimap(byte col, byte row) {
		minimap.updatePlayerLocation(col, row);
		minimap.repaint();
	}
	
	/**
	 * Decodes the players' score data and creates the scoreboard
	 * @param data
	 */
	public synchronized void decodeScoreboard(byte[] data) throws IOException {
		bin = new ByteArrayInputStream(data);
		din = new DataInputStream(bin);
		List<Integer[]> players = new ArrayList<Integer[]>();
		int numPlayers = (int)din.readByte();
		
		for(int i = 0; i < numPlayers; i++) {
			byte playerUid = din.readByte();
			int playerScore = din.readInt();
			Integer[] pl = new Integer[2];
			pl[0] = (int)playerUid;
			pl[1] = playerScore;
			players.add(pl);
		}
		if(numPlayers < 4) {
			for(int i = numPlayers; i < 4; i++) {
				Integer[] pl = {i+1,0};
				players.add(pl);
			}
		}
		Comparator<Integer[]> comp = (Integer[] a, Integer[] b) -> {
			return b[1].compareTo(a[1]);
		};
		
		Collections.sort(players, comp);
		
		Scoreboard scoreboard = new Scoreboard(this, players);
		scoreboard.setAlwaysOnTop(true);
		scoreboard.setVisible(true);
	}

	/**
	 * updates the player information from the server
	 * @param data
	 * @throws IOException 
	 * 
	 */
	private synchronized void decodePlayer(byte[] data) throws IOException {
		bin = new ByteArrayInputStream(data);
		din = new DataInputStream(bin);
		Icon[] icons = new Icon[Player.INVEN_SIZE];
		byte[] playerInven = new byte[Player.INVEN_SIZE];
		this.data = data;
		int playerScore = din.readInt();
		byte playerStatus = din.readByte();
		byte roomX = din.readByte();
		byte roomY = din.readByte();
		for(int i = 0; i < Player.INVEN_SIZE; i++) {
			byte itId = din.readByte();
			if(itId != 0) {
				icons[i] = new ImageIcon(itemIDMap.get(itId)[0]);
			} else {
				icons[i] = null;
			}
			playerInven[i] = itId;
		}
		inventory.updateButtons(icons, playerInven);
		updateScore(playerScore);
		updateMinimap(roomX, roomY);
		render.setPlayerStatus(playerStatus);
	}

	private synchronized void decodeHint(byte[] data) {
		String hint = new String(data);
		setInfo(hint);
	}

	/**
	 * Decodes the chest bytes and opens the chest UI
	 * @param data
	 * 
	 * @throws IOException 
	 */
	public synchronized void decodeChest(byte[] data) throws IOException {
		bin = new ByteArrayInputStream(data);
		din = new DataInputStream(bin);
		Icon[] icons = new Icon[Chest.INVEN_SIZE];
		byte[] chestInven = new byte[Chest.INVEN_SIZE];
		
		if(din.readBoolean() && chestFrame == null) {
			chestFrame = new ChestFrame(this);
		}

		for(int i = 0; i < Chest.INVEN_SIZE; i++) {
			byte itId = din.readByte();
			if(itId != 0) {
				icons[i] = new ImageIcon(itemIDMap.get(itId)[0]);
			} else {
				icons[i] = null;
			}
			chestInven[i] = itId;
		}

		chestFrame.updateButtons(icons, chestInven);
		render.requestFocus();
	}
	
	/**
	 * Closes the chestFrame window
	 */
	public synchronized void closeChest() {
		if(chestFrame != null) {
			chestFrame.setVisible(false);
			chestFrame.dispose();
			chestFrame = null;
		}
	}

	/**
	 * Sets the text of the info panel
	 * @param s
	 */
	public void setInfo(String s) {
		info.setText(s);
	}

	/**
	 * Gets the index of the button that was pressed and sends it to the slave.
	 * @param index
	 * 
	 */
	public void buttonPress(byte index) {
		client.buttonPressed(index);
		render.requestFocus();
	}

	private Map<Byte, String> initOvlyMap() {
		Map<Byte, String>  ovMap = new HashMap<Byte, String>();
		ovMap.put((byte) 1, ovFolder+"disoriented_overlay.png");
		ovMap.put((byte) 2, ovFolder+"stunned_overlay.png");
		return ovMap;
	}

	/**
	 * Initialises the player object resources.
	 * @return
	 * 
	 */
	private Map<Byte, String> initPlMap() {
		Map<Byte, String> plMap = new HashMap<Byte, String>();

		plMap.put((byte) 1, plFolder+"player_1.png");
		plMap.put((byte) 2, plFolder+"player_2.png");
		plMap.put((byte) 3, plFolder+"player_3.png");
		plMap.put((byte) 4, plFolder+"player_4.png");

		return plMap;
	}

	/**
	 * Initialises the square object resources.
	 * @return
	 * 
	 */
	private Map<Byte, String> initSqMap() {
		Map<Byte, String> sqMap = new HashMap<Byte, String>();
		//Blank square assets
		sqMap.put((byte) 1, sqFolder+"ground_beach_1.png");
		sqMap.put((byte) 2, sqFolder+"ground_beach_2.png");
		sqMap.put((byte) 3, sqFolder+"ground_forest_1.png");
		sqMap.put((byte) 4, sqFolder+"ground_forest_2.png");
		sqMap.put((byte) 5, sqFolder+"ground_cave_1.png");
		sqMap.put((byte) 6, sqFolder+"ground_cave_2.png");
		sqMap.put((byte) 7, sqFolder+"ground_cave_1_dark.png");
		sqMap.put((byte) 8, sqFolder+"ground_cave_2_dark.png");

		//Door square assets
		sqMap.put((byte) 9, sqFolder+"door_green.png");
		sqMap.put((byte) 10, sqFolder+"door_red.png");
		sqMap.put((byte) 11, sqFolder+"door_blue.png");
		sqMap.put((byte) 12, sqFolder+"door_black.png");
		sqMap.put((byte) 13, sqFolder+"door_silver.png");
		sqMap.put((byte) 14, sqFolder+"door_gold.png");
		sqMap.put((byte) 15, sqFolder+"door_green_dark.png");
		sqMap.put((byte) 16, sqFolder+"door_red_dark.png");
		sqMap.put((byte) 17, sqFolder+"door_blue_dark.png");
		sqMap.put((byte) 18, sqFolder+"door_black_dark.png");
		sqMap.put((byte) 19, sqFolder+"door_silver_dark.png");
		sqMap.put((byte) 20, sqFolder+"door_gold_dark.png");

		//Wall square assets
		sqMap.put((byte) 21, sqFolder+"wall_beach.png");
		sqMap.put((byte) 22, sqFolder+"wall_forest.png");
		sqMap.put((byte) 23, sqFolder+"wall_cave.png");
		sqMap.put((byte) 24, sqFolder+"wall_cave_dark.png");

		return sqMap;
	}

	/**
	 * Initialises the item object resources
	 * @return
	 * 
	 */
	private Map<Byte, String[]> initItMap() {
		Map<Byte, String[]> itMap = new HashMap<Byte, String[]>();
		String[] imgDesc = new String[2];
		//Misc
		imgDesc[0] = itFolder+"score_1.png";
		imgDesc[1] = "Score Bubble*Give you points.";
		itMap.put((byte) 1, imgDesc); 	//Score
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"torch_1.png";
		imgDesc[1] = "Torch*Lights your way.";
		itMap.put((byte) 2, imgDesc);	//Torch
		//Containers
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"chest_1.png";
		imgDesc[1] = "Chest*Holds things, how did you get this item?!";
		itMap.put((byte) 3, imgDesc);	//Container 1
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"chest_2.png";
		imgDesc[1] = "Chest*Holds things, how did you get this item?!";
		itMap.put((byte) 4, imgDesc);	//Container 2
		//Keys
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"key_green.png";
		imgDesc[1] = "Green Key*Opens green doors.";
		itMap.put((byte) 5, imgDesc);	//Green key
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"key_red.png";
		imgDesc[1] = "Red Key*Opens red doors.";
		itMap.put((byte) 6, imgDesc);	//Red key
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"key_blue.png";
		imgDesc[1] = "Blue Key*Opens blue doors.";
		itMap.put((byte) 7, imgDesc);	//Blue key
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"key_black.png";
		imgDesc[1] = "Black Key*Opens black doors.";
		itMap.put((byte) 8, imgDesc);	//Black key
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"key_silver.png";
		imgDesc[1] = "Silver Key*Opens silver doors";
		itMap.put((byte) 9, imgDesc);	//Silver key
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"key_gold.png";
		imgDesc[1] = "Gold Key*Opens gold doors";
		itMap.put((byte) 10, imgDesc);	//Gold key
		//Trees
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"tree_beach.png";
		imgDesc[1] = "Tree*Gives you hints, how did you get this item?!";
		itMap.put((byte) 11, imgDesc);	//Tree 1
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"tree_forest.png";
		imgDesc[1] = "Tree*Gives you hints, how did you get this item?!";
		itMap.put((byte) 12, imgDesc);	//Tree 2
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"tree_cave.png";
		imgDesc[1] = "Tree*Gives you hints, how did you get this item?!";
		itMap.put((byte) 13, imgDesc);	//Tree 3
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"tree_4.png";
		imgDesc[1] = "Tree*Gives you hints, how did you get this item?!";
		itMap.put((byte) 14, imgDesc);	//Tree 4
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"tree_5.png";
		imgDesc[1] = "Tree*Gives you hints, how did you get this item?!";
		itMap.put((byte) 15, imgDesc);	//Tree 5
		//Disarmed Traps
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"disorientTrap_disarmed.png";
		imgDesc[1] = "Disorientation Trap*Disorients the victim, causing their movements to be random for a short time.";
		itMap.put((byte) 16, imgDesc);	//Disorient Trap
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"scoreTrap_disarmed.png";
		imgDesc[1] = "Score Trap*Lowers the score of the victim.";
		itMap.put((byte) 17, imgDesc);	//Score Trap
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"stunTrap_disarmed.png";
		imgDesc[1] = "Stun Trap*Stuns the victim, stopping their movement for a short time.";
		itMap.put((byte) 18, imgDesc);	//Stun Trap
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"teleportTrap_disarmed.png";
		imgDesc[1] = "Teleportation Trap*Teleports the victim back to the starting room.";
		itMap.put((byte) 19, imgDesc);	//Teleport Trap
		//Armed Traps
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"disorientTrap_armed.png";
		imgDesc[1] = "Disorientation Trap*Disorients the victim, causing their movements to be random for a short time. How did you get this item?!";
		itMap.put((byte) 20, imgDesc);	//Armed Disorient Trap
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"scoreTrap_armed.png";
		imgDesc[1] = "Score Trap*Lowers the score of the victim. How did you get this item?!";
		itMap.put((byte) 21, imgDesc);	//Armed Score Trap
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"stunTrap_armed.png";
		imgDesc[1] = "Stun Trap*Stuns the victim, stopping their movement for a short time. How did you get this item?!";
		itMap.put((byte) 22, imgDesc);	//Armed Stun Trap
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"teleportTrap_armed.png";
		imgDesc[1] = "Teleportation Trap*Teleports the victim back to the starting room. How did you get this item?!";
		itMap.put((byte) 23, imgDesc);	//Armed Teleport Trap

		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_1.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 116, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_2.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 117, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_3.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 118, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_4.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 119, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_5.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 120, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_6.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 121, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_7.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 122, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_8.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 123, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_9.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 124, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_10.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 125, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_11.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 126, imgDesc);	//Win Item
		imgDesc = new String[2];
		imgDesc[0] = itFolder+"winItem_12.png";
		imgDesc[1] = "Win Item Trap*Gives you a bunch of points and ends the game. How did you get this item?!";
		itMap.put((byte) 127, imgDesc);	//Win Item

		return itMap;
	}
}