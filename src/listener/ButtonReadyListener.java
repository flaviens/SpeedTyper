package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import network.NetworkManager;

public class ButtonReadyListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread readyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				NetworkManager.getInstance().setReady();
			}
		});
		readyThread.start();
	}

}
