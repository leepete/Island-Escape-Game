package model.squares;

/**
 * WallSquare is a Square that players can't walk onto and that can't hold any items
 * @author Nathan Larson 300334702
 */
public class WallSquare implements Square {

	public final byte uid;

	private boolean dark;

	public WallSquare(boolean dark, byte uid){
		this.dark = dark;
		this.uid = uid;
	}

	@Override
	public boolean canMoveTo() {
		//Can't move to a WallSquare
		return false;
	}

	@Override
	public boolean isDark() {
		return dark;
	}

	public byte getUid() {
		return uid;
	}

	@Override
	public void setDark(boolean dark) {
		this.dark = dark;
		
	}
}
