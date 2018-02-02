import java.io.IOException;

import graphics.ConnectionFrame;
import graphics.GameFrame;
import graphics.LobbiesFrame;
import graphics.LobbyFrame;
import network.NetworkManager;

public class MainClass {
		
	public static void main(String[] args) throws IOException {
		
		ConnectionFrame.getInstance().initialize();
		LobbiesFrame.getInstance().initialize();
		LobbyFrame.getInstance().initialize();
		GameFrame.getInstance().initialize();
		
		ConnectionFrame.getInstance().open();
	}

}