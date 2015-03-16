package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

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
            else if (request.equals("logout")) serverResponse = logout(body);
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
    		if (user.isOnline()) {
    			notify(user.getAddress(), user.getPort(), "You have been logged out because your account is logged in somewhere else.");
    		}
    		address = address.substring(1);
    		address = address.split(":")[0];
    		user.login(address, port);
    		response.put("result", "success");
        	response.put("response", "Welcome to simple chat server!");
    	}
    	
    	return response;
    }
    
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
    	return response;
    }
    
    /* TODO Notify previous address about duplicated login */
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
