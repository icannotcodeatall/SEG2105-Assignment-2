package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;

import java.io.IOException;

import edu.seg2105.client.common.*;
import edu.seg2105.edu.server.ui.ServerConsole;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  /**
   * The key used for setInfo and getInfo
   */
  final private static String loginKey = "loginID";
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	String msgStr = (String) msg;
	
	System.out.println("Message received: " + msg + " from " + client.getInfo(loginKey));
	
	if (msgStr.startsWith("#login")) {
		if (client.getInfo(loginKey) == null) {
			String loginID = msgStr.substring(7);
			client.setInfo(loginKey, loginID);
			System.out.println(client.getInfo(loginKey) + " has logged on");
			this.sendToAllClients(client.getInfo(loginKey) + " has logged on");
		} else {
			try {
				client.sendToClient("You are already logged in, cannot log in again");
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	} else {
		this.sendToAllClients(client.getInfo(loginKey) + "> " + msg);
	}
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when a client connects to the server.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println(client.getInfo(loginKey) + " has connected to the server");
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the a client disconnects from the server.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println(client.getInfo(loginKey) + " has disconnected from the server");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    ServerConsole serverConsole = new ServerConsole(sv);
    serverConsole.accept();
  }
}
//End of EchoServer class
