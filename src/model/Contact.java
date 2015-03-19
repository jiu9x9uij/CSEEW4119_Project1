package model;

public class Contact {
	private String mUsername;
	private String mAddress;
	private int mPort;
	
	public Contact(String username, String address, int port) {
		mUsername = username;
		mAddress = address;
		mPort = port;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public int getPort() {
		return mPort;
	}
}
