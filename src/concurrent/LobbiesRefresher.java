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
		while(true) {
			// TODO rafraichir la fenetre si elle est ouverte
			
			try {
				NetworkManager.getInstance().updateLobbies();
				LobbiesFrame.getInstance().updateLobbies();
			} catch (Exception e1) {
				LobbiesFrame.getInstance().close();
				ConnectionFrame.getInstance().open();
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
