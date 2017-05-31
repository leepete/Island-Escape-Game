package control;

import model.Board;

/**
 * Updates the player status and animation for the portal
 * @author Darren Hobern 300341002
 *
 */
public class ClockThread extends Thread {
	private final int delay;
	private final Board board;

	public ClockThread(int delay, Board board) {
		this.delay = delay;
		this.board = board;
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(delay);
				board.clockTick();
			} catch(InterruptedException e) {
				//Shouldn't get here
			}
		}
	}
}
