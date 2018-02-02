package concurrent;

import game.Lobby;
import graphics.GameFrame;
import network.NetworkManager;

public class GameUpdater extends Thread {
	private boolean stopRequested;
	
	public GameUpdater() {
		stopRequested = false;
	}
	
	public void requestStop() {
		stopRequested = true;
	}

	@Override
	public void run() {
		while(!stopRequested) {
			try {
				NetworkManager.getInstance().updateGamePlayers(Lobby.currentLobby.name);
				if(NetworkManager.getInstance().isGameFinished())
					GameFrame.getInstance().finish();
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
