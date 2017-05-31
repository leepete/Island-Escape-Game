package model.items;

import model.Player;

/**
 * Adds points to the players score when picked up, ScoreItems are automatically picked up when walked over.
 * @author Nathan Larson 300334702
 *
 */
public class ScoreItem implements Item {
	
	private final byte uid = 1;
	
	private String name;
	private int points;
	
	/**
	 * A pick up that players can pick up to score points
	 * @param points how many points to score a player when they pick this up
	 */
	public ScoreItem(int points){
		this.points = points;
	}
	
	public int getPoints(){
		return points;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public byte getUid() {
		return uid;
	}
	
	//used for testing
	public boolean equals(ScoreItem si) {
		if (this.uid == si.uid && this.name == si.name  && this.points == si.points) {
			return true;
		}
		return false;
	}
}
