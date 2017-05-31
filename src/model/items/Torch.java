package model.items;

/**
 * If a room is dark, a Torch is needed to light it up so players can see items. Torches do not need to be equip and will automatically
 * work once in a dark room with one in the inventory. All torches are equal.
 * @author Nathan Larson 300334702
 *
 */
public class Torch implements Item {
	
	public final byte uid = 2;
	
	public static final int RADIUS = 2;
	private String name = "Torch";
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Torch;
	}
	
	public byte getUid() {
		return uid;
	}
	
	//used for testing
	public boolean equals(Torch t) {
		if (this.uid == t.uid && this.name == t.name) {
			return true;
		}
		return false;
	}
}
