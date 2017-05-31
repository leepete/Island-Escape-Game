package model.items;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import control.Master;
import model.Player;

/**
 * A tree provides hints to players that walk into them. They are the only Item that cannot be walked onto.
 * @author Nathan Larson 300334702
 *
 */
public class Tree extends LargeItem {
	
	private static byte DATA_ID = Master.TREE_DATA;
	private final byte uid;
	
	private String name;
	private String hint;
	private String[] hints = {"Try running in circles!", "Head towards the center!", "Watch out for traps!", 
			"If it's dark make sure you have a torch on you!", "Don't forget to get those score bubbles before other players grab them!", 
			"The cake is a lie!", "There is no cow level!"};
	
	public Tree(byte uid){
		this.uid = uid;
		this.name = "Tree";
		
		int randomHint = (int) (Math.floor(Math.random()*hints.length));
		this.hint = "HINT*" +hints[randomHint];
	}
	
	/**
	 * Alternate constructor used when loading to create a tree with a given
	 * hint to ensure correct hint to tree mapping is carried over
	 * 
	 * @param uid
	 * @param hint
	 * @author juddwill
	 */
	public Tree(byte uid, String hint) {
		this.uid = uid;
		this.hint = hint;
		this.name = "Tree";
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getHint(){
		return hint;
	}
	
	@Override
	public byte getUid() {
		return uid;
	}

	@Override
	public LargeItem connect(Player p) {
		return this;
	}
	
	@Override
	public boolean disconnect(Player p) {
		return true;
	}

	
	@Override 
	public byte getDataID() {
		return DATA_ID;
	}

	/**
	 * Converts the hint to a byte array to be sent to the server
	 * @author Darren Hobern 300341002
	 */
	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		dout.writeBytes(hint);
		return bout.toByteArray();
	}
	
	//used for testing
	public boolean equals(Tree t) {
		if (this.uid == t.uid && this.name == t.name && this.hint == t.hint) {
			return true;
		}
		return false;
	}
}