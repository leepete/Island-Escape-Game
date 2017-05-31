package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.Player;

/**
 * Panel component for the inventory of the player.
 * @author Darren Hobern 300341002
 *
 */
public class Inventory extends JPanel implements ActionListener, MouseListener {
	public JButton[] buttons = new JButton[Player.INVEN_SIZE];
	private GUI gui;
	private byte[] playerInven;
	
	public Inventory(GUI gui){
		this.gui = gui;
		setPreferredSize(new Dimension(600,100));
		GridLayout grid = new GridLayout(1,Player.INVEN_SIZE,10,5);
		setLayout(grid);
		for(int i=0;i<buttons.length;i++){
			buttons[i] = new JButton();
			//Set Button Image to correspond with Item in Player's Inventory.
			buttons[i].addActionListener(this);
			buttons[i].addMouseListener(this);
			add(buttons[i]);
		}
		setVisible(true);
	}
	
	/**
	 * Updates the buttons with the icons of the player's inventory
	 * @param icons
	 */
	public void updateButtons(Icon[] icons, byte[] playerInven) {
		
		for(int i = 0; i < buttons.length; i++) {
			buttons[i].setIcon(icons[i]);
		}
		this.playerInven = playerInven;
	}

	/**
	 * Takes the event from the pressing a button and sends it to the slave
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object button = e.getSource();
		for(int i = 0; i < buttons.length; i++) {
			if(button.equals(buttons[i])) {
				gui.buttonPress((byte)i);
			}
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	/**
	 * Sets the info panel to the item's description
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		Object button = e.getSource();
		for(int i = 0; i < buttons.length; i++) {
			if(button.equals(buttons[i])) {
				if(buttons[i].getIcon() == null) return; //nothing to show if theres no item
				gui.setInfo(gui.itemIDMap.get(playerInven[i])[1]);
				break;
			}
		}
	}

	/**
	 * Resets the info panel to blank
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		gui.setInfo("");
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
}
