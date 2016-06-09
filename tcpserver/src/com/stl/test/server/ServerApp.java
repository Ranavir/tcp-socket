package com.stl.test.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerApp extends Thread{

	private ServerSocket serverSocket;
	 
	BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
	   public ServerApp(int port) throws IOException
	   {
	      serverSocket = new ServerSocket(port);
	      //serverSocket.setSoTimeout(10000);
	   }

	   public void run()
	   {
	      while(true)
	      {
	         try
	         {
	            System.out.println("Waiting for client on port " +
	            serverSocket.getLocalPort() + "...");
	            Socket server = serverSocket.accept();
	            System.out.println("Just connected to " + server.getRemoteSocketAddress());
	            
	            
	            while(true)
	            {
	            	String receiveMessage, sendMessage;
	            	DataInputStream in = new DataInputStream(server.getInputStream());
	            	receiveMessage = in.readUTF() ;
		            System.out.println(receiveMessage);
		            if(receiveMessage.equals("stop")){
		            	System.out.println("Client says to stop communication");
		            	DataOutputStream out  = new DataOutputStream(server.getOutputStream());
			            out.writeUTF("stop");
		            	server.close();
		            	break ;
		            }
		            else{
		            	System.out.println("Enter Server Side message to client : ");
			            sendMessage = keyRead.readLine();  // keyboard reading
			            DataOutputStream out  = new DataOutputStream(server.getOutputStream());
			            //out.writeUTF("Server Message :"+sendMessage+" from " + server.getLocalSocketAddress());
			            out.writeUTF(sendMessage);
			            if(sendMessage.equals("stop")){
				        	 System.out.println("Server stopping Communication...");
				        	 server.close();
				             break ;
				        }
			            
		            }
		            
	               /*if((receiveMessage = receiveRead.readLine()) != null) //receive from server
	               {
	                   System.out.println(receiveMessage); // displaying at DOS prompt
	                   server.close();
	               }   */      
	             }//End messaging while loop   
	           
	         }catch(SocketTimeoutException s)
	         {
	            System.out.println("Socket timed out!");
	            break;
	         }catch(IOException e)
	         {
	            e.printStackTrace();
	            break;
	         }
	      }//end thread while loop
	   }//end thread run
	   public static void main(String [] args)
	   {
	      int port = Integer.parseInt("124");
	      try
	      {
	    	  //new ServerApp(port).run();
	         Thread t = new ServerApp(port);
	         t.start();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	   }

}
