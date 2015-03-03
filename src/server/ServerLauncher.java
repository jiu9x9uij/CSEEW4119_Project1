/**
 * Server Executable
 */
package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Yilin
 *
 */
public class ServerLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = -1;
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		ArrayList<User> allClients = new ArrayList<User>();
		ArrayList<User> onlineClients = new ArrayList<User>();
		ArrayList<User> offlineClients = new ArrayList<User>();
		String clientMsg;
		
		try {
			/* Store all clients' credentials */
			String line;
			FileInputStream fileInputStream = new FileInputStream("credentials.txt");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		 
			while ((line = bufferedReader.readLine()) != null) {
				String[] credential = line.split(" ");
				allClients.add(new User(credential[0], credential[1]));
			}
			
			/* Open welcoming socket */
			port = Integer.parseInt(args[0]);
			System.out.println(port);///
			welcomeSocket = new ServerSocket(port);
			
			/* Listen to incoming client requests */
			while (true) {
				connectionSocket = welcomeSocket.accept();
				BufferedReader inFromClient = new BufferedReader (new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				
				clientMsg = inFromClient.readLine(); // TODO Parse JSON, and execute corresponding command
				outToClient.writeBytes(clientMsg.toUpperCase() + '\n'); // TODO Current function is for test only
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				welcomeSocket.close();
				connectionSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
