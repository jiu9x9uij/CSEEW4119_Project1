package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

public class ThreadPooledServer implements Runnable{
	protected int port = 8080;
    protected ServerSocket welcomeSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread= null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public ThreadPooledServer(int port){
        this.port = port;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        
        openWelcomeSocket();
        
        while(!isStopped()){
            Socket clientSocket = null;
            
            try {
                clientSocket = this.welcomeSocket.accept();
                this.threadPool.execute(
                        new WorkerRunnable(clientSocket, "Thread Pooled Server"));
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("IOException Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
        }
        
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.welcomeSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openWelcomeSocket() {
        try {
            this.welcomeSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }
    
    
    
    class WorkerRunnable implements Runnable{

        protected Socket connectionSocket = null;
        protected String serverText   = null;

        public WorkerRunnable(Socket connectionSocket, String serverText) {
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
	            String serverResponse;
	            if (request.equals("capitalize")) serverResponse = capitalize(body);
	            else serverResponse = responseOK();
	        	
	        	/* Respond to client */
	        	System.out.println("---response---\n" + serverResponse + "------end-----"); // DEBUG Response content
	        	DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes(serverResponse);
	        	
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
        
        /* TEST Echo capitalized version of client input */
        private String capitalize(JSONObject body) {
        	String msg = body.getString("msg");
        	return msg.toUpperCase() + '\n';
        }
        
        /* TEST Give a HTTP 200 OK response to client */
        private String responseOK() {
        	long time = System.currentTimeMillis();
            return "HTTP/1.1 200 OK\n\nWorkerRunnable: " + this.serverText + " - " + time + "\n";
        }
    }
}