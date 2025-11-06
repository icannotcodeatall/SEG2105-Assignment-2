package edu.seg2105.edu.server.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a server console.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here was cloned from ClientConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ServerConsole implements ChatIF 
{
  //Instance variables **********************************************
  
  /**
   * The server to take user input in
   */
  EchoServer server;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ServerConsole(EchoServer server) 
  {
    this.server = server;
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        
        if (message.startsWith("#")) {
        	handleCommand(message.substring(1));
        } else {
        	display("SERVER MSG> " + message);
            server.sendToAllClients("SERVER MSG> " + message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
  
  private void handleCommand(String command) throws IOException {
	  String[] commandSplit = command.split(" ");
	  switch(commandSplit[0]) {
	  	case "quit":
			server.stopListening();
			server.close();
			System.exit(0);
			break;
		case "stop":
			server.stopListening();
			break;
		case "close":
			server.close();
			break;
		case "setport":
			if (server.isListening() || server.getNumberOfClients() > 0) {
				display("ERROR - Cannot set port unless server is closed");
			} else {
				if (commandSplit.length > 1) {
					server.setPort(Integer.parseInt(commandSplit[1]));
				} else {
					display("ERROR - Port parameter missing");
				}
			}
			break;
		case "start":
			if (server.isListening()) {
				display("ERROR - Cannot start server as it is already listening for clients");
			} else {
				server.listen();
			}
			break;
		case "getport":
			display("Port is: " + server.getPort());
			break;
		default:
			display("ERROR - Command not recognized");
	  }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }
}
//End of ConsoleChat class
