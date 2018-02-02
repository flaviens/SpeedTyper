package concurrent;

import game.Lobby;
import graphics.ConnectionFrame;
import graphics.GameFrame;
import graphics.LobbiesFrame;
import graphics.LobbyFrame;
import network.NetworkManager;

public class LobbyJoiner extends Thread {
	
	String lobbyName;
	String playerName;
	
	public LobbyJoiner() {
	}
	
	public void start(String lobbyName, String playerName) {
		this.lobbyName = lobbyName;
		this.playerName = playerName;
		
		start();
	}

	@Override
	public void run() {
		try {
			if(NetworkManager.getInstance().joinLobby(lobbyName, playerName)) {
				GameFrame.getInstance().close();
				ConnectionFrame.getInstance().close();
				LobbiesFrame.getInstance().close();
				LobbyFrame.getInstance().open();
			}
			
			Thread fetchWords = new Thread(new Runnable() {
				@Override
				public void run() {
					NetworkManager.getInstance().fetchWords();
				}
			});
			fetchWords.start();
		} catch (Exception e) {
			e.printStackTrace();
			NetworkManager.getInstance().disconnect();
		}
	}
	
	
}
