package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Client-side worker thread
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
//    		System.out.println("### Client Request Received " + System.currentTimeMillis() + " ###"); // DEBUG Request received stamp
			BufferedReader inFromClient = new BufferedReader (new InputStreamReader(connectionSocket.getInputStream()));
			String clientMsg = inFromClient.readLine();
			
			/* Execute request */
			// Parse request
			JSONObject requestJson = new JSONObject(clientMsg);
			String request = requestJson.getString("request");
			JSONObject body = requestJson.getJSONObject("body");
//			System.out.println("request = " + request); // DEBUG Request type
			
			// Execute corresponding method
			JSONObject serverResponse;
			if (request.equals("message")) serverResponse = displayMessage(body);
			else if (request.equals("notify")) serverResponse = displayNotification(body);
			// TODO handle P2P connection request
			else serverResponse = responseOK();
        	
        	/* Respond to client */
//        	System.out.println("---response---\n" + serverResponse + "\n------end-----"); // DEBUG Response content
        	DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes(serverResponse.toString() + '\n');
        	
        	/* Close connection */
//			System.out.println("closing connection"); // DEBUG Responded
        	inFromClient.close();
        	outToClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
//    	System.out.println("######### Done #########"); // DEBUG Request processed
    }
    
    /* Display received message */
    private JSONObject displayMessage(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String sender = body.getString("sender");
    	String content = body.getString("content");
    	ClientLauncher.println(sender + ": " + content);
    	
    	response.put("result", "success");
    	response.put("response", "Message received.");
    	
    	return response;
    }
    
    /* Display notification */
    private JSONObject displayNotification(JSONObject body) {
    	JSONObject response = new JSONObject();

    	String content = body.getString("content");
    	ClientLauncher.println(content);
    	
    	if (content.contains("logged out")) ClientLauncher.terminateClient();
    	
    	response.put("result", "success");
    	response.put("response", "Notification received.");
    	
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
