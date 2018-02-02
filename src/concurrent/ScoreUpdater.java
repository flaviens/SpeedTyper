package concurrent;

import graphics.GameFrame;
import network.NetworkManager;

public class ScoreUpdater extends Thread {

	private boolean stopRequested;

	public ScoreUpdater() {
		stopRequested = false;
	}

	public void requestStop() {
		stopRequested = false;
	}

	@Override
	public void run() {
		while (!stopRequested) {
			NetworkManager.getInstance().updateScore(GameFrame.getInstance().getScore());
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
