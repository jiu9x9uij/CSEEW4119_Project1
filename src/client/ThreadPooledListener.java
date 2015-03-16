package client;

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

public class ThreadPooledListener implements Runnable{
	protected String address = null;
	protected int port = 0;
	protected ServerSocket welcomeSocket = null;
	protected boolean isStopped = false;
	protected Thread runningThread= null;
	protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public void run(){
        synchronized(this){
            runningThread = Thread.currentThread();
        }
        
        openWelcomeSocket();
        
        while(!isStopped()){
            Socket clientSocket = null;
            
            try {
                clientSocket = welcomeSocket.accept();
                threadPool.execute(new RequestWorkerRunnable(clientSocket, "Thread Pooled Client"));
            } catch (IOException e) {
                if(isStopped()) {
//                    System.out.println("IOException Server Stopped.");///
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
        }
        
        threadPool.shutdown();
//        System.out.println("Server Stopped.");///
    }


    private synchronized boolean isStopped() {
        return isStopped;
    }

    public synchronized void stop(){
        isStopped = true;
        try {
            welcomeSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openWelcomeSocket() {
        try {
            welcomeSocket = new ServerSocket(0);
            port = welcomeSocket.getLocalPort();
//            System.out.println("port = " + port);///
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + 0, e);
        }
    }
    
    public int getPort() {
    	return port;
    }
}