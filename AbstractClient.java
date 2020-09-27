
/**
 *This class includes functionality for the client
 *so it can be run in a console and in a GUI
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class AbstractClient {
	/** Client's socket */
	protected Socket ClientSocket;
	/** Client's user name */
	protected String userName;
	/* Kill word that quits the client program */
	protected String killword = "you have logged out";

	/**
	 * Method attempts to connect the client with he socket by instantiating a
	 * socket object
	 * 
	 * @param IP
	 *            String The client's IP address
	 * @param port
	 *            Integer The client' port
	 * @param userName
	 *            String The client's user name
	 */
	protected void connect(String IP, int port, String userName) {
		try {
			this.ClientSocket = new Socket(IP, port);
			this.userName = userName;
		} catch (UnknownHostException e) {
			/* Close the system if there is an exception with the IP address */
			System.err.println("Couldnt connect because IP address can't be determined");
			System.exit(0);
		} catch (IOException e) {
			/*
			 * If the server isn't online notify the user then close the p rogram
			 */
			System.out.println("The server isn't online please come back later");
			System.exit(0);
		} 
		
		/*This deals with port numbers too big or too small for example -1*/
		catch (IllegalArgumentException e) {
			System.err.println("Illeagal argument passed in program shutting down");
			System.exit(0);
		}
	}

	/**
	 * Method displays text
	 * 
	 * @param line
	 *            String Message to be displayed
	 * 
	 */
	protected abstract void display(String line);

	/** Send message and receive message */
	protected void go() {
		sendMessage();
		receiveMessage();
	}

	/**
	 * Method gets text
	 * 
	 * @return String Text
	 */
	abstract protected String getText();

	/**
	 * Method sends text to the server
	 */
	abstract protected void sendMessage();

	/**
	 * Method receives messages from other clients
	 */
	protected void receiveMessage() {
		/*
		 * create a thread so that messages can be received whilst a client is sending a
		 * message
		 */
		Thread readMessage = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader receiveInfo;

				try {
					while (true) {

						receiveInfo = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
						String outputMsg = receiveInfo.readLine();

						/*
						 * If null or the killword is sent back from the server exit the program note that the
						 * client's socket is closed in the client manager. Hence it doesn't need to be closed again.
						 */
						if (outputMsg == null || outputMsg.equals(killword)) {
							display(killword);
							System.exit(0);
						} else {
							display(outputMsg);
						}
					}
				}

				/*
				 * This catches the I/O exception and tries to close the Client socket. If the
				 * client socket can't close properly the client program will terminate.
				 */
				catch (IOException e) {
					System.err.println("Error receiving message");
				}

				finally {
					try {
						ClientSocket.close();
					} catch (IOException e) {
						System.err.println("couldn't close socket program will exit");
					}

					finally {
						System.exit(0);
					}
				}
			}
		});
		readMessage.start();
	}
}
