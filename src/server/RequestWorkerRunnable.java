package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

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
            else if (request.equals("capitalize")) serverResponse = capitalize(body);
            else serverResponse = responseOK();
        	
        	/* Respond to client */
        	System.out.println("---response---\n" + serverResponse + "------end-----"); // DEBUG Response content
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
    	
    	System.out.println("######### Done #########"); // DEBUG Request processed
    }
    
    /* Validate login credential */
    private JSONObject login(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String username = body.getString("username");
    	String password = body.getString("password");
    	String address = body.getString("address");
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
    		if (user.isOnline()) {
    			// TODO Notify previous address about duplicated login
    			user.getAddress();
    			user.getPort();
    		}
    		user.login(address, port);
    		response.put("result", "success");
        	response.put("response", "Welcome to simple chat server!");
    	}
    	
    	// Update statuses of this user
    	if (user != null) ServerLauncher.INSTANCE.getAllClients().put(user.getUsername(), user);
    	
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
