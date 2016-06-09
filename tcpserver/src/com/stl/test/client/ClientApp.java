package com.stl.test.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientApp {

	public static void main(String[] args) {
		String serverName = "192.168.0.23";
	      int port = Integer.parseInt("124");
	      
	      
	      BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
	      
	      try
	      {
	         System.out.println("Connecting to " + serverName + " on port " + port);
	         Socket client = new Socket(serverName, port);
	         System.out.println("Just connected to " + client.getRemoteSocketAddress());
	         while(true)
	         {
	        	 String receiveMessage, sendMessage;
	        	 System.out.println("Enter your message to server : ");
		         sendMessage = keyRead.readLine() ;
		         
		         OutputStream outToServer = client.getOutputStream();
		         DataOutputStream out = new DataOutputStream(outToServer);
	        	 //out.writeUTF("Client Says :"+sendMessage+" from " + client.getLocalSocketAddress());
		         if(sendMessage.equals("stop")){
		        	 System.out.println("Client stopping Communication...");
		         }
		         out.writeUTF(sendMessage);
	        	 
	        	 InputStream inFromServer = client.getInputStream();
	        	 DataInputStream in = new DataInputStream(inFromServer);
	        	 receiveMessage = in.readUTF() ;
	        	 System.out.println("Server says " + receiveMessage);
	        	 
	        	 if(receiveMessage.equals("stop")){
	        		System.out.println("I'm stopped by server...");
	            	client.close();
	            	break ;
		         }
	        	 else
	        		 continue ;
	        	 /*if((receiveMessage = receiveRead.readLine()) != null) //receive from server
	               {
	                   System.out.println(receiveMessage); // displaying at DOS prompt
	                   client.close();
	               }*/
	         }//end messaging while
	         
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }

	}

}
