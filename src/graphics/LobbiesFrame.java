package graphics;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import concurrent.LobbiesRefresher;
import game.Lobby;

import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class LobbiesFrame extends JFrame {

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

	private LobbiesRefresher lobbiesRefresher;
	private JTextField txtName;

	private LobbyTableModel model;
	private JScrollPane scrollPane;

	public LobbiesFrame() {
		model = new LobbyTableModel();
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
		sl_contentPane.putConstraint(SpringLayout.WEST, btnJoin, 171, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnJoin, -12, SpringLayout.NORTH, txtCreate);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnJoin, -169, SpringLayout.EAST, contentPane);
		contentPane.add(btnJoin);

		JButton btnCreate = new JButton("Create");
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
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -16, SpringLayout.NORTH, btnJoin);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		table = new JTable(model);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnJoin, 17, SpringLayout.SOUTH, table);
		scrollPane.setViewportView(table);
		sl_contentPane.putConstraint(SpringLayout.NORTH, table, 145, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, table, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, table, -88, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, table, -10, SpringLayout.EAST, contentPane);
		
		BorderLayout blayout = new BorderLayout();
		blayout.addLayoutComponent(table.getTableHeader(), BorderLayout.NORTH);
		blayout.addLayoutComponent(table, BorderLayout.SOUTH);

		lobbiesRefresher = new LobbiesRefresher();
	}

	public void open() {
		setVisible(true);

		lobbiesRefresher.start();
	}

	public void close() {
		setVisible(false);

		lobbiesRefresher.requestStop();
	}

	public void updateLobbies() {
		model.update();
	}
}
