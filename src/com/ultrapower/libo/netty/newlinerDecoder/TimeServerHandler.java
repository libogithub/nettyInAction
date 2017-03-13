package com.ultrapower.libo.netty.newlinerDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
	    throws Exception {
//		ByteBuf buf = (ByteBuf) msg;
//    	String buf = (String)msg ;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req, "UTF-8");
    	
    	//使用了StringDecoder后直接返回的就是String对象
    	String body = (String)msg ;
		System.out.println("服务器收到指令 :" + body + " ; 第 " + ++counter + "次指令");
		
		String currentTime = "查询时间".equalsIgnoreCase(body) ? new java.util.Date(
			System.currentTimeMillis()).toString() : "错误的指令";
			
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	new Exception(cause).printStackTrace();
    	ctx.close();
    }
}
