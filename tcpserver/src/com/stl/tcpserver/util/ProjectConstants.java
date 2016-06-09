package com.stl.tcpserver.util;
/**
 * Defines all the Constants of the project at 
 * single places
 * 
 * @author ranavir
 * @date 24052016
 */
public class ProjectConstants {

	//server related date:24052016
	public static final String SERVER_URL = "http://localhost:8080/tcpweb/status?reqData=" ;
	public static final String SERVER_IP = "192.168.0.145" ;
	public static final int SERVER_PORT = 124 ;
	
	//client related date:24052016
	public static final int MAX_CLIENTS_ALLOWED = 6 ;//1 client for web browser so total normal client n - 1
	
	//global messages
	public static final String MSG_DISCONNECT_FROM_CLIENT = "/quit" ;
	public static final String MSG_DISCONNECT_FROM_SERVER = "Bye" ;
	public static final String MSG_BUSY = "/busy" ;
	public static final String MSG_NEW_CONNECTION = "A new connection from  " ;
	public static final String MSG_LOST_CONNECTION = "Connection Lost to " ;
	public static final String MSG_BUSY1 = "Server too busy. Try later." ;
}
