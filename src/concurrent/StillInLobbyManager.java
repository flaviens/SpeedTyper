package concurrent;

import game.Lobby;
import graphics.GameFrame;
import network.NetworkManager;

public class StillInLobbyManager extends Thread {
	private boolean stopRequested;
	
	public StillInLobbyManager() {
		stopRequested = false;
	}
	
	public void requestStop() {
		stopRequested = true;
	}

	@Override
	public void run() {
		while(!stopRequested) {
			try {
				NetworkManager.getInstance().stillInLobby();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
