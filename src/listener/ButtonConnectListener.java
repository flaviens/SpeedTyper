package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;import java.nio.channels.NetworkChannel;

import graphics.ConnectionFrame;
import network.NetworkManager;

public class ButtonConnectListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(NetworkManager.getInstance().connect(ConnectionFrame.getInstance().getIp(), ConnectionFrame.getInstance().getPort()))
			System.out.println("Connection successful !");
		else
			System.out.println("Connection failed...");
		
	}

}
