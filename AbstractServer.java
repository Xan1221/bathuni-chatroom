
/**
 * This class includes functionality of the server
 */

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServer {

	/** Server socket */
	protected ServerSocket servSoc;
	/** ArrayList managing messages sent by clients */
	protected List<clientManager> ar = new ArrayList<>();
	/** Keeping track of how many clients are being managed */
	protected int i = 0;

	/**
	 * Instantiates server socket starting the server
	 * 
	 * @param port
	 *            Server's port
	 */
	protected void serv(int port) {
		try {
			this.servSoc = new ServerSocket(port);

		} 
		/*This ensures that 2 instances of the server can't be instantiated*/
		catch (IOException e) {
			System.err.println("Couldn't create object");
			System.exit(0);
		}
		/*This ensures that illegal arguments can't be passed into the port*/
		catch (IllegalArgumentException e1) {
			System.err.println("Illeagal argument passed in program shutting down");
			System.exit(0);
		}
	}

	/**
	 * This method accepts messages connections from clients
	 * @param gui
	 *      The GUI interface
	 */
	protected void go(guiServer gui) {
		acceptMessage(gui);
	}

	/**
	 * Method loops through all connected clients and disconnects them, then
	 * disconnects the server socket and then exits the program
	 */
	protected void closeSystem() {
		try {
			for (int i = 0; i < ar.size(); i++) {
				ar.get(i).GetClient().close();
			}
			
			if (servSoc != null) {
				servSoc.close();
			}
		}

		/*
		 * If the client socket or Server socket can't close properly inform the user by the command line
		 */
		catch (IOException e) {
			System.err.println("Something went wrong with closing a socket");
		}
		/* Always close the program in the end */
		finally {
			System.exit(0);
		}
	}

	/**
	 * Method displays text
	 */
	protected abstract void display(String line);

	/**
	 * This method accepts messages from clients
	 * 
	 * @param gui
	 *            of type guiServer the user interface
	 */
	abstract protected void acceptMessage(guiServer gui);

	/**
	 * Method Has to be synchronized because it is continuously changing due to
	 * clients logging out hence only one thread must have access to it.
	 * 
	 * @return List of clientManagers
	 */
	protected synchronized List<clientManager> getClientMan() {
		return ar;
	}

}
