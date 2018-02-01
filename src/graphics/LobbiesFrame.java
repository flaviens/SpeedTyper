package graphics;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import concurrent.LobbiesRefresher;

import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LobbiesFrame extends JFrame {

	public static final LobbiesFrame instance = new LobbiesFrame();
	
	public static LobbiesFrame getInstance() {
		return instance;
	}
	
	private JPanel contentPane;
	private JTable table;
	private JTextField textField;
	
	private LobbiesRefresher lobbiesRefresher;

	/**
	 * Create the frame.
	 */
	public LobbiesFrame() {
		
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
		
		table = new JTable();
		sl_contentPane.putConstraint(SpringLayout.NORTH, table, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, table, -88, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, table, 412, SpringLayout.WEST, contentPane);
		contentPane.add(table);
		
		textField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, table, 0, SpringLayout.WEST, textField);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textField, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField, 279, SpringLayout.WEST, contentPane);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnJoin = new JButton("Join");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnJoin, 6, SpringLayout.SOUTH, table);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnJoin, 175, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnJoin, -18, SpringLayout.NORTH, textField);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnJoin, -163, SpringLayout.EAST, contentPane);
		contentPane.add(btnJoin);
		
		JButton btnCreate = new JButton("Create");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCreate, -3, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCreate, -31, SpringLayout.EAST, contentPane);
		contentPane.add(btnCreate);
		
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
}
