package com.ultrapower.libo.netty.codec.serializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestUserInfo {

	public static void main(String[] args) throws IOException {
		UserInfo info = new UserInfo();
		info.buildUserID(100).buildUserName("Welcome to Netty");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		os.writeObject(info);
		os.flush();
		os.close();
		byte[] b = bos.toByteArray();
		System.out.println("使用jdk自带的序列化产生的数据长度 : " + b.length);
		bos.close();
		System.out.println("-------------------------------------");
		System.out.println("使用byteBuffer编码后产生的长度 : " + info.codeC().length);

	}

}
