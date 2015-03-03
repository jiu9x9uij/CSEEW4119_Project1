package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public enum Connector {
	INSTANCE;
	
	private String host;
	private int port;
	private Socket clientSocket;

	public static Connector getInstance() {
		return INSTANCE;
	}
	
	public void init(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/* TEST Simple communication with server through socket */
	public void capitalize(String msg) {
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(msg + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("FROM SERVER: " + inFromServer.readLine()); 
			
			// Close connection
			clientSocket.close();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
	}
}
