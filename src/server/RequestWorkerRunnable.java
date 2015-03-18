package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Server-side worker thread
 */
public class RequestWorkerRunnable implements Runnable{

    protected Socket connectionSocket = null;
    protected String serverText   = null;

    public RequestWorkerRunnable(Socket connectionSocket, String serverText) {
        this.connectionSocket = connectionSocket;
        this.serverText   = serverText;
    }

    public void run() {
    	try {
    		/* Read client request */
    		System.out.println("### Client Request Received " + System.currentTimeMillis() + " ###"); // DEBUG Request received stamp
			BufferedReader inFromClient = new BufferedReader (new InputStreamReader(connectionSocket.getInputStream()));
			String clientMsg = inFromClient.readLine();
			
			/* Execute request */
			// Parse request
			JSONObject requestJson = new JSONObject(clientMsg);
			String request = requestJson.getString("request");
			JSONObject body = requestJson.getJSONObject("body");
			System.out.println("request = " + request); // DEBUG Request type
			
			// Execute corresponding method
            JSONObject serverResponse;
            if (request.equals("login")) serverResponse = login(body);
            else if (request.equals("getofflinemsgs")) serverResponse = getOfflineMsgs(body);
            else if (request.equals("logout")) serverResponse = logout(body);
            else if (request.equals("message")) serverResponse = message(body);
            else if (request.equals("broadcast")) serverResponse = broadcast(body);
            else if (request.equals("block")) serverResponse = block(body);
            else if (request.equals("unblock")) serverResponse = unblock(body);
            else if (request.equals("online")) serverResponse = online(body);
            else if (request.equals("getAddress")) serverResponse = getAddress(body);
            else if (request.equals("capitalize")) serverResponse = capitalize(body);
            else serverResponse = responseOK();
        	
        	/* Respond to client */
        	System.out.println("---response---\n" + serverResponse + "\n------end-----"); // DEBUG Response content
        	DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes(serverResponse.toString() + '\n');
        	
        	/* Close connection */
			System.out.println("closing connection"); // DEBUG Responded
        	inFromClient.close();
        	outToClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	System.out.println("######### Done #########\n"); // DEBUG Request processed
    }

	/* Validate login credential */
    private JSONObject login(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String username = body.getString("username");
    	String password = body.getString("password");
    	int port = body.getInt("port");
    	User user = (User) ServerLauncher.INSTANCE.getAllClients().get(username);
    	if (user == null) {
    		// Credential does not exist
    		response.put("result", "failure");
        	response.put("errMsg", "No such user. Please try again.");
    	} else if (user.isBlockedLogin()) {
    		// User is blocked from logging in
    		response.put("result", "failure");
        	response.put("errMsg", "Due to multiple login failures, your account has been blocked. Please try again after sometime.");
    	} else if (!password.equals(user.getPassword())) {
    		// Password does not match
    		user.increaseLoginAttemps();
    		if (user.getLoginAttemps() == ServerSettings.MAX_LOGIN_ATTEMPTS) {
    			user.blockLogin();
    			response.put("result", "failure");
            	response.put("errMsg", "Invalid Password. Your account has been blocked. Please try again after sometime.");
    		} else {
    			response.put("result", "failure");
            	response.put("errMsg", "Invalid Password. Please try again.");
    		}
    	} else {
    		// Credential validated
    		String address = connectionSocket.getRemoteSocketAddress().toString();
    		
    		// Notify previously logged in session of this credential before disconnecting it
    		if (user.isOnline()) {
    			notify(user.getAddress(), user.getPort(), "You have been logged out because your account is logged in somewhere else.");
    		}
    		
    		// Store address and port of current session
    		address = address.substring(1);
    		address = address.split(":")[0];
    		user.login(address, port);
    		response.put("result", "success");
        	response.put("response", "Welcome to simple chat server!");
        	
        	// Notify other users about the presence of this user
        	HashMap<String, User> onlineClients = ServerLauncher.INSTANCE.getOnlineClients();
        	for (User u: onlineClients.values()) {
        		if (u.getUsername().equals(username)) continue;
        		notify(u.getAddress(), u.getPort(), "User \"" + username + "\" is now online.");
        	}
    	}
    	
    	return response;
    }
    
    /* Send messages received when client was offline */
    private JSONObject getOfflineMsgs(JSONObject body) {
    	JSONObject response = new JSONObject();
    	
    	String username = body.getString("username");
    	User user = (User) ServerLauncher.INSTANCE.getAllClients().get(username);
    	ConcurrentLinkedQueue<Message> offlineMsgs =  user.getOfflineMsgs();
    	
    	if (offlineMsgs.isEmpty()) {
    		response.put("result", "failure");
        	response.put("errMsg", "No offline messages.");
    	}
    	else {
    		response.put("result", "success");
        	response.put("response", "Offline messages retrieved.");
    	}
    	
    	// Dequeue offline messages
    	while (!offlineMsgs.isEmpty()) {
    		Message m = offlineMsgs.remove();
    		forwardMessage(user.getAddress(), user.getPort(), m.getSender(), m.getContent());
    	}
    	
    	return response;
	}
    
    /* Log out */
    private JSONObject logout(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String username = body.getString("username");
    	User user = (User) ServerLauncher.INSTANCE.getAllClients().get(username);
    	if (user == null) {
    		// Credential does not exist, should not happen
    		response.put("result", "failure");
        	response.put("errMsg", "No such user. Something is wrong.");
    	}
    	
    	user.logout();
    	response.put("result", "success");
    	response.put("response", "You have successfully logged out.");
    	
    	// Notify other users about the leave of this user
    	HashMap<String, User> onlineClients = ServerLauncher.INSTANCE.getOnlineClients();
    	for (User u: onlineClients.values()) {
    		if (u.getUsername().equals(username)) continue;
    		notify(u.getAddress(), u.getPort(), "User \"" + username + "\" is now offline.");
    	}
    	
    	return response;
    }
    
    /* Send message to a client */
    private JSONObject message(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String usernameSender = body.getString("sender");
    	String usernameReceiver = body.getString("receiver");
    	String msg = body.getString("msg");
    	User receiver = (User) ServerLauncher.INSTANCE.getAllClients().get(usernameReceiver);
    	if (receiver == null) {
    		// Receiver does not exist
    		response.put("result", "failure");
        	response.put("errMsg", "No such user. Please try again.");
    	} else if (receiver.blocked(usernameSender)) {
    		// Sender is blocked
    		response.put("result", "failure");
        	response.put("errMsg", "Your message could not be delivered as the recipient has blocked you.");
    	} else {
    		// Send message to receiver
    		if (receiver.isOnline()) {
    			forwardMessage(receiver.getAddress(), receiver.getPort(), usernameSender, msg);
    		}
    		else receiver.addOfflineMsg(usernameSender, msg);
    		
    		response.put("result", "success");
        	response.put("response", "Message sent.");
    	}
    	
    	return response;
    }
    
    /* Broadcast a message from a client to every other client */
    private JSONObject broadcast(JSONObject body) {
    	JSONObject response = new JSONObject();
    	boolean blockedBySome = false;

    	String usernameSender = body.getString("sender");
    	String msg = body.getString("msg");
    	HashMap<String, User> allClients = ServerLauncher.INSTANCE.getAllClients();
    	for (User receiver: allClients.values()) {
    		// Skip the sender
    		if (receiver.getUsername().equals(usernameSender)) continue;
    		
    		// Sender is blocked by this user
    		if (receiver.blocked(usernameSender)) {
    			blockedBySome = true;
    			continue;
    		}
    		
    		// If this user is online, send message
    		if (receiver.isOnline()) {
    			forwardMessage(receiver.getAddress(), receiver.getPort(), usernameSender, msg);
    		}
    	}
    	
    	// Response to sender
    	if (blockedBySome) {
    		response.put("result", "success");
        	response.put("response", "Your message could not be delivered to some recipients.");
		}
    	else {
    		response.put("result", "success");
        	response.put("response", "Message broadcasted.");
    	}
    	
    	return response;
	}
    
    /* Forward message to client */
    private JSONObject forwardMessage(String address, int port, String username, String msg) {
    	JSONObject response = null;
    	
    	Socket clientSocket = null;
    	try {
			// Open connection
			clientSocket = new Socket(address, port);
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "message");
			JSONObject reqBody = new JSONObject();
			reqBody.put("sender", username);
			reqBody.put("content", msg);
			request.put("body", reqBody);
			
			// Talk to client
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			outToClient.writeBytes(request.toString() + '\n');
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromClient.readLine());
//			printClientResponse(response);
			
			// Close connection
			clientSocket.close();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
//			e.printStackTrace();
			System.out.println("ERROR: Cannot forward message to user because user is unreachable.");
			response = new JSONObject();
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

	/* Send notification to client */
    private JSONObject notify(String address, int port, String msg) {
    	JSONObject response = null;
    	
    	Socket clientSocket = null;
    	try {
			// Open connection
			clientSocket = new Socket(address, port);
			// Build request JSONObject
			JSONObject request = new JSONObject();
			request.put("request", "notify");
			JSONObject body = new JSONObject();
			body.put("content", msg);
			request.put("body", body);
			
			// Talk to client
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			outToClient.writeBytes(request.toString() + '\n');
			
			// Get response back from client
			if (!msg.contains("logged out")) {
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				response = new JSONObject(inFromClient.readLine());
//				printClientResponse(response);
			}
			
			// Close connection
			clientSocket.close();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
//			e.printStackTrace();
			System.out.println("ERROR: Cannot notify user because user is unreachable.");
			response = new JSONObject();
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
    
    /* Block a user */
    private JSONObject block(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String username = body.getString("username");
    	String usernameToBlock = body.getString("usernameToBlock");
    	User user = (User) ServerLauncher.INSTANCE.getAllClients().get(username);
    	if (!ServerLauncher.INSTANCE.getAllClients().containsKey(usernameToBlock)) {
    		// User to be blocked does not exist
    		response.put("result", "failure");
        	response.put("errMsg", "No such user. Please try again.");
    	} else {
    		// Block specified user for requesting user
    		user.blockUser(usernameToBlock);
    		
    		response.put("result", "success");
        	response.put("response", "User " + usernameToBlock + " has been blocked.");
    	}
    	
    	return response;
	}
    
    /* Unblock a user */
    private JSONObject unblock(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String username = body.getString("username");
    	String usernameToUnblock = body.getString("usernameToUnblock");
    	User user = (User) ServerLauncher.INSTANCE.getAllClients().get(username);
    	if (!ServerLauncher.INSTANCE.getAllClients().containsKey(usernameToUnblock)) {
    		// User to be unblocked does not exist
    		response.put("result", "failure");
        	response.put("errMsg", "No such user. Please try again.");
    	} else {
    		// Unblock specified user for requesting user
    		user.unblockUser(usernameToUnblock);
    		
    		response.put("result", "success");
        	response.put("response", "User " + usernameToUnblock + " is unblocked.");
    	}
    	
    	return response;
	}
    
    /* List all online users */
    private JSONObject online(JSONObject body) {
    	JSONObject response = new JSONObject();

    	JSONArray onlineList = new JSONArray();
    	HashMap<String, User> allClients = ServerLauncher.INSTANCE.getAllClients();
    	for (User u: allClients.values()) {
    		// If this user is online, add to response
    		if (u.isOnline()) onlineList.put(u.getUsername());
    	}

    	response.put("result", "success");
    	response.put("response", onlineList);
    	
    	return response;
    }
    
    /* Get address and port of specified user */
    private JSONObject getAddress(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String username = body.getString("username");
    	String usernameToGetAddress = body.getString("usernameToGetAddress");
    	User userToGetAddress = (User) ServerLauncher.INSTANCE.getAllClients().get(usernameToGetAddress);
    	if (userToGetAddress == null) {
    		// User requested does not exist
    		response.put("result", "failure");
        	response.put("errMsg", "No such user. Please try again.");
    	} else if (userToGetAddress.blocked(username)) {
    		// User requested blocked requesting user
    		response.put("result", "failure");
        	response.put("response", "You cannot get address of this user because the user has blocked you.");
    	} /* TODO else if (userToGetAddress.getAddress().equals("")) { 
    		// TODO Requested user hasn't logged in once yet so no valid address
    	}*/ else {
    		// Send address and port of requested user
    		response.put("result", "success");
    		JSONObject info = new JSONObject();
    		info.put("address", userToGetAddress.getAddress());
    		info.put("port", userToGetAddress.getPort());
        	response.put("response", info);
    	}
    	
    	return response;
	}
    
    /* TEST Echo capitalized version of client input */
    private JSONObject capitalize(JSONObject body) {
    	JSONObject response = new JSONObject();
    	response.put("result", "success");
    	response.put("response", body.getString("msg").toUpperCase());
    	
    	return response;
    }
    
    /* TEST Give a HTTP 200 OK response to client */
    private JSONObject responseOK() {
    	JSONObject response = new JSONObject();
    	response.put("result", "success");
    	long time = System.currentTimeMillis();
    	response.put("response", "HTTP/1.1 200 OK\n\nWorkerRunnable: " + this.serverText + " - " + time);
    	
    	return response;
    }
}
