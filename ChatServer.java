
/**
 * This class is in charge of the server
 */

import java.io.*;

public class ChatServer extends AbstractServer {
	/** thread to read console input */
	private Thread consoleIncomming;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            The server's port
	 */
	ChatServer(int port) {
		serv(port);
		consoleIncomming = new Thread() {
			public void run() {
				while (true) {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					try {
						String line = br.readLine();
						// Call the close system method if EXIT is typed into the command line
						if (line.equals("EXIT")) {
							closeSystem();
						}
					}

					/*
					 * This catches the I/O exception and tries to shut the server down. If the
					 * client sockets can't close properly the server will terminate terminating all
					 * clients along with it
					 */
					catch (IOException e) {
						System.err.println("Something went wrong with I/O, shutting down");
						closeSystem();
					}
				}
			}
		};
		consoleIncomming.start();
	}

	public static void main(String[] args) {

		/* Default server port */
		int port = 14001;
		/* Response to prompt */
		String response;
		/* The user interface, set it to null for the time being */
		guiServer gui = null;
		/* Server */
		ChatServer serv;

		System.out.println("Do you want to have a gui sever yes/no");
		BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
		try {
			response = read.readLine();
		} catch (IOException e1) {
			response = "no";
		}

		/*
		 * If there is a -csp in the command line read the next string attempt to turn
		 * it into an integer
		 */
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-csp")) {
				// Try to set the port. if it isn't a number print out an error message
				try {
					port = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					System.err.println("this isn't a number/invalid input setting up default port");
				}
			}
		}

		/*    
		 * If the response is equal to yes run the gui
		 */
		if (response.equals("yes")) {
			gui = new guiServer(port);
		}

		/*
		 * Otherwise instantiate the server and run the program
		 */
		else {
			serv = new ChatServer(port);
			serv.go(gui);
		}
	}

	@Override
	protected void display(String line) {
		System.out.println(line);
	}

	@Override
	protected void acceptMessage(guiServer gui) {
		try {
			if (gui != null) {
				gui.display("Server online");
			}
			display("Server online");
			clientManager man;
			while (true) {
				man = new clientManager(servSoc.accept(), this, gui);

				/*
				 * If the GUI is being run display text on the GUI otherwise display text on the
				 * console
				 */
				if (gui != null) {
					gui.display("Server accepted connection on " + servSoc.getLocalPort() + " ; " + man.getPort());
				}
				display("Server accepted connection on " + servSoc.getLocalPort() + " ; " + man.getPort());
				/*
				 * Start a new thread for the client manager and add it to the array of client
				 * managers
				 */
				Thread t = new Thread(man);
				ar.add(man);
				t.start();
				i++;
			}
		}

		/* Catches an exception when connecting the server to the client */
		catch (IOException e) {
			if(!servSoc.isClosed()) {
			System.err.println("Couldn't create a connection between the server and the client");
			}
		}
	}
}
