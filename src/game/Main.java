package game;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import control.*;
import data.*;
import model.Board;
import model.enums.Status;

/**
 * Main method for the game, this will be run with the -server and -connect arguments
 * to create the server and clients running/playing the game.
 * @author Darren Hobern 300341002
 *
 */
public class Main {
	public static final int DEFAULT_CLOCK_PERIOD = 20;
	private static final int DEFAULT_BROADCAST_CLOCK_PERIOD = 5;
	private static Board board;

	public static void main(String[] args) {
		boolean server = true; //Server by default
		int numPlayers = 2; //Default of 2 players
		String url = null;
		int port = 5000;

		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-")) {
				String arg = args[i];
				switch(arg) {
				case "-server":
					System.out.println("SERVER");
					server = true;
					numPlayers = Integer.parseInt(args[++i]);
					if(numPlayers < 2 || numPlayers > 4) {
						System.out.println("Number of players must be between 2 and 4");
						System.exit(1);
					}
					break;
				case "-connect":
					System.out.println("CLIENT");
					server = false;
					url = args[++i];
					break;
				case "-port":
					port = Integer.parseInt(args[++i]);
					System.out.println("Using port " + port);
					break;
				}
			}
		}

		if(url != null && server) {
			System.out.println("Cannot connect a server to another server");
			System.exit(1);
		}

		try {
			if(server) {
				//Server
				board = new Board();
				Load load = new Load();
				Save save = new Save();
				runServer(port, numPlayers, board);
			} else if(url != null) {
				//Client
				runClient(url, port);
			} else {
				System.out.println("Error running server or client"); //TODO change this error message
				System.exit(1);
			}
		} catch(IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}

	/**
	 * Starts a client connecting to the passed address and port.
	 * @param addr
	 * @param port
	 * @throws IOException
	 */
	private static void runClient(String addr, int port) throws IOException {
		Socket s = new Socket(addr, port);
		System.out.println("Connecting client to " + addr + ":" + port);
		new Slave(s).run();
	}

	/**
	 * Start a server using the port and number of players argument.
	 * @param port
	 * @param numPlayers
	 * @param board
	 */
	private static void runServer(int port, int numPlayers, Board board) {
		ClockThread clock = new ClockThread(DEFAULT_CLOCK_PERIOD, board);

		System.out.println("GAME SERVER LISTENING ON PORT " + port);
		System.out.println("GAME SERVER WAITING FOR " + numPlayers + " CLIENTS");

		try {
			Master[] connections = new Master[numPlayers];
			//Wait for connections
			ServerSocket ss = new ServerSocket(port);
			while(true) {
				//wait for a socket
				Socket sock = ss.accept();
				System.out.println("ACCEPTED CONNECTION FROM: " + sock.getInetAddress());
				byte uid = board.registerPlayer();
				Master master = new Master(sock, uid, DEFAULT_BROADCAST_CLOCK_PERIOD, board);
				connections[--numPlayers] = master;
				connections[numPlayers].start();
				board.getPlayer(uid).setStatus(Status.STUNNED);
				if(numPlayers == 0) {
					System.out.println("ALL CLIENTS ACCEPTED -- START GAME!");
					startGame(board, clock, connections);
					System.out.println("ALL CLIENTS DISCONNECTED -- END GAME!");
					ss.close();
					return;
				}
			}
			
		} catch(IOException e) {
			System.err.println("I/O ERROR: " + e.getMessage());
		}

	}

	/**
	 * Start the game, game will run until all clients disconnect or someone wins
	 * @param board
	 * @param clock
	 * @param connections
	 * @throws IOException
	 */
	private static void startGame(Board board, ClockThread clock, Master... connections) throws IOException {
		clock.start();
		board.setState(Board.WAITING);
		
		//Pause for a moment
		pause(3000);
		//Start the game
		board.setState(Board.PLAYING);
		
		while(board.state == Board.PLAYING && atleastOneConnection(connections)) {
			Thread.yield();
		}
		
		board.stunPlayers();
		pause(20000);

	}
	
	/**
	 * Pauses the thread
	 * @param delay, time in milliseconds to pause
	 */
	private static void pause(int delay) {
		try {
			Thread.sleep(delay);
		} catch(InterruptedException e){			
		}
	}

	/**
	 * Returns true if there is at least one connection to the server.
	 * @param connections
	 * @return
	 */
	private static boolean atleastOneConnection(Master... connections) {
		for(Master m : connections) {
			if(m.isAlive()) {
				return true;
			}
		}
		return false;
	}

}