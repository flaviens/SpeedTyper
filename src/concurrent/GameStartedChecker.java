package concurrent;

import game.Lobby;
import graphics.GameFrame;
import graphics.LobbyFrame;
import network.NetworkManager;

public class GameStartedChecker extends Thread {
	private boolean stopRequested;
	
	public GameStartedChecker() {
		stopRequested = false;
	}
	
	public void requestStop() {
		stopRequested = true;
	}

	@Override
	public void run() {
		while(!stopRequested) {
			try {
				if(NetworkManager.getInstance().isGameCreated()) {
					LobbyFrame.getInstance().close();
					GameFrame.getInstance().open();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
