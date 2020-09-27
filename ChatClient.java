
/**
 * This class runs the client program and
 * contains implementation of abstract methods
 */

import java.io.*;

public class ChatClient extends AbstractClient {

	/** BufferedReader to read input */
	private BufferedReader in;

	/* Constructor */
	ChatClient(String IP, int port, String userName) {
		connect(IP, port, userName);

	}

	@Override
	protected void display(String line) {
		System.out.println(line);
	}

	@Override
	protected String getText() {
		// Message to be sent to the server
		String msg;
		in = new BufferedReader(new InputStreamReader(System.in));
		try {
			msg = in.readLine();
		} catch (IOException e) {
			msg = null;
		}
		return msg;

	}

	@Override
	protected void sendMessage() {
		/*
		 * A thread is created as a client can receive messages as they send them
		 */
		display("Type something");
		Thread sendMessage = new Thread(new Runnable() {

			@Override
			public void run() {
				PrintWriter send;
				String msg;
				try {
					while (true) {
						msg = getText();
						send = new PrintWriter(ClientSocket.getOutputStream(), true);

						// As long as the message isn't empty send the message to the server
						if (msg != null && msg.length() > 0) {
							send.println(userName + ": " + msg);
							msg = null;
						}
					}
				}

				/*
				 * This catches the I/O exception and tries to close the Client socket. If the
				 * client socket can't close properly the client program will terminate.
				 */
				catch (IOException e) {
					System.err.println("Something went wrong when sending the message");
				} finally {
					try {
						ClientSocket.close();
					} catch (IOException e) {
						System.err.println("Error closing the client socket");
					} finally {
						System.exit(0);
					}
				}
			}
		});
		sendMessage.start();
	}

	/*
	 * Main method to run ChatClient
	 */
	public static void main(String[] args) {
		/* Default IP address */
		String IP = "localhost";
		/* Default port */
		int port = 14001;
		System.out.println("Please enter a username as well as if you want to use a gui");
		/* user's user name */
		String user;
		/* user's response */
		String response;

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			user = reader.readLine();
			response = reader.readLine();
		} catch (IOException e) {
			user = "Default";
			response = "no";
		}

		// Loop through to see if "-ccp" or "-cca" are present.
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-cca")) {
				// set next argument to the IP address
				IP = args[i + 1];
			}

			if (args[i].equals("-ccp")) {
				// Try to set the port. if it isn't a number print out an error message
				try {
					port = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					System.err.println("this isn't a number/invalid input setting up default port");
				}
			}
		}

		/*
		 * Depending on user response launch the GUI or the command line client
		 */

		if (response.equals("no")) {
			new ChatClient(IP, port, user).go();
		}

		else {
			new clientGui(IP, port, user);
		}
	}
}
