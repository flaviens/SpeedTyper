package graphics;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import concurrent.GameStartedChecker;
import concurrent.LobbiesRefresher;
import concurrent.LobbyJoiner;
import concurrent.LobbyRefresher;
import concurrent.StillInLobbyManager;
import game.Lobby;
import game.Player;
import listener.ButtonBackListener;
import listener.ButtonReadyListener;

public class LobbyFrame extends JFrame {

	private class LobbyTableModel extends AbstractTableModel {

		private final int columns = 2;
		private int rows;

		private Object[][] content;

		public LobbyTableModel() {
			super();
			rows = 0;
			content = new Object[][] {};
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0)
				return "Player";
			return "Ready";
		}

		public void update() { // WARNING Synchronize ?
			content = new Object[Lobby.currentLobby.players.size()][2];

			rows = Lobby.currentLobby.players.size();

			for (int i = 0; i < rows; i++) {
				Player tempPlayer = Lobby.currentLobby.players.get(i);
				content[i][0] = tempPlayer.name;
				content[i][1] = tempPlayer.ready;
			}
			
			setTitle(Lobby.currentLobby.name);
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return columns;
		}

		@Override
		public int getRowCount() {
			return rows;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return content[arg0][arg1];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0)
				return String.class;
			return boolean.class;
		}

	}
	
	private static final LobbyFrame instance = new LobbyFrame();
	
	public static LobbyFrame getInstance() {
		return instance;
	}
	
	private LobbyRefresher lobbyRefresher;
	private GameStartedChecker gameStartedChecker;
	private StillInLobbyManager stillInLobbyManager;
	
	private JPanel contentPane;
	
	private JTable table;

	private LobbyTableModel model;
	private JScrollPane scrollPane;
	private JButton btnBack;
	private JButton btnReady;
	
	public LobbyFrame() {
		model = new LobbyTableModel();
	}
	
	public void initialize() {
		setTitle("Lobby: "+Lobby.currentLobby.name);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		btnReady = new JButton("Ready");
		btnReady.setEnabled(false);
		btnReady.addActionListener(new ButtonReadyListener());
		sl_contentPane.putConstraint(SpringLayout.WEST, btnReady, 172, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnReady, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnReady, -168, SpringLayout.EAST, contentPane);
		contentPane.add(btnReady);
		
		scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnReady, 17, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -64, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table = new JTable(model);
		table.setDefaultRenderer(boolean.class, centerRenderer);
		table.setDefaultRenderer(String.class, centerRenderer);
		scrollPane.setViewportView(table);
		sl_contentPane.putConstraint(SpringLayout.NORTH, table, 145, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, table, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, table, -88, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, table, -10, SpringLayout.EAST, contentPane);
		
		BorderLayout blayout = new BorderLayout();
		blayout.addLayoutComponent(table.getTableHeader(), BorderLayout.NORTH);
		blayout.addLayoutComponent(table, BorderLayout.SOUTH);
		
		btnBack = new JButton("Back");
		btnBack.addActionListener(new ButtonBackListener());
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnBack, 0, SpringLayout.NORTH, btnReady);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnBack, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnBack, 0, SpringLayout.SOUTH, btnReady);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnBack, -63, SpringLayout.WEST, btnReady);
		contentPane.add(btnBack);	
	}
	
	public void open() {
		if(isVisible())
			return;
		
		setVisible(true);

		lobbyRefresher = new LobbyRefresher();
		gameStartedChecker = new GameStartedChecker();
		stillInLobbyManager = new StillInLobbyManager();
		lobbyRefresher.start();
		gameStartedChecker.start();
		stillInLobbyManager.start();
	}

	public void close() {
		if(!isVisible())
			return;
		setVisible(false);
		
		lobbyRefresher.requestStop();
		gameStartedChecker.requestStop();
		stillInLobbyManager.requestStop();
	}

	public void updateLobby() {
		model.update();
	}
	
	public JTable getTable() {
		return table;
	}
	
	public void enableReadyButton() {
		btnReady.setEnabled(true);
	}

}
