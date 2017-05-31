package model.squares;

/**
 * Square interface that represents a space in a room. Squares know if they're dark and have a uid which determines there graphic
 * @author Nathan Larson 300334702
 */  
public interface Square {
	
	/**
	 * Checks to see if the square is able to be moved onto by a player
	 */
	public boolean canMoveTo();
	
	/**
	 * Returns whether the square is dark or not
	 */
	public boolean isDark();
	
	/**
	 * Returns the uid of the square
	 */
	public byte getUid();

	void setDark(boolean dark);
}
