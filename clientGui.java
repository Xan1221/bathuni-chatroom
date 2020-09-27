
/**
 * This class is responsible for the implementation of the client GUI
 */

import javax.swing.*;
import java.io.*;
import java.awt.EventQueue;
import java.awt.event.*;

public class clientGui extends AbstractClient {

	/** The GUI frame */
	private JFrame frame;
	/** The receive message text area */
	private JTextArea textArea;
	/** Send text area */
	private JTextArea textArea_1;
	/** field for the IP address */
	private JTextField textField;
	/** field for the port number */
	private JTextField textField_1;
	/** A boolean that marks if the GUI has attempted to be connected */
	private boolean connected;

	/**
	 * Method that makes the GUI program visible
	 */
	public void runGui() {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Constructor
	 * 
	 * @param IP
	 *            Client IP address
	 * @param port
	 *            Client port
	 * @param name
	 *            Client user name
	 */
	public clientGui(String IP, int port, String name) {
		/* in order to enable the user to login set connected to false */
		connected = false;
		initialize(IP, port, name);
		runGui();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String IP, int port, String name) {
		/*
		 * Creating a new elements for the GUI and formatting them. Formatting was done
		 * on window builder
		 */

		/* Setting up JFrame */
		frame = new JFrame();
		frame.setBounds(100, 100, 321, 448);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		/* Setting up JPanel */
		JPanel panel = new JPanel();
		panel.setBounds(-6, -12, 327, 438);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		/* Setting up the message label */
		JLabel lblMessages = new JLabel("Messages");
		lblMessages.setBounds(134, 108, 61, 16);
		panel.add(lblMessages);

		/* Adding a scroll pane such that the message area can be scrolled */
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(34, 133, 258, 156);
		panel.add(scrollPane);

		/*Setting up the message text area*/
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		/* setting up the Send button */
		JButton btnNewButton = new JButton("Send");
		btnNewButton.setBounds(34, 378, 262, 54);
		/*
		 * Create an action listener for the button. when the button is pressed send the
		 * message to the server only if the client is connected
		 */
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (connected) {
					sendMessage();
					return;
				}
			}
		});
		panel.add(btnNewButton);

		/*The area in which the client writes into to send a message*/
		textArea_1 = new JTextArea();
		textArea_1.setBounds(34, 301, 262, 68);
		panel.add(textArea_1);

		/* The logout button */
		JButton btnNewButton_1 = new JButton("Logout");
		/* enable the client to logout only if the client is connected to the server */
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (connected) {
					sendLogoutMessage();
				}
			}
		});
		btnNewButton_1.setBounds(229, 21, 92, 17);
		panel.add(btnNewButton_1);

		/* set up the text field that stores the IP address of the client */
		textField = new JTextField();
		textField.setBounds(22, 80, 121, 26);
		panel.add(textField);
		textField.setColumns(10);
		textField.setText(IP);

		JLabel lblIpAddress = new JLabel("IP address");
		lblIpAddress.setBounds(50, 63, 77, 16);
		panel.add(lblIpAddress);

		JLabel lblPortNumber = new JLabel("port number");
		lblPortNumber.setBounds(179, 63, 96, 16);
		panel.add(lblPortNumber);

		/* Text field that stores the port number of the client */
		textField_1 = new JTextField();
		textField_1.setBounds(166, 80, 113, 26);
		panel.add(textField_1);
		textField_1.setColumns(10);
		textField_1.setText(Integer.toString(port));

		/* setting up the connect button */
		JButton btnConenct = new JButton("conenct");
		btnConenct.addActionListener(new ActionListener() {
			/* when the button is pressed attempt to connect the client to the server */
			public void actionPerformed(ActionEvent e) {

				/* If the text fields aren't empty attempt connection */
				if (!textField.getText().equals("") && !textField_1.getText().equals("")) {
					String address = textField.getText();
					int portnum;
					try {
						portnum = Integer.parseInt(textField_1.getText());
					} catch (NumberFormatException e1) {
						/*
						 * If the user enters anything other than a port number set the port to the
						 * default one
						 */
						display("this isn't a number entering default port number");
						textField_1.setText(Integer.toString(port));
						portnum = port;
					}

					connect(address, portnum, name);
					textField.setEditable(false);
					textField_1.setEditable(false);
					connected = true;
					/*
					 * set the connect button's editable to false so the user can't cause an error
					 * by connecting to multiple servers
					 */
					btnConenct.setEnabled(false);
					receiveMessage();
				}
			}
		});
		btnConenct.setBounds(6, 16, 111, 26);
		panel.add(btnConenct);
	}

	@Override
	protected void display(String line) {
		line = line + "\n";
		textArea.append(line);
	}

	@Override
	protected String getText() {
		String msg = textArea_1.getText();
		textArea_1.setText("");
		return msg;
	}

	/**
	 * This method sends a string to the server which in turn shuts down the client
	 */
	protected void sendLogoutMessage() {
		Thread logout = new Thread(new Runnable() {
			@Override

			public void run() {
				PrintWriter send;
				String msg = "/logout";
				try {
					send = new PrintWriter(ClientSocket.getOutputStream(), true);
					send.println(userName + ": " + msg);
				}

				/*
				 * This catches the I/O exception and tries to close the Client socket. If the
				 * client socket can't close properly the client program will terminate.
				 */
				catch (IOException e) {
					System.err.println("Something went wrong when sending the message");
					try {
						ClientSocket.close();
					} catch (IOException e1) {
						System.err.println("Error closing the client socket");
					} finally {
						System.exit(0);
					}
				}

			}
		});
		logout.start();
	}

	@Override
	protected void sendMessage() {
		Thread sendMessage = new Thread(new Runnable() {
			@Override

			public void run() {
				PrintWriter send;
				String msg;
				/*Here there isn't a while loop because the button doesn't buffer like the buffered reader*/
				try {
					msg = getText();
					send = new PrintWriter(ClientSocket.getOutputStream(), true);
					if (msg != null && msg.length() > 0) {
						send.println(userName + ": " + msg);
						msg = null;
					}
				}

				/*
				 * This catches the I/O exception and tries to close the Client socket. If the
				 * client socket can't close properly the client program will terminate.
				 */
				catch (IOException e) {
					System.err.println("Something went wrong when sending the message");
					try {
						ClientSocket.close();
					} catch (IOException e1) {
						System.err.println("Error closing the client socket");
					} finally {
						System.exit(0);
					}
				}
			}

		});
		sendMessage.start();
	}
}
