package server;

import java.util.ArrayList;
import java.util.Queue;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class User {
	String username, password;
	int numOfLoginAttemps = 0;
	long timestampOfBlocking = -1;
	String address = null;
	int port = -1;
	Queue<String> offlineMsgs;
	ArrayList<String> blackList;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public int getLoginAttemps() {
		return numOfLoginAttemps;
	}
	public void increaseLoginAttemps() {
		numOfLoginAttemps++;
	}
	
	public void resetLoginAttemps() {
		numOfLoginAttemps = 0;
	}
	
	public boolean isBlockedLogin() {
		if (timestampOfBlocking == -1) {
			return false;
		}
		else if (NANOSECONDS.toSeconds(System.nanoTime() - timestampOfBlocking) >= ServerSettings.MAX_BLOCK_TIME) {
			unblockLogin();
			return false;
		} else {
			return true;
		}
	}
	
	public void blockLogin() {
		resetLoginAttemps();
		timestampOfBlocking = System.nanoTime();
	}
	
	public void unblockLogin() {
		timestampOfBlocking = -1;
	}
	
	public void login(String address, int port) {
		resetLoginAttemps();
		this.address = address;
		this.port = port;
		ServerLauncher.INSTANCE.addOnlineClient(this); // Because clients are kept in HashMap, old login info will be replaced
		ServerLauncher.INSTANCE.removeOfflineClient(this);
	}
	
	public boolean isOnline() {
		if (ServerLauncher.INSTANCE.getOnlineClients().containsKey(username)) return true;
		else return false;
	}

	public void logoff() {
		this.address = null;
		this.port = -1;
		ServerLauncher.INSTANCE.addOfflineClient(this);
		ServerLauncher.INSTANCE.removeOnlineClient(this);
	}
	
	public void addMsg(String msg) {
		offlineMsgs.add(msg);
	}
	
	public void blockUser(String username) {
		blackList.add(username);
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
}
