package concurrent;

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
			// TODO chercher les lobbies et les mettre dans un conteneur
			// TODO rafraichir la fenetre si elle est ouverte
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
