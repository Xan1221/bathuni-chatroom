
/**
 * This class is responsible for managing client connections
 */
import java.net.*;
import java.io.*;

public class clientManager implements Runnable {

	/** Client's socket */
	private Socket client;
	/** Client's port */
	private int port;
	/** PrintWriter to send messages back to the client */
	private PrintWriter out;
	/** Message sent by the client */
	private String line;
	/** The GUI on the server side */
	private guiServer gui;
	/** The chat server itself */
	private ChatServer server;
	/** The word that logs the client out */
	private final String killWord = "/logout";
	/** Message client manager sends client if the client logs out */
	private final String logoutMessage = "you have logged out";

	/**
	 * Constructor: Sets fields for the variables within this class
	 * 
	 * @param client
	 *            Client's socket
	 * @param serv
	 *            The chat server
	 * @param guig
	 *            The Server GUI
	 */
	clientManager(Socket client, ChatServer serv, guiServer guig) {
		this.client = client;
		this.port = client.getPort();
		this.gui = guig;
		this.server = serv;
	}

	/**
	 * Method returns the client's port
	 * 
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Display text on the console
	 * 
	 * @param serverMsg
	 */
	void serverDisplay(String serverMsg) {
		System.out.println(serverMsg);
	}

	/**
	 * Display text on the GUI
	 * 
	 * @param gui
	 *            The server GUI
	 * @param serverMsg
	 *            Message sent by the client
	 */
	void serverGUIDisplay(guiServer gui, String serverMsg) {
		gui.display(serverMsg);
	}

	/**
	 * Client managers method to read send text and send it to other clients
	 */
	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			// continues to manage clients until they logout
			while ((line = in.readLine()) != null) {
				if (gui == null) {
					serverDisplay("Server received: " + line);
				}

				else {
					serverGUIDisplay(gui, "Server received: " + line);
				}

				// As the name is of variable length split the string where the semicolon is
				// present
				String[] splitter = line.split(": "); 

				/*
				 * If the message including a space is the kill word send the client back the
				 * kill word Which then terminates their program
				 */
				if (splitter.length==2&&splitter[1].equals(killWord)) {
					this.out.println(logoutMessage);
					// remove the client manager from the client that logged out
					server.getClientMan().remove(this);
					break;
				}

				/*
				 * A loop that sends messages to the other clients excluding the client who sent
				 * the message to the server
				 */
				for (clientManager mc : server.getClientMan()) {
					if (mc != this) {
						mc.out.println(line);
					}
				}
			}
		}
		/*If the message can't be read and the client's socket isn't closed display the error message*/
		catch (IOException e) {
			if (!client.isClosed()) {
				System.err.println("Couldn't read message");
			}

		}

		// Close all resources safely
		finally {
			try {
				if (out != null) {
					out.close();
				}

				if (in != null) {
					in.close();
				}
				client.close();

			} catch (IOException e) {
				serverDisplay("Error closing this client's socket");
			}

		}
	}

	/**
	 * return the client socket
	 */
	public Socket GetClient() {
		return client;
	}
}
