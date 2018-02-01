import java.io.IOException;

import graphics.ConnectionFrame;
import graphics.LobbiesFrame;
import network.NetworkManager;

public class MainClass {
		
	public static void main(String[] args) throws IOException {
		/*if(!NetworkManager.getInstance().connect("http://localhost:8080/")) {
			System.out.println("Connection failed !");
			return;
		}*/
		
		ConnectionFrame.getInstance().initialize();
		LobbiesFrame.getInstance().initialize();
		
		LobbiesFrame.getInstance().open();
	}

}