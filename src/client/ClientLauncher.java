package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ClientLauncher {

	public static void main(String[] args) {
		String host = args[0];
		int port = -1;
		Socket clientSocket = null;
		String msg;
		
		try {
			port = Integer.parseInt(args[1]);
			System.out.println(host + ":" + port);///

			BufferedReader inFromUser = new BufferedReader (new InputStreamReader(System.in));
			clientSocket = new Socket(host, port);
			
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			msg = inFromUser.readLine();
			
			outToServer.writeBytes(msg + '\n'); // TODO Encode in JSON
			System.out.println("FROM SERVER: " + inFromServer.readLine()); // TODO Current function is for test only
			
		} catch (NumberFormatException e) {
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
