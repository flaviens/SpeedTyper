package concurrent;

import java.net.ConnectException;

import graphics.ConnectionFrame;
import graphics.LobbiesFrame;
import network.NetworkManager;

public class LobbiesRefresher extends Thread {

	private boolean stopRequested;
	
	public LobbiesRefresher() {
		stopRequested = false;
	}
	
	public void requestStop() {
		stopRequested = true;
	}
	
	@Override
	public void run() {
		while(!stopRequested) {			
			NetworkManager.getInstance().updateLobbies();
			LobbiesFrame.getInstance().updateLobbies();
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
