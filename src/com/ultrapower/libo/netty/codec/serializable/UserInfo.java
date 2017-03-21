package com.ultrapower.libo.netty.codec.serializable;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {

	/**
	 * 默认的序列号
	 */
	private static final long serialVersionUID = 1L;

	private String userName;

	private int userID;

	public UserInfo buildUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public UserInfo buildUserID(int userID) {
		this.userID = userID;
		return this;
	}

	/**
	 * @return the userName
	 */
	public final String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public final void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userID
	 */
	public final int getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public final void setUserID(int userID) {
		this.userID = userID;
	}

	public byte[] codeC() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] value = this.userName.getBytes();
		buffer.putInt(value.length);
		buffer.put(value);
		buffer.putInt(this.userID);
		buffer.flip();
		value = null;
		byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return result;
	}

	public byte[] codeC(ByteBuffer buffer) {
		buffer.clear();
		byte[] value = this.userName.getBytes();
		buffer.putInt(value.length);
		buffer.put(value);
		buffer.putInt(this.userID);
		buffer.flip();
		value = null;
		byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return result;
	}
	
	public UserInfo dcodeC(byte[] bytes) {
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put(bytes);
		buffer.flip();
		UserInfo info = new UserInfo();
		
		int length = buffer.getInt();
		byte[] username = new byte[length];
		buffer.get(username);
		
		int userId = buffer.getInt();
		
		info.setUserID(userId);
		info.setUserName(new String(username));;
		
		return info;
	}
	
	public static void main(String[] args) {
		UserInfo info = new UserInfo();
		info.setUserID(1);
		info.setUserName("test");
		byte[] infobyte = info.codeC();
		UserInfo temp = info.dcodeC(infobyte);
		System.out.println(temp.getUserName());
		System.out.println(temp.getUserID());
	}
}
