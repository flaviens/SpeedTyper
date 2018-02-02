package graphics;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.PriorityQueue;

import javax.security.auth.login.CredentialNotFoundException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import concurrent.CountDownManager;
import concurrent.GameUpdater;
import concurrent.ScoreUpdater;
import game.Lobby;
import game.Player;
import javax.swing.SpringLayout;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class GameFrame extends JFrame {

	private class GameTableModel extends AbstractTableModel {

		private final int columns = 2;
		private int rows;

		private Object[][] content;

		public GameTableModel() {
			super();
			rows = 0;
			content = new Object[][] {};
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0)
				return "Player";
			return "Score";
		}

		public void update() { // WARNING Synchronize ?
			content = new Object[Lobby.currentLobby.players.size()][2];

			PriorityQueue<Player> priorityPlayers = new PriorityQueue<Player>(new Player.PlayerComparator());
			
			rows = Lobby.currentLobby.players.size();

			for (int i = 0; i < rows; i++) {
				priorityPlayers.add(Lobby.currentLobby.players.get(i));
			}
			for (int i = 0; i < rows; i++) {
				Player tempPlayer = priorityPlayers.poll();
				content[i][0] = tempPlayer.name;
				content[i][1] = tempPlayer.score;
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
	
	private static final GameFrame instance = new GameFrame();
	
	public static GameFrame getInstance() {
		return instance;
	}
	
	private GameTableModel model;
	
	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	
	private CountDownManager countDownManager;
	private ScoreUpdater scoreUpdater;
	private GameUpdater gameUpdater;
	
	private JLabel lblCountdown;
	private JLabel lblNextword;
	
	private int score;
	
	private String currentWord;
	private int currentWordIndex;
	
	private final int nbWordsIncomingMax = 30;
	private final int nbWordsWrittenMax = 20;
	
	private JTextPane textPaneIncoming;
	private JTextPane textPane_1;
	
	private Robot robot;

	public GameFrame() {		
		model = new GameTableModel();
	}
	
	public void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		textPaneIncoming = new JTextPane();
		textPaneIncoming.setEnabled(true); // TODO false
		sl_contentPane.putConstraint(SpringLayout.NORTH, textPaneIncoming, 52, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, textPaneIncoming, 15, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textPaneIncoming, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, textPaneIncoming, -572, SpringLayout.EAST, contentPane);
		contentPane.add(textPaneIncoming);
		
		JLabel lblIncomingWords = new JLabel("Incoming words");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblIncomingWords, 22, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblIncomingWords, 59, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblIncomingWords, -6, SpringLayout.NORTH, textPaneIncoming);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblIncomingWords, 179, SpringLayout.WEST, contentPane);
		contentPane.add(lblIncomingWords);
		
		JLabel lblNowWrite = new JLabel("Now write:");
		contentPane.add(lblNowWrite);
		
		
		lblNextword = new JLabel("next_word");
		Font f = lblNextword.getFont();
		lblNextword.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNextword, 47, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNowWrite, 0, SpringLayout.NORTH, lblNextword);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNowWrite, -6, SpringLayout.WEST, lblNextword);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNextword, 115, SpringLayout.EAST, textPaneIncoming);
		contentPane.add(lblNextword);
		
		textField = new JTextField();
		textField.setEnabled(false);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkWordEntered();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				checkWordEntered();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				checkWordEntered();
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField, 20, SpringLayout.SOUTH, lblNextword);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField, -373, SpringLayout.EAST, contentPane);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textPane_1 = new JTextPane();
		textPane_1.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField, 0, SpringLayout.WEST, textPane_1);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textField, -27, SpringLayout.NORTH, textPane_1);
		sl_contentPane.putConstraint(SpringLayout.NORTH, textPane_1, -289, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, textPane_1, 26, SpringLayout.EAST, textPaneIncoming);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textPane_1, 0, SpringLayout.SOUTH, textPaneIncoming);
		sl_contentPane.putConstraint(SpringLayout.EAST, textPane_1, -373, SpringLayout.EAST, contentPane);
		contentPane.add(textPane_1);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, textPane_1);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 32, SpringLayout.EAST, textPane_1);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, textPaneIncoming);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(model);
		scrollPane.setViewportView(table);
		
		JLabel lblGameStartsIn = new JLabel("Game starts in :");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblGameStartsIn, 4, SpringLayout.NORTH, lblIncomingWords);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblGameStartsIn, -138, SpringLayout.EAST, contentPane);
		contentPane.add(lblGameStartsIn);
		
		lblCountdown = new JLabel("countdown");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCountdown, 0, SpringLayout.NORTH, lblNowWrite);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCountdown, 10, SpringLayout.WEST, lblGameStartsIn);
		contentPane.add(lblCountdown);	}
	
	public void open() {
		setVisible(true);
		
		countDownManager = new CountDownManager();
		countDownManager.start();
		
		// TODO Move
		gameUpdater = new GameUpdater();
		scoreUpdater = new ScoreUpdater();
		scoreUpdater.start();
		gameUpdater.start();
				
		currentWordIndex = 0;
		System.out.println(Lobby.currentLobbyWords[5]);
		currentWord = Lobby.currentLobbyWords[0];

		refreshWordTables();
	}
	
	public void refreshWordTables() {
		String incomingText = "";
		for(int i = currentWordIndex+1; i < currentWordIndex+nbWordsIncomingMax; i++) {
			incomingText += Lobby.currentLobbyWords[i]+"\n";
		}
		textPaneIncoming.setText(incomingText);
		
		String writtenText = "";
		for(int i = currentWordIndex-1; i >= Math.max(0, currentWordIndex-nbWordsWrittenMax); i--) {
			writtenText += Lobby.currentLobbyWords[i]+"\n";
		}
		textPane_1.setText(writtenText);
		
		lblNextword.setText(currentWord);
		
		// TODO repaint ?
	}
	
	public void close() {
		setVisible(false);
	}
	
	public void updateCountDown(int remaining) {
		if(remaining <= 0) {
			lblCountdown.setText("Go !");
			countDownManager.requestStop();
			textField.setEnabled(true);
			textField.requestFocusInWindow();
			
			score = 0;
		}
		else
			lblCountdown.setText(String.valueOf(remaining/1000));
	}
	
	public void finish() {
		textField.setEnabled(false);
		scoreUpdater.requestStop();
		lblCountdown.setText("Finished !");
		// On laisse le gameupdater tourner
	}
	
	public int getScore() {
		return score;
	}
	
	public void updateGame() {
		model.update();
	}
	
	public void checkWordEntered() {
		if(currentWord.equals(textField.getText())) {
			score += currentWord.length();
			try {
				robot = new Robot();
				for(int i = 0; i < 100; i++)
					robot.keyPress(KeyEvent.VK_BACK_SPACE);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			currentWordIndex++;
			currentWord = Lobby.currentLobbyWords[currentWordIndex];
			refreshWordTables();
			
			
		}
	}

}
