package network;

import java.net.ConnectException;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Lobby;
import game.Player;
import graphics.ConnectionFrame;
import graphics.GameFrame;
import graphics.LobbiesFrame;
import graphics.LobbyFrame;

public class NetworkManager {
	private static final NetworkManager instance = new NetworkManager();
	
	private String serverUrl = "";
	
	// TODO harmoniser les try et les throws: on try/catch ou on throw, mais pas random un des deux à chaque fois.
	
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
	
	public void disconnect() {
		serverUrl = "";
		GameFrame.getInstance().close();
		LobbiesFrame.getInstance().close();
		LobbyFrame.getInstance().close();
		ConnectionFrame.getInstance().open();
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
				player.score = jsonPlayer.getInt("score");
				player.ready = (boolean) jsonPlayer.get("ready");

				newLobby.players.add(player);
			}
			Lobby.lobbies.add(newLobby);
		}	
	}
	
	public void updateLobby(String lobbyName) throws Exception {
		serverUrl = "http://localhost:8080"; // TODO remove
		if(serverUrl == "")
			throw new ConnectException();

		GetRequest request = new GetRequest(serverUrl+"/lobbies");

		request.run(); // Actually, we execute it sequentially
		
		Lobby.currentLobby = new Lobby(lobbyName);
		
		JSONObject json = new JSONObject(request.getResponse());
		for(String str : json.keySet()) {
			if(!str.equals(lobbyName))
				continue;
			Lobby.currentLobby = new Lobby(str);
			JSONArray arr = json.getJSONArray(str);
			for(int i = 0; i < arr.length(); i++) {
				JSONObject jsonPlayer = arr.getJSONObject(i);
				Player player = new Player();
				player.ip = (String) jsonPlayer.get("ip");
				player.name = (String) jsonPlayer.get("name");
				player.score = (int) jsonPlayer.get("score");
				player.ready = (boolean) jsonPlayer.get("ready");
				Lobby.currentLobby.players.add(player);
			}
			break;
		}	
	}

	public void updateGamePlayers(String gameName) throws Exception {
		serverUrl = "http://localhost:8080"; // TODO remove
		if(serverUrl == "")
			throw new ConnectException();

		GetRequest request = new GetRequest(serverUrl+"/game/"+gameName);

		request.run(); // Actually, we execute it sequentially
		
		Lobby.currentLobby = new Lobby(gameName);
		System.out.println(request.getResponse());
		JSONObject json = new JSONObject(request.getResponse());
		JSONArray arr = json.getJSONArray("players");
		for(int i = 0; i < arr.length(); i++) {
			JSONObject jsonPlayer = arr.getJSONObject(i);
			Player player = new Player();
			player.ip = (String) jsonPlayer.get("ip");
			player.name = (String) jsonPlayer.get("name");
			player.score = jsonPlayer.getInt("score");
			player.ready = (boolean) jsonPlayer.get("ready");
			Lobby.currentLobby.players.add(player);
		}
		GameFrame.getInstance().updateGame();
	}
	
	public synchronized boolean joinLobby(String lobbyName, String playerName) throws Exception { // WARNING Synchronized ?
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return false;
			}

		GetRequest request = new GetRequest(serverUrl+"/joinlobby/"+lobbyName+"/"+playerName);

		request.run(); // Actually, we execute it sequentially

		if(request.getResponse().equals("Created.") || request.getResponse().equals("Success.")) {
			updateLobby(lobbyName);
			return true;
			
		}
		return false;
	}
	
	public synchronized void leave(String lobbyName) {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return;
			}

		GetRequest request = new GetRequest(serverUrl+"/leave/"+lobbyName);

		request.run(); // Actually, we execute it sequentially
	}
	
	public void setReady() {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return;
			}

		GetRequest request = new GetRequest(serverUrl+"/ready/"+Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
	}
	
	public void fetchWords() {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return;
			}

		GetRequest request = new GetRequest(serverUrl+"/words/"+Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
		
		Lobby.currentLobbyWords = request.getResponse().split(",");
		System.out.println("Fetched words.");
		
		LobbyFrame.getInstance().enableReadyButton();
	}
	
	public int getCountDownRemaining() {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return 0;
			}

		GetRequest request = new GetRequest(serverUrl+"/game/"+Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
		
		JSONObject json = new JSONObject(request.getResponse());
		return json.getInt("remainingTime");
	}
	
	public int getGameDuration() {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return 0;
			}

		GetRequest request = new GetRequest(serverUrl+"/game/"+Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
		
		JSONObject json = new JSONObject(request.getResponse());
		return json.getInt("duration");
	}
	
	public void updateScore(int score) {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return;
			}

		GetRequest request = new GetRequest(serverUrl+"/updatescore/"+Lobby.currentLobby.name+"/"+score);

		request.run(); // Actually, we execute it sequentially
	}
	
	public boolean isGameFinished() {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return false;
			}

		GetRequest request = new GetRequest(serverUrl+"/game/"+Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
		
		JSONObject json = new JSONObject(request.getResponse());
		return -json.getInt("remainingTime") >= json.getInt("duration");
	}

	public boolean isGameCreated() {
		serverUrl = "http://localhost:8080"; // TODO remove

		if(serverUrl == "")
			try {
				throw new ConnectException();
			} catch (ConnectException e) {
				e.printStackTrace();
				disconnect();
				return false;
			}

		GetRequest request = new GetRequest(serverUrl+"/game/"+Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
		
		return !request.getResponse().equals("{}");
	}


}
