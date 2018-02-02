package concurrent;

import graphics.GameFrame;
import network.NetworkManager;

public class CountDownManager extends Thread {
	private boolean stopRequested;
	
	public CountDownManager() {
		stopRequested = false;
	}
	
	public void requestStop() {
		stopRequested = false;
	}
	
	@Override
	public void run() {
		while(!stopRequested) {
			int remaining = NetworkManager.getInstance().getCountDownRemaining();
			GameFrame.getInstance().updateCountDown(remaining);
			if(remaining <= 0)
				return;
		}
	}
}
