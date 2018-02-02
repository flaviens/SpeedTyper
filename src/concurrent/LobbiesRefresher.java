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
			// TODO quitter automatiquement les lobbies qui n'ont pas �t� quitt�s proprement
			
			try {
				NetworkManager.getInstance().updateLobbies();
				LobbiesFrame.getInstance().updateLobbies();
			} catch (Exception e1) {
				NetworkManager.getInstance().disconnect();
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
