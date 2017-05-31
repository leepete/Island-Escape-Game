package model.squares;

import java.util.Random;

import model.Player;
import model.items.Chest;
import model.items.Item;
import model.items.LargeItem;
import model.items.ScoreItem;
import model.items.TrapItem;
import model.items.Tree;

/**
 * BlankSquare makes up the majority of the board. Can hold players and items, and holds its tiletype. 
 * @author Nathan Larson 300334702
 */
public class BlankSquare implements Square {

	protected byte uid;

	private Item item;
	private boolean dark;
	protected Player occupant;
	
	/**
	 * 
	 * @param tileType must match an image name in the image folder
	 * @param dark 
	 */
	public BlankSquare(boolean dark, byte uid){
		this.dark = dark;
		int randomTexture = (int) (Math.round((Math.random())));
		System.out.println(randomTexture);
		if(uid > 8) randomTexture = 0;
		this.uid = (byte)(uid+randomTexture);
	}
	
	/**
	 * Alternate constructor used when loading to create a square that may already be
	 * occupied by a player and/or an item
	 * 
	 * @param dark
	 * @param item
	 * @param occupant
	 * @param uid
	 * @author juddwill
	 */
	public BlankSquare(boolean dark, Item item, Player occupant, byte uid) {
		this.dark = dark;
		this.item = item;
		this.occupant = occupant;
		this.uid = (byte)(uid);
	}

	/**
	 * Place an item on the square if it has no item on it already. Traps will become armed when placed
	 * @Note Can't place items on door squares
	 * @param i the item to be placed down
	 */
	public boolean placeItem(Item i){
		if(item == null){
			item = i;
			if(item instanceof TrapItem)
				((TrapItem) item).setArmed(true);
			return true;
		}
		return false;
	}
	
	/**
	 * Moves a player into this square (triggering items that respond to a player stepping on them)
	 * @param p the player to be moved
	 */
	public void moveTo(Player p){
		occupant = p;
		
		if(item != null){
			//If there's an armed trap on the square, trigger it
			if(item instanceof TrapItem){
				if(((TrapItem) item).isArmed()){
					((TrapItem) item).triggered(p);
					item = null;
				}
			}
			//If there's a score pickup, pick it up auto
			else if(item instanceof ScoreItem){
				p.addScore(((ScoreItem) item).getPoints());
				item = null;
			}
		}
	}
	
	public void removeOccupant(){
		occupant = null;
	}

	@Override
	public boolean canMoveTo() {
		if(occupant == null && !(item instanceof LargeItem)) return true;
		return false;
	}

	public boolean isOccupied() {
		return occupant != null;
	}
	
	/**
	 * Sets the item to i, overwriting the item already there, does not arm traps
	 * @param i
	 */
	public void setItem(Item i){
		item = i;
	}
	
	public Item getItem(){
		return item;
	}
	
	@Override
	public void setDark(boolean dark){
		this.dark = dark;
	}

	@Override
	public boolean isDark() {
		return dark && !isOccupied();
	}
	
	public Player getPlayer() {
		return occupant;
	}
	
	public byte getUid() {
		return uid;
	}
}
