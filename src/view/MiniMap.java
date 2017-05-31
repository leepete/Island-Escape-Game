package view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import model.Board;

/**
 * Minimap displays the room and door locations of the map from a birds-eye view. Also highlights what room 
 * the player is currently in
 * @author Nathan Larson 300334702
 *
 */
public class MiniMap extends JPanel{
	
	private static final int SQUARE_SIZE = 37;
	private static final int DOOR_HEIGHT = 6;
	private static final int DOOR_WIDTH = 3;
	private static final int X_OFFSET = 48;
	private static final int Y_OFFSET = 42;
	private int playerCol = Board.START_ROOM_COLUMN;
	private int playerRow = Board.START_ROOM_ROW;
	
	public MiniMap(){
		setPreferredSize(new Dimension(300,250));
		setBackground(Color.blue);
		setLayout(new BorderLayout());
		setVisible(true);
	}
	
	/**
	 * Updates the player's room position
	 * @param col
	 * @param row
	 */
	public void updatePlayerLocation(byte col, byte row) {
		playerCol = (int) col;
		playerRow = (int) row;
	}
	
	public void paint(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0,300,250);
		g.setColor(Color.WHITE);
		g.fillRect(10,10,280,230);
		for(int col = 0; col < 5; col++){
			for(int row = 0; row < 5; row++){
				
				if(col == playerCol && row == playerRow)
					g.setColor(new Color(187, 97, 81));
				else
					g.setColor(new Color(153, 153, 153));
				
				g.fillRect(X_OFFSET+(col*(SQUARE_SIZE+5)), Y_OFFSET/2+(row*(SQUARE_SIZE+5)), SQUARE_SIZE, SQUARE_SIZE);
				
				if(col == playerCol && row == playerRow)
					g.setColor(new Color(135, 65, 53));
				else
					g.setColor(new Color(103, 103, 103));
				g.drawRect(X_OFFSET+(col*(SQUARE_SIZE+5)), Y_OFFSET/2+(row*(SQUARE_SIZE+5)), SQUARE_SIZE, SQUARE_SIZE);
				

				g.setColor(new Color(103, 103, 103));

				if( ((col == 0 || col == 4) && row != 0) || ((col == 1 || col== 3) && (row == 2 || row == 3)) || (col == 2 && (row == 4 || row == 3)) ){
					g.fillRect(X_OFFSET+(col*(SQUARE_SIZE+5))+SQUARE_SIZE/2-DOOR_WIDTH/2, Y_OFFSET/2+(row*(SQUARE_SIZE+5))-DOOR_HEIGHT/2, DOOR_HEIGHT, DOOR_WIDTH); //North
				}
				if( ((row == 0 || row == 4) && col != 4) || ((row == 1 || row == 3) && (col == 1 || col == 2)) ){
					g.fillRect(X_OFFSET+(col*(SQUARE_SIZE+5))+SQUARE_SIZE-DOOR_WIDTH/2+1, Y_OFFSET/2+(row*(SQUARE_SIZE+5))+SQUARE_SIZE/2-DOOR_HEIGHT/2, DOOR_WIDTH, DOOR_HEIGHT);//East
				}
				if( ((col == 0 || col == 4) && row != 4) || ((col == 1 || col== 3) && (row == 2 || row == 1)) 
						|| (col == 2 && (row == 2 || row == 3)) ){
					g.fillRect(X_OFFSET+(col*(SQUARE_SIZE+5))+SQUARE_SIZE/2-DOOR_WIDTH/2, Y_OFFSET/2+(row*(SQUARE_SIZE+5))+SQUARE_SIZE, DOOR_HEIGHT, DOOR_WIDTH);//South
				}
				if(((row == 0 || row == 4) && col != 0) || ((row == 1 || row == 3) && (col == 3 || col == 2)) ){
					g.fillRect(X_OFFSET+(col*(SQUARE_SIZE+5))-2, Y_OFFSET/2+(row*(SQUARE_SIZE+5))+(SQUARE_SIZE)/2-DOOR_HEIGHT/2, DOOR_WIDTH, DOOR_HEIGHT);//West
				}
				
			}
		}
	}

}
