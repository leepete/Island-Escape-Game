package model.items;

import java.util.ArrayList;

import model.enums.KeyType;

/**
 * KeyItem is a key which can open a door with the same KeyType. KeyItems are equals if they have the same KeyType.
 * @author Nathan Larson 300334702
 */
public class KeyItem implements Item {
	
	public final byte uid;
	
	private ArrayList<Byte> playerBlackList = new ArrayList<Byte>();
	private String name;
	private KeyType type;


	public KeyItem(KeyType type){
		this.type = type;
		uid = (byte) (5 + type.ordinal());
	}
	
	/**
	 * Alternate constructor used when loading to create keys that have already
	 * been accessed by players
	 * 
	 * @param type
	 * @param playerBlackList
	 * @author juddwill
	 */
	public KeyItem(KeyType type, ArrayList<Byte> playerBlackList){
		this.type = type;
		this.playerBlackList = playerBlackList;
		uid = (byte) (5 + type.ordinal());
	}

	@Override
	public boolean equals(Object o){
		if( o instanceof KeyItem ){
			return ((KeyItem) o).getType() == this.type;
		}
		return false;
	}

	public void addToList(Byte uid){
		playerBlackList.add(uid);
	}
	
	public void removeFromList(Byte uid){
		playerBlackList.remove(uid);
	}
	
	public boolean checkIfBlackListed(byte uid){
		return playerBlackList.contains(uid);
	}
	
	public KeyType getType(){
		return type;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public byte getUid() {
		return uid;
	}
	
	public ArrayList<Byte> getBlackList() {
		return playerBlackList;
	}

}