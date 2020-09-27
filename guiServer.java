
/**
 *This class is responsible for the implementation of the client Gui
 */
import javax.swing.*;
import java.awt.event.*;

public class guiServer {

	/** GUI JFrame */
	private JFrame frame;
	/** GUI text area for displaying client messages */
	private JTextArea textArea;
	/** GUI text field to input port number */
	private JTextField textField;
	/** Connection of the GUI to the server */
	private boolean connected;
	/** ChatServer object to be initialized */
	private ChatServer serve;
	/** GUI object */
	private guiServer gui = this;

	/**
	 * Ensures that the GUI is visible
	 */
	public void apply() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Create the application. Must pass the server in initialize as the GUI is just
	 * an image and the functionality comes from the server
	 */
	public guiServer(int port) {
		/*Set the connection to false*/
		connected = false;
		initialize(port);
		apply();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(int port) {
		frame = new JFrame();
		frame.setBounds(100, 100, 321, 448);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);


		/* Set up the Kill button */
		JButton btnKill = new JButton("kill");
		btnKill.setBounds(0, 397, 321, 29);
		btnKill.addActionListener(new ActionListener() {
			/*
			 * Adding an action that shuts down the server when the kill button is pressed
			 */
			public void actionPerformed(ActionEvent e) {
				if (connected) {
					serve.closeSystem();
				}
			}
		});
		frame.getContentPane().add(btnKill);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 104, 317, 273);
		frame.getContentPane().add(scrollPane);

		/* Text area for the server */
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JLabel lblServer = new JLabel("Server");
		lblServer.setBounds(142, 6, 50, 22);
		frame.getContentPane().add(lblServer);
		/*
		 * creating a text field that displays the default port which is editable for
		 * the user to start the server
		 */
		textField = new JTextField();
		textField.setBounds(6, 30, 130, 26);
		textField.setText(Integer.toString(port));
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Port number");
		lblNewLabel.setBounds(27, 9, 89, 16);
		frame.getContentPane().add(lblNewLabel);

		/* Connect button that attempts to connect to the server when it is pressed */
		JButton btnNewButton = new JButton("connect");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/* Attempt to launch the server if the port text field isn't empty */
				if (!textField.getText().equals("")) {
					int portnum;
					try {
						portnum = Integer.parseInt(textField.getText());
					}

					catch (NumberFormatException e1) {
						/*
						 * If the user enters anything other than a port number set the port to the
						 * default one
						 */
						display("You have to enter a number logging on the default port");
						textField.setText(Integer.toString(port));
						portnum = port;
					}
					ServerThread(portnum);
					connected = true;
					textField.setEditable(false);
					btnNewButton.setEnabled(false);
				}

			}
		});
		btnNewButton.setBounds(204, 30, 117, 29);
		frame.getContentPane().add(btnNewButton);
	}

	/**
	 * Displays text on the GUI
	 * 
	 * @param line
	 *            The message sent by other clients
	 */
	public void display(String line) {
		textArea.append(line + "\n");
	}
	
	
	/**Create a thread just for instantiating the server and running the go method*/
	public void ServerThread(int port) {
		Thread th = new Thread(new Runnable() {
			public void run() {
				serve = new ChatServer(port);
				serve.go(gui);
			}
		});
		th.start();
	}
}
