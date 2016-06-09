package com.stl.tcpserver.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

import com.stl.tcpserver.util.ProjectConstants;
import com.stl.tcpserver.util.ServerUtil;

/*
 * A chat server that delivers public and private messages.
 * @author Ranvir Dash
 */
public class MultiThreadChatServerSync {

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;
  
  

  // This chat server can accept up to maxClientsCount clients' connections.
  //private static final int maxClientsCount = 5;
  private static final ClientThread[] threads = new ClientThread[ProjectConstants.MAX_CLIENTS_ALLOWED];
  
  //private static String serverIp = ProjectConstants.SERVER_IP ;//used for creating the browser client socket
	
	


  public static void main(String args[]) {

    // The default port number.
    int portNumber = ProjectConstants.SERVER_PORT ;
    if (args.length < 1) {
      System.out.println("Running java MultiThreadChatServerSync <portNumber> = " + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

    /*
     * Open a server socket on the portNumber (default 2222). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        //boolean busyFlag = false ;
        if((clientSocket.getRemoteSocketAddress()+"").indexOf(ProjectConstants.SERVER_IP) == -1 ){
        	/*
        	 * Create only maxClientCount - 1 no of clients for normal usage
        	 */
	        for (i = 0; i < ProjectConstants.MAX_CLIENTS_ALLOWED - 1; i++) {
	          if (threads[i] == null) {
	        	  System.out.println("Normal Client Request...");
	            (threads[i] = new ClientThread(clientSocket, threads)).start();
	            //System.out.println("Valueof I ************************: "+i);
	            
	            break;
	          }
	        }
        }
        /*if (i == maxClientsCount) {
        	PrintStream os = new PrintStream(clientSocket.getOutputStream());
            os.println("Server too busy. Try later.");
            //ServerUtil.sendToWeb("Server too busy. Try later.");
            ServerUtil.sendToWeb("/busy");
            os.close();
            clientSocket.close();
          }*/
        /*
         * Create a special client for browser side communication i.e same IP address client(logically browser client here)
         */
        else if((clientSocket.getRemoteSocketAddress()+"").indexOf(ProjectConstants.SERVER_IP) != -1 ){//client is browser client i.e same IP address client
        	System.out.println("Browser Client Request...");
        	(threads[ ProjectConstants.MAX_CLIENTS_ALLOWED - 1 ] = new ClientThread(clientSocket, threads)).start();
        }
        /*
         * Check max no of normal client for busy message 
         */
        if ( i == ProjectConstants.MAX_CLIENTS_ALLOWED - 1 && (clientSocket.getRemoteSocketAddress()+"").indexOf(ProjectConstants.SERVER_IP) == -1 ) {//check if socket is not of the browser client and already reached maxClient
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          System.out.println( ProjectConstants.MSG_BUSY1 );
          os.println( ProjectConstants.MSG_BUSY1 );
          ServerUtil.sendToWeb( ProjectConstants.MSG_BUSY );
          os.close();
          clientSocket.close();
        }
        
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}
/**
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 * @author Ranvir Dash
 * 
 */
class ClientThread extends Thread {

  private String clientName = null;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final ClientThread[] threads;
  private int maxClientsCount;
  //private String serverIp = ProjectConstants.SERVER_IP ;

//Global message
  public static String globalMsg = "" ;
  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
  
  public ClientThread(Socket clientSocket, ClientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
    /*
     * Check if it is not the special client i.e browser client then send the new connection message to browser
     */
    System.out.println("clientSocket.getRemoteSocketAddress()::"+clientSocket.getRemoteSocketAddress());
    if((clientSocket.getRemoteSocketAddress()+"").indexOf( ProjectConstants.SERVER_IP ) == -1){
    	System.out.println(ProjectConstants.MSG_NEW_CONNECTION + clientSocket.getRemoteSocketAddress()+"");
    	ServerUtil.sendToWeb(ProjectConstants.MSG_NEW_CONNECTION + clientSocket.getRemoteSocketAddress()+"");
    }
    
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    ClientThread[] threads = this.threads;

    try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      /*String name;
      while (true) {
        os.println("Enter your name.");
        name = is.readLine().trim();
        if (name.indexOf('@') == -1) {
          break;
        } else {
          os.println("The name should not contain '@' character.");
        }
      }*/

      /* Welcome the new the client. */
     // os.println("Welcome " + name + " to our chat room.\nTo leave enter /quit in a new line.");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            //clientName = "@" + name;
        	  clientName = threads[i].clientSocket.getRemoteSocketAddress()+"";
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            //threads[i].os.println("*** A new user " + name + " entered the chat room !!! ***");
        	  
          }
        }
      }
      /* Start the conversation. */
      while (true) {
        String line = is.readLine();
        if(line.indexOf("#")!=-1){
        	//String strArrData[] = line.split("#");
        	globalMsg = line.substring(0,line.length()-1);
        	//globalMsg = strArrData[0];
        	//serverAddr = strArrData[1];
        	//System.out.println("globalMsg Received from server:"+clientSocket.getLocalSocketAddress()+" is: "+globalMsg);
        }
        if (line.startsWith(ProjectConstants.MSG_DISCONNECT_FROM_CLIENT)) {
          break;
        }
        /* If the message is private sent it to the given client. */
        /*if (line.startsWith("@")) {
          String[] words = line.split("\\s", 2);
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
              synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(words[0])) {
                    threads[i].os.println("<" + name + "> " + words[1]);
                    
                     * Echo this message to let the client know the private
                     * message was sent.
                     
                    this.os.println(">" + name + "> " + words[1]);
                    break;
                  }
                }
              }
            }
          }
        }*/ else {
          /* The message is public, broadcast it to all other clients. */
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].clientName != null) {
                //threads[i].os.println("<" + name + "> " + line);
            	  if(globalMsg!="" && threads[i]!= this){//message from server browser broadcast to other clients except to own  thread
            		    /*threads[i].os.println(sdf.format(Calendar.getInstance().getTime())+"\tServer -> "+threads[i].clientName +" " + globalMsg);
          	  		    ServerUtil.sendToWeb(sdf.format(Calendar.getInstance().getTime())+"&nbsp;&nbsp;&nbsp;&nbsp;"+"Server -> "+threads[i].clientName +" " + globalMsg);*/
            		  	threads[i].os.println(threads[i].clientName +" " + globalMsg);
        	  		    ServerUtil.sendToWeb(threads[i].clientName +" " + globalMsg);
            	  }
            	  //put here threads[i]==this condition so that only one time message sent to web browser
            	  else{//message from other threads or clients
            		  /*
            		   * do not broadcast to Server thread (1st condition)
            		   * and only send the message to browser once(2nd condition)
            		   */
            		  if(threads[i].clientName.indexOf( ProjectConstants.SERVER_IP ) == -1 &&  threads[i] == this){
            			    /*threads[i].os.println(sdf.format(Calendar.getInstance().getTime())+"\t"+threads[i].clientName + " " + line);
            	  			ServerUtil.sendToWeb(sdf.format(Calendar.getInstance().getTime())+"&nbsp;&nbsp;&nbsp;&nbsp;"+threads[i].clientName + " " + line);*/
            			  
            			  /*
            			   * also do not send the client message to other clients so comment the below line 
            			   * (if client msg is to be sent to other clients then uncomment the below line along with threads[i]==this )
            			   */
            		  		//threads[i].os.println(threads[i].clientName + " " + line);
            		  		ServerUtil.sendToWeb(threads[i].clientName + " " + line);
            		  }
            	  }
              }
            }//end for loop
            if(globalMsg != ""){
            	globalMsg = "" ;//Reseting global message
            	break ;
            }
          }
        }
      }//End while loop
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
            //threads[i].os.println("*** The user " + name + " is leaving the chat room !!! ***");
        	  //threads[i].os.println("Connection Lost to " + threads[i].clientName);
        	  //ServerUtil.sendToWeb("Connection Lost to " + threads[i].clientName);
          }
        }
      }
      //os.println("*** Bye " + name + " ***");
      //os.println("Bye " + clientName);
      //ServerUtil.sendToWeb("Server Says: Bye " + clientName);

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
        	  threads[i].os.println(ProjectConstants.MSG_LOST_CONNECTION + threads[i].clientName);
        	  /*
        	   * Check if it is not the special browser client then send the connection loss message to browser
        	   */
        	  if(threads[i].clientName.indexOf( ProjectConstants.SERVER_IP ) == -1)
        		  	ServerUtil.sendToWeb(ProjectConstants.MSG_LOST_CONNECTION + threads[i].clientName);
        	  threads[i] = null;
          }
        }
      }
      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }

	
}