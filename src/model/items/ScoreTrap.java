package model.items;

import model.Player;
import model.enums.Status;

/**
 * A trap that causes players to lose points from their score when they trigger it. 
 * @author Nathan Larson 300334702
 *
 */
public class ScoreTrap extends TrapItem implements Item {
	
	public final byte uid = 17;
	
	private int extraPointsLost = 100;
	private String name = "Stank Bomb";
	private String desc = "When armed, causes the player that walks onto this trap to lose points.";
	
	/**
	 * Alternate constructor used when loading to create an armed trap
	 * 
	 * @param armed
	 * @author juddwill
	 */
	public ScoreTrap(boolean armed) {
		super(armed);
	}
	
	public ScoreTrap() {
		super();
	}

	@Override
	public void triggered(Player p) {
		p.addScore(-(pointsLost+extraPointsLost));
	}

	@Override
	public String getName() {
		return name;
	}
	
	public byte getUid() {
		return uid;
	}
	
	//used for testing
	public boolean equals(ScoreTrap st) {
		if (this.uid == st.uid && this.name == st.name && this.desc == st.desc && this.extraPointsLost == st.extraPointsLost) {
			return true;
		}
		return false;
	}
}
