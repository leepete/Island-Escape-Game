package model.items;

import model.Player;

/**
 * Traps are automatically triggered if walked over when armed, and are automatically armed when placed. They cause 
 * negative effects on the affected player
 * @author Nathan Larson 300334702
 *
 */
public abstract class TrapItem implements Item {
	
	private boolean armed;
	protected int pointsLost = 25;
	
	public TrapItem(){
		armed = false;
	}
	
	public TrapItem(boolean armed) {
		this.armed = armed;
	}
	
	public abstract void triggered(Player p);

	public boolean isArmed(){
		return armed;
	}
	
	public void setArmed(boolean armed){
		this.armed = armed;
	}
	
}
