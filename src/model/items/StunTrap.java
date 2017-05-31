package model.items;

import model.Player;
import model.enums.Status;

/**
 * Causes a player to become stunned when triggered
 * @author Nathan Larson 300334702
 */
public class StunTrap extends TrapItem implements Item {
	
	private final byte uid = 18;
	
	private String name = "Bear Trap";
	private String desc = "When armed, will snap shut on the players leg, stunning them. Ouch.";
	
	public StunTrap(){
		super();
	}
	
	/**
	 * Alternate constructor used when loading to create an armed trap
	 * 
	 * @param armed
	 * @author juddwill
	 */
	public StunTrap(Boolean armed) {
		super(armed);
	}

	@Override
	public void triggered(Player p) {
		p.addScore(-pointsLost);
		p.setStatus(Status.STUNNED);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public byte getUid() {
		return uid;
	}
	
	//used for testing
	public boolean equals(StunTrap st) {
		if (this.uid == st.uid && this.name == st.name && this.desc == st.desc) {
			return true;
		}
		return false;
	}
}
