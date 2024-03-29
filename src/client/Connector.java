package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import model.Contact;

import org.json.JSONObject;

public enum Connector {
	INSTANCE;
	
	private String host;
	private int port;
	private Socket clientSocket;

	public synchronized static Connector getInstance() {
		return INSTANCE;
	}
	
	public synchronized void init(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/** Log in
	 * @param username
	 * @param password
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the capitalized msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject login(String username, String password, int clientPort) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "login");
			JSONObject body = new JSONObject();
			body.put("username", username);
			body.put("password", password);
//			body.put("address", "");
			body.put("port", clientPort);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();	
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Get msgs sent to user while user was offline
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success notification (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject getOfflineMsgs(String clientUsername) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "getofflinemsgs");
			JSONObject body = new JSONObject();
			body.put("username", clientUsername);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}	
	
	/** Log out
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success notification (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject logout(String username) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "logout");
			JSONObject body = new JSONObject();
			body.put("username", username);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Send message through server
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject sendMessage(String usernameSender, String usernameReceiver, String msg) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "message");
			JSONObject body = new JSONObject();
			body.put("sender", usernameSender);
			body.put("receiver", usernameReceiver);
			body.put("msg", msg);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Broadcast message to every other online user
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject broadcast(String clientUsername, String msg) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "broadcast");
			JSONObject body = new JSONObject();
			body.put("sender", clientUsername);
			body.put("msg", msg);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Block a specified user
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject block(String clientUsername, String usernameToBlock) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "block");
			JSONObject body = new JSONObject();
			body.put("username", clientUsername);
			body.put("usernameToBlock", usernameToBlock);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Unblock a specified user
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject unblock(String clientUsername, String usernameToUnblock) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "unblock");
			JSONObject body = new JSONObject();
			body.put("username", clientUsername);
			body.put("usernameToUnblock", usernameToUnblock);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Get list of all online users
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject listOnlineUsers() {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "online");
			JSONObject body = new JSONObject();
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Get address and port of specified user
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject getAddress(String clientUsername, String usernameToGetAddress) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "getAddress");
			JSONObject body = new JSONObject();
			body.put("username", clientUsername);
			body.put("usernameToGetAddress", usernameToGetAddress);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}

	/** Send P2P message directly to specified user
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject sendPrivateMessage(String usernameSender, Contact receiver, String msg) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(receiver.getAddress(), receiver.getPort());
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "message");
			JSONObject body = new JSONObject();
			body.put("sender", usernameSender);
			body.put("receiver", receiver.getUsername());
			body.put("content", msg);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Client cannot be reached.");
		} catch (IllegalArgumentException e) {
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Client has never logged into server.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** Send a heartbeat to server indicating current client being active
	 * @return 
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the success msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject sendHeartbeat(String clientUsername, long timestamp) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "heartbeat");
			JSONObject body = new JSONObject();
			body.put("username", clientUsername);
			body.put("timestamp", timestamp);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/** TEST Simple communication with server through socket
	 * @param msg string to be capitlized
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the capitalized msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public synchronized JSONObject capitalize(String msg) {
		JSONObject response = null;
		
		try {
			// Open connection
			clientSocket = new Socket(host, port);
			
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "capitalize");
			JSONObject body = new JSONObject();
			body.put("msg", msg);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			
			// Close connection
			clientSocket.close();	
		} catch (ConnectException e) {
//			e.printStackTrace();
			response = new JSONObject();
			response.put("result", "failure");
        	response.put("errMsg", "Server cannot be reached. Please make sure server is running.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return response;
	}


}
