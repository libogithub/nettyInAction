package com.ultrapower.libo.netty.newlinerDecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * LineBasedFrameDecoder:工作原理是依次遍历ByteBuf中的可读字节,判断是否有\n或者\r\n,如果有
 *                       就从当前位置为结束位置,从可读的索引位置到结束位置形成一行,它是以换行符
 *                       为标识的解码器，支持携带结束符或者不带结束符两种方式，同时支持最大长度
 *                       如果超过最大长度仍然没有读取到\r\n，那么就抛出异常并且忽略之前读取到的
 *                       异常码流。
 *                       
 * StringDecoder: 将接收到的对象转换成为字符串。                      
 */
public class TimeServer {

    public void bind(int port) throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
		    ServerBootstrap b = new ServerBootstrap();
		    b.group(bossGroup, workerGroup)
			    .channel(NioServerSocketChannel.class)
			    .option(ChannelOption.SO_BACKLOG, 1024)
			    .childHandler(new ChildChannelHandler());
		    // 绑定端口，同步等待成功
		    ChannelFuture f = b.bind(port).sync();
	
		    // 等待服务端监听端口关闭
		    f.channel().closeFuture().sync();
		} finally {
		    // 优雅退出，释放线程池资源
		    bossGroup.shutdownGracefully();
		    workerGroup.shutdownGracefully();
		}
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
			arg0.pipeline().addLast(new StringDecoder());
		    arg0.pipeline().addLast(new TimeServerHandler());
		}
    }

    public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
		    try {
		    	port = Integer.valueOf(args[0]);
		    } catch (NumberFormatException e) {
			// 采用默认值
		    }
		}
		new TimeServer().bind(port);
    }
}
