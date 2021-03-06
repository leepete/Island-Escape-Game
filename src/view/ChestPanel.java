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
import model.items.Chest;

/**
 * Panel component for the Chest user interface.
 * @author Darren Hobern 300341002
 *
 */
public class ChestPanel extends JPanel implements ActionListener, MouseListener {
	public JButton[] buttons = new JButton[Chest.INVEN_SIZE];
	private GUI gui;
	private byte[] chestInven;
	
	public ChestPanel(GUI gui){
		this.gui = gui;
		setPreferredSize(new Dimension(600,150));
		GridLayout grid = new GridLayout(1,Chest.INVEN_SIZE,10,5);
		setLayout(grid);
		for(int i=0;i<buttons.length;i++){
			buttons[i] = new JButton();
			//Set Button Image to correspond with Item in Container's Inventory.
			buttons[i].addActionListener(this);
			buttons[i].addMouseListener(this);
			add(buttons[i]);
		}
		setVisible(true);
	}
	
	/**
	 * Updates the buttons with the icons of the chest's inventory
	 * @param icons
	 */
	public void updateButtons(Icon[] icons, byte[] chestInven) {
		for(int i = 0; i < buttons.length; i++) {
			buttons[i].setIcon(icons[i]);
		}
		this.chestInven = chestInven;
	}

	/**
	 * Takes the event from the pressing a button and sends it to the slave.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object button = e.getSource();
		for(int i = 0; i < buttons.length; i++) {
			if(button.equals(buttons[i])) {
				gui.buttonPress((byte)(i+GUI.CHEST_OFFSET));
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
				if(buttons[i].getIcon() == null) return; //nothing to show if there's no item
				gui.setInfo(gui.itemIDMap.get(chestInven[i])[1]);
				break;
			}
		}
	}

	/**
	 * Resets the info panel to be blank.
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
