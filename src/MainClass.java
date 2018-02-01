import java.io.IOException;

import network.NetworkManager;

public class MainClass {
	public static void main(String[] args) throws IOException {
		if(!NetworkManager.getInstance().connect("http://localhost:8080/")) {
			System.out.println("Connection failed !");
			return;
		}
		
		String[] lobbies = NetworkManager.getInstance().getLobbies();
		
		for(String lobby: lobbies)
			System.out.println(lobby);
	}

}