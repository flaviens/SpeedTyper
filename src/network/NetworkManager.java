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
	
	public String[] getLobbies() throws ConnectException {
		if(serverUrl == "")
			throw new ConnectException();

		GetRequest request = new GetRequest(serverUrl+"lobbies");

		request.run(); // Actually, we execute it sequentially
				
		return request.getResponse().split(","); // TODO Decode JSON instead of csv
	}
	
}
