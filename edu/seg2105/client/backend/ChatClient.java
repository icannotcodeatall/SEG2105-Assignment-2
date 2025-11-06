// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  /**
   * The ID of the client
   */
  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.startsWith("#")) {
    		handleCommand(message.substring(1));
    	} else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  private void handleCommand(String command) throws IOException {
	  String[] commandSplit = command.split(" ");
	  switch(commandSplit[0]) {
	  	case "quit":
			quit();
			break;
		case "logoff":
			closeConnection();
			break;
		case "sethost":
			if (isConnected()) {
				clientUI.display("ERROR - Cannot set host unless logged off");
			} else {
				if (commandSplit.length > 1) {
					setHost(commandSplit[1]);
				} else {
					clientUI.display("ERROR - Host parameter missing");
				}
			}
			break;
		case "setport":
			if (isConnected()) {
				clientUI.display("ERROR - Cannot set port unless logged off");
			} else {
				if (commandSplit.length > 1) {
					setPort(Integer.parseInt(commandSplit[1]));
				} else {
					clientUI.display("ERROR - Port parameter missing");
				}
			}
			break;
		case "login":
			if (isConnected()) {
				clientUI.display("ERROR - Cannot log in as user is already connected to server");
			} else {
				openConnection();
			}
			break;
		case "gethost":
			clientUI.display("Host is: " + getHost());
			break;
		case "getport":
			clientUI.display("Port is: " + getPort());
			break;
		default:
			clientUI.display("ERROR - Command not recognized");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  	/**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method has been
	 * overridden to display a message when there is a connection exception
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server has shut down");
  		quit();
	}
  	
  	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method has been overridden to
	 * display a message when the connection is closed
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("Connection closed");
	}
  	
  	/**
	 * Implements the hook method called after a connection has been established. The default
	 * implementation does nothing. the method has been overridden to
	 * to send a message upon establishing a connection
	 */
  	@Override
	protected void connectionEstablished() {
  		try { // Apparently you need to account for the IOException, but at the same time the AbstractClient hook method doesn't do that so I think this is the only way
			sendToServer("#login " + loginID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
//End of ChatClient class
