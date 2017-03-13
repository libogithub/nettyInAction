package com.ultrapower.libo.netty.newlinerDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;
									
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());

	private int counter;

	private byte[] req;

	/**
	 * Creates a client-side handler.
	 */
	public TimeClientHandler() {
		req = ("查询时间"+System.getProperty("line.separator")).getBytes();
	}

	public void channelActive(ChannelHandlerContext ctx) {
		ByteBuf message = null;
		for (int i = 0; i < 100; i++) {
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		ByteBuf buf = (ByteBuf) msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req, "UTF-8");
		
		String body = (String)msg ;
		System.out.println("服务器时间 : " + body + " ; 第 " + ++counter +"次从服务器获取时间");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// 释放资源
		logger.warning("Unexpected exception from downstream : " + cause.getMessage());
		ctx.close();
	}
}
