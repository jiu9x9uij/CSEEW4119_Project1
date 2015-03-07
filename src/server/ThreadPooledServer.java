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
                        new RequestWorkerRunnable(clientSocket, "Thread Pooled Server"));
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
}