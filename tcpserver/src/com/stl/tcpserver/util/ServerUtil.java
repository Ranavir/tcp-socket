package com.stl.tcpserver.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerUtil {
	public static void sendToWeb(String reqData) {
		String url = ProjectConstants.SERVER_URL ;
		System.out.println("url:"+url);
		final String USER_AGENT = "Mozilla/5.0";
		URL obj;
		try {
			String encodedData=java.net.URLEncoder.encode(reqData,"UTF-8");
			System.out.println("encodedData:"+encodedData);
			obj = new URL( url + encodedData );
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url + encodedData);
			System.out.println("Response Code : " + responseCode);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
