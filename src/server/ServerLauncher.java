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
		ArrayList<User> allClients = new ArrayList<User>();
		ArrayList<User> onlineClients = new ArrayList<User>();
		ArrayList<User> offlineClients = new ArrayList<User>();
		
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
			
			ThreadPooledServer server = new ThreadPooledServer(port);
			new Thread(server).start();

			/* Proof that server is running in another thread */
//			try {
//			    Thread.sleep(20 * 1000);
//			} catch (InterruptedException e) {
//			    e.printStackTrace();
//			}
//			System.out.println("Stopping Server");
//			server.stop();
			
			/* Old method */
//			welcomeSocket = new ServerSocket(port);
//			
//			/* Listen to incoming client requests */
//			while (true) {
//				System.out.println("loop");
//				final ServerSocket welcomeSocketCopy = welcomeSocket;
//				new Thread(){
//					public void run() {
//						System.out.println("new thread");///
//						Socket connectionSocket = null;
//						try {
//							connectionSocket = welcomeSocketCopy.accept();
//							BufferedReader inFromClient = new BufferedReader (new InputStreamReader(connectionSocket.getInputStream()));
//							DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//							
//							String clientMsg = inFromClient.readLine(); // TODO Parse JSON, and execute corresponding command
//							outToClient.writeBytes(clientMsg.toUpperCase() + '\n'); // TODO Current function is for test only
//						} catch (IOException e) {
//							e.printStackTrace();
//						} finally {
//							try {
//								connectionSocket.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//						System.out.println("thread finished");///
//					}
//				}.run();
//				System.out.println("loop2");
//			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			try {
//				welcomeSocket.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
}
