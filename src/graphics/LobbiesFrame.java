package graphics;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import concurrent.LobbiesRefresher;
import concurrent.LobbyJoiner;
import game.Lobby;
import listener.ButtonCreateLobbyListener;
import listener.LobbySelectListener;
import network.NetworkManager;

import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class LobbiesFrame extends JFrame {

	private class LobbiesTableModel extends AbstractTableModel {

		private final int columns = 2;
		private int rows;

		private Object[][] content;

		public LobbiesTableModel() {
			super();
			rows = 0;
			content = new Object[][] {};
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0)
				return "Lobby";
			return "Players";
		}

		public void update() { // WARNING Synchronize ?
			content = new Object[Lobby.lobbies.size()][2];

			rows = Lobby.lobbies.size();

			for (int i = 0; i < rows; i++) {
				Lobby tempLobby = Lobby.lobbies.poll();
				content[i][0] = tempLobby.name;
				content[i][1] = tempLobby.players.size();
			}
			
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
			return int.class;
		}

	}

	private static final LobbiesFrame instance = new LobbiesFrame();

	public static LobbiesFrame getInstance() {
		return instance;
	}

	private JPanel contentPane;
	private JTable table;
	private JTextField txtCreate;
	private JTextField txtName;

	private LobbiesRefresher lobbiesRefresher;
	private LobbyJoiner lobbyJoiner;

	private LobbiesTableModel model;
	private JScrollPane scrollPane;
	private JButton btnDisconnect;

	public LobbiesFrame() {
		model = new LobbiesTableModel();
	}

	public void initialize() {
		setTitle("Lobbies");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 357);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		txtCreate = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCreate, 287, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCreate, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtCreate, -5, SpringLayout.SOUTH, contentPane);
		contentPane.add(txtCreate);
		txtCreate.setColumns(10);

		JButton btnJoin = new JButton("Join");
		contentPane.add(btnJoin);

		JButton btnCreate = new JButton("Create");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnJoin, 10, SpringLayout.WEST, btnCreate);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnJoin, -21, SpringLayout.NORTH, btnCreate);
		btnCreate.addActionListener(new ButtonCreateLobbyListener());
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCreate, -3, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCreate, -31, SpringLayout.EAST, contentPane);
		contentPane.add(btnCreate);

		txtName = new JTextField();
		txtName.setText("Anonymous");
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtName, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtName, -277, SpringLayout.EAST, btnCreate);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtName, 0, SpringLayout.EAST, btnCreate);
		txtName.setColumns(10);
		contentPane.add(txtName);

		JLabel lblYourName = new JLabel("Your name:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblYourName, 3, SpringLayout.NORTH, txtName);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblYourName, -22, SpringLayout.WEST, txtName);
		contentPane.add(lblYourName);
		
		scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 24, SpringLayout.SOUTH, txtName);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -87, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnJoin, 6, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnJoin, 0, SpringLayout.EAST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table = new JTable(model);
		table.setDefaultRenderer(int.class, centerRenderer);
		table.setDefaultRenderer(String.class, centerRenderer);
		table.addMouseListener(new LobbySelectListener());
		scrollPane.setViewportView(table);
		sl_contentPane.putConstraint(SpringLayout.NORTH, table, 145, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, table, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, table, -88, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, table, -10, SpringLayout.EAST, contentPane);
		
		BorderLayout blayout = new BorderLayout();
		blayout.addLayoutComponent(table.getTableHeader(), BorderLayout.NORTH);
		blayout.addLayoutComponent(table, BorderLayout.SOUTH);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkManager.getInstance().disconnect();
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnDisconnect, 0, SpringLayout.NORTH, btnJoin);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnDisconnect, 0, SpringLayout.WEST, txtCreate);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnDisconnect, 0, SpringLayout.SOUTH, btnJoin);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnDisconnect, 19, SpringLayout.EAST, lblYourName);
		contentPane.add(btnDisconnect);
	}

	public void open() {
		if(isVisible())
			return;
		setVisible(true);

		lobbiesRefresher = new LobbiesRefresher();
		lobbiesRefresher.start();
	}

	public void close() {
		if(!isVisible())
			return;
		setVisible(false);

		lobbiesRefresher.requestStop();
	}

	public void updateLobbies() {
		model.update();
	}
	
	public void createLobby() {
		lobbyJoiner = new LobbyJoiner();
		lobbyJoiner.start(txtCreate.getText().replaceAll("[^0-9a-zA-Z_]", ""), txtName.getText());
	}
	
	public void joinExistingLobby(String lobbyName) {
		lobbyJoiner = new LobbyJoiner();
		lobbyJoiner.start(lobbyName, txtName.getText());
	}
	
	public JTable getTable() {
		return table;
	}
}
