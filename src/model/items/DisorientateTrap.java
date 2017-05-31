package model.items;

import model.Player;
import model.enums.Status;

/**
 * Causes a player to become disorientated when triggered
 * @author Nathan Larson 300334702
 */
public class DisorientateTrap extends TrapItem implements Item {
	
	public final byte uid = 16;
	
	private String name = "Smoke Bomb";
	private String desc = "When armed, causes disorientation to the player that walks onto this trap.";
	
	public DisorientateTrap(){
		super();
	}
	
	/**
	 * Alternate constructor used when loading to create an armed trap
	 * 
	 * @param armed
	 * @author juddwill
	 */
	public DisorientateTrap(Boolean armed) {
		super(armed);
	}

	@Override
	public void triggered(Player p) {
		p.addScore(-pointsLost);
		p.setStatus(Status.DISORIENTATED);
	}

	@Override
	public String getName() {
		return name;
	}
	
	public byte getUid() {
		return uid;
	}
	
	//used for testing
	public boolean equals(DisorientateTrap dt) {
		if (this.uid == dt.uid && this.name == dt.name && this.desc == dt.desc) {
			return true;
		}
		return false;
	}

}
