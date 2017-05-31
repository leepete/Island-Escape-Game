package model.items;

import model.Player;
import model.Room;
import model.enums.Status;
import model.squares.BlankSquare;

/**
 * Causes a player to become blind when triggered
 * @author Nathan Larson 300334702
 */
public class TeleportTrap extends TrapItem implements Item {
	
	private final byte uid = 19;
	
	private String name = "Teleporter Trap";
	private String desc = "When armed, causes a player to teleport back to the beach!";
	private Room teleRoom;
	
	public TeleportTrap(Room teleportRoom){
		super();
		this.teleRoom = teleportRoom;
	}
	
	/**
	 * Alternate constructor used when loading to create an armed trap. Room is then
	 * loaded later when iterating through rooms
	 * 
	 * @param armed
	 * @author juddwill
	 */
	public TeleportTrap(Boolean armed) {
		super(armed);
	}

	@Override
	public void triggered(Player p) {
		p.addScore(-pointsLost);
		((BlankSquare) p.getRoom().getBoard()[p.getLocation().x][p.getLocation().y]).removeOccupant();
		p.getRoom().updateAllLight();
		p.getRoom().remove(p);
		teleRoom.teleportPlayer(p);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte getUid() {
		return uid;
	}
	public void loadRoom(Room teleRoom) {
		this.teleRoom = teleRoom;
	}
	
	//used for testing
	public boolean equals(TeleportTrap tt) {
		if (this.uid == tt.uid && this.name == tt.name && this.desc == tt.desc && this.teleRoom == tt.teleRoom) {
			return true;
		}
		return false;
	}
}
