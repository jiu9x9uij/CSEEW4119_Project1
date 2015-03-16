/**
 * Server Executable
 */
package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Yilin
 *
 */
public enum ServerLauncher {
	INSTANCE;
	
	private HashMap<String, User> allClients = new HashMap<String, User>();
	private HashMap<String, User> onlineClients = new HashMap<String, User>();
	private HashMap<String, User> offlineClients = new HashMap<String, User>();

	public static ServerLauncher getInstance() {
		return INSTANCE;
	}
	
	public HashMap<String, User> getAllClients() {
		return allClients;
	}
	
	public void addClient(User user) {
		allClients.put(user.getUsername(), user);
	}

	public HashMap<String, User> getOnlineClients() {
		return onlineClients;
	}
	
	public void addOnlineClient(User user) {
		onlineClients.put(user.getUsername(), user);
	}
	
	public void removeOnlineClient(User user) {
		onlineClients.remove(user.getUsername());
	}
	
	public HashMap<String, User> getOfflineClients() {
		return offlineClients;
	}
	
	public void addOfflineClient(User user) {
		offlineClients.put(user.getUsername(), user);
	}
	
	public void removeOfflineClient(User user) {
		offlineClients.remove(user.getUsername());
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/* Store all clients' credentials */
			String line;
			FileInputStream fileInputStream = new FileInputStream("credentials.txt");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		 
			while ((line = bufferedReader.readLine()) != null) {
				String[] credential = line.split(" ");
				User user = new User(credential[0], credential[1]);
				ServerLauncher.INSTANCE.addClient(user);
				ServerLauncher.INSTANCE.addOfflineClient(user); // TODO Not sure if this is needed at all
			}
			
			bufferedReader.close();
			
			/* Run server */
			int port = Integer.parseInt(args[0]);
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
