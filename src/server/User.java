package server;

import java.util.ArrayList;
import java.util.Queue;

public class User {
	String username, password;
	boolean isOnline;
	Queue<String> offlineMsgs;
	ArrayList<String> blackList;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void logIn() {
		isOnline = true;
	}
	
	public void logOut() {
		isOnline = false;
	}
	
	public void addMsg(String msg) {
		offlineMsgs.add(msg);
	}
	
	public void block(String username) {
		blackList.add(username);
	}
}
