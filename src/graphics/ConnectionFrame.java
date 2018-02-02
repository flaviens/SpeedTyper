package graphics;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import listener.ButtonConnectListener;

public class ConnectionFrame extends JFrame {
	private static final ConnectionFrame instance = new ConnectionFrame();
	
	public static ConnectionFrame getInstance() {
		return instance;
	}
	
	private JPanel contentPane;
	private JTextField textFieldIP;
	private JTextField textFieldPort;
	
	private ButtonConnectListener buttonConnectListener;
		
	public ConnectionFrame() {
	}
	
	public void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 216);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JLabel lblIp = new JLabel("Server IP: ");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblIp, -108, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblIp, -246, SpringLayout.EAST, contentPane);
		contentPane.add(lblIp);
		
		JLabel label = new JLabel("Port: ");
		contentPane.add(label);
		
		textFieldIP = new JTextField("127.0.0.1");
		sl_contentPane.putConstraint(SpringLayout.NORTH, textFieldIP, 32, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, textFieldIP, 30, SpringLayout.EAST, lblIp);
		sl_contentPane.putConstraint(SpringLayout.EAST, textFieldIP, -51, SpringLayout.EAST, contentPane);
		contentPane.add(textFieldIP);
		textFieldIP.setColumns(10);
		
		textFieldPort = new JTextField("8080");
		sl_contentPane.putConstraint(SpringLayout.NORTH, textFieldPort, 4, SpringLayout.SOUTH, textFieldIP);
		sl_contentPane.putConstraint(SpringLayout.NORTH, label, 3, SpringLayout.NORTH, textFieldPort);
		sl_contentPane.putConstraint(SpringLayout.EAST, label, -36, SpringLayout.WEST, textFieldPort);
		sl_contentPane.putConstraint(SpringLayout.WEST, textFieldPort, 0, SpringLayout.WEST, textFieldIP);
		sl_contentPane.putConstraint(SpringLayout.EAST, textFieldPort, -136, SpringLayout.EAST, contentPane);
		textFieldPort.setColumns(10);
		contentPane.add(textFieldPort);
		
		JButton btnConnect = new JButton("Connect");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnConnect, 30, SpringLayout.SOUTH, textFieldPort);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnConnect, 141, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnConnect, -24, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnConnect, -172, SpringLayout.EAST, contentPane);
		
		buttonConnectListener = new ButtonConnectListener();
		btnConnect.addActionListener(buttonConnectListener);
		getRootPane().setDefaultButton(btnConnect);
		
		contentPane.add(btnConnect);
	}
	
	public void open() {
		setVisible(true);
	}
	
	public void close() {
		setVisible(false);
	}
	
	public String getIp() {
		return textFieldIP.getText();
	}
	
	public String getPort() {
		return textFieldPort.getText();
	}
}
