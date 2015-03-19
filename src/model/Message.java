package model;

public class Message {
	private String mSender;
	private String mContent;
	
	public Message(String sender, String content) {
		mSender = sender;
		mContent = content;
	}
	
	public String getSender() {
		return mSender;
	}
	
	public String getContent() {
		return mContent;
	}
}
