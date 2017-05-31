package view;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 * Frame for the Chest user interface.
 * @author Darren Hobern 300341002
 *
 */
public class ChestFrame extends JFrame {
	
	private ChestPanel chestPanel;
	
	public ChestFrame(GUI gui) {
		super("Chest");
		chestPanel = new ChestPanel(gui);
		setAlwaysOnTop(true);
		add(chestPanel);
		setPreferredSize(chestPanel.getPreferredSize());
		setSize(getPreferredSize());
		
		setLocationRelativeTo(null);
		
		
		setVisible(true);
		//Disconnect the player from the chest if this window is closed
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				gui.buttonPress((byte)(GUI.CHEST_OFFSET+GUI.CHEST_OFFSET));
			}
		});
	}
	
	/**
	 * Update the inventory buttons of the chest
	 * @param icons
	 * @param chestInven
	 */
	public void updateButtons(Icon[] icons, byte[] chestInven) {
		chestPanel.updateButtons(icons, chestInven);
	}
	

}
