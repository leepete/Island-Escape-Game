package model.items;

import java.io.IOException;

import model.Player;

/**
 * A large item is an item that can not be walked onto, but walking into it 'connects' the player with it, storing a reference to
 * the object inside the player
 * @author Nathan Larson 300334702
 */
public abstract class LargeItem implements Item {
	
	protected String name;
	protected byte uid;
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte getUid() {
		return uid;
	}
	
	
	public abstract byte getDataID();
	public abstract LargeItem connect(Player p);
	public abstract boolean disconnect(Player p);
	public abstract byte[] toByteArray() throws IOException;

}
