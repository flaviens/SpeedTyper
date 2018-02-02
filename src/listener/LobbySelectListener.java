package listener;

import java.awt.event.MouseListener;

import graphics.LobbiesFrame;

import java.awt.event.MouseAdapter;

public class LobbySelectListener extends MouseAdapter{
	
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = LobbiesFrame.getInstance().getTable().rowAtPoint(evt.getPoint());
        int col = LobbiesFrame.getInstance().getTable().columnAtPoint(evt.getPoint());
        if (row >= 0 && col >= 0) {
            LobbiesFrame.getInstance().joinExistingLobby((String)LobbiesFrame.getInstance().getTable().getModel().getValueAt(row, 0));
        }
    }
}
