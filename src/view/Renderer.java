package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Renders the board onto the GUI that will display each
 * 	and one of every item, square, player and can 
 * 	show the status effects of the current player
 * 
 * @author Peter Lee 300343606
 *
 */

public class Renderer extends DoubleBuffer {

	private static final long serialVersionUID = 1L;
	
	private final int xAxis = 580;
	private final int yAxis = 120;
	private final int hWidth = 64; //half width
	private final int hHeight = 32; //half height
	private int xMod = 0;
	private int yMod = 0;
	private int colMod = 0;
	private int rowMod = 0;

	private byte[] data;
	
	private byte playerStatus = 0;
	
	private GUI gui;
	
	/**
	 * Class constructor to display in the GUI
	 * @param gui
	 */
	public Renderer(GUI gui) {
		this.gui = gui;
	}

	/**
	 * This method will prevent flicking of the map,
	 * 		clearing the panel in order to paint to it
	 */
	@Override
	public synchronized void update(Graphics g){
		paint(g); //prevents flicker
	}

	/**
	 * Updates the data for the room from the server
	 * @param data
	 * @author Darren Hobern 300341002
	 */
	public void decodeRoom(byte[] data) throws IOException{
		this.data = data;
	}
	
	/**
	 * Sets player status
	 * @param playerStatus
	 * @author Darren Hobern 300341002
	 */
	public void setPlayerStatus(byte playerStatus) {
		this.playerStatus = playerStatus;
	}
	
	
	/**
	 * Draws the graphics on the rendering canvas.
	 * @author Darren Hobern 300341002
	 */
	@Override
	public void paintBuffer(Graphics g) throws IOException {
		xMod = 0; yMod = 0; colMod = 0; rowMod = 0;
		if(data == null) return;
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);
		byte width, height;
		width = din.readByte();
		height = din.readByte();
		
		for(int col = 0; col < width; col++ ){
			for (int row = 0; row < height; ++row) {
				
				//All the needed info about the squares:
				byte sqrUid = din.readByte();
				boolean dark = din.readBoolean();
				boolean blacklisted = din.readBoolean();
				byte itemUid = din.readByte();
				byte playerUid = din.readByte();
				
				drawSquare(sqrUid, dark, blacklisted, itemUid, playerUid, g);
				rowMod +=1;
		
		}
			colMod+=1;
			rowMod = 0;
			xMod = xMod - hWidth;
			yMod = yMod - hHeight;
		}
		
		drawStatus(g);
	}
	
	/**
	 * Draws an image overlay depending on player status.
	 * @param g
	 * @throws IOException
	 * @author Darren Hobern 300341002
	 */
	private void drawStatus(Graphics g) throws IOException {
		if(playerStatus == 0) return;
		String path = gui.overlayIDMap.get(playerStatus);
		BufferedImage ov = ImageIO.read(new File(path));
		g.drawImage(ov, 0, 0, null);
	}

	/**
	 * Draws a square based off the information received from the server
	 * @param sqrUid
	 * @param dark
	 * @param itemUid
	 * @param playerUid
	 * @param g
	 * @throws IOException
	 */
	private void drawSquare(byte sqrUid, boolean dark, boolean blacklisted, byte itemUid, byte playerUid, Graphics g) throws IOException{
		String path = gui.squareIDMap.get(sqrUid);
		BufferedImage sq = ImageIO.read(new File(path));

		//Draw base
		if(sqrUid < 9)
			g.drawImage(sq, xAxis+hWidth*rowMod+xMod, 
					yAxis+hWidth*colMod+hHeight*rowMod+yMod, null);
		
		//Draw walls
		if(sqrUid != 0 && sqrUid >= 21){
			path = gui.squareIDMap.get(sqrUid);
			BufferedImage wall = ImageIO.read(new File(path));
			g.drawImage(wall, xAxis+hWidth*rowMod+xMod,
					yAxis+hWidth*colMod+hHeight*rowMod+yMod-sq.getHeight()/3+7, null);
		}
		//Draw DOORS
		if(sqrUid != 0 && sqrUid >= 9 && sqrUid <= 20){
			path = gui.squareIDMap.get(sqrUid);
			BufferedImage wall = ImageIO.read(new File(path));
			g.drawImage(wall, xAxis+hWidth*rowMod+xMod + sq.getWidth()/3 - wall.getWidth()/3, 					
					yAxis+hWidth*colMod+hHeight*rowMod+yMod-sq.getHeight()/3 + wall.getHeight()/3, null);
			}
		
		//Draw items
		if(itemUid != 0 && !dark) {
			//System.out.println("item:" +itemUid);
			if (!blacklisted) {
				path = gui.itemIDMap.get(itemUid)[0];
				//System.out.println(path);
				BufferedImage item = ImageIO.read(new File(path));
				g.drawImage(item, (xAxis+hWidth*rowMod+xMod) + sq.getWidth()/2- item.getWidth()/2 ,
						(yAxis+hWidth*colMod+hHeight*rowMod+yMod) - item.getHeight()+sq.getHeight()/2, null);
			} else {
				path = "./resources/images/tiles/items/key_absent.png";
				//System.out.println(path);
				BufferedImage item = ImageIO.read(new File(path));
				g.drawImage(item, (xAxis+hWidth*rowMod+xMod) + sq.getWidth()/2- item.getWidth()/2 ,
						(yAxis+hWidth*colMod+hHeight*rowMod+yMod) - item.getHeight()+sq.getHeight()/2, null);
			}
		}
		//Draw players
		if(playerUid != 0){
			path = gui.playerIDMap.get(playerUid);
			BufferedImage player = ImageIO.read(new File(path));
			g.drawImage(player, (xAxis+hWidth*rowMod+xMod) + sq.getWidth()/2 - player.getWidth()/2,
					(yAxis+hWidth*colMod+hHeight*rowMod+yMod) - player.getHeight()+sq.getHeight()/2, null);
		}
	}
}