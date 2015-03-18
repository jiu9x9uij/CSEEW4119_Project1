package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class ClientLauncher {
	private static ThreadPooledListener serverListener;
	private static Connector connector;
	private static String clientUsername = null;
	static BufferedReader input;
	
	public static void terminateClient() {
		serverListener.stop();
		System.exit(0);
	}
	
	/** Log in
	 * @return 1 login successful, proceed;
	 * 	0 login unsuccessful, retry;
	 *  -1 login unsuccessful, blocked;
	 *  -2 exception during login
	 */
	private static int login() {
		String username, password;
		BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
		
		try {
			// Ask for username
			print("username: ");
			username = input.readLine();
			// Ask for password
			print("password: ");
			password = input.readLine();
			
			// Validate credential on server
			JSONObject response = connector.login(username, password, serverListener.getPort());
			if (response.getString("result").equals("success")) {
				clientUsername = username;
				return 1;
			}
			else if (!response.getString("errMsg").contains("blocked")) return 0;
			else return -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return -2;
	}
	
	private static boolean logout() {
		if (clientUsername == null) return false;
		
		JSONObject response = connector.logout(clientUsername);
		if (response.getString("result").equals("failure")) return false;
		else return true;
	}
	
	private static void sendMessage(String command) {
		String[] args = command.split(" ");
		if (args.length < 3) {
			println("ERROR: Invalid command format. Please use \"message <username> <message content>\"");
			return;
		}
		
		String usernameReceiver = args[1];
		String msg = new String();
		for (int i = 2; i < args.length; i++) msg += args[i];
		JSONObject response = connector.sendMessage(clientUsername, usernameReceiver, msg);
	}
	
	private static void broadcast(String command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void listOnlineUsers() {
		// TODO Auto-generated method stub
		
	}
	
	private static void blockUser(String command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void unblockUser(String command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void getAddressOfUser(String command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void sendPrivateMessage(String command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void listCommands() {
		// TODO Auto-generated method stub
		
	}
	
	/* TEST Simple communication with server through socket */
	private static void capitalize() {
		String msg;
		BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
		
		try {
			msg = input.readLine();
			connector.capitalize(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void println(String s) {
		System.out.println(s);
	}
	
	public static void print(String s) {
		System.out.println(s);
	}
	
	
	
	
	public static void main(String[] args) {
		try {
			/* Open socket */
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			System.out.println(host + ":" + port);///
			connector = Connector.INSTANCE;
			connector.init(host, port);
			
//			capitalize(); ///
			
			/* Listen to server and P2P messages in thread pool */
			serverListener = new ThreadPooledListener();
			new Thread(serverListener).start();
			
			/* Log in */
			int result;
			while ((result = login()) != 1) {
				// If blocked by server, terminate client
				if (result == -1) return;
			}
			
			/* Listen to user command until logout command or terminated by server*/
			String command;
			input = new BufferedReader (new InputStreamReader(System.in));
			try {
				// Listen to commands
				while (true) {
					command = input.readLine();
					if (command.equals("logout")) {
						logout();
						break;
					}
					
					if (command.startsWith("message")) sendMessage(command);
					else if (command.startsWith("broadcast")) broadcast(command);
					else if (command.equals("online")) listOnlineUsers();
					else if (command.startsWith("block")) blockUser(command);
					else if (command.startsWith("unblock")) unblockUser(command);
					else if (command.startsWith("getaddress")) getAddressOfUser(command);
					else if (command.startsWith("private")) sendPrivateMessage(command);
					else if (command.equals("help")) listCommands();
					else println("ERROR: Invalid command. Type \"help\" to see list of commands.");
				}
				
				// User requested to log out, shut down client-side listener
				serverListener.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
