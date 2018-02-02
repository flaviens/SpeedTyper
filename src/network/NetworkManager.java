package network;

import java.net.ConnectException;

import org.json.JSONArray;
import org.json.JSONException;
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

	public static NetworkManager getInstance() {
		return instance;
	}

	public boolean isConnected() {
		return serverUrl.length() > 0;
	}

	/**
	 * @param ip
	 *            remote ip.
	 * @param port
	 *            remote port.
	 * @return true iif connection successful, and stores ip and port in this case.
	 */
	public boolean connect(String ip, String port) {

		String tempUrl = "http://" + ip + ":" + port;

		GetRequest request = new GetRequest(tempUrl);
		request.run();

		if (request.getResponse().equals("Ping")) {
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

	public synchronized void updateLobbies() {
		if(serverUrl == "")
			disconnect();

		GetRequest request = new GetRequest(serverUrl+"/lobbies");
	
		try {
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
		} catch(JSONException e) {
			//System.out.println("Response: "+request.getResponse());
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void updateLobby(String lobbyName) {
		if (serverUrl == "")
			disconnect();

		GetRequest request = new GetRequest(serverUrl + "/lobbies");

		request.run(); // Actually, we execute it sequentially

		Lobby.currentLobby = new Lobby(lobbyName);

		JSONObject json = new JSONObject(request.getResponse());
		for (String str : json.keySet()) {
			if (!str.equals(lobbyName))
				continue;
			Lobby.currentLobby = new Lobby(str);
			JSONArray arr = json.getJSONArray(str);
			for (int i = 0; i < arr.length(); i++) {
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

	public synchronized void updateGamePlayers(String gameName) {
		if (serverUrl == "")
			disconnect();
		
		GetRequest request = new GetRequest(serverUrl + "/game/" + gameName);

		try {
			request.run(); // Actually, we execute it sequentially
	
			Lobby.currentLobby = new Lobby(gameName);
			JSONObject json = new JSONObject(request.getResponse());
			JSONArray arr = json.getJSONArray("players");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject jsonPlayer = arr.getJSONObject(i);
				Player player = new Player();
				player.ip = (String) jsonPlayer.get("ip");
				player.name = (String) jsonPlayer.get("name");
				player.score = jsonPlayer.getInt("score");
				player.ready = (boolean) jsonPlayer.get("ready");
				Lobby.currentLobby.players.add(player);
			}
			GameFrame.getInstance().updateGame();
		} catch(JSONException e) {
			//System.out.println("Response: "+request.getResponse());
			e.printStackTrace();
		}
	}

	public synchronized boolean joinLobby(String lobbyName, String playerName) {
		if (serverUrl == "") {
			disconnect();
			return false;
		}

		GetRequest request = new GetRequest(serverUrl + "/joinlobby/" + lobbyName + "/" + playerName);

		request.run(); // Actually, we execute it sequentially

		if (request.getResponse().equals("Created.") || request.getResponse().equals("Joined.")) {
			updateLobby(lobbyName);
			return true;

		}
		return false;
	}

	public synchronized void leave() {
		if (serverUrl == "") {
			disconnect();
			return;
		}
		GetRequest request = new GetRequest(serverUrl + "/leave/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
	}

	public synchronized void setReady() {
		if (serverUrl == "") {
			disconnect();
			return;
		}

		GetRequest request = new GetRequest(serverUrl + "/ready/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
	}

	public synchronized void fetchWords() {
		if (serverUrl == "") {
			disconnect();
			return;
		}

		GetRequest request = new GetRequest(serverUrl + "/words/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially

		Lobby.currentLobbyWords = request.getResponse().split(",");

		LobbyFrame.getInstance().enableReadyButton();
	}

	public synchronized int getCountDownRemaining() {
		if (serverUrl == "") {
			disconnect();
			return 0;
		}

		GetRequest request = new GetRequest(serverUrl + "/game/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially

		JSONObject json = new JSONObject(request.getResponse());
		return json.getInt("remainingTime");
	}

	public synchronized int getGameDuration() {
		if (serverUrl == "") {
			disconnect();
			return 0;
		}

		GetRequest request = new GetRequest(serverUrl + "/game/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially

		JSONObject json = new JSONObject(request.getResponse());
		return json.getInt("duration");
	}

	public synchronized void updateScore(int score) {
		if (serverUrl == "") {
			disconnect();
			return;
		}

		GetRequest request = new GetRequest(serverUrl + "/updatescore/" + Lobby.currentLobby.name + "/" + score);

		request.run(); // Actually, we execute it sequentially
	}

	public synchronized boolean isGameFinished() {

		if (serverUrl == "") {
			disconnect();
			return true;
		}
		GetRequest request = new GetRequest(serverUrl + "/game/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially

		JSONObject json = new JSONObject(request.getResponse());
		return -json.getInt("remainingTime") >= json.getInt("duration");
	}

	public synchronized boolean isGameCreated() {
		if (serverUrl == "") {
			disconnect();
			return false;
		}

		GetRequest request = new GetRequest(serverUrl + "/game/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially

		return !request.getResponse().equals("{}");
	}

	public synchronized void stillInLobby() {
		if (serverUrl == "") {
			disconnect();
			return;
		}

		GetRequest request = new GetRequest(serverUrl + "/stillinlobby/" + Lobby.currentLobby.name);

		request.run(); // Actually, we execute it sequentially
	}

}
