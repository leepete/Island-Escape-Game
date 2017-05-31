package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Info panel is used to display game information to the player. Such information is for example
 * item descriptions or hints from tree.
 * @author Nathan Larson 300334702
 *
 */

public class Info extends JPanel {

	JTextPane text = new JTextPane ();
	JTextPane text2 = new JTextPane ();


	public Info(){
		Border border = BorderFactory.createLineBorder(Color.GRAY, 10);
		setPreferredSize(new Dimension(300,250));
		setLayout(new BorderLayout());
		setBorder(border);
		setBackground(Color.WHITE);

		//Center the text
		StyledDocument doc = text.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		text.setPreferredSize(new Dimension(300,35));
		text.setFont(new Font("Helvetica", Font.BOLD, 20));
		add(text,BorderLayout.PAGE_START);

		text2.setPreferredSize(new Dimension(300,200));
		text2.setFont(new Font("Helvetica", Font.PLAIN, 20));
		add(text2,BorderLayout.PAGE_END);

		setVisible(true);
	}

	public void setText(String info){
		
		if(info == ""){
			text.setText(info);
			text2.setText(info);
			return;
		}
		
		int titleBreak = info.indexOf("*"); //Titles are marked by *
		
		if(titleBreak != -1){ //If there is a title in the text
			String title = info.substring(0, titleBreak); //Break up text at the titile
			String infoText = info.substring(titleBreak+1);
			text.setText(title);
			text2.setText(infoText);
		}
		else{
			text2.setText(info);
		}
	}
}
