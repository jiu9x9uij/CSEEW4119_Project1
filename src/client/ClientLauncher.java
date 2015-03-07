package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientLauncher {
	static Connector connector;
	
	private static boolean login() {
		String username, password;
		BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
		
		try {
			// Ask for username
			System.out.print("username: ");
			username = input.readLine();
			// Ask for password
			System.out.print("password: ");
			password = input.readLine();
			
			// Validate credential on server
			if (connector.login(username, password).getString("result").equals("success")) return true;
			else return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		try {
			/* Open socket */
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			System.out.println(host + ":" + port);///
			connector = Connector.INSTANCE;
			connector.init(host, port);
			
			BufferedReader inFromUser = new BufferedReader (new InputStreamReader(System.in));
			String msg = inFromUser.readLine();
			connector.capitalize(msg); // TODO Current function is for test only
			
			login();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
