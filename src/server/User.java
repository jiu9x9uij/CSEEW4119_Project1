package server;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
	String username, password;
	int numOfLoginAttemps = 0;
	long timestampOfBlocking = -1;
	String address = null;
	int port = -1;
	ConcurrentLinkedQueue<Message> offlineMsgs = new ConcurrentLinkedQueue<Message>();
	HashSet<String> blackList = new HashSet<String>();
	
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

	public void logout() {
		this.address = null;
		this.port = -1;
		ServerLauncher.INSTANCE.addOfflineClient(this);
		ServerLauncher.INSTANCE.removeOnlineClient(this);
	}
	
	public ConcurrentLinkedQueue<Message> getOfflineMsgs() {
		return offlineMsgs;
	}
	
	public void addOfflineMsg(String sender, String msg) {
		offlineMsgs.add(new Message(sender, msg));
	}
	
	public void blockUser(String username) {
		blackList.add(username);
	}
	
	public boolean blocked(String username) {
		if (blackList.contains(username)) return true;
		else return false;
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
