package com.ultrapower.libo.io.nio2.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
	private int port = 8000;
	private ServerSocketChannel serverSocketChannel = null;
	private ExecutorService executorService;
	private static final int POOL_MULTIPLE = 4;

	public EchoServer() throws IOException {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		System.out.println("服务器启动");
	}

	public void service() {
		while (true) {
			SocketChannel socketChannel = null;
			try {
				socketChannel = serverSocketChannel.accept();
				executorService.execute(new Handler(socketChannel));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer().service();
	}
}

class Handler implements Runnable {
	private SocketChannel socketChannel;

	public Handler(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public void run() {
		handle(socketChannel);
	}

	public void handle(SocketChannel socketChannel) {
		try {
			Socket socket = socketChannel.socket();
			 System.out.println("接收到客户连接，来自: " + socket.getInetAddress() + ":" + socket.getPort());

			BufferedReader br = getReader(socket);
			PrintWriter pw = getWriter(socket);

			String msg = null;
			while ((msg = br.readLine()) != null) {
				System.out.println(msg);
				pw.println(echo(msg));
				if (msg.equals("bye"))
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socketChannel != null)
					socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private PrintWriter getWriter(Socket socket) throws IOException {
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
	}

	private BufferedReader getReader(Socket socket) throws IOException {
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
	}

	public String echo(String msg) {
		return "echo:" + msg;
	}
}
