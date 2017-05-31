package model.items;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import control.Master;
import model.Player;

/**
 * A chest can hold other items, and acts as a storage for players
 * @author Nathan Larson 300334702
 *
 */
public class Chest extends LargeItem {
	
	public static final int INVEN_SIZE = 6;
	
	private ArrayList<Player> playersUsingChest = new ArrayList<Player>();
	private Item[] contents;
	public final byte uid = 3;
	private boolean openChest = false;
	
	public Chest(){
		name = "Chest";
		contents = new Item[INVEN_SIZE];
	}
	
	/**
	 * Alternate constructor used when loading to create a Chest
	 * that already has items in it.
	 * 
	 * @author juddwill
	 */
	public Chest(Item[] contents) {
		name = "Chest";
		this.contents = contents;
	}
	
	/**
	 * Adds an item to the first empty position in the chest. Returns false is chest is full
	 * @param item to be added
	 */
	public boolean addItem(Item item){
		for(int i = 0; i < INVEN_SIZE; i++){
			if(contents[i] == null){
				contents[i] = item;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes and returns the item at index
	 */
	public void transferItem(int index, byte uid){
		Item item = contents[index];
		
		if(contents[index] != null && contents[index].equals(item)){
			
			Item matchingItem = contents[index];

			for(Player p : playersUsingChest){
				if(p.uid == uid){
					if(p.addItem(matchingItem))
						contents[index] = null;
					return;
				}
			}
		}
	}
	
	/**
	 * Converts the contents of this chest to a byte array to be sent to the ui
	 * @return
	 * @throws IOException
	 * @author Darren Hobern 300341002 
	 */
	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		
		dout.writeBoolean(openChestUi());
		byte itUid;
		for(int i = 0; i < contents.length; i++){
			if(contents[i] == null) {
				itUid = (byte) 0;
			} else {
				itUid = contents[i].getUid();
			}
			dout.writeByte(itUid);
		}
		dout.flush();
		return bout.toByteArray();
	}
	
	
	//used for save/load
	public Item[] getContainedItems() {
		return contents;
	}

	public boolean hasItem(Item item) {
		for(int i = 0; i < INVEN_SIZE; i++){
			if(contents[i] != null && contents[i].equals(item))
				return true;		
		}
		return false;
	}
	
	@Override
	public byte getDataID() {
		return Master.CHEST_DATA;
	}

	@Override
	public byte getUid() {
		return uid;
	}
	
	@Override
	public LargeItem connect(Player p) {
		playersUsingChest.add(p);
		openChest = true;
		return this;
	}
	
	/**
	 * Returns true if a new chest ui should be opened
	 * @return
	 * @author Darren Hobern 300341002
	 */
	public boolean openChestUi() {
		boolean open = openChest;
		openChest = false;
		return open;
	}
	
	
	@Override
	public boolean disconnect(Player p) {
		return playersUsingChest.remove(p);
	}
	
}