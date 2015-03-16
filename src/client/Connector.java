package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

public enum Connector {
	INSTANCE;
	
	private String host;
	private int port;
	private Socket clientSocket;

	public static Connector getInstance() {
		return INSTANCE;
	}
	
	public void init(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	private void printServerResponse(JSONObject response) {
//		System.out.println("---Server Response---");
		if (response.getString("result").equals("success")) System.out.println(response.getString("response"));
		else System.out.println("ERROR: " + response.getString("errMsg"));
//		System.out.println("---------End---------");
	}
	
	/** Log in
	 * @param username
	 * @param password
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the capitalized msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public JSONObject login(String username, String password, int clientPort) {
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
			// TODO use real address, port
			body.put("address", "");
			body.put("port", clientPort);
			request.put("body", body);
			
			// Talk to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(request.toString() + '\n');
			
			// Get response back from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = new JSONObject(inFromServer.readLine());
			printServerResponse(response);
			
			// Close connection
			clientSocket.close();	
		} catch (UnknownHostException e) {
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
	
	/** Log out
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the capitalized msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public JSONObject logout(String username) {
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
			printServerResponse(response);
			
			// Close connection
			clientSocket.close();
		} catch (UnknownHostException e) {
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
	
	/** TEST Simple communication with server through socket
	 * @param msg string to be capitlized
	 * @return response JSONObject with key <tt>result</tt> ("success" / "failure"),
	 *  <tt>response</tt> that contains the capitalized msg (only exists if code is "success"),
	 *   and <tt>errMsg</tt> (only exists if code is "failure")
	 */
	public JSONObject capitalize(String msg) {
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
			printServerResponse(response);
			
			// Close connection
			clientSocket.close();	
		} catch (UnknownHostException e) {
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

		return response;
	}	
}
