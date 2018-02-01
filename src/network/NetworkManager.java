package network;

import java.net.ConnectException;

public class NetworkManager {
	public static final NetworkManager instance = new NetworkManager();
	
	private String serverUrl = "";
	
	public static NetworkManager getInstance() {
		return instance;
	}
	
	public boolean isConnected() {
		return serverUrl.length() > 0;
	}
	
	public boolean connect(String serverUrl) {
		
		GetRequest request = new GetRequest(serverUrl);
		request.start();
		
		 // TODO attendre un signal de fin
		
		try {
			request.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		
		if(request.getResponse().equals("Bienvenue")) {
			this.serverUrl = serverUrl;
			return true;
		}
		this.serverUrl = "";
		return false;
	}
	
	public String[] getLobbies() throws ConnectException {
		if(serverUrl == "")
			throw new ConnectException();

		GetRequest request = new GetRequest(serverUrl+"lobbies");

		request.start();
		
		// TODO attendre le signal
		
		try {
			request.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		
		return request.getResponse().split(",");
	}
	
}
