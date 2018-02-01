package network;

import java.net.ConnectException;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Lobby;
import game.Player;
import graphics.LobbiesFrame;

public class NetworkManager {
	private static final NetworkManager instance = new NetworkManager();
	
	private String serverUrl = "";
	
	public static NetworkManager getInstance() {
		return instance;
	}
	
	public boolean isConnected() {
		return serverUrl.length() > 0;
	}
	
	/**
	 * @param ip remote ip.
	 * @param port remote port.
	 * @return true iif connection successful, and stores ip and port in this case.
	 */
	public boolean connect(String ip, String port) {
		
		String tempUrl = "http://"+ip+":"+port;
		
		GetRequest request = new GetRequest(tempUrl);
		request.start();
		
		 // TODO attendre un signal de fin
		
		try {
			request.join();
		} catch (InterruptedException e) {
			this.serverUrl = "";
			e.printStackTrace();
			return false;
		}
		
		if(request.getResponse().equals("Ping")) {
			this.serverUrl = tempUrl;
			return true;
		}
		this.serverUrl = "";
		return false;
	}
	
	public void updateLobbies() throws Exception {
		serverUrl = "http://localhost:8080"; // TODO remove
		if(serverUrl == "")
			throw new ConnectException();

		GetRequest request = new GetRequest(serverUrl+"/lobbies");

		request.run(); // Actually, we execute it sequentially
		
		Lobby.lobbies.clear();
		
		JSONObject json = new JSONObject(request.getResponse());
		for(String str : json.keySet()) {
			Lobby newLobby = new Lobby(str);
			JSONArray arr = json.getJSONArray(str);
			for(int i = 0; i < arr.length(); i++) {
				JSONObject jsonPlayer = arr.getJSONObject(i);
				Player player = new Player();
				player.ip = (String) jsonPlayer.get("ip");
				player.name = (String) jsonPlayer.get("name");
				player.score = (int) jsonPlayer.get("score");
				player.ready = (boolean) jsonPlayer.get("ready");

				newLobby.players.add(player);
			}
			Lobby.lobbies.add(newLobby);
		}	
	}
	
}
