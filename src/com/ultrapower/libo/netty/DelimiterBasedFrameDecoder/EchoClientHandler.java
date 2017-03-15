package com.ultrapower.libo.netty.DelimiterBasedFrameDecoder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	private int counter;

	static final String ECHO_REQ = "Welcome to Netty.$_";

	/**
	 * Creates a client-side handler.
	 */
	public EchoClientHandler() {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		// ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer(ECHO_REQ
		// .getBytes().length);
		// buf.writeBytes(ECHO_REQ.getBytes());
		for (int i = 0; i < 10; i++) {
			ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("第" + ++counter + "次收到服务器端返回的消息: [" + msg + "]");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}