package model.items;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import control.Master;
import model.Board;
import model.Player;

/**
 * An item that causes the game to end. Players get a large score bonus for reaching this first
 * @author Nathan Larson 300334702
 *
 */
public class WinItem extends LargeItem {
	
	private static final byte uid = 116;
	private static byte id = uid;
	private int renderCount = 0;
	private String message = "Congratulations!* You won the game, now lets see how you did...";
	public int scoreGiven = 250;
	
	public WinItem(String name){
		this.name = "A Grossly Incandescent Portal";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte getUid() {
		renderCount++;
		if (renderCount == 20) {
			if(id == 127) {
				id = uid;
			} else {
				id++;
			}
			renderCount = 0;
		}	
		return id;
	}

	@Override
	public LargeItem connect(Player p) {
		System.out.println("GAME OVER");
		p.addScore(scoreGiven);
		Board.state = Board.GAMEOVER;
		return this;
	}
	
	@Override
	public boolean disconnect(Player p) {
		return true;
	}
	
	/**
	 * Updates the uid to the next frame of the animation,
	 * @author Darren Hobern 300341002
	 */
	public static void tick() {
		if(id == 127) {
			id = uid;
		} else {
			id++;
		}
	}

	/**
	 * Converts the win message to a byte array to be sent to the ui
	 * @author Darren Hobern 300341002
	 */
	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		dout.writeBytes(message);
		return bout.toByteArray();
	}
	
	@Override
	public byte getDataID() {
		return Master.WINITEM_DATA;
	}
	
	//used for testing
	public boolean equals(WinItem wi) {
		if (this.uid == wi.uid && this.name == wi.name && this.id == wi.id && this.scoreGiven == wi.scoreGiven && this.renderCount == wi.renderCount) {
			return true;
		}
		return false;
	}

}