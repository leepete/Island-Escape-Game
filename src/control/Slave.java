package control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import model.Board;
import view.GUI;

/**
 * Slave connection that receives the bare minimum amount of information for the user interface to display.
 * Also sends mouse and key events to the server to process
 * 
 * @author Darren Hobern 300341002
 */
public class Slave extends Thread implements KeyListener {

	public static final byte INVENTORY_OFFSET = 10;
	public static final byte MENU_OFFSET = 30;
	
	private final Socket socket;
	private DataOutputStream output;
	private DataInputStream input;
	private int uid;

	/**
	 * Construct a slave connection from a socket. No logic is done by the slave other then display
	 * all logic is controlled by the master which receives input information from the slaves.
	 * @param socket
	 */
	public Slave(Socket socket) {
		this.socket = socket;
	}


	/**
	 * Reads incoming messages from the server and passes them onto the GUI
	 */
	public void run() {
		try {
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
			
			GUI display = new GUI(uid, this);
			
			boolean exit = false;
			while(!exit) {
				byte inType = input.readByte(); //Reads the type of input we're receiving
				switch(inType) {
				//Room
				case 0: byte state = input.readByte();
				//Player
				case 1:
				//Hint
				case 2:
				//WinItem
				case 3:
				//Endgame
				case 4:
				//Chest
				case 5: int dataLen = input.readInt();
						byte[] data = new byte[dataLen];
						input.readFully(data); //Reads the byte array into data
						display.decodeBytes(data, inType);
						display.redraw();
						break;
				//Chest closing
				case 6: display.closeChest();
						break;
				}
			}
			System.out.println("Client closing now");
			socket.close();
		} catch (IOException e) {
			System.err.println("SERVER SHUTTING DOWN");
		}
	}
	
	/**
	 * Sends info to the server that a clients button was pressed
	 * @param index
	 */
	public void buttonPressed(byte index) {
		try {
			output.writeByte(index+INVENTORY_OFFSET);
			output.flush();
		} catch(IOException e) {}
	}

	/**
	 * Sends to the server what button was pressed
	 * @param menuButton
	 */
	public void menu(byte menuButton) {
		try {
			output.writeByte(menuButton+MENU_OFFSET);
			output.flush();
		} catch(IOException e){}
	}
	
	/**
	 * Sends to the server what key was pressed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		try {
			int code = e.getKeyCode();
			if(code == KeyEvent.VK_W) {
				output.writeByte(0);
			} else if(code == KeyEvent.VK_S) {
				output.writeByte(1);
			} else if(code == KeyEvent.VK_D) {
				output.writeByte(2);
			} else if(code == KeyEvent.VK_A) {
				output.writeByte(3);
			} else if(code == KeyEvent.VK_E) {
				output.writeByte(4);
			} else if(code == KeyEvent.VK_Q) {
				output.writeByte(5);
			} else if(code == KeyEvent.VK_SPACE) {
				output.writeByte(6);
			}
			output.flush();
		} catch(IOException ioe) {}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
