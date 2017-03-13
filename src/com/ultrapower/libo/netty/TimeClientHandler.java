package com.ultrapower.libo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());

	private final ByteBuf firstMessage;

	/**
	 * Creates a client-side handler.
	 */
	public TimeClientHandler() {
		byte[] req = "查询时间".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("服务器时间 : " + body);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// 释放资源
		logger.warning("异常 : " + cause.getMessage());
		ctx.close();
	}
}
