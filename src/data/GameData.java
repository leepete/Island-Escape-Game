package data;

import java.util.List;

import model.Player;
import model.Room;

/**
 * A GameData object represents the data needed to load a game. This is
 * a 2d array of Room objects and a List of Player objects
 * 
 * @author William Judd 300262468
 */
public class GameData {
	private final Room[][] rooms;
	private final List<Player> players;
	
	public GameData(Room[][] rooms, List<Player> players) {
		this.rooms = rooms;
		this.players = players;
	}
	
	public Room[][] getRooms() {
		return rooms;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
}
