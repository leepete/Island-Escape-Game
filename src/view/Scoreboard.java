package view;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This class shows the end game board of all the points scored by each player
 * @author Peter Lee 300343606 - Design
 * @author Darren Hobern 300341002 - Functionality
 * 
 */
public class Scoreboard extends JDialog{

	private static final long serialVersionUID = 1L;

	private GUI gui;
	
	/**
	 * Constructor for the Scoreboard
	 */
	public Scoreboard(GUI gui, List<Integer[]> players){
		this.gui = gui;	

		//SCOREBOARD container
		setTitle("Scoreboard");
		Container contain = getContentPane();
		contain.setLayout(new FlowLayout());

		//PANELS for each Row
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		JPanel row4 = new JPanel();

		//Main Outer panel for alignment
		JPanel gridPanel = new JPanel(new GridLayout(0,1));
		
		//=====First Place=====
		Integer[] pl1 = players.get(0);
		JLabel p1 = new JLabel("P" + pl1[0].intValue()); //Set name
		p1.setHorizontalAlignment(JTextField.LEFT);
		p1.setPreferredSize(new Dimension(30,20));
		row1.add(p1);

		JLabel pic1 = new JLabel(); //1st place Image
		pic1.setIcon(new ImageIcon(gui.playerIDMap.get( pl1[0].byteValue() )));
		pic1.setPreferredSize(new Dimension(90,70));
		row1.add(pic1);

		JTextField scoreNum1 = new JTextField(pl1[1].toString()); //score number
		scoreNum1.setPreferredSize(new Dimension(100,50));
		scoreNum1.setHorizontalAlignment(JTextField.CENTER);
		scoreNum1.setEditable(false);
		row1.add(scoreNum1);

		//=====Second Place=====
		Integer[] pl2 = players.get(1);
		JLabel p2 = new JLabel("P" + pl2[0].intValue());	//Set name
		p2.setHorizontalAlignment(JTextField.LEFT);
		p2.setPreferredSize(new Dimension(30,20));
		row2.add(p2);

		JLabel pic2 = new JLabel(); //picture
		pic2.setIcon(new ImageIcon(gui.playerIDMap.get( pl2[0].byteValue() )));
		pic2.setPreferredSize(new Dimension(90,70));
		row2.add(pic2);

		JTextField scoreNum2 = new JTextField(pl2[1].toString()); //score number
		scoreNum2.setPreferredSize(new Dimension(100,50));
		scoreNum2.setHorizontalAlignment(JTextField.CENTER);
		scoreNum2.setEditable(false);
		row2.add(scoreNum2);

		//=====Third Place=====
		Integer[] pl3 = players.get(2);
		JLabel p3 = new JLabel("P" + pl3[0].intValue());
		p3.setHorizontalAlignment(JTextField.LEFT);
		p3.setPreferredSize(new Dimension(30,20));
		row3.add(p3);

		JLabel pic3 = new JLabel(); //picture
		pic3.setIcon(new ImageIcon(gui.playerIDMap.get( pl3[0].byteValue() )));
		pic3.setPreferredSize(new Dimension(90,70));
		row3.add(pic3);

		JTextField scoreNum3 = new JTextField(pl3[1].toString()); //score number
		scoreNum3.setPreferredSize(new Dimension(100,50));
		scoreNum3.setHorizontalAlignment(JTextField.CENTER);
		scoreNum3.setEditable(false);
		row3.add(scoreNum3);


		//=====Fourth Place=====
		Integer[] pl4 = players.get(3);
		JLabel p4 = new JLabel("P" + pl4[0].intValue()); 
		p4.setHorizontalAlignment(JTextField.LEFT);
		p4.setPreferredSize(new Dimension(30,20));;
		row4.add(p4);

		JLabel pic4 = new JLabel(); //picture
		pic4.setIcon(new ImageIcon(gui.playerIDMap.get( pl4[0].byteValue() )));
		pic4.setPreferredSize(new Dimension(90,70));
		row4.add(pic4);

		JTextField scoreNum4 = new JTextField(pl4[1].toString()); //score number
		scoreNum4.setPreferredSize(new Dimension(100,50));
		scoreNum4.setHorizontalAlignment(JTextField.CENTER);
		scoreNum4.setEditable(false);
		row4.add(scoreNum4);


		//adding rows to Gridded layout
		gridPanel.add(row1);
		gridPanel.add(row2);
		gridPanel.add(row3);
		gridPanel.add(row4);
		add(gridPanel); //add to container

		setPreferredSize(new Dimension(300,420));
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true); 
		pack();
	}

}
