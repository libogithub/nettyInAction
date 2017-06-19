package com.ultrapower.libo.io.nio2.nonblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private int port = 8000;
	private Charset charset = Charset.forName("GBK");

	public EchoServer() throws IOException {
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		System.out.println("服务器启动");
	}

	public void service() throws IOException {
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		int n ;
		while ((n = selector.select()) > 0) {
			System.out.println("--------------"+n);
			Set readyKeys = selector.selectedKeys();
			Iterator it = readyKeys.iterator();
			while (it.hasNext()) {
				SelectionKey key = null;
				try {
					key = (SelectionKey) it.next();
					it.remove();

					if (key.isAcceptable()) {
						ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
						SocketChannel socketChannel = (SocketChannel) ssc.accept();
						System.out.println("接收到客户连接，来自: " + socketChannel.socket().getInetAddress() + ":"
								+ socketChannel.socket().getPort());
						socketChannel.configureBlocking(false);
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, buffer);
					}
					if (key.isReadable()) {
						receive(key);
					}
					if (key.isWritable()) {
						send(key);
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						if (key != null) {
							key.cancel();
							key.channel().close();
						}
					} catch (Exception ex) {
						e.printStackTrace();
					}
				}
			} // #while
		} // #while
	}

	public void send(SelectionKey key) throws IOException {
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		SocketChannel socketChannel = (SocketChannel) key.channel();
		buffer.flip(); //把极限设为位置，把位置设为0
		String data = decode(buffer);
		if (data.indexOf("\r\n") == -1)
			return;
		String outputData = data.substring(0, data.indexOf("\n") + 1);
		System.out.print(outputData);
		ByteBuffer outputBuffer = encode("echo:" + outputData);
		/*
		 * 非阻塞模式下scoketChannel不能保证一次把outputBuffer中的数据一次性的发送完成，而是采取能发送多少就发送多少。
		 */
		while (outputBuffer.hasRemaining())
			socketChannel.write(outputBuffer);

		//发送完成后需要把key.attachment()中的ByteBuffer已经发送的数据进行删除
		ByteBuffer temp = encode(outputData);
		buffer.position(temp.limit());
		buffer.compact();

		if (outputData.equals("bye\r\n")) {
			key.cancel();
			socketChannel.close();
			System.out.println("关闭与客户的连接");
		}
	}

	public void receive(SelectionKey key) throws IOException {
		ByteBuffer buffer = (ByteBuffer) key.attachment();

		SocketChannel socketChannel = (SocketChannel) key.channel();
		ByteBuffer readBuff = ByteBuffer.allocate(32);
		socketChannel.read(readBuff);
		readBuff.flip();

		buffer.limit(buffer.capacity());
		buffer.put(readBuff);
	}

	public String decode(ByteBuffer buffer) { //解码
		CharBuffer charBuffer = charset.decode(buffer);
		return charBuffer.toString();
	}

	public ByteBuffer encode(String str) { //编码
		return charset.encode(str);
	}

	public static void main(String args[]) throws Exception {
		EchoServer server = new EchoServer();
		server.service();
	}
}
