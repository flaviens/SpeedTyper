package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;import java.nio.channels.NetworkChannel;

import concurrent.LobbyJoiner;
import graphics.ConnectionFrame;
import graphics.LobbiesFrame;
import graphics.LobbyFrame;
import network.NetworkManager;

public class ButtonCreateLobbyListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		LobbiesFrame.getInstance().createLobby();
	}

}
